package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


@PageTitle("Store")
@Route(value = "store", layout = MainLayout.class)
public class StoreView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int storeId = -1;
    private StoreInfo storeInfo;
    private List<WorkerCard> workers;
    private HashMap<UserPermissions.StoreRole, List<WorkerCard>> rolesToWorkers = new HashMap<>();
    private HashMap<Integer, String> userIdToUsername = new HashMap<>();
    private List<ProductInfo> products;
    private HashMap<String, List<ProductInfo>> categoriesToProducts = new HashMap<>();

    //components
    private final VerticalLayout headerLayout = new VerticalLayout();
    private final HorizontalLayout header_name_score= new HorizontalLayout();
    private final HorizontalLayout scoreLabelLayout = new HorizontalLayout();
    private final VerticalLayout scoreLayout = new VerticalLayout();
    private final ProgressBar scoreBar = new ProgressBar();
    private final Div scoreLabel = new Div();
    private final HorizontalLayout bodyLayout = new HorizontalLayout();
    private final Accordion categoriesAccordion = new Accordion();
    private final HashMap<String, Grid<ProductInfo>> categoriesToGrids = new HashMap<>();
    private final Button addProductButton = new Button(new Icon(VaadinIcon.PLUS));
    private final VerticalLayout workersLayout = new VerticalLayout();
    private final VerticalLayout productsLayout = new VerticalLayout();
    private final HorizontalLayout productsHeaderLayout = new HorizontalLayout();
    private final Accordion rolesAccordion = new Accordion();
    private final HashMap<String, Grid<WorkerCard>> rolesToGrids = new HashMap<>();
    private final Button addWorkerButton = new Button("Add Worker");
    private final Button hideStoreButton = new Button("Hide Store");
    private final Button editStoreButton = new Button("Delete Store");
    private final HorizontalLayout bottomButtonsLayout = new HorizontalLayout();


    @Autowired
    public StoreView(Session session) {
        super();

        demoData();
        init_header();
        init_body();


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
        add(bodyLayout);
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
    }

    private void init_workers_section(){

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

    private void init_userIdToUsername(){
        //TODO: get from backend
    }

    private double getRoundedScore(double score){
        return Math.round(score * 10.0) / 10.0;
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
        products.add(new ProductInfo(0, 0, "milk", "dairy", 5.0, 10, "milk description", 4.2F));
        products.add(new ProductInfo(-1, 0, "cheese", "dairy", 10.0, 20, "cheese description", 4.5F));
        products.add(new ProductInfo(-2, 0, "bread", "bakery", 3.0, 30, "bread description", 4.0F));
        products.add(new ProductInfo(-3, 0, "butter", "dairy", 7.0, 40, "butter description", 4.1F));
        products.add(new ProductInfo(-4, 0, "eggs", "dairy", 8.0, 50, "eggs description", 4.3F));
        products.add(new ProductInfo(-5, 0, "yogurt", "dairy", 6.0, 60, "yogurt description", 4.4F));
        products.add(new ProductInfo(-6, 0, "cake", "bakery", 12.0, 70, "cake description", 4.6F));
        products.add(new ProductInfo(-7, 0, "cookies", "bakery", 9.0, 80, "cookies description", 4.7F));
        products.add(new ProductInfo(-8, 0, "chocolate", "bakery", 11.0, 90, "chocolate description", 4.8F));
        products.add(new ProductInfo(-9, 0, "pizza", "bakery", 15.0, 100, "pizza description", 4.9F));
        products.add(new ProductInfo(-10, 0, "water", "drinks", 2.0, 110, "water description", 3.3F));
        products.add(new ProductInfo(-11, 0, "soda", "drinks", 3.0, 120, "soda description", 2.5F));
        products.add(new ProductInfo(-12, 0, "juice", "drinks", 4.0, 130, "juice description", 1.7F));
        products.add(new ProductInfo(-13, 0, "beer", "drinks", 5.0, 140, "beer description", 3.8F));
        products.add(new ProductInfo(-14, 0, "wine", "drinks", 6.0, 150, "wine description", 2.1F));

        init_categoriesToProducts();
        init_rolesToWorkers();
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer storeIdParam) {
        this.storeId = storeIdParam;
    }

    private <T> T handleResponse(Response<T> response) {
        if (response.didntSucceed()) {
            Notification errorNotification = Notification.show("Error: " + response.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate("home");
        }
        return response.getData();
    }
}
