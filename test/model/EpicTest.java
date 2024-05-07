package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {

    @Test
    void testEpicEquality() {
        Epic epic = new Epic("name", "desc");
        Epic epicExpected = new Epic("name", "desc");

        epic.setId(1);
        epicExpected.setId(1);

        assertEquals(epicExpected, epic, "Эпики должны совпадать");

        assertTrue(assertEqualsTask(epicExpected, epic), "Эпики должны совпадать");
    }

    @Test
    void testEpicStatusCalculationAllNewSubtasks() {
        Epic epic = new Epic("name", "desc");
        epic.setId(1);

        SubTask subTask1 = new SubTask(1, "subTask1", Status.NEW, "desc1", Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        SubTask subTask2 = new SubTask(2, "subTask2", Status.NEW, "desc2", Duration.ofHours(1), LocalDateTime.now(), epic.getId());

        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статус должен быть NEW, если все подзадачи NEW");
    }

    @Test
    void testEpicStatusCalculationAllDoneSubtasks() {
        Epic epic = new Epic("name", "desc");
        epic.setId(1);

        SubTask subTask1 = new SubTask(1, "subTask1", Status.DONE, "desc1", Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        SubTask subTask2 = new SubTask(2, "subTask2", Status.DONE, "desc2", Duration.ofHours(1), LocalDateTime.now(), epic.getId());

        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        assertEquals(Status.DONE, epic.getStatus(), "Статус должен быть DONE, если все подзадачи DONE");
    }

    @Test
    void testEpicStatusCalculationMixedSubtasks() {
        Epic epic = new Epic("name", "desc");
        epic.setId(1);

        SubTask subTask1 = new SubTask(1, "subTask1", Status.NEW, "desc1", Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        SubTask subTask2 = new SubTask(2, "subTask2", Status.DONE, "desc2", Duration.ofHours(1), LocalDateTime.now(), epic.getId());

        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS, если есть подзадачи с разными статусами");
    }

    @Test
    void testEpicStatusCalculationInProgressSubtasks() {
        Epic epic = new Epic("name", "desc");
        epic.setId(1);

        SubTask subTask1 = new SubTask(1, "subTask1", Status.IN_PROGRESS, "desc1", Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        SubTask subTask2 = new SubTask(2, "subTask2", Status.IN_PROGRESS, "desc2", Duration.ofHours(1), LocalDateTime.now(), epic.getId());

        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS, если все подзадачи IN_PROGRESS");
    }

    private static boolean assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getId(), actual.getId(), "ID задач должны совпадать");
        assertEquals(expected.getName(), actual.getName(), "Имена задач должны совпадать");
        assertEquals(expected.getDescription(), actual.getDescription(), "Описания задач должны совпадать");
        assertEquals(expected.getStatus(), actual.getStatus(), "Статусы задач должны совпадать");

        return true;
    }
}
