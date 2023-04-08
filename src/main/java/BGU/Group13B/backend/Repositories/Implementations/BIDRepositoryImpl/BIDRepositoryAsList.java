package BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IBIDRepository;
import BGU.Group13B.backend.storePackage.BID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class BIDRepositoryAsList implements IBIDRepository {
    private final List<BID> bids;
    private AtomicInteger maxBidId;

    public BIDRepositoryAsList() {

        this.bids = new ArrayList<>();
        this.maxBidId = new AtomicInteger(0);
    }

    @Override
    public synchronized void addBID(int userId, int productId, double newProductPrice, int amount) {
        /*int maxUserBid = bids.stream().filter(bid -> bid.getUserId() == userId).
                map(BID::getBidId).max(Integer::compareTo).orElse(-1);*/

        this.bids.add(new BID(maxBidId.getAndIncrement(), userId, productId, newProductPrice, amount));
    }

    @Override
    public void removeBID(int bidId) {
        this.bids.removeIf(bid -> bid.getBidId() == bidId);
    }

    @Override
    public Optional<BID> getBID(int bidId) {
        var _bid = this.bids.stream().
                filter(bid -> bid.getBidId() == bidId).toList();
        if (_bid.isEmpty()) return Optional.empty();
        return Optional.ofNullable(_bid.get(0));
    }

    @Override
    public boolean contains(int productId) {
        return this.bids.stream().anyMatch(bid -> bid.getProductId() == productId);
    }

}
