package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Tag;

import com.vaadin.flow.component.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;


@Tag("login-view")
@Route(value = "login", layout = MainLayout.class)
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    private final SessionToIdMapper sessionToIdMapper = SessionToIdMapper.getInstance();
    private final TextField username = new TextField("Username");

    private final PasswordField password = new PasswordField("Password");
    private final Button loginButton = new Button("Login");
    private final Button registerButton = new Button("Register");

    @Autowired
    public LoginView(Session session) {
        //code below logs as guest and puts the guest id in the hashmap
        final int guestId = session.enterAsGuest();
        UI currentUI = UI.getCurrent();
        VaadinSession currentSession = currentUI.getSession();
        String sessionId = currentSession.getSession().getId();
        sessionToIdMapper.add(sessionId, guestId);

        // Use UI.access() to access the VaadinSession state on the UI thread

        VaadinSession web_session = VaadinSession.getCurrent();
        web_session.getSession().getId();
        RegisterView.setGuestId(guestId);
        // You can initialise any data required for the connected UI components here.
        loginButton.addClickListener(e -> {
            //need to change completely
            if (session.login(guestId, username.getValue(), password.getValue(),
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
        FormLayout formLayout = new FormLayout();
        formLayout.add(username, password);
        add(formLayout, loginButton, registerButton);
        //setAlignItems(Alignment.CENTER);
    }


}
