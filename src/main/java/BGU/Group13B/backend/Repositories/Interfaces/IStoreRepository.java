package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Store;


public interface IStoreRepository {
    Store getStore(int storeId);
}
