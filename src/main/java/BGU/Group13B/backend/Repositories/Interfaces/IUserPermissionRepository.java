package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.permissions.ChangePermissionException;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;

public interface IUserPermissionRepository {
    UserPermissions getUserPermission(int userId);

    void addUserPermission(int userId, UserPermissions userPermissions);

    void setSaveMode(boolean saveMode);


    boolean isUserPermissionsExists(int userId);

    void deletePermissions(int adminId, int userId) throws NoPermissionException, ChangePermissionException;
}
