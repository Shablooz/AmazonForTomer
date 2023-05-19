package BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IConditionRepository;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.AND;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.IMPLY;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.OR;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.XOR;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.*;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConditionRepositoryAsHashMap implements IConditionRepository {
    private final ConcurrentHashMap<Integer, Condition> conditions;
    private final AtomicInteger nextId;

    public ConditionRepositoryAsHashMap() {
        conditions = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
    }

    @Override
    public int addORCondition(int condition1, int condition2) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new OR(id, getCondition(condition1), getCondition(condition2)));
        return id;
    }

    @Override
    public int addANDCondition(int condition1, int condition2) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new AND(id, getCondition(condition1), getCondition(condition2)));
        return id;
    }

    @Override
    public int addXORCondition(int condition1, int condition2) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new XOR(id, getCondition(condition1), getCondition(condition2)));
        return id;
    }

    @Override
    public int addIMPLYCondition(int condition1, int condition2) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new IMPLY(id, getCondition(condition1), getCondition(condition2)));
        return id;
    }

    @Override
    public int addCategoryPriceCondition(String category, double lowerBound, double upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new CategoryPriceCondition(id, category, lowerBound, upperBound));
        return id;
    }

    @Override
    public int addCategoryPriceCondition(String category, double lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new CategoryPriceCondition(id, category, lowerBound));
        return id;
    }

    @Override
    public int addCategoryQuantityCondition(String category, int lowerBound, int upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new CategoryQuantityCondition(id, category, lowerBound, upperBound));
        return id;
    }

    @Override
    public int addCategoryQuantityCondition(String category, int lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new CategoryQuantityCondition(id, category, lowerBound));
        return id;
    }

    @Override
    public int addDateCondition(LocalDateTime lowerBound, LocalDateTime upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new DateCondition(id, lowerBound, upperBound));
        return id;
    }

    @Override
    public int addDateCondition(LocalDateTime lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new DateCondition(id, lowerBound));
        return id;
    }

    @Override
    public int addProductPriceCondition(int productId, double lowerBound, double upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ProductPriceCondition(id, productId, lowerBound, upperBound));
        return id;
    }

    @Override
    public int addProductPriceCondition(int productId, double lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ProductPriceCondition(id, productId, lowerBound));
        return id;
    }

    @Override
    public int addProductQuantityCondition(int productId, int lowerBound, int upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ProductQuantityCondition(id, productId, lowerBound, upperBound));
        return id;
    }

    @Override
    public int addProductQuantityCondition(int productId, int lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ProductQuantityCondition(id, productId, lowerBound));
        return id;
    }

    @Override
    public int addTimeCondition(LocalDateTime lowerBound, LocalDateTime upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new TimeCondition(id, lowerBound, upperBound));
        return id;
    }

    @Override
    public int addTimeCondition(LocalDateTime lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new TimeCondition(id, lowerBound));
        return id;
    }

    @Override
    public int addUserAgeCondition(int lowerBound, int upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new UserAgeCondition(id, lowerBound, upperBound));
        return id;
    }

    @Override
    public int addUserAgeCondition(int lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new UserAgeCondition(id, lowerBound));
        return id;
    }

    @Override
    public Condition getCondition(int conditionId) {
        if (!conditions.containsKey(conditionId))
            throw new IllegalArgumentException("Condition with id " + conditionId + " does not exist");

        return conditions.get(conditionId);
    }

    @Override
    public void removeCondition(int conditionId) {
        conditions.remove(conditionId);
    }

    @Override
    public void reset() {
        conditions.clear();
        nextId.set(0);
    }
}
