package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@PageTitle("Register")
@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {

    private final TextField username;
    private final PasswordField password;
    private final EmailField email;

    private final TextField answer1;
    private final TextField answer2;
    private final TextField answer3;
    private final Button registerButton;
    private final DatePicker birthPicker;

    @Autowired
    public RegisterView(Session session) {
        super();
        username = new TextField("*Username");
        password = new PasswordField("*Password");
        answer1 = new TextField("your favorite color?");
        answer2 = new TextField("your favorite food?");
        answer3 = new TextField("your favorite book?");
        birthPicker = new DatePicker("*Birth date");
        email = new EmailField("*Email");
        registerButton = new Button("Register");
        registerButton.addClickListener(e -> {
            if (username.getValue().isEmpty() || password.getValue().isEmpty() || email.getValue().isEmpty()) {
                Notification.show("Please fill all fields");
                return;
            }
            try {
                session.register(SessionToIdMapper.getInstance().getCurrentSessionId()/*temp*/, username.getValue(), password.getValue(), email.getValue(),
                        answer1.getValue(), answer2.getValue(), answer3.getValue(), LocalDateTime.of(birthPicker.getValue(), LocalDateTime.now().toLocalTime()));
                Notification.show("Registered successfully");
                UI.getCurrent().navigate(LoginView.class);
            }catch (Exception exp){
                Notification.show(exp.getMessage());
            }
        });
        FormLayout formLayout = new FormLayout();
        formLayout.add(username, password, email,answer1,answer2,answer3,birthPicker);
        add(formLayout, registerButton);
    }
}
