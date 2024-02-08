package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testEpicEquality() {
        Epic epic = new Epic("name", "desc", Status.NEW);
        Epic epicExpected = new Epic("name", "desc", Status.NEW);

        epic.setId(1);
        epicExpected.setId(1);

        assertEquals(epicExpected, epic, "Эпики должны совпадать");

        assertTrue(assertEqualsTask(epicExpected, epic), "Эпики должны совпадать");
    }

    private static boolean assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getId(), actual.getId(), "ID задач должны совпадать");
        assertEquals(expected.getName(), actual.getName(), "Имена задач должны совпадать");
        assertEquals(expected.getDescription(), actual.getDescription(), "Описания задач должны совпадать");
        assertEquals(expected.getStatus(), actual.getStatus(), "Статусы задач должны совпадать");

        return true;
    }

}
