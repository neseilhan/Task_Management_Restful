package loremipsum.dev.enums;

import org.springframework.security.core.parameters.P;

public enum RoleType {
    TEAM_MEMBER("TEAM_MEMBER"),
    TEAM_LEADER("TEAM_LEADER"),
    PROJECT_MANAGER("PROJECT_MANAGER");

    private final String roleType;

    RoleType (String type){ roleType = type;}

    String getRoleType() { return roleType;}
}
