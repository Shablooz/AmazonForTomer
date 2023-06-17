package BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.DailyUserTrafficRepositoryImpl.DailyUserTrafficRepositoryAsListService;
import BGU.Group13B.backend.Repositories.Interfaces.IBIDRepository;
import BGU.Group13B.backend.storePackage.BID;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
@Entity
public class BIDRepositoryAsList implements IBIDRepository {
    @Transient
    private boolean saveMode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "BIDRepositoryAsList_BIDS",
            joinColumns = {@JoinColumn(name = "BIDRepositoryAsList_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "BDI_id", referencedColumnName = "bidId")})

    private Set<BID> bids;
    private AtomicInteger maxBidId;

    public BIDRepositoryAsList() {
        this.bids = new ConcurrentSkipListSet<>();
        this.maxBidId = new AtomicInteger(0);
        this.saveMode = true;
    }

    public BIDRepositoryAsList(boolean saveMode) {
        this.bids = new ConcurrentSkipListSet<>();
        this.maxBidId = new AtomicInteger(0);
        this.saveMode = saveMode;
    }

    @Override
    public synchronized void addBID(int userId, int productId, double newProductPrice, int amount) {
        /*int maxUserBid = bids.stream().filter(bid -> bid.getUserId() == userId).
                map(BID::getBidId).max(Integer::compareTo).orElse(-1);*/

        this.bids.add(new BID(maxBidId.getAndIncrement(), userId, productId, newProductPrice, amount));
        save();
    }

    @Override
    public void removeBID(int bidId) {
        this.bids.removeIf(bid -> bid.getBidId() == bidId);
        save();
    }

    @Override
    public synchronized Optional<BID> getBID(int bidId) {
        var _bid = this.bids.stream().
                filter(bid -> bid.getBidId() == bidId).toList();
        if (_bid.isEmpty()) return Optional.empty();
        return Optional.ofNullable(_bid.get(0));
    }

    @Override
    public boolean contains(int productId) {
        return this.bids.stream().anyMatch(bid -> bid.getProductId() == productId);
    }

    @Override
    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }

    @Override
    public void reset() {
        this.bids.clear();
        this.maxBidId = new AtomicInteger(0);
        save();
    }

    public Set<BID> getBids() {
        return bids;
    }

    public void setBids(Set<BID> bids) {
        this.bids = bids;
    }

    public AtomicInteger getMaxBidId() {
        return maxBidId;
    }

    public void setMaxBidId(AtomicInteger maxBidId) {
        this.maxBidId = maxBidId;
    }

    public boolean isSaveMode() {
        return saveMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(BIDRepositoryAsListService.class).save(this);

    }
}
