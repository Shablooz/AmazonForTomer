package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.backend.storePackage.Store;

public interface IStoreRepository {
    Store getStore(int storeId);

    //(#24) open store - requirement 3.2
    void addStore(int founderId, String storeName, String category);
}
