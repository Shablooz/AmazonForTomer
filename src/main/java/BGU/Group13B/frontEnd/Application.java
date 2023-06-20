package BGU.Group13B.frontEnd;

import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl.PairForDiscountService;
import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryImpl.PairForDiscounts;
import BGU.Group13B.backend.User.User;
import BGU.Group13B.service.Session;
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
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.logging.Logger;

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
    private static final Logger LOGGER_INFO = Logger.getLogger(Session.class.getName());

    static {
        SingletonCollection.setFileHandler(LOGGER_INFO, true);
    }

    public static void main(String[] args) {
        LOGGER_INFO.info(String.valueOf(SingletonCollection.databaseExists()));

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

        if (SingletonCollection.getUserRepository().getUser(1) == null) {
            int id = 1;
            SingletonCollection.getUserRepository().addUser(id, new User(id));
            SingletonCollection.getSession().register(id, "kingOfTheSheep", "SheePLover420",
                    "mrsheep@gmail.com", "11", "11", "11", LocalDateTime.now().minusYears(100));
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
        SingletonCollection.setStoreMessagesRepository();
        SingletonCollection.setUserRepository();
        SingletonCollection.getUserRepository().logoutAllUsers();
        ConfigurationFileParser.parse();
        SingletonCollection.getUserRepository().logoutAllUsers();

        LOGGER_INFO.info("Application finished loading up 🐑");
        LOGGER_INFO.info(String.valueOf(SingletonCollection.databaseExists()));

    }


}
