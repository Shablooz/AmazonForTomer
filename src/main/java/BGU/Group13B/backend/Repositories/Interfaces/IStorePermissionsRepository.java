package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.permissions.StorePermission;

public interface IStorePermissionsRepository {
    StorePermission getStorePermission(int storeId);

    void addStorePermission(int storeId, StorePermission storePermission);

    void reset();

    void deleteStorePermissions(int storeId);

    public void setSaveMode(boolean saved);
    public boolean getSaveMode();
}
