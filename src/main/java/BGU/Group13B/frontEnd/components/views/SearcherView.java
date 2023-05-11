package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("searcher")
@Route(value = "searcher", layout = MainLayout.class)
public class SearcherView extends VerticalLayout implements HasUrlParameter<String> {
    private String searchTerm;

    @Autowired
    public SearcherView(Session session) {
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
            getUI().ifPresent(ui -> ui.navigate("product/" + clickedProduct.productId() + "/" + clickedProduct.storeId()));
        });

        add(productGrid);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
