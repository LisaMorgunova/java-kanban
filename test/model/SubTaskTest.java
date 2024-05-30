package model;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {

    @Test
    void subTaskCreationAndFieldsTest() {
        int epicId = 100;
        SubTask subTask = new SubTask("SubTask Test", Status.NEW, "SubTask Description", epicId);

        Assertions.assertEquals("SubTask Test", subTask.getName(), "Название подзадачи не соответствует ожидаемому.");
        Assertions.assertEquals("SubTask Description", subTask.getDescription(), "Описание подзадачи не соответствует ожидаемому.");
        Assertions.assertEquals(Status.NEW, subTask.getStatus(), "Статус подзадачи не соответствует ожидаемому.");
        Assertions.assertEquals(epicId, subTask.getEpicId(), "ID эпика не соответствует ожидаемому.");
    }

    @Test
    void epicIdAssignmentTest() {
        int initialEpicId = 1;
        SubTask subTask = new SubTask("SubTask", Status.NEW, "Description", initialEpicId);

        assertEquals(initialEpicId, subTask.getEpicId(), "Начальный ID эпика не соответствует ожидаемому.");

        int newEpicId = 2;
        subTask.setEpicId(newEpicId);
        assertEquals(newEpicId, subTask.getEpicId(), "Обновленный ID эпика не соответствует ожидаемому.");
    }
}
