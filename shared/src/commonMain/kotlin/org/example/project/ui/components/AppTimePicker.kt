package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import instaresolv.shared.generated.resources.Res
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource
import instaresolv.shared.generated.resources.ic_calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePicker(
    text: String,
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val displayText = selectedTime.ifEmpty { text }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF2F2F2))
            .clickable { showDialog = true }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reuse calendar icon or maybe a clock icon if available. We will use ic_calendar for now unless there's an ic_clock.
        Image(
            painter = painterResource(Res.drawable.ic_calendar),
            contentDescription = "Select Time",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = displayText,
            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
            color = if (selectedTime.isNotEmpty()) AppColors.Black else AppColors.TextGray
        )
    }

    if (showDialog) {
        val timePickerState = rememberTimePickerState(is24Hour = false)
        
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            val hour = timePickerState.hour
                            val minute = timePickerState.minute
                            val amPm = if (hour < 12) "AM" else "PM"
                            val formattedHour = if (hour % 12 == 0) 12 else hour % 12
                            val formattedMinute = minute.toString().padStart(2, '0')
                            val formattedTime = "${formattedHour.toString().padStart(2, '0')} : $formattedMinute $amPm"
                            
                            onTimeSelected(formattedTime)
                            showDialog = false
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
