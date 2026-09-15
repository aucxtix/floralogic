package com.example.ai

import com.example.BuildConfig
import com.example.data.model.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Recognized farmer intent categories for semantic routing.
 */
enum class FarmerQueryIntent {
    FARM_STATUS,
    CROP_HEALTH,
    DISEASE,
    IRRIGATION,
    WEATHER,
    YIELD,
    MARKET,
    SELLING,
    SATELLITE,
    HARDWARE,
    HISTORY,
    RECOMMENDATION,
    FARM_PLAN,
    GENERAL_AGRI
}

/**
 * FarmCopilotService
 *
 * Implements grounded, explainable agricultural decision support:
 * 1. Intent Detection across Hindi, Gujarati, English
 * 2. Precise context slicing from DigitalFarmState
 * 3. Execution of domain rule engines (IrrigationDecisionEngine, FarmRiskEngine, etc.)
 * 4. Grounded synthesis via Gemini LLM (with robust offline fallback that never hallucinates)
 */
typealias GeminiCopilotService = FarmCopilotService

class FarmCopilotService {

    fun detectIntent(query: String): FarmerQueryIntent {
        val q = query.lowercase().trim()
        return when {
            q.contains("pani") || q.contains("water") || q.contains("irrigate") || q.contains("sinchai") || q.contains("paani") || q.contains("પાણી") || q.contains("સિંચાઈ") || q.contains("drip") -> FarmerQueryIntent.IRRIGATION
            q.contains("rog") || q.contains("disease") || q.contains("bimari") || q.contains("keeda") || q.contains("blight") || q.contains("fungus") || q.contains("રોગ") || q.contains("જીવાત") || q.contains("spray") -> FarmerQueryIntent.DISEASE
            q.contains("bhav") || q.contains("price") || q.contains("mandi") || q.contains("rate") || q.contains("apmc") || q.contains("ભાવ") || q.contains("બજાર") || q.contains("मार्केट") -> FarmerQueryIntent.MARKET
            q.contains("bechna") || q.contains("sell") || q.contains("bechu") || q.contains("વેચવું") || q.contains("ક્યારે વેચવું") -> FarmerQueryIntent.SELLING
            q.contains("utpadan") || q.contains("yield") || q.contains("pedavar") || q.contains("paidaavar") || q.contains("ઉત્પાદન") || q.contains("tonnes") -> FarmerQueryIntent.YIELD
            q.contains("mausam") || q.contains("weather") || q.contains("barish") || q.contains("rain") || q.contains("हवामान") || q.contains("વરસાદ") || q.contains("તાપમાન") -> FarmerQueryIntent.WEATHER
            q.contains("satellite") || q.contains("ndvi") || q.contains("upgrah") || q.contains("સેટેલાઇટ") -> FarmerQueryIntent.SATELLITE
            q.contains("sensor") || q.contains("iot") || q.contains("esp32") || q.contains("battery") || q.contains("ડિવાઇસ") -> FarmerQueryIntent.HARDWARE
            q.contains("swasthya") || q.contains("health") || q.contains("condition") || q.contains("तबीयत") || q.contains("સ્થિતિ") -> FarmerQueryIntent.CROP_HEALTH
            q.contains("plan") || q.contains("karya") || q.contains("yojana") || q.contains("આયોજન") || q.contains("7 day") -> FarmerQueryIntent.FARM_PLAN
            q.contains("kya karu") || q.contains("advice") || q.contains("salah") || q.contains("recommend") || q.contains("સલાહ") -> FarmerQueryIntent.RECOMMENDATION
            q.contains("history") || q.contains("purana") || q.contains("record") || q.contains("ઇતિહાસ") -> FarmerQueryIntent.HISTORY
            else -> FarmerQueryIntent.FARM_STATUS
        }
    }

    suspend fun askQuestion(
        question: String,
        farmState: DigitalFarmState,
        preferredLanguage: AppLanguage = AppLanguage.ENGLISH
    ): String = withContext(Dispatchers.IO) {
        val intent = detectIntent(question)

        // Pre-compute verified domain model outputs based on intent
        val groundedFacts = extractGroundedFacts(intent, farmState)

        val apiKey = "" // BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty()) {
            return@withContext buildOfflineGroundedResponse(intent, farmState, groundedFacts, preferredLanguage)
        }

        val systemPrompt = """
            You are Kissan AI, a serious, trustworthy, production-grade agricultural decision support copilot for Indian farmers.
            
            STRICT AGRICULTURAL SAFETY RULES:
            1. Never invent or hallucinate data. Ground your response purely on the verified FARM CONTEXT and ENGINE FACTS provided below.
            2. Answer the farmer's specific question directly in a friendly, practical, and respectful tone.
            3. Structure your response to answer:
               - WHAT is happening?
               - WHY is it happening?
               - WHAT SHOULD THE FARMER DO?
            4. Never guarantee future crop yield or market prices.
            5. Respond in the farmer's preferred language: ${preferredLanguage.name} (if Hindi or Gujarati, use authentic agricultural terminology in Devanagari/Gujarati script).
            
            INTENT DETECTED: $intent
            
            VERIFIED ENGINE FACTS:
            $groundedFacts
            
            COMPLETE FARM CONTEXT:
            ${farmState.toContextString()}
        """.trimIndent()

        try {
            val answer = GeminiApiClient.generateContent(
                apiKey = apiKey,
                prompt = question,
                systemInstruction = systemPrompt,
                temperature = 0.2f
            )
            if (!answer.isNullOrBlank()) {
                answer
            } else {
                buildOfflineGroundedResponse(intent, farmState, groundedFacts, preferredLanguage)
            }
        } catch (e: Exception) {
            buildOfflineGroundedResponse(intent, farmState, groundedFacts, preferredLanguage)
        }
    }

    private fun extractGroundedFacts(intent: FarmerQueryIntent, state: DigitalFarmState): String {
        val sb = StringBuilder()
        when (intent) {
            FarmerQueryIntent.IRRIGATION -> {
                val irrigation = IrrigationDecisionEngine.evaluateIrrigation(state)
                sb.append("RECOMMENDATION: ${irrigation.action.label}\n")
                sb.append("REASON: ${irrigation.headlineReason}\n")
                sb.append("DETAIL: ${irrigation.detailedExplanation}\n")
                sb.append("OPTIMAL TIMING: ${irrigation.optimalApplicationWindow}\n")
                sb.append("SOIL MOISTURE: ${state.sensorData.soilMoisturePercent}% (Target: ${state.soil.targetMoistureMin}-${state.soil.targetMoistureMax}%)\n")
                sb.append("RAIN CHANCE: ${state.weather.rainProbabilityPercent}%\n")
            }
            FarmerQueryIntent.DISEASE -> {
                val activeScans = state.recentScans.filter { !it.isHealthy }
                if (activeScans.isNotEmpty()) {
                    val latest = activeScans.first()
                    val knowledge = DiseaseKnowledgeBase.getKnowledge(latest.diseaseName)
                    sb.append("ACTIVE DISEASE: ${latest.diseaseName} (Severity: ${latest.severity})\n")
                    sb.append("SYMPTOMS: ${latest.symptoms}\n")
                    sb.append("ORGANIC REMEDY: ${knowledge.organicRemedies}\n")
                    sb.append("CHEMICAL CONTROL: ${knowledge.chemicalManagementGuideline} at ${knowledge.dosageFormula}\n")
                    sb.append("PREVENTION: ${knowledge.preventionPractices.joinToString("; ")}\n")
                } else {
                    sb.append("DISEASE STATUS: No active crop infections detected on latest leaf scans.\n")
                    sb.append("PREVENTIVE ADVICE: Maintain clean field borders and avoid excess nitrogen fertilizer.\n")
                }
            }
            FarmerQueryIntent.YIELD -> {
                val yield = YieldPredictionEngine.predictYield(state)
                sb.append("EXPECTED YIELD: ${yield.expectedYieldRange}\n")
                sb.append("POTENTIAL BENCHMARK: ${yield.benchmarkPotentialYield}\n")
                sb.append("YIELD GAP: ${yield.yieldGapPercent}%\n")
                sb.append("LIMITING FACTORS: ${yield.primaryLimitingFactors.joinToString("; ")}\n")
                sb.append("ACTION TO CLOSE GAP: ${yield.yieldEnhancementAction}\n")
            }
            FarmerQueryIntent.MARKET, FarmerQueryIntent.SELLING -> {
                val forecast = MarketForecastEngine.forecastPrices(state)
                val selling = MarketForecastEngine.evaluateSellingDecision(state)
                sb.append("COMMODITY: ${state.marketData.commodity} at ${state.marketData.mandiName}\n")
                sb.append("CURRENT MODAL PRICE: ₹${state.marketData.modalPricePerQ}/quintal\n")
                sb.append("7-DAY FORECAST: ₹${forecast.forecast7dMin} – ₹${forecast.forecast7dMax}/q (${forecast.projectedTrend})\n")
                sb.append("SELLING DIRECTIVE: ${selling.directive.label}\n")
                sb.append("SELLING REASON: ${selling.primaryReason}\n")
                sb.append("ADVICE: ${selling.detailedAdvice}\n")
            }
            FarmerQueryIntent.CROP_HEALTH -> {
                val health = CropHealthEngine.evaluateCropHealth(state)
                sb.append("HEALTH SCORE: ${health.healthScore}/100 (${health.healthCategory})\n")
                sb.append("TREND: ${health.trend}\n")
                sb.append("PRIMARY STRESS: ${health.primaryStress}\n")
                sb.append("EXPLANATION: ${health.explanationFarmerLanguage}\n")
            }
            else -> {
                val briefing = DailyFarmBriefingEngine.generateBriefing(state)
                val recs = FarmRecommendationEngine.generateRecommendations(state).take(2)
                sb.append("FARM HEALTH: ${briefing.farmHealthScore}/100\n")
                sb.append("ACTIVE CROP: ${state.activeCropContext.cropName} at ${state.activeCropContext.currentStage} stage (DAS: ${state.activeCropContext.daysAfterSowing})\n")
                sb.append("WEATHER: ${briefing.weatherOverview}\n")
                sb.append("TOP ACTIONS:\n")
                recs.forEach { r -> sb.append("- ${r.what} (Why: ${r.why})\n") }
            }
        }
        return sb.toString()
    }

    private fun buildOfflineGroundedResponse(
        intent: FarmerQueryIntent,
        state: DigitalFarmState,
        facts: String,
        lang: AppLanguage
    ): String {
        return when (lang) {
            AppLanguage.HINDI -> buildHindiOfflineResponse(intent, state, facts)
            AppLanguage.GUJARATI -> buildGujaratiOfflineResponse(intent, state, facts)
            else -> buildEnglishOfflineResponse(intent, state, facts)
        }
    }

    private fun buildEnglishOfflineResponse(
        intent: FarmerQueryIntent,
        state: DigitalFarmState,
        facts: String
    ): String {
        return when (intent) {
            FarmerQueryIntent.IRRIGATION -> {
                val irr = IrrigationDecisionEngine.evaluateIrrigation(state)
                """
                |**Irrigation Decision: ${irr.action.label}**
                |
                |• **What is happening:** Soil moisture is currently at ${state.sensorData.soilMoisturePercent}% (Target: ${state.soil.targetMoistureMin}% – ${state.soil.targetMoistureMax}%).
                |• **Why:** ${irr.detailedExplanation}
                |• **What you should do:** ${if (irr.action == IrrigationAction.IRRIGATE) "Run drip irrigation for ${irr.recommendedDripHours ?: 2.5} hours early tomorrow morning." else "Hold irrigation today and re-check root zone in 24 hours."}
                |• **Precipitation outlook:** ${state.weather.rainProbabilityPercent}% chance of rain.
                """.trimMargin()
            }
            FarmerQueryIntent.DISEASE -> {
                val activeScans = state.recentScans.filter { !it.isHealthy }
                if (activeScans.isNotEmpty()) {
                    val s = activeScans.first()
                    val k = DiseaseKnowledgeBase.getKnowledge(s.diseaseName)
                    """
                    |**Disease Diagnosis: ${s.diseaseName}**
                    |
                    |• **Severity:** ${s.severity}
                    |• **What is happening:** Symptoms detected on ${s.cropName} foliage.
                    |• **Recommended Organic Treatment:** ${k.organicRemedies}
                    |• **Chemical Control:** ${k.chemicalManagementGuideline} (${k.dosageFormula})
                    |• **Safety:** Follow registered CIBRC labels and wear protective gear.
                    """.trimMargin()
                } else {
                    """
                    |**Foliar Health: Clear & Healthy**
                    |
                    |No active fungal or bacterial diseases were found on recent leaf scans. Continue weekly scouting and maintain balanced drip irrigation.
                    """.trimMargin()
                }
            }
            FarmerQueryIntent.YIELD -> {
                val y = YieldPredictionEngine.predictYield(state)
                """
                |**Yield Forecast for ${state.activeCropContext.cropName}: ${y.expectedYieldRange}**
                |
                |• **Genetic Potential:** ${y.benchmarkPotentialYield} (Yield gap: ${y.yieldGapPercent}%)
                |• **Limiting Factors:**
                |  ${y.primaryLimitingFactors.joinToString("\n|  • ")}
                |• **What you should do:** ${y.yieldEnhancementAction}
                """.trimMargin()
            }
            FarmerQueryIntent.MARKET, FarmerQueryIntent.SELLING -> {
                val m = MarketForecastEngine.forecastPrices(state)
                val s = MarketForecastEngine.evaluateSellingDecision(state)
                """
                |**Market Decision: ${s.directive.label}**
                |
                |• **Current Price:** ₹${m.currentPricePerQ}/quintal at ${m.mandiName}
                |• **7-Day Forecast:** ₹${m.forecast7dMin} – ₹${m.forecast7dMax}/q (Trend: ${m.projectedTrend})
                |• **Why:** ${s.primaryReason}
                |• **Action Plan:** ${s.detailedAdvice}
                |• *Note: Market prices are statistical estimates, not financial guarantees.*
                """.trimMargin()
            }
            else -> {
                val b = DailyFarmBriefingEngine.generateBriefing(state)
                """
                |**Farm Status Briefing: ${state.farmerProfile.farmName}**
                |
                |• **Farm Health Score:** ${b.farmHealthScore}/100
                |• **Active Crop:** ${b.primaryCropName} at ${b.currentGrowthStage} stage (DAS: ${b.daysAfterSowing} days)
                |• **Weather:** ${b.weatherOverview}
                |• **Irrigation:** ${b.irrigationStatus}
                |• **Top Action Today:** ${b.topPriorities.firstOrNull()?.what ?: "Inspect active root zone moisture."}
                """.trimMargin()
            }
        }
    }

    private fun buildHindiOfflineResponse(
        intent: FarmerQueryIntent,
        state: DigitalFarmState,
        facts: String
    ): String {
        return when (intent) {
            FarmerQueryIntent.IRRIGATION -> {
                val irr = IrrigationDecisionEngine.evaluateIrrigation(state)
                """
                |**सिंचाई सलाह: ${if (irr.action == IrrigationAction.IRRIGATE) "अभी सिंचाई करें" else "सिंचाई रोकें / इंतजार करें"}**
                |
                |• **स्थिति:** मिट्टी की नमी ${state.sensorData.soilMoisturePercent}% है (अनुकूल स्तर: ${state.soil.targetMoistureMin}% – ${state.soil.targetMoistureMax}%).
                |• **कारण:** ${irr.headlineReason}
                |• **आपको क्या करना चाहिए:** ${if (irr.action == IrrigationAction.IRRIGATE) "कल सुबह 06:00 से 08:30 के बीच ड्रिप से लगभग ${irr.recommendedDripHours ?: 2.5} घंटे पानी दें।" else "आज पानी न दें। 24 घंटे बाद पुनः जांच करें।"}
                |• **बारिश की संभावना:** ${state.weather.rainProbabilityPercent}%
                """.trimMargin()
            }
            FarmerQueryIntent.DISEASE -> {
                val activeScans = state.recentScans.filter { !it.isHealthy }
                if (activeScans.isNotEmpty()) {
                    val s = activeScans.first()
                    val k = DiseaseKnowledgeBase.getKnowledge(s.diseaseName)
                    """
                    |**रोग निदान: ${s.diseaseName}**
                    |
                    |• **गंभीरता:** ${s.severity}
                    |• **जैविक उपचार:** ${k.organicRemedies}
                    |• **रासायनिक नियंत्रण:** ${k.chemicalManagementGuideline} (मात्रा: ${k.dosageFormula})
                    |• **सावधानी:** छिड़काव के समय दस्ताने और मास्क का प्रयोग करें।
                    """.trimMargin()
                } else {
                    """
                    |**फसल स्वास्थ्य: स्वस्थ एवं सुरक्षित**
                    |
                    |हाल के स्कैन में कोई सक्रिय फंगल या बैक्टीरियल रोग नहीं पाया गया है। संतुलित खाद और पानी जारी रखें।
                    """.trimMargin()
                }
            }
            FarmerQueryIntent.MARKET, FarmerQueryIntent.SELLING -> {
                val m = MarketForecastEngine.forecastPrices(state)
                val s = MarketForecastEngine.evaluateSellingDecision(state)
                """
                |**मंडी एवं बिक्री सलाह: ${if (s.directive == SellingDirective.SELL_NOW) "अभी बेचें" else "रुकें / प्रतीक्षा करें"}**
                |
                |• **वर्तमान भाव:** ₹${m.currentPricePerQ}/क्विंटल (${m.mandiName})
                |• **7-दिन का अनुमान:** ₹${m.forecast7dMin} – ₹${m.forecast7dMax}/क्विंटल (रुझान: ${m.projectedTrend})
                |• **सलाह:** ${s.detailedAdvice}
                """.trimMargin()
            }
            else -> {
                val b = DailyFarmBriefingEngine.generateBriefing(state)
                """
                |**खेत की स्थिति रिपोर्ट**
                |
                |• **खेत स्वास्थ्य स्कोर:** ${b.farmHealthScore}/100
                |• **फसल:** ${b.primaryCropName} (${b.currentGrowthStage}, बोआई के ${b.daysAfterSowing} दिन)
                |• **मौसम:** तापमान ${b.temperatureC}°C, बारिश की संभावना ${b.rainfallChancePercent}%
                |• **आज का मुख्य कार्य:** ${b.topPriorities.firstOrNull()?.what ?: "खेत की नमी की जांच करें।"}
                """.trimMargin()
            }
        }
    }

    private fun buildGujaratiOfflineResponse(
        intent: FarmerQueryIntent,
        state: DigitalFarmState,
        facts: String
    ): String {
        return when (intent) {
            FarmerQueryIntent.IRRIGATION -> {
                val irr = IrrigationDecisionEngine.evaluateIrrigation(state)
                """
                |**પિયત સલાહ: ${if (irr.action == IrrigationAction.IRRIGATE) "અત્યારે પિયત આપો" else "પિયત અટકાવો / રાહ જુઓ"}**
                |
                |• **પરિસ્થિતિ:** જમીનમાં ભેજનું પ્રમાણ ${state.sensorData.soilMoisturePercent}% છે (જરૂરી: ${state.soil.targetMoistureMin}% – ${state.soil.targetMoistureMax}%).
                |• **કારણ:** ${irr.headlineReason}
                |• **ખેડૂત મિત્ર શું કરવું:** ${if (irr.action == IrrigationAction.IRRIGATE) "આવતીકાલે વહેલી સવારે ડ્રિપ દ્વારા ${irr.recommendedDripHours ?: 2.5} કલાક પાણી આપો." else "આજે પિયત આપવાની જરૂર નથી. ૨૪ કલાક પછી ફરી તપાસો."}
                """.trimMargin()
            }
            FarmerQueryIntent.MARKET, FarmerQueryIntent.SELLING -> {
                val m = MarketForecastEngine.forecastPrices(state)
                """
                |**માર્કેટ અને વેચાણ સલાહ**
                |
                |• **હાલનો ભાવ:** ₹${m.currentPricePerQ}/ક્વિન્ટલ (${m.mandiName})
                |• **૭ દિવસનો અંદાજ:** ₹${m.forecast7dMin} – ₹${m.forecast7dMax}/ક્વિન્ટલ
                |• **સલાહ:** પાકની ગુણવત્તા ચકાસી ગ્રેડિંગ કરીને ધીમે ધીમે માર્કેટમાં માલ લાવો.
                """.trimMargin()
            }
            else -> {
                val b = DailyFarmBriefingEngine.generateBriefing(state)
                """
                |**ખેતરની દૈનિક સ્થિતિ**
                |
                |• **ખેતર હેલ્થ સ્કોર:** ${b.farmHealthScore}/100
                |• **પાક:** ${b.primaryCropName} (${b.currentGrowthStage}, વાવણી પછી ${b.daysAfterSowing} દિવસ)
                |• **મુખ્ય કાર્ય:** ${b.topPriorities.firstOrNull()?.what ?: "જમીનનો ભેજ ચકાસો."}
                """.trimMargin()
            }
        }
    }
}
