package BGU.Group13B.backend.User;

import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UserPermissions {

    public List<Pair<Integer, String>> getStoresAndRoles() {
        List<Pair<Integer,String>> lst = new LinkedList<>();
        for (Integer storeId : userStoreRole.keySet()) {
            lst.add(Pair.of(storeId, userStoreRole.get(storeId).toString()));
        }
        return lst;
    }

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


    public void register(int id) {
        this.setUserPermissionStatus(UserPermissionStatus.MEMBER);
        if(id == 1) {
            this.setUserPermissionStatus(UserPermissionStatus.ADMIN);
        }
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

