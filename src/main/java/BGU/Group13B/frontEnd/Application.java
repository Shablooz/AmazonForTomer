package BGU.Group13B.frontEnd;

import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl.PairForDiscountService;
import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl.PairForDiscounts;
import BGU.Group13B.backend.User.User;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.ConfigurationFileParser;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static BGU.Group13B.service.SingletonCollection.setPurchaseHistoryRepository;
import static java.lang.Thread.sleep;

import java.time.LocalDate;
import java.util.Timer;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@Theme(value = "trading-system", variant = Lumo.DARK)
@ComponentScan(basePackages = "BGU.Group13B")
@EnableJpaRepositories(basePackages = {"BGU.Group13B.frontEnd", "BGU.Group13B.backend"})
@Push
@EntityScan(basePackages = {"BGU.Group13B.backend", "BGU.Group13B.frontEnd", "BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl"})
public class Application implements AppShellConfigurator {
    public static void main(String[] args) {
        //timer for deleting idle sessions
        //running the ui
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        SingletonCollection.setContext(context);
        //TODO load all repositories from db to memory
        SingletonCollection.setSession();
        SingletonCollection.setDailyUserTrafficRepository();
        SingletonCollection.setReviewRepository();
        SingletonCollection.setProductRepository();
        SingletonCollection.setStoreRepository();
        SingletonCollection.setStorePermissionRepository();
        SingletonCollection.setStoreScoreRepository();
        SingletonCollection.setUserPermissionRepository();
        SingletonCollection.setBasketRepository();
        SingletonCollection.setBasketProductRepository();
        SingletonCollection.setMessageRepository();
        SingletonCollection.setStoreMessagesRepository();
        SingletonCollection.setUserRepository();
        if (SingletonCollection.getUserRepository().getUser(1) == null) {
            int id = 1;
            SingletonCollection.getUserRepository().addUser(id, new User(id));
            SingletonCollection.getSession().register(id, "kingOfTheSheep", "SheePLover420",
                    "mrsheep@gmail.com", "11", "11", "11", LocalDate.MIN);
        }


        //need to access singleton collection here

        //remove those - examples
//        NoderSonService service = SingletonCollection.getContext().getBean(NoderSonService.class);
//        service.addPerson(new NoderSon("until when"));
//        service.addPerson(new NoderSon("and how much more"));
//        System.out.println(service.getNoder(1));


        SingletonCollection.setConditionRepository();
        SingletonCollection.setDiscountaccuRepository();
        SingletonCollection.setDiscountRepository();
        SingletonCollection.setStoreDiscountRootsRepository();
        SingletonCollection.setPurchasePolicyRootsRepository();
        SingletonCollection.setBidRepository();
        SingletonCollection.setPurchaseHistoryRepository();
        ConfigurationFileParser.parse();
    }


}
