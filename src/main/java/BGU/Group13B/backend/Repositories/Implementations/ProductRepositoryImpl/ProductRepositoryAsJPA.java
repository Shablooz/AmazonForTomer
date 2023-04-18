package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class ProductRepositoryAsJPA implements IProductRepository {
    private final JpaRepository<Product, Integer> repo;

    public ProductRepositoryAsJPA(JpaRepository<Product, Integer> repo) {
        this.repo = repo;
    }


    @Override
    public void specialFunction() {

    }

    @Override
    public void add(Product item) {
        repo.save(item);
    }

    @Override
    public void remove() {

    }

    @Override
    public Product get(int productId) {
        return null;
    }

    @Override
    public List<Product> getProductByName(String name) {
        return null;
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        return null;
    }

    @Override
    public List<Product> getProductByKeywords(List<String> keywords) {
        return null;
    }

    @Override
    public List<Product> filterByPriceRange(int minPrice, int maxPrice) {
        return null;
    }

    @Override
    public Product getProduct(int productId, int storeId) { //TODO:implement
        return null;
    }


}
