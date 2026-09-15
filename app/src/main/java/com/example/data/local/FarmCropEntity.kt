package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farm_crops")
data class FarmCropEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val cropName: String,
  val variety: String,
  val areaAcres: Double,
  val sowingDate: String,
  val growthStage: String,
  val soilType: String,
  val healthStatus: String,
  val lastWateredDate: String,
  val notes: String
)
