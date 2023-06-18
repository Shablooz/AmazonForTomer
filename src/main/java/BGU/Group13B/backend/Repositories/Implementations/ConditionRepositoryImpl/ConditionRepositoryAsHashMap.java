package BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Interfaces.IConditionRepository;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.AndCond;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.ImplyCond;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.OrCond;
import BGU.Group13B.backend.storePackage.newDiscoutns.Logical.XorCond;
import BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.baseCase.*;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
//TODO
@Entity
public class ConditionRepositoryAsHashMap implements IConditionRepository {

    @Transient
    private boolean saveMode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "ConditionRepositoryAsHashMap_Condition",
            joinColumns = {@JoinColumn(name = "ConditionRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "Condition_id", referencedColumnName = "conditionId")})
    @MapKeyJoinColumn(name = "condition_id")
    private Map<Integer, Condition> conditions;

    private  AtomicInteger nextId;

    public ConditionRepositoryAsHashMap() {
        conditions = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
        saveMode = true;
    }

    public ConditionRepositoryAsHashMap(boolean saveMode) {
        conditions = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
        this.saveMode = saveMode;
    }

    @Override
    public int addORCondition(int condition1, int condition2) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new OrCond(id, getCondition(condition1), getCondition(condition2)));
        save();
        return id;
    }

    @Override
    public int addANDCondition(int condition1, int condition2) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new AndCond(id, getCondition(condition1), getCondition(condition2)));
        save();
        return id;
    }

    @Override
    public int addXORCondition(int condition1, int condition2) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new XorCond(id, getCondition(condition1), getCondition(condition2)));
        save();
        return id;
    }

    @Override
    public int addIMPLYCondition(int condition1, int condition2) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ImplyCond(id, getCondition(condition1), getCondition(condition2)));
        save();
        return id;
    }

    @Override
    public int addStorePriceCondition(double lowerBound, double upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new StorePriceCondition(id, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addStorePriceCondition(double lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new StorePriceCondition(id, lowerBound));
        save();
        return id;
    }

    @Override
    public int addStoreQuantityCondition(int lowerBound, int upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new StoreQuantityCondition(id, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addStoreQuantityCondition(int lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new StoreQuantityCondition(id, lowerBound));
        save();
        return id;
    }

    @Override
    public int addCategoryPriceCondition(String category, double lowerBound, double upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new CategoryPriceCondition(id, category, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addCategoryPriceCondition(String category, double lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new CategoryPriceCondition(id, category, lowerBound));
        save();
        return id;
    }

    @Override
    public int addCategoryQuantityCondition(String category, int lowerBound, int upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new CategoryQuantityCondition(id, category, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addCategoryQuantityCondition(String category, int lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new CategoryQuantityCondition(id, category, lowerBound));
        save();
        return id;
    }

    @Override
    public int addDateCondition(LocalDateTime lowerBound, LocalDateTime upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new DateCondition(id, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addDateCondition(LocalDateTime lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new DateCondition(id, lowerBound));
        save();
        return id;
    }

    @Override
    public int addProductPriceCondition(int productId, double lowerBound, double upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ProductPriceCondition(id, productId, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addProductPriceCondition(int productId, double lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ProductPriceCondition(id, productId, lowerBound));
        save();
        return id;
    }

    @Override
    public int addProductQuantityCondition(int productId, int lowerBound, int upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ProductQuantityCondition(id, productId, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addProductQuantityCondition(int productId, int lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new ProductQuantityCondition(id, productId, lowerBound));
        save();
        return id;
    }

    @Override
    public int addTimeCondition(LocalDateTime lowerBound, LocalDateTime upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new TimeCondition(id, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addTimeCondition(LocalDateTime lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new TimeCondition(id, lowerBound));
        save();
        return id;
    }

    @Override
    public int addUserAgeCondition(int lowerBound, int upperBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new UserAgeCondition(id, lowerBound, upperBound));
        save();
        return id;
    }

    @Override
    public int addUserAgeCondition(int lowerBound) {
        int id = nextId.getAndIncrement();
        conditions.put(id, new UserAgeCondition(id, lowerBound));
        save();
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
        save();
    }

    @Override
    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }

    private void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(ConditionRepositoryAsHashMapService.class).save(this);
    }
}
