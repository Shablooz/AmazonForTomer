package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.entity.ServiceBasketProduct;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.gridpro.GridProVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

@Route(value = "cart", layout = MainLayout.class)
@PageTitle("Cart")

public class CartView extends Div implements ResponseHandler {
    private final Session session;
    private final GridPro<ServiceBasketProduct> cartItemsGrid;
    private final Button purchaseItemsButton = new Button("Purchase Items");
    private final NumberField totalPrice = new NumberField("Total Price");

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

    private VerticalLayout getLowerButtons() {
        var horizontal = new HorizontalLayout(purchaseItemsButton(), totalPrice);
        horizontal.setAlignItems(FlexComponent.Alignment.BASELINE);
        return new VerticalLayout(cartItemsGrid, horizontal);
    }

    private Component purchaseItemsButton() {
        totalPrice.setReadOnly(true);
        purchaseItemsButton.addClickListener(buttonClickEvent -> {
            var cartItems = session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId());
            if(handleOptionalResponse(cartItems).orElse(new ArrayList<>()).isEmpty()){
                Notification.show("Please select items to purchase");
                return;
            }
            UI.getCurrent().navigate("payment");
        });
        return purchaseItemsButton;
    }

    private void setItemsToGrid(GridPro<ServiceBasketProduct> cartItemsGrid) {

        //createItemsForDevelopment();//delete me
        //fixme: change to sessionId
        Response<List<ServiceBasketProduct>> serviceProductsResponse = session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId());
        if (serviceProductsResponse.getStatus() == Response.Status.SUCCESS) {
            updateGrid(cartItemsGrid, serviceProductsResponse);
        } else
            Notification.show(serviceProductsResponse.getMessage());
    }

    private void updateGrid(GridPro<ServiceBasketProduct> cartItemsGrid, Response<List<ServiceBasketProduct>> serviceProductsResponse) {
        List<ServiceBasketProduct> serviceBasketProducts = serviceProductsResponse.getData();
        cartItemsGrid.setItems(serviceBasketProducts);
        totalPrice.setValue(serviceBasketProducts.stream().mapToDouble(ServiceBasketProduct::getSubtotal).sum());
    }


    private void createItemsForDevelopment() {
        int storeId = SingletonCollection.getStoreRepository().addStore(SessionToIdMapper.getInstance().getCurrentSessionId(), "StoreName " + (int) (100 * Math.random()), "store category");
        int newProductId1 = session.addProduct(SessionToIdMapper.getInstance().getCurrentSessionId(), storeId, "ProductName " + (int) (100 * Math.random()), "product category " + (int)(100*Math.random()), (int)(10 * Math.random()) + (double)((int)(100 * Math.random()))/100, 10, "Best Product Ever").getData();
        session.addToCart(SessionToIdMapper.getInstance().getCurrentSessionId(), storeId, newProductId1);
    }

    private GridPro<ServiceBasketProduct> getServiceProductGrid() {
        GridPro<ServiceBasketProduct> cartItemsGrid = new GridPro<>();
        cartItemsGrid.addColumn(ServiceBasketProduct::getName).setHeader("Name");
        cartItemsGrid.addEditColumn(ServiceBasketProduct::getQuantity).text(this::changeQuantity).setHeader("Quantity");
        cartItemsGrid.addColumn(ServiceBasketProduct::getPrice).setHeader("Price");
        cartItemsGrid.addColumn(ServiceBasketProduct::getSubtotal).setHeader("Subtotal");
        cartItemsGrid.addColumn(ServiceBasketProduct::getCategory).setHeader("Category");
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
            updateGrid(cartItemsGrid, session.getCartContent(SessionToIdMapper.getInstance().getCurrentSessionId()));
        }
    }
}
