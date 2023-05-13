package BGU.Group13B.backend.storePackage.permissions;

import BGU.Group13B.backend.User.UserPermissions.StoreRole;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.backend.storePackage.WorkerCard;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class StorePermission {

    private final HashMap<Integer/*userID*/, Set<String>> userStoreFunctionPermissions;
    private final HashMap<StoreRole, Set<String>> defaultStoreRoleFunctionalities;
    private final HashMap<Integer/*userId*/, StoreRole> userToStoreRole;
    private final HashMap<Integer/*got appointed by*/, Set<Integer>/*appointee*/> appointedOwnersMap;

    public StorePermission(int founderId) {
        userStoreFunctionPermissions = new HashMap<>();
        defaultStoreRoleFunctionalities = new HashMap<>();
        initDefaultStoreRoleFunctionalities();
        userToStoreRole = new HashMap<>();
        appointedOwnersMap = new HashMap<>();
        userToStoreRole.put(founderId, StoreRole.FOUNDER);
        userStoreFunctionPermissions.put(founderId, defaultStoreRoleFunctionalities.get(StoreRole.FOUNDER));
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
            //the founder can do every function
            storeFounderFunctions.add(method.getName());
        }
        defaultStoreRoleFunctionalities.put(StoreRole.MANAGER, storeManagerFunctions);
        defaultStoreRoleFunctionalities.put(StoreRole.OWNER, storeOwnerFunctions);
        defaultStoreRoleFunctionalities.put(StoreRole.FOUNDER, storeFounderFunctions);
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
        String fullMethodCallName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String storeFunctionName = getFunctionName(fullMethodCallName);

        //will throw exception if the store is hidden and the user doesn't have permission to view it
        //I did it here in order to avoid future bugs that can happen if we forget to check it in every function
        validateStoreVisibility(userId, isStoreHidden);

        //check for the user's permissions
        if(userStoreFunctionPermissions.containsKey(userId)){
            return userStoreFunctionPermissions.get(userId).contains(storeFunctionName);
        }
        else if(userToStoreRole.containsKey(userId)){
            return defaultStoreRoleFunctionalities.get(userToStoreRole.get(userId)).contains(storeFunctionName);
        }
        return false;
    }

    public void validateStoreVisibility(int userId, boolean isStoreHidden) throws NoPermissionException{
        if(isStoreHidden && !this.hasAccessWhileHidden(userId))
            throw new NoPermissionException("This store does not exist");
    }

    public boolean hasAccessWhileHidden(int userId){
        return userToStoreRole.containsKey(userId) &&
                (userToStoreRole.get(userId) == StoreRole.FOUNDER || userToStoreRole.get(userId) == StoreRole.OWNER);
    }

    public Set<Integer/*userId*/> getAllUsersWithPermission(String storeFunctionName){
        Set<Integer> usersWithPermission = new HashSet<>();
        for (var entry : userStoreFunctionPermissions.entrySet()) {
            if(entry.getValue().contains(storeFunctionName)){
                usersWithPermission.add(entry.getKey());
            }
        }
        return usersWithPermission;
    }

    public void addOwnerPermission(int newOwnerId, int appointerId) throws ChangePermissionException {
        //check if not already in that role
        StoreRole userRole = userToStoreRole.get(newOwnerId);
        if(userRole == StoreRole.FOUNDER || userRole == StoreRole.OWNER){
            throw new ChangePermissionException("Cannot grant owner title to a user who is already an owner");
        }
        else {
            userToStoreRole.put(newOwnerId, StoreRole.OWNER);
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

    public List<Integer> removeOwnerPermission(int removeOwnerId, int removerId, boolean removeManager) throws ChangePermissionException {
        List<Integer> removeUsersId = new ArrayList<>();
        StoreRole removeOwnerRole = userToStoreRole.get(removeOwnerId);

        if (!(removeOwnerRole == StoreRole.OWNER || (removeManager && removeOwnerRole == StoreRole.MANAGER))) {
            throw new ChangePermissionException("Cannot remove owner title from a user who is not an owner");
        }
        else if(appointedOwnersMap.getOrDefault(removerId, Collections.emptySet()).contains(removeOwnerId)){
            Set<Integer> underlingsToRemove = appointedOwnersMap.get(removeOwnerId);
            if(underlingsToRemove != null){
                for (Integer underlingId : underlingsToRemove) {
                    removeUsersId.add(underlingId);
                    List<Integer> recursiveFiringList = removeOwnerPermission(underlingId, removeOwnerId, true);
                    removeUsersId.addAll(recursiveFiringList);
                }
            }
            appointedOwnersMap.get(removerId).remove(removeOwnerId);
            userToStoreRole.remove(removeOwnerId);
            removeUsersId.add(removeOwnerId);
        }
        else {
            throw new ChangePermissionException("Cannot remove owner title from an owner you didn't appoint");
        }
        return removeUsersId;
    }

    public void addManagerPermission(int newManagerId, int appointerId) throws ChangePermissionException {
        //check if not already in that role
        StoreRole userRole = userToStoreRole.get(newManagerId);
        if(userRole == StoreRole.FOUNDER || userRole == StoreRole.OWNER || userRole == StoreRole.MANAGER){
            throw new ChangePermissionException("Cannot grant manager title to a user who is aleady a manager");
        }
        else {
            userToStoreRole.put(newManagerId, StoreRole.MANAGER);
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
        }
        else if(appointedOwnersMap.getOrDefault(removerId, Collections.emptySet()).contains(removeManagerId)){
            appointedOwnersMap.get(removerId).remove(removeManagerId);
            userToStoreRole.remove(removeManagerId);
        }
        else {
            throw new ChangePermissionException("Cannot remove manager title from an owner you didn't appoint");
        }
    }

    public List<WorkerCard> getWorkersInfo(){
        List<WorkerCard> workerCards = new ArrayList<>();
        for (Map.Entry<Integer, StoreRole> entry : userToStoreRole.entrySet()) {
            List<String> userPermissions = new ArrayList<>();
            //TODO: add user permissions
            Integer userId = entry.getKey();
            StoreRole role = entry.getValue();
            WorkerCard workerCard = new WorkerCard(userId, role,userPermissions);
            workerCards.add(workerCard);
        }
        return workerCards;
    }

    public StoreRole getUserPermission(int userId){
        return userToStoreRole.get(userId);
    }

    public void clearForTest(){
        userToStoreRole.clear();
        appointedOwnersMap.clear();
    }
    public int getStoreOwner(int storeId){
        throw new UnsupportedOperationException();
        //return storeToOwner.get(storeId);/*fixme*/
    }
}