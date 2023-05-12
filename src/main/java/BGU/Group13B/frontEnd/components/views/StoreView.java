package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

@PageTitle("My Stores")
@Route(value = "store", layout = MainLayout.class)
public class StoreView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int storeId = -1;
    private final StoreInfo storeInfo;
    private final List<WorkerCard> workers;
    private final List<ProductInfo> products;
    private final HashMap<String, List<ProductInfo>> categoriesToProducts = new HashMap<>();

    //components
    private final VerticalLayout headerLayout = new VerticalLayout();
    private final HorizontalLayout header_name_score_buttons = new HorizontalLayout();
    private final HorizontalLayout headerButtonsLayout = new HorizontalLayout();
    private final Button hideStoreButton = new Button("Hide Store");
    private final Button editStoreButton = new Button("Delete Store");
    private final HorizontalLayout bodyLayout = new HorizontalLayout();
    private final VerticalLayout ProductsLayout = new VerticalLayout();
    private final Accordion categoriesAccordion = new Accordion();
    private final HashMap<String, Grid<ProductInfo>> categoriesToGrids = new HashMap<>();
    private final Button addProductButton = new Button("Add Product");
    private final VerticalLayout workersLayout = new VerticalLayout();
    private final Accordion rolesAccordion = new Accordion();
    private final HashMap<String, Grid<WorkerCard>> rolesToGrids = new HashMap<>();
    private final Button addWorkerButton = new Button("Add Worker");


    @Autowired
    public StoreView(Session session) {
        super();
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer storeIdParam) {
        this.storeId = storeIdParam;
    }
}
