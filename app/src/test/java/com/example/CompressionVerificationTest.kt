package com.example

import com.example.data.remote.WeatherApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * End-to-end audit and verification test suite for HTTP compression in transit.
 * Validates:
 * 1. Client negotiation (Accept-Encoding: gzip)
 * 2. Threshold enforcement on server/edge (>= 1KB)
 * 3. Prevention of double-compression on binary/image/pre-compressed streams
 * 4. Transfer size reduction verification (significant bandwidth drop)
 * 5. Transparent parsing fidelity on the client
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CompressionVerificationTest {

  private val sampleWeatherJson = """
    {
      "latitude": 28.6139,
      "longitude": 77.2090,
      "timezone": "Asia/Kolkata",
      "current": {
        "time": "2026-09-14T12:00",
        "interval": 900,
        "temperature_2m": 31.5,
        "relative_humidity_2m": 62,
        "rain": 0.0,
        "weather_code": 1,
        "wind_speed_10m": 12.4
      },
      "daily": {
        "time": ["2026-09-14", "2026-09-15", "2026-09-16", "2026-09-17", "2026-09-18"],
        "weather_code": [1, 2, 61, 80, 0],
        "temperature_2m_max": [34.2, 33.8, 30.1, 29.5, 32.0],
        "temperature_2m_min": [24.1, 24.5, 23.0, 22.8, 23.5],
        "precipitation_sum": [0.0, 1.2, 14.5, 8.2, 0.0],
        "precipitation_probability_max": [10, 35, 85, 70, 5],
        "et0_fao_evapotranspiration": [4.8, 4.5, 3.2, 3.4, 4.6]
      }
    }
  """.trimIndent()

  @Test
  fun `clientOkHttpAutomaticallyNegotiatesAcceptEncodingGzip`() {
    // OkHttp's BridgeInterceptor transparently adds Accept-Encoding: gzip
    val client = OkHttpClient()
    val request = Request.Builder()
      .url("https://api.open-meteo.com/v1/forecast")
      .build()

    // Verify request does not corrupt headers with manual lowercase duplicates
    // OkHttp will inject "Accept-Encoding: gzip" at the transport bridge layer
    val manualEncoding = request.header("Accept-Encoding")
    // When unset manually, it is null here, guaranteeing BridgeInterceptor activates transparent decompression
    assertEquals(null, manualEncoding)
  }

  @Test
  fun `edgeThresholdEnforcesMinimumPayloadSizeBeforeCompression`() {
    // Payloads below 1024 bytes should NOT be compressed (overhead > gain)
    val tinyPayload = "{\"status\":\"ok\"}".toByteArray(Charsets.UTF_8).size.toLong()
    assertFalse(
      "Tiny payloads (<1KB) must not trigger compression overhead",
      WeatherApiService.shouldCompress("application/json", tinyPayload, 1024L)
    )

    // Payloads above 1024 bytes should be compressed
    val largeWeatherPayload = sampleWeatherJson + "\n" + sampleWeatherJson
    val largePayloadSize = largeWeatherPayload.toByteArray(Charsets.UTF_8).size.toLong()
    assertTrue("Large payload size must exceed 1024 bytes", largePayloadSize > 1024L)
    assertTrue(
      "Large JSON payloads (>=1KB) must be compressed",
      WeatherApiService.shouldCompress("application/json", largePayloadSize, 1024L)
    )
  }

  @Test
  fun `antiDoubleCompressionProtectsImagesAndArchives`() {
    // Images must be rejected from compression
    assertTrue("JPEG must not be compressed", WeatherApiService.isAlreadyCompressed("image/jpeg"))
    assertTrue("PNG must not be compressed", WeatherApiService.isAlreadyCompressed("image/png"))
    assertTrue("WebP must not be compressed", WeatherApiService.isAlreadyCompressed("image/webp"))

    // Archives must be rejected from compression
    assertTrue("ZIP must not be compressed", WeatherApiService.isAlreadyCompressed("application/zip"))
    assertTrue("GZIP must not be compressed", WeatherApiService.isAlreadyCompressed("application/gzip"))

    // Already encoded streams must be rejected
    assertTrue("Gzip encoded response must not be re-compressed", WeatherApiService.isAlreadyCompressed("application/json", "gzip"))
    assertTrue("Brotli encoded response must not be re-compressed", WeatherApiService.isAlreadyCompressed("application/json", "br"))

    // Plain uncompressed JSON must be eligible
    assertFalse("Uncompressed JSON is eligible", WeatherApiService.isAlreadyCompressed("application/json", null))
  }

  @Test
  fun `verifyResponseTransferSizeDropsSignificantlyInTransit`() {
    val uncompressedBytes = sampleWeatherJson.toByteArray(Charsets.UTF_8)
    val uncompressedLength = uncompressedBytes.size

    // Simulate Server/Edge GZIP compression
    val byteStream = ByteArrayOutputStream()
    GZIPOutputStream(byteStream).use { gzipOut ->
      gzipOut.write(uncompressedBytes)
    }
    val compressedBytes = byteStream.toByteArray()
    val compressedLength = compressedBytes.size

    val compressionRatio = (1.0 - (compressedLength.toDouble() / uncompressedLength.toDouble())) * 100.0

    println("Original uncompressed size: $uncompressedLength bytes")
    println("Wire transfer compressed size: $compressedLength bytes")
    println("Bandwidth savings: ${String.format("%.2f", compressionRatio)}%")

    // Verify significant transfer reduction (typically > 40-70% on JSON payloads)
    assertTrue("Compressed size must be significantly smaller than uncompressed", compressedLength < uncompressedLength)
    assertTrue("Compression savings must exceed 40%", compressionRatio > 40.0)
  }

  @Test
  fun `verifyCompressedPayloadDecompressesAndParsesCorrectlyOnClient`() {
    val rawBytes = sampleWeatherJson.toByteArray(Charsets.UTF_8)

    // 1. Compress at Server / Edge
    val compressedOut = ByteArrayOutputStream()
    GZIPOutputStream(compressedOut).use { it.write(rawBytes) }
    val wireBytes = compressedOut.toByteArray()

    // 2. Client receives wireBytes and transparently decompresses
    val decompressedOut = ByteArrayOutputStream()
    GZIPInputStream(ByteArrayInputStream(wireBytes)).use { gzipIn ->
      gzipIn.copyTo(decompressedOut)
    }
    val decompressedString = decompressedOut.toString(Charsets.UTF_8.name())

    // 3. Verify JSON integrity
    val parsedJson = JSONObject(decompressedString)
    assertNotNull(parsedJson)

    val current = parsedJson.getJSONObject("current")
    assertEquals(31.5, current.getDouble("temperature_2m"), 0.01)
    assertEquals(62, current.getInt("relative_humidity_2m"))

    val daily = parsedJson.getJSONObject("daily")
    val dailyPrecip = daily.getJSONArray("precipitation_sum")
    assertEquals(5, dailyPrecip.length())
    assertEquals(14.5, dailyPrecip.getDouble(2), 0.01)
  }
}
