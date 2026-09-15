package com.example.core.location

import android.content.Context
import android.util.Log
import com.example.core.storage.KissanStorageService
import com.example.core.storage.StorageService
import com.example.data.location.LocationResult
import com.example.data.location.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

data class FarmLocation(
  val village: String = "Bardoli",
  val district: String = "Surat",
  val state: String = "Gujarat",
  val latitude: Double = 21.1175,
  val longitude: Double = 73.1118,
  val isManual: Boolean = false
) {
  fun toDisplayString(): String {
    return if (district.isNotBlank() && village != district) {
      "$village, $district, $state"
    } else {
      "$village, $state"
    }
  }

  fun toJson(): String {
    val obj = JSONObject()
    obj.put("village", village)
    obj.put("district", district)
    obj.put("state", state)
    obj.put("latitude", latitude)
    obj.put("longitude", longitude)
    obj.put("isManual", isManual)
    return obj.toString()
  }

  companion object {
    fun fromJson(jsonStr: String): FarmLocation? {
      if (jsonStr.isBlank()) return null
      return try {
        val obj = JSONObject(jsonStr)
        FarmLocation(
          village = obj.optString("village", "Bardoli"),
          district = obj.optString("district", "Surat"),
          state = obj.optString("state", "Gujarat"),
          latitude = obj.optDouble("latitude", 21.1175),
          longitude = obj.optDouble("longitude", 73.1118),
          isManual = obj.optBoolean("isManual", false)
        )
      } catch (e: Exception) {
        null
      }
    }
  }
}

sealed interface LocationDetectionResult {
  data class Success(val location: FarmLocation) : LocationDetectionResult
  object PermissionDenied : LocationDetectionResult
  object LocationDisabled : LocationDetectionResult
  data class Error(val message: String) : LocationDetectionResult
}

/**
 * Robust FarmerLocationService.
 * Handles device geolocation, saved persistence, and manual selection fallback.
 * Never crashes when permission is denied.
 */
interface FarmerLocationService {
  val currentLocation: StateFlow<FarmLocation>
  suspend fun detectLocation(): LocationDetectionResult
  suspend fun saveLocation(location: FarmLocation)
  suspend fun updateLocation(location: FarmLocation)
  suspend fun clearLocation()
  fun getSavedLocation(): FarmLocation
}

class KissanFarmerLocationService(
  private val context: Context,
  private val storageService: StorageService,
  private val deviceLocationService: LocationService = LocationService(context)
) : FarmerLocationService {

  private val _currentLocation: MutableStateFlow<FarmLocation>

  init {
    val saved = loadSavedLocation()
    _currentLocation = MutableStateFlow(saved)
  }

  override val currentLocation: StateFlow<FarmLocation> = _currentLocation.asStateFlow()

  private fun loadSavedLocation(): FarmLocation {
    val rawJson = storageService.getString(KissanStorageService.KEY_SAVED_LOCATION_JSON, "")
    return FarmLocation.fromJson(rawJson) ?: FarmLocation()
  }

  override fun getSavedLocation(): FarmLocation {
    return _currentLocation.value
  }

  override suspend fun detectLocation(): LocationDetectionResult {
    return try {
      when (val result = deviceLocationService.getCurrentCoordinates()) {
        is LocationResult.Success -> {
          val loc = FarmLocation(
            village = result.locality ?: "Bardoli",
            district = result.locality ?: "Surat",
            state = result.state ?: "Gujarat",
            latitude = result.coordinates.latitude,
            longitude = result.coordinates.longitude,
            isManual = false
          )
          saveLocation(loc)
          LocationDetectionResult.Success(loc)
        }
        is LocationResult.PermissionDenied -> LocationDetectionResult.PermissionDenied
        is LocationResult.LocationDisabled -> LocationDetectionResult.LocationDisabled
        is LocationResult.Error -> LocationDetectionResult.Error(result.message)
      }
    } catch (e: Exception) {
      Log.e("FarmerLocationService", "Error detecting location", e)
      LocationDetectionResult.Error(e.localizedMessage ?: "Unknown location error")
    }
  }

  override suspend fun saveLocation(location: FarmLocation) {
    _currentLocation.value = location
    try {
      storageService.putString(KissanStorageService.KEY_SAVED_LOCATION_JSON, location.toJson())
    } catch (e: Exception) {
      Log.e("FarmerLocationService", "Failed to save location", e)
    }
  }

  override suspend fun updateLocation(location: FarmLocation) {
    saveLocation(location)
  }

  override suspend fun clearLocation() {
    storageService.remove(KissanStorageService.KEY_SAVED_LOCATION_JSON)
    _currentLocation.value = FarmLocation()
  }

  companion object {
    @Volatile
    private var INSTANCE: KissanFarmerLocationService? = null

    fun getInstance(context: Context, storageService: StorageService): KissanFarmerLocationService {
      return INSTANCE ?: synchronized(this) {
        val instance = KissanFarmerLocationService(context.applicationContext, storageService)
        INSTANCE = instance
        instance
      }
    }
  }
}
