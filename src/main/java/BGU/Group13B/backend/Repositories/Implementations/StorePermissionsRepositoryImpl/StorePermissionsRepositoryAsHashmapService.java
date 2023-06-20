package BGU.Group13B.backend.Repositories.Implementations.StorePermissionsRepositoryImpl;

import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorePermissionsRepositoryAsHashmapService {
    private StorePermissionsRepositoryAsHashmapJPA storePermissionsRepositoryAsHashmapJPA;

    @Autowired
    public StorePermissionsRepositoryAsHashmapService(StorePermissionsRepositoryAsHashmapJPA storePermissionsRepositoryAsHashmapJPA) {
        this.storePermissionsRepositoryAsHashmapJPA = storePermissionsRepositoryAsHashmapJPA;
    }

    public StorePermissionsRepositoryAsHashmapService(){

    }

    public void save(StorePermissionsRepositoryAsHashmap storePermissionsRepositoryAsHashmap){
        SingletonCollection.setStorePermissionRepository(storePermissionsRepositoryAsHashmapJPA.save(storePermissionsRepositoryAsHashmap));
    }

    public void delete(StorePermissionsRepositoryAsHashmap storePermissionsRepositoryAsHashmap){
        storePermissionsRepositoryAsHashmapJPA.delete(storePermissionsRepositoryAsHashmap);
    }

    public StorePermissionsRepositoryAsHashmap getStorePermissionsRepositoryAsHashmap() {
        return storePermissionsRepositoryAsHashmapJPA.findById(1).orElse(null);
    }
}
