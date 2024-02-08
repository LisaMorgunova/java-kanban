package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void taskCreationAndFieldsTest() {
        Task task = new Task("Test Task", Status.NEW, "Description");

        assertEquals("Test Task", task.getName(), "Название задачи не соответствует ожидаемому.");
        assertEquals("Description", task.getDescription(), "Описание задачи не соответствует ожидаемому.");
        assertEquals(Status.NEW, task.getStatus(), "Статус задачи не соответствует ожидаемому.");
    }

    @Test
    void taskIdentityTest() {
        Task task1 = new Task("Task 1", Status.NEW, "Description 1");
        Task task2 = new Task("Task 2", Status.DONE, "Description 2");

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1.getId(), task2.getId(), "ID задач не должны совпадать.");
    }

    @Test
    void taskStatusUpdateTest() {
        Task task = new Task("Task", Status.NEW, "Description");
        assertEquals(Status.NEW, task.getStatus(), "Начальный статус задачи должен быть NEW.");

        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus(), "Статус задачи должен быть обновлен на IN_PROGRESS.");

        task.setStatus(Status.DONE);
        assertEquals(Status.DONE, task.getStatus(), "Статус задачи должен быть обновлен на DONE.");
    }
}
