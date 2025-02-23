package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;

import java.util.List;
import java.util.Set;

public interface IStoreRepository {
    Store getStore(int storeId);

    //(#24) open store - requirement 3.2
    int addStore(int founderId, String storeName, String category);

    //for testing
    void removeStore(int storeId);

    void reset();

    List<Store> getAllStoresTheUserCanView(int userId);

    public void setSaveMode(boolean saved);

    void removeMemberStores(int adminId, int userId) throws NoPermissionException;

    void save();

    Set<Integer> getAllStoresId();

    boolean getSaveMode();
}
