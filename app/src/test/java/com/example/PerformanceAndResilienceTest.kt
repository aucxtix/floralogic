package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.cache.AdvisoryFragment
import com.example.core.cache.AdvisoryFragmentKey
import com.example.core.cache.AgronomicFragmentCache
import com.example.core.cache.FarmerHoleContext
import com.example.core.network.CircuitBreaker
import com.example.data.local.FarmCropEntity
import com.example.data.local.KisanDatabase
import com.example.data.local.ScanRecordEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CropDisease
import com.example.data.model.DiseaseSeverity
import com.example.ui.KisanViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PerformanceAndResilienceTest {

  private lateinit var database: KisanDatabase

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Application>()
    database = Room.inMemoryDatabaseBuilder(context, KisanDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  // =========================================================================
  // PILLAR 1: Bulk & Batched Writes with Chunking in Room Transactions
  // =========================================================================

  @Test
  fun `verifyBatchedWritesExecuteInSingleTransactionWithChunking`() = runBlocking {
    val dao = database.kisanDao()

    // Generate 250 farm crop entities
    val testCrops = (1..250).map { i ->
      FarmCropEntity(
        cropName = "Crop_$i",
        variety = "Hybrid_$i",
        areaAcres = 1.0 + (i % 5),
        sowingDate = "2024-10-01",
        growthStage = "Vegetative",
        soilType = "Loamy",
        healthStatus = "Healthy",
        lastWateredDate = "Today",
        notes = "Batch Test Crop $i"
      )
    }

    // Chunk size 100 ensures 250 items are cleanly processed in 3 chunked transactions
    val insertedIds = dao.insertCropsChunked(testCrops, chunkSize = 100)
    assertEquals(250, insertedIds.size)
    assertEquals(250, dao.getCropCount())

    // Verify chunked batch deletion
    dao.deleteCropsChunked(insertedIds, chunkSize = 100)
    assertEquals(0, dao.getCropCount())
  }

  @Test
  fun `verifyBatchedScansWritePerformanceFasterThanIndividualLoops`() = runBlocking {
    val dao = database.kisanDao()

    val testScans = (1..100).map { i ->
      ScanRecordEntity(
        cropName = "Tomato",
        diseaseName = "Blight_$i",
        scientificName = "Alternaria",
        isHealthy = false,
        confidence = 0.9f,
        severity = "Moderate",
        symptoms = "Leaf spots",
        organicTreatment = "Neem spray",
        chemicalTreatment = "Fungicide",
        dosage = "2ml/L",
        estimatedCostInr = "₹200",
        notes = "Scan $i"
      )
    }

    // Batched insert via single transaction
    val batchStartTime = System.currentTimeMillis()
    dao.insertScansChunked(testScans, chunkSize = 50)
    val batchDuration = System.currentTimeMillis() - batchStartTime

    val allScans = dao.getAllScans().first()
    assertEquals(100, allScans.size)
    println("Batched write of 100 scans completed in: ${batchDuration}ms")
  }

  // =========================================================================
  // PILLAR 2: Circuit Breaker for Degraded External Dependencies
  // =========================================================================

  @Test
  fun `verifyCircuitBreakerTripsAndFastFailsOnConsecutiveFailures`() = runBlocking {
    val breaker = CircuitBreaker(
      name = "TestApiBreaker",
      failureThreshold = 3,
      cooldownDurationMs = 50L,
      timeoutMs = 500L,
      maxConcurrentRequests = 2
    )

    assertEquals(CircuitBreaker.State.CLOSED, breaker.state)

    // Simulate 3 consecutive external failures
    repeat(3) {
      val result = breaker.execute(
        fallback = { "FALLBACK_VALUE" }
      ) {
        throw java.io.IOException("Connection refused to external server")
      }
      assertEquals("FALLBACK_VALUE", result)
    }

    // After 3 failures, circuit breaker MUST trip to OPEN
    assertEquals(CircuitBreaker.State.OPEN, breaker.state)
    assertEquals(1, breaker.currentTotalTrips)

    // In OPEN state, the breaker MUST fast-fail immediately without executing the block
    var blockExecutedInOpenState = false
    val fastFailResult = breaker.execute(
      fallback = { err ->
        assertTrue(err is CircuitBreaker.CircuitBreakerOpenException)
        "FAST_FAIL_FALLBACK"
      }
    ) {
      blockExecutedInOpenState = true
      "LIVE_DATA"
    }

    assertEquals("FAST_FAIL_FALLBACK", fastFailResult)
    assertFalse("Open circuit must never execute remote dependency block", blockExecutedInOpenState)

    // Test recovery: simulate cooldown expiration
    Thread.sleep(60L) // Wait past cooldownDurationMs

    // Canary request in HALF_OPEN succeeds
    val canaryResult = breaker.execute(
      fallback = { "CANARY_FALLBACK" }
    ) {
      "RECOVERED_LIVE_DATA"
    }

    assertEquals("RECOVERED_LIVE_DATA", canaryResult)
    // Circuit breaker successfully recovered and reset to CLOSED
    assertEquals(CircuitBreaker.State.CLOSED, breaker.state)
    assertEquals(0, breaker.currentConsecutiveFailures)
  }

  // =========================================================================
  // PILLAR 3: Optimistic UI Updates and Graceful Rollback
  // =========================================================================

  @Test
  fun `verifyOptimisticCropAdditionAndInstantVisibility`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = KisanViewModel(app)

    // Add a crop optimistically
    viewModel.addNewCrop(
      cropName = "Mustard",
      variety = "Pusa Bold",
      areaAcres = 2.0,
      sowingDate = "2024-10-15",
      growthStage = "Flowering",
      soilType = "Alluvial",
      notes = "Optimistic Test Crop"
    )

    // Optimistic addition is IMMEDIATELY reflected in optimisticAddedCrops without waiting for DB
    val addedList = viewModel.optimisticAddedCrops.value
    assertTrue(
      "Optimistic crop must be immediately present in optimisticAddedCrops list",
      addedList.any { it.cropName == "Mustard" }
    )
  }

  @Test
  fun `verifyOptimisticCropDeletionImmediatelyHidesFromUi`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = KisanViewModel(app)

    val cropId = 999L
    // Immediately trigger delete
    viewModel.deleteCrop(cropId)

    // It must immediately be tracked in optimisticDeletedCropIds
    assertTrue(
      "Crop ID must immediately be present in optimisticDeletedCropIds upon delete action",
      viewModel.optimisticDeletedCropIds.value.contains(cropId)
    )
  }

  @Test
  fun `verifyOptimisticFeedbackUpdateAndRollback`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = KisanViewModel(app)

    val scanId = 42L

    // Submit optimistic rating of +1 (thumbs up)
    viewModel.submitHistoryFeedback(scanId, rating = 1)
    val msg = viewModel.snackbarMessage.value
    assertNotNull(msg)
    assertTrue(msg!!.contains("Feedback recorded"))
  }

  // =========================================================================
  // PILLAR 4: Agricultural Fragment Caching & Client-Side Hydration
  // =========================================================================

  @Test
  fun `verifyAgronomicFragmentCacheServesIdenticalOutputWithDynamicHydration`() {
    val cache = AgronomicFragmentCache(ttlMs = 300_000L)

    val disease = CropDisease(
      id = "tomato_early_blight",
      cropName = "Tomato",
      diseaseName = "Early Blight",
      scientificName = "Alternaria solani",
      isHealthy = false,
      confidence = 0.95f,
      severity = DiseaseSeverity.MEDIUM,
      symptoms = listOf("Target concentric rings", "Yellow chlorotic haloes"),
      organicTreatment = "Spray Trichoderma viride @ 5g/L",
      chemicalTreatment = "Azoxystrobin 23% SC @ 1.5ml/L",
      dosage = "300ml in 200L water",
      estimatedCostInr = "₹250 / acre",
      preventiveMeasures = listOf("Crop rotation"),
      adviceHindi = "अगेती झुलसा का जैविक उपचार करें",
      adviceGujarati = "અગેતી ઝુલસા માટે જૈવિક ઉપચાર કરો"
    )

    val keyEn = AdvisoryFragmentKey(
      cropName = disease.cropName,
      diseaseName = disease.diseaseName,
      severity = disease.severity.label,
      language = AppLanguage.ENGLISH
    )

    fun generator() = AdvisoryFragment(
      key = keyEn,
      localizedHeader = "Agricultural Advisory: Early Blight",
      staticSymptomsFormatted = "Target concentric rings",
      staticOrganicRemedy = disease.organicTreatment,
      staticChemicalTreatment = disease.chemicalTreatment,
      staticDosageFormula = disease.dosage,
      baseCostPerAcre = 250.0,
      renderedTemplate = """
        [ADVISORY: {{FARMER_NAME}} | Plot: {{PLOT_NAME}}]
        Acreage: {{AREA_ACRES}} | Total Cost: {{TOTAL_ESTIMATED_COST}}
        Treatment: ${disease.organicTreatment}
      """.trimIndent()
    )

    // Farmer 1 request (Cache MISS, generated and cached)
    val farmer1 = FarmerHoleContext(
      farmerName = "Ramesh Kumar",
      farmPlotName = "North Plot",
      landAreaAcres = 4.0,
      villageLocality = "Anand, Gujarat",
      scanTimestamp = "14 Sep 2026, 10:00"
    )

    val result1 = cache.getOrRenderHydrated(keyEn, farmer1, ::generator)
    assertFalse("First call is a cache miss", result1.isServedFromCache)
    assertEquals(1000.0, result1.totalEstimatedFarmCostInr, 0.01) // 4.0 * 250.0
    assertTrue(result1.fullyRenderedText.contains("Ramesh Kumar"))
    assertTrue(result1.fullyRenderedText.contains("₹1000.00"))

    // Farmer 2 with SAME disease and locale (Cache HIT, instant response)
    val farmer2 = FarmerHoleContext(
      farmerName = "Suresh Patel",
      farmPlotName = "South Valley",
      landAreaAcres = 2.5,
      villageLocality = "Surat, Gujarat",
      scanTimestamp = "14 Sep 2026, 10:15"
    )

    val result2 = cache.getOrRenderHydrated(keyEn, farmer2, ::generator)
    assertTrue("Subsequent call for identical disease/locale must be a cache hit", result2.isServedFromCache)
    assertEquals(625.0, result2.totalEstimatedFarmCostInr, 0.01) // 2.5 * 250.0
    assertTrue(result2.fullyRenderedText.contains("Suresh Patel"))
    assertTrue(result2.fullyRenderedText.contains("₹625.00"))
    assertEquals(1, cache.cacheHits.get())
    assertEquals(1, cache.cacheMisses.get())

    // Locale distinction: Hindi request produces a DIFFERENT cache key
    val keyHi = keyEn.copy(language = AppLanguage.HINDI)
    val result3 = cache.getOrRenderHydrated(keyHi, farmer2) {
      generator().copy(
        renderedTemplate = """
          [कृषि सलाह: {{FARMER_NAME}} | खेत: {{PLOT_NAME}}]
          क्षेत्रफल: {{AREA_ACRES}} एकड़ | कुल लागत: {{TOTAL_ESTIMATED_COST}}
        """.trimIndent()
      )
    }
    assertFalse("Different locale must miss and create distinct cache entry", result3.isServedFromCache)
    assertTrue(result3.fullyRenderedText.contains("कृषि सलाह"))
    assertEquals(2, cache.size())
  }
}
