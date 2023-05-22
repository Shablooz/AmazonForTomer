package BGU.Group13B.frontEnd.components.store;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class AddProductDialog extends Dialog{
    private final StoreProductsLayout storeProductsLayout;

    public AddProductDialog(StoreProductsLayout storeProductsLayout) {
        super();
        this.storeProductsLayout = storeProductsLayout;

        setWidth("350px");
        setHeight("550px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

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
             storeProductsLayout.addProduct(productName.getValue(), productCategory.getValue(),
                    productPrice.getValue(), productStock.getValue(), productDescription.getValue());
            close();
        });


        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, productName, productPrice, productStock, productDescription, confirmButton);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(dialogLayout);
    }

    public void openDialog(){
        open();
    }


}
