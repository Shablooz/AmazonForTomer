package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.VoidResponse;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarGroup.AvatarGroupItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
import java.util.Locale;
import java.util.stream.Collectors;


@PageTitle("Store")
@Route(value = "store", layout = MainLayout.class)
public class StoreView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final Session session;
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int storeId = -1;
    private StoreInfo storeInfo;
    private List<WorkerCard> workers;
    private HashMap<UserPermissions.StoreRole, List<WorkerCard>> rolesToWorkers;
    private HashMap<Integer, String> userIdToUsername;
    private List<ProductInfo> products;
    private HashMap<String, List<ProductInfo>> categoriesToProducts;
    private String[] rolesToAdd = {"Owner", "Manager"};

    //components
    private VerticalLayout headerLayout;
    private HorizontalLayout header_name_score;
    private HorizontalLayout scoreLabelLayout;
    private VerticalLayout scoreLayout;
    private ProgressBar scoreBar;
    private Div scoreLabel;
    private HorizontalLayout bodyLayout;
    private Accordion categoriesAccordion;
    private HashMap<String, Grid<ProductInfo>> categoriesToGrids;
    private Button addProductButton;
    private VerticalLayout workersLayout;
    private HorizontalLayout workersHeaderLayout;
    private VerticalLayout productsLayout;
    private HorizontalLayout productsHeaderLayout;
    private Accordion rolesAccordion;
    private HashMap<String, AvatarGroup> rolesToAvatarGroup;
    private Button addWorkerButton;
    private Button hideStoreButton;
    private Button deleteStoreButton;
    private HorizontalLayout bottomButtonsLayout;
    private Button storeMessagesButton;



    @Autowired
    public StoreView(Session session) {
        super();
        this.session = session;
    }

    private void start(){
        this.removeAll();
        init_dataFields();
        demoData();
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
        categoriesAccordion = new Accordion();
        categoriesToGrids = new HashMap<>();
        workersLayout = new VerticalLayout();
        workersHeaderLayout = new HorizontalLayout();
        productsLayout = new VerticalLayout();
        productsHeaderLayout = new HorizontalLayout();
        rolesAccordion = new Accordion();
        rolesToAvatarGroup = new HashMap<>();
        bottomButtonsLayout = new HorizontalLayout();

        addProductButton = new Button(new Icon(VaadinIcon.PLUS));
        addWorkerButton = new Button(new Icon(VaadinIcon.PLUS));
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

        // Define header name and score layout
        H1 storeName = new H1(storeInfo.storeName());
        storeName.getStyle().set("margin-bottom", "0");
        header_name_score.add(storeName, scoreLayout);
        header_name_score.setSpacing(false);
        header_name_score.getStyle().set("margin-bottom", "0");

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
        init_products_section();
        init_workers_section();
        bodyLayout.add(productsLayout, workersLayout);
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

    private void init_products_section(){
        for(String category : categoriesToProducts.keySet()){
            Grid<ProductInfo> grid = new Grid<>();
            grid.setItems(categoriesToProducts.get(category));
            grid.addColumn(ProductInfo::name).setHeader("Name");
            grid.addColumn(ProductInfo::price).setHeader("Price");
            grid.addColumn(ProductInfo::stockQuantity).setHeader("Stock");
            grid.addColumn(p -> getRoundedScore(p.score())).setHeader("Rating");
            categoriesToGrids.put(category, grid);

            //styling
            grid.getStyle().set("margin", "10px");
            grid.setAllRowsVisible(true);
            grid.setMaxWidth("960px");
            grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        }

        for(String category : categoriesToGrids.keySet()){
            categoriesAccordion.add(category, categoriesToGrids.get(category));
        }

        addProductButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        addProductButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        productsHeaderLayout.add(new H2("Products"), addProductButton);
        productsHeaderLayout.setAlignItems(Alignment.CENTER);
        productsLayout.add(productsHeaderLayout, categoriesAccordion);

        //button action
        addProductButton.addClickListener(e -> addProductDialog());
    }

    private void init_workers_section(){
        for(UserPermissions.StoreRole role : rolesToWorkers.keySet()){
            AvatarGroup avatarGroup = new AvatarGroup();
            avatarGroup.setItems(rolesToWorkers.get(role).stream().map(w -> new AvatarGroupItem(getUsername(w))).collect(Collectors.toList()));
            rolesToAvatarGroup.put(roleToStringTitle(role), avatarGroup);

            //styling
            avatarGroup.setMaxItemsVisible(5);
        }

        //ensure founder is first
        if(rolesToAvatarGroup.containsKey("Founder")){
            rolesAccordion.add("Founder", rolesToAvatarGroup.get("Founder"));
        }

        for(String role : rolesToAvatarGroup.keySet()){
            if(!role.equals("Founder")){
                rolesAccordion.add(rolesToAvatarGroup.get(role).getItems().size() > 1 ? role + "s" : role,
                        rolesToAvatarGroup.get(role));
            }
        }

        addWorkerButton.addClickListener(e -> addWorkerDialog());

        addWorkerButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        addWorkerButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        workersHeaderLayout.add(new H2("Workers"), addWorkerButton);
        workersHeaderLayout.setAlignItems(Alignment.CENTER);
        workersLayout.add(workersHeaderLayout, rolesAccordion);

        //uncomment to align workers to the right
        workersLayout.getStyle().set("margin-left", "auto");
        workersLayout.setMaxWidth("200px");


    }

    private void addProductDialog(){
        Dialog dialog = new Dialog();
        dialog.setWidth("350px");
        dialog.setHeight("550px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        H2 dialogTitle = new H2("Add Product");
        TextField productName = new TextField("Name");
        TextField productCategory = new TextField("Category");
        NumberField productPrice = new NumberField("Price");
        IntegerField productStock = new IntegerField("Stock");
        TextArea productDescription = new TextArea("Description");
        productDescription.getStyle().set("max-height", "150px");
        productDescription.getStyle().set("min-height", "150px");

        productPrice.setValue(0.0);
        productStock.setValue(0);
        productPrice.setStepButtonsVisible(true);
        productStock.setStepButtonsVisible(true);


        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(e2 -> {
            handleResponseAndRefresh(session.addProduct(userId, storeId, productName.getValue(), productCategory.getValue(),
                    productPrice.getValue(), productStock.getValue(), productDescription.getValue()), "store/" + storeId);
            dialog.close();
        });


        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, productName, productPrice, productStock, productDescription, confirmButton);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(Alignment.STRETCH);

        dialog.add(dialogLayout);
        dialog.open();

    }

    private void addWorkerDialog(){
        Dialog dialog = new Dialog();
        dialog.setWidth("350px");
        dialog.setHeight("300px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        H2 dialogTitle = new H2("Add Worker");
        TextField workerName = new TextField("Username");
        ComboBox<String> workerRole = new ComboBox<>("Role");
        workerRole.setItems(rolesToAdd);

        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(e2 -> {
            Notification.show("not implemented yet in GUI :(");
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, workerName, workerRole, confirmButton);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(Alignment.STRETCH);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void getData(){

    }

    private void init_categoriesToProducts(){
        categoriesToProducts = new HashMap<>();
        for(ProductInfo product : products){
            if(!categoriesToProducts.containsKey(product.category())){
                categoriesToProducts.put(product.category(), new LinkedList<>());
            }
            categoriesToProducts.get(product.category()).add(product);
        }
    }

    private void init_rolesToWorkers(){
        rolesToWorkers = new HashMap<>();
        for(WorkerCard worker : workers){
            if(!rolesToWorkers.containsKey(worker.storeRole())){
                rolesToWorkers.put(worker.storeRole(), new LinkedList<>());
            }
            rolesToWorkers.get(worker.storeRole()).add(worker);
        }
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

        if(true /*if storeOwner - getUnreadMessages*/)
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
        Response<Message> messageResponse = session.readReadMassageStore(userId, storeId);
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
            Response<VoidResponse> messageResponse= session.sendMassageStore(userId,inputHeader.getValue(),inputBody.getValue(),userId);
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

    private String getUsername(WorkerCard worker){
        return userIdToUsername.get(worker.userId());
    }

    private String roleToStringTitle(UserPermissions.StoreRole role){
        String roleString = role.toString();
        String[] words = roleString.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private void init_dataFields(){
        rolesToWorkers = new HashMap<>();
        userIdToUsername = new HashMap<>();
        categoriesToProducts = new HashMap<>();
    }

    private void demoData(){
        storeInfo = new StoreInfo(0, "SheepStore", "sheep", 3.9);

        userIdToUsername.put(0, "lior");
        userIdToUsername.put(-1, "eden");
        userIdToUsername.put(-2, "eyal");
        userIdToUsername.put(-3, "shaun");
        userIdToUsername.put(-4, "tomer");
        userIdToUsername.put(-5, "yoav");

        WorkerCard worker0 = new WorkerCard(0, UserPermissions.StoreRole.FOUNDER, new LinkedList<>());
        WorkerCard worker1 = new WorkerCard(-1, UserPermissions.StoreRole.OWNER, new LinkedList<>());
        WorkerCard worker2 = new WorkerCard(-2, UserPermissions.StoreRole.OWNER, new LinkedList<>());
        WorkerCard worker3 = new WorkerCard(-3, UserPermissions.StoreRole.MANAGER, new LinkedList<>());
        WorkerCard worker4 = new WorkerCard(-4, UserPermissions.StoreRole.MANAGER, new LinkedList<>());
        WorkerCard worker5 = new WorkerCard(-5, UserPermissions.StoreRole.MANAGER, new LinkedList<>());

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

        init_categoriesToProducts();
        init_rolesToWorkers();
    }


    private <T> T handleResponse(Response<T> response, String navInCaseOfFailure) {
        if (response.didntSucceed()) {
            Notification errorNotification = Notification.show("Error: " + response.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate(navInCaseOfFailure);
        }
        return response.getData();
    }

    private <T> T handleResponse(Response<T> response) {
        return handleResponse(response, "");
    }

    private <T> T handleResponseAndRefresh(Response<T> response, String navInCaseOfFailure) {
        if (response.didntSucceed()) {
            Notification errorNotification = Notification.show("Error: " + response.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate(navInCaseOfFailure);
            start();
        }
        return response.getData();
    }

    private <T> T handleResponseAndRefresh(Response<T> response) {
        return handleResponseAndRefresh(response, "");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer storeIdParam) {
        this.storeId = storeIdParam;
        start();
    }
}
