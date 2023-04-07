package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.BID;

public interface IBIDRepository {
    void addBID(int userId, int productId, double newProductPrice, int amount);
    void removeBID(int userId, int bidId);
    BID getBID(int userId, int bidId);

    boolean contains(int userId, int productId);
}
