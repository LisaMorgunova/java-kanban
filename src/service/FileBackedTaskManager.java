package service;

import exception.ManagerSaveException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File save;

    public FileBackedTaskManager(HistoryManager historyManager, File save) {
        super(historyManager);
        this.save = save;
    }

    @Override
    public Task createTask(Task task) {
        Task a = super.createTask(task);
        save();
        return a;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask a = super.createSubTask(subTask);
        save();
        return a;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic a = super.createEpic(epic);
        save();
        return a;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTaskStatus(int id, Status newStatus) {
        super.updateTaskStatus(id, newStatus);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    protected void save() {
        try (FileWriter FW = new FileWriter(save)) {
            FW.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    static String historyToString(HistoryManager manager) {
        List<Task> list = manager.getHistory();
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : list) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",\n");
            }
            stringBuilder.append(toString(task));
        }
        stringBuilder.insert(0, "id,type,name,status,description,epic,\n");
        return stringBuilder.toString();
    }

    static List<Task> historyFromString(String value) {
        List<Task> list = new LinkedList<>();
        String[] substring = value.split("[,\n]+");
        for (int i = 6; i < substring.length; i += 5) {
            Task t;
            switch (substring[i + 1]) {
                case "TASK":
                    t = new Task(substring[i + 2], Status.check(substring[i + 3]).get(), substring[i + 4]);
                    t.setId(Integer.parseInt(substring[i]));
                    break;
                case "SUBTASK":
                    t = new SubTask(substring[i + 2], Status.check(substring[i + 3]).get(), substring[i + 4], Integer.parseInt(substring[i + 5]));
                    t.setId(Integer.parseInt(substring[i]));
                    i++;
                    break;
                default:
                    t = new Epic(substring[i + 2], Status.check(substring[i + 3]).get(), substring[i + 4]);
                    t.setId(Integer.parseInt(substring[i]));
                    break;
            }
            list.add(t);
        }
        return list;
    }

    enum Tasks {
        SUBTASK("SUBTASK"), TASK("TASK"), EPIC("EPIC");
        String title;

        @Override
        public String toString() {
            return title;
        }

        Tasks(String subtask) {
            title = subtask;
        }
    }

    public static String toString(Task task) {
        Tasks type;
        int epicId = -1;
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId());
        builder.append(",");
        if (task instanceof Epic) {
            type = Tasks.EPIC;
        } else if (task instanceof SubTask) {
            type = Tasks.SUBTASK;
            epicId = ((SubTask) task).getEpicId();

        } else {
            type = Tasks.TASK;
        }
        builder.append(type);
        builder.append(",");
        builder.append(task.getName());
        builder.append(",");
        builder.append(task.getStatus());
        builder.append(",");
        builder.append(task.getDescription());
        if (type == Tasks.SUBTASK) {
            builder.append(",");
            builder.append(epicId);
        }
        return builder.toString();
    }

    static FileBackedTaskManager loadFromFile(File file) {
        HistoryManager historyManager1 = new InMemoryHistoryManager();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(historyManager1, file);
        try (FileReader FR = new FileReader(file);
             Scanner scanner = new Scanner(FR)) {
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }
            List<Task> list = FileBackedTaskManager.historyFromString(stringBuilder.toString());
            for (Task t : list) {
                historyManager1.add(t);
                if (t instanceof Epic) {
                    fileBackedTaskManager.epics.put(t.getId(), (Epic) t);
                } else if (t instanceof SubTask) {
                    fileBackedTaskManager.subTasks.put(t.getId(), (SubTask) t);
                } else {
                    fileBackedTaskManager.tasks.put(t.getId(), t);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
        return fileBackedTaskManager;
    }
}



