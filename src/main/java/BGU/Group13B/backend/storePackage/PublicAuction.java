package BGU.Group13B.backend.storePackage;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class PublicAuction {

    private final int productId;
    private final double startingPrice;
    private double currentPrice;
    private final LocalDateTime endTime;
    private final ReentrantLock lock = new ReentrantLock();
    private int currentUserId;

    public PublicAuction(int productId, double startingPrice, double currentPrice, LocalDateTime endTime) {
        this.productId = productId;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.endTime = endTime;
        this.currentUserId = -1;
    }

    public int getProductId() {
        return productId;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double newPrice) {
        if (newPrice <= currentPrice) {
            if(lock.isLocked())//if the lock is locked, we need to unlock it before throwing the exception
                //we can reach this point only if the lock is locked, because the only place that calls this method is the updateAuction method in the AuctionRepository class
                lock.unlock();
            throw new IllegalArgumentException("The new price is lower or equal than the current price");
        }
        this.currentPrice = newPrice;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicAuction that = (PublicAuction) o;
        return productId == that.productId && startingPrice == that.startingPrice && currentPrice == that.currentPrice && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, startingPrice, currentPrice, endTime);
    }

    public void acquireLock() {
        this.lock.lock();
    }
    public void releaseLock() {
        this.lock.unlock();
    }
    public int getCurrentUserId() {
        return currentUserId;
    }
    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }
}
