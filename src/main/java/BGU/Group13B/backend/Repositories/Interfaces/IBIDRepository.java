package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.BID;

import java.util.Optional;

public interface IBIDRepository {

    void addBID(int userId, int productId, double newProductPrice, int amount);
    void removeBID(int userId, int productId);
    Optional<BID> getBID(int userId, int productId);

    boolean contains(int bidId);
    void setSaveMode(boolean saveMode);
    void reset();
}
