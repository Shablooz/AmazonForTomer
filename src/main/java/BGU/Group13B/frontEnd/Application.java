package BGU.Group13B.frontEnd;

import BGU.Group13B.service.SingletonCollection;
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

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "trading-system", variant = Lumo.DARK)
@ComponentScan(basePackages = "BGU.Group13B")
@EnableJpaRepositories(basePackages = {"BGU.Group13B.frontEnd","BGU.Group13B.backend"})
@Push
@EntityScan(basePackages = {"BGU.Group13B.backend","BGU.Group13B.frontEnd","BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl"})
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        //timer for deleting idle sessions
        //running the ui
        ConfigurableApplicationContext context =SpringApplication.run(Application.class, args);

        SingletonCollection.setContext(context);
        //TODO load all repositories from db to memory
        SingletonCollection.setReviewRepository();
        SingletonCollection.setProductRepository();
        SingletonCollection.setConditionRepository();
        SingletonCollection.setDiscountRepository();
        //need to access singleton collection here

        //remove those - examples
        NoderSonService service = SingletonCollection.getContext().getBean(NoderSonService.class);
        service.addPerson(new NoderSon("until when"));
        service.addPerson(new NoderSon("and how much more"));
        System.out.println(service.getNoder(1));



    }

    public void setup(){





        //Timer timer = new Timer();
        //timer.schedule(SessionToIdMapper.getInstance().kickExpired(), 5 * 60 * 1000, 5 * 60 * 1000);
    }

}




//Grade grade1 = new Grade(4444, 99999);
//Preson person1 = new Preson(2222, "rotem");
//person1.addGrade(grade1);
//ser.addPerson(person1);
//Preson p=ser.getPerson(2222);

//Grade grade2 = new Grade(888, 789789);
//Preson person2 = new Preson(4848, "momo");
//person2.addGrade(grade2);

//Grade grade3=new Grade(99,10);
//person2.addReview(grade3,9);
//List<Grade> ls= p.getGrades();


//serPerson ser2=SingletonCollection.getContext().getBean(serPerson.class);

//Preson p1=ser2.getPerson(4848);
//ser2.addPerson(person2);

