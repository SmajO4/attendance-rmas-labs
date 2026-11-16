package ba.untz.attendance1

import org.junit.Assert.assertEquals
import org.junit.Test

class LectureUiStateTest {
    private val base = LectureUiState(
        lecture = sampleLectures.last(),
        checkIns = sampleCheckIns
    )

    @Test
    fun noWindow_showsAllFifteenCheckIns() {
        assertEquals(15, base.shown.size)
    }

    @Test
    fun middle_hasSixCheckIns() {
        val state = base.copy(openWindow = Window.MIDDLE)
        assertEquals(6, state.inWindow.size)
        assertEquals(6, state.shown.size)
    }

    @Test
    fun middle_onlyLate_showsFarisAndSelma() {
        val state = base.copy(openWindow = Window.MIDDLE, onlyLate = true)
        assertEquals(listOf("Faris Ćosić", "Selma Lugavić"), state.shown.map { it.student.name })
    }

    @Test
    fun middle_searchAmi_showsAminaAndAmirIgnoringCase() {
        val state = base.copy(openWindow = Window.MIDDLE, query = "AMI")
        assertEquals(listOf("Amina Bajrektarević", "Amir Imširović"), state.shown.map { it.student.name })
    }
}
