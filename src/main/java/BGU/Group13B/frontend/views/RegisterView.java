package BGU.Group13B.frontend.views;

import BGU.Group13B.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {

    private final TextField username;
    private final PasswordField password;
    private final PasswordField confirmPassword;
    private final EmailField email;
    private static int guestId;
    private final Button registerButton;

    @Autowired
    public RegisterView(Session session) {
        //super();
        //int currentSession = 2;/*VaadinSession.getCurrent().getSession().getId();//not correct*/
        // You can initialise any data required for the connected UI components here.
        username = new TextField("Username");
        password = new PasswordField("Password");
        confirmPassword = new PasswordField("Confirm Password");
        email = new EmailField("Email");
        registerButton = new Button("Register");
        registerButton.addClickListener(e -> {
            if(username.getValue().isEmpty() || password.getValue().isEmpty() || email.getValue().isEmpty()) {
                Notification.show("Please fill all fields");
                return;
            }
            if(!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Passwords do not match");
                return;
            }
            session.register(guestId/*temp*/, username.getValue(), password.getValue(), email.getValue(),
                    "054-1234567", "1234", "answer3");
            UI.getCurrent().navigate(LoginView.class);
        });
        add(username, password, confirmPassword, email, registerButton);
    }
    public static void setGuestId(int _guestId) {
        guestId = _guestId;
    }
}
