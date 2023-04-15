package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;
import BGU.Group13B.backend.System.Searcher;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.service.callbacks.AddToUserCart;

import java.util.List;

public class Market {
    private final IStoreRepository storeRepository;

    private final AddToUserCart addToUserCart;

    private Searcher searcher; //inject in the loading of the system

    public Market(IStoreRepository storeRepository, AddToUserCart addToUserCart, Searcher searcher) {
        this.searcher = searcher;
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

    public void searchProductByName(String productName) {
        searcher.searchByName(productName);
    }

    public void searchProductByCategory(String category) {
        searcher.searchByCategory(category);
    }

    public void searchProductByKeywords(List<String> keywords) {
        searcher.searchByKeywords(keywords);
    }

    public void filterByPriceRange(int minPrice, int maxPrice) {
        searcher.filterByPriceRange(minPrice, maxPrice);
    }

    public void filterByProductRank(int minRating, int maxRating) {
        searcher.filterByProductRank(minRating, maxRating);
    }

    public void filterByCategory(String category) {
        searcher.filterByCategory(category);
    }

    public void filterByStoreRank(int minRating, int maxRating) {
        searcher.filterByStoreRank(minRating, maxRating);
    }




}
