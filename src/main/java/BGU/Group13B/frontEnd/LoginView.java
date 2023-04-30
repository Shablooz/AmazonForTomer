package BGU.Group13B.frontEnd;

import BGU.Group13B.frontEnd.View.HomeView;
import BGU.Group13B.frontEnd.service.Session;
import com.vaadin.flow.component.Tag;

import com.vaadin.flow.component.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;


@Tag("login-view")
@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {


    private final TextField username = new TextField("Username");

    private final TextField password = new TextField("Password");
    private final Button loginButton = new Button("Login");
    private final Button registerButton = new Button("Register");
    @Autowired
    public LoginView(Session _session) {
        int[] _guestId ={_session.enterAsGuest()};

        // Use UI.access() to access the VaadinSession state on the UI thread
        UI.getCurrent().access(() -> {
            VaadinSession session = VaadinSession.getCurrent();
            RegisterView.setGuestId(_guestId[0]);
            // You can initialise any data required for the connected UI components here.
            loginButton.addClickListener(e -> {
                if (_session.login(_guestId[0], username.getValue(), password.getValue(),
                        "054-1234567", "1234", "answer3") != 0) {
                    Notification.show("Login successful");
                    UI.getCurrent().navigate(HomeView.class);
                } else {
                    Notification.show("Login failed");
                }
            });

            registerButton.addClickListener(e -> {
                UI.getCurrent().navigate(RegisterView.class);
            });

            add(username, password, loginButton, registerButton);
            //setAlignItems(Alignment.CENTER);
        });
    }


}
