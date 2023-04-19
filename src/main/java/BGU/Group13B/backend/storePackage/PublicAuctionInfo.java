package BGU.Group13B.backend.storePackage;

import java.time.LocalDateTime;

public record PublicAuctionInfo(double startingPrice, double currentPrice, LocalDateTime lastDate) {
}
