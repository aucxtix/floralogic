package com.example.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Geographic coordinates representation with metadata for weather and advisory integration.
 */
data class Coordinates(
  val latitude: Double,
  val longitude: Double,
  val accuracyMeters: Float? = null,
  val altitudeMeters: Double? = null,
  val timestampMs: Long = System.currentTimeMillis()
) {
  /**
   * Formatted string e.g. "21.1702° N, 72.8311° E"
   */
  fun toFormattedString(): String {
    val latDirection = if (latitude >= 0) "N" else "S"
    val lonDirection = if (longitude >= 0) "E" else "W"
    val latAbs = Math.abs(latitude)
    val lonAbs = Math.abs(longitude)
    return "%.4f° %s, %.4f° %s".format(Locale.US, latAbs, latDirection, lonAbs, lonDirection)
  }
}

/**
 * Result state for location queries
 */
sealed interface LocationResult {
  data class Success(
    val coordinates: Coordinates,
    val locality: String? = null,
    val state: String? = null
  ) : LocationResult

  data class Error(val message: String, val cause: Throwable? = null) : LocationResult
  object PermissionDenied : LocationResult
  object LocationDisabled : LocationResult
}

/**
 * Service using FusedLocationProviderClient to fetch the farmer's current coordinates
 * for accurate hyperlocal weather forecasting, spray windows, and crop advisory.
 */
class LocationService(
  private val context: Context,
  private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) {

  /**
   * Checks whether ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION has been granted.
   */
  fun hasLocationPermission(): Boolean {
    val fine = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
  }

  /**
   * Checks if GPS or Network location providers are enabled on the device.
   */
  fun isLocationEnabled(): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
      ?: return false
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
      locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
  }

  /**
   * Fetches current coordinates using FusedLocationProviderClient with cancellation support.
   */
  @SuppressLint("MissingPermission")
  suspend fun getCurrentCoordinates(
    priority: Int = Priority.PRIORITY_HIGH_ACCURACY
  ): LocationResult {
    if (!hasLocationPermission()) {
      return LocationResult.PermissionDenied
    }

    if (!isLocationEnabled()) {
      return LocationResult.LocationDisabled
    }

    return try {
      val cancellationTokenSource = CancellationTokenSource()
      val location = suspendCancellableCoroutine<Location?> { continuation ->
        continuation.invokeOnCancellation {
          cancellationTokenSource.cancel()
        }

        fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
          .addOnSuccessListener { loc ->
            if (continuation.isActive) continuation.resume(loc)
          }
          .addOnFailureListener { exception ->
            if (continuation.isActive) continuation.resumeWith(Result.failure(exception))
          }
          .addOnCanceledListener {
            if (continuation.isActive) continuation.resume(null)
          }
      }

      if (location != null) {
        val geoInfo = resolveAddress(location.latitude, location.longitude)
        LocationResult.Success(
          coordinates = Coordinates(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracyMeters = if (location.hasAccuracy()) location.accuracy else null,
            altitudeMeters = if (location.hasAltitude()) location.altitude else null,
            timestampMs = location.time
          ),
          locality = geoInfo?.locality,
          state = geoInfo?.adminArea
        )
      } else {
        // Fallback to last known cached location
        getLastKnownCoordinates()
      }
    } catch (e: Exception) {
      LocationResult.Error("Failed to obtain current location: ${e.localizedMessage ?: "Unknown error"}", e)
    }
  }

  /**
   * Fetches the last known cached location from FusedLocationProviderClient.
   */
  @SuppressLint("MissingPermission")
  suspend fun getLastKnownCoordinates(): LocationResult {
    if (!hasLocationPermission()) {
      return LocationResult.PermissionDenied
    }

    return try {
      val location = suspendCancellableCoroutine<Location?> { continuation ->
        fusedLocationClient.lastLocation
          .addOnSuccessListener { loc ->
            if (continuation.isActive) continuation.resume(loc)
          }
          .addOnFailureListener { exception ->
            if (continuation.isActive) continuation.resumeWith(Result.failure(exception))
          }
          .addOnCanceledListener {
            if (continuation.isActive) continuation.resume(null)
          }
      }

      if (location != null) {
        val geoInfo = resolveAddress(location.latitude, location.longitude)
        LocationResult.Success(
          coordinates = Coordinates(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracyMeters = if (location.hasAccuracy()) location.accuracy else null,
            altitudeMeters = if (location.hasAltitude()) location.altitude else null,
            timestampMs = location.time
          ),
          locality = geoInfo?.locality,
          state = geoInfo?.adminArea
        )
      } else {
        LocationResult.Error("Location not available. Please verify GPS is enabled.")
      }
    } catch (e: Exception) {
      LocationResult.Error("Failed to fetch last known location: ${e.localizedMessage ?: "Unknown error"}", e)
    }
  }

  /**
   * Flow that emits location updates periodically for active farm tracking or field boundary walking.
   */
  @SuppressLint("MissingPermission")
  fun getLocationUpdates(
    intervalMs: Long = 15000L,
    minUpdateDistanceMeters: Float = 10f,
    priority: Int = Priority.PRIORITY_BALANCED_POWER_ACCURACY
  ): Flow<LocationResult> = callbackFlow {
    if (!hasLocationPermission()) {
      trySend(LocationResult.PermissionDenied)
      close()
      return@callbackFlow
    }

    val request = LocationRequest.Builder(priority, intervalMs)
      .setMinUpdateDistanceMeters(minUpdateDistanceMeters)
      .build()

    val callback = object : LocationCallback() {
      override fun onLocationResult(gmsResult: com.google.android.gms.location.LocationResult) {
        val lastLocation = gmsResult.lastLocation ?: return
        trySend(
          LocationResult.Success(
            coordinates = Coordinates(
              latitude = lastLocation.latitude,
              longitude = lastLocation.longitude,
              accuracyMeters = if (lastLocation.hasAccuracy()) lastLocation.accuracy else null,
              altitudeMeters = if (lastLocation.hasAltitude()) lastLocation.altitude else null,
              timestampMs = lastLocation.time
            )
          )
        )
      }
    }

    fusedLocationClient.requestLocationUpdates(request, callback, context.mainLooper)
    awaitClose {
      fusedLocationClient.removeLocationUpdates(callback)
    }
  }

  /**
   * Reverse geocoding to resolve locality / village / district and state from coordinates.
   */
  suspend fun resolveAddress(latitude: Double, longitude: Double): Address? = withContext(Dispatchers.IO) {
    try {
      val geocoder = Geocoder(context, Locale.getDefault())
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { continuation ->
          geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
              if (continuation.isActive) continuation.resume(addresses.firstOrNull())
            }
            override fun onError(errorMessage: String?) {
              if (continuation.isActive) continuation.resume(null)
            }
          })
        }
      } else {
        @Suppress("DEPRECATION")
        geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
      }
    } catch (_: Exception) {
      null
    }
  }
}
