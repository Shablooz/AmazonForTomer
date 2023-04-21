package BGU.Group13B.service.info;

import BGU.Group13B.backend.storePackage.Store;

/*
 * this class stores the information of a store
 * it will be used to send the information to the UI
 * it can also be used for tests
 */
public class StoreInfo {
    private final int storeId;
    private final String storeName;
    private final String category;
    private final float storeScore;

    public StoreInfo(Store store){
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        category = store.getCategory();
        storeScore = store.getStoreScore();
    }

    public int getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getCategory() {
        return category;
    }

    public float getStoreScore() {
        return storeScore;
    }

}
