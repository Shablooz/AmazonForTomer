package BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl;

import BGU.Group13B.backend.storePackage.Product;
import jakarta.persistence.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

@Entity
public class SkipListHolderClickbate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "SkipListHolderClickbate_Product",
            joinColumns = {@JoinColumn(name = "SkipListHolderClickbate_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "product_Id", referencedColumnName = "productId")})
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

    public boolean isEmpty() {
        return concurrentSkipListSet.isEmpty();
    }

    public Stream<Product> stream() {
        return concurrentSkipListSet.stream();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Product> getConcurrentSkipListSet() {
        return concurrentSkipListSet;
    }

    public void setConcurrentSkipListSet(Set<Product> concurrentSkipListSet) {
        this.concurrentSkipListSet = concurrentSkipListSet;
    }
}
