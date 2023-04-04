package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.User.UserPermissions.StoreRole;


import java.util.HashMap;
import java.util.Set;

public class StorePermission {

    public enum StoreFunction {
        ADD_PRODUCT;
        /*to complete!!*/
    }


    private final HashMap<Integer/*userID*/, Set<StoreFunction>> userStoreFunctionPermissions;

    private final HashMap<StoreRole, Set<StoreFunction>> defaultStoreRoleFunctionalities;
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
        defaultStoreRoleFunctionalities.put(StoreRole.OWNER, Set.of(StoreFunction.ADD_PRODUCT));
        defaultStoreRoleFunctionalities.put(StoreRole.MANAGER, Set.of(StoreFunction.ADD_PRODUCT));
    }
    /*
    * This is the template do for every function in Store
    * */
    public boolean checkAddProductPermission(int userId) {
        return checkPermission(userId, StoreFunction.ADD_PRODUCT);
    }

    private boolean checkPermission(int userId, StoreFunction storeFunction) {
        if (userStoreFunctionPermissions.containsKey(userId)) {
            return userStoreFunctionPermissions.get(userId).contains(storeFunction);
        }
        return false;
<<<<<<< HEAD


=======
>>>>>>> origin/create_system_infrastructure
    }
}
