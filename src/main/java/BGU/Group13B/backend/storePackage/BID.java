package BGU.Group13B.backend.storePackage;

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
}
