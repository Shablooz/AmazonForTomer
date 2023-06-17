package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.UserPermissions;

public interface IUserPermissionRepository {
    UserPermissions getUserPermission(int userId);

    void addUserPermission(int userId, UserPermissions userPermissions);

    boolean isUserPermissionsExists(int userId);
}
