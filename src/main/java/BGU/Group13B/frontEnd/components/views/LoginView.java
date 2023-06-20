package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.BroadCaster;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.SingletonCollection;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY_INLINE;


@Tag("login-view")
@Route(value = "login", layout = MainLayout.class)
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver{

    private final TextField username = new TextField("Username");

    private final PasswordField password = new PasswordField("Password");
    private final Button loginButton = new Button("Login");
    private final Button registerButton = new Button("Register");


    private final Button authenticate = new Button("authenticate");

    private final TextField answer1;

    private final TextField answer3;

    private final TextField answer2;

    private final VerticalLayout authenticationLayout = new VerticalLayout();
    private final Session session;

    @Autowired
    public LoginView(Session session) {
        this.session = session;
        answer1 = new TextField("favorite color?");
        answer2 = new TextField("favorite food?");
        answer3 = new TextField("bUgAati cOloR?");

        authenticationLayout.add(answer1, answer2, answer3, authenticate);
        authenticationLayout.setVisible(false);
        int guestId = SessionToIdMapper.getInstance().getCurrentSessionId();


        setLoginButton(session, guestId);

        setregisterButton();


        FormLayout formLayout = new FormLayout();
        formLayout.add(username, password);
        add(formLayout, loginButton, registerButton);
        //setAlignItems(Alignment.CENTER);
        add(authenticationLayout);
    }

    private void setLoginButton(Session session, int guestId) {
        loginButton.addClickListener(e -> {
            //need to change completely
            if (session.checkIfQuestionsExist(username.getValue())) {
                Notification.show("Please answer the questions that u answered when registered!");
                setAuthenticateButton(session, guestId);
                return;
            }
            try {

                int newId = session.login(guestId, username.getValue(), password.getValue(),
                        "", "", "");
                Notification.show("Login successful");
                SessionToIdMapper.getInstance().updateCurrentSession(newId);
                // SessionToIdMapper.getInstance().setRefreshRequired(true);
                UI.getCurrent().navigate(HomeView.class);
                UI.getCurrent().getPage().reload();
                var ui = UI.getCurrent();
                //Tomer section
                BroadCaster.register(newId, newMessage -> {
                    ui.access(() -> {
                        if (newMessage.startsWith("BID"))
                            createPurchaseProposalSubmitRequest(newMessage).open();
                        else
                            createSubmitSuccess(newMessage).open();

                    });
                });


                session.fetchMessages(newId);
            } catch (Exception ex) {
                Notification.show("Login failed");
            }

        });
    }


    private void setregisterButton() {
        registerButton.addClickListener(e -> {
            UI.getCurrent().navigate(RegisterView.class);
        });
    }


    private void hideNoneNeededAuthentication(Session session) {
        if (SingletonCollection.getUserRepository().checkIfUserExists(username.getValue()) == null)
            return;
        if (!session.SecurityAnswer1Exists(SingletonCollection.getUserRepository().checkIfUserExists(username.getValue()).getUserId())) {
            answer1.setVisible(false);
        }
        if (!session.SecurityAnswer2Exists(SingletonCollection.getUserRepository().checkIfUserExists(username.getValue()).getUserId())) {
            answer2.setVisible(false);
        }
        if (!session.SecurityAnswer3Exists(SingletonCollection.getUserRepository().checkIfUserExists(username.getValue()).getUserId())) {
            answer3.setVisible(false);
        }
    }

    private void setAuthenticateButton(Session session, int guestId) {
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
                // SessionToIdMapper.getInstance().setRefreshRequired(true);
                UI.getCurrent().navigate(HomeView.class);
                var ui = UI.getCurrent();
                //Tomer section
                BroadCaster.register(newId, newMessage -> {
                    ui.access(() -> createSubmitSuccess(newMessage).open());
                });


                session.fetchMessages(newId);

            } catch (Exception ex) {
                Notification.show("Login failed");
            }


        });
    }

    private Notification createPurchaseProposalSubmitRequest(String message) {
        Notification notification = new Notification();
        //notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        String newMessage = message.substring(message.indexOf("]") + 1);
        int managerId = SessionToIdMapper.getInstance().getCurrentSessionId();
        String[] storeProduct = message.substring(message.indexOf("[") + 1, message.indexOf("]")).split(",");
        int storeId = Integer.parseInt(storeProduct[0]);
        int productId = Integer.parseInt(storeProduct[1]);
        int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
        //accept button
        Button accept = new Button(VaadinIcon.CHECK_CIRCLE.create(), event -> {
            session.purchaseProposalApprove(managerId, storeId, productId, userId);
            notification.close();
        });
        accept.addThemeVariants(LUMO_TERTIARY_INLINE);
        //reject button
        Button reject = new Button(VaadinIcon.CLOSE_SMALL.create(), event -> {
            session.purchaseProposalReject(managerId, storeId, productId, userId);
            notification.close();
        });
        accept.setVisible(true);
        reject.setVisible(true);
        reject.addThemeVariants(LUMO_TERTIARY_INLINE);

        Div info = new Div(new Text(newMessage));
        VerticalLayout layout = new VerticalLayout(info, accept, reject);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        notification.add(layout);
        return notification;
    }

    private Notification createSubmitSuccess(String message) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        Icon icon = VaadinIcon.CHECK_CIRCLE.create();
        Div info;
        try {
            info = new Div(new Html(message));
        } catch (Exception e) {
            info = new Div(new Text(message));
        }

        HorizontalLayout layout = new HorizontalLayout(icon, info,
                createCloseBtn(notification));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);

        return notification;
    }

    private Button createCloseBtn(Notification notification) {
        Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(LUMO_TERTIARY_INLINE);

        return closeBtn;
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
        if (session.isUserLogged(userId))
            event.rerouteTo(HomeView.class);

    }
}
