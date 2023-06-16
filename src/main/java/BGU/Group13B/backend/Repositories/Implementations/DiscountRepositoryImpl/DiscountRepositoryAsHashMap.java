package BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IDiscountRepository;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.CategoryDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = "DiscountRepositoryAsHashMap_StoreDiscount",
            joinColumns = {@JoinColumn(name = "DiscountRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "pair_id", referencedColumnName = "discountId")})
    @MapKeyJoinColumn(name = "discountId_for_pair")
    private Map<PairForDiscounts, StoreDiscount> discounts;

    private AtomicInteger nextId;

    public DiscountRepositoryAsHashMap() {
        this.saveMode = true;
        discounts = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
    }

    @Override
    public int addStoreDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String coupon) {
        int id = nextId.getAndIncrement();
        StoreDiscount storeDiscount = new StoreDiscount(id, storeId, condition, discountPercentage, expirationDate, coupon);
        PairForDiscounts pair = new PairForDiscounts(id, storeId);
        discounts.put(pair, storeDiscount);
        save();
        return id;
    }

    @Override
    public int addStoreDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String coupon) {
        int id = nextId.getAndIncrement();
        PairForDiscounts pair = new PairForDiscounts(id, storeId);
        discounts.put(pair, new StoreDiscount(id, storeId, discountPercentage, expirationDate, coupon));
        save();
        return id;
    }

    @Override
    public int addStoreDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate) {
        int id = nextId.getAndIncrement();
        PairForDiscounts pair = new PairForDiscounts(id, storeId);
        discounts.put(pair, new StoreDiscount(id, storeId, condition, discountPercentage, expirationDate));
        save();
        return id;

    }

    @Override
    public int addStoreDiscount(int storeId, double discountPercentage, LocalDate expirationDate) {
        int id = nextId.getAndIncrement();
        PairForDiscounts pair = new PairForDiscounts(id, storeId);
        discounts.put(pair, new StoreDiscount(id, storeId, discountPercentage, expirationDate));
        save();
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        int id = nextId.getAndIncrement();
        PairForDiscounts pair = new PairForDiscounts(id, storeId);
        discounts.put(pair, new CategoryDiscount(id, storeId, condition, discountPercentage, expirationDate, category, coupon));
        save();
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(new PairForDiscounts(id, storeId), new CategoryDiscount(id, storeId, discountPercentage, expirationDate, category, coupon));
        save();
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category) {
        int id = nextId.getAndIncrement();
        discounts.put(new PairForDiscounts(id, storeId), new CategoryDiscount(id, storeId, condition, discountPercentage, expirationDate, category));
        save();
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String category) {
        int id = nextId.getAndIncrement();
        discounts.put(new PairForDiscounts(id, storeId), new CategoryDiscount(id, storeId, discountPercentage, expirationDate, category));
        save();
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(new PairForDiscounts(id, storeId), new ProductDiscount(id, storeId, condition, discountPercentage, expirationDate, productId, coupon));
        save();
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(new PairForDiscounts(id, storeId), new ProductDiscount(id, storeId, discountPercentage, expirationDate, productId, coupon));
        save();
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId) {
        int id = nextId.getAndIncrement();
        discounts.put(new PairForDiscounts(id, storeId), new ProductDiscount(id, storeId, condition, discountPercentage, expirationDate, productId));
        save();
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, double discountPercentage, LocalDate expirationDate, int productId) {
        int id = nextId.getAndIncrement();
        discounts.put(new PairForDiscounts(id, storeId), new ProductDiscount(id, storeId, discountPercentage, expirationDate, productId));
        save();
        return id;
    }

    @Override
    public List<StoreDiscount> getStoreDiscounts(int storeId) {
        return discounts.
                entrySet().
                stream().
                filter(e -> e.getKey().getStoreId_for_pair() == storeId).
                map(Map.Entry::getValue).
                toList();
    }

    @Override
    public StoreDiscount getDiscount(int discountId, int storeId) {
        if (!discounts.containsKey(new PairForDiscounts(discountId, storeId)))
            throw new IllegalArgumentException("DiscountNode with id " + discountId + " does not exist");

        return discounts.get(new PairForDiscounts(discountId, storeId));
    }

    @Override
    public void removeDiscount(int discountId, int storeId) {
        discounts.remove(new PairForDiscounts(discountId, storeId));
        save();
    }

    @Override
    public void reset() {
        discounts.clear();
        nextId.set(0);
    }

    @Override
    public void removeStoreProductDiscounts(int storeId, int productId) {
        List<PairForDiscounts> keysToRemove = new ArrayList<>();

        for (var keyValue : discounts.entrySet()) {
            if (keyValue.getKey().getStoreId_for_pair() == storeId && keyValue.getValue() instanceof ProductDiscount) {
                if (((ProductDiscount) keyValue.getValue()).getProductId() == productId) {
                    keysToRemove.add(keyValue.getKey());
                }
            }
        }

        for (var key : keysToRemove) {
            discounts.remove(key);
        }
        save();
    }


    @Override
    public void removeAllStoreDiscounts(int storeId) {
        List<PairForDiscounts> keysToRemove = new ArrayList<>();

        for (var keyValue : discounts.entrySet()) {
            if (keyValue.getKey().getStoreId_for_pair() == storeId) {
                keysToRemove.add(keyValue.getKey());
            }
        }

        for (var key : keysToRemove) {
            discounts.remove(key);
        }
        save();
    }

    public ConcurrentHashMap<PairForDiscounts, StoreDiscount> getDiscounts() {
        return (ConcurrentHashMap<PairForDiscounts, StoreDiscount>) discounts;
    }

    public void setDiscounts(ConcurrentHashMap<PairForDiscounts, StoreDiscount> discounts) {
        this.discounts = discounts;
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
