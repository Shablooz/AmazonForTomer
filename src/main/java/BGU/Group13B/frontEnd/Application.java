package BGU.Group13B.frontEnd;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Timer;

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
@Push
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        //timer for deleting idle sessions
        //running the ui
        SpringApplication.run(Application.class, args);
        Timer timer = new Timer();
        timer.schedule(SessionToIdMapper.getInstance().kickExpired(), 5 * 60 * 1000, 5 * 60 * 1000);
    }
    @Bean
    CommandLineRunner commandLineRunner(PersonRepo personRepo){
        return args -> {
            Person person = new Person(22L,"Tomer");
            personRepo.save(person);
        };
    }


}
