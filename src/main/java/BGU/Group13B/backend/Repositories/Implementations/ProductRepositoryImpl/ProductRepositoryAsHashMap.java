package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Product;
import jakarta.persistence.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
@Entity
public class ProductRepositoryAsHashMap implements IProductRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "ProductRepositoryAsHashMap_SkipListHolderClickbate",
            joinColumns = {@JoinColumn(name = "ProductRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "store_id", referencedColumnName = "storeId")})
    @MapKeyJoinColumn(name = "storeId")
    private final Map<Integer/*storeId*/, SkipListHolderClickbate> storeProducts;

    private final AtomicInteger productIdCounter = new AtomicInteger(0);

    public ProductRepositoryAsHashMap() {
        this.storeProducts = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Set<Product>> getStoreProducts(int storeId) {
        if(storeProducts.containsKey(storeId))
            return storeProducts.get(storeId).getStoreProducts();
        return Optional.empty();
    }

    @Override
    public void removeStoreProduct(int productId, int storeId) {
        getStoreProducts(storeId).orElseThrow(
                () -> new IllegalArgumentException("Store " + storeId + " not found")
        ).removeIf(product -> product.getProductId() == productId);
    }

  
    public synchronized Product addProduct(int storeId, String name, String category, double price, int stockQuantity, String description) {
        if (!storeProducts.containsKey(storeId))
            storeProducts.put(storeId, new SkipListHolderClickbate());
        int productId = productIdCounter.getAndIncrement();
        Product product=new Product(productId, storeId, name, category, price, stockQuantity, description);
        storeProducts.get(storeId).add(product);
        return product;
    }


    @Override
    public synchronized Product getStoreProductById(int productId, int storeId) {
        return getStoreProducts(storeId).orElseThrow(
                        () -> new IllegalArgumentException("Store " + storeId + " not found or has no products")
                ).stream().filter(product -> product.getProductId() == productId).
                findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Product " + productId + " not found in store " + storeId)
                );
    }

    @Override
    public Product getProductById(int productId) {
        return storeProducts.values().stream().flatMap(SkipListHolderClickbate::stream).filter(product -> product.getProductId() == productId).
                findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Product " + productId + " not found")
                );
    }


    /*user for tests*/
    @Override
    public void removeStoreProducts(int storeId) {
        storeProducts.remove(storeId);
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
        storeProducts.forEach((key, value) -> {
            for (Product product : value.getStoreProducts().orElse(new ConcurrentSkipListSet<>(Comparator.comparingInt(Product::getProductId)))) {
                if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                    products.add(product);
                }
            }
        });
        return products;
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        List<Product> products = new LinkedList<>();
        storeProducts.forEach((key, value) -> {
            Set<Product> storeProducts = value.getStoreProducts().orElse(new ConcurrentSkipListSet<>(Comparator.comparingInt(Product::getProductId)));
            for (Product product : storeProducts) {
                if (product.getCategory().toLowerCase().contains(category.toLowerCase())) {
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
        storeProducts.entrySet().forEach(entry -> {
            Set<Product> storeProducts = entry.getValue().getStoreProducts().orElse(new ConcurrentSkipListSet<>(Comparator.comparingInt(Product::getProductId)));
            for(Product product : storeProducts){
                if(checkIfContainsSomeKeywords(keywords, product.getDescription())){
                    products.add(product);
                }
            }
        });
        return products;
    }

    @Override
    public void reset() {
        storeProducts.clear();
        productIdCounter.set(0);
    }

    @Override
    public void hideAllStoreProducts(int storeId) {
        if(!storeProducts.containsKey(storeId))
            return;
        getStoreProducts(storeId).orElseThrow(
                () -> new IllegalArgumentException("Store " + storeId + " not found")
        ).forEach(Product::hide);
    }

    @Override
    public void unhideAllStoreProducts(int storeId) {
        if(!storeProducts.containsKey(storeId))
            return;
        getStoreProducts(storeId).orElseThrow(
                () -> new IllegalArgumentException("Store " + storeId + " not found")
        ).forEach(Product::unhide);
    }

    @Override
    public synchronized int addHiddenProduct(int storeId, String name, String category, double price, int stockQuantity, String description) {
        if (!storeProducts.containsKey(storeId))
            storeProducts.put(storeId, new SkipListHolderClickbate());
        int productId = productIdCounter.getAndIncrement();
        storeProducts.get(storeId).add(new Product(productId, storeId, name, category, price, stockQuantity, description, true));
        return productId;
    }
}
