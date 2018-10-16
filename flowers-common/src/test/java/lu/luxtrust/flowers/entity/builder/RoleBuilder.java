package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.enums.RoleType;

public class RoleBuilder {

    private RoleBuilder() {}

    public static Role role(RoleType roleType) {
        Role role = new Role();
        role.setRoleType(roleType);
        role.setId((long) roleType.ordinal());
        return role;
    }

}
