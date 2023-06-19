package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.store.products.StoreProductsLayout;
import BGU.Group13B.frontEnd.components.store.workers.StoreWorkersLayout;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.VoidResponse;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.stream.Collectors;


@PageTitle("Store")
@Route(value = "store", layout = MainLayout.class)
public class StoreView extends VerticalLayout implements HasUrlParameter<Integer>, BeforeEnterObserver, ResponseHandler {

    private final Session session;
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private WorkerCard currentWorker;
    private int storeId = -1;
    private StoreInfo storeInfo;
    private List<WorkerCard> workers;
    private HashMap<Integer, String> userIdToUsername;
    private Set<ProductInfo> products;
    private boolean isAdmin = false;
    private boolean isHidden = false;
    private boolean terminate = false;

    //components
    private VerticalLayout headerLayout;
    private HorizontalLayout header_name_score;
    private HorizontalLayout scoreLabelLayout;
    private VerticalLayout scoreLayout;
    private ProgressBar scoreBar;
    private Div scoreLabel;
    private Icon hiddenStoreIcon;
    private HorizontalLayout bodyLayout;
    private Button hideStoreButton;
    private Button unhideStoreButton;
    private Button deleteStoreButton;
    private Button storePurchaseHistoryButton;
    private Button myVotesButton;
    private Button manageDiscountsButton;
    private Button managePurchasePolicyButton;
    private HorizontalLayout bottomButtonsLayout;
    private Button storeMessagesButton;
    private StoreProductsLayout storeProductsLayout;
    private StoreWorkersLayout storeWorkersLayout;
    private Button storeIncomeButton;




    @Autowired
    public StoreView(Session session) {
        super();
        this.session = session;
    }

    private void start(){
        this.removeAll();
        init_dataFields();
        //demoData();
        getData();
        if(terminate){
            return;
        }
        getCurrentWorkerCard();
        init_components();

        init_header();
        init_body();
        init_bottomButtons();
        setInvisibleBasedOnPermissions();
        setSizeFull();
    }

    private void setInvisibleBasedOnPermissions(){
        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.HISTORY)){
            storePurchaseHistoryButton.setVisible(false);
        }

        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.STAFF)){
            myVotesButton.setVisible(false);
        }

        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.POLICIES)){
            managePurchasePolicyButton.setVisible(false);
            manageDiscountsButton.setVisible(false);
        }

        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.FONLY)){
            hideStoreButton.setVisible(false);
            unhideStoreButton.setVisible(false);
        }

        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.WORKERS_INFO)){
            storeWorkersLayout.setVisible(false);
        }

        if(!isAdmin){
            deleteStoreButton.setVisible(false);
        }

        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.STATS)){
            storeIncomeButton.setVisible(false);
        }

    }


    private void init_components(){
        headerLayout = new VerticalLayout();
        header_name_score = new HorizontalLayout();
        scoreLabelLayout = new HorizontalLayout();
        scoreLayout = new VerticalLayout();
        scoreBar = new ProgressBar();
        scoreLabel = new Div();
        bodyLayout = new HorizontalLayout();
        bottomButtonsLayout = new HorizontalLayout();
        hideStoreButton = new Button("Hide Store");
        unhideStoreButton = new Button("Reopen Store");
        deleteStoreButton = new Button("Delete Store");
        storePurchaseHistoryButton = new Button("Store Purchase History");
        myVotesButton = new Button("Owner Vote");
        manageDiscountsButton = new Button("Manage Discounts");
        managePurchasePolicyButton = new Button("Manage Purchase Policy");
        storeIncomeButton = new Button("Store Income");
        storeMessagesButton = messageStoreDialog();
        hiddenStoreIcon = new Icon(VaadinIcon.EYE_SLASH);
    }

    private void init_header(){
        // Define score bar
        scoreBar.setMax(5);
        scoreBar.setMin(0);
        double roundedStoreScore = getRoundedScore(storeInfo.storeScore());
        scoreBar.setValue(roundedStoreScore);
        scoreBar.setClassName(String.valueOf(roundedStoreScore));
        scoreBar.getStyle().set("margin-top", "0");

        // Define score label
        scoreLabel.setText(String.valueOf(roundedStoreScore));
        scoreLabel.getStyle().set("margin-bottom", "0");

        // Define score label layout
        Component starIcon = LineAwesomeIcon.STAR.create();
        starIcon.getStyle().set("font-size", "10px");
        starIcon.getStyle().set("margin", "4px");
        starIcon.getStyle().set("margin-right", "0");
        scoreLabelLayout.add(scoreLabel, starIcon);
        scoreLabelLayout.setSpacing(false);
        scoreLabelLayout.setAlignItems(Alignment.CENTER);

        // Define score layout
        scoreLayout.add(scoreLabelLayout, scoreBar);
        scoreLayout.setSpacing(false);
        scoreLayout.setMaxWidth("80px");
        scoreLayout.setAlignItems(Alignment.CENTER);

        // Define header name and score layout
        H1 storeName = new H1(storeInfo.storeName());
        storeName.getStyle().set("margin-bottom", "0");
        hiddenStoreIcon.setVisible(isHidden);
        hideStoreButton.setVisible(!isHidden);
        unhideStoreButton.setVisible(isHidden);
        storeMessagesButton.getStyle().set("margin-left", "auto");
        header_name_score.add(hiddenStoreIcon, storeName, scoreLayout, storeMessagesButton);
        header_name_score.setSpacing(false);
        header_name_score.getStyle().set("margin-bottom", "0");
        header_name_score.setWidthFull();

        // Define category label
        Label categoryLabel = new Label("Category: " + storeInfo.category());
        categoryLabel.getStyle().set("margin-top", "0");

        // Define header layout
        headerLayout.add(header_name_score, categoryLabel);
        headerLayout.setSpacing(false);
        headerLayout.setPadding(false);

        add(headerLayout);
    }


    private void init_body(){
        storeProductsLayout = new StoreProductsLayout(userId, storeId, session, products, currentWorker);
        storeWorkersLayout = new StoreWorkersLayout(userId, storeId, session, workers, userIdToUsername, currentWorker);
        bodyLayout.add(storeProductsLayout, storeWorkersLayout);
        bodyLayout.setSizeFull();
        add(bodyLayout);
    }

    private void init_bottomButtons() {

        storeIncomeButton.addClickListener(e -> {
            if(currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.HISTORY)){
                navigate("storeIncome/" + storeId);
            }
            else{
                notifyWarning("You don't have permission to view store income");
            }
        });

        manageDiscountsButton.addClickListener(e -> {
            if(currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.POLICIES)){
                navigate("manageDiscounts/" + storeId);
            }
            else{
                notifyWarning("You don't have permission to manage store discounts");
            }
        });

        managePurchasePolicyButton.addClickListener(e -> {
            if(currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.POLICIES)){
                navigate("managePurchasePolicy/" + storeId);
            }
            else{
                notifyWarning("You don't have permission to manage store purchase policy");
            }
        });

        storePurchaseHistoryButton.addClickListener(e-> {
            if(currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.HISTORY)){
                navigate("purchaseHistory/" + storeId); //TODO: change, ask eden
            }
            else{
                notifyWarning("You don't have permission to view store purchase history");
            }
        });

        myVotesButton.addClickListener(e-> {
            if(currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.STAFF)){
                navigate("myvotes/" + storeId);
            }
            else{
                notifyWarning("You don't have permission to view owner votes");
            }
        });

        hideStoreButton.addClickListener(e -> {
            if(isHidden){
                notifyWarning("Store is already hidden");
                return;
            }
            if(handleResponse(session.hideStore(userId, storeId)) != null){
                isHidden = true;
                hiddenStoreIcon.setVisible(true);
                hideStoreButton.setVisible(false);
                unhideStoreButton.setVisible(true);
                notifySuccess("Store has been hidden successfully");
            }

        });

        unhideStoreButton.addClickListener(e -> {
            if(!isHidden){
                notifyWarning("Store is not hidden");
                return;
            }
            if(handleResponse(session.unhideStore(userId, storeId)) != null){
                isHidden = false;
                hiddenStoreIcon.setVisible(false);
                hideStoreButton.setVisible(true);
                unhideStoreButton.setVisible(false);
                notifySuccess("Store has been reopened successfully");
            }
        });

        deleteStoreButton.addClickListener(e -> {
            if(handleResponse(session.deleteStore(userId, storeId)) != null){
                notifySuccess("Store deleted successfully");
                UI.getCurrent().navigate(HomeView.class);
            }

        });
        deleteStoreButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        bottomButtonsLayout.add(storeIncomeButton, manageDiscountsButton, managePurchasePolicyButton, storePurchaseHistoryButton, hideStoreButton, unhideStoreButton, deleteStoreButton, myVotesButton);
        bottomButtonsLayout.getStyle().set("margin-top", "auto");
        add(bottomButtonsLayout);
    }

    private void getData(){
        //isAdmin
        isAdmin = handleResponse(session.isAdmin(userId), "");

        //isHidden
        isHidden = handleResponse(session.isStoreHidden(storeId), "");

        //products
        products = handleResponse(session.getAllStoreProductsInfo(userId, storeId), "");

        //workers
        workers = handleResponse(session.getStoreWorkersInfo(1 /*mafhhhiiddd*/, storeId), "");

        //userIdToUsername
        List<Integer> userIds = workers.stream().map(WorkerCard::userId).collect(Collectors.toList());
        userIdToUsername = handleResponse(session.getUserIdsToUsernamesMapper(userIds), "");
    }

    private double getRoundedScore(double score){
        return Math.round(score * 10.0) / 10.0;
    }


    private void init_dataFields(){
        userIdToUsername = new HashMap<>();

    }

    private void getCurrentWorkerCard(){
        currentWorker = workers.stream().filter(worker -> worker.userId() == userId).findFirst().orElse(new WorkerCard(userId, null, Set.of()));
    }


    private void demoData(){
        storeInfo = new StoreInfo(0, "SheepStore", "sheep", 3.9);

        userIdToUsername.put(0, "lior");
        userIdToUsername.put(1, "eden");
        userIdToUsername.put(2, "eyal");
        userIdToUsername.put(3, "shaun");
        userIdToUsername.put(4, "tomer");
        userIdToUsername.put(5, "yoav");

        WorkerCard worker0 = new WorkerCard(0, UserPermissions.StoreRole.FOUNDER, Set.of(UserPermissions.IndividualPermission.values()));
        WorkerCard worker1 = new WorkerCard(1, UserPermissions.StoreRole.OWNER, Set.of());
        WorkerCard worker2 = new WorkerCard(2, UserPermissions.StoreRole.OWNER, Set.of());
        WorkerCard worker3 = new WorkerCard(3, UserPermissions.StoreRole.MANAGER, Set.of());
        WorkerCard worker4 = new WorkerCard(4, UserPermissions.StoreRole.MANAGER, Set.of());
        WorkerCard worker5 = new WorkerCard(5, UserPermissions.StoreRole.MANAGER, Set.of());

        workers = new LinkedList<>();
        workers.add(worker0);
        workers.add(worker1);
        workers.add(worker2);
        workers.add(worker3);
        workers.add(worker4);
        workers.add(worker5);

        products = new LinkedHashSet<>();
        products.add(new ProductInfo(0, 0, storeInfo.storeName(), "milk", "dairy", 5.0, 10, "milk description", 4.2F));
        products.add(new ProductInfo(-1, 0, storeInfo.storeName(),"cheese", "dairy", 10.0, 20, "cheese description", 4.5F));
        products.add(new ProductInfo(-2, 0, storeInfo.storeName(),"bread", "bakery", 3.0, 30, "bread description", 4.0F));
        products.add(new ProductInfo(-3, 0, storeInfo.storeName(),"butter", "dairy", 7.0, 40, "butter description", 4.1F));
        products.add(new ProductInfo(-4, 0, storeInfo.storeName(),"eggs", "dairy", 8.0, 50, "eggs description", 4.3F));
        products.add(new ProductInfo(-5, 0, storeInfo.storeName(),"yogurt", "dairy", 6.0, 60, "yogurt description", 4.4F));
        products.add(new ProductInfo(-6, 0, storeInfo.storeName(),"cake", "bakery", 12.0, 70, "cake description", 4.6F));
        products.add(new ProductInfo(-7, 0, storeInfo.storeName(),"cookies", "bakery", 9.0, 80, "cookies description", 4.7F));
        products.add(new ProductInfo(-8, 0, storeInfo.storeName(),"chocolate", "bakery", 11.0, 90, "chocolate description", 4.8F));
        products.add(new ProductInfo(-9, 0, storeInfo.storeName(),"pizza", "bakery", 15.0, 100, "pizza description", 4.9F));
        products.add(new ProductInfo(-10, 0, storeInfo.storeName(),"water", "drinks", 2.0, 110, "water description", 3.3F));
        products.add(new ProductInfo(-11, 0, storeInfo.storeName(),"soda", "drinks", 3.0, 120, "soda description", 2.5F));
        products.add(new ProductInfo(-12, 0, storeInfo.storeName(),"juice", "drinks", 4.0, 130, "juice description", 1.7F));
        products.add(new ProductInfo(-13, 0, storeInfo.storeName(),"beer", "drinks", 5.0, 140, "beer description", 3.8F));
        products.add(new ProductInfo(-14, 0, storeInfo.storeName(),"wine", "drinks", 6.0, 150, "wine description", 2.1F));
    }

    //TODO:lior - all the functions below are for store - please provide the store id and call to functions that checks if the store owner
    //TODO: lior- please add button to your store only if the current user is logged in and the button should call to the mainStoreDialog

    private Button messageStoreDialog() {
        Button messageButton = new Button("Message Store Center");
        messageButton.setIcon(VaadinIcon.COMMENTS_O.create());
        Dialog myDialog = new Dialog();

        mainStoreDialog(myDialog);

        messageButton.addClickListener(event -> myDialog.open());
        return messageButton;
    }

    private void mainStoreDialog(Dialog currentDialog) {

        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();

        currentDialog.setHeaderTitle("My Stores Messages");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        VerticalLayout buttonsLayout = new VerticalLayout();
        currentDialog.add(buttonsLayout);
        Button sendMessageToStore = new Button("Send message to Store");
        sendMessageToStore.addClickListener(event -> {
            sendMessageToStoreDialog(currentDialog);
        });

        Button newMessagesStore = new Button("New messages");
        newMessagesStore.addClickListener(event -> {
            newMessagesStoreDialog(currentDialog);
        });

        Button oldMessagesStore = new Button("Old messages");
        oldMessagesStore.addClickListener(event -> {
            oldMessagesStoreDialog(currentDialog);

        });

        if(currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.MESSAGES) /*if storeOwner - getUnreadMessages*/)
        {
            buttonsLayout.add(newMessagesStore, oldMessagesStore);
        }
        if(session.isUserLogged(userId)/*logged in user*/)
        {
            buttonsLayout.add(sendMessageToStore);
        }

    }

    private void oldMessagesStoreDialog(Dialog currentDialog) {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();
        session.clearMessageToReply(userId);
        currentDialog.setHeaderTitle("Old Store Messages");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button jmpMainDialog = new Button("Messages Store Center");
        Button nextMessageButton = new Button("next message");
        VerticalLayout verticalDialogMessage = new VerticalLayout();
        TextArea messageBody = new TextArea();
        Button refreshMessagesButton = new Button("Refresh Messages");
        Response<Message> messageResponse = session.readReadMassageStore(userId,storeId);
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


        jmpMainDialog.addClickListener(event -> mainStoreDialog(currentDialog));
        nextMessageButton.addClickListener(event -> oldMessagesStoreDialog(currentDialog));
        refreshMessagesButton.addClickListener(event -> {
            session.refreshOldMessageStore(userId, storeId);
            oldMessagesStoreDialog(currentDialog);
        });

    }

    private void newMessagesStoreDialog(Dialog currentDialog) {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();
        session.clearMessageToReply(userId);

        currentDialog.setHeaderTitle("Complaints");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button jmpMainDialog = new Button("Messages Store Center");
        Button nextMessageButton = new Button("Next Message");
        VerticalLayout verticalDialogMessage = new VerticalLayout();
        TextArea messageBody = new TextArea();
        Button replyButton = new Button("Reply");
        TextArea inputBody = new TextArea();
        Button sendMessageButton = new Button("Send Answer");
        Response<Message> messageResponse = session.readUnreadMassageStore(userId, storeId);
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


        jmpMainDialog.addClickListener(event -> mainStoreDialog(currentDialog));
        replyButton.addClickListener(event -> {
            verticalDialogMessage.remove(replyButton);
            verticalDialogMessage.add(inputBody, sendMessageButton);
        });

        nextMessageButton.addClickListener(event -> newMessagesStoreDialog(currentDialog));
        sendMessageButton.addClickListener(event -> {
            Response<VoidResponse> response= session.answerQuestionStore(userId,inputBody.getValue());
            newMessagesStoreDialog(currentDialog);
            Notification notification=new Notification("Message sent", 3000);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if(response.getStatus()== Response.Status.FAILURE) {
                notification.setText(response.getMessage());
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            notification.open();

        });

    }

    private void sendMessageToStoreDialog(Dialog currentDialog) {
        currentDialog.removeAll();
        currentDialog.getFooter().removeAll();
        currentDialog.getHeader().removeAll();
        session.clearMessageToReply(userId);
        currentDialog.setHeaderTitle("Send Message");

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> currentDialog.close());
        currentDialog.getHeader().add(closeButton);

        Button jmpMainDialog = new Button("Messages Center");
        VerticalLayout verticalDialogMessage = new VerticalLayout();
        TextField inputHeader = new TextField();
        TextArea inputBody = new TextArea();
        Button sendMessageButton = new Button("Send Message");


        currentDialog.getFooter().add(jmpMainDialog);
        currentDialog.add(verticalDialogMessage);

        verticalDialogMessage.add(inputHeader);
        verticalDialogMessage.add(inputBody);
        verticalDialogMessage.add(sendMessageButton);


        inputHeader.setWidthFull();
        inputHeader.setLabel("Subject:");
        inputBody.setWidthFull();
        inputBody.setMinWidth("300px");
        inputBody.setLabel("Body:");


        jmpMainDialog.addClickListener(event -> mainStoreDialog(currentDialog));
        sendMessageButton.addClickListener(event -> {
            Response<VoidResponse> messageResponse= session.sendMassageStore(userId,inputHeader.getValue(),inputBody.getValue(),storeId);
            sendMessageToStoreDialog(currentDialog);
            Notification notification=new Notification("Message sent", 3000);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if(messageResponse.getStatus()== Response.Status.FAILURE){
                notification.setText(messageResponse.getMessage());
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            notification.open();

        });

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer storeIdParam) {
        if(storeIdParam == null){
            beforeEvent.rerouteTo("");
            return;
        }

        this.storeId = storeIdParam;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        storeInfo = handleResponse(session.getStoreInfo(userId, storeId));
        if(storeInfo == null){
            terminate = true;
            beforeEnterEvent.rerouteTo(HomeView.class);
            return;
        }
        start();
    }
}

