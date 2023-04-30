package BGU.Group13B.frontEnd;

import BGU.Group13B.backend.User.User;
import BGU.Group13B.frontEnd.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("register")
public class RegisterView extends VerticalLayout {

    private final TextField username;
    private final TextField password;
    private final TextField email;
    private static int guestId;
    private final Button registerButton;

    @Autowired
    public RegisterView(Session _session) {
        super();
        //int currentSession = 2;/*VaadinSession.getCurrent().getSession().getId();//not correct*/
        // You can initialise any data required for the connected UI components here.
        username = new TextField("Username");
        password = new TextField("Password");
        email = new TextField("Email");
        registerButton = new Button("Register");
        registerButton.addClickListener(e -> {
            _session.register(guestId/*temp*/, username.getValue(), password.getValue(), email.getValue(),
                    "054-1234567", "1234", "answer3");
            //navigate(LoginView.class);
            UI.getCurrent().navigate(LoginView.class);
        });
        add(username, password, email, registerButton);
    }
    public static void setGuestId(int _guestId) {
        guestId = _guestId;
    }
}
