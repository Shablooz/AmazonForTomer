package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.storePackage.Product;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

@Entity
public class SkipListHolderClickbate {
    @Id
    private int storeId;

    @OneToMany
    private Set<Product> concurrentSkipListSet;

    public SkipListHolderClickbate(Set<Product> concurrentSkipListSet) {
        this.concurrentSkipListSet = concurrentSkipListSet;
    }

    public SkipListHolderClickbate() {
    concurrentSkipListSet = new ConcurrentSkipListSet<>(Comparator.comparingInt(Product::getProductId));
    }


    public Optional<Set<Product>> getStoreProducts() {
        return Optional.ofNullable(concurrentSkipListSet);
    }

    public void add(Product product) {
        concurrentSkipListSet.add(product);
    }

    public void remove(Product product) {
        concurrentSkipListSet.remove(product);
    }

    public Stream<Product> stream() {
        return concurrentSkipListSet.stream();
    }
}
