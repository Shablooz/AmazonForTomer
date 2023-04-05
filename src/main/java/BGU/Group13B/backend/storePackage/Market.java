package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;

public class Market {
    private final IStoreRepository storeRepository;

    public Market(IStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public void addProduct(int userId, String productName, int quantity, double price, int storeId) {
        storeRepository.getStore(storeId).addProduct(userId, productName, quantity, price);
    }
}
