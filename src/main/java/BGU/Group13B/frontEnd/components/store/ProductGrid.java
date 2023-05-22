package BGU.Group13B.frontEnd.components.store;

import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ProductGrid extends Grid<ProductInfo> implements ResponseHandler {

    private List<ProductInfo> products;

    public ProductGrid(Collection<ProductInfo> products){
        setItems(products);
        this.products = new LinkedList<>(products);
        addColumn(ProductInfo::name).setHeader("Name").setWidth("200px").setFlexGrow(0);
        addColumn(ProductInfo::price).setHeader("Price");
        addColumn(ProductInfo::stockQuantity).setHeader("Stock");
        addColumn(ProductInfo::getRoundedScore).setHeader("Rating");

        //styling
        getStyle().set("margin", "10px");
        setAllRowsVisible(true);
        setMaxWidth("1000px");
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        //actions
        addItemDoubleClickListener(e -> {
            ProductInfo productInfo = e.getItem();
            navigate("product/" + productInfo.productId() + "/" + productInfo.storeId());
        });
    }

    public void addProduct(ProductInfo product){
        this.products.add(product);
        setItems(this.products);
    }

}
