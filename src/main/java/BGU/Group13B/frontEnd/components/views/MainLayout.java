package BGU.Group13B.frontEnd.components.views;


import BGU.Group13B.backend.User.Message;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.appnav.AppNav;
import BGU.Group13B.frontEnd.components.appnav.AppNavItem;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.VoidResponse;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private final Session session;
    private int USERID = 0;

    private final String MEMBER = "Member";
    private final String ADMIN = "Admin";
    private final String GUEST = "Guest";

    private Button loginButton = null;

    private Button logoutButton = null;
    private Button signUpButton = null;

    public interface VoidAction {
        void act();
    }

    private void setUSERID() {
        if (USERID == 0)
            USERID = SessionToIdMapper.getInstance().getCurrentSessionId();
    }

    @Autowired
    public MainLayout(Session session) {
        this.session = session;
        UI currentUI = UI.getCurrent();
        VaadinSession currentSession = currentUI.getSession();
        String sessionId = currentSession.getSession().getId();
        if (!SessionToIdMapper.getInstance().containsKey(sessionId)) {
            int tempId = session.enterAsGuest();
            SessionToIdMapper.getInstance().add(sessionId, tempId);
        }

        setUSERID();

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Button cartButton = new Button(new Icon(VaadinIcon.CART));
        cartButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("cart")));

        HorizontalLayout rightAlignment = rightAlignmentHeaderContext();

        addToNavbar(true, toggle, viewTitle, cartButton, rightAlignment);
    }

    private HorizontalLayout rightAlignmentHeaderContext() {
        HorizontalLayout rightAlignment = new HorizontalLayout();
        Button messageButton = messageDialog();
        Button test =new Button("PUSH");
        test.addClickListener(event->session.pushTest());
        rightAlignment.add(test);
        System.out.println("USERID: " + USERID + " is logged in: " + session.isUserLogged(USERID));
        if (session.isUserLogged(USERID))
            rightAlignment.add(messageButton);

        rightAlignment.setJustifyContentMode(FlexLayout.JustifyContentMode.END);
        rightAlignment.setWidthFull();
        rightAlignment.setSpacing(true);
        rightAlignment.add(new H1()); //need to be always the last one (create gap between the last component and the end of the screen)
        return rightAlignment;
    }

    private void addDrawerContent() {
        H1 appName = new H1("trading-system");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());
        FlexLayout flexLayout = new FlexLayout();
        flexLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.END);
        flexLayout.setWidthFull();
        //defining the buttons
        prepareLoginButton(flexLayout);
        prepareLogoutButton(flexLayout);
        prepareSignUpButton(flexLayout);
        if (!session.isUserLogged(SessionToIdMapper.getInstance().getCurrentSessionId()/*todo change to session*/)) {    //) {
            logoutButton.setVisible(false);
        } else {
            loginButton.setVisible(false);
            signUpButton.setVisible(false);
        }

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.add(flexLayout);

        addToDrawer(header, scroller, horizontalLayout, createFooter());
    }

    private void newMessageDialog(Dialog currentDialog) {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();
        session.clearMessageToReply(USERID);
        currentDialog.setHeaderTitle("New Messages");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button jmpMainDialog = new Button("Messages Center");
        Button nextMessageButton = new Button("next message");
        VerticalLayout verticalDialogMessage = new VerticalLayout();
        TextArea messageBody = new TextArea();
        Button replyButton = new Button("Reply");
        TextArea inputBody = new TextArea();
        Button sendMessageButton = new Button("Send Answer");

        Response<Message> messageResponse = session.readMessage(USERID);
        String message;
        if(messageResponse.getStatus()== Response.Status.SUCCESS)
        {
            message = messageResponse.getData().toString();
        }else
        {
            message = messageResponse.getMessage();
        }

        currentDialog.getFooter().add(jmpMainDialog);
        currentDialog.getFooter().add(nextMessageButton);
        currentDialog.add(verticalDialogMessage);

        messageBody.setWidthFull();
        messageBody.setMinWidth("300px");
        messageBody.setLabel("The message:");
        messageBody.setReadOnly(true);
        messageBody.setValue(message);
        verticalDialogMessage.add(messageBody);
        verticalDialogMessage.add(replyButton);

        inputBody.setWidthFull();
        inputBody.setLabel("Answer:");


        jmpMainDialog.addClickListener(event -> mainDialog(currentDialog));
        replyButton.addClickListener(event -> {
            verticalDialogMessage.remove(replyButton);
            verticalDialogMessage.add(inputBody, sendMessageButton);
        });


    }

        nextMessageButton.addClickListener(event -> newMessageDialog(currentDialog));
        sendMessageButton.addClickListener(event -> {
            Response<VoidResponse> response= session.replayMessage(USERID,inputBody.getValue());
            Notification notification= new Notification("Message sent",3000);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if(response.getStatus()== Response.Status.FAILURE){
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText(response.getMessage());
            }
            newMessageDialog(currentDialog);
            notification.open();
        });

    private void prepareLogoutButton(FlexLayout flexLayout) {
        logoutButton = new Button("Logout");
        logoutButton.addClickListener(event -> {
            session.logout(SessionToIdMapper.getInstance().getCurrentSessionId());
            SessionToIdMapper.getInstance().updateCurrentSession(session.enterAsGuest());
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        flexLayout.add(logoutButton);
    }

    private void oldMessageDialog(Dialog currentDialog) {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();
        session.clearMessageToReply(USERID);
        currentDialog.setHeaderTitle("Old Messages");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button jmpMainDialog = new Button("Messages Center");
        Button nextMessageButton = new Button("next message");
        VerticalLayout verticalDialogMessage = new VerticalLayout();
        TextArea messageBody = new TextArea();
        Button refreshMessagesButton = new Button("Refresh Messages");
        Response<Message> messageResponse = session.readOldMessage(USERID);
        String message;
        if(messageResponse.getStatus()== Response.Status.SUCCESS) {
            message = messageResponse.getData().toString();
        }else{
            message = messageResponse.getMessage();
        }

        currentDialog.getFooter().add(jmpMainDialog);
        currentDialog.getFooter().add(refreshMessagesButton, nextMessageButton);
        currentDialog.add(verticalDialogMessage);

        messageBody.setWidthFull();
        messageBody.setLabel("The message:");
        messageBody.setMinWidth("300px");
        messageBody.setReadOnly(true);
        messageBody.setValue(message);
        verticalDialogMessage.add(messageBody);


        jmpMainDialog.addClickListener(event -> mainDialog(currentDialog));
        nextMessageButton.addClickListener(event -> oldMessageDialog(currentDialog));
        refreshMessagesButton.addClickListener(event -> {
            session.refreshOldMessages(USERID);
            oldMessageDialog(currentDialog);
        });

    }

    private void openComplaintDialog(Dialog currentDialog) {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();
        session.clearMessageToReply(USERID);
        currentDialog.setHeaderTitle("Open Complaints");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button jmpMainDialog = new Button("Messages Center");
        VerticalLayout verticalDialogMessage = new VerticalLayout();
        TextField inputHeader = new TextField();
        TextArea inputBody = new TextArea();
        Button sendMessageButton = new Button("Send Complaint");


        currentDialog.getFooter().add(jmpMainDialog);
        currentDialog.add(verticalDialogMessage);

        verticalDialogMessage.add(inputHeader);
        verticalDialogMessage.add(inputBody);
        verticalDialogMessage.add(sendMessageButton);

        inputHeader.setWidthFull();
        inputHeader.setLabel("Subject:");
        inputBody.setWidthFull();
        inputBody.setLabel("Body:");
        inputBody.setMinWidth("300px");

        jmpMainDialog.addClickListener(event -> mainDialog(currentDialog));
        sendMessageButton.addClickListener(event -> {
            Response<VoidResponse> messageResponse= session.openComplaint(USERID,inputHeader.getValue(),inputBody.getValue());
            openComplaintDialog(currentDialog);
            Notification notification=new Notification("Complaint sent", 3000);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if(messageResponse.getStatus()== Response.Status.FAILURE){
                notification.setText(messageResponse.getMessage());
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            notification.open();

        });
    }

    private void showComplaintDialog(Dialog currentDialog) {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();
        currentDialog.setHeaderTitle("Complaints");
        session.clearMessageToReply(USERID);
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button jmpMainDialog = new Button("Messages Center");
        Button nextMessageButton = new Button("Next Complaint");
        VerticalLayout verticalDialogMessage = new VerticalLayout();
        TextArea messageBody = new TextArea();
        Button replyButton = new Button("Reply");
        TextArea inputBody = new TextArea();
        Button sendMessageButton = new Button("Send Answer");
        Response<Message> messageResponse = session.getComplaint(USERID);
        String message;
        if(messageResponse.getStatus()== Response.Status.SUCCESS) {
            message = messageResponse.getData().toString();
        }else{
            message = messageResponse.getMessage();
        }

        currentDialog.getFooter().add(jmpMainDialog);
        currentDialog.getFooter().add(nextMessageButton);
        currentDialog.add(verticalDialogMessage);
        verticalDialogMessage.add(messageBody);
        verticalDialogMessage.add(replyButton);


        messageBody.setWidthFull();
        messageBody.setMinWidth("300px");
        messageBody.setLabel("The Complaint:");
        messageBody.setReadOnly(true);
        messageBody.setValue(message);
        inputBody.setWidthFull();
        inputBody.setLabel("Answer:");


        jmpMainDialog.addClickListener(event -> mainDialog(currentDialog));
        replyButton.addClickListener(event -> {
            verticalDialogMessage.remove(replyButton);
            verticalDialogMessage.add(inputBody, sendMessageButton);
        });

        nextMessageButton.addClickListener(event -> showComplaintDialog(currentDialog));
        sendMessageButton.addClickListener(event -> {
            Response<VoidResponse> response= session.answerComplaint(USERID,inputBody.getValue());
            showComplaintDialog(currentDialog);
            Notification notification=new Notification("Message sent", 3000);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if(response.getStatus()== Response.Status.FAILURE) {
                notification.setText(response.getMessage());
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            notification.open();

        });

    }

    private void sendMessageDialog(Dialog currentDialog) {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();
        session.clearMessageToReply(USERID);
        currentDialog.setHeaderTitle("Send Message");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button jmpMainDialog = new Button("Messages Center");
        VerticalLayout verticalDialogMessage = new VerticalLayout();
        TextField receiverName = new TextField();
        TextField inputHeader = new TextField();
        TextArea inputBody = new TextArea();
        Button sendMessageButton = new Button("Send Message");


        currentDialog.getFooter().add(jmpMainDialog);
        currentDialog.add(verticalDialogMessage);

        verticalDialogMessage.add(receiverName);
        verticalDialogMessage.add(inputHeader);
        verticalDialogMessage.add(inputBody);
        verticalDialogMessage.add(sendMessageButton);

        receiverName.setWidthFull();
        receiverName.setLabel("Receiver Name:");
        inputHeader.setWidthFull();
        inputHeader.setLabel("Subject:");
        inputBody.setWidthFull();
        inputBody.setMinWidth("300px");
        inputBody.setLabel("Body:");


        jmpMainDialog.addClickListener(event -> mainDialog(currentDialog));
        sendMessageButton.addClickListener(event -> {
            Response<VoidResponse> messageResponse= session.sendMassageAdmin(USERID,receiverName.getValue(),inputHeader.getValue(),inputBody.getValue());
            sendMessageDialog(currentDialog);
            Notification notification=new Notification("Message sent", 3000);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if(messageResponse.getStatus()== Response.Status.FAILURE){
                notification.setText(messageResponse.getMessage());
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            notification.open();

        });

    }

    private void mainDialog(Dialog currentDialog) {

        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();

        currentDialog.setHeaderTitle("Message Center");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        VerticalLayout buttonsLayout = new VerticalLayout();
        Button newMessagesButton = new Button("New Messages");
        newMessagesButton.addClickListener(event -> {
            newMessageDialog(currentDialog);
        });

        Button oldMessagesButton = new Button("Old Messages");
        oldMessagesButton.addClickListener(event -> {
            oldMessageDialog(currentDialog);
        });

        Button openComplaintsButton = new Button("Open Complaints");
        openComplaintsButton.addClickListener(event -> {
            openComplaintDialog(currentDialog);
        });

        Button showComplaintsButton = new Button("Show Complaints");
        showComplaintsButton.addClickListener(event -> {
            showComplaintDialog(currentDialog);
        });

        Button sendMessageButton = new Button("Send Message");
        sendMessageButton.addClickListener(event -> {
            sendMessageDialog(currentDialog);
        });

        buttonsLayout.add(newMessagesButton, oldMessagesButton, openComplaintsButton);
        currentDialog.add(buttonsLayout);

        if (session.getUserStatus(USERID).equals(ADMIN)) {
            buttonsLayout.add(showComplaintsButton, sendMessageButton);
        }

    }

    private Button messageDialog() {
        Button messageButton = new Button("Message Center");
        messageButton.setIcon(VaadinIcon.COMMENTS_O.create());
        Dialog myDialog = new Dialog();

        mainDialog(myDialog);

        messageButton.addClickListener(event -> myDialog.open());
        return messageButton;
    }


    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();
        //
        nav.addItem(new AppNavItem("Home View", HomeView.class, LineAwesomeIcon.HOME_SOLID.create()));

        //my stores
        if (true) { //TODO! check if the user is logged in
            nav.addItem(new AppNavItem("My Stores", MyStoresView.class, LineAwesomeIcon.STORE_SOLID.create()));
        }


        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        if (session.isUserLogged(SessionToIdMapper.getInstance().getCurrentSessionId())) {
            this.loginButton.setVisible(false);
            this.signUpButton.setVisible(false);
            this.logoutButton.setVisible(true);
        }
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
