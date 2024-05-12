package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private LocalDateTime endTime;

    private final List<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, Status.NEW, description);
    }

    public Epic(String name, String status, Status description) {
        super(name, description, status);
    }

    public Epic(String name, Status status, String s) {
        super(name, status, s);
    }

    public Epic(int id, String name, Status status, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
        if (startTime == null || startTime.isAfter(subTask.startTime)) {
            startTime = subTask.startTime;
        }
        if (endTime == null || endTime.isBefore(subTask.getEndTime())) {
            endTime = subTask.getEndTime();
        }
        duration = Duration.between(startTime, endTime);
        updateEpicStatus();
    }

    public void removeSubTaskId(SubTask subTask) {
        subTasks.remove(subTask);
        updateEpicStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;

        return subTasks.equals(epic.subTasks);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subTasks.hashCode();
        return result;
    }

    private void updateEpicStatus() {
        Long done = subTasks.stream().filter(o -> o.getStatus() == Status.DONE).count();
        Long new_ = subTasks.stream().filter(o -> o.getStatus() == Status.NEW).count();
        if (done == subTasks.size()) {
            this.setStatus(Status.DONE);
        } else if (new_ == subTasks.size()) {
            this.setStatus(Status.NEW);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }
}
