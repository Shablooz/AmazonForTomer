package BGU.Group13B.backend.User;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Entity
public class SetBasket {

    @Id
    int userId;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "SetBasket_Basket",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "basket_user_id", referencedColumnName = "userId"),
                    @JoinColumn(name = "basket_store_id", referencedColumnName = "storeId")})
    private Set<Basket> baskets;

    public SetBasket(int userId) {
        this.baskets = ConcurrentHashMap.newKeySet();
        this.userId = userId;
    }
    public SetBasket() {
        this.baskets = ConcurrentHashMap.newKeySet();
        this.userId = 0;
    }

    public void add(Basket basket) {
        baskets.add(basket);
    }
    public void removeIf(int storeId){
       baskets.removeIf(basket -> basket.getStoreId() == storeId);
    }
    public boolean streamNoneMatch(int storeId){
       return baskets.stream().noneMatch(basket -> basket.getStoreId() == storeId);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Set<Basket> getBaskets() {
        return baskets;
    }

    public void setBaskets(Set<Basket> baskets) {
        this.baskets = baskets;
    }
}
