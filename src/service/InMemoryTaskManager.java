package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager;
    private int currentId = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        List<Task> listOfTasks = historyManager.getHistory();
        for (Task task :
                listOfTasks) {
            if (task instanceof Epic) {
                epics.put(task.getId(), (Epic) task);
            } else if (task instanceof SubTask) {
                subTasks.put(task.getId(), (SubTask) task);
            } else {
                tasks.put(task.getId(), task);
            }
        }
    }

    private int generateId() {
        return currentId++;
    }

    @Override
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        int id = generateId();
        subTask.setId(id);
        subTasks.put(id, subTask);
        Epic parentEpic = epics.get(subTask.getEpicId());
        if (parentEpic != null) {
            parentEpic.addSubTask(subTask);
        }
        return subTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            epics.get(subTask.getEpicId()).addSubTask(subTask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateTaskStatus(int id, Status newStatus) {
        if (tasks.containsKey(id)) {
            tasks.get(id).setStatus(newStatus);
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            subTask.setStatus(newStatus);
            Epic epic = epics.get(subTask.getEpicId());
            epic.removeSubTaskId(subTask);
            epic.addSubTask(subTask);
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.remove(id) != null || subTasks.remove(id) != null) {
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTaskId(subTask);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubTasks().stream().peek(subTask -> {
                subTasks.remove(subTask);
                historyManager.remove(subTask.getId());
            });
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        subTasks.clear();
        epics.clear();
        historyManager.clear();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTasksOfEpic(int epicId) {
        List<SubTask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubTasks().stream().peek(subTaskId -> result.add(subTasks.get(subTaskId)));
        }
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
