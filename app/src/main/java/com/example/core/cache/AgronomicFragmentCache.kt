package com.example.core.cache

import com.example.data.model.AppLanguage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Cache key accounting for critical variations: crop, disease, severity, and locale.
 */
data class AdvisoryFragmentKey(
  val cropName: String,
  val diseaseName: String,
  val severity: String,
  val language: AppLanguage
)

/**
 * Server/Edge-rendered agricultural advisory fragment.
 * Identical across all users with the same crop, disease, severity, and locale.
 */
data class AdvisoryFragment(
  val key: AdvisoryFragmentKey,
  val localizedHeader: String,
  val staticSymptomsFormatted: String,
  val staticOrganicRemedy: String,
  val staticChemicalTreatment: String,
  val staticDosageFormula: String,
  val baseCostPerAcre: Double,
  val renderedTemplate: String,
  val generatedAt: Long = System.currentTimeMillis()
)

/**
 * Dynamic farmer-specific parameters to hydrate into the static fragment ("hole punching").
 */
data class FarmerHoleContext(
  val farmerName: String,
  val farmPlotName: String,
  val landAreaAcres: Double,
  val villageLocality: String,
  val scanTimestamp: String
)

/**
 * Resulting personalized fragment after client-side hydration.
 */
data class HydratedAdvisory(
  val fullyRenderedText: String,
  val totalEstimatedFarmCostInr: Double,
  val isServedFromCache: Boolean,
  val renderLatencyNs: Long
)

/**
 * Fragment cache for identical diagnostic & treatment output across farmers.
 * Supports TTL regeneration, on-content-change invalidation, and client-side hydration of dynamic holes.
 */
class AgronomicFragmentCache(
  private val ttlMs: Long = 3_600_000L // 1 hour default TTL
) {
  private val cache = ConcurrentHashMap<AdvisoryFragmentKey, AdvisoryFragment>()
  
  val cacheHits = AtomicLong(0)
  val cacheMisses = AtomicLong(0)

  /**
   * Retrieves or computes the static fragment, then hydrates dynamic farmer holes.
   */
  fun getOrRenderHydrated(
    key: AdvisoryFragmentKey,
    context: FarmerHoleContext,
    generator: () -> AdvisoryFragment
  ): HydratedAdvisory {
    val startTime = System.nanoTime()
    val now = System.currentTimeMillis()
    
    val existing = cache[key]
    val (fragment, isHit) = if (existing != null && (now - existing.generatedAt) < ttlMs) {
      cacheHits.incrementAndGet()
      Pair(existing, true)
    } else {
      cacheMisses.incrementAndGet()
      val fresh = generator()
      cache[key] = fresh
      Pair(fresh, false)
    }

    // Dynamic Hole Punching / Client-Side Hydration
    val totalCost = fragment.baseCostPerAcre * context.landAreaAcres
    val hydratedText = fragment.renderedTemplate
      .replace("{{FARMER_NAME}}", context.farmerName)
      .replace("{{PLOT_NAME}}", context.farmPlotName)
      .replace("{{VILLAGE}}", context.villageLocality)
      .replace("{{AREA_ACRES}}", context.landAreaAcres.toString())
      .replace("{{TOTAL_ESTIMATED_COST}}", String.format(java.util.Locale.ENGLISH, "₹%.2f", totalCost))
      .replace("{{SCAN_TIMESTAMP}}", context.scanTimestamp)

    val latency = System.nanoTime() - startTime
    return HydratedAdvisory(
      fullyRenderedText = hydratedText,
      totalEstimatedFarmCostInr = totalCost,
      isServedFromCache = isHit,
      renderLatencyNs = latency
    )
  }

  fun invalidate(key: AdvisoryFragmentKey) {
    cache.remove(key)
  }

  fun invalidateAll() {
    cache.clear()
  }

  fun size(): Int = cache.size
}
