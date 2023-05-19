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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.Button;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@PageTitle("product")
@Route(value = "product", layout = MainLayout.class)
public class ProductView extends VerticalLayout implements HasUrlParameter<String> {
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int productId;
    private int storeId;
    private Session session;
    private HorizontalLayout seller;
    private HorizontalLayout category;
    private HorizontalLayout price;
    private HorizontalLayout description;
    private HorizontalLayout score;
    private Button buyNow;
    private Button addToCart;
    private Button offerBid;

    private String USER_NAME_COL = "User Name";
    private String REVIEW_COL = "Review";


    @Autowired
    public ProductView(Session session) {
        this.session=session;
//        price = new NumberField();
//        stockQuantity = new NumberField();
//        score = new NumberField();
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameters) {
        String[] params = parameters.split("/");
        productId = Integer.parseInt(params[0]);
        storeId = Integer.parseInt(params[1]);
        start();
    }
    private void start(){
        ProductInfo info = session.getStoreProductInfo(userId,storeId,productId).getData(); //TODO: CHECK ON ERRORS
        seller = getIconLabel("Seller :  "+info.seller(),VaadinIcon.MALE);
        category = getIconLabel("Category :  "+info.category(), VaadinIcon.TAGS);
        price = getIconLabel("Price :  " + info.price(), VaadinIcon.CASH);
        description = getIconLabel("Description :  " + info.description(), VaadinIcon.INFO_CIRCLE);
        score = getIconLabel("Score :  "+ info.score(), VaadinIcon.STAR);
        add(new H1(info.name()));
        setAlignItems(Alignment.CENTER);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(seller, category, price, description, score);
        verticalLayout.getStyle().set("background-color", "#171C41");
        verticalLayout.setWidth("50%");
        add(verticalLayout);
        buyNow = new com.vaadin.flow.component.button.Button("Buy Now");
        buyNow.setIcon(VaadinIcon.CREDIT_CARD.create());
        addToCart = new Button("Add To Cart");
        addToCart.setIcon(VaadinIcon.CART_O.create());
        offerBid = new Button("Offer Bid");
        offerBid.setIcon(VaadinIcon.CASH.create());
        HorizontalLayout Buttons = new HorizontalLayout();
        Buttons.add(buyNow, addToCart, offerBid);
        add(Buttons);
    }

    private HorizontalLayout getIconLabel(String text, VaadinIcon icon){
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Label label = new Label(text);
        label.getStyle().set("font-size", "20px");
        horizontalLayout.add(icon.create(), label);
        return horizontalLayout;
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
