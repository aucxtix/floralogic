package com.example.ai

/**
 * Agronomic knowledge entity stored independently of ML models.
 */
data class DiseaseAgronomicKnowledge(
    val diseaseId: String,
    val diseaseName: String,
    val scientificName: String,
    val cropName: String,
    val category: String, // "Fungal", "Bacterial", "Viral", "Nutrient Deficiency", "Healthy"
    val symptoms: List<String>,
    val commonCauses: List<String>,
    val favorableConditions: String, // e.g. "Relative humidity > 85%, temperature 18-24°C, prolonged leaf wetness"
    val warningSigns: List<String>,
    val expertEscalationCriteria: String,
    val preventionPractices: List<String>,
    val organicRemedies: String,
    val chemicalManagementGuideline: String,
    val dosageFormula: String,
    val estimatedCostPerAcre: String,
    val safetyDisclaimer: String = "Follow registered CIBRC label directions. Avoid spraying in high winds or right before rain. Wear protective gloves and face mask."
)

/**
 * DiseaseKnowledgeBase
 *
 * Grounded agricultural repository providing domain explanations for detected crop pathologies.
 */
object DiseaseKnowledgeBase {

    private val catalog: Map<String, DiseaseAgronomicKnowledge> = mapOf(
        "tomato_late_blight" to DiseaseAgronomicKnowledge(
            diseaseId = "tomato_late_blight",
            diseaseName = "Late Blight (पछेती झुलसा)",
            scientificName = "Phytophthora infestans",
            cropName = "Tomato",
            category = "Fungal-like Oomycete",
            symptoms = listOf(
                "Water-soaked irregular dark brown lesions starting from leaf edges",
                "Delicate white fuzzy mildew growth on leaf undersides in humid early mornings",
                "Dark brown sunken firm lesions on green tomato fruits and main stems"
            ),
            commonCauses = listOf(
                "Airborne sporangia carried from infected potato/tomato volunteer plants",
                "Prolonged leaf wetness exceeding 8 hours without rapid canopy drying",
                "Cool wet night temperatures combined with high morning relative humidity"
            ),
            favorableConditions = "High humidity (>85%), temperatures between 16°C and 22°C, and overcast cloudy conditions with intermittent rain.",
            warningSigns = listOf(
                "White sporulation on the underside of green leaves",
                "Stem girdling lesions causing upper canopy wilting",
                "Rapid spread across adjacent rows within 48-72 hours"
            ),
            expertEscalationCriteria = "If more than 15% of the canopy shows stem necrosis, or if lesions appear directly on green fruit clusters, consult the local KVK / Agriculture Officer immediately.",
            preventionPractices = listOf(
                "Avoid overhead sprinkler irrigation; use root-zone drip lines",
                "Maintain 60cm x 45cm plant spacing for adequate air ventilation",
                "Apply clean plastic or paddy straw mulch to prevent soil spore splash",
                "Rogue out and deeply bury heavily infected volunteer plants"
            ),
            organicRemedies = "Foliar spray with Trichoderma viride (5g/L) + Neem Seed Kernel Extract (NSKE 5%). Apply bio-formulations early morning.",
            chemicalManagementGuideline = "Preventive: Mancozeb 75% WP. Curative on early detection: Metalaxyl 8% + Mancozeb 64% WP (Ridomil MZ) or Dimethomorph 50% WP.",
            dosageFormula = "2.0 – 2.5 grams per Liter of clean water (approx. 450-500g in 200L water per acre).",
            estimatedCostPerAcre = "₹220 – ₹280 / acre"
        ),

        "tomato_early_blight" to DiseaseAgronomicKnowledge(
            diseaseId = "tomato_early_blight",
            diseaseName = "Early Blight (अगेती झुलसा)",
            scientificName = "Alternaria solani",
            cropName = "Tomato",
            category = "Fungal",
            symptoms = listOf(
                "Target-board concentric dark brown rings on older lower foliage",
                "Yellow chlorotic halos surrounding lesions",
                "Premature lower leaf defoliation exposing green fruits to sunscald"
            ),
            commonCauses = listOf(
                "Spore survival in solanaceous crop debris and contaminated seed",
                "Warm humid weather with alternating dry and wet periods",
                "Stressed plants deficient in nitrogen and potassium"
            ),
            favorableConditions = "Temperatures of 24°C to 29°C with heavy morning dews and warm afternoons.",
            warningSigns = listOf(
                "Concentric rings advancing above the mid-canopy",
                "Dark sunken stem cankers at the soil line (collar rot in young transplants)"
            ),
            expertEscalationCriteria = "If lesions spread into the upper third of the canopy during peak flowering or fruit set.",
            preventionPractices = listOf(
                "Prune the lowest 30cm (1 foot) of leaves once plants reach flowering",
                "Adopt 2-3 year crop rotation with non-solanaceous crops (maize, pulses)",
                "Ensure balanced NPK fertilization; avoid nitrogen starvation"
            ),
            organicRemedies = "Foliar spray of Pseudomonas fluorescens (5g/L) or 1% Bordeaux mixture.",
            chemicalManagementGuideline = "Spray Chlorothalonil 75% WP or Azoxystrobin 23% SC. Alternate modes of action to prevent resistance.",
            dosageFormula = "1.5 ml Azoxystrobin or 2.0g Chlorothalonil per Liter of water.",
            estimatedCostPerAcre = "₹210 – ₹290 / acre"
        ),

        "rice_bacterial_blight" to DiseaseAgronomicKnowledge(
            diseaseId = "rice_bacterial_blight",
            diseaseName = "Bacterial Leaf Blight (BLB / जीवाणु झुलसा)",
            scientificName = "Xanthomonas oryzae pv. oryzae",
            cropName = "Rice / Paddy",
            category = "Bacterial",
            symptoms = listOf(
                "Water-soaked yellowish-green translucent streaks starting from leaf tips along margins",
                "Wavy lesion margins progressing down the leaf blade turning grayish-white",
                "Amber-colored bacterial beads or ooze droplets visible on fresh morning lesions"
            ),
            commonCauses = listOf(
                "Excessive or late top-dressing of synthetic nitrogen (urea)",
                "Paddy fields with stagnant standing water and poor drainage",
                "Strong winds and rains creating mechanical wounds on leaves"
            ),
            favorableConditions = "Warm humid tropical climate (25-34°C, >70% RH), typhoon winds, and standing deep flood water.",
            warningSigns = listOf(
                "Kresek / seedling wilt phase in young transplanted fields",
                "Chaffy panicles with unfilled discolored grains"
            ),
            expertEscalationCriteria = "If more than 20% of tillers show marginal leaf necrosis before the booting stage.",
            preventionPractices = listOf(
                "Temporarily stop all top-dressing of urea; apply potash (MOP) to harden cell walls",
                "Drain standing water from paddy fields for 3-4 days to arrest bacterial multiplication",
                "Select certified BLB-resistant seed varieties (PR 126, Improved Pusa Basmati 1)"
            ),
            organicRemedies = "Spray supernatant of 20% fresh cow dung slurry or 5% Neem oil.",
            chemicalManagementGuideline = "Spray Streptocycline (antibiotic) combined with Copper Oxychloride 50% WP.",
            dosageFormula = "15g Streptocycline + 500g Copper Oxychloride in 200 Liters water per acre.",
            estimatedCostPerAcre = "₹190 – ₹260 / acre"
        ),

        "cotton_leaf_curl" to DiseaseAgronomicKnowledge(
            diseaseId = "cotton_leaf_curl",
            diseaseName = "Cotton Leaf Curl Virus (CLCuV / पत्ता मरोड़)",
            scientificName = "Cotton leaf curl virus (Begomovirus)",
            cropName = "Cotton",
            category = "Viral",
            symptoms = listOf(
                "Upward and downward curling of young leaf margins",
                "Thickening and swelling of main and secondary leaf veins on the underside",
                "Cup-shaped leaf-like enations (foliar outgrowths) on leaf veins",
                "Severe stunting and shedding of flower squares and young bolls"
            ),
            commonCauses = listOf(
                "Vector transmission by Whitefly (Bemisia tabaci)",
                "Proximity to weed reservoirs like Congress grass (Parthenium)",
                "Hot dry spells punctuated by humid weather favoring whitefly explosions"
            ),
            favorableConditions = "Warm dry conditions (30-38°C) that stimulate rapid whitefly reproduction.",
            warningSigns = listOf(
                "Whitefly nymph count exceeding 6-8 per leaf",
                "New flush of leaves emerging cupped and leathery"
            ),
            expertEscalationCriteria = "If average whitefly population exceeds economic threshold level (ETL) of 6 adults/leaf across >20% sampled plants.",
            preventionPractices = listOf(
                "Install yellow sticky traps (10-12 traps/acre) at canopy height",
                "Maintain clean field borders by weeding alternate host plants",
                "Avoid spraying non-selective pyrethroids that decimate beneficial predators"
            ),
            organicRemedies = "Spray 5% Neem seed kernel extract (NSKE) or Neem oil (10,000 ppm) @ 2ml/L to deter whitefly oviposition.",
            chemicalManagementGuideline = "Target vector whiteflies with Diafenthiuron 50% WP or Pyriproxyfen 10% EC. No direct chemical cure for viral pathogen.",
            dosageFormula = "Diafenthiuron 1.0 – 1.25g per Liter of water.",
            estimatedCostPerAcre = "₹310 – ₹380 / acre"
        ),

        "wheat_leaf_rust" to DiseaseAgronomicKnowledge(
            diseaseId = "wheat_leaf_rust",
            diseaseName = "Brown / Leaf Rust (गेरुआ / तांबिया)",
            scientificName = "Puccinia triticina",
            cropName = "Wheat",
            category = "Fungal",
            symptoms = listOf(
                "Scattered round to oval orange-brown powdery pustules on upper leaf surface",
                "Pustules rub off on fingers as bright rusty powder",
                "Leaves turning prematurely yellow, drying up and lowering grain test weight"
            ),
            commonCauses = listOf(
                "Airborne urediniospores blown across regions",
                "Moderate winter temperatures with morning dew and dense crop canopy"
            ),
            favorableConditions = "Temperatures 15°C to 25°C with at least 6-8 hours of free moisture/dew on leaves.",
            warningSigns = listOf(
                "Rust pustules spreading from lower canopy to the flag leaf",
                "Rapid coalescing of pustules covering >30% of flag leaf area"
            ),
            expertEscalationCriteria = "If rust appears on the flag leaf before or during milk stage.",
            preventionPractices = listOf(
                "Plant rust-resistant certified wheat varieties (HD-2967, PBW-550, DBW-187)",
                "Adhere to recommended sowing windows to avoid late-season temperature spikes",
                "Avoid excessive dense seeding that restricts internal canopy air movement"
            ),
            organicRemedies = "Foliar spray with Cow urine (10%) fermented with neem leaves.",
            chemicalManagementGuideline = "Spray Propiconazole 25% EC (Tilt) or Tebuconazole 25.9% EC at the first appearance of pustules.",
            dosageFormula = "1.0 ml Propiconazole per Liter of water (200 ml in 200L water per acre).",
            estimatedCostPerAcre = "₹240 – ₹320 / acre"
        ),

        "healthy_foliage" to DiseaseAgronomicKnowledge(
            diseaseId = "healthy_foliage",
            diseaseName = "Healthy Foliage (स्वस्थ फसल)",
            scientificName = "Normal Healthy Chlorophyll Tissue",
            cropName = "Crop Specimen",
            category = "Healthy",
            symptoms = listOf(
                "Vibrant uniform green coloration without necrotic spots",
                "Smooth, uncurled leaf margins with intact vascular veins",
                "Clean leaf undersides free of fungal mycelium or insect colonies"
            ),
            commonCauses = listOf("Optimal soil moisture, balanced nutrition, and good agronomic management."),
            favorableConditions = "Balanced agro-climatic conditions matching crop variety requirements.",
            warningSigns = emptyList(),
            expertEscalationCriteria = "No escalation required. Maintain current preventive schedule.",
            preventionPractices = listOf(
                "Continue balanced NPK irrigation scheduling",
                "Perform weekly routine scouting for early pest appearance",
                "Maintain clean weed-free bunds around the field"
            ),
            organicRemedies = "Preventive spray of Jeevamrit or diluted seaweed extract every 15 days.",
            chemicalManagementGuideline = "No chemical pesticides required. Protect beneficial natural predators.",
            dosageFormula = "None required.",
            estimatedCostPerAcre = "₹0"
        )
    )

    fun getKnowledge(diseaseId: String): DiseaseAgronomicKnowledge {
        val normalized = diseaseId.lowercase().trim()
        return catalog.entries.firstOrNull { normalized.contains(it.key) }?.value
            ?: catalog["tomato_late_blight"]!!
    }

    fun getAllSupportedDiseases(): List<DiseaseAgronomicKnowledge> = catalog.values.toList()
}
