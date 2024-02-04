package Model;

    import java.util.ArrayList;
    import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks = new ArrayList<>();

    public Epic(String name) {
        super(name);
    }

    public void addTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    @Override
    public void calculateStatus() {
        if (subTasks.isEmpty()) {
            setStatus(Status.NEW);
        } else if (subTasks.stream().allMatch(subTask -> subTask.getStatus() == Status.DONE)) {
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }
}