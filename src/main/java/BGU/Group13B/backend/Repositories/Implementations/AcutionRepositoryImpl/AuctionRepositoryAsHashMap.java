package BGU.Group13B.backend.Repositories.Implementations.AcutionRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IAuctionRepository;
import BGU.Group13B.backend.storePackage.PublicAuction;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.service.callbacks.AddToUserCart;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuctionRepositoryAsHashMap implements IAuctionRepository {
    private final AbstractMap<Integer/*storeId*/, List<PublicAuction>> storeAuctions;
    private AddToUserCart addToCart;

    public AuctionRepositoryAsHashMap() {
        storeAuctions = new ConcurrentHashMap<>();
    }

    @Override
    public void addNewAuctionForAProduct(int productId, double startingPrice, int storeId, LocalDateTime endTime) {
        if (storeAuctions.containsKey(storeId)) {
            var storeAuctionsList = storeAuctions.get(storeId);
            if (storeAuctionsList.stream().anyMatch(auction -> auction.getProductId() == productId)) {
                throw new IllegalArgumentException("There is already an auction for this product in this store");
            }
            storeAuctionsList.add(new PublicAuction(productId, startingPrice, startingPrice, endTime));
            scheduleAuctionEnd(storeId, productId, endTime);
        } else {
            List<PublicAuction> auctions = new LinkedList<>(List.of(new PublicAuction(productId, startingPrice, startingPrice, endTime)));
            storeAuctions.put(storeId, auctions);
            scheduleAuctionEnd(storeId, productId, endTime);
        }
    }

    private void scheduleAuctionEnd(int storeId, int productId, LocalDateTime endTime) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> removeAuction(storeId, productId),
                Duration.between(LocalDateTime.now(), endTime).toMillis(), TimeUnit.MILLISECONDS);
        executor.shutdown();
    }

    @Override
    public void removeAuction(int productId, int storeId) {
        if (!storeAuctions.containsKey(storeId)) {
            throw new IllegalArgumentException("There are no auctions for this store");
        }
        storeAuctions.computeIfPresent(storeId, (k, v) -> {
            var auction = getAuction(productId, storeId);
            v.remove(auction);
            auction.acquireLock();
            if (auction.getCurrentUserId() != -1)//if there exists a user that won the auction
                addToCart.apply(auction.getCurrentUserId(), storeId, productId, 1/*could be changed by the user*/);
            auction.releaseLock();
            return v.isEmpty() ? null : v;
        });
    }

    @Override
    public void updateAuction(int productId, int storeId, double newPrice, int newUserId) {
        if (!storeAuctions.containsKey(storeId)) {
            throw new IllegalArgumentException("There are no auctions for this store");
        }
        storeAuctions.computeIfPresent(storeId, (k, v) -> {
            v.stream()
                    .filter(auction -> auction.getProductId() == productId)
                    .findFirst()
                    .ifPresent(auction -> {
                        auction.acquireLock();
                        auction.setCurrentPrice(newPrice);//could raise an exception important to do first, releases the lock.
                        auction.setCurrentUserId(newUserId);
                        auction.releaseLock();
                    });
            return v;
        });
    }

    @Override
    public Optional<PublicAuctionInfo> getAuctionInfo(int productId, int storeId) {
        return storeAuctions.getOrDefault(storeId, Collections.emptyList())
                .stream()
                .filter(auction -> auction.getProductId() == productId)
                .findFirst()
                .map(auction ->
                        new PublicAuctionInfo(auction.getStartingPrice(), auction.getCurrentPrice(), auction.getEndTime()));
    }

    private PublicAuction getAuction(int productId, int storeId) {
        return storeAuctions.getOrDefault(storeId, Collections.emptyList())
                .stream()
                .filter(auction -> auction.getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no auction for this product in this store"));
    }

    @Override
    public void setAddToCartLambda(AddToUserCart addToUserCart) {
        this.addToCart = addToUserCart;
    }
}
