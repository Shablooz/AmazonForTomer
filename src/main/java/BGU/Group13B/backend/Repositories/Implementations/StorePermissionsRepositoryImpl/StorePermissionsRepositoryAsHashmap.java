package BGU.Group13B.backend.Repositories.Implementations.StorePermissionsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStorePermissionsRepository;
import BGU.Group13B.backend.storePackage.permissions.StorePermission;

import java.util.concurrent.ConcurrentHashMap;

public class StorePermissionsRepositoryAsHashmap implements IStorePermissionsRepository {
    private final ConcurrentHashMap<Integer, StorePermission> integerStorePermissionConcurrentHashMap;

    public StorePermissionsRepositoryAsHashmap(){
        this.integerStorePermissionConcurrentHashMap = new ConcurrentHashMap<>();
    }

    @Override
    public StorePermission getStorePermission(int storeId){
        return integerStorePermissionConcurrentHashMap.get(storeId);
    }

    @Override
    public void addStorePermission(int storeId, StorePermission storePermission){
        integerStorePermissionConcurrentHashMap.put(storeId, storePermission);
    }

    @Override
    public void reset() {
        integerStorePermissionConcurrentHashMap.clear();
    }
}
