package BGU.Group13B.backend.storePackage;
import jakarta.persistence.*;
import jakartac.Cache.Cache;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
@Entity
public class BID implements Comparable<BID>{
    @Id
    private int bidId;
    private int userId;
    private int productId;
    private double newProductPrice;
    private int amount;

    @Cache(policy = Cache.PolicyType.LRU)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name ="managerIds_collection")
        private Set<Integer/*managerIds*/> approvedBy;
    private volatile boolean rejected = false;

    public BID(int bidId, int userId, int productId, double newProductPrice, int amount) {
        this.bidId = bidId;
        this.userId = userId;
        this.productId = productId;
        this.newProductPrice = newProductPrice;
        this.amount = amount;
        approvedBy = new ConcurrentSkipListSet<>();
    }

    public BID() {
        this.bidId = 420;
        this.userId = 420;
        this.productId = 420;
        this.newProductPrice = 420;
        this.amount = 420;
        approvedBy = null;
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

    @Override
    public int compareTo(BID o) {
        return Integer.compare(this.bidId, o.bidId);
    }

    public Set<Integer> getApprovedBy() {
        return approvedBy;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setNewProductPrice(double newProductPrice) {
        this.newProductPrice = newProductPrice;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setApprovedBy(Set<Integer> approvedBy) {
        this.approvedBy = approvedBy;
    }
}
