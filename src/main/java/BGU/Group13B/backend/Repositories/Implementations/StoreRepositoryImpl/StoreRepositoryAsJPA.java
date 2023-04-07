package BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public class StoreRepositoryAsJPA implements IStoreRepository {
    private final JpaRepository<Store, Integer> repo;

    public StoreRepositoryAsJPA(JpaRepository<Store, Integer> repo) {
        this.repo = repo;
    }


    @Override
    public Store getStore(int storeId) {
        return null;
    }
}
