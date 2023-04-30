package BGU.Group13B.backend;

import java.util.Objects;

public class Pair<First, Second> {
    private First first;
    private Second second;

    private Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }
    public static <First, Second> Pair<First, Second> of(First first, Second second) {
        return new Pair<>(first, second);
    }

    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public void setSecond(Second second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
