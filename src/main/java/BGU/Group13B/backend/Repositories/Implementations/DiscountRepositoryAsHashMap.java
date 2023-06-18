package BGU.Group13B.backend.Repositories.Implementations;

import BGU.Group13B.backend.Repositories.Interfaces.IDiscountRepository;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.CategoryDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class DiscountRepositoryAsHashMap implements IDiscountRepository {
    @Transient
    boolean saveMode;
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DiscountRepositoryAsHashMap_storeId",
            joinColumns = {@JoinColumn(name = "DiscountRepositoryAsHashMap_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "discountId")
    @Column(name = "storeId")
    private Map<Integer/*discountId*/, Integer/*storeId*/> discountToStoreId;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "DiscountRepositoryAsHashMap_storeDiscount",
            joinColumns = {@JoinColumn(name = "DiscountRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "StoreDiscount_id", referencedColumnName = "discountId")})
    @MapKeyJoinColumn(name = "discountId")
    private Map<Integer/*discountId*/, StoreDiscount>  discountToStoreDiscount;
    private AtomicInteger nextId;

    public DiscountRepositoryAsHashMap() {
        this.saveMode = true;
        //discounts = new ConcurrentHashMap<>();
        discountToStoreDiscount = new ConcurrentHashMap<>();
        discountToStoreId = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
    }

    @Override
    public int addStoreDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String coupon) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new StoreDiscount(id, storeId, condition, discountPercentage, expirationDate, coupon);
        //PairForDiscounts pair = new PairForDiscounts(id, storeId);
        //discounts.put(pair, storeDiscount);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addStoreDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String coupon) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new StoreDiscount(id, storeId, discountPercentage, expirationDate, coupon);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);

        save();
        return id;
    }

    @Override
    public int addStoreDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new StoreDiscount(id, storeId, condition, discountPercentage, expirationDate);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;

    }

    @Override
    public int addStoreDiscount(int storeId, double discountPercentage, LocalDate expirationDate) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new StoreDiscount(id, storeId, discountPercentage, expirationDate);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new CategoryDiscount(id, storeId, condition, discountPercentage, expirationDate, category, coupon);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new CategoryDiscount(id, storeId, discountPercentage, expirationDate, category, coupon);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new CategoryDiscount(id, storeId, condition, discountPercentage, expirationDate, category);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String category) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new CategoryDiscount(id, storeId, discountPercentage, expirationDate, category);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new ProductDiscount(id, storeId, condition, discountPercentage, expirationDate, productId, coupon);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new ProductDiscount(id, storeId, discountPercentage, expirationDate, productId, coupon);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new ProductDiscount(id, storeId, condition, discountPercentage, expirationDate, productId);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, double discountPercentage, LocalDate expirationDate, int productId) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new ProductDiscount(id, storeId, discountPercentage, expirationDate, productId);
        discountToStoreId.put(id, storeId);
        discountToStoreDiscount.put(id, storeDiscount);
        save();
        return id;
    }

    @Override
    public List<StoreDiscount> getStoreDiscounts(int storeId) {
        /*return discounts.
                entrySet().
                stream().
                filter(e -> e.getKey().getStoreId_for_pair() == storeId).
                map(Map.Entry::getValue).
                toList();*/
        List<StoreDiscount> storeDiscounts = new LinkedList<>();
        for(var keyValue : discountToStoreId.entrySet()){
            if(keyValue.getValue() == storeId){
                storeDiscounts.add(discountToStoreDiscount.get(keyValue.getKey()));
            }
        }
        return storeDiscounts;
    }

    @Override
    public StoreDiscount getDiscount(int discountId, int storeId) {
        /*if (!discounts.containsKey(new PairForDiscounts(discountId, storeId)))
            throw new IllegalArgumentException("DiscountNode with id " + discountId + " does not exist");

        return discounts.get(new PairForDiscounts(discountId, storeId));*/
        if(!discountToStoreId.containsKey(discountId))
            throw new IllegalArgumentException("DiscountNode with id " + discountId + " does not exist");
        if(discountToStoreId.get(discountId) != storeId)
            throw new IllegalArgumentException("DiscountNode with id " + discountId + " does not exist");
        return discountToStoreDiscount.get(discountId);

    }

    @Override
    public void removeDiscount(int discountId, int storeId) {
        discountToStoreId.remove(discountId);
        discountToStoreDiscount.remove(discountId);
        save();
    }

    @Override
    public void reset() {
        discountToStoreId.clear();
        discountToStoreDiscount.clear();
        nextId.set(0);
        save();
    }

    @Override
    public void removeStoreProductDiscounts(int storeId, int productId) {
        List<Integer> keysToRemove = new ArrayList<>();

        for (var discount_store : discountToStoreId.entrySet()) {
            if(discount_store.getValue() == storeId && discountToStoreDiscount.get(discount_store.getKey()) instanceof ProductDiscount productDiscount){
                if(productDiscount.getProductId() == productId)
                    keysToRemove.add(discount_store.getKey());
            }
        }

        for (var key : keysToRemove) {
            discountToStoreDiscount.remove(key);
            discountToStoreId.remove(key);
        }
        save();
    }


    @Override
    public void removeAllStoreDiscounts(int storeId) {
        List<Integer> keysToRemove = new ArrayList<>();

        for (var keyValue : discountToStoreId.entrySet()) {
            if (keyValue.getValue() == storeId) {
                keysToRemove.add(keyValue.getKey());
            }
        }

        for (var key : keysToRemove) {
            discountToStoreDiscount.remove(key);
            discountToStoreId.remove(key);
        }
        save();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDiscountToStoreDiscount(Map<Integer, StoreDiscount> discountToStoreDiscount) {
        this.discountToStoreDiscount = discountToStoreDiscount;
    }

    public void setDiscountToStoreId(Map<Integer, Integer> discountToStoreId) {
        this.discountToStoreId = discountToStoreId;
    }

    public int getId() {
        return id;
    }

    public Map<Integer, Integer> getDiscountToStoreId() {
        return discountToStoreId;
    }

    public Map<Integer, StoreDiscount> getDiscountToStoreDiscount() {
        return discountToStoreDiscount;
    }

    public AtomicInteger getNextId() {
        return nextId;
    }

    public void setNextId(AtomicInteger nextId) {
        this.nextId = nextId;
    }

    @Override
    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }

    private void save() {
        if (saveMode)
            SingletonCollection.getContext().getBean(DiscountRepositoryService.class).save(this);
    }

}
