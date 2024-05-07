package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private String name;
    private Status status;
    private String description;
    protected Duration duration;
    protected LocalDateTime startTime;


    public Task(String name, Status status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(int id, String name, Status status, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (!name.equals(task.name)) return false;
        if (status != task.status) return false;
        return description.equals(task.description);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }
}

