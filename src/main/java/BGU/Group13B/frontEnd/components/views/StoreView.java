package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
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
    private final VerticalLayout ProductsLayout = new VerticalLayout();
    private final Accordion categoriesAccordion = new Accordion();
    private final HashMap<String, Grid<ProductInfo>> categoriesToGrids = new HashMap<>();
    private final Button addProductButton = new Button("Add Product");
    private final VerticalLayout workersLayout = new VerticalLayout();
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


    }

    private void init_header(){
        // Define score bar
        scoreBar.setMax(5);
        scoreBar.setMin(0);
        scoreBar.setValue(getRoundedStoreScore());
        scoreBar.setClassName(String.valueOf(getRoundedStoreScore()));
        scoreBar.getStyle().set("margin-top", "0");

        // Define score label
        scoreLabel.setText(String.valueOf(getRoundedStoreScore()));
        scoreLabel.getStyle().set("margin-bottom", "0");

        // Define score label layout
        Icon starIcon = new Icon("vaadin", "star-o");
        starIcon.getStyle().set("font-size", "9px");
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
        categoryLabel.getStyle().set("margin", "0");

        // Define header layout
        headerLayout.add(header_name_score, categoryLabel);
        headerLayout.setSpacing(false);
        headerLayout.setPadding(false);

        add(headerLayout);
    }


    private void getData(){

    }

    private void init_categoriesToProducts(){
        for(ProductInfo product : products){
            if(!categoriesToProducts.containsKey(product.category())){
                categoriesToProducts.put(product.category(), new LinkedList<>());
            }
            categoriesToProducts.get(product.category()).add(product);
        }
    }

    private void init_rolesToWorkers(){
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

    private double getRoundedStoreScore(){
        return Math.round(storeInfo.storeScore() * 10.0) / 10.0;
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

        ProductInfo product0 = new ProductInfo(0, 0, "milk", "dairy", 5.0, 10, "milk description", 4.2F);
        ProductInfo product1 = new ProductInfo(-1, 0, "cheese", "dairy", 10.0, 20, "cheese description", 4.5F);
        ProductInfo product2 = new ProductInfo(-2, 0, "bread", "bakery", 3.0, 30, "bread description", 4.0F);
        ProductInfo product3 = new ProductInfo(-3, 0, "butter", "dairy", 7.0, 40, "butter description", 4.1F);

        products = new LinkedList<>();
        products.add(product0);
        products.add(product1);
        products.add(product2);
        products.add(product3);

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
