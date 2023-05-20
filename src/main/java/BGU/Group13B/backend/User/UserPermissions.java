package BGU.Group13B.backend.User;


import BGU.Group13B.backend.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    public enum IndividualPermission{
        FUNCTION1,
        FUNCTION2,
        FUNCTION3,
    }

    private UserPermissionStatus userPermissionStatus;
    private HashMap<Integer/*storeId*/, StoreRole> userStoreRole;
    private HashMap<Integer/*storeId*/, Set<IndividualPermission>> userIndividualPermission;

    public UserPermissions() {
        this.userPermissionStatus = UserPermissionStatus.GUEST;
        this.userStoreRole = new HashMap<>();
        this. userIndividualPermission = new HashMap<>();
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

    public void addIndividualPermission(int storeId, IndividualPermission individualPermission){
        userIndividualPermission.get(storeId).add(individualPermission);
    }

    public void deleteIndividualPermission(int storeId, IndividualPermission individualPermission){
        userIndividualPermission.get(storeId).remove(individualPermission);
    }

    public Set<IndividualPermission> getIndividualPermissions(int storeId){
        return userIndividualPermission.get(storeId);
    }

    public void removeAllIndividualPermissions(int storeId){
        userIndividualPermission.get(storeId).clear();
    }

    public void clearForTest(){
        userStoreRole.clear();
        userIndividualPermission.clear();
    }
}

