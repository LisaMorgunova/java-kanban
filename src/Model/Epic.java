package Model;

    import java.util.ArrayList;
    import java.util.List;

public class Epic extends Task {

    private List<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasksIds = new ArrayList<>();
    }

    public List<Integer> getSubTasksIds() {
        return new ArrayList<>(subTasksIds);
    }

    public void addSubTaskId(int id) {
        subTasksIds.add(id);
    }

    public void removeSubTaskId(Integer id) {
        subTasksIds.remove(id);
    }
}