package BGU.Group13B.backend.storePackage;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class BID {
    private final int bidId;
    private final int userId;
    private final int productId;
    private final double newProductPrice;
    private final int amount;
    private final Set<Integer/*managerIds*/> approvedBy;
    private volatile boolean rejected = false;

    public BID(int bidId, int userId, int productId, double newProductPrice, int amount) {
        this.bidId = bidId;
        this.userId = userId;
        this.productId = productId;
        this.newProductPrice = newProductPrice;
        this.amount = amount;
        approvedBy = new ConcurrentSkipListSet<>();
    }

    public int getUserId() {
        return userId;
    }

    public int getProductId() {
        return productId;
    }

    public double getNewProductPrice() {
        return newProductPrice;
    }

    public int getBidId() {
        return bidId;
    }

    public int getAmount() {
        return amount;
    }
    public synchronized void approve(int managerId){
        approvedBy.add(managerId);
    }

    public synchronized boolean isRejected() {
        return rejected;
    }
    public synchronized void reject(){
        rejected = true;
    }

    public synchronized boolean approvedByAll(Set<Integer> managers) {
        return approvedBy.containsAll(managers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BID bid = (BID) o;
        return bidId == bid.bidId && userId == bid.userId && productId == bid.productId && Double.compare(bid.newProductPrice, newProductPrice) == 0 && amount == bid.amount && rejected == bid.rejected && approvedBy.equals(bid.approvedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bidId, userId, productId, newProductPrice, amount, approvedBy, rejected);
    }
}
