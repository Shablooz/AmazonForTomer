package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

import com.vaadin.flow.component.UI;
import java.util.Timer;
import java.util.TimerTask;

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

    private final Button authenticate = new Button("authenticate");

    private final TextField answer1;

    private final TextField answer3;

    private final TextField answer2;

    private final VerticalLayout authenticationLayout = new VerticalLayout();


    @Autowired
    public LoginView(Session session) {
        answer1 = new TextField("מה המספר של אמא שלך");
        answer2 = new TextField("אבא שלך ערומכו?");
        answer3 = new TextField("What is your favorite book or movie?");

        authenticationLayout.add(answer1, answer2, answer3, authenticate);
        authenticationLayout.setVisible(false);

        final int guestId = SessionToIdMapper.getInstance().getCurrentSessionId();

        // Use UI.access() to access the VaadinSession state on the UI thread
        VaadinSession web_session = VaadinSession.getCurrent();
        web_session.getSession().getId();
        RegisterView.setGuestId(guestId);

        setLoginButton(session, guestId);

        setregisterButton();

        FormLayout formLayout = new FormLayout();
        formLayout.add(username, password);
        add(formLayout, loginButton, registerButton);
        //setAlignItems(Alignment.CENTER);
        add(authenticationLayout);
    }

    private void setLoginButton(Session session,int guestId){
        loginButton.addClickListener(e -> {
            //need to change completely
            if(session.checkIfQuestionsExist(username.getValue())){
                Notification.show("Please answer the questions that u answered when registered!");
                setAuthenticateButton(session,guestId);

                return;
            } else if (session.login(guestId, username.getValue(), password.getValue(),
                    "", "", "") != 0) {
                Notification.show("Login successful");
                UI.getCurrent().navigate(HomeView.class);
            } else {
                Notification.show("Login failed");
            }
        });
    }


    private void setregisterButton(){
        registerButton.addClickListener(e -> {
            UI.getCurrent().navigate(RegisterView.class);
        });
    }


    private void setAuthenticateButton(Session session,int guestId){
        authenticationLayout.setVisible(true);
        authenticate.addClickListener(e -> {
            if (session.login(guestId, username.getValue(), password.getValue(),
                    answer1.getValue(), answer2.getValue(), answer3.getValue()) != 0){
                Notification.show("Login successful");
                UI.getCurrent().navigate(HomeView.class);
            }else{
                Notification.show("Login failed");
            }


        });
    }


}
