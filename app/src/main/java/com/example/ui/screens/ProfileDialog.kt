package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile

@Composable
fun ProfileDialog(
  strings: AppStrings,
  profile: FarmerProfile,
  onDismiss: () -> Unit,
  onSave: (FarmerProfile) -> Unit
) {
  var name by remember { mutableStateOf(profile.name) }
  var village by remember { mutableStateOf(profile.village) }
  var state by remember { mutableStateOf(profile.state) }
  var landAcresStr by remember { mutableStateOf(profile.totalLandAcres.toString()) }
  var primaryCrop by remember { mutableStateOf(profile.primaryCrop) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(text = strings.farmerProfile, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Farmer Name") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("farmer_name_input"),
          singleLine = true
        )

        OutlinedTextField(
          value = village,
          onValueChange = { village = it },
          label = { Text("Village / District") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = state,
          onValueChange = { state = it },
          label = { Text("State") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = landAcresStr,
          onValueChange = { landAcresStr = it },
          label = { Text("Total Land (Acres)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = primaryCrop,
          onValueChange = { primaryCrop = it },
          label = { Text("Primary Crops") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val acres = landAcresStr.toDoubleOrNull() ?: profile.totalLandAcres
          onSave(
            profile.copy(
              name = name.ifBlank { profile.name },
              village = village.ifBlank { profile.village },
              state = state.ifBlank { profile.state },
              totalLandAcres = acres,
              primaryCrop = primaryCrop.ifBlank { profile.primaryCrop }
            )
          )
        },
        modifier = Modifier.testTag("save_profile_button")
      ) {
        Text("Save Profile")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
