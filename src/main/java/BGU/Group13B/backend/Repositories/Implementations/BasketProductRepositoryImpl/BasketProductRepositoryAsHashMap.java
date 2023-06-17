package BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl.BasketRepositoryService;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketProductRepository;
import BGU.Group13B.backend.Repositories.Interfaces.IProductRepository;
import BGU.Group13B.backend.User.BasketProduct;
import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class BasketProductRepositoryAsHashMap implements IBasketProductRepository {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Transient
    private boolean saveMode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = "BasketProductRepositoryAsHashMap_BasketProducts",
            joinColumns = {@JoinColumn(name = "BasketProductRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "listBacketProduct_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "BasketProductPair_id")
    private Map<BasketProductPair, ListBasketProduct> basketProducts;
    @Transient
    private  IProductRepository productRepository;

    public BasketProductRepositoryAsHashMap() {
        this.basketProducts = new ConcurrentHashMap<>();
        this.productRepository = SingletonCollection.getProductRepository();
        this.saveMode=true;
    }

    public BasketProductRepositoryAsHashMap(boolean saveMode) {
        this.basketProducts = new ConcurrentHashMap<>();
        this.productRepository = SingletonCollection.getProductRepository();
        this.saveMode=saveMode;
    }
    @Override
    public Optional<List<BasketProduct>>
    getBasketProducts(int storeId, int userId) {
        ListBasketProduct basketProduct =  basketProducts.getOrDefault(BasketProductPair.of(storeId, userId), null);
        if(basketProduct == null)
            return Optional.empty();
        return Optional.of(basketProduct.getBasketProducts());
    }

    @Override
    public void removeBasketProducts(int storeId, int userId) {
       getBasketProducts(storeId, userId).ifPresent(List::clear);
       save();
        /* basketProducts.remove(Pair.of(storeId, userId)).orElseThrow(
                () -> new IllegalArgumentException("There is not basket for store with id: " + storeId + " and user with id: " + userId));
    */}



    //1 - get the products of the store
    //2 - put the products in the basket
    @Override
    public void addNewProductToBasket(int productId, int storeId, int userId) {
        Product product = getProductRepository().getStoreProductById(productId, storeId);

        if(!basketProducts.containsKey(BasketProductPair.of(storeId, userId))) {
            List<BasketProduct> newBasketProducts = new LinkedList<>();
            newBasketProducts.add(new BasketProduct(product));
            basketProducts.put(BasketProductPair.of(storeId, userId),new ListBasketProduct(newBasketProducts) );
        }else{
            List<BasketProduct> basketProductsList = basketProducts.get(BasketProductPair.of(storeId, userId)).getBasketProducts();
            var basketProductOptional = basketProductsList.stream().filter(basketProduct -> basketProduct.getProductId() == productId).findFirst();
            if(basketProductOptional.isPresent()) {
               throw new IllegalArgumentException("Product with id: " + productId + " already exists in the basket");
            }
            basketProductsList.add(new BasketProduct(product));
        }
        save();
    }


    @Override
    public void changeProductQuantity(int productId, int storeId, int userId, int newQuantity) {

        var basketProducts = getBasketProducts(storeId, userId).orElseThrow(
                () -> new IllegalArgumentException("Products of store with id: " + storeId + " does not exist"));

        basketProducts.stream().filter(basketProduct -> basketProduct.getProductId() == productId).findFirst().
                orElseThrow(
                        () -> new IllegalArgumentException("Product with id: " + productId + " does not exist in this basket")
                ).setQuantity(newQuantity);
        save();
    }

    @Override
    public BasketProduct getBasketProduct(int productId, int storeId, int userId) throws IllegalArgumentException{
        for (BasketProduct basketProduct : getBasketProducts(storeId, userId).orElseGet(LinkedList::new)) {
            if (basketProduct.getProductId() == productId) {
                return basketProduct;
            }
        }
        return null;
    }
    @Override
    public void removeBasketProduct(int productId, int userId, int storeId) {
        BasketProduct basketProduct = getBasketProduct(productId, storeId, userId);
        if(basketProduct!=null)
            getBasketProducts(storeId, userId).get().remove(basketProduct);
        save();
    }
    public void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(BasketProductRepoService.class).save(this);
    }
    public void setSaveMode(boolean saved) {
        this.saveMode = saved;
    }
    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<BasketProductPair, ListBasketProduct> getBasketProducts() {
        return basketProducts;
    }

    public void setBasketProducts(Map<BasketProductPair, ListBasketProduct> basketProducts) {
        this.basketProducts = basketProducts;
    }

    public IProductRepository getProductRepository() {
        productRepository= SingletonCollection.getProductRepository();
        return productRepository;
    }

    public void setProductRepository(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }


}
