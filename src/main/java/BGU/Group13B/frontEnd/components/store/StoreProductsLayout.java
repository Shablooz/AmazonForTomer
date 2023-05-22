package BGU.Group13B.frontEnd.components.store;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.*;

public class StoreProductsLayout extends VerticalLayout implements ResponseHandler {

    private final Session session;
    private final int userId;
    private final int storeId;

    private final HashMap<String, List<ProductInfo>> categoriesToProducts = new HashMap<>();
    private final HashMap<String, ProductGrid> categoriesToGrids = new HashMap<>();
    private Accordion categoriesAccordion = new Accordion();
    private Button addProductButton = new Button(new Icon(VaadinIcon.PLUS));;
    private HorizontalLayout productsHeaderLayout = new HorizontalLayout();
    private final AddProductDialog addProductDialog = new AddProductDialog(this);

    public StoreProductsLayout(int userId, int storeId, Session session){
        super();
        this.userId = userId;;
        this.storeId = storeId;;
        this.session = session;

        setHeader();
        setProducts(handleResponse(session.getAllStoreProductsInfo(userId, storeId)));
        add(productsHeaderLayout, categoriesAccordion);
    }

    public StoreProductsLayout(int userId, int storeId, Session session, Collection<ProductInfo> products){
        this(userId, storeId, session);
        setProducts(products);
    }

    private void setHeader(){
        productsHeaderLayout = new HorizontalLayout();
        addProductButton = new Button(new Icon(VaadinIcon.PLUS));;

        addProductButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        addProductButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        productsHeaderLayout.add(new H2("Products"), addProductButton);
        productsHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        addProductButton.addClickListener(e -> {
            addProductDialog.open();
        });
    }

    public void setProducts(Collection<ProductInfo> products){
        if(products == null){
            return;
        }

        categoriesToProducts.clear();
        categoriesToGrids.clear();
        categoriesAccordion = new Accordion();

        for(ProductInfo product : products){
            if(!categoriesToProducts.containsKey(product.category())){
                categoriesToProducts.put(product.category(), new LinkedList<>());
            }
            categoriesToProducts.get(product.category()).add(product);
        }

        for(String category : categoriesToProducts.keySet()) {
            ProductGrid productGrid = new ProductGrid(categoriesToProducts.get(category));
            categoriesToGrids.put(category, productGrid);
            categoriesAccordion.add(category, productGrid);
        }
    }

    public void addProduct(String name, String category, double price, int stock, String description){
        Integer productId = handleResponse(session.addProduct(userId, storeId, name, category, price, stock, description));
        if(productId == null){
            return;
        }
        notifySuccess("Product '" + name + "' added successfully");
        ProductInfo productInfo = handleResponse(session.getStoreProductInfo(userId, storeId, productId));
        if(productInfo == null){
            notifyInfo("Product '" + name + "' added successfully, but failed to retrieve product info\n" +
                    "Please refresh the page to see the new product");
        }
    }
}
