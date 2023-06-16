package BGU.Group13B.backend.storePackage.permissions;

import BGU.Group13B.backend.Repositories.Interfaces.IUserPermissionRepository;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.User.UserPermissions.StoreRole;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class StorePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int founderId;

    @Transient
    private HashMap<Integer/*permissionNumber*/, Set<String>> userStoreFunctionPermissions;//THIS ONE INCLUDE WHAT EACH ADDITIONAL ROLE INCLUDES
    @Transient
    private  HashMap<StoreRole, Set<String>> defaultStoreRoleFunctionalities;
    @Transient
    private  HashMap<Integer/*userId*/, StoreRole> userToStoreRole;
    @Transient
    private  HashMap<Integer/*got appointed by*/, Set<Integer>/*appointee*/> appointedOwnersMap;
    @Transient
    private  HashMap<Integer/*userId*/, Set<UserPermissions.IndividualPermission>> userToIndividualPermissions;//THIS ONE EXPLAINS THE "ADDITIONAL ROLES" OF EACH MANAGER
    @Transient
    private  IUserPermissionRepository userPermissionRepository = SingletonCollection.getUserPermissionRepository();

    public StorePermission(int founderId) {
        this.founderId= founderId;
        userStoreFunctionPermissions = new HashMap<>();
        defaultStoreRoleFunctionalities = new HashMap<>();
        initDefaultStoreRoleFunctionalities();
        userToStoreRole = new HashMap<>();
        appointedOwnersMap = new HashMap<>();
        userToStoreRole.put(founderId, StoreRole.FOUNDER);
        userToIndividualPermissions = new HashMap<>();
        Set<UserPermissions.IndividualPermission> founderPermissions = new HashSet<>();
        founderPermissions.add(UserPermissions.IndividualPermission.STOCK);
        founderPermissions.add(UserPermissions.IndividualPermission.MESSAGES);
        founderPermissions.add(UserPermissions.IndividualPermission.POLICIES);
        founderPermissions.add(UserPermissions.IndividualPermission.AUCTION);
        founderPermissions.add(UserPermissions.IndividualPermission.WORKERS_INFO);
        founderPermissions.add(UserPermissions.IndividualPermission.HISTORY);
        founderPermissions.add(UserPermissions.IndividualPermission.STAFF);
        founderPermissions.add(UserPermissions.IndividualPermission.FONLY);
        userToIndividualPermissions.put(founderId, founderPermissions);
        initIndividualPermissionsFunctionalities();
    }

    public StorePermission() {
        this.founderId= 0;
        userStoreFunctionPermissions = new HashMap<>();
        defaultStoreRoleFunctionalities = new HashMap<>();
        userToStoreRole = new HashMap<>();
        appointedOwnersMap = new HashMap<>();
        userToIndividualPermissions = new HashMap<>();
    }



    private void initDefaultStoreRoleFunctionalities() {
        /*
         * TODO: complete the default permissions for each role
         * */
        HashSet<String> storeManagerFunctions = new HashSet<>();
        HashSet<String> storeOwnerFunctions = new HashSet<>();
        HashSet<String> storeFounderFunctions = new HashSet<>();
        var storeClass = Store.class;
        for (Method method : storeClass.getMethods()) {
            if (method.isAnnotationPresent(DefaultManagerFunctionality.class))
                storeManagerFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(DefaultOwnerFunctionality.class))
                storeOwnerFunctions.add(method.getName());

            if (method.isAnnotationPresent(DefaultFounderFunctionality.class))
                storeFounderFunctions.add(method.getName());
        }
        defaultStoreRoleFunctionalities.put(StoreRole.MANAGER, storeManagerFunctions);
        defaultStoreRoleFunctionalities.put(StoreRole.OWNER, storeOwnerFunctions);
        defaultStoreRoleFunctionalities.put(StoreRole.FOUNDER, storeFounderFunctions);
    }

    private void initIndividualPermissionsFunctionalities() {
        HashSet<String> stockFunctions = new HashSet<>();
        HashSet<String> messagesFunctions = new HashSet<>();
        HashSet<String> policiesFunctions = new HashSet<>();
        HashSet<String> auctionFunctions = new HashSet<>();
        HashSet<String> infoFunctions = new HashSet<>();
        HashSet<String> historyFunctions = new HashSet<>();
        HashSet<String> staffFunctions = new HashSet<>();
        HashSet<String> founderFunctions = new HashSet<>();
        var storeClass = Store.class;
        for (Method method : storeClass.getMethods()) {
            if (method.isAnnotationPresent(StockPermission.class))
                stockFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(MessagesPermission.class))
                messagesFunctions.add(method.getName());

            if (method.isAnnotationPresent(PoliciesPermission.class))
                policiesFunctions.add(method.getName());

            if (method.isAnnotationPresent(AuctionPermission.class))
                auctionFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(WorkersInfoPermission.class))
                infoFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(HistoryPermission.class))
                historyFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(StaffPermission.class))
                staffFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(DefaultFounderFunctionality.class))
                founderFunctions.add(method.getName());
        }
        userStoreFunctionPermissions.put(0, stockFunctions);
        userStoreFunctionPermissions.put(1, messagesFunctions);
        userStoreFunctionPermissions.put(2, policiesFunctions);
        userStoreFunctionPermissions.put(3, auctionFunctions);
        userStoreFunctionPermissions.put(4, infoFunctions);
        userStoreFunctionPermissions.put(5, historyFunctions);
        userStoreFunctionPermissions.put(6, staffFunctions);
        userStoreFunctionPermissions.put(7, founderFunctions);
    }

    private static String getFunctionName(Method method) {
        var name = method.getName();
        return getFunctionName(name);
    }

    public static String getFunctionName(String methodName) {
        var index = methodName.lastIndexOf(".");
        return methodName.substring(index + 1);
    }

    public boolean checkPermission(int userId, boolean isStoreHidden) throws NoPermissionException {
        //check if the user is admin
        if(hasAdminPermission(userId)){
            return true;
        }

        String fullMethodCallName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String storeFunctionName = getFunctionName(fullMethodCallName);

        //will throw exception if the store is hidden and the user doesn't have permission to view it
        //I did it here in order to avoid future bugs that can happen if we forget to check it in every function
        validateStoreVisibility(userId, isStoreHidden);

        //check for the user's permissions
        if (userToIndividualPermissions.containsKey(userId)) {
            boolean found = false;
            Set<UserPermissions.IndividualPermission> individualPermissions = userToIndividualPermissions.get(userId);
            for (UserPermissions.IndividualPermission ip: individualPermissions) {
                if(userStoreFunctionPermissions.get(ip.ordinal()).contains(storeFunctionName))
                    found = true;
            }
            return found;
        } /*else if (userToStoreRole.containsKey(userId)) {
            return defaultStoreRoleFunctionalities.get(userToStoreRole.get(userId)).contains(storeFunctionName);
        }*/
        return false;
    }

    private boolean hasAdminPermission(int userId)  {
        return getUserPermissionRepository().getUserPermission(userId).getUserPermissionStatus()
                .equals(UserPermissions.UserPermissionStatus.ADMIN);
    }

    public void validateStoreVisibility(int userId, boolean isStoreHidden) throws NoPermissionException {
        if (isStoreHidden && !this.hasAccessWhileHidden(userId))
            throw new NoPermissionException("This store does not exist");
    }

    public boolean hasAccessWhileHidden(int userId) {
        return userToStoreRole.containsKey(userId) &&
                (userToStoreRole.get(userId) == StoreRole.FOUNDER || userToStoreRole.get(userId) == StoreRole.OWNER);
    }

    public Set<Integer/*userId*/> getAllUsersWithPermission(String storeFunctionName) {
        Set<Integer> usersWithPermission = new HashSet<>();
        //all users with roles that have that permission
        for(var key : defaultStoreRoleFunctionalities.keySet()){
            if(defaultStoreRoleFunctionalities.get(key).contains(storeFunctionName)){
                for (Map.Entry<Integer, StoreRole> entry : userToStoreRole.entrySet()) {
                    if (entry.getValue() == key) {
                        usersWithPermission.add(entry.getKey());
                    }
                }
            }
        }
        //all users who got this permission individually
        Set<Integer> permissionNumber = new HashSet<>();
        for (var entry : userStoreFunctionPermissions.entrySet()) {
            if (entry.getValue().contains(storeFunctionName)) {
                permissionNumber.add(entry.getKey());
            }
        }
        for (var entry : userToIndividualPermissions.entrySet()) {
            for(int pn : permissionNumber){
                UserPermissions.IndividualPermission permission = UserPermissions.IndividualPermission.values()[pn];
                if (entry.getValue().contains(permission)) {
                    usersWithPermission.add(entry.getKey());
                }
            }
        }
        return usersWithPermission;
    }

    public void addOwnerPermission(int newOwnerId, int appointerId) throws ChangePermissionException {
        //check if not already in that role
        StoreRole userRole = userToStoreRole.get(newOwnerId);
        if (userRole == StoreRole.FOUNDER || userRole == StoreRole.OWNER) {
            throw new ChangePermissionException("Cannot grant owner title to a user who is already an owner");
        } else {
            userToStoreRole.put(newOwnerId, StoreRole.OWNER);
            if(userToIndividualPermissions.containsKey(newOwnerId)){
                userToIndividualPermissions.get(newOwnerId).clear();
                userToIndividualPermissions.get(newOwnerId).add(UserPermissions.IndividualPermission.STOCK);
                userToIndividualPermissions.get(newOwnerId).add(UserPermissions.IndividualPermission.MESSAGES);
                userToIndividualPermissions.get(newOwnerId).add(UserPermissions.IndividualPermission.HISTORY);
                userToIndividualPermissions.get(newOwnerId).add(UserPermissions.IndividualPermission.AUCTION);
                userToIndividualPermissions.get(newOwnerId).add(UserPermissions.IndividualPermission.WORKERS_INFO);
                userToIndividualPermissions.get(newOwnerId).add(UserPermissions.IndividualPermission.POLICIES);
                userToIndividualPermissions.get(newOwnerId).add(UserPermissions.IndividualPermission.STAFF);
            }
            else{
                Set<UserPermissions.IndividualPermission> newSet = new HashSet<>();
                newSet.add(UserPermissions.IndividualPermission.STOCK);
                newSet.add(UserPermissions.IndividualPermission.MESSAGES);
                newSet.add(UserPermissions.IndividualPermission.HISTORY);
                newSet.add(UserPermissions.IndividualPermission.AUCTION);
                newSet.add(UserPermissions.IndividualPermission.WORKERS_INFO);
                newSet.add(UserPermissions.IndividualPermission.POLICIES);
                newSet.add(UserPermissions.IndividualPermission.STAFF);
                userToIndividualPermissions.put(newOwnerId, newSet);
            }
            // Check if the appointerId is already present in the appointedOwnersMap
            Set<Integer> appointees = appointedOwnersMap.get(appointerId);

            // If appointerId is not present, create a new set and add it to the map
            if (appointees == null) {
                appointees = new HashSet<>();
                appointedOwnersMap.put(appointerId, appointees);
            }

            // Add the newOwnerId to the set of appointees
            appointees.add(newOwnerId);
        }
    }

    public Set<Integer> removeOwnerPermission(int removeOwnerId, int removerId, boolean removeManager) throws ChangePermissionException {
        Set<Integer> removeUsersId = new HashSet<>();
        StoreRole removeOwnerRole = userToStoreRole.get(removeOwnerId);

        if (!(removeOwnerRole == StoreRole.OWNER || (removeManager && removeOwnerRole == StoreRole.MANAGER))) {
            throw new ChangePermissionException("Cannot remove owner title from a user who is not an owner");
        } else if (appointedOwnersMap.getOrDefault(removerId, Collections.emptySet()).contains(removeOwnerId)) {
            Set<Integer> underlingsToRemove = appointedOwnersMap.get(removeOwnerId);
            if (underlingsToRemove != null) {
                for (Integer underlingId : underlingsToRemove) {
                    removeUsersId.add(underlingId);
                    Set<Integer> recursiveFiringList = removeOwnerPermission(underlingId, removeOwnerId, true);
                    removeUsersId.addAll(recursiveFiringList);
                }
            }
            appointedOwnersMap.get(removerId).remove(removeOwnerId);
            userToStoreRole.remove(removeOwnerId);
            removeUsersId.add(removeOwnerId);
            if(userToIndividualPermissions.containsKey(removeOwnerId))
                userToIndividualPermissions.get(removeOwnerId).clear();
        } else {
            throw new ChangePermissionException("Cannot remove owner title from an owner you didn't appoint");
        }
        return removeUsersId;
    }

    public void addManagerPermission(int newManagerId, int appointerId) throws ChangePermissionException {
        //check if not already in that role
        StoreRole userRole = userToStoreRole.get(newManagerId);
        if (userRole == StoreRole.FOUNDER || userRole == StoreRole.OWNER || userRole == StoreRole.MANAGER) {
            throw new ChangePermissionException("Cannot grant manager title to a user who is aleady a manager");
        } else {
            userToStoreRole.put(newManagerId, StoreRole.MANAGER);
            if(userToIndividualPermissions.containsKey(newManagerId)){
                userToIndividualPermissions.get(newManagerId).clear();
                userToIndividualPermissions.get(newManagerId).add(UserPermissions.IndividualPermission.HISTORY);
            }
            else{
                Set<UserPermissions.IndividualPermission> newSet = new HashSet<>();
                newSet.add(UserPermissions.IndividualPermission.HISTORY);
                userToIndividualPermissions.put(newManagerId, newSet);
            }
            // Check if the appointerId is already present in the appointedOwnersMap
            Set<Integer> appointees = appointedOwnersMap.get(appointerId);

            // If appointerId is not present, create a new set and add it to the map
            if (appointees == null) {
                appointees = new HashSet<>();
                appointedOwnersMap.put(appointerId, appointees);
            }

            // Add the newOwnerId to the set of appointees
            appointees.add(newManagerId);
        }
    }

    public void removeManagerPermission(int removeManagerId, int removerId) throws ChangePermissionException {
        StoreRole removeManagerRole = userToStoreRole.get(removeManagerId);

        if (removeManagerRole != StoreRole.MANAGER) {
            throw new ChangePermissionException("Cannot remove manager title from a user who is not a manager");
        } else if (appointedOwnersMap.getOrDefault(removerId, Collections.emptySet()).contains(removeManagerId)) {
            appointedOwnersMap.get(removerId).remove(removeManagerId);
            userToStoreRole.remove(removeManagerId);
            if(userToIndividualPermissions.containsKey(removeManagerId))
                userToIndividualPermissions.get(removeManagerId).clear();
        } else {
            throw new ChangePermissionException("Cannot remove manager title from an owner you didn't appoint");
        }
    }

    public void addIndividualPermission(int userId, UserPermissions.IndividualPermission individualPermission) throws ChangePermissionException {
        StoreRole userRole = userToStoreRole.get(userId);
        if (userRole != StoreRole.MANAGER)
            throw new ChangePermissionException("Cannot grant an individual permission to a user who is no a manager");
        if(userToIndividualPermissions.containsKey(userId))
            userToIndividualPermissions.get(userId).add(individualPermission);
        else{
            Set<UserPermissions.IndividualPermission> newSet = new HashSet<>();
            newSet.add(individualPermission);
            userToIndividualPermissions.put(userId, newSet);
        }
    }

    public void removeIndividualPermission(int userId, UserPermissions.IndividualPermission individualPermission) throws ChangePermissionException {
        StoreRole userRole = userToStoreRole.get(userId);
        if (userRole != StoreRole.MANAGER)
            throw new ChangePermissionException("Cannot grant an individual permission to a user who is no a manager");
        if(userToIndividualPermissions.containsKey(userId))
            userToIndividualPermissions.get(userId).remove(individualPermission);
    }

    public List<WorkerCard> getWorkersInfo() {
        List<WorkerCard> workerCards = new ArrayList<>();
        for (Map.Entry<Integer, StoreRole> entry : userToStoreRole.entrySet()) {
            Integer userId = entry.getKey();
            StoreRole role = entry.getValue();
            Set<UserPermissions.IndividualPermission> userPermissions = userToIndividualPermissions.get(userId);
            WorkerCard workerCard = new WorkerCard(userId, role, userPermissions);
            workerCards.add(workerCard);
        }
        return workerCards;
    }

    public StoreRole getUserRole(int userId) {
        return userToStoreRole.get(userId);
    }

    public void clearForTest() {
        userToStoreRole.clear();
        appointedOwnersMap.clear();
        userToIndividualPermissions.clear();
    }

    public int getStoreFounder() {
       return userToStoreRole.entrySet().stream()
                .filter(entry -> entry.getValue() == StoreRole.FOUNDER)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    public List<Integer> getStoreOwners(){
        System.out.println("HERE");
        return userToStoreRole.entrySet().stream()
                .filter(entry -> entry.getValue() == StoreRole.OWNER)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public HashMap<Integer/*userId*/, Set<UserPermissions.IndividualPermission>> getUserToIndividualPermissions(){
        return userToIndividualPermissions;
    }
    public List<Integer> getAllUsersWithIndividualPermission(UserPermissions.IndividualPermission individualPermission){
        List<Integer> users = new ArrayList<>();
        for(Map.Entry<Integer, Set<UserPermissions.IndividualPermission>> entry : userToIndividualPermissions.entrySet()){
            if(entry.getValue().contains(individualPermission))
                users.add(entry.getKey());
        }
        return users;
    }


    //getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFounderId() {
        return founderId;
    }

    public void setFounderId(int founderId) {
        this.founderId = founderId;
    }

    public HashMap<Integer, Set<String>> getUserStoreFunctionPermissions() {
        return userStoreFunctionPermissions;
    }

    public void setUserStoreFunctionPermissions(HashMap<Integer, Set<String>> userStoreFunctionPermissions) {
        this.userStoreFunctionPermissions = userStoreFunctionPermissions;
    }

    public HashMap<StoreRole, Set<String>> getDefaultStoreRoleFunctionalities() {
        return defaultStoreRoleFunctionalities;
    }

    public void setDefaultStoreRoleFunctionalities(HashMap<StoreRole, Set<String>> defaultStoreRoleFunctionalities) {
        this.defaultStoreRoleFunctionalities = defaultStoreRoleFunctionalities;
    }

    public HashMap<Integer, StoreRole> getUserToStoreRole() {
        return userToStoreRole;
    }

    public void setUserToStoreRole(HashMap<Integer, StoreRole> userToStoreRole) {
        this.userToStoreRole = userToStoreRole;
    }

    public HashMap<Integer, Set<Integer>> getAppointedOwnersMap() {
        return appointedOwnersMap;
    }

    public void setAppointedOwnersMap(HashMap<Integer, Set<Integer>> appointedOwnersMap) {
        this.appointedOwnersMap = appointedOwnersMap;
    }

    public void setUserToIndividualPermissions(HashMap<Integer, Set<UserPermissions.IndividualPermission>> userToIndividualPermissions) {
        this.userToIndividualPermissions = userToIndividualPermissions;
    }

    public IUserPermissionRepository getUserPermissionRepository() {
        userPermissionRepository = SingletonCollection.getUserPermissionRepository();
        return userPermissionRepository;
    }

    public void setUserPermissionRepository(IUserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }
}