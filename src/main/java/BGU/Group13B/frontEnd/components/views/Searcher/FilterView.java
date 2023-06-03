package BGU.Group13B.frontEnd.components.views.Searcher;
import BGU.Group13B.frontEnd.components.views.MainLayout;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.data.validator.LongRangeValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static BGU.Group13B.frontEnd.components.views.Searcher.SearcherView.productGrid;

@SpringComponent
@PageTitle("filter")
@Route(value = "filter", layout = MainLayout.class)
public class FilterView extends VerticalLayout {
    private Session session;

    private FormLayout price;
    private FormLayout category;
    private FormLayout productRank;
    private FormLayout storeRank;
    private HorizontalLayout priceRange;
    private NumberField minPriceField;
    private NumberField maxPriceField;
    private HorizontalLayout categoryLayout;
    private TextField categoryField;
    private HorizontalLayout productRankRange;
    private NumberField minProductRankField;
    private NumberField maxProductRankField;
    private HorizontalLayout storeRankRange;
    private NumberField minStoreRankField;
    private NumberField maxStoreRankField;

    private VerticalLayout mainLayout;

    @Autowired
    public FilterView(Session session) {
        this.session = session;
        getStyle().set("background-color", "#171C41");
        Label label = new Label("Filter ");
        label.getStyle().set("font-size", "21px");
        add(label);
        mainLayout = new VerticalLayout();
        buildFirstComponent();
        buildSecondComponent();
        buildApplyButton();
        add(mainLayout);
    }

    public void buildFirstComponent(){
        buildPriceComponent();
        buildCategoryComponent();
        mainLayout.add(category, price);
    }

    public void buildPriceComponent() {
        minPriceField = new NumberField();
        minPriceField.setPlaceholder("Min Price");
        maxPriceField = new NumberField();
        maxPriceField.setPlaceholder("Max Price");
        priceRange = new HorizontalLayout();
        priceRange.add(minPriceField, maxPriceField);
        price = new FormLayout();
        price.addFormItem(priceRange, "Price");
    }

    public void buildCategoryComponent() {
        categoryField = new TextField();
        categoryField.setPlaceholder("Category");
        category = new FormLayout();
        category.addFormItem(categoryField, "Category");
    }

    public void buildSecondComponent(){
        buildProductRankComponent();
        buildStoreRankComponent();
        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        mainLayout.add(storeRank, productRank);
    }

    public void buildProductRankComponent(){
        minProductRankField = new NumberField();
        minProductRankField.setPlaceholder("Min Product Rank");
        maxProductRankField = new NumberField();
        maxProductRankField.setPlaceholder("Max Product Rank");
        productRankRange = new HorizontalLayout();
        productRankRange.add(minProductRankField, maxProductRankField);
        productRank = new FormLayout();
        productRank.addFormItem(productRankRange, "Product Rank");
    }
    public void buildStoreRankComponent(){
        minStoreRankField = new NumberField();
        minStoreRankField.setPlaceholder("Min Store Rank");
        maxStoreRankField = new NumberField();
        maxStoreRankField.setPlaceholder("Max Store Rank");
        storeRankRange = new HorizontalLayout();
        storeRankRange.add(minStoreRankField, maxStoreRankField);
        storeRank = new FormLayout();
        storeRank.addFormItem(storeRankRange, "Store Rank");
    }

    public void buildApplyButton() {
        Button applyFilterButton = new Button("Apply");
        applyFilterButton.setIcon(VaadinIcon.FILTER.create());
        mainLayout.add(applyFilterButton);

        applyFilterButton.addClickListener(event -> {session.filterByPriceRange(minPriceField.getValue(), maxPriceField.getValue());
            session.filterByCategory(categoryField.getValue());
            session.filterByProductRank(minProductRankField.getValue(), maxProductRankField.getValue());
            Response<List<ProductInfo>> products = session.filterByStoreRank(minStoreRankField.getValue(), maxStoreRankField.getValue());
            productGrid.setItems();
            productGrid.setItems(products.getData());
        });
    }
    public void buildClearButton() {
        Button clearButton = new Button("Clear");
        clearButton.setIcon(VaadinIcon.CLOSE_SMALL.create());
        add(clearButton);

        clearButton.addClickListener(event -> {session.filterByPriceRange(minPriceField.getValue(), maxPriceField.getValue());
            session.filterByCategory(categoryField.getValue());
            session.filterByProductRank(minProductRankField.getValue(), maxProductRankField.getValue());
            Response<List<ProductInfo>> products = session.filterByStoreRank(minStoreRankField.getValue(), maxStoreRankField.getValue());
            productGrid.setItems();
            productGrid.setItems(products.getData());
        });
    }

    public void removeFilter() {
        remove(mainLayout);
    }

}
