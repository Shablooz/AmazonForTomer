package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.Keys;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.util.Objects;


@Entity
@IdClass(ReviewRepoSinglePairId.class)
public class ReviewRepoSinglePair {
    @Id
    private Integer first;

    @Id
    private Integer Second;


    public ReviewRepoSinglePair(Integer first, Integer second ) {
        this.first = first;
        Second = second;
    }

    public ReviewRepoSinglePair() {
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getSecond() {
        return Second;
    }

    public void setSecond(Integer second) {
        Second = second;
    }

    public static ReviewRepoSinglePair of(Integer first, Integer second) {
        return new ReviewRepoSinglePair(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewRepoSinglePair that = (ReviewRepoSinglePair) o;
        return Objects.equals(first, that.first) && Objects.equals(Second, that.Second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, Second);
    }
}
