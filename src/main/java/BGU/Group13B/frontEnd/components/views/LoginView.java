package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.SingletonCollection;
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
        answer1 = new TextField("favorite color?");
        answer2 = new TextField("favorite food?");
        answer3 = new TextField("bUgAati cOloR?");

        authenticationLayout.add(answer1, answer2, answer3, authenticate);
        authenticationLayout.setVisible(false);
        int guestId = SessionToIdMapper.getInstance().getCurrentSessionId();

        // Use UI.access() to access the VaadinSession state on the UI thread
        VaadinSession web_session = VaadinSession.getCurrent();
        web_session.getSession().getId();

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
            } else {
                try {
                    int newId = session.login(guestId, username.getValue(), password.getValue(),
                            "", "", "");
                    Notification.show("Login successful");
                    SessionToIdMapper.getInstance().updateCurrentSession(newId);
                    UI.getCurrent().navigate(HomeView.class);
                }catch (Exception ex){
                    Notification.show("Login failed");
                }
            }
        });
    }


    private void setregisterButton(){
        registerButton.addClickListener(e -> {
            UI.getCurrent().navigate(RegisterView.class);
        });
    }

    private void hideNoneNeededAuthentication(Session session){
        if(SingletonCollection.getUserRepository().checkIfUserExists(username.getValue()) == null)
            return;
        if(!session.SecurityAnswer1Exists(SingletonCollection.getUserRepository().checkIfUserExists(username.getValue()).getUserId())){
            answer1.setVisible(false);
        }
        if(!session.SecurityAnswer2Exists(SingletonCollection.getUserRepository().checkIfUserExists(username.getValue()).getUserId())){
            answer2.setVisible(false);
        }
        if(!session.SecurityAnswer3Exists(SingletonCollection.getUserRepository().checkIfUserExists(username.getValue()).getUserId())){
            answer3.setVisible(false);
        }
    }
    private void setAuthenticateButton(Session session,int guestId){
        authenticationLayout.setVisible(true);
        answer1.setVisible(true);
        answer2.setVisible(true);
        answer3.setVisible(true);
        hideNoneNeededAuthentication(session);
        authenticate.addClickListener(e -> {
            try {
                int newId = session.login(guestId, username.getValue(), password.getValue(),
                        answer1.getValue(), answer2.getValue(), answer3.getValue());
                Notification.show("Login successful");
                SessionToIdMapper.getInstance().updateCurrentSession(newId);
                UI.getCurrent().navigate(HomeView.class);
            }catch (Exception ex){
                Notification.show("Login failed");
            }


        });
    }


}
