package ba.untz.attendance1

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
