package BGU.Group13B.frontEnd;

import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.frontEnd.service.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Session _session(Market market) {
        return new Session(market);
    }
    @Bean
    public Market market() {
        return new Market();
    }
}
