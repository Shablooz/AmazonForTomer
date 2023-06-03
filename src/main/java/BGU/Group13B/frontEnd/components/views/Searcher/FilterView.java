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
import static BGU.Group13B.frontEnd.components.views.Searcher.SearcherView.searchTerm;

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

    @Autowired
    public FilterView(Session session) {
        this.session = session;
        getStyle().set("background-color", "#171C41");
        Label label = new Label("Filter ");
        label.getStyle().set("font-size", "21px");
        add(label);
        buildFirstComponent();
        buildSecondComponent();
        buildClearButton();
    }

    public void buildFirstComponent(){
        buildPriceComponent();
        buildCategoryComponent();
        add(category, price);
    }

    public void buildPriceComponent() {
        minPriceField = new NumberField();
        minPriceField.setPlaceholder("Min Price");
        maxPriceField = new NumberField();
        maxPriceField.setPlaceholder("Max Price");

        Button applyFilterButton = new Button("Apply");
        applyFilterButton.setIcon(VaadinIcon.FILTER.create());

        applyFilterButton.addClickListener(event -> {
            Response<List<ProductInfo>> products = session.filterByPriceRange(minPriceField.getValue(), maxPriceField.getValue());
            productGrid.setItems();
            productGrid.setItems(products.getData());
        });

        priceRange = new HorizontalLayout();
        priceRange.add(minPriceField, maxPriceField, applyFilterButton);
        price = new FormLayout();
        price.addFormItem(priceRange, "Price");
    }

    public void buildCategoryComponent() {
        categoryField = new TextField();
        categoryField.setPlaceholder("Category");

        Button applyFilterButton = new Button("Apply");
        applyFilterButton.setIcon(VaadinIcon.FILTER.create());
        add(applyFilterButton);

        applyFilterButton.addClickListener(event -> {
            Response<List<ProductInfo>> products = session.filterByCategory(categoryField.getValue());
            productGrid.setItems();
            productGrid.setItems(products.getData());
        });

        categoryLayout = new HorizontalLayout();
        categoryLayout.add(categoryField, applyFilterButton);
        category = new FormLayout();
        category.addFormItem(categoryLayout, "Category");
    }

    public void buildSecondComponent(){
        buildProductRankComponent();
        buildStoreRankComponent();
        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        add(storeRank, productRank);
    }

    public void buildProductRankComponent(){
        minProductRankField = new NumberField();
        minProductRankField.setPlaceholder("Min Product Rank");
        maxProductRankField = new NumberField();
        maxProductRankField.setPlaceholder("Max Product Rank");

        Button applyFilterButton = new Button("Apply");
        applyFilterButton.setIcon(VaadinIcon.FILTER.create());
        add(applyFilterButton);

        applyFilterButton.addClickListener(event -> {
            Response<List<ProductInfo>> products = session.filterByProductRank(minProductRankField.getValue(), maxProductRankField.getValue());
            productGrid.setItems();
            productGrid.setItems(products.getData());
        });

        productRankRange = new HorizontalLayout();
        productRankRange.add(minProductRankField, maxProductRankField, applyFilterButton);
        productRank = new FormLayout();
        productRank.addFormItem(productRankRange, "Product Rank");
    }
    public void buildStoreRankComponent(){
        minStoreRankField = new NumberField();
        minStoreRankField.setPlaceholder("Min Store Rank");
        maxStoreRankField = new NumberField();
        maxStoreRankField.setPlaceholder("Max Store Rank");

        Button applyFilterButton = new Button("Apply");
        applyFilterButton.setIcon(VaadinIcon.FILTER.create());
        add(applyFilterButton);

        applyFilterButton.addClickListener(event -> {
            Response<List<ProductInfo>> products = session.filterByStoreRank(minStoreRankField.getValue(), maxStoreRankField.getValue());
            productGrid.setItems();
            productGrid.setItems(products.getData());
        });

        storeRankRange = new HorizontalLayout();
        storeRankRange.add(minStoreRankField, maxStoreRankField, applyFilterButton);
        storeRank = new FormLayout();
        storeRank.addFormItem(storeRankRange, "Store Rank");
    }

    public void buildClearButton() {
        Button clearButton = new Button("Clear");
        clearButton.setIcon(VaadinIcon.CLOSE_SMALL.create());
        add(clearButton);

        clearButton.addClickListener(event -> {
            minPriceField.clear();
            maxPriceField.clear();
            categoryField.clear();
            minProductRankField.clear();
            maxProductRankField.clear();
            minStoreRankField.clear();
            maxStoreRankField.clear();

            Response<List<ProductInfo>> products = session.search(searchTerm);
            productGrid.setItems();
            productGrid.setItems(products.getData());
        });
    }

}
