package ba.untz.attendance1

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ba.untz.attendance1.ui.theme.Attendance1Theme
import kotlin.math.roundToInt

// Lab 5 - Zadatak 1: stanje dolazi od roditelja, zato nema remember.
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search student") },
        placeholder = { Text("e.g. Amir") },
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun SearchFieldPreview() {
    Attendance1Theme {
        Surface {
            SearchField(query = "ami", onQueryChange = {})
        }
    }
}

// Lab 5 - Zadatak 3: roditelj cuva stanje, cijeli red obradjuje klik.
@Composable
fun LateOnlySwitch(
    onlyLate: Boolean,
    onOnlyLateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = onlyLate,
                role = Role.Switch,
                onValueChange = onOnlyLateChange
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Only late", modifier = Modifier.weight(1f))
        // Klik obradjuje Row, tako da kontrola ima samo jedan slusac.
        Switch(checked = onlyLate, onCheckedChange = null)
    }
}

@Preview(name = "Only late - ukljucen", showBackground = true, widthDp = 380)
@Composable
fun LateOnlySwitchOnPreview() {
    Attendance1Theme {
        Surface {
            LateOnlySwitch(onlyLate = true, onOnlyLateChange = {})
        }
    }
}

@Preview(name = "Only late - iskljucen", showBackground = true, widthDp = 380)
@Composable
fun LateOnlySwitchOffPreview() {
    Attendance1Theme {
        Surface {
            LateOnlySwitch(onlyLate = false, onOnlyLateChange = {})
        }
    }
}

// Lab 5 - Zadatak 5: vrsta statusa; minute kasnjenja biramo zasebno.
enum class StatusKind(val label: String) {
    ON_TIME("On time"),
    LATE("Late"),
    ABSENT("Absent")
}
@Composable
fun StatusPicker(
    selected: StatusKind,
    onSelect: (StatusKind) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        StatusKind.entries.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = option == selected,
                        onClick = { onSelect(option) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = option == selected,
                    onClick = null
                )
                Text(option.label)
            }
        }
    }
}

@Preview(name = "Status - Late", showBackground = true, widthDp = 380)
@Composable
fun StatusPickerPreview() {
    Attendance1Theme {
        Surface {
            StatusPicker(selected = StatusKind.LATE, onSelect = {})
        }
    }
}

// Lab 5 - Zadatak 6, PREDVIDI:
// 5, 10, ... 60 je 12 vrijednosti, odnosno 10 tacaka izmedju krajeva.
// Zato je steps = (60 - 5) / 5 - 1 = 10.
// steps = 12 daje 14 vrijednosti i razmak 55 / 13 (oko 4.23),
// pa unutrasnje vrijednosti nisu cijeli brojevi.
@Composable
fun LateMinutesSlider(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(if (enabled) "Late: $minutes min" else "Late: —")
        Slider(
            value = minutes.toFloat(),
            onValueChange = { onMinutesChange(it.roundToInt()) },
            valueRange = 5f..60f,
            steps = 10,
            enabled = enabled
        )
    }
}

@Preview(name = "Late minutes - omogucen", showBackground = true, widthDp = 380)
@Composable
fun LateMinutesSliderEnabledPreview() {
    // Stanje je u roditelju; sama kontrola nema remember.
    var minutes by rememberSaveable { mutableStateOf(15) }
    Attendance1Theme {
        Surface {
            LateMinutesSlider(
                minutes = minutes,
                onMinutesChange = { minutes = it },
                enabled = true
            )
        }
    }
}

@Preview(name = "Late minutes - onemogucen", showBackground = true, widthDp = 380)
@Composable
fun LateMinutesSliderDisabledPreview() {
    Attendance1Theme {
        Surface {
            LateMinutesSlider(
                minutes = 15,
                onMinutesChange = {},
                enabled = false
            )
        }
    }
}

// Lab 5 - Zadatak 8: vremena su minute od ponoci.
fun Window.startMinutes(): Int = when (this) {
    Window.START -> 8 * 60
    Window.MIDDLE -> 10 * 60
    Window.END -> 11 * 60 + 30
}

fun StatusKind.toStatus(lateMinutes: Int): CheckInStatus = when (this) {
    StatusKind.ON_TIME -> CheckInStatus.OnTime
    StatusKind.LATE -> CheckInStatus.Late(lateMinutes)
    StatusKind.ABSENT -> CheckInStatus.Absent
}

// Lab 5 - Zadatak 7: nacrt ostaje u formi, validacija se racuna iz nacrta.
@Composable
fun ManualCheckInForm(
    window: Window,
    existing: List<CheckIn>,
    onSave: (CheckIn) -> Unit,
    modifier: Modifier = Modifier
) {
    var index by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf(StatusKind.ON_TIME) }
    var lateMinutes by rememberSaveable { mutableStateOf(5) }
    var confirmed by rememberSaveable { mutableStateOf(false) }

    val indexValid = Regex("IB\\d{3}").matches(index)
    val nameValid = name.trim().contains(" ")
    val canSave = indexValid && nameValid && confirmed

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Manual check-in · ${window.label}",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = index,
            onValueChange = { index = it.uppercase() },
            label = { Text("Index") },
            isError = index.isNotEmpty() && !indexValid,
            supportingText = { Text("Format: IB + 3 digits") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("First and last name") },
            isError = name.isNotEmpty() && !nameValid,
            supportingText = { Text("e.g. Tarik Hodžić") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        StatusPicker(selected = status, onSelect = { status = it })
        LateMinutesSlider(
            minutes = lateMinutes,
            onMinutesChange = { lateMinutes = it },
            enabled = status == StatusKind.LATE
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = confirmed,
                    role = Role.Checkbox,
                    onValueChange = { confirmed = it }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = confirmed, onCheckedChange = null)
            Text("Confirmed in person")
        }
        Button(
            onClick = {
                val minutes = when (status) {
                    StatusKind.ON_TIME -> window.startMinutes()
                    StatusKind.LATE -> window.startMinutes() + lateMinutes
                    StatusKind.ABSENT -> 0
                }
                onSave(
                    CheckIn(
                        student = Student(index.trim(), name.trim()),
                        window = window,
                        minutes = minutes,
                        status = status.toStatus(lateMinutes)
                    )
                )
                index = ""
                name = ""
                status = StatusKind.ON_TIME
                lateMinutes = 5
                confirmed = false
            },
            enabled = canSave
        ) {
            Text("Add check-in")
        }
    }
}

@Preview(name = "Manual check-in - middle", showBackground = true, widthDp = 380)
@Composable
fun ManualCheckInFormPreview() {
    Attendance1Theme {
        Surface {
            ManualCheckInForm(
                window = Window.MIDDLE,
                existing = sampleCheckIns,
                onSave = {},
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            )
        }
    }
}
