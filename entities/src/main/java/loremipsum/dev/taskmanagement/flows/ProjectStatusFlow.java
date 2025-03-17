package loremipsum.dev.taskmanagement.flows;

import loremipsum.dev.taskmanagement.enums.ProjectStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class ProjectStatusFlow {
    private static final Map<ProjectStatus, Set<ProjectStatus>> validTransitions = new EnumMap<>(ProjectStatus.class);
//
//    static {
//        validTransitions.put(ProjectStatus.BACKLOG, EnumSet.of(ProjectStatus.ANALYSIS));
//        validTransitions.put(ProjectStatus.ANALYSIS, EnumSet.of(ProjectStatus.DEVELOPMENT));
//        validTransitions.put(ProjectStatus.DEVELOPMENT, EnumSet.of(ProjectStatus.COMPLETED));
//        validTransitions.put(ProjectStatus.COMPLETED, EnumSet.noneOf(ProjectStatus.class)); // Tamamlanmış projeler değiştirilemez
//    }
//
//    public static boolean isValidTransition(ProjectStatus current, ProjectStatus next) {
//        return validTransitions.getOrDefault(current, EnumSet.noneOf(ProjectStatus.class)).contains(next);
//    }
}
