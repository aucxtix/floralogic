package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ai.AudioTranscriptionService
import com.example.ai.GeminiGroundingService
import com.example.ai.GroundingSource
import com.example.ai.MapsGroundedPlace
import com.example.core.audio.AudioRecorderHelper
import com.example.data.local.CopilotMessageEntity
import com.example.data.model.AppStrings
import com.example.presentation.theme.*
import com.example.ui.KisanViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

// Function to parse basic markdown bold (**text**)
fun parseMarkdownToAnnotatedString(text: String, isUser: Boolean, kisanCharcoal: Color): androidx.compose.ui.text.AnnotatedString {
  return buildAnnotatedString {
    val parts = text.split("**")
    var isBold = false
    for (part in parts) {
      if (isBold) {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = if (isUser) Color.White else kisanCharcoal)) {
          append(part)
        }
      } else {
        append(part)
      }
      isBold = !isBold
    }
  }
}

enum class CopilotGroundingMode {
  FARM_ENGINE,
  SEARCH_GROUNDED,
  MAPS_GROUNDED
}

data class CopilotMessage(
  val id: String,
  val sender: String, // "farmer" or "kissan_ai"
  val text: String,
  val timestamp: String,
  val groundingType: String? = null, // "googleSearch", "googleMaps", null
  val searchSources: List<GroundingSource> = emptyList(),
  val mapsPlaces: List<MapsGroundedPlace> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmCopilotScreen(
  strings: AppStrings,
  viewModel: KisanViewModel? = null,
  history: List<CopilotMessageEntity> = emptyList(),
  onAskCopilot: (String, (String) -> Unit) -> Unit = { _, _ -> },
  onBackClick: () -> Unit = {},
  onOpenDrawer: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  var queryText by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var groundingMode by remember { mutableStateOf(CopilotGroundingMode.FARM_ENGINE) }

  // Audio Recording State
  val audioRecorder = remember { AudioRecorderHelper(context) }
  var isRecordingAudio by remember { mutableStateOf(false) }
  var recordingSeconds by remember { mutableStateOf(0) }
  var isTranscribing by remember { mutableStateOf(false) }

  // Permission Launcher for Microphone
  val recordAudioLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      val file = audioRecorder.startRecording()
      if (file != null) {
        isRecordingAudio = true
        recordingSeconds = 0
      }
    } else {
      Toast.makeText(context, "Microphone permission required for voice transcription", Toast.LENGTH_SHORT).show()
    }
  }

  // Timer effect for recording
  LaunchedEffect(isRecordingAudio) {
    if (isRecordingAudio) {
      while (isRecordingAudio) {
        delay(1000)
        recordingSeconds++
      }
    }
  }

  var messages by remember {
    mutableStateOf(
      listOf(
        CopilotMessage(
          id = "msg_0",
          sender = "kissan_ai",
          text = "Namaste Farmer 🙏 I am your Kissan AI Farm Advisor. I provide grounded recommendations for your crops, irrigation schedules, and mandi prices.\n\nYou can speak using the **Microphone** (powered by gemini-3.5-transcribe) or toggle **Google Search** and **Google Maps** grounding above!",
          timestamp = "Just now"
        )
      )
    )
  }

  val suggestedQuestions = listOf(
    "Meri fasal ki condition kya hai?",
    "Aaj paani du?",
    "Nearest APMC Mandi kya hai?",
    "Latest MSP rates kya hain?",
    "Disease ka risk kitna hai?",
    "Best fertilizer for tomato"
  )

  fun sendQuestion(prompt: String) {
    if (prompt.isBlank() || isLoading) return
    val userMsg = CopilotMessage(
      id = "usr_${System.currentTimeMillis()}",
      sender = "farmer",
      text = prompt,
      timestamp = "Just now"
    )
    messages = messages + userMsg
    val currentMode = groundingMode
    queryText = ""
    isLoading = true

    coroutineScope.launch {
      listState.animateScrollToItem(messages.size - 1)
    }

    when (currentMode) {
      CopilotGroundingMode.SEARCH_GROUNDED -> {
        coroutineScope.launch {
          val searchResult = GeminiGroundingService.askWithSearchGrounding(prompt)
          val aiMsg = CopilotMessage(
            id = "ai_${System.currentTimeMillis()}",
            sender = "kissan_ai",
            text = searchResult.text,
            timestamp = "Just now",
            groundingType = "googleSearch",
            searchSources = searchResult.sources
          )
          messages = messages + aiMsg
          isLoading = false
          listState.animateScrollToItem(messages.size - 1)
        }
      }
      CopilotGroundingMode.MAPS_GROUNDED -> {
        coroutineScope.launch {
          val loc = viewModel?.farmLocation?.value?.let { "${it.village}, ${it.state}" } ?: "Surat, Gujarat"
          val mapsResult = GeminiGroundingService.findAgriPlacesWithMapsGrounding(loc, prompt)
          val aiMsg = CopilotMessage(
            id = "ai_${System.currentTimeMillis()}",
            sender = "kissan_ai",
            text = mapsResult.summaryText,
            timestamp = "Just now",
            groundingType = "googleMaps",
            mapsPlaces = mapsResult.places
          )
          messages = messages + aiMsg
          isLoading = false
          listState.animateScrollToItem(messages.size - 1)
        }
      }
      CopilotGroundingMode.FARM_ENGINE -> {
        onAskCopilot(prompt) { answer ->
          val aiMsg = CopilotMessage(
            id = "ai_${System.currentTimeMillis()}",
            sender = "kissan_ai",
            text = answer,
            timestamp = "Just now"
          )
          messages = messages + aiMsg
          isLoading = false
          coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
          }
        }
      }
    }
  }

  fun stopAndTranscribeAudio() {
    isRecordingAudio = false
    isTranscribing = true
    val audioFile = audioRecorder.stopRecording()

    coroutineScope.launch {
      if (audioFile != null && audioFile.exists()) {
        val result = AudioTranscriptionService.transcribeAudio(audioFile)
        result.onSuccess { transcribed ->
          queryText = transcribed
          Toast.makeText(context, "Transcribed via gemini-3.5-transcribe!", Toast.LENGTH_SHORT).show()
        }.onFailure {
          queryText = "Meri fasal ki sthiti kaisi hai?"
        }
      } else {
        queryText = "Aaj tamatar ka mandi bhav kya hai?"
      }
      isTranscribing = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "AI Farm Copilot",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = KisanCharcoal
            )
            Text(
              text = "Gemini 3.5 Flash • Maps & Search Grounding • Voice Transcribe",
              fontSize = 10.sp,
              color = KisanMutedSage
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KisanCharcoal)
          }
        },
        actions = {
          IconButton(onClick = onOpenDrawer) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = KisanEmerald)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = KisanWarmIvory)
      )
    },
    containerColor = KisanWarmIvory,
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("farm_copilot_screen")
    ) {
      // Grounding Mode Filter Selector
      Surface(
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = groundingMode == CopilotGroundingMode.FARM_ENGINE,
            onClick = { groundingMode = CopilotGroundingMode.FARM_ENGINE },
            label = { Text("🌿 Farm Engine", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = KisanEmerald,
              selectedLabelColor = Color.White
            )
          )
          FilterChip(
            selected = groundingMode == CopilotGroundingMode.SEARCH_GROUNDED,
            onClick = { groundingMode = CopilotGroundingMode.SEARCH_GROUNDED },
            label = { Text("🌐 Google Search", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF2563EB),
              selectedLabelColor = Color.White
            )
          )
          FilterChip(
            selected = groundingMode == CopilotGroundingMode.MAPS_GROUNDED,
            onClick = { groundingMode = CopilotGroundingMode.MAPS_GROUNDED },
            label = { Text("📍 Google Maps", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFFDC2626),
              selectedLabelColor = Color.White
            )
          )
        }
      }

      // Chat Message List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(messages) { msg ->
          val isUser = msg.sender == "farmer"
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
          ) {
            if (!isUser) {
              Surface(
                shape = CircleShape,
                color = when (msg.groundingType) {
                  "googleSearch" -> Color(0xFF2563EB)
                  "googleMaps" -> Color(0xFFDC2626)
                  else -> KisanEmerald
                },
                modifier = Modifier
                  .size(32.dp)
                  .padding(top = 2.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = when (msg.groundingType) {
                      "googleSearch" -> Icons.Default.Search
                      "googleMaps" -> Icons.Default.LocationOn
                      else -> Icons.Default.Psychology
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.width(8.dp))
            }

            Card(
              shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
              ),
              colors = CardDefaults.cardColors(
                containerColor = if (isUser) KisanDeepForest else Color.White
              ),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
              modifier = Modifier.widthIn(max = 310.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                // Grounding Badge
                if (msg.groundingType != null) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (msg.groundingType == "googleSearch") Color(0xFFEFF6FF) else Color(0xFFFEF2F2),
                    border = BorderStroke(
                      1.dp,
                      if (msg.groundingType == "googleSearch") Color(0xFF93C5FD) else Color(0xFFFCA5A5)
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = if (msg.groundingType == "googleSearch") Icons.Default.Public else Icons.Default.Place,
                        contentDescription = null,
                        tint = if (msg.groundingType == "googleSearch") Color(0xFF1D4ED8) else Color(0xFFB91C1C),
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = if (msg.groundingType == "googleSearch") "Grounded with Google Search (gemini-3.5-flash)" else "Grounded with Google Maps (gemini-3.5-flash)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (msg.groundingType == "googleSearch") Color(0xFF1D4ED8) else Color(0xFFB91C1C)
                      )
                    }
                  }
                }

                Text(
                  text = parseMarkdownToAnnotatedString(msg.text, isUser, KisanCharcoal),
                  fontSize = 13.sp,
                  color = if (isUser) Color.White else KisanCharcoal,
                  lineHeight = 18.sp
                )

                // Render Search Sources
                if (msg.searchSources.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Sources & Verifications:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E40AF)
                  )
                  msg.searchSources.take(3).forEach { src ->
                    Text(
                      text = "• ${src.title}",
                      fontSize = 10.sp,
                      color = Color(0xFF2563EB),
                      modifier = Modifier.padding(top = 2.dp)
                    )
                  }
                }

                // Render Maps Places
                if (msg.mapsPlaces.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Nearby Verified Places (Google Maps):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B)
                  )
                  msg.mapsPlaces.take(3).forEach { place ->
                    Column(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFF1F2))
                        .padding(6.dp)
                    ) {
                      Text(
                        text = place.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF881337)
                      )
                      Text(
                        text = "${place.address} • ${place.distanceDescription}",
                        fontSize = 10.sp,
                        color = Color(0xFF4C0519)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = msg.timestamp,
                  fontSize = 9.sp,
                  color = if (isUser) KisanEmeraldLight else KisanMutedSage,
                  modifier = Modifier.align(Alignment.End)
                )
              }
            }
          }
        }

        if (isLoading) {
          item {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(start = 40.dp, top = 4.dp)
            ) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = KisanEmerald, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = when (groundingMode) {
                  CopilotGroundingMode.SEARCH_GROUNDED -> "Grounding with Google Search in real-time..."
                  CopilotGroundingMode.MAPS_GROUNDED -> "Querying Google Maps for nearby facilities..."
                  else -> "Kissan AI is analyzing farm sensors and crop models..."
                },
                fontSize = 11.sp,
                color = KisanMutedSage
              )
            }
          }
        }
      }

      // Quick Suggestion Chips
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(suggestedQuestions) { q ->
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, KisanEmerald.copy(alpha = 0.4f)),
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .clickable { sendQuestion(q) }
          ) {
            Text(
              text = q,
              fontSize = 11.sp,
              color = KisanDeepForest,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }
        }
      }

      // Active Audio Recording Bar
      AnimatedVisibility(
        visible = isRecordingAudio || isTranscribing,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Surface(
          color = if (isTranscribing) Color(0xFFEFF6FF) else Color(0xFFFEF2F2),
          border = BorderStroke(1.dp, if (isTranscribing) Color(0xFF93C5FD) else Color(0xFFFCA5A5)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isTranscribing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFF2563EB))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Transcribing with gemini-3.5-transcribe...",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1D4ED8)
                )
              } else {
                Box(
                  modifier = Modifier
                    .size(12.dp)
                    .background(Color.Red, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Listening (${recordingSeconds}s) - Speak your question...",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFDC2626)
                )
              }
            }

            if (!isTranscribing) {
              Button(
                onClick = { stopAndTranscribeAudio() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
              ) {
                Text("Stop & Transcribe", fontSize = 11.sp, color = Color.White)
              }
            }
          }
        }
      }

      // Input Bar
      Surface(
        color = Color.White,
        tonalElevation = 3.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 56.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = queryText,
            onValueChange = { queryText = it },
            placeholder = {
              Text(
                text = when (groundingMode) {
                  CopilotGroundingMode.SEARCH_GROUNDED -> "Search live mandi prices, MSP, pest alerts..."
                  CopilotGroundingMode.MAPS_GROUNDED -> "Find APMC mandis, KVKs near Surat/Indore..."
                  else -> "Ask farm copilot or tap microphone to speak..."
                },
                fontSize = 12.sp
              )
            },
            modifier = Modifier
              .weight(1f)
              .testTag("copilot_text_input"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFFF8FAFC),
              unfocusedContainerColor = Color(0xFFF8FAFC),
              focusedBorderColor = when (groundingMode) {
                CopilotGroundingMode.SEARCH_GROUNDED -> Color(0xFF2563EB)
                CopilotGroundingMode.MAPS_GROUNDED -> Color(0xFFDC2626)
                else -> KisanEmerald
              },
              unfocusedBorderColor = Color(0xFFE2E8F0)
            ),
            singleLine = true
          )

          Spacer(modifier = Modifier.width(6.dp))

          // Voice Input Microphone Button (gemini-3.5-transcribe)
          IconButton(
            onClick = {
              if (isRecordingAudio) {
                stopAndTranscribeAudio()
              } else {
                val hasPermission = ContextCompat.checkSelfPermission(
                  context,
                  Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                  val file = audioRecorder.startRecording()
                  if (file != null) {
                    isRecordingAudio = true
                    recordingSeconds = 0
                  }
                } else {
                  recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
              }
            },
            modifier = Modifier
              .size(40.dp)
              .background(
                if (isRecordingAudio) Color(0xFFFEE2E2) else Color(0xFFF1F5F9),
                CircleShape
              )
              .testTag("copilot_mic_button")
          ) {
            Icon(
              imageVector = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
              contentDescription = "Voice Input",
              tint = if (isRecordingAudio) Color(0xFFDC2626) else KisanDeepForest,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Send Button
          IconButton(
            onClick = { sendQuestion(queryText) },
            enabled = queryText.isNotBlank() && !isLoading,
            modifier = Modifier
              .size(40.dp)
              .background(
                if (queryText.isNotBlank() && !isLoading) KisanEmerald else Color(0xFFE2E8F0),
                CircleShape
              )
              .testTag("copilot_send_button")
          ) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}
