package ba.untz.attendance1

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ba.untz.attendance1.ui.theme.Attendance1Theme

@Composable
fun AttendanceSummary(
    checkIns: List<CheckIn>,
    modifier: Modifier = Modifier
) {
    val onTime = checkIns.count {
        it.status == CheckInStatus.OnTime
    }
    val late = checkIns.count {
        it.status is CheckInStatus.Late
    }
    val absent = checkIns.count {
        it.status == CheckInStatus.Absent
    }

    Text(
        text = "On time: $onTime · Late: $late · Absent: $absent",
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Preview(showBackground = true)
@Composable
fun AttendanceSummaryAllPreview() {
    Attendance1Theme {
        Surface {
            AttendanceSummary(sampleCheckIns)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceSummaryMiddlePreview() {
    // Objasnjenje zadatka 1: MIDDLE ima 4 OnTime, 2 Late i 0 Absent.
    Attendance1Theme {
        Surface {
            AttendanceSummary(
                sampleCheckIns.filter {
                    it.window == Window.MIDDLE
                }
            )
        }
    }
}

@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun CheckInCard(
    checkIn: CheckIn,
    allCheckIns: List<CheckIn>,
    modifier: Modifier = Modifier
) {
    // Zadatak 10 - PREDICT prije rotacije:
    // Uz remember, rekreiranje Activityja zatvara karticu (expanded = false).
    // Zelim da detalji ostanu otvoreni nakon rotacije kako korisnik
    // ne bi izgubio ono sto cita. Zato sada koristim rememberSaveable.
    var expanded by rememberSaveable { mutableStateOf(false) }

    // PREDICT: Student je data class pa == poredi id i name.
    // Zato pronalazimo i zasebne Student objekte s istim podacima.
    // Obicna klasa bez vlastitog equals poredila bi reference,
    // pa ne bi povezala sve Amirove prijave.
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column {
            CheckInRow(checkIn)

            if (expanded) {
                Column(
                    modifier = Modifier.padding(
                        start = 64.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    allCheckIns
                        .filter { it.student == checkIn.student }
                        .forEach {
                            Text(
                                text = "${it.window.label}: ${it.status.label()}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                }
            }
        }
    }
}

// LAB6, Zadatak 5 - PREDVIDI prije rotacije:
// Otvoreni middle, pretraga, Only late i dodani Tarik ostaju u ViewModelu.
// Isti ViewModel prezivi rotaciju, pa nema novog "ViewModel created: RMAS-5".
// Ekran prima stanje i dogadjaje od rute; cuva samo stanje prikaza (scroll).
@Composable
fun ProfessorScreen(
    state: LectureUiState,
    onOpen: (Window) -> Unit,
    onQueryChange: (String) -> Unit,
    onOnlyLateChange: (Boolean) -> Unit,
    onSave: (CheckIn) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Lecture ${state.lecture.number} · ${state.lecture.title}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        WindowControls(openWindow = state.openWindow, onOpen = onOpen)
        AttendanceSummary(
            checkIns = state.inWindow,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        SearchField(
            query = state.query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LateOnlySwitch(
            onlyLate = state.onlyLate,
            onOnlyLateChange = onOnlyLateChange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (state.openWindow == null) {
            Text("Open a window to see check-ins", modifier = Modifier.padding(16.dp))
        } else if (state.shown.isEmpty()) {
            Text("No check-ins match the filter", modifier = Modifier.padding(16.dp))
        } else {
            state.shown.forEach { checkIn ->
                key(checkIn.student.id, checkIn.window) {
                    CheckInCard(
                        checkIn = checkIn,
                        allCheckIns = state.checkIns,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
        state.openWindow?.let { window ->
            ManualCheckInForm(
                window = window,
                existing = state.checkIns,
                onSave = onSave,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "LAB6 - bez otvorenog prozora", showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfessorScreenNoWindowPreview() {
    Attendance1Theme {
        ProfessorScreen(
            state = LectureUiState(lecture = sampleLectures.last(), checkIns = sampleCheckIns),
            onOpen = {}, onQueryChange = {}, onOnlyLateChange = {}, onSave = {}
        )
    }
}

@Preview(name = "LAB6 - middle only late", showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun ProfessorScreenMiddleLatePreview() {
    Attendance1Theme {
        ProfessorScreen(
            state = LectureUiState(
                lecture = sampleLectures.last(),
                checkIns = sampleCheckIns,
                openWindow = Window.MIDDLE,
                onlyLate = true
            ),
            onOpen = {}, onQueryChange = {}, onOnlyLateChange = {}, onSave = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun CheckInRowsPreview() {
    Attendance1Theme {
        Surface {
            Column {
                sampleCheckIns
                    .filter { it.window == Window.START }
                    .forEach {
                        CheckInRow(it)
                    }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckInCardPreview() {
    Attendance1Theme {
        Surface {
            CheckInCard(
                checkIn = sampleCheckIns[1],
                allCheckIns = sampleCheckIns
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModifierOrderPreview() {
    // Objasnjenje zadatka 3:
    // A: padding je izvan obojene pozadine.
    // B: pozadina obuhvata i padding.
    Attendance1Theme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("A: padding pa background")

                Text(
                    text = "Late",
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer
                        )
                )

                Text("B: background pa padding")

                Text(
                    text = "Late",
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.errorContainer
                        )
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: CheckInStatus,
    modifier: Modifier = Modifier
) {
    Text(
        text = status.label(),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        color = status.color(),
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
fun CheckInRow(
    checkIn: CheckIn,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box {
            Avatar(checkIn.student.initials())

            if (checkIn.status is CheckInStatus.Late) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = checkIn.student.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            StatusBadge(checkIn.status)
        }

        Text(
            text = checkIn.timeText(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CheckInStatus.color(): Color = when (this) {
    CheckInStatus.OnTime -> MaterialTheme.colorScheme.primary
    is CheckInStatus.Late -> MaterialTheme.colorScheme.error
    CheckInStatus.Absent -> MaterialTheme.colorScheme.outline
}

// Objasnjenje zadatka 6: dodavanje Excused zahtijevalo bi novu granu
// u label() i color(), jer njihovi when izrazi moraju biti iscrpni.
// Ovo je objasnjenje; rezultat eksperimenta provjeriti u editoru.

@Preview(
    name = "Light",
    group = "Zadatak 7",
    showBackground = true,
    widthDp = 380
)
@Preview(
    name = "Dark",
    group = "Zadatak 7",
    showBackground = true,
    widthDp = 380,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CheckInRowThemePreview() {
    Attendance1Theme {
        Surface {
            CheckInRow(sampleCheckIns[1])
        }
    }
}

// Zadatak 8 - predvidjanje za prethodnu verziju s obicnom varijablom:
// PREDICT: Nakon klika na middle Logcat ispisuje "now: middle",
// ali ekran ostaje na "Open: none" i dugme ostaje omoguceno.
// Obicna varijabla ne obavjestava Compose o promjeni.
// Pri ponovnom izvrsavanju ove funkcije opet se postavlja na null.
@Composable
fun WindowControls(
    openWindow: Window?,
    onOpen: (Window) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Open: ${openWindow?.label ?: "none"}",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Window.entries.forEach { w ->
                Button(
                    onClick = { onOpen(w) },
                    enabled = openWindow != w
                ) {
                    Text(w.label)
                }
            }
        }
    }
}

@Preview(
    name = "Middle otvoren",
    group = "Zadatak 11",
    showBackground = true,
    widthDp = 380
)
@Composable
fun WindowControlsMiddlePreview() {
    Attendance1Theme {
        Surface {
            WindowControls(
                openWindow = Window.MIDDLE,
                onOpen = {}
            )
        }
    }
}

