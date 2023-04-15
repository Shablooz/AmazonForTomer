package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.AddToUserCart;

import java.util.concurrent.atomic.AtomicInteger;

public class Market {
    private final IStoreRepository storeRepository;

    public Market(AddToUserCart addToUserCart) {
        this.storeRepository = SingletonCollection.getStoreRepository();
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

    //(#24) open store - requirement 3.2
    public void addStore(int founderId, String storeName, String category){
        storeRepository.addStore(founderId, storeName, category);
    }

}
