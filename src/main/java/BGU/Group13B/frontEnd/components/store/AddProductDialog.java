package BGU.Group13B.frontEnd.components.store;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class AddProductDialog extends Dialog{

    public AddProductDialog(StoreProductsLayout storeProductsLayout) {
        super();

        setWidth("350px");
        setHeight("630px");
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
            if(validInputs(productName, productCategory, productPrice, productStock, productDescription)){
                storeProductsLayout.addProduct(productName.getValue(), productCategory.getValue(),
                        productPrice.getValue(), productStock.getValue(), productDescription.getValue());
                close();
            }

        });


        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(dialogTitle, productName, productCategory, productPrice, productStock, productDescription, confirmButton);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialogLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        add(dialogLayout);
    }

    private boolean validInputs(TextField productName, TextField productCategory, NumberField productPrice, IntegerField productStock, TextArea productDescription) {
        boolean flag = true;
        if(productName.getValue().isEmpty()){
            productName.setInvalid(true);
            flag =  false;
        }

        if(productCategory.getValue().isEmpty()){
            productCategory.setInvalid(true);
            flag =  false;
        }

        if(productPrice.getValue() <= 0){
            productPrice.setInvalid(true);
            flag =  false;
        }

        if(productStock.getValue() < 0){
            productStock.setInvalid(true);
            flag =  false;
        }

        if(productDescription.getValue().isEmpty()){
            productDescription.setInvalid(true);
            flag =  false;
        }
        return flag;
    }

    public void openDialog(){
        open();
    }


}
