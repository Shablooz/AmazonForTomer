package BGU.Group13B.service;

public interface ISession {
    void addProduct(int userId, String productName, int quantity, double price, int storeId);
}
