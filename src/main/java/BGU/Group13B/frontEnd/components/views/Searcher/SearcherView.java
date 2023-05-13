package BGU.Group13B.frontEnd.components.views.Searcher;

import BGU.Group13B.frontEnd.components.views.MainLayout;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@PageTitle("Search results")
@Route(value = "Search results", layout = MainLayout.class)
public class SearcherView extends VerticalLayout {
    private String searchTerm;

    @Autowired
    public SearcherView(Session session) {
        Map<String, String[]> params = VaadinService.getCurrentRequest().getParameterMap();
        if (params.containsKey("searchTerm")) {
            searchTerm = Arrays.toString(params.get("searchTerm"));
        } else {
            searchTerm = "";
        }
        Response<List<ProductInfo>> productsInfo= session.search(searchTerm);
        Grid<ProductInfo> productGrid = new Grid<>();
        productGrid.setItems(productsInfo.getData()); // products is a List<Product> containing the products to be displayed
        productGrid.addColumn(ProductInfo::name).setHeader("Name");
        productGrid.addColumn(ProductInfo::category).setHeader("Category");
        productGrid.addColumn(ProductInfo::description).setHeader("Description");
        productGrid.addColumn(ProductInfo::price).setHeader("Price");
        productGrid.addColumn(ProductInfo::score).setHeader("Score");
        productGrid.addColumn(ProductInfo::seller).setHeader("Seller");

        productGrid.addItemClickListener(event -> {
            ProductInfo clickedProduct = event.getItem();
            //navigate to product page
           // getUI().ifPresent(ui -> ui.navigate("product/" + clickedProduct.productId() + "/" + clickedProduct.storeId()));
        });

        add(productGrid);
    }


}
