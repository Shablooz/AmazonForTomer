package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Product;

import java.util.List;

public class ProductRepositoryAsList implements IProductRepository {

    private final List<Product> list;

    public ProductRepositoryAsList(List<Product> list) {
        this.list = list;
    }

    @Override
    public void specialFunction() {

    }

    @Override
    public void add(Product item) {
        list.add(item);
    }

    @Override
    public void remove() {

    }

    @Override
    public Product get(int productId) {
        return list.stream().filter(product -> product.getProductId() == productId).findFirst().orElse(null);
    }
}
