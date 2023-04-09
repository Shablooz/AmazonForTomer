package BGU.Group13B.backend.storePackage.permissions;

import BGU.Group13B.backend.User.UserPermissions.StoreRole;
import BGU.Group13B.backend.storePackage.Store;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class StorePermission {

    private final Set<String> storeFunctions = new HashSet<>();

    private final HashMap<Integer/*userID*/, Set<String>> userStoreFunctionPermissions;

    private final HashMap<StoreRole, Set<String>> defaultStoreRoleFunctionalities;
    private final HashMap<Integer/*storeId*/, StoreRole> userToStoreRole;

    public StorePermission() {
        userStoreFunctionPermissions = new HashMap<>();
        defaultStoreRoleFunctionalities = new HashMap<>();
        initDefaultStoreRoleFunctionalities();
        userToStoreRole = new HashMap<>();
    }

    private void initDefaultStoreRoleFunctionalities() {
        /*
         * complete the default permissions for each role
         * */
        HashSet<String> storeManagerFunctions = new HashSet<>();
        HashSet<String> storeOwnerFunctions = new HashSet<>();
        var storeClass = Store.class;
        for (Method method : storeClass.getMethods()) {
            if (method.isAnnotationPresent(DefaultManagerFunctionality.class)) {
                storeManagerFunctions.add(getFunctionName(method));
            } else if (method.isAnnotationPresent(DefaultOwnerFunctionality.class)) {
                storeOwnerFunctions.add(method.getName());
                storeFunctions.add(getFunctionName(method));//think about it
            }
        }
        defaultStoreRoleFunctionalities.put(StoreRole.MANAGER, storeManagerFunctions);
        defaultStoreRoleFunctionalities.put(StoreRole.OWNER, storeOwnerFunctions);

    }

    private static String getFunctionName(Method method) {
        var name = method.getName();
        return getFunctionName(name);
    }

    public static String getFunctionName(String methodName) {
        var index = methodName.lastIndexOf(".");
        return methodName.substring(index + 1);
    }

    public boolean checkPermission(int userId) {
        String fullMethodCallName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String storeFunctionName = getFunctionName(fullMethodCallName);
        if (userStoreFunctionPermissions.containsKey(userId)) {
            return userStoreFunctionPermissions.get(userId).contains(storeFunctionName);
        }
        return false;
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

}
