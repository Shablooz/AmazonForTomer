package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.views.viewEntity.Product;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.entity.ServiceBasketProduct;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.gridpro.GridProVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.*;

@Route(value = "cart", layout = MainLayout.class)
@PageTitle("Cart")

public class CartView extends Div implements ResponseHandler {
    private final Session session;
    private final GridPro<ServiceBasketProduct> cartItemsGrid;
    private final Button purchaseItemsButton = new Button("Purchase Items");
    private final NumberField totalPrice = new NumberField("Total Price");
    private ComboBox<Product> storeName;
    private ComboBox<Product> productName;

    public CartView(Session session) {
        this.session = session;
        //setSizeFull();
        //do dynamic scaling

        cartItemsGrid = getServiceProductGrid();
        cartItemsGrid.addThemeVariants(GridProVariant.LUMO_HIGHLIGHT_READ_ONLY_CELLS);
        //cartItemsGrid.setSizeFull();
        // Add the grid to the layout
        cartItemsGrid.setAllRowsVisible(true);
        add(getLowerButtons());

        setItemsToGrid(cartItemsGrid);
    }

    private VerticalLayout createDialogLayout() {
        List<ServiceBasketProduct> serviceProducts = handleResponse(session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId()));
        var products = serviceProducts.stream().map(serviceBasketProduct -> new Product(serviceBasketProduct.getName(), serviceBasketProduct.getStoreName(session))).toList();
        List<Product> storeNames = new ArrayList<>();
        for(var product : products){
            int size = storeNames.stream().filter(product1 -> Objects.equals(product.getStoreName(), product1.getStoreName())).toList().size();
            if(size == 0)
                storeNames.add(product);
        }
        List<Product> productNames = new ArrayList<>();
        for(var product : products){
            int size = productNames.stream().filter(product1 -> Objects.equals(product.getProductName(), product1.getProductName())).toList().size();
            if(size == 0)
                productNames.add(product);
        }
        productName = new ComboBox<>("Product name");
        productName.setItems(productNames);
        productName.setItemLabelGenerator(Product::getProductName);

        storeName = new ComboBox<>("Store name");
        storeName.setItems(storeNames);
        storeName.setItemLabelGenerator(Product::getStoreName);

        VerticalLayout dialogLayout = new VerticalLayout(storeName,
                productName);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private Button createRemoveButton(Dialog dialog) {
        Button removeButton = new Button("Remove", e -> {
            if (productName.isEmpty() || storeName.isEmpty()) {
                Notification.show("please enter both values");
                return;
            }
            int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
            List<ServiceBasketProduct> serviceProducts = handleResponse(session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId()));
            if(serviceProducts == null)
                return;
            String productNameString = productName.getValue().getProductName();
            String storeNameString = storeName.getValue().getStoreName();
            int productId = serviceProducts.stream().filter(serviceBasketProduct -> serviceBasketProduct.getName().equals(productNameString)).findFirst().get().getProductId();
            int storeId = serviceProducts.stream().filter(serviceBasketProduct -> serviceBasketProduct.getStoreName(session).equals(storeNameString)).findFirst().get().getStoreId();
            handleResponse(session.removeBasketProduct(userId, productId, storeId));
            var serviceProductsResponse = handleResponse(session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId()), "");
            if(serviceProductsResponse == null)
                return;

            updateGrid(cartItemsGrid, serviceProductsResponse);
            dialog.close();
        });
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        return removeButton;
    }

    private VerticalLayout getLowerButtons() {
        var horizontal = new HorizontalLayout(purchaseItemsButton(), totalPrice);
        horizontal.setAlignItems(FlexComponent.Alignment.BASELINE);
        return new VerticalLayout(cartItemsGrid, horizontal);
    }

    private Component purchaseItemsButton() {
        totalPrice.setReadOnly(true);
        purchaseItemsButton.addClickListener(buttonClickEvent -> {
            var cartItems = session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId());
            if (handleOptionalResponse(cartItems).orElse(new ArrayList<>()).isEmpty()) {
                Notification.show("Please select items to purchase");
                return;
            }
            UI.getCurrent().navigate("payment");
        });
        return purchaseItemsButton;
    }

    private void setItemsToGrid(GridPro<ServiceBasketProduct> cartItemsGrid) {
        var cartContent = handleResponse(session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId()));
        updateGrid(cartItemsGrid, cartContent);
    }

    private void updateGrid(GridPro<ServiceBasketProduct> cartItemsGrid, List<ServiceBasketProduct> serviceProductsResponse) {
        if(serviceProductsResponse == null)
            return;
        List<ServiceBasketProduct> serviceBasketProducts = serviceProductsResponse.stream().filter(serviceBasketProduct -> serviceBasketProduct.storeExists(session)).toList();
        List<ServiceBasketProduct> serviceBasketProductsToRemove = serviceProductsResponse.stream().filter(serviceBasketProduct -> !serviceBasketProduct.storeExists(session)).toList();
        if (serviceBasketProductsToRemove.size() > 0)
            handleResponse(session.removeBasketProducts(serviceBasketProductsToRemove, SessionToIdMapper.getInstance().getCurrentSessionId()));
        cartItemsGrid.setItems(serviceBasketProducts);
        totalPrice.setValue(serviceBasketProducts.stream().mapToDouble(ServiceBasketProduct::getSubtotal).sum());
    }


    private void createItemsForDevelopment() {
        int storeId = SingletonCollection.getStoreRepository().addStore(SessionToIdMapper.getInstance().getCurrentSessionId(), "StoreName " + (int) (100 * Math.random()), "store category");
        int newProductId1 = session.addProduct(SessionToIdMapper.getInstance().getCurrentSessionId(), storeId, "ProductName " + (int) (100 * Math.random()), "product category " + (int) (100 * Math.random()), (int) (10 * Math.random()) + (double) ((int) (100 * Math.random())) / 100, 10, "Best Product Ever").getData();
        session.addToCart(SessionToIdMapper.getInstance().getCurrentSessionId(), storeId, newProductId1);
    }

    private GridPro<ServiceBasketProduct> getServiceProductGrid() {
        GridPro<ServiceBasketProduct> cartItemsGrid = new GridPro<>();
        cartItemsGrid.addColumn(ServiceBasketProduct::getName).setHeader("Name");
        cartItemsGrid.addEditColumn(ServiceBasketProduct::getQuantity).text(this::changeQuantity).setHeader("Quantity");
        cartItemsGrid.addColumn(ServiceBasketProduct::getPrice).setHeader("Price");
        cartItemsGrid.addColumn(ServiceBasketProduct::getSubtotal).setHeader("Subtotal");
        cartItemsGrid.addColumn(ServiceBasketProduct::getCategory).setHeader("Category");
        cartItemsGrid.addColumn(serviceBasketProduct -> serviceBasketProduct.getStoreName(session)).setHeader("Store name");
        cartItemsGrid.addComponentColumn(serviceBasketProduct -> new Button("Remove", e -> {
            handleResponse(session.removeBasketProduct(SessionToIdMapper.getInstance().getCurrentSessionId(), serviceBasketProduct.getProductId(), serviceBasketProduct.getStoreId()));
            updateGrid(cartItemsGrid, handleResponse(session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId())));
        })).setHeader("Remove");
        //total price before discounts
        cartItemsGrid.setSizeFull();
        return cartItemsGrid;
    }

    private void changeQuantity(ServiceBasketProduct serviceBasketProduct, String newValue) {
        if (newValue == null || newValue.isEmpty())
            return;
        int newQuantity;
        try {
            newQuantity = Integer.parseInt(newValue);
        } catch (NumberFormatException e) {
            Notification.show("Quantity must be a number");
            return;
        }
        if (newQuantity <= 0)
            Notification.show("Quantity must be positive");
        else {
            session.changeProductQuantityInCart(SessionToIdMapper.getInstance().getCurrentSessionId(), serviceBasketProduct.getStoreId(), serviceBasketProduct.getProductId(),
                    newQuantity);
            // Update the quantity when the user changes the value in the TextField
            updateGrid(cartItemsGrid, handleResponse(session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId())));
        }
    }
}
