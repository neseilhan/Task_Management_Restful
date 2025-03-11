package loremipsum.dev.enums;

public enum TaskPriority {
   CRITICAL("CRITICAL"),
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

   private final String taskPriority;

   TaskPriority(String type) { taskPriority = type;}

    String getType() {
        return taskPriority;
    }

}
