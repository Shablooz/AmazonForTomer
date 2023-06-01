package BGU.Group13B.frontEnd.components.policyComponent;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.*;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static java.util.List.of;


public class ConditionDialog extends Dialog {

    private final ConditionTreeGrid conditionTreeGrid;
    private boolean hasUpperBound = false;

    private final NumberField doubleLowerBound = new NumberField("lower bound");
    private final IntegerField intLowerBound = new IntegerField("lower bound");
    private final DatePicker dateLowerBound = new DatePicker("from date");
    private final TimePicker timeLowerBound = new TimePicker("from time");

    private final NumberField doubleUpperBound = new NumberField("upper bound");
    private final IntegerField intUpperBound = new IntegerField("upper bound");
    private final DatePicker dateUpperBound = new DatePicker("to date");
    private final TimePicker timeUpperBound = new TimePicker("to time");

    private final VerticalLayout dialogLayout = new VerticalLayout();
    private final Button revealUpperBoundBtn = new Button(new Icon(VaadinIcon.PLUS));
    private final Button hideUpperBoundBtn = new Button(new Icon(VaadinIcon.MINUS));
    private final Button confirm = new Button("Confirm");

    private final ComboBox<ProductInfo> products = new ComboBox<>("product");
    private final ComboBox<ProductInfo> categories = new ComboBox<>("category");

    private Set<ProductInfo> productsInfos;

    /*
    *  ComboBox<String> comboBox = new ComboBox<>("category");
        comboBox.setAutoOpen(false);
        add(comboBox);
        comboBox.setItems(session.getProducts().getCategories());
        comboBox.setItemLabelGenerator(Country::getName);??
    * */

    public ConditionDialog(ConditionTreeGrid conditionTreeGrid, ConditionEntity conditionEntity, Session session, int storeId, ConditionEntity plusToDelete) {
        this.conditionTreeGrid = conditionTreeGrid;

        //can't close dialog
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        hideAllFields();
        showRelevantLowerBound(conditionEntity);
        revealUpperBoundBtn.addClickListener(event -> {
            showRelevantUpperBound(conditionEntity);
            revealUpperBoundBtn.setVisible(false);
            hideUpperBoundBtn.setVisible(true);
            hasUpperBound = true;
        });
        hideUpperBoundBtn.addClickListener(event -> {
            hideAllFields();
            showRelevantLowerBound(conditionEntity);
            revealUpperBoundBtn.setVisible(true);
            hideUpperBoundBtn.setVisible(false);
            hasUpperBound = false;
        });

        confirm.addClickListener(e -> confirm(conditionEntity, plusToDelete));

        this.productsInfos = getProducts(session, storeId);
        products.setAutoOpen(false);
        products.setItems(productsInfos);
        products.setItemLabelGenerator(ProductInfo::name);


        categories.setAutoOpen(false);
        categories.setItems(productsInfos);
        categories.setItemLabelGenerator(ProductInfo::category);


        categories.setVisible(false);
        products.setVisible(false);
        switch (conditionEntity) {
            case CategoryPriceConditionEntity __ -> categories.setVisible(true);
            case ProductPriceConditionEntity __ -> products.setVisible(true);

            case CategoryQuantityConditionEntity __ -> categories.setVisible(true);
            case ProductQuantityConditionEntity __ -> products.setVisible(true);

            default -> products.setVisible(false);//do nothing;
        }


        String className = conditionEntity.getClass().getSimpleName();
        H1 dialogTitle = new H1(className);
        dialogLayout.add(dialogTitle, products, categories, doubleLowerBound, intLowerBound, dateLowerBound, timeLowerBound,
                revealUpperBoundBtn, hideUpperBoundBtn, doubleUpperBound, intUpperBound, dateUpperBound, timeUpperBound, confirm);

        add(dialogLayout);

        //Lior style
        revealUpperBoundBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
        revealUpperBoundBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        hideUpperBoundBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
        hideUpperBoundBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        confirm.addThemeVariants(ButtonVariant.LUMO_SMALL);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
    }

    private void showRelevantLowerBound(ConditionEntity conditionEntity) {
        reveal(conditionEntity, doubleLowerBound, intLowerBound, dateLowerBound, timeLowerBound);
    }

    private void showRelevantUpperBound(ConditionEntity conditionEntity) {
        reveal(conditionEntity, doubleUpperBound, intUpperBound, dateUpperBound, timeUpperBound);
    }

    private void reveal(ConditionEntity conditionEntity, NumberField doubleBound, IntegerField intBound, DatePicker dateBound, TimePicker timeBound) {
        switch (conditionEntity) {
            case CategoryPriceConditionEntity __ -> doubleBound.setVisible(true);
            case ProductPriceConditionEntity __ -> doubleBound.setVisible(true);
            case StorePriceConditionEntity __ -> doubleBound.setVisible(true);

            case CategoryQuantityConditionEntity __ -> intBound.setVisible(true);
            case ProductQuantityConditionEntity __ -> intBound.setVisible(true);
            case StoreQuantityConditionEntity __ -> intBound.setVisible(true);
            case UserAgeConditionEntity __ -> intBound.setVisible(true);

            case DateConditionEntity __ -> dateBound.setVisible(true);
            case TimeConditionEntity __ -> timeBound.setVisible(true);
            default -> throw new IllegalStateException("Unexpected value: " + conditionEntity);
        }
    }

    private void hideAllFields() {
        doubleLowerBound.setVisible(false);
        intLowerBound.setVisible(false);
        dateLowerBound.setVisible(false);
        timeLowerBound.setVisible(false);

        doubleUpperBound.setVisible(false);
        intUpperBound.setVisible(false);
        dateUpperBound.setVisible(false);
        timeUpperBound.setVisible(false);

        hideUpperBoundBtn.setVisible(false);
    }

    public void confirm(ConditionEntity conditionEntity, ConditionEntity plusToDelete) {
        // relevant fields not empty
        boolean invalid = false;
        switch (conditionEntity) {
            case CategoryPriceConditionEntity __ -> {
                invalid = doubleBoundsCheck();
                if (invalid) {
                    break;
                }
                double lowerBound = doubleLowerBound.getValue();
                if (hasUpperBound) {
                    double upperBound = doubleUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new CategoryPriceConditionEntity(categories.getValue().category(), conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new CategoryPriceConditionEntity(categories.getValue().category(), conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            case ProductPriceConditionEntity __ -> {
                invalid = doubleBoundsCheck();
                if (invalid) {
                    break;
                }
                double lowerBound = doubleLowerBound.getValue();
                if (hasUpperBound) {
                    double upperBound = doubleUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new ProductPriceConditionEntity(products.getValue().productId(), products.getValue().name(), conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new ProductPriceConditionEntity(products.getValue().productId(), products.getValue().name(), conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            case StorePriceConditionEntity __ -> {
                invalid = doubleBoundsCheck();
                if (invalid) {
                    break;
                }
                double lowerBound = doubleLowerBound.getValue();
                if (hasUpperBound) {
                    double upperBound = doubleUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new StorePriceConditionEntity(conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new StorePriceConditionEntity(conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            case CategoryQuantityConditionEntity __ -> {
                invalid = intBoundsCheck();
                if (invalid) {
                    break;
                }
                int lowerBound = intLowerBound.getValue();
                if (hasUpperBound) {
                    int upperBound = intUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new CategoryQuantityConditionEntity(categories.getValue().category(), conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new CategoryQuantityConditionEntity(categories.getValue().category(), conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            case ProductQuantityConditionEntity __ -> {
                invalid = intBoundsCheck();
                if (invalid) {
                    break;
                }
                int lowerBound = intLowerBound.getValue();
                if (hasUpperBound) {
                    int upperBound = intUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new ProductQuantityConditionEntity(products.getValue().productId(), products.getValue().name(), conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new ProductQuantityConditionEntity(products.getValue().productId(), products.getValue().name(), conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            case StoreQuantityConditionEntity __ -> {
                invalid = intBoundsCheck();
                if (invalid) {
                    break;
                }
                int lowerBound = intLowerBound.getValue();
                if (hasUpperBound) {
                    int upperBound = intUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new StoreQuantityConditionEntity(conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new StoreQuantityConditionEntity(conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            case UserAgeConditionEntity __ -> {
                invalid = intBoundsCheck();
                if (invalid) {
                    break;
                }
                int lowerBound = intLowerBound.getValue();
                if (hasUpperBound) {
                    int upperBound = intUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new UserAgeConditionEntity(conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new UserAgeConditionEntity(conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            case DateConditionEntity __ -> {
                invalid = dateBoundsCheck();
                if (invalid) {
                    break;
                }
                LocalDate lowerBound = dateLowerBound.getValue();
                if (hasUpperBound) {
                    LocalDate upperBound = dateUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new DateConditionEntity(conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new DateConditionEntity(conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            case TimeConditionEntity __ -> {
                invalid = timeBoundsCheck();
                if (invalid) {
                    break;
                }
                LocalTime lowerBound = timeLowerBound.getValue();
                if (hasUpperBound) {
                    LocalTime upperBound = timeUpperBound.getValue();
                    conditionTreeGrid.addLeafCondition(new TimeConditionEntity(conditionEntity.getParent(), lowerBound, upperBound), plusToDelete);
                } else {
                    conditionTreeGrid.addLeafCondition(new TimeConditionEntity(conditionEntity.getParent(), lowerBound), plusToDelete);
                }
                close();
            }
            default -> throw new IllegalStateException("Unexpected value: " + conditionEntity);
        }

        if (!invalid)
            close();
    }


    private boolean doubleBoundsCheck() {
        boolean invalid = false;
        if (doubleLowerBound.isEmpty() || doubleLowerBound.getValue() < 0) {
            doubleLowerBound.setInvalid(true);
            invalid = true;
        }
        if (hasUpperBound && (doubleUpperBound.isEmpty() || doubleUpperBound.getValue() < 0)) {
            doubleUpperBound.setInvalid(true);
            invalid = true;
        }
        if (!invalid && hasUpperBound && doubleLowerBound.getValue() > doubleUpperBound.getValue())
            invalid = true;

        return invalid;
    }


    private boolean intBoundsCheck() {
        boolean invalid = false;
        if (intLowerBound.isEmpty() || intLowerBound.getValue() < 0) {
            intLowerBound.setInvalid(true);
            invalid = true;
        }
        if (hasUpperBound && (intUpperBound.isEmpty() || intUpperBound.getValue() < 0)) {
            intUpperBound.setInvalid(true);
            invalid = true;
        }
        if (!invalid && hasUpperBound && intUpperBound.getValue() > intUpperBound.getValue())
            invalid = true;

        return invalid;

    }

    private boolean timeBoundsCheck() {
        boolean invalid = false;
        if (timeLowerBound.isEmpty() || timeLowerBound.getValue() == null) {
            timeLowerBound.setInvalid(true);
            invalid = true;
        }
        if (hasUpperBound && (timeUpperBound.isEmpty() || timeUpperBound.getValue() == null)) {
            timeUpperBound.setInvalid(true);
            invalid = true;
        }
        return invalid;
    }

    private boolean dateBoundsCheck() {
        boolean invalid = false;
        if (dateLowerBound.isEmpty() || dateLowerBound.getValue() == null) {
            dateLowerBound.setInvalid(true);
            invalid = true;
        }
        if (hasUpperBound && dateUpperBound.isEmpty() || dateUpperBound.getValue() == null) {
            dateUpperBound.setInvalid(true);
            invalid = true;
        }
        if (hasUpperBound && dateLowerBound.getValue().isAfter(dateUpperBound.getValue())) {
            dateLowerBound.setInvalid(true);
            dateUpperBound.setInvalid(true);
            invalid = true;
        }
        return invalid;
    }

    private Set<ProductInfo> getProducts(Session session, int storeId) {
        int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
        Response<Set<ProductInfo>> response = session.getAllStoreProductsInfo(userId, storeId);

        if (response.didntSucceed()) {
            notifyError("Error: " + response.getMessage());
            close();
            return Set.of();
        }
        return response.getData();
    }

    void notifyError(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

}
