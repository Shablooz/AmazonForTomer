package BGU.Group13B.backend.Repositories.Implementations.StoreRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.concurrent.atomic.AtomicInteger;

public class StoreRepositoryAsJPA implements IStoreRepository {
    private final JpaRepository<Store, Integer> repo;
    private final AtomicInteger storeIdCounter = new AtomicInteger(0);


    public StoreRepositoryAsJPA(JpaRepository<Store, Integer> repo) {
        this.repo = repo;
    }


    @Override
    public Store getStore(int storeId) {
        return null;
    }

    //(#24) open store - requirement 3.2
    @Override
    public void addStore(int founderId, String storeName, String category) {
        repo.save(new Store(storeIdCounter.getAndIncrement(), founderId, storeName, category));
    }
}
