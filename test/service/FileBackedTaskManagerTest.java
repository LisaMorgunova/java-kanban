package service;

import exception.ManagerSaveException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new InMemoryHistoryManager(), new File("path_to_your_file"));
    }

    @Test
    void testFileOperationException() {
        Path tmp = Paths.get("C:\\users\\username\\desktop\\favouriteFile.txt");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(tmp.toFile()), "Должно быть выброшено исключение при попытке загрузки из несуществующего файла");
    }
}