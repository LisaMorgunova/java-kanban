package model;

import java.util.Optional;

public enum Status {
    NEW, IN_PROGRESS, DONE;

    public static Optional<Status> check(String val) {
        try {
            return Optional.of(Status.valueOf(val));
        } catch (Exception e) {/* do nothing */}
        return Optional.empty();
    }
}