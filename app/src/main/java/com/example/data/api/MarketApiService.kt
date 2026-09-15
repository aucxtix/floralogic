package com.example.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class MarketPriceDto(
    val id: String,
    val crop: String,
    val market: String,
    val unit: String,
    val minPrice: Double,
    val maxPrice: Double,
    val modalPrice: Double,
    val timestamp: Long,
    val source: String
)

data class MarketForecastDto(
    val crop: String,
    val market: String,
    val expectedMin: Double,
    val expectedMax: Double,
    val trend: String,
    val confidence: String,
    val horizonDays: Int,
    val predictionTimestamp: Long
)

/**
 * Retrofit Interface simulating connection to the modular backend for Market data
 * Route: /api/market
 */
interface MarketApiService {
    @GET("/api/market/prices/{crop}")
    suspend fun getLivePrices(@Path("crop") crop: String): List<MarketPriceDto>

    @GET("/api/market/forecast/{crop}")
    suspend fun getPriceForecast(@Path("crop") crop: String): MarketForecastDto
}
