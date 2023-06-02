package BGU.Group13B.frontEnd.components.policyComponent;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.icon.Icon;

import java.time.LocalDate;
import java.util.Set;

public class AddDiscountComponent extends VerticalLayout implements ResponseHandler {

    private final int userId;

    private enum DiscountType {
        STORE_DISCOUNT,
        CATEGORY_DISCOUNT,
        PRODUCT_DISCOUNT,
    }

    private final Session session;
    private final int storeId;

    private final ComboBox<ProductInfo> products;
    private final ComboBox<ProductInfo> categories;
    private final ComboBox<DiscountType> discountType;
    private final NumberField discountPercentage = new NumberField("Discount percentage");
    private final TextField coupon = new TextField("Coupon");
    private final DatePicker expirationDate = new DatePicker("Expiration date");

    public AddDiscountComponent(Session session, int storeId) {
        this.session = session;
        this.storeId = storeId;
        userId = SessionToIdMapper.getInstance().getCurrentSessionId();
        products = new ComboBox<>("Products");
        categories = new ComboBox<>("Categories");
        discountType = new ComboBox<>("Discount Type");
        var productsSet = session.getAllStoreProductsInfo(userId, storeId);
        var productsList = handleOptionalResponse(productsSet).orElse(Set.of()).stream().toList();

        products.setItems(productsList);
        products.setItemLabelGenerator(ProductInfo::name);
        categories.setItems(productsList);
        categories.setItemLabelGenerator(ProductInfo::category);
        discountType.setItemLabelGenerator(this::getName);

        products.setVisible(false);
        categories.setVisible(false);
        discountType.setItems(DiscountType.values());
        discountType.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                switch (e.getValue()) {
                    case STORE_DISCOUNT -> {
                        products.setVisible(false);
                        categories.setVisible(false);
                        products.setRequired(false);
                        products.setRequiredIndicatorVisible(false);
                        categories.setRequired(false);
                        categories.setRequiredIndicatorVisible(false);
                    }
                    case CATEGORY_DISCOUNT -> {
                        products.setVisible(false);
                        categories.setVisible(true);
                        products.setRequired(false);
                        products.setRequiredIndicatorVisible(false);
                        categories.setRequired(true);
                        categories.setRequiredIndicatorVisible(true);
                    }
                    case PRODUCT_DISCOUNT -> {
                        products.setVisible(true);
                        categories.setVisible(false);
                        products.setRequired(true);
                        products.setRequiredIndicatorVisible(true);
                        categories.setRequired(false);
                        categories.setRequiredIndicatorVisible(false);
                    }
                }
            }
        });
        discountType.setRequired(true);
        discountType.setRequiredIndicatorVisible(true);
        discountPercentage.setSuffixComponent(new Icon(VaadinIcon.BOOK_PERCENT));
        discountPercentage.setRequired(true);
        discountPercentage.setRequiredIndicatorVisible(true);
        expirationDate.setRequired(true);
        expirationDate.setRequiredIndicatorVisible(true);
        coupon.setPlaceholder("optional");

        add(discountType, products, categories, discountPercentage, expirationDate, coupon);
    }


    public void addDiscount() {
        double discountPercentage = this.discountPercentage.getValue() / 100;
        String coupon = this.coupon.getValue();
        LocalDate expirationDate = this.expirationDate.getValue();
        boolean hasCoupon = this.coupon.isEmpty() || coupon.equals("");
        if (discountPercentage <= 0 || discountPercentage > 100) {
            notifyInfo("discount percentage must be between 0 and 100");
            return false;
        }
        Integer discount = null;
        switch (discountType.getValue()) {
            case STORE_DISCOUNT -> {
                if (hasCoupon)
                    discount = handleResponse(session.addStoreDiscount(storeId, userId, discountPercentage, expirationDate, coupon));
                else
                    discount = handleResponse(session.addStoreDiscount(storeId, userId, discountPercentage, expirationDate));
            }
            case CATEGORY_DISCOUNT -> {
                if (hasCoupon)
                    discount = handleResponse(session.addCategoryDiscount(storeId, userId, discountPercentage, expirationDate, categories.getValue().category(), coupon));
                else
                    discount = handleResponse(session.addCategoryDiscount(storeId, userId, discountPercentage, expirationDate, categories.getValue().category()));
            }
            case PRODUCT_DISCOUNT -> {
                if (hasCoupon)
                    discount = handleResponse(session.addProductDiscount(storeId, userId, discountPercentage, expirationDate, products.getValue().productId(), coupon));
                else
                    discount = handleResponse(session.addProductDiscount(storeId, userId, discountPercentage, expirationDate, products.getValue().productId()));
            }
            case null -> notifyInfo("please select discount type");
        }
        if (discount != null) {
            notifySuccess("the discount was added successfully");
            return true;
        }
        return false;
    }

    public void addDiscount(int conditionId) {
        if(discountPercentage.isEmpty() || expirationDate.isEmpty()){
            return false;
        }
        double discountPercentage = this.discountPercentage.getValue() / 100;
        String coupon = this.coupon.getValue();
        LocalDate expirationDate = this.expirationDate.getValue();
        boolean hasCoupon = this.coupon.isEmpty() || coupon.equals("");
        if (discountPercentage <= 0 || discountPercentage > 100) {
            this.discountPercentage.setErrorMessage("discount percentage must be between 0 and 100");
            return false;
        }
        Integer discount = null;
        switch (discountType.getValue()) {
            case STORE_DISCOUNT -> {
                if (hasCoupon)
                    discount = handleResponse(session.addStoreDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, coupon));
                else
                    discount = handleResponse(session.addStoreDiscount(storeId, userId, conditionId, discountPercentage, expirationDate));
            }
            case CATEGORY_DISCOUNT -> {
                if(categories.isEmpty())
                    return false;
                if (hasCoupon)
                    discount = handleResponse(session.addCategoryDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, categories.getValue().category(), coupon));
                else
                    discount = handleResponse(session.addCategoryDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, categories.getValue().category()));
            }
            case PRODUCT_DISCOUNT -> {
                if(products.isEmpty())
                    return false;
                if (hasCoupon)
                    discount = handleResponse(session.addProductDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, products.getValue().productId(), coupon));
                else
                    discount = handleResponse(session.addProductDiscount(storeId, userId, conditionId, discountPercentage, expirationDate, products.getValue().productId()));
            }
        }
        if (discount != null) {
            notifySuccess("the discount was added with the condition successfully");
            return true;
        }
        return false;
    }

    private String getName(DiscountType discountType) {
        String[] words = discountType.name().split("_");
        StringBuilder name = new StringBuilder();
        for (String word : words) {
            name.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
        }
        return name.toString().trim();
    }
}
