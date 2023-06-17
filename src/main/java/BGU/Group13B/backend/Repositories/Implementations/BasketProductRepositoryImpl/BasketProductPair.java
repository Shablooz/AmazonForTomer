package BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl;

import BGU.Group13B.backend.Pair;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class BasketProductPair {

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        private int id;

        private Integer first;
        private Integer second;

        public BasketProductPair(Integer first, Integer second) {
            this.first = first;
            this.second = second;
        }
    public BasketProductPair() {
        this.first = 0;
        this.second = 0;
    }
        public static BasketProductPair of(Integer first, Integer second) {
            return new BasketProductPair(first, second);
        }

        public Integer getFirst() {
            return first;
        }

        public Integer getSecond() {
            return second;
        }

        public void setFirst(Integer first) {
            this.first = first;
        }

        public void setSecond(Integer second) {
            this.second = second;
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketProductPair that = (BasketProductPair) o;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
