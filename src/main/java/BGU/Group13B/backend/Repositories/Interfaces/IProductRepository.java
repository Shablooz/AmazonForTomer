package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.Product;

public interface IProductRepository {
    void specialFunction();
    void add(Product item);
    void remove();
    Product get(int productId);
}
