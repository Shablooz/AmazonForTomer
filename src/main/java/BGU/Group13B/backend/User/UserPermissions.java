package BGU.Group13B.backend.User;

import java.util.HashMap;

public class UserPermissions {
    public enum UserPermissionStatus {
        ADMIN,
        MEMBER,
        GUEST
    }

    public enum StoreRole {
        OWNER,
        MANAGER
    }

    private UserPermissionStatus userPermissionStatus;
    private HashMap<Integer/*storeId*/, StoreRole> userStoreRole;
}

