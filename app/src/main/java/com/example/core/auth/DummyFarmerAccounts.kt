package com.example.core.auth

import com.example.data.model.AppLanguage
import com.example.data.model.FarmerProfile

/**
 * Pre-configured dummy farmer accounts for quick demo and testing.
 * Features diverse agricultural profiles across India (regions, crops, farm sizes).
 */
data class DummyFarmerAccount(
  val id: String,
  val email: String,
  val password: String = "kisan123",
  val name: String,
  val village: String,
  val state: String,
  val totalLandAcres: Double,
  val farmName: String,
  val primaryCrop: String,
  val secondaryCrops: List<String>,
  val mobileNumber: String,
  val language: AppLanguage,
  val avatarColorHex: Long,
  val badge: String,
  val soilType: String,
  val irrigationType: String
) {
  fun toFarmerProfile(): FarmerProfile = FarmerProfile(
    name = name,
    village = village,
    state = state,
    totalLandAcres = totalLandAcres,
    farmName = farmName,
    primaryCrop = primaryCrop,
    mobileNumber = mobileNumber,
    language = language
  )
}

object DummyFarmerAccounts {
  val accounts = listOf(
    DummyFarmerAccount(
      id = "farmer_rudra",
      email = "rudra.patel@kisan.ai",
      password = "kisan123",
      name = "Rudra Patel",
      village = "Surat",
      state = "Gujarat",
      totalLandAcres = 12.5,
      farmName = "Shivam Organic Farm",
      primaryCrop = "Tomato",
      secondaryCrops = listOf("Cotton", "Groundnut"),
      mobileNumber = "+91 98765 43210",
      language = AppLanguage.GUJARATI,
      avatarColorHex = 0xFF10B981,
      badge = "Tomato & Cash Crops",
      soilType = "Black Clay Loam",
      irrigationType = "Drip & Borewell"
    ),
    DummyFarmerAccount(
      id = "farmer_rameshwar",
      email = "rameshwar.sharma@kisan.ai",
      password = "kisan123",
      name = "Rameshwar Sharma",
      village = "Indore",
      state = "Madhya Pradesh",
      totalLandAcres = 25.0,
      farmName = "Narmada Valley Agro",
      primaryCrop = "Wheat",
      secondaryCrops = listOf("Soybean", "Mustard"),
      mobileNumber = "+91 94250 11223",
      language = AppLanguage.HINDI,
      avatarColorHex = 0xFFF59E0B,
      badge = "Grain & Oilseed Specialist",
      soilType = "Deep Black Soil (Regur)",
      irrigationType = "Canal & Sprinkler"
    ),
    DummyFarmerAccount(
      id = "farmer_ananya",
      email = "ananya.deshmukh@kisan.ai",
      password = "kisan123",
      name = "Ananya Deshmukh",
      village = "Nashik",
      state = "Maharashtra",
      totalLandAcres = 8.0,
      farmName = "Sahyadri Orchards",
      primaryCrop = "Grapes",
      secondaryCrops = listOf("Onion", "Pomegranate"),
      mobileNumber = "+91 98220 55443",
      language = AppLanguage.ENGLISH,
      avatarColorHex = 0xFF8B5CF6,
      badge = "Horticulture & Vineyard",
      soilType = "Red Sandy Loam",
      irrigationType = "Automated Micro-Drip"
    ),
    DummyFarmerAccount(
      id = "farmer_harpreet",
      email = "harpreet.singh@kisan.ai",
      password = "kisan123",
      name = "Harpreet Singh",
      village = "Ludhiana",
      state = "Punjab",
      totalLandAcres = 32.0,
      farmName = "Golden Grain Estate",
      primaryCrop = "Basmati Rice",
      secondaryCrops = listOf("Wheat", "Potato"),
      mobileNumber = "+91 98140 77665",
      language = AppLanguage.ENGLISH,
      avatarColorHex = 0xFF059669,
      badge = "Commercial Paddy & Wheat",
      soilType = "Fertile Alluvial Loam",
      irrigationType = "Submersible Tube-well"
    ),
    DummyFarmerAccount(
      id = "farmer_rajesh",
      email = "rajesh.verma@kisan.ai",
      password = "kisan123",
      name = "Rajesh Verma",
      village = "Varanasi",
      state = "Uttar Pradesh",
      totalLandAcres = 15.0,
      farmName = "Ganga Kinare Farms",
      primaryCrop = "Sugarcane",
      secondaryCrops = listOf("Paddy", "Vegetables"),
      mobileNumber = "+91 94150 99887",
      language = AppLanguage.HINDI,
      avatarColorHex = 0xFF0284C7,
      badge = "Sugarcane & Green Fodder",
      soilType = "Gangetic Alluvium",
      irrigationType = "Flood & Furrow"
    ),
    DummyFarmerAccount(
      id = "farmer_suresh",
      email = "suresh.reddy@kisan.ai",
      password = "kisan123",
      name = "Suresh Reddy",
      village = "Guntur",
      state = "Andhra Pradesh",
      totalLandAcres = 18.0,
      farmName = "Krishna Agro Fields",
      primaryCrop = "Chilli",
      secondaryCrops = listOf("Cotton", "Maize"),
      mobileNumber = "+91 98480 33221",
      language = AppLanguage.ENGLISH,
      avatarColorHex = 0xFFEF4444,
      badge = "Spices & Commercial Crops",
      soilType = "Red Loam with High Phosphorus",
      irrigationType = "Drip & Rain Gun"
    ),
    DummyFarmerAccount(
      id = "farmer_atharv",
      email = "patilatharv104@gmail.com",
      password = "kisan123",
      name = "Atharv Patil",
      village = "Kolhapur",
      state = "Maharashtra",
      totalLandAcres = 20.0,
      farmName = "Panchaganga Agro Farm",
      primaryCrop = "Sugarcane",
      secondaryCrops = listOf("Soybean", "Turmeric"),
      mobileNumber = "+91 98230 45678",
      language = AppLanguage.HINDI,
      avatarColorHex = 0xFF16A34A,
      badge = "Precision Sugarcane & Drip Tech",
      soilType = "Rich River Alluvial Loam",
      irrigationType = "Automated IoT Drip"
    ),
    DummyFarmerAccount(
      id = "farmer_meena",
      email = "meena.devi@kisan.ai",
      password = "kisan123",
      name = "Meena Devi",
      village = "Darbhanga",
      state = "Bihar",
      totalLandAcres = 6.5,
      farmName = "Mithila Natural Agro",
      primaryCrop = "Makhana / Foxnut",
      secondaryCrops = listOf("Paddy", "Maize"),
      mobileNumber = "+91 93040 66778",
      language = AppLanguage.HINDI,
      avatarColorHex = 0xFF0D9488,
      badge = "Aquaculture & Natural Farming",
      soilType = "Wetland Peaty Clay",
      irrigationType = "Pond & Inundation"
    ),
    DummyFarmerAccount(
      id = "farmer_manoj",
      email = "manoj.choudhary@kisan.ai",
      password = "kisan123",
      name = "Manoj Choudhary",
      village = "Sikar",
      state = "Rajasthan",
      totalLandAcres = 14.0,
      farmName = "Marwar Desert Bloom",
      primaryCrop = "Pearl Millet (Bajra)",
      secondaryCrops = listOf("Cluster Bean (Guar)", "Mustard"),
      mobileNumber = "+91 94140 12345",
      language = AppLanguage.HINDI,
      avatarColorHex = 0xFFD97706,
      badge = "Arid Zone Resilience",
      soilType = "Sandy Loam",
      irrigationType = "Solar Powered Drip"
    ),
    DummyFarmerAccount(
      id = "farmer_kavitha",
      email = "kavitha.murugan@kisan.ai",
      password = "kisan123",
      name = "Kavitha Murugan",
      village = "Erode",
      state = "Tamil Nadu",
      totalLandAcres = 11.0,
      farmName = "Cauvery Organic Groves",
      primaryCrop = "Turmeric",
      secondaryCrops = listOf("Coconut", "Banana"),
      mobileNumber = "+91 98420 88990",
      language = AppLanguage.ENGLISH,
      avatarColorHex = 0xFF7C3AED,
      badge = "High Curcumin Spice Grower",
      soilType = "Red Sandy Clay",
      irrigationType = "Micro-Sprinklers"
    )
  )

  fun findByEmail(email: String): DummyFarmerAccount? {
    return accounts.find { it.email.equals(email.trim(), ignoreCase = true) }
  }

  val defaultAccount = accounts[0]
}
