package BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IDiscountRepository;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.CategoryDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.ProductDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscountRepositoryAsHashMap implements IDiscountRepository {

    private final ConcurrentHashMap<Pair<Integer/*discountId*/, Integer/*storeId*/>, StoreDiscount> discounts;

    private final AtomicInteger nextId;

    public DiscountRepositoryAsHashMap() {
        discounts = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
    }

    @Override
    public int addStoreDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new StoreDiscount(id, storeId, condition, discountPercentage, expirationDate, coupon));
        return id;
    }

    @Override
    public int addStoreDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new StoreDiscount(id, storeId, discountPercentage, expirationDate, coupon));
        return id;
    }

    @Override
    public int addStoreDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new StoreDiscount(id, storeId, condition, discountPercentage, expirationDate));
        return id;

    }

    @Override
    public int addStoreDiscount(int storeId, double discountPercentage, LocalDate expirationDate) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new StoreDiscount(id, storeId, discountPercentage, expirationDate));
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new CategoryDiscount(id, storeId, condition, discountPercentage, expirationDate, category, coupon));
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String category, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new CategoryDiscount(id, storeId, discountPercentage, expirationDate, category, coupon));
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, String category) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new CategoryDiscount(id, storeId, condition, discountPercentage, expirationDate, category));
        return id;
    }

    @Override
    public int addCategoryDiscount(int storeId, double discountPercentage, LocalDate expirationDate, String category) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new CategoryDiscount(id, storeId, discountPercentage, expirationDate, category));
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new ProductDiscount(id, storeId, condition, discountPercentage, expirationDate, productId, coupon));
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, double discountPercentage, LocalDate expirationDate, int productId, String coupon) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new ProductDiscount(id, storeId, discountPercentage, expirationDate, productId, coupon));
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, Condition condition, double discountPercentage, LocalDate expirationDate, int productId) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new ProductDiscount(id, storeId, condition, discountPercentage, expirationDate, productId));
        return id;
    }

    @Override
    public int addProductDiscount(int storeId, double discountPercentage, LocalDate expirationDate, int productId) {
        int id = nextId.getAndIncrement();
        discounts.put(Pair.of(id, storeId), new ProductDiscount(id, storeId, discountPercentage, expirationDate, productId));
        return id;
    }

    @Override
    public List<StoreDiscount> getStoreDiscounts(int storeId) {
        return discounts.
                entrySet().
                stream().
                filter(e -> e.getKey().getSecond() == storeId).
                map(Map.Entry::getValue).
                toList();
    }

    @Override
    public StoreDiscount getDiscount(int discountId, int storeId) {
        if (!discounts.containsKey(Pair.of(discountId, storeId)))
            throw new IllegalArgumentException("DiscountNode with id " + discountId + " does not exist");

        return discounts.get(Pair.of(discountId, storeId));
    }

    @Override
    public void removeDiscount(int discountId, int storeId) {
        discounts.remove(Pair.of(discountId, storeId));
    }

    @Override
    public void reset() {
        discounts.clear();
        nextId.set(0);
    }
}
