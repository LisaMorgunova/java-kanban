package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager;
    private int currentId = 1;
    private final TreeSet<Task> sortedTasks;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        List<Task> listOfTasks = historyManager.getHistory();
        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        for (Task task : listOfTasks) {
            if (task instanceof Epic) {
                epics.put(task.getId(), (Epic) task);
            } else if (task instanceof SubTask) {
                subTasks.put(task.getId(), (SubTask) task);
            } else {
                tasks.put(task.getId(), task);
            }
            sortedTasks.add(task);
        }
    }

    private int generateId() {
        return currentId++;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = new Task(task.getName(), task.getDescription());
        Integer id = task.getId();
        if (id == null)
            id = generateId();
        newTask.setId(id);
        tasks.put(id, newTask);
        sortedTasks.add(newTask);
        historyManager.add(newTask);
        return newTask;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubtask = new SubTask(subTask.getName(), subTask.getDescription());
        int id = generateId();
        newSubtask.setId(id);
        newSubtask.setStatus(Status.NEW);
        newSubtask.setEpicId(subTask.getEpicId());
        subTasks.put(id, newSubtask);
        sortedTasks.add(newSubtask);
        Epic parentEpic = epics.get(newSubtask.getEpicId());
        if (parentEpic != null) {
            parentEpic.addSubTask(newSubtask);
        }
        historyManager.add(newSubtask);
        return newSubtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getName(), epic.getDescription());
        int id = generateId();
        newEpic.setId(id);
        epics.put(id, newEpic);
        sortedTasks.add(newEpic);
        historyManager.add(newEpic);
        return newEpic;
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
            if (!hasTimeOverlap(task)) {
                sortedTasks.remove(task);
                tasks.put(task.getId(), task);
                sortedTasks.add(task);
                historyManager.add(task);
            } else {
                System.out.println("Ошибка: Обновление невозможно из-за пересечения по времени с другой задачей.");
            }
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            if (!hasTimeOverlap(subTask)) {
                sortedTasks.remove(subTask);
                subTasks.put(subTask.getId(), subTask);
                sortedTasks.add(subTask);
                epics.get(subTask.getEpicId()).addSubTask(subTask);
                historyManager.add(subTask);
            } else {
                System.out.println("Ошибка: Обновление невозможно из-за пересечения по времени с другой задачей.");
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (!hasTimeOverlap(epic)) {
                sortedTasks.remove(epic);
                epics.put(epic.getId(), epic);
                sortedTasks.add(epic);
                historyManager.add(epic);
            } else {
                System.out.println("Ошибка: Обновление невозможно из-за пересечения по времени с другой задачей.");
            }
        }
    }

    private boolean hasTimeOverlap(Task task) {
        for (Task t : sortedTasks) {
            if (!t.equals(task) &&
                    ((t.getStartTime().isBefore(task.getStartTime()) && t.getEndTime().isAfter(task.getStartTime())) ||
                            (t.getStartTime().isBefore(task.getEndTime()) && t.getEndTime().isAfter(task.getEndTime())) ||
                            (t.getStartTime().equals(task.getStartTime())) || t.getEndTime().equals(task.getEndTime()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateTaskStatus(int id, Status newStatus) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            sortedTasks.remove(task);
            task.setStatus(newStatus);
            sortedTasks.add(task);
            historyManager.add(task);
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            sortedTasks.remove(subTask);
            subTask.setStatus(newStatus);
            sortedTasks.add(subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epic.removeSubTask(subTask);
            epic.addSubTask(subTask);
            historyManager.add(subTask);
        }
    }

    @Override
    public void deleteTask(int id) {
        Task taskToRemove = tasks.remove(id);
        if (taskToRemove != null) {
            sortedTasks.remove(taskToRemove);
            historyManager.remove(id);
        } else {
            SubTask subTaskToRemove = subTasks.remove(id);
            if (subTaskToRemove != null) {
                sortedTasks.remove(subTaskToRemove);
                historyManager.remove(id);
            }
        }
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTaskToRemove = subTasks.remove(id);
        if (subTaskToRemove != null) {
            sortedTasks.remove(subTaskToRemove);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epicToRemove = epics.remove(id);
        if (epicToRemove != null) {
            sortedTasks.remove(epicToRemove);
            epicToRemove.getSubTasks().forEach(subTask -> {
                subTasks.remove(subTask.getId());
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
        sortedTasks.clear();
        historyManager.clear();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(sortedTasks);
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
