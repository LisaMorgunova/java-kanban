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

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask, int epicId) {
        subTask.setId(generateId());
        subTask.setEpicId(epicId);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubTasks().add(subTask);
            calculateEpicStatus(epicId);
        }
        return subTask;
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }
        return null; // Возвращаем null, если задача не найдена
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else if (subTasks.containsKey(task.getId())) {
            SubTask subTask = (SubTask) task;
            subTasks.put(subTask.getId(), subTask);
            calculateEpicStatus(subTask.getEpicId());
        }
    }

    public void deleteTask(int id) {
        if (tasks.remove(id) != null || subTasks.remove(id) != null) {
            // Если удаляемая задача - подзадача, обновляем статус эпика
            SubTask subTask = subTasks.get(id);
            if (subTask != null) {
                calculateEpicStatus(subTask.getEpicId());
            }
        } else if (epics.containsKey(id)) {
            // Если удаляем эпик, удаляем и его подзадачи
            Epic epic = epics.remove(id);
            for (SubTask subTask : epic.getSubTasks()) {
                subTasks.remove(subTask.getId());
            }
        }
    }

    public void updateTaskStatus(int id, Status status) {
        Task task = getTask(id);
        if (task != null) {
            task.setStatus(status);
            updateTask(task);
            if (task instanceof SubTask) {
                calculateEpicStatus(((SubTask) task).getEpicId());
            }
        }
    }

    private void calculateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null && !epic.getSubTasks().isEmpty()) {
            int countDone = 0;
            int countInProgress = 0;
            for (SubTask subTask : epic.getSubTasks()) {
                switch (subTask.getStatus()) {
                    case DONE:
                        countDone++;
                        break;
                    case IN_PROGRESS:
                        countInProgress++;
                        break;
                    default:
                        break;
                }
            }
            if (countDone == epic.getSubTasks().size()) {
                epic.setStatus(Status.DONE);
            } else if (countInProgress > 0 || countDone > 0) {
                epic.setStatus(Status.IN_PROGRESS);
            } else {
                epic.setStatus(Status.NEW);
            }
        }
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subTasks.values());
        return allTasks;
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getSubTasksOfEpic(int epicId) {
        List<SubTask> result = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == epicId) {
                result.add(subTask);
            }
        }
        return result;
    }
}

