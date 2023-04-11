package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Store;

import java.util.Optional;

public interface IStoreRepository {
    Optional<Store> getStore(int storeId);
}
