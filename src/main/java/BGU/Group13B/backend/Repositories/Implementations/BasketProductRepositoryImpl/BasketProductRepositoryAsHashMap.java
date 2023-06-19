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
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class BasketProductRepositoryAsHashMap implements IBasketProductRepository {

    private AtomicInteger idGenerator;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Transient
    private boolean saveMode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = "BasketProductRepositoryAsHashMap_basketProductPair",
            joinColumns = {@JoinColumn(name = "BasketProductRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "BasketProductPair_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "BasketProductPair_id")

    private Map<Integer, BasketProductPair> basketProductPairs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = "BasketProductRepositoryAsHashMap_ListBasketProducts",
            joinColumns = {@JoinColumn(name = "BasketProductRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "listBacketProduct_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "BasketProductPair_id")

    private Map<Integer, ListBasketProduct> basketProducts;

    @Transient
    private IProductRepository productRepository;

    public BasketProductRepositoryAsHashMap() {
        this.basketProducts = new ConcurrentHashMap<>();
        this.productRepository = SingletonCollection.getProductRepository();
        this.basketProductPairs = new ConcurrentHashMap<>();
        this.idGenerator = new AtomicInteger(0);
        this.saveMode = true;
    }

    public BasketProductRepositoryAsHashMap(boolean saveMode) {
        this.basketProducts = new ConcurrentHashMap<>();
        this.basketProductPairs = new ConcurrentHashMap<>();
        this.productRepository = SingletonCollection.getProductRepository();
        this.idGenerator = new AtomicInteger(0);
        this.saveMode = saveMode;
    }

    @Override
    public Optional<List<BasketProduct>> getBasketProducts(int storeId, int userId) {
        BasketProductPair basketProductPair = getBasketProductPair(storeId, userId);
        ListBasketProduct basketProduct = basketProducts.getOrDefault(basketProductPair == null ? -1 : basketProductPair.getId(), null);
        if (basketProduct == null)
            return Optional.empty();
        return Optional.of(basketProduct.getBasketProducts());
    }

    private BasketProductPair getBasketProductPair(int storeId, int userId) {
        BasketProductPair basketProductPair = null;
        for(BasketProductPair basketProductPair1 : basketProductPairs.values())
        {
            if(basketProductPair1.getFirst() == storeId && basketProductPair1.getSecond() == userId)
            {
                basketProductPair = basketProductPair1;
                break;
            }
        }
        return basketProductPair;
    }

    @Override
    public void removeBasketProducts(int storeId, int userId) {
        getBasketProducts(storeId, userId).ifPresent(List::clear);
        save();
    }


    //1 - get the products of the store
    //2 - put the products in the basket
    @Override
    public void addNewProductToBasket(int productId, int storeId, int userId) {
        //***********************************************************************//
        Product product = getProductRepository().getStoreProductById(productId, storeId);
        var basketProductPair = getBasketProductPair(storeId, userId);
        if(basketProductPair == null) {
            basketProductPair = new BasketProductPair(storeId, userId);
            //add to maps
            int identifier = idGenerator.getAndIncrement();
            basketProductPair.setId(identifier);
            basketProductPairs.put(basketProductPair.getId(), basketProductPair);//todo plus 1
            basketProducts.put(basketProductPair.getId(), new ListBasketProduct(new LinkedList<>()));//here
        }
        if (!basketProducts.containsKey(basketProductPair.getId())) {
            System.out.println("basketProductPair.getId() = " + basketProductPair.getId());
            List<BasketProduct> newBasketProducts = new LinkedList<>();
            newBasketProducts.add(new BasketProduct(product));

            basketProducts.put(basketProductPair.getId(), new ListBasketProduct(newBasketProducts));//here
            basketProductPairs.put(basketProductPair.getId(), basketProductPair);//here
        } else {
            List<BasketProduct> basketProductsList = basketProducts.get(basketProductPair.getId()).getBasketProducts();
            var basketProductOptional = basketProductsList.stream().filter(basketProduct -> basketProduct.getProductId() == productId).findFirst();
            if (basketProductOptional.isPresent()) {
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
    public BasketProduct getBasketProduct(int productId, int storeId, int userId) throws IllegalArgumentException {
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
        if (basketProduct != null)
            getBasketProducts(storeId, userId).get().remove(basketProduct);//seems that if a user deletes basketProduct it also deletes from other users because of product
        save();
    }

    public void save() {
        if (saveMode)
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

    public Map<Integer, ListBasketProduct> getBasketProducts() {
        return basketProducts;
    }

    @Override
    public boolean isUserBasketsExists(int userId) {
        //if basketProducts contain pair keys with userId return true else return false
        return basketProductPairs.values().stream().anyMatch(pair -> pair.getSecond() == userId);
    }


    @Override
    public void dropBasket(int storeId, int userId) {
        var v_id = getBasketProductPair(storeId, userId).getId();
        basketProducts.remove(v_id);
        basketProductPairs.remove(v_id);
    }

    public IProductRepository getProductRepository() {
        productRepository = SingletonCollection.getProductRepository();
        return productRepository;
    }

    public void setProductRepository(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Map<Integer, BasketProductPair> getBasketProductPairs() {
        return basketProductPairs;
    }

    public boolean isSaveMode() {
        return saveMode;
    }

    public void setBasketProductPairs(Map<Integer, BasketProductPair> basketProductPairs) {
        this.basketProductPairs = basketProductPairs;
    }

    public void setBasketProducts(Map<Integer, ListBasketProduct> basketProducts) {
        this.basketProducts = basketProducts;
    }

    public AtomicInteger getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(AtomicInteger idGenerator) {
        this.idGenerator = idGenerator;
    }


}
