package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.DataProvider.ReviewDataProvider;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.entity.ReviewService;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@PageTitle("product")
@Route(value = "product", layout = MainLayout.class)
public class ProductView extends VerticalLayout implements HasUrlParameter<String> {
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int productId;
    private int storeId;
    private Session session;
    private TextField seller;
    private TextField name;
    private TextField category;
    private NumberField price;
    private NumberField stockQuantity;
    private TextField description;
    private NumberField score;

    private String USER_NAME_COL = "User Name";
    private String REVIEW_COL = "Review";


    @Autowired
    public ProductView(Session session) {
        this.session=session;
        price = new NumberField();
        stockQuantity = new NumberField();
        score = new NumberField();
    }

    @Override
    public void setParameter(BeforeEvent event, String parameters) {
        String[] params = parameters.split("/");
        productId = Integer.parseInt(params[0]);
        storeId = Integer.parseInt(params[1]);
        start();
    }
    private void start(){
        ProductInfo info = session.getStoreProductInfo(userId,storeId,productId).getData(); //TODO: CHECK ON ERRORS
        seller = new TextField(info.seller());
        name = new TextField(info.name());
        category = new TextField(info.category());
        price.setValue(info.price());
        stockQuantity.setValue((double) info.stockQuantity());
        description = new TextField(info.description());
        score.setValue((double) info.score());
    }

    private Crud<ReviewService> createCrudeReview(){
        Crud<ReviewService> crud = new Crud<>(ReviewService.class, createEditor());

        setupGrid(crud);
        setupDataProvider(crud);

        return crud;
    }
    private CrudEditor<ReviewService> createEditor() {
        TextField userName = new TextField("User Name");
        TextField review = new TextField("Review");
        FormLayout form = new FormLayout(userName, review);

        Binder<ReviewService> binder = new Binder<>(ReviewService.class);
        binder.forField(userName).asRequired().bind(ReviewService::getUserName, ReviewService::setUserName);
        binder.forField(review).asRequired().bind(ReviewService::getReview, ReviewService::setReview);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupGrid(Crud<ReviewService> crud) {
        Grid<ReviewService> grid = crud.getGrid();

        // Only show these columns (all columns shown by default):
        List<String> visibleColumns = Arrays.asList(USER_NAME_COL,REVIEW_COL);
        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!visibleColumns.contains(key)) {
                grid.removeColumn(column);
            }
        });

        // Reorder the columns (alphabetical by default)
        grid.setColumnOrder(grid.getColumnByKey(USER_NAME_COL),
                grid.getColumnByKey(REVIEW_COL));
    }

    private void setupDataProvider(Crud<ReviewService> crud) {
        ReviewDataProvider dataProvider = new ReviewDataProvider(session,storeId,productId);
        crud.setDataProvider(dataProvider);
        crud.addSaveListener(
                saveEvent -> session.addReview(userId,saveEvent.getItem().getReview(),storeId,productId));
    }


}
