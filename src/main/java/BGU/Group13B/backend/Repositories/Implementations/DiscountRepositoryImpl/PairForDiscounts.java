package BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PairForDiscounts {

    @Id
    private int discountId_for_pair;
    private int storeId_for_pair;


    public PairForDiscounts(int discountId_for_pair, int storeId_for_pair) {
        this.discountId_for_pair = discountId_for_pair;
        this.storeId_for_pair = storeId_for_pair;
    }


    public PairForDiscounts() {
        this.discountId_for_pair = 0;
        this.storeId_for_pair = 0;

    }

    public int getDiscountId_for_pair() {
        return discountId_for_pair;
    }

    public void setDiscountId_for_pair(int discountId) {
        this.discountId_for_pair = discountId;
    }

    public int getStoreId_for_pair() {
        return storeId_for_pair;
    }

    public void setStoreId_for_pair(int storeId) {
        this.storeId_for_pair = storeId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairForDiscounts that = (PairForDiscounts) o;
        return discountId_for_pair == that.discountId_for_pair && storeId_for_pair == that.storeId_for_pair;
    }
}
