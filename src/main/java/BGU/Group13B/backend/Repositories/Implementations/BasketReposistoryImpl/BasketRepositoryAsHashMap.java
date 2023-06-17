package BGU.Group13B.backend.Repositories.Implementations.BasketReposistoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.UserPemissionRepositoryImpl.UserPermissionRepService;
import BGU.Group13B.backend.Repositories.Interfaces.IBasketRepository;
import BGU.Group13B.backend.User.Basket;
import BGU.Group13B.backend.User.SetBasket;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;


import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Entity
public class BasketRepositoryAsHashMap implements IBasketRepository {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Transient
    private boolean saveMode;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "BasketRepositoryAsHashMap_Baskets",
            joinColumns = {@JoinColumn(name = "BasketRepositoryAsHashMap_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "Baskets_id", referencedColumnName = "userId")})
    @MapKeyJoinColumn(name = "userId")
    private final Map<Integer/*userId*/, SetBasket> baskets;

    public BasketRepositoryAsHashMap() {
        this.baskets = new ConcurrentHashMap<>();
        this.saveMode= true;
    }
    public BasketRepositoryAsHashMap(boolean saveMode) {
        this.baskets = new ConcurrentHashMap<>();
        this.saveMode= saveMode;
    }

    public Set<Basket> getUserBaskets(int userId) {
        return baskets.getOrDefault(userId, new SetBasket(userId)).getBaskets(); //maybe bug
    }

    public void removeUserBasket(int userId, int storeId) {
        if (!baskets.containsKey(userId))
            throw new NoSuchElementException("User does not have the basket associated with the storeId: " + storeId);
        baskets.get(userId).removeIf(storeId);
        save();
    }

    public Basket addUserBasket(int userId, int storeId) {
        if (baskets.putIfAbsent(userId, new SetBasket(userId)) == null
                || noStoreBasket(userId, storeId)) {
            Basket basket = new Basket(userId, storeId);
            baskets.get(userId).add(basket);
            save();
            return basket;
        }
        throw new IllegalArgumentException("User already has a basket associated with the storeId: " + storeId);
    }
    public void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(BasketRepositoryService.class).save(this);
    }

    //for tests
    @Override
    public void addUserBasket(Basket basket) {
        //removeUserBasket(basket.getUserId(), basket.getStoreId());
        if (baskets.putIfAbsent(basket.getUserId(), new SetBasket(basket.getUserId())) == null)
            baskets.get(basket.getUserId()).add(basket);
    }

    private boolean noStoreBasket(int userId, int storeId) {
        return baskets.get(userId).streamNoneMatch(storeId);
    }
    public void setSaveMode(boolean saved) {
        this.saveMode = saved;
    }


    //getter and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, SetBasket> getBaskets() {
        return baskets;
    }
}
