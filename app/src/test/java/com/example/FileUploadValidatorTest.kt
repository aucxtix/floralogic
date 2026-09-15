package com.example

import com.example.util.FileUploadValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.ByteArrayInputStream

class FileUploadValidatorTest {

  @Test
  fun `detectMagicBytes correctly identifies JPEG binary header`() {
    val jpegHeader = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 0x00, 0x10)
    val result = FileUploadValidator.detectMagicBytes(ByteArrayInputStream(jpegHeader))
    assertEquals(FileUploadValidator.DetectedMimeType.JPEG, result)
  }

  @Test
  fun `detectMagicBytes correctly identifies PNG binary header`() {
    val pngHeader = byteArrayOf(
      0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
      0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte(),
      0x00, 0x00, 0x00, 0x0D
    )
    val result = FileUploadValidator.detectMagicBytes(ByteArrayInputStream(pngHeader))
    assertEquals(FileUploadValidator.DetectedMimeType.PNG, result)
  }

  @Test
  fun `detectMagicBytes correctly identifies WebP binary header`() {
    val webpHeader = "RIFF1234WEBP".toByteArray(Charsets.US_ASCII)
    val result = FileUploadValidator.detectMagicBytes(ByteArrayInputStream(webpHeader))
    assertEquals(FileUploadValidator.DetectedMimeType.WEBP, result)
  }

  @Test
  fun `detectMagicBytes rejects HTML or SVG disguised as image`() {
    val svgPayload = "<svg onload=alert(1)>".toByteArray(Charsets.UTF_8)
    val result = FileUploadValidator.detectMagicBytes(ByteArrayInputStream(svgPayload))
    assertNull("SVG payload must be rejected as an invalid binary image header", result)

    val htmlPayload = "<html><body>evil</body></html>".toByteArray(Charsets.UTF_8)
    val resultHtml = FileUploadValidator.detectMagicBytes(ByteArrayInputStream(htmlPayload))
    assertNull("HTML payload must be rejected", resultHtml)
  }

  @Test
  fun `detectMagicBytes rejects executable or random text`() {
    val scriptPayload = "#!/bin/bash\nrm -rf /".toByteArray(Charsets.UTF_8)
    val result = FileUploadValidator.detectMagicBytes(ByteArrayInputStream(scriptPayload))
    assertNull(result)

    val shortData = byteArrayOf(0x01, 0x02)
    val resultShort = FileUploadValidator.detectMagicBytes(ByteArrayInputStream(shortData))
    assertNull(resultShort)
  }
}
