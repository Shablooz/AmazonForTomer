package BGU.Group13B.frontEnd.components.store.products;

import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.*;

public class StoreProductsLayout extends VerticalLayout implements ResponseHandler {

    private final Session session;
    private final int userId;
    private final int storeId;
    private final WorkerCard workerCard;

    private final HashMap<String, List<ProductInfo>> categoriesToProducts = new HashMap<>();
    private final HashMap<String, ProductGrid> categoriesToGrids = new HashMap<>();
    private Accordion categoriesAccordion = new Accordion();
    private Button addProductButton = new Button(new Icon(VaadinIcon.PLUS));;
    private HorizontalLayout productsHeaderLayout = new HorizontalLayout();
    private final AddProductDialog addProductDialog = new AddProductDialog(this);

    public StoreProductsLayout(int userId, int storeId, Session session, Collection<ProductInfo> products, WorkerCard workerCard){
        super();
        this.userId = userId;;
        this.storeId = storeId;;
        this.session = session;
        this.workerCard = workerCard;

        setHeader();
        setProducts(products);
        add(productsHeaderLayout, categoriesAccordion);
    }

    private void setHeader(){
        productsHeaderLayout = new HorizontalLayout();
        addProductButton = new Button(new Icon(VaadinIcon.PLUS));

        addProductButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        addProductButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        productsHeaderLayout.add(new H2("Products"), addProductButton);
        productsHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        addProductButton.addClickListener(e -> {
            if(!workerCard.userPermissions().contains(UserPermissions.IndividualPermission.STOCK)){
                notifyWarning("You don't have permission to add products");
            }
            else{
                addProductDialog.open();
            }
        });

        if(!workerCard.userPermissions().contains(UserPermissions.IndividualPermission.STOCK)){
            addProductButton.setVisible(false);
        }
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
            ProductGrid productGrid = new ProductGrid(this, categoriesToProducts.get(category), workerCard);
            categoriesToGrids.put(category, productGrid);
            categoriesAccordion.add(category, productGrid);
        }
    }

    public void addProduct(String name, String category, double price, int stock, String description){
        Integer productId = handleResponse(session.addProduct(userId, storeId, name, category, price, stock, description));
        if(productId == null){
            return;
        }
        ProductInfo productInfo = handleResponse(session.getStoreProductInfo(userId, storeId, productId));
        if(productInfo == null){
            notifyInfo("Product '" + name + "' added successfully, but failed to retrieve product info\n" +
                    "Please refresh the page to see the new product");
            return;
        }
        if(!categoriesToProducts.containsKey(category)){
            categoriesToProducts.put(category, new LinkedList<>());
            ProductGrid productGrid = new ProductGrid(this, categoriesToProducts.get(category), workerCard);
            categoriesToGrids.put(category, productGrid);
            categoriesAccordion.add(category, productGrid);
        }
        categoriesToProducts.get(category).add(productInfo);
        categoriesToGrids.get(category).addProduct(productInfo);
        notifySuccess("Product '" + name + "' added successfully");
    }

    public void editProductPrice(ProductInfo item, double v) {
        var res = handleResponse(session.setProductPrice(userId, storeId, item.productId(), v));
        if(res == null){
            return;
        }
        ProductInfo productInfo = new ProductInfo(item.productId(), item.storeId(), item.seller(), item.name(), item.category(), v, item.stockQuantity(), item.description(), item.score());
        categoriesToProducts.get(item.category()).remove(item);
        categoriesToProducts.get(item.category()).add(productInfo);
        categoriesToGrids.get(item.category()).editProductPrice(item, v);
    }

    public void editProductStock(ProductInfo item, int stock) {
        var res = handleResponse(session.setProductStockQuantity(userId, storeId, item.productId(), stock));
        if(res == null){
            return;
        }
        ProductInfo productInfo = new ProductInfo(item.productId(), item.storeId(), item.seller(), item.name(), item.category(), item.price(), stock, item.description(), item.score());
        categoriesToProducts.get(item.category()).remove(item);
        categoriesToProducts.get(item.category()).add(productInfo);
        categoriesToGrids.get(item.category()).editProductStock(item, stock);
    }

    public void removeProduct(ProductInfo p) {
        var res = handleResponse(session.removeProduct(userId, storeId, p.productId()));
        if(res == null){
            return;
        }
        categoriesToProducts.get(p.category()).remove(p);
        if(categoriesToProducts.get(p.category()).isEmpty()){
            categoriesAccordion.remove(categoriesToGrids.get(p.category()));
            categoriesToGrids.remove(p.category());
            categoriesToProducts.remove(p.category());
            return;
        }
        categoriesToGrids.get(p.category()).removeProduct(p);
    }
}
