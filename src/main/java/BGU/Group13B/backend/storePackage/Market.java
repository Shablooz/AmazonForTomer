package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.callbacks.AddToUserCart;

import java.time.LocalDateTime;

public class Market {
    private final IStoreRepository storeRepository;

    private final AddToUserCart addToUserCart;

    public Market(IStoreRepository storeRepository, AddToUserCart addToUserCart) {
        this.storeRepository = storeRepository;
        this.addToUserCart = addToUserCart;
    }

    public void addProduct(int userId, String productName, int quantity, double price, int storeId) throws NoPermissionException {
        storeRepository.getStore(storeId).addProduct(userId, productName, quantity, price);
    }

    public void purchaseProposalSubmit(int userId, int storeId, int productId, double proposedPrice, int amount) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalSubmit(userId, productId, proposedPrice, amount);
    }

    public void purchaseProposalApprove(int managerId, int storeId, int productId) throws NoPermissionException {
        storeRepository.getStore(storeId).purchaseProposalApprove(managerId, productId);
    }
    public void auctionPurchase(int userId, int storeId, int productId, double newPrice) throws NoPermissionException {
        storeRepository.getStore(storeId).auctionPurchase(userId, productId, newPrice);
    }
    public PublicAuctionInfo getAuctionInfo(int userId, int storeId, int productId) throws NoPermissionException {
        return storeRepository.getStore(storeId).getAuctionInfo(userId, productId);
    }
    public void createAuctionForProduct(int storeManagerId, int storeId, int productId,
                                        double minPrice, LocalDateTime lastDate) throws NoPermissionException {
        storeRepository.getStore(storeId).createAuctionForProduct(storeManagerId, productId, minPrice, lastDate);
    }

}
