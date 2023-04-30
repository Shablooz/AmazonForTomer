package BGU.Group13B.backend.Repositories.Implementations.AcutionRepositoryImpl;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IAuctionRepository;
import BGU.Group13B.backend.storePackage.PublicAuction;
import BGU.Group13B.backend.storePackage.PublicAuctionInfo;
import BGU.Group13B.frontEnd.service.SingletonCollection;
import BGU.Group13B.frontEnd.service.callbacks.AddToUserCart;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class AuctionRepositoryAsHashMap implements IAuctionRepository {
    private final AbstractMap<Integer/*storeId*/, List<PublicAuction>> storeAuctions;
    private final AddToUserCart addToCart;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<Pair<Integer, Integer>, Future<?>> scheduledFutureHashMap = new ConcurrentHashMap();

    public AuctionRepositoryAsHashMap() {
        storeAuctions = new ConcurrentHashMap<>();
        addToCart = SingletonCollection.getAddToUserCart();
    }

    public AuctionRepositoryAsHashMap(AddToUserCart addToUserCart) {
        storeAuctions = new ConcurrentHashMap<>();
        this.addToCart = addToUserCart;
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
        var scheduledFuture = executor.schedule(() -> endAuction(storeId, productId),
                Duration.between(LocalDateTime.now(), endTime).toMillis(), TimeUnit.MILLISECONDS);
        scheduledFutureHashMap.put(Pair.of(storeId, productId), scheduledFuture);


        //executor.shutdown();
    }

    @Override
    public synchronized void endAuction(int productId, int storeId) {
        if (scheduledFutureHashMap.get(Pair.of(storeId, productId)) != null
                && !scheduledFutureHashMap.get(Pair.of(storeId, productId)).isDone()) {
            scheduledFutureHashMap.get(Pair.of(storeId, productId)).cancel(true);
            scheduledFutureHashMap.remove(Pair.of(storeId, productId));
        }
        if (!storeAuctions.containsKey(storeId)) {
            throw new IllegalArgumentException("There are no auctions for this store");
        }
        storeAuctions.computeIfPresent(storeId, (k, v) -> {
            var auction = getAuction(productId, storeId);
            v.remove(auction);
            auction.acquireLock();
            if (auction.getCurrentUserId() != -1) {//if there exists a user that won the auction
                addToCart.apply(auction.getCurrentUserId(), storeId, productId /*could be changed by the user*/);
                auction.setCurrentUserId(-1);
            } else
                System.out.println("[DEBUG]:No one won the auction");//add to logger
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


}
