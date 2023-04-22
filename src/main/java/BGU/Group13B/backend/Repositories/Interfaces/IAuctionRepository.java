package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.service.callbacks.AddToUserCart;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IAuctionRepository {
    void addNewAuctionForAProduct(int productId, double startingPrice, int storeId, LocalDateTime endTime);
    void endAuction(int productId, int storeId);
    void updateAuction(int productId,int storeId, double newPrice, int newUserId);
    Optional<PublicAuctionInfo> getAuctionInfo(int productId, int storeId);

}
