package ba.untz.attendance1

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Lab 6, zadatak 1: podaci ekrana cine jednu nepromjenjivu vrijednost.
data class LectureUiState(
    val lecture: Lecture,
    val openWindow: Window? = null,
    val checkIns: List<CheckIn> = emptyList(),
    val query: String = "",
    val onlyLate: Boolean = false
) {
    val inWindow: List<CheckIn>
        get() = if (openWindow == null) checkIns else checkIns.filter { it.window == openWindow }

    // PREDVIDI: kad bi shown bilo zasebno sacuvano polje, promjena query
    // bez njegovog azuriranja ostavila bi stare rezultate na ekranu.
    val shown: List<CheckIn>
        get() = inWindow.filter {
            it.student.name.contains(query, ignoreCase = true) &&
                (!onlyLate || it.status is CheckInStatus.Late)
        }
}

class LectureViewModel(private val lectureId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(
        LectureUiState(
            lecture = sampleLectures.first { it.id == lectureId },
            checkIns = sampleCheckIns
        )
    )

    // PREDVIDI: javni MutableStateFlow dopustio bi bilo kojem ekranu da
    // mijenja stanje mimo dogovorenih dogadjaja, pa je teze pratiti i
    // kontrolisati promjene cak i dok aplikacija ispravno radi.
    val uiState: StateFlow<LectureUiState> = _uiState.asStateFlow()

    init {
        Log.d("Lab6", "ViewModel created: $lectureId")
    }

    fun openWindow(window: Window) {
        _uiState.update { it.copy(openWindow = window) }
    }

    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun setOnlyLate(onlyLate: Boolean) {
        _uiState.update { it.copy(onlyLate = onlyLate) }
    }

    fun addCheckIn(checkIn: CheckIn) {
        _uiState.update { it.copy(checkIns = it.checkIns + checkIn) }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Lab6", "ViewModel cleared: $lectureId")
    }
}
