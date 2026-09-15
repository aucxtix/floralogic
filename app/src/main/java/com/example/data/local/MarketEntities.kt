package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_prices")
data class MarketPriceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val marketName: String,
    val cropName: String,
    val unit: String,
    val minPrice: Double,
    val maxPrice: Double,
    val modalPrice: Double,
    val timestamp: Long,
    val source: String
)
