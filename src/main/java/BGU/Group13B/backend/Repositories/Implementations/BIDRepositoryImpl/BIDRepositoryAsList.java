package BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IBIDRepository;
import BGU.Group13B.backend.storePackage.BID;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BIDRepositoryAsList implements IBIDRepository {
    private final List<BID> bids;

    public BIDRepositoryAsList() {
        this.bids = new ArrayList<>();
    }

    @Override
    public void addBID(int userId, int productId, double newProductPrice, int amount) {
        int maxUserBid = bids.stream().filter(bid -> bid.getUserId() == userId).
                map(BID::getBidId).max(Integer::compareTo).get();
        this.bids.add(new BID(maxUserBid, userId, productId, newProductPrice, amount));
    }

    @Override
    public void removeBID(int userId, int bidId) {
        this.bids.removeIf(bid -> bid.getBidId() == bidId && bid.getUserId() == userId);
    }

    @Override
    public BID getBID(int userId, int bidId) {
        var _bid = this.bids.stream().
                filter(bid -> bid.getBidId() == bidId && bid.getUserId() == userId).toList();
        if (_bid.isEmpty()) return null;
        return _bid.get(0);
    }

    @Override
    public boolean contains(int userId, int productId) {
        return this.bids.stream().anyMatch(bid -> bid.getUserId() == userId && bid.getProductId() == productId);
    }

}
