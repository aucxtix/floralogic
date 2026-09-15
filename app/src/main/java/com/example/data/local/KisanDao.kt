package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KisanDao {
  // Scans
  @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
  fun getAllScans(): Flow<List<ScanRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScan(scan: ScanRecordEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScans(scans: List<ScanRecordEntity>): List<Long>

  @Query("DELETE FROM scan_records WHERE id = :id")
  suspend fun deleteScanById(id: Long)

  @Query("DELETE FROM scan_records WHERE id IN (:ids)")
  suspend fun deleteScansChunk(ids: List<Long>)

  @Query("UPDATE scan_records SET feedbackRating = :rating, feedbackReason = :reason WHERE id = :id")
  suspend fun updateScanFeedback(id: Long, rating: Int, reason: String)

  @Query("DELETE FROM scan_records")
  suspend fun clearAllScans()

  // Farm Crops
  @Query("SELECT * FROM farm_crops ORDER BY id DESC")
  fun getAllCrops(): Flow<List<FarmCropEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCrop(crop: FarmCropEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCrops(crops: List<FarmCropEntity>): List<Long>

  @Query("DELETE FROM farm_crops WHERE id = :id")
  suspend fun deleteCropById(id: Long)

  @Query("DELETE FROM farm_crops WHERE id IN (:ids)")
  suspend fun deleteCropsChunk(ids: List<Long>)

  @Query("SELECT COUNT(*) FROM farm_crops")
  suspend fun getCropCount(): Int

  @Query("DELETE FROM farm_crops")
  suspend fun clearAllCrops()

  // Sensor Readings
  @Query("SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentSensorReadings(limit: Int = 100): Flow<List<SensorReadingEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSensorReading(reading: SensorReadingEntity): Long

  // Devices
  @Query("SELECT * FROM devices")
  fun getAllDevices(): Flow<List<DeviceEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDevice(device: DeviceEntity)

  // Market Prices
  @Query("SELECT * FROM market_prices ORDER BY timestamp DESC")
  fun getMarketPrices(): Flow<List<MarketPriceEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMarketPrices(prices: List<MarketPriceEntity>)

  // Weather Observations
  @Query("SELECT * FROM weather_observations ORDER BY timestamp DESC LIMIT 1")
  fun getLatestWeather(): Flow<WeatherObservationEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWeatherObservation(observation: WeatherObservationEntity)
  /**
   * Batched and chunked writes inside a transaction to prevent statement overflow
   * and reduce round trips on write-heavy bulk operations.
   */
  @androidx.room.Transaction
  suspend fun insertCropsChunked(crops: List<FarmCropEntity>, chunkSize: Int = 100): List<Long> {
    val results = mutableListOf<Long>()
    crops.chunked(chunkSize).forEach { chunk ->
      results.addAll(insertCrops(chunk))
    }
    return results
  }

  @androidx.room.Transaction
  suspend fun insertScansChunked(scans: List<ScanRecordEntity>, chunkSize: Int = 100): List<Long> {
    val results = mutableListOf<Long>()
    scans.chunked(chunkSize).forEach { chunk ->
      results.addAll(insertScans(chunk))
    }
    return results
  }

  @androidx.room.Transaction
  suspend fun deleteCropsChunked(ids: List<Long>, chunkSize: Int = 100) {
    ids.chunked(chunkSize).forEach { chunk ->
      deleteCropsChunk(chunk)
    }
  }

  @androidx.room.Transaction
  suspend fun deleteScansChunked(ids: List<Long>, chunkSize: Int = 100) {
    ids.chunked(chunkSize).forEach { chunk ->
      deleteScansChunk(chunk)
    }
  }
}
