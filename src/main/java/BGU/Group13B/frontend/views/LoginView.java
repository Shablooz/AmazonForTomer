package BGU.Group13B.frontend.views;

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


    private final TextField username = new TextField("Username");

    private final PasswordField password = new PasswordField("Password");
    private final Button loginButton = new Button("Login");
    private final Button registerButton = new Button("Register");

    @Autowired
    public LoginView(Session session) {
        final int _guestId = session.enterAsGuest();

        // Use UI.access() to access the VaadinSession state on the UI thread

        VaadinSession web_session = VaadinSession.getCurrent();
        web_session.getSession().getId();
        RegisterView.setGuestId(_guestId);// todo replace with web_session create hashmap
        loginButton.addClickListener(e -> {
            if (session.login(_guestId, username.getValue(), password.getValue(),
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
        // Create a HorizontalLayout to hold the buttons
/*
            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.add(loginButton, registerButton);

            // Create a FormLayout to hold the text fields
            FormLayout formLayout = new FormLayout();
            formLayout.add(username, password);

            // Create a VerticalLayout to hold the form and button layouts
            VerticalLayout mainLayout = new VerticalLayout();
            mainLayout.add(formLayout, buttonLayout);

            add(mainLayout);
*/

    }


}
