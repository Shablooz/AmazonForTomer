package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Product;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductRepositoryAsHashMap implements IProductRepository {

    private final ConcurrentHashMap<Integer/*storeId*/, ConcurrentSkipListSet<Product>> storeProducts;
    private final AtomicInteger productIdCounter = new AtomicInteger(0);

    public ProductRepositoryAsHashMap() {
        this.storeProducts = new ConcurrentHashMap<>();
    }


    @Override
    public Optional<Set<Product>> getStoreProducts(int storeId) {
        return Optional.ofNullable(storeProducts.get(storeId));
    }

    @Override
    public void removeStoreProduct(int productId, int storeId) {
        getStoreProducts(storeId).orElseThrow(
                () -> new IllegalArgumentException("Store " + storeId + " not found")
        ).removeIf(product -> product.getProductId() == productId);
    }

    @Override
    public void addProduct(int storeId, String name, String category, double price, int maxAmount) {
        if(!storeProducts.containsKey(storeId))
            storeProducts.put(storeId, new ConcurrentSkipListSet<>(Comparator.comparingInt(Product::getProductId)));
        storeProducts.get(storeId).add(new Product(productIdCounter.getAndIncrement(), storeId, name, category, price, maxAmount));
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
    public Product getStoreProductById(int productId, int storeId){
        return getStoreProducts(storeId).orElseThrow(
                        () -> new IllegalArgumentException("Store " + storeId + " not found")
                ).stream().filter(product -> product.getProductId() == productId).
                findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Product " + productId + " not found in store " + storeId)
                );
    }

    @Override
    public Product getProductById(int productId) {
        return storeProducts.values().stream().flatMap(Set::stream).filter(product -> product.getProductId() == productId).
                findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Product " + productId + " not found")
                );
    }

/*    @Override
    public double calculatePrice(int storeId, int productId, int productQuantity, String couponCode) {
        if (!storeProducts.containsKey(storeId))
            throw new IllegalArgumentException("Store not found");
        return storeProducts.get(storeId)
                .stream().filter(product -> product.getProductId() == productId).
                findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Product not found in store")
                ).calculatePrice(productQuantity, couponCode);
    }*/
}