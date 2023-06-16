package BGU.Group13B.backend.User;


import BGU.Group13B.backend.Pair;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class UserPermissions {

    @Id
    private int userId;

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
        STOCK,
        MESSAGES,
        POLICIES,
        AUCTION,
        WORKERS_INFO,
        HISTORY,
        STAFF,
        FONLY
    }



    private UserPermissionStatus userPermissionStatus;
    @ElementCollection
    @CollectionTable(name = "UserPermissions_StoreRole",
            joinColumns = {@JoinColumn(name = "UserPermissions_id", referencedColumnName = "userId")})
    @MapKeyColumn(name = "storeId")
    @Column(name = "storeRole")
    private Map<Integer/*storeId*/, StoreRole> userStoreRole;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "UserPermissions_IndividualPermissionSet",
            joinColumns = {@JoinColumn(name = "UserPermissions_id", referencedColumnName = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "IndividualPermissionSet_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "storeId")
    private Map<Integer/*storeId*/, IndividualPermissionSet> userIndividualPermission;

    public UserPermissions(int userId) {
        this.userPermissionStatus = UserPermissionStatus.GUEST;
        this.userStoreRole = new HashMap<>();
        this. userIndividualPermission = new HashMap<>();
        this.userId= userId;
    }
    public UserPermissions() {
        this.userPermissionStatus = UserPermissionStatus.GUEST;
        this.userStoreRole = new HashMap<>();
        this. userIndividualPermission = new HashMap<>();
        this.userId=0;
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
        if (userIndividualPermission.containsKey(storeId)){
            userIndividualPermission.get(storeId).add(individualPermission);
        }
        else{
            IndividualPermissionSet ns = new IndividualPermissionSet();
            ns.add(individualPermission);
            userIndividualPermission.put(storeId, ns);
        }
    }

    public void deleteIndividualPermission(int storeId, IndividualPermission individualPermission){
        userIndividualPermission.get(storeId).remove(individualPermission);
    }



    public void removeAllIndividualPermissions(int storeId){
        userIndividualPermission.get(storeId).clear();
    }

    public void clearForTest(){
        userStoreRole.clear();
        userIndividualPermission.clear();
    }
    //getters and setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Map<Integer, StoreRole> getUserStoreRole() {
        return userStoreRole;
    }

    public void setUserStoreRole(Map<Integer, StoreRole> userStoreRole) {
        this.userStoreRole = userStoreRole;
    }

    // getters and setters

    public Map<Integer, IndividualPermissionSet> getUserIndividualPermission() {
        return userIndividualPermission;
    }

    public void setUserIndividualPermission(Map<Integer, IndividualPermissionSet> userIndividualPermission) {
        this.userIndividualPermission = userIndividualPermission;
    }

}

