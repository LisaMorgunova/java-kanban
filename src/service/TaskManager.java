package service;

import Model.Epic;
import Model.SubTask;
import Model.Task;
import Model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int seq = 0;

    private int generateId() {
        return ++seq;
    }
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

public SubTask createSubTask(SubTask subTask) {
    int epicId = subTask.getEpicId();
    Epic epic = epics.get(epicId);
    if (epic == null) {
        return null;
    }
    subTask.setId(generateId());
    subTasks.put(subTask.getId(), subTask);
    epic.getSubTasksIds().add(subTask.getId());
    calculateEpicStatus(epicId);
    return subTask;
}

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId());
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            calculateEpicStatus(epic.getId());
        }
    }

    public void updateTaskStatus(int id, Status newStatus) {
        if (tasks.containsKey(id)) {
            tasks.get(id).setStatus(newStatus);
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            subTask.setStatus(newStatus);
            updateEpicStatus(subTask.getEpicId());
        } else if (epics.containsKey(id)) {
            updateEpicStatus(id);
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<SubTask> subTasksOfEpic = getSubTasksOfEpic(epicId);
        boolean allDone = subTasksOfEpic.stream().allMatch(subTask -> subTask.getStatus() == Status.DONE);
        boolean anyInProgress = subTasksOfEpic.stream().anyMatch(subTask -> subTask.getStatus() == Status.IN_PROGRESS);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (subTasksOfEpic.stream().allMatch(subTask -> subTask.getStatus() == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.getSubTasksIds().remove(Integer.valueOf(id));
                calculateEpicStatus(epic.getId());
            }
        }
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTasksIds()) {
                subTasks.remove(subTaskId);
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        subTasks.clear();
        epics.clear();
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<SubTask> getSubTasksOfEpic(int epicId) {
        List<SubTask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTasksIds()) {
                result.add(subTasks.get(subTaskId));
            }
        }
        return result;
    }

    private void calculateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Integer subTaskId : epic.getSubTasksIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask != null) {
                Status status = subTask.getStatus();
                if (status != Status.DONE) {
                    allDone = false;
                }
                if (status == Status.IN_PROGRESS) {
                    anyInProgress = true;
                }
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }
}