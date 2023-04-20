package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Product;

import java.util.*;
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
    public void addProduct(int storeId, String name, String category, double price, int maxAmount, String description) {
        if(!storeProducts.containsKey(storeId))
            storeProducts.put(storeId, new ConcurrentSkipListSet<>(Comparator.comparingInt(Product::getProductId)));
        storeProducts.get(storeId).add(new Product(productIdCounter.getAndIncrement(), storeId, name, category, price, maxAmount,description));
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

    @Override
    public List<Product> getProductByName(String name) {
        List<Product> products = new LinkedList<>();
        storeProducts.entrySet().stream().forEach(entry -> {
            Set<Product> storeProducts = entry.getValue();
            for(Product product : storeProducts){
                if(product.getName().toLowerCase().contains(name.toLowerCase())){
                    products.add(product);
                }
            }
        });
        return products;
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        List<Product> products = new LinkedList<>();
        storeProducts.entrySet().stream().forEach(entry -> {
            Set<Product> storeProducts = entry.getValue();
            for(Product product : storeProducts){
                if(product.getCategory().toLowerCase().contains(category.toLowerCase())){
                    products.add(product);
                }
            }
        });
        return products;
    }

    private boolean checkIfContainsSomeKeywords(List<String> keywords, String description) {
        List<String> modifiedKeywords = keywords.stream().map(String::toLowerCase).toList();
        String modifiedDescription = description.toLowerCase();
        for (String keyword : modifiedKeywords) {
            if (modifiedDescription.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Product> getProductByKeywords(List<String> keywords) {
        List<Product> products = new LinkedList<>();
        storeProducts.entrySet().stream().forEach(entry -> {
            Set<Product> storeProducts = entry.getValue();
            for(Product product : storeProducts){
                if(checkIfContainsSomeKeywords(keywords, product.getDescription())){
                    products.add(product);
                }
            }
        });
        return products;
    }
}
