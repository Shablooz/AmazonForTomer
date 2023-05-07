/*
package BGU.Group13B.frontend;

import BGU.Group13B.frontend.views.HomeView;
import BGU.Group13B.frontend.views.LoginView;
import BGU.Group13B.frontend.View_.MainLayout;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "BGU.Group13B")

@Route(value = "", layout = MainLayout.class)
public class Application extends VerticalLayout implements AppShellConfigurator, RouterLayout, BeforeEnterObserver {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private final Session _session;

    @Autowired
    public Application(Session session) {
       */
/* add("Welcome to the system!");
        //add login description
        Div loginDescription = new Div(new Text("Please login to continue"));
        Button loginButton = new Button("Login");
        loginButton.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
        //put the text in the top right corner


        setUpperRightComponent(loginDescription, 0, 0);
        setUpperRightComponent(loginButton, 50, 50);
        if (session.isUserLoggedIn())
            loginButton.setVisible(false);
        add(loginDescription, loginButton);*//*

        _session = session;
    }

    private void setUpperRightComponent(Component component, int topMargin, int rightMargin) {
        component.getElement().getStyle().set("position", "absolute");
        component.getElement().getStyle().set("top", "" + topMargin + "px");
        component.getElement().getStyle().set("right", "0" + rightMargin + "px");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (_session.isUserLoggedIn()) {
            event.rerouteTo(HomeView.class);
        }
    }
    */
/*private void addRegisterLink() {
        //add register router link
        RouterLink registerLink = new RouterLink("Register", RegisterView.class);
        add(registerLink);
    }*//*

}
*/
