package lu.luxtrust.flowers.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static lu.luxtrust.flowers.enums.Permission.*;

public enum RoleType {
    END_USER,

    DIA (CREATE_CERTIFICATEORDER, EDIT_CERTIFICATEORDER, READ_CERTIFICATEORDER, SIGN_AND_SEND_CERTIFICATEORDER),

    CSD_AGENT (EDIT_CERTIFICATEORDER, READ_CERTIFICATEORDER, CREATE_CERTIFICATEORDER, SIGN_AND_SEND_CERTIFICATEORDER, CREATE_IN_BATCH_CERTIFICATEORDER, READ_ESEALORDER),

    FLOWERS_ADMIN (READ_CERTIFICATEORDER, CREATE_UNIT, EDIT_UNIT, READ_UNIT, CREATE_USER, EDIT_USER, READ_USER, READ_ESEALORDER),

    ESEAL_MANAGER (READ_ESEALORDER, ACTIVATE_ESEALORDER),

    ESEAL_OFFICER (CREATE_UNIT, READ_UNIT, EDIT_UNIT, CREATE_USER, READ_USER, EDIT_USER, CREATE_ESEALORDER, EDIT_ESEALORDER, READ_ESEALORDER, SIGN_AND_SEND_ESEALORDER);

    private final Set<Permission> permissions;

    RoleType(Permission... permissions) {
        this.permissions = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(permissions)));
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
