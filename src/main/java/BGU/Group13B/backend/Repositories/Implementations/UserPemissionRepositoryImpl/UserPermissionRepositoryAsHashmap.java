package BGU.Group13B.backend.Repositories.Implementations.UserPemissionRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IUserPermissionRepository;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.permissions.ChangePermissionException;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.permissions.StorePermission;

import java.util.concurrent.ConcurrentHashMap;

public class UserPermissionRepositoryAsHashmap implements IUserPermissionRepository {
    private final ConcurrentHashMap<Integer, UserPermissions> integerUserPermissionConcurrentHashMap;

    public UserPermissionRepositoryAsHashmap(){
        this.integerUserPermissionConcurrentHashMap = new ConcurrentHashMap<>();
    }

    @Override
    public UserPermissions getUserPermission(int userId){
        return integerUserPermissionConcurrentHashMap.get(userId);
    }

    @Override
    public void addUserPermission(int userId, UserPermissions userPermissions){
        integerUserPermissionConcurrentHashMap.put(userId, userPermissions);
    }

    @Override
    public boolean isUserPermissionsExists(int userId){
        if(!integerUserPermissionConcurrentHashMap.containsKey(userId))
            return false;
        return integerUserPermissionConcurrentHashMap.get(userId).isUserPermissionsExists();
    }

    @Override
    public void deletePermissions(int adminId, int userId) throws NoPermissionException, ChangePermissionException {
        integerUserPermissionConcurrentHashMap.get(userId).deletePermissions(adminId, userId);
        integerUserPermissionConcurrentHashMap.remove(userId);
    }

}
