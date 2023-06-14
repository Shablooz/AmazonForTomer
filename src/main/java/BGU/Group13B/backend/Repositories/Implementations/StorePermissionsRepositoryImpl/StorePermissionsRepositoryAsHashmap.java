package BGU.Group13B.backend.Repositories.Implementations.StorePermissionsRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl.StoreRepoService;
import BGU.Group13B.backend.Repositories.Interfaces.IStorePermissionsRepository;
import BGU.Group13B.backend.storePackage.permissions.StorePermission;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.concurrent.ConcurrentHashMap;

@Entity
public class StorePermissionsRepositoryAsHashmap implements IStorePermissionsRepository {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Transient
    private final ConcurrentHashMap<Integer, StorePermission> integerStorePermissionConcurrentHashMap;

    @Transient
    private boolean saveMode;

    public StorePermissionsRepositoryAsHashmap(){
        this.integerStorePermissionConcurrentHashMap = new ConcurrentHashMap<>();
        this.saveMode = true;
    }
    public StorePermissionsRepositoryAsHashmap(boolean saveMode){
        this.integerStorePermissionConcurrentHashMap = new ConcurrentHashMap<>();
        this.saveMode = saveMode;
    }

    @Override
    public StorePermission getStorePermission(int storeId){
        return integerStorePermissionConcurrentHashMap.get(storeId);
    }

    @Override
    public void addStorePermission(int storeId, StorePermission storePermission){
        integerStorePermissionConcurrentHashMap.put(storeId, storePermission);
        save();
    }

    @Override
    public void reset() {
        integerStorePermissionConcurrentHashMap.clear();
        save();
    }

    @Override
    public void deleteStorePermissions(int storeId) {
        integerStorePermissionConcurrentHashMap.remove(storeId);
        save();
    }

    public void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(StorePermissionsRepositoryAsHashmapService.class).save(this);
    }

    public void setSaveMode(boolean saved) {
        this.saveMode = saved;
    }

    @Override
    public boolean getSaveMode() {
        return false;
    }
}
