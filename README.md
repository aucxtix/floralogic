# 🌱 KisanAI — Predict Before the Loss

> **An offline-first, multimodal Edge AI farming intelligence platform that detects risk early, explains the cause, simulates decisions, and guides farmers toward precise field-level action.**

**Smart India Hackathon 2026 • Problem Statement 26180**

KisanAI is designed as a **Farm Decision Intelligence System** rather than another agricultural chatbot or standalone disease scanner. It combines crop vision, IoT sensing, weather intelligence, satellite signals and farm history to transform scattered data into **early warnings, explainable insights and actionable field-level decisions**.

---

## 🚜 1. The Problem We Are Solving

Indian farmers often make high-impact decisions using fragmented, delayed or incomplete information.

A field may simultaneously experience:

- subtle crop stress,
- declining soil moisture,
- disease-conducive weather,
- abnormal vegetation patterns,
- increasing pest pressure,
- or unreliable internet connectivity.

### The real problem is not the lack of data.

> **The real problem is the gap between _data_ and _decision_.**

Traditional point solutions often work in isolation:

```text
Leaf Image      → Disease Detection
Soil Sensor     → Moisture Reading
Weather API     → Forecast
Satellite       → Vegetation Signal
Farmer          → Must interpret everything
```

KisanAI connects these signals into one intelligence loop:

```text
Camera + IoT + Weather + Satellite + Crop Stage + Farm History
                              ↓
                     🧠 Multimodal Edge AI
                              ↓
                      Risk & Cause Analysis
                              ↓
                   ⚠️ Early Warning System
                              ↓
                    🔍 Explainable Insights
                              ↓
                    🔮 What-If Simulation
                              ↓
                 🎯 Zone-Level Recommended Action
                              ↓
                       👨‍🌾 Farmer Action
                              ↓
                        🔄 Farm Learning
```

---

# 🌾 2. Our Solution — KisanAI

## **The Intelligent Farmer Command Center**

KisanAI brings multiple agricultural intelligence layers into a single farmer-first system.

### 👁️ 01. Vision Intelligence
Uses crop and leaf images to identify visible disease symptoms, lesions and plant-health abnormalities.

### 📡 02. Sensor Intelligence
Uses local measurements such as soil moisture, soil temperature, ambient temperature, humidity and solar exposure.

### 🌦️ 03. Weather Intelligence
Tracks rainfall, temperature, wind and other localized environmental conditions to identify crop-stress and climate-risk patterns.

### 🛰️ 04. Satellite Intelligence
Uses vegetation and field-level signals such as NDVI, canopy behaviour and moisture-related indicators to identify abnormal crop zones.

### 🧾 05. Farm Memory
Stores crop history, previous scans, sensor trends, interventions and field observations to preserve long-term farm context.

### 🧠 06. Decision Intelligence
Converts complex signals into a small set of understandable actions:

> **Inspect • Irrigate • Wait • Treat • Protect • Re-scan**

That final translation from **technical data → practical action** is the core of the product.

---

# ⚡ 3. Core Innovation — Detection → Prediction → Decision

A conventional agricultural AI system might return:

> **“Disease detected.”**

KisanAI aims to answer:

> **“Disease risk is increasing. Here is why, where it is increasing, what may happen next, and which intervention should be considered.”**

### The KisanAI Intelligence Loop

```text
        🔎 DETECT
           ↓
        🔗 CORRELATE
           ↓
        📈 PREDICT
           ↓
        💡 EXPLAIN
           ↓
        🧪 SIMULATE
           ↓
        🎯 ACT
           ↓
        🔄 LEARN
           ↺
```

KisanAI therefore moves beyond simple classification toward **context-aware farm decision support**.

---

# 🚀 4. Flagship Innovation Layer

## 4.1 🧩 Multimodal Risk Fusion Engine

KisanAI does not rely on a single image, single sensor or single forecast.

It correlates:

- crop images,
- environmental sensors,
- weather conditions,
- satellite observations,
- crop growth stage,
- and historical farm behaviour.

Example:

```text
Leaf symptoms detected
        +
High humidity
        +
Recent rainfall
        +
Susceptible crop stage
        +
Vegetation stress in the same zone
        ↓
Higher contextual disease risk
```

### Why it matters

The same visual symptom can have different causes under different field conditions. By combining signals, KisanAI can reason about the **context around the symptom**, not only the symptom itself.

---

## 4.2 📡 Predictive Farm Risk Radar

Instead of waiting for visible damage, KisanAI maintains a continuously updated multi-risk profile.

```text
                 FARM RISK RADAR

Disease Risk       ████████░░  HIGH
Pest Risk          ███░░░░░░░  LOW
Water Stress       ██████░░░░  MODERATE
Heat Stress        █████████░  HIGH
Flood Risk         ██░░░░░░░░  LOW
Nutrient Stress    █████░░░░░  MODERATE
```

Every important risk is linked to evidence.

### Example — 🔥 Heat-Stress Risk: HIGH

```text
Contributing signals:
• Elevated temperature forecast
• Low soil moisture
• Sensitive crop stage
• Recent reduction in vegetation vitality
```

This converts monitoring into **early warning intelligence**.

---

## 4.3 🗺️ Farm Digital Twin

Each registered plot is represented as a continuously updated digital profile.

```text
                 FARM DIGITAL TWIN

Crop: Cotton
Stage: Flowering
Area: Plot A

Health Score        76/100
Soil Moisture       34%
Disease Risk        18%
Heat Risk            61%
Water Stress         48%

Last Scan            2h ago
Last Sensor Sync     7m ago
```

The purpose is not to create a decorative dashboard.

The digital twin gives the decision engine a **persistent understanding of the farm state over time**.

---

## 4.4 🔮 What-If Farm Simulator

One of the strongest decision-support capabilities is the ability to compare possible actions **before** taking them.

### Example — “Should I irrigate now?”

```text
┌──────────────────── OPTION A ────────────────────┐
│ IRRIGATE NOW                                     │
│                                                  │
│ Expected soil moisture: Higher                  │
│ Rainfall probability: Still significant         │
│ Runoff risk: Increased                           │
│ Water consumption: Higher                        │
└──────────────────────────────────────────────────┘

┌──────────────────── OPTION B ────────────────────┐
│ WAIT 12–18 HOURS                                 │
│                                                  │
│ Expected rainfall: Probable                      │
│ Water requirement: Potentially lower             │
│ Runoff risk: Lower                               │
└──────────────────────────────────────────────────┘
```

The system structures decisions as:

> **Observation → Expected Outcome → Risk → Resource Impact → Recommended Action**

This changes KisanAI from an information dashboard into a **decision simulator**.

---

## 4.5 🎯 Zone-Level Precision Intervention

Instead of telling a farmer:

> “Irrigate the field.”

KisanAI can identify where the abnormality is concentrated.

```text
┌─────────────┬─────────────┐
│   ZONE A    │   ZONE B    │
│   ✅ Healthy │ 💧 Water    │
│             │    Stress   │
├─────────────┼─────────────┤
│   ZONE C    │   ZONE D    │
│ ⚠️ Disease  │   ✅ Healthy │
│    Risk     │             │
└─────────────┴─────────────┘
```

### Result

- 💧 Irrigate **Zone B**
- 🔍 Inspect **Zone C**
- ✅ Leave healthy zones unchanged

This supports targeted intervention instead of blanket action.

---

## 4.6 💬 Explainable Decision Copilot

KisanAI should not behave like a generic chatbot.

Every important recommendation follows a structured reasoning chain:

```text
WHAT HAPPENED?
      ↓
WHY IS IT HAPPENING?
      ↓
HOW SERIOUS IS IT?
      ↓
WHAT ARE MY OPTIONS?
      ↓
WHAT SHOULD I DO NOW?
      ↓
WHAT COULD HAPPEN NEXT?
```

### Example

> **Recommendation:** Delay irrigation.
>
> **Why:** Soil moisture is currently adequate and rainfall probability is elevated.
>
> **Risk if irrigated now:** Possible water wastage and runoff.
>
> **Next step:** Re-check soil moisture after the forecast window.

The objective is **farmer-usable explainability**, not developer-only technical transparency.

---

# 🔌 5. Offline-First Edge AI Architecture

Connectivity cannot be treated as a permanent dependency in a field deployment.

KisanAI follows a **local-first architecture** so essential intelligence can remain available during intermittent connectivity.

```text
                    👨‍🌾 FARMER APP
                          │
                    Decision Layer
                          │
               ┌──────────┴──────────┐
               │  FARM INTELLIGENCE │
               └──────────┬──────────┘
                          │
       ┌──────────────────┼──────────────────┐
       │                  │                  │
     📷 CAMERA           📡 IoT          💾 LOCAL DATA
       │                  │                  │
       └──────────────────┼──────────────────┘
                          ↓
                   🧠 EDGE AI ENGINE
                          │
                    LOCAL DECISIONS
                          │
                          ↓
                   🎯 FARMER ACTION
                          │
                   NETWORK RETURNS
                          ↓
                 ☁️ CLOUD SYNC / ENRICHMENT
```

### 🔵 Live Mode

- remote AI services,
- dynamic weather information,
- satellite updates,
- market intelligence,
- cloud synchronization.

### 🟢 Edge / Offline Mode

- local sensor data,
- cached farm state,
- locally available inference,
- offline recommendations,
- historical farm context.

> **Internet should improve KisanAI — not determine whether KisanAI works.**

---

# 👨‍🌾 6. Farmer-Centric Experience

The design principle is simple:

> **Advanced intelligence underneath. Simple decisions above.**

A farmer should not need to understand every technical metric to use the system.

Instead of:

```text
NDVI = 0.73
```

show:

```text
🌿 Vegetation Health: EXCELLENT
```

Advanced parameters remain available for deeper inspection.

### 🌐 Language-first design

The architecture supports:

- 🇬🇧 English
- 🇮🇳 Hindi
- ગુજરાતીમાં Gujarati

### Field-ready UX

- large touch targets,
- readable status states,
- simple action labels,
- semantic accessibility support,
- offline-friendly operation.

---

# 🧠 7. Complete Intelligence Stack

| Layer | Capability | Outcome |
|---|---|---|
| 👁️ Vision AI | Disease / symptom analysis | Early crop-health detection |
| 🐛 Pest Intelligence | Pest activity recognition | Faster targeted response |
| 🌱 Nutrient Intelligence | Stress-pattern analysis | Earlier deficiency awareness |
| 📡 IoT | Moisture / temperature / humidity | Field-condition monitoring |
| 🌦️ Weather AI | Risk forecasting | Climate-aware action |
| 🛰️ Satellite AI | Vegetation / moisture signals | Large-area field intelligence |
| ⚠️ Risk Radar | Multi-risk scoring | Prioritized action |
| 🗺️ Digital Twin | Plot state over time | Persistent farm context |
| 🔮 What-If Simulator | Action comparison | Better decisions before intervention |
| 🎯 Precision Zones | Targeted recommendations | Reduced blanket intervention |
| 💬 Copilot | Explainable guidance | Farmer-friendly decisions |
| 🧾 Farm History | Previous scans / events | Contextual intelligence |
| 💰 Market Intelligence | Mandi trends | Better harvest decisions |
| 🔌 Edge Mode | Offline intelligence | Connectivity resilience |
| 🔄 Feedback Loop | Farmer corrections | Continuous improvement |

---

# 🔄 8. End-to-End Intelligence Workflow

```text
1. SENSE
   Camera + Sensors + Weather + Satellite

                 ↓

2. UNDERSTAND
   Detect crop state, abnormalities and environmental signals

                 ↓

3. FUSE
   Correlate multiple signals with crop stage and history

                 ↓

4. SCORE
   Calculate risk, confidence and severity

                 ↓

5. EXPLAIN
   Show the evidence behind the risk

                 ↓

6. SIMULATE
   Compare possible farmer actions

                 ↓

7. RECOMMEND
   Generate precise, understandable intervention

                 ↓

8. ACT
   Farmer irrigates / inspects / treats / protects

                 ↓

9. LEARN
   Store result and farmer feedback

                 ↺
```

### This closed loop is the heart of KisanAI.

It connects **field signals → intelligence → decision → intervention → learning**.

---

# 🏗️ 9. Technical Architecture

KisanAI follows a modular architecture using Android Native, Kotlin, Jetpack Compose, MVVM, coroutines/flows and an offline-capable local repository.

```text
┌────────────────────────────────────────────────┐
│              JETPACK COMPOSE UI               │
│ Dashboard • Scanner • Farm • Market • Alerts  │
└──────────────────────────┬─────────────────────┘
                           ↓
                    STATEFLOW / MVVM
                           ↓
┌────────────────────────────────────────────────┐
│                KISAN REPOSITORY                │
└───────┬──────────────┬──────────────┬─────────┘
        ↓              ↓              ↓
     ROOM DB         SENSORS       EDGE / CLOUD AI
        ↓              ↓              ↓
   Local State      ESP32/MQTT      AI Inference
        └──────────────┴──────────────┘
                       ↓
                🧠 Decision Engine
                       ↓
              👨‍🌾 Farmer-Friendly UX
```

### Core technology direction

- **Android Native / Kotlin**
- **Jetpack Compose**
- **MVVM + StateFlow**
- **Room / SQLite** for local persistence
- **CameraX** for image capture
- **ESP32 / MQTT** for field sensing
- **Edge + Cloud AI** for hybrid inference

---

# 📱 10. Core Product Modules

### 🏠 Command Center
Unified overview of registered plots, crop stages, health, moisture, alerts and priority actions.

### 📷 Crop Disease Scanner
Image capture, diagnostic processing, confidence-aware results, symptoms, treatment guidance and farmer feedback.

### 🌱 My Farm
Crop registration, planting dates, acreage, growth stages, yield intelligence and plot-level history.

### 🌦️ Weather & Irrigation Planner
Localized weather-risk tracking and irrigation recommendations based on moisture, crop requirements and precipitation outlook.

### 🛰️ Satellite Intelligence
NDVI-style vegetation health, canopy behaviour and moisture-related field signals.

### 📡 Smart Farm IoT
Live telemetry, sensor history, device health, battery state, signal quality and last-active timestamps.

### 💰 Market Intelligence
Regional mandi rates, market history and trend-oriented decision support.

### 🧾 Farm History
Persistent records of scans, interventions, sensor events and farm activity.

### 🔍 Model Transparency
Clear presentation of model behaviour, confidence and relevant parameters without forcing technical complexity onto the farmer.

---

# 🛡️ 11. Trust, Safety & Explainability

KisanAI is designed around **decision support**, not blind automation.

### Human-in-the-loop

The system recommends; the farmer remains the decision-maker.

### Confidence-aware output

Low-confidence visual diagnosis can be marked as inconclusive rather than presenting uncertain output as fact.

### Evidence-backed recommendations

Important recommendations should display their primary contributing signals.

### Graceful degradation

Weak connectivity should reduce enrichment, not eliminate essential local functionality.

---

# 🌍 12. Why This Architecture Matters

The problem statement calls for field-deployable intelligence that can support disease, pest, nutrient, irrigation and environmental-risk decisions while remaining useful in areas with limited connectivity.

KisanAI addresses this through one connected architecture:

```text
         DATA
          ↓
      INTELLIGENCE
          ↓
     EARLY WARNING
          ↓
      EXPLANATION
          ↓
    DECISION SIMULATION
          ↓
    PRECISION ACTION
          ↓
      FARM LEARNING
```

The result is not simply a collection of features.

> **It is a continuous farm decision system.**

---

# 📈 13. Expected Impact Areas

KisanAI is designed to support measurable improvement across multiple dimensions:

### 💧 Resource Efficiency
More informed irrigation decisions can help reduce unnecessary water use.

### 🌱 Input Precision
Zone-specific guidance can reduce unnecessary blanket treatment.

### 🛑 Earlier Response
Predictive risk signals can help farmers investigate threats before they spread.

### 🌦️ Climate Resilience
Localized risk intelligence can improve preparedness for heat, drought, rainfall and flooding conditions.

### 📶 Connectivity Resilience
Edge-first processing keeps core intelligence available even when connectivity is intermittent.

### 🧑‍🌾 Decision Accessibility
Complex agricultural signals are converted into understandable actions for everyday users.

---

# 🧪 14. Example Decision Scenario

### Scenario: Tomato Field — Increasing Disease Risk

```text
Observed:
• Elevated humidity
• Recent rainfall
• Suitable crop stage
• Local vegetation stress
• Leaf-image abnormality

                ↓

KisanAI detects a rising disease risk

                ↓

WHY?
Multiple independent signals support the risk

                ↓

WHERE?
Risk concentrated in Zone C

                ↓

WHAT IF?
Continued overhead irrigation increases moisture exposure

                ↓

ACTION
Inspect Zone C + consider targeted treatment + re-scan
```

This demonstrates the central philosophy:

> **Detect early. Understand the cause. Compare the options. Act precisely.**

---

# 🧭 15. Product Philosophy

KisanAI is built on five principles:

```text
① EARLY     → Detect risk before loss becomes obvious
② CONTEXT   → Never depend on one signal alone
③ EXPLAIN   → Show why a recommendation exists
④ PRECISION → Target the affected area, not the whole farm
⑤ RESILIENCE→ Keep core intelligence useful offline
```

Together, these principles define the product's identity.

---

# 🏆 16. The One-Line Pitch

> ### **KisanAI doesn't just detect farm problems — it predicts risk, explains the cause, simulates the choices, and guides the farmer to the right action.**

---

# 🔥 17. The Core Differentiator

Most agricultural systems stop at:

```text
DATA → DETECTION
```

KisanAI is designed around:

```text
DATA
  ↓
FUSION
  ↓
RISK
  ↓
CAUSE
  ↓
PREDICTION
  ↓
WHAT-IF
  ↓
ACTION
  ↓
LEARNING
```

### That is the central idea behind KisanAI:

> **From a passive farming dashboard to a proactive Farm Decision Intelligence System.**

---

# 📚 18. Reference Context

KisanAI is aligned with the Smart India Hackathon 2026 problem statement **26180**, which calls for a field-deployable AI-powered Smart Farming Assistant supporting crop-health monitoring, pest detection, smart irrigation, environmental-risk monitoring, Edge AI, farmer advisory and farm analytics.

The architecture and feature direction in this repository are based on the submitted KisanAI technical blueprint and the SIH idea-submission structure.

---

<div align="center">

### 🌱 **KisanAI**
**See the Risk. Understand the Cause. Act Before the Loss.**

`Edge AI` • `Multimodal Fusion` • `Predictive Risk` • `Explainable Decisions` • `Precision Action`

</div>
