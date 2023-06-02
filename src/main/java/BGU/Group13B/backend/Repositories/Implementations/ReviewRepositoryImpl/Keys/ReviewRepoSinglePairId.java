package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.Keys;

import java.io.Serializable;
import java.util.Objects;

public class ReviewRepoSinglePairId implements Serializable {
    private Integer first;
    private Integer Second;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewRepoSinglePairId that = (ReviewRepoSinglePairId) o;
        return Objects.equals(first, that.first) && Objects.equals(Second, that.Second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, Second);
    }
}
