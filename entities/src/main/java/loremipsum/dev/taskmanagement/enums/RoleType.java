package loremipsum.dev.taskmanagement.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    TEAM_MEMBER("TEAM_MEMBER"),
    TEAM_LEADER("TEAM_LEADER"),
    PROJECT_MANAGER("PROJECT_MANAGER");

    private final String roleType;

    RoleType (String type){ roleType = type;}

    String getRoleType() { return roleType;}


    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
