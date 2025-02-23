package BGU.Group13B.backend.User;


import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IStorePermissionsRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.permissions.ChangePermissionException;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;
import jakartac.Cache.Cache;

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
        FONLY,
        STATS
    }

    public enum PopulationStatus{
        ADMIN,
        OWNER,
        MANAGER_NOT_OWNER,
        REGULAR_MEMBER, //if the user is a member but not a manager or an owner
        GUEST
    }



    private UserPermissionStatus userPermissionStatus;

    @Cache(policy = Cache.PolicyType.LRU)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "UserPermissions_StoreRole",
            joinColumns = {@JoinColumn(name = "UserPermissions_id", referencedColumnName = "userId")})
    @MapKeyColumn(name = "storeId")
    @Column(name = "storeRole")
    private Map<Integer/*storeId*/, StoreRole> userStoreRole;

    @Cache(policy = Cache.PolicyType.LRU)
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
        save();
    }
    public Set<IndividualPermission> getIndividualPermissions(int storeId){
        return userIndividualPermission.get(storeId).getIndividualPermissions();
    }

    public void register(int id) {
        this.setUserPermissionStatus(UserPermissionStatus.MEMBER);
        if(id == 1) {
            this.setUserPermissionStatus(UserPermissionStatus.ADMIN);
        }
    }

    public void updateRoleInStore(int storeId, StoreRole storeRole){
        userStoreRole.put(storeId, storeRole);
        save();
    }

    public void deletePermission(int storeId){
        userStoreRole.remove(storeId);
        save();
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
        save();
    }

    public void deleteIndividualPermission(int storeId, IndividualPermission individualPermission){
        userIndividualPermission.get(storeId).remove(individualPermission);
        save();
    }



    public void removeAllIndividualPermissions(int storeId){
        userIndividualPermission.get(storeId).clear();
        save();
    }

    public void clearForTest(){
        userStoreRole.clear();
        userIndividualPermission.clear();
    }


    public PopulationStatus getPopulationStatus(){
        if (userPermissionStatus == UserPermissionStatus.ADMIN)
            return PopulationStatus.ADMIN;

        if (userPermissionStatus == UserPermissionStatus.GUEST)
            return PopulationStatus.GUEST;

        if(userStoreRole.containsValue(StoreRole.FOUNDER) || userStoreRole.containsValue(StoreRole.OWNER))
            return PopulationStatus.OWNER;

        if(userStoreRole.containsValue(StoreRole.MANAGER))
            return PopulationStatus.MANAGER_NOT_OWNER;

        return PopulationStatus.REGULAR_MEMBER;
    }


    public void clearUserStorePermissions(int storeId) {
        userStoreRole.remove(storeId);
        userIndividualPermission.remove(storeId);
        save();
    }

    public boolean isUserPermissionsExists(){
       if (userStoreRole.isEmpty())
           return false;
       return true;
    }

    public void deletePermissions(int adminId, int userId) throws NoPermissionException, ChangePermissionException {
        IStoreRepository storeRepository = SingletonCollection.getStoreRepository();
        for (Integer storeId : userStoreRole.keySet()) {
            if (userStoreRole.get(storeId) == StoreRole.MANAGER) {
                storeRepository.getStore(storeId).removeManager(adminId, userId);
            }
            else if (userStoreRole.get(storeId) == StoreRole.OWNER) {
                storeRepository.getStore(storeId).removeOwner(adminId, userId);
            }
        }
        userStoreRole.clear();
        userIndividualPermission.clear();
        save();
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

    public void save(){
        SingletonCollection.getUserPermissionRepository().save();
    }

}

