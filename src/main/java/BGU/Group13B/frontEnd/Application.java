package BGU.Group13B.frontEnd;

import BGU.Group13B.frontEnd.View.HomeView;
import BGU.Group13B.frontEnd.service.ISession;
import BGU.Group13B.frontEnd.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication

@Route("")
public class Application extends VerticalLayout implements AppShellConfigurator, RouterLayout,AfterNavigationObserver {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    private Session _session;

    @Autowired
    public Application(Session session) {
        add("Welcome to the system!");
        _session = session;

//        int guestId = session.enterAsGuest();
//        if (session.isUserLoggedIn()) {
//            UI.getCurrent().navigate(HomeView.class);
//            return;
//        }
//        LoginView loginView = new LoginView(session);
//        add(loginView);
        //UI.getCurrent().access(this::addRegisterLink);
        //add register router link
        //addRegisterLink();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (!_session.isUserLoggedIn()) {
            LoginView loginView = new LoginView(_session);
            add(loginView);
        } else {
            UI.getCurrent().navigate(HomeView.class);
        }
    }
    private void addRegisterLink() {
        //add register router link
        RouterLink registerLink = new RouterLink("Register", RegisterView.class);
        add(registerLink);
    }
}
