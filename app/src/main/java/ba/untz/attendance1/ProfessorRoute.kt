package ba.untz.attendance1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// Lab 6 - Zadatak 4: ruta povezuje ekran sa ViewModelom.
@Composable
fun ProfessorRoute(lectureId: String, modifier: Modifier = Modifier) {
    val viewModel: LectureViewModel = viewModel { LectureViewModel(lectureId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfessorScreen(
        state = state,
        onOpen = viewModel::openWindow,
        onQueryChange = viewModel::setQuery,
        onOnlyLateChange = viewModel::setOnlyLate,
        onSave = viewModel::addCheckIn,
        modifier = modifier
    )
}
