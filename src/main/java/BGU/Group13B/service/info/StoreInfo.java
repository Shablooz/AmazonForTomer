package BGU.Group13B.service.info;

import BGU.Group13B.backend.storePackage.Store;

/*
 * this class stores the information of a store
 * it will be used to send the information to the UI
 * it can also be used for tests
 */

public record StoreInfo(int storeId, String storeName, String category, double storeScore) {

    public StoreInfo(Store store) {
        this(store.getStoreId(), store.getStoreName(), store.getCategory(), store.getStoreScore());
    }
}





