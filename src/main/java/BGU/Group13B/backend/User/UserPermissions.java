package BGU.Group13B.backend.User;

import java.util.HashMap;

public class UserPermissions {

    public enum UserPermissionStatus {
        ADMIN,
        MEMBER,
        GUEST
    }

    public enum StoreRole {
        FOUNDER,
        OWNER,
        MANAGER,
    }

    private UserPermissionStatus userPermissionStatus;
    private HashMap<Integer/*storeId*/, StoreRole> userStoreRole;

    public UserPermissions() {
        this.userPermissionStatus = UserPermissionStatus.GUEST;
        this.userStoreRole = new HashMap<>();
    }

    public UserPermissionStatus getUserPermissionStatus() {
        return userPermissionStatus;
    }

    public void setUserPermissionStatus(UserPermissionStatus userPermissionStatus) {
        this.userPermissionStatus = userPermissionStatus;
    }


    public void register() {
        this.setUserPermissionStatus(UserPermissionStatus.MEMBER);
    }

    public void updateRoleInStore(int storeId, StoreRole storeRole){
        userStoreRole.put(storeId, storeRole);
    }

    public void deletePermission(int storeId){
        userStoreRole.remove(storeId);
    }

    public StoreRole getStoreRole(int storeId){
        return userStoreRole.get(storeId);
    }

    public void clearForTest(){
        userStoreRole.clear();
    }
}

