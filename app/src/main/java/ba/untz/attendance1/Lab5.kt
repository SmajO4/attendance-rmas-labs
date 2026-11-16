package ba.untz.attendance1

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import ba.untz.attendance1.ui.theme.Attendance1Theme

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

// PREDVIDI: Ako svako dugme ima svoj Boolean state, vise dugmadi
// moze biti odabrano istovremeno. To vise nije radio grupa.
// Jedna zajednicka vrijednost StatusKind osigurava samo jedan izbor.
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
