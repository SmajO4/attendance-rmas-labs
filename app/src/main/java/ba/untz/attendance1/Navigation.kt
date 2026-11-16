package ba.untz.attendance1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

@Serializable
data object LectureList : NavKey

@Serializable
data class LectureDetail(val lectureId: String) : NavKey

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(LectureList)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        // LAB6, Zadatak 6: svaki unos back stacka ima vlastiti ViewModel.
        // PREDVIDI prije dekoratora: RMAS-4 bi ponovo koristio ViewModel
        // aktivnosti za RMAS-5, s naslovom 5, middle i Tarikovom prijavom.
        // S dekoratorom Back uklanja taj unos i cisti njegov ViewModel;
        // drugi unos pocinje ispocetka. Za trajne prijave trebaju spremiste
        // podataka / baza, jer ViewModel ne cuva podatke nakon uklanjanja.
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<LectureList> {
                LectureListScreen(
                    lectures = sampleLectures,
                    onOpen = { id -> backStack.add(LectureDetail(id)) }
                )
            }
            entry<LectureDetail> { key ->
                ProfessorRoute(lectureId = key.lectureId)
            }
        }
    )
}

@Composable
fun LectureListScreen(
    lectures: List<Lecture>,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("RMAS · Lectures", style = MaterialTheme.typography.titleLarge)
        lectures.forEach { lecture ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onOpen(lecture.id) }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lecture ${lecture.number}", style = MaterialTheme.typography.labelLarge)
                    Text(lecture.title, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
