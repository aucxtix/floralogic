package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_records")
data class ScanRecordEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val cropName: String,
  val diseaseName: String,
  val scientificName: String,
  val isHealthy: Boolean,
  val confidence: Float,
  val severity: String,
  val symptoms: String, // comma separated or bulleted
  val organicTreatment: String,
  val chemicalTreatment: String,
  val dosage: String,
  val estimatedCostInr: String,
  val imageUri: String? = null,
  val notes: String = "",
  val feedbackRating: Int = 0, // 0 = unrated, 1 = accurate/thumbs up, -1 = inaccurate/thumbs down
  val feedbackReason: String = ""
)
