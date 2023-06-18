package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingleService;
import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
@Entity
public class ProductRepositoryAsHashMap implements IProductRepository {
    @Transient
    private boolean saveMode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "ProductRepositoryAsHashMap_SkipListHolderClickbate",
            joinColumns = {@JoinColumn(name = "ProductRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "SkipListHolderClickbate_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "storeId")
    private Map<Integer/*storeId*/, SkipListHolderClickbate> storeProducts;

    private AtomicInteger productIdCounter = new AtomicInteger(0);

    public ProductRepositoryAsHashMap() {
        this.storeProducts = new ConcurrentHashMap<>();
        this.saveMode = true;
    }

    public ProductRepositoryAsHashMap(boolean saveMode) {
        this.storeProducts = new ConcurrentHashMap<>();
        this.saveMode = saveMode;
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
        if(storeProducts.get(storeId).isEmpty())
            storeProducts.remove(storeId);
        save();
    }

  
    public synchronized Product addProduct(int storeId, String name, String category, double price, int stockQuantity, String description) {
        if (!storeProducts.containsKey(storeId))
            storeProducts.put(storeId, new SkipListHolderClickbate());
        int productId = productIdCounter.getAndIncrement();
        Product product=new Product(productId, storeId, name, category, price, stockQuantity, description);
        storeProducts.get(storeId).add(product);
        save();
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
        save();
    }

    @Override
    public void hideAllStoreProducts(int storeId) {
        if(!storeProducts.containsKey(storeId))
            return;
        getStoreProducts(storeId).orElseThrow(
                () -> new IllegalArgumentException("Store " + storeId + " not found")
        ).forEach(Product::hide);
        save();
    }

    @Override
    public void unhideAllStoreProducts(int storeId) {
        if(!storeProducts.containsKey(storeId))
            return;
        getStoreProducts(storeId).orElseThrow(
                () -> new IllegalArgumentException("Store " + storeId + " not found")
        ).forEach(Product::unhide);
        save();
    }

    @Override
    public synchronized int addHiddenProduct(int storeId, String name, String category, double price, int stockQuantity, String description) {
        if (!storeProducts.containsKey(storeId))
            storeProducts.put(storeId, new SkipListHolderClickbate());
        int productId = productIdCounter.getAndIncrement();
        storeProducts.get(storeId).add(new Product(productId, storeId, name, category, price, stockQuantity, description, true));
        save();
        return productId;
    }

    @Override
    public boolean isStoreProductsExists(int storeId) {
        return storeProducts.containsKey(storeId);
    }



    @Override
    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }

    public boolean getSaveMode(){
        return saveMode;
    }

    private void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(ProductRepositoryAsHashMapService.class).save(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, SkipListHolderClickbate> getStoreProducts() {
        return storeProducts;
    }

    public void setStoreProducts(Map<Integer, SkipListHolderClickbate> storeProducts) {
        this.storeProducts = storeProducts;
    }

    public AtomicInteger getProductIdCounter() {
        return productIdCounter;
    }

    public void setProductIdCounter(AtomicInteger productIdCounter) {
        this.productIdCounter = productIdCounter;
    }




}
