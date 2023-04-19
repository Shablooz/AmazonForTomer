package BGU.Group13B.backend.Repositories.Interfaces;

public interface IPurchaseHistoryRepository {

    boolean isPurchase(int userId, int storeId, int productId); //TODO:eden need to implement - return true if the user purchased the product from the store
    boolean isPurchaseFromStore(int userId, int storeId); //TODO:eden need to implement -return true if the user purchased from the store
}

