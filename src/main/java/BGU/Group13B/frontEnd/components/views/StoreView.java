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
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@PageTitle("Store")
@Route(value = "store", layout = MainLayout.class)
public class StoreView extends VerticalLayout implements HasUrlParameter<Integer>, ResponseHandler {

    private final Session session;
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private WorkerCard currentWorker;
    private int storeId = -1;
    private StoreInfo storeInfo;
    private List<WorkerCard> workers;
    private HashMap<Integer, String> userIdToUsername;
    private List<ProductInfo> products;
    private List<String> allPermissions;
    private List<String> founderPermissions;
    private List<String> ownerPermissions;
    private List<String> managerPermissions;

    //components
    private VerticalLayout headerLayout;
    private HorizontalLayout header_name_score;
    private HorizontalLayout scoreLabelLayout;
    private VerticalLayout scoreLayout;
    private ProgressBar scoreBar;
    private Div scoreLabel;
    private HorizontalLayout bodyLayout;
    private Button hideStoreButton;
    private Button deleteStoreButton;
    private HorizontalLayout bottomButtonsLayout;
    private Button storeMessagesButton;
    private StoreProductsLayout storeProductsLayout;
    private StoreWorkersLayout storeWorkersLayout;




    @Autowired
    public StoreView(Session session) {
        super();
        this.session = session;
    }

    private void start(){
        this.removeAll();
        init_dataFields();
        demoData();
        getCurrentWorkerCard();
        init_components();

        init_header();
        init_body();
        init_bottomButtons();
        setSizeFull();
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
        deleteStoreButton = new Button("Delete Store");
        storeMessagesButton = messageStoreDialog();
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
        storeMessagesButton.getStyle().set("margin-left", "auto");
        header_name_score.add(storeName, scoreLayout, storeMessagesButton);
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
        storeProductsLayout = new StoreProductsLayout(userId, storeId, session, products);
        storeWorkersLayout = new StoreWorkersLayout(userId, storeId, session, workers, userIdToUsername);
        bodyLayout.add(storeProductsLayout, storeWorkersLayout);
        bodyLayout.setSizeFull();
        add(bodyLayout);
    }

    private void init_bottomButtons() {
        hideStoreButton.addClickListener(e -> {
            handleResponse(session.hideStore(userId, storeId));
        });
        deleteStoreButton.addClickListener(e -> {
            Notification.show("not implemented yet in GUI :(");
        });
        deleteStoreButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        bottomButtonsLayout.add(hideStoreButton, deleteStoreButton);
        bottomButtonsLayout.getStyle().set("margin-top", "auto");
        add(bottomButtonsLayout);
    }

    private void getData(){

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

        if(currentWorker.userPermissions().contains("getUnreadMessages") /*if storeOwner - getUnreadMessages*/)
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

    private void init_userIdToUsername(){
        //TODO: get from backend
    }

    private double getRoundedScore(double score){
        return Math.round(score * 10.0) / 10.0;
    }


    private void init_dataFields(){
        userIdToUsername = new HashMap<>();

        //all permissions: [removeStoreDiscount, setStorePurchasePriceLowerBound, removeManager, removeProduct, setProductPurchasePriceLowerBound, addProduct, hideStore, setProductStockQuantity, setProductName, createAuctionForProduct, setStorePurchaseQuantityLowerBound, addManager, unhideStore, addStoreVisibleDiscount, getAuctionInfo, disallowPurchasePolicyConflicts, setProductPurchaseQuantityLowerBound, addStoreConditionalDiscount, setStorePurchasePriceBounds, setProductDescription, addProductVisibleDiscount, purchaseProposalSubmit, allowPurchasePolicyConflicts, setProductPurchasePriceUpperBound, purchaseProposalReject, removeProductDiscount, markAsCompleted, refreshMessages, auctionPurchase, getReadMessages, setProductPurchaseQuantityUpperBound, addOwner, setProductPurchaseQuantityBounds, getUnreadMessages, endAuctionForProduct, setProductPurchasePriceBounds, setStorePurchaseQuantityBounds, addStoreHiddenDiscount, addProductConditionalDiscount, removeOwner, setProductCategory, setStorePurchasePriceUpperBound, setStorePurchaseQuantityUpperBound, purchaseProposalApprove, addProductHiddenDiscount, getStoreWorkersInfo, setProductPrice, deleteStore]
        allPermissions = List.of("removeStoreDiscount", "setStorePurchasePriceLowerBound", "removeManager", "removeProduct",
                "setProductPurchasePriceLowerBound", "addProduct", "hideStore", "setProductStockQuantity",
                "setProductName", "createAuctionForProduct", "setStorePurchaseQuantityLowerBound", "addManager",
                "unhideStore", "addStoreVisibleDiscount", "getAuctionInfo", "disallowPurchasePolicyConflicts",
                "setProductPurchaseQuantityLowerBound", "addStoreConditionalDiscount", "setStorePurchasePriceBounds",
                "setProductDescription", "addProductVisibleDiscount", "purchaseProposalSubmit", "allowPurchasePolicyConflicts",
                "setProductPurchasePriceUpperBound", "purchaseProposalReject", "removeProductDiscount", "markAsCompleted",
                "refreshMessages", "auctionPurchase", "getReadMessages", "setProductPurchaseQuantityUpperBound", "addOwner",
                "setProductPurchaseQuantityBounds", "getUnreadMessages", "endAuctionForProduct", "setProductPurchasePriceBounds",
                "setStorePurchaseQuantityBounds", "addStoreHiddenDiscount", "addProductConditionalDiscount", "removeOwner",
                "setProductCategory", "setStorePurchasePriceUpperBound", "setStorePurchaseQuantityUpperBound",
                "purchaseProposalApprove", "addProductHiddenDiscount", "getStoreWorkersInfo", "setProductPrice", "deleteStore");

        //founder permissions: [removeStoreDiscount, setStorePurchasePriceLowerBound, removeManager, removeProduct, setProductPurchasePriceLowerBound, addProduct, hideStore, setProductStockQuantity, setProductName, createAuctionForProduct, setStorePurchaseQuantityLowerBound, addManager, unhideStore, addStoreVisibleDiscount, getAuctionInfo, disallowPurchasePolicyConflicts, setProductPurchaseQuantityLowerBound, addStoreConditionalDiscount, setStorePurchasePriceBounds, setProductDescription, addProductVisibleDiscount, purchaseProposalSubmit, allowPurchasePolicyConflicts, setProductPurchasePriceUpperBound, purchaseProposalReject, removeProductDiscount, markAsCompleted, refreshMessages, auctionPurchase, getReadMessages, setProductPurchaseQuantityUpperBound, addOwner, setProductPurchaseQuantityBounds, getUnreadMessages, endAuctionForProduct, setProductPurchasePriceBounds, setStorePurchaseQuantityBounds, addStoreHiddenDiscount, addProductConditionalDiscount, removeOwner, setProductCategory, setStorePurchasePriceUpperBound, setStorePurchaseQuantityUpperBound, purchaseProposalApprove, addProductHiddenDiscount, getStoreWorkersInfo, setProductPrice]
        founderPermissions = List.of(
                "removeStoreDiscount", "setStorePurchasePriceLowerBound", "removeManager", "removeProduct",
                "setProductPurchasePriceLowerBound", "addProduct", "hideStore", "setProductStockQuantity",
                "setProductName", "createAuctionForProduct", "setStorePurchaseQuantityLowerBound", "addManager",
                "unhideStore", "addStoreVisibleDiscount", "getAuctionInfo", "disallowPurchasePolicyConflicts",
                "setProductPurchaseQuantityLowerBound", "addStoreConditionalDiscount", "setStorePurchasePriceBounds",
                "setProductDescription", "addProductVisibleDiscount", "purchaseProposalSubmit", "allowPurchasePolicyConflicts",
                "setProductPurchasePriceUpperBound", "purchaseProposalReject", "removeProductDiscount", "markAsCompleted",
                "refreshMessages", "auctionPurchase", "getReadMessages", "setProductPurchaseQuantityUpperBound", "addOwner",
                "setProductPurchaseQuantityBounds", "getUnreadMessages", "endAuctionForProduct", "setProductPurchasePriceBounds",
                "setStorePurchaseQuantityBounds", "addStoreHiddenDiscount", "addProductConditionalDiscount", "removeOwner",
                "setProductCategory", "setStorePurchasePriceUpperBound", "setStorePurchaseQuantityUpperBound",
                "purchaseProposalApprove", "addProductHiddenDiscount", "getStoreWorkersInfo", "setProductPrice"
        );

        //owner permissions: [removeManager, removeProduct, addOwner, addProduct, getUnreadMessages, setProductStockQuantity, setProductName, createAuctionForProduct, endAuctionForProduct, addManager, getAuctionInfo, removeOwner, setProductCategory, setProductDescription, purchaseProposalSubmit, purchaseProposalApprove, purchaseProposalReject, getStoreWorkersInfo, markAsCompleted, setProductPrice, refreshMessages, auctionPurchase, getReadMessages]
        ownerPermissions = List.of(
                "removeManager", "removeProduct", "addOwner", "addProduct", "getUnreadMessages", "setProductStockQuantity",
                "setProductName", "createAuctionForProduct", "endAuctionForProduct", "addManager", "getAuctionInfo",
                "removeOwner", "setProductCategory", "setProductDescription", "purchaseProposalSubmit", "purchaseProposalApprove",
                "purchaseProposalReject", "getStoreWorkersInfo", "markAsCompleted", "setProductPrice", "refreshMessages",
                "auctionPurchase", "getReadMessages"
        );

        //manager permissions: [addProduct, purchaseProposalSubmit, createAuctionForProduct, endAuctionForProduct, purchaseProposalApprove, purchaseProposalReject, getAuctionInfo, auctionPurchase]
        managerPermissions = List.of(
                "addProduct", "purchaseProposalSubmit", "createAuctionForProduct", "endAuctionForProduct",
                "purchaseProposalApprove", "purchaseProposalReject", "getAuctionInfo", "auctionPurchase"
        );
    }

    private void getCurrentWorkerCard(){
        currentWorker = workers.stream().filter(worker -> worker.userId() == userId).findFirst().orElse(null);
        if(currentWorker == null){
            Notification erorrNotification = new Notification("Error: could not find worker with id " + userId, 3000);
            erorrNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            erorrNotification.open();
            navigate("");
        }
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

        products = new LinkedList<>();
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

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer storeIdParam) {
        this.storeId = storeIdParam;
        start();
    }
}

