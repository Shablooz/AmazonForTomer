package BGU.Group13B.frontEnd.components.store.products;

import BGU.Group13B.backend.storePackage.Product;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.gridpro.GridPro;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ProductGrid extends GridPro<ProductInfo> implements ResponseHandler {

    private List<ProductInfo> products;
    private StoreProductsLayout storeProductsLayout;

    public ProductGrid(StoreProductsLayout storeProductsLayout, Collection<ProductInfo> products, WorkerCard workerCard){
        setItems(products);
        this.products = new LinkedList<>(products);
        addColumn(ProductInfo::name).setHeader("Name").setWidth("200px").setFlexGrow(0);
        if(workerCard.userPermissions().contains(UserPermissions.IndividualPermission.STOCK)){
            addEditColumn(ProductInfo::price)
                    .text((item, newValue) ->
                        storeProductsLayout.editProductPrice(item, Double.parseDouble(newValue)))
                    .setHeader("Price");
            addEditColumn(ProductInfo::stockQuantity)
                    .text((item, newValue) ->
                        storeProductsLayout.editProductStock(item, Integer.parseInt(newValue)))
                    .setHeader("Stock");
        }
        else{
            addColumn(ProductInfo::price).setHeader("Price");
        }
        addColumn(ProductInfo::getRoundedScore).setHeader("Rating");
        setEditOnClick(true);

        GridContextMenu<ProductInfo> contextMenu = this.addContextMenu();
        contextMenu.addItem("View Product", e -> {
            Optional<ProductInfo> productInfo = e.getItem();
            if(productInfo.isPresent()){
                ProductInfo p = productInfo.get();
                storeProductsLayout.navigate("product/" + p.productId() + "/" + p.storeId());
            }
        });


        if(workerCard.userPermissions().contains(UserPermissions.IndividualPermission.STOCK)){
            contextMenu.addItem("Remove Product", e -> {
                if(!workerCard.userPermissions().contains(UserPermissions.IndividualPermission.STOCK)){
                    notifyWarning("You don't have permission to remove products");
                }
                else{
                    Optional<ProductInfo> productInfo = e.getItem();
                    if(productInfo.isPresent()){
                        ProductInfo p = productInfo.get();
                        storeProductsLayout.removeProduct(p);
                    }
                }
            });
        }

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

    public void editProductPrice(ProductInfo productInfo, double newPrice){
        int index = products.indexOf(productInfo);
        products.remove(productInfo);
        ProductInfo newProductInfo = new ProductInfo(productInfo.productId(), productInfo.storeId(), productInfo.seller(), productInfo.name(), productInfo.category(), newPrice, productInfo.stockQuantity(), productInfo.description(), productInfo.score());
        products.add(index, newProductInfo);
        setItems(products);
    }

    public void editProductStock(ProductInfo item, int stock) {
        int index = products.indexOf(item);
        products.remove(item);
        ProductInfo newProductInfo = new ProductInfo(item.productId(), item.storeId(), item.seller(), item.name(), item.category(), item.price(), stock, item.description(), item.score());
        products.add(index, newProductInfo);
        setItems(products);
    }

    public void removeProduct(ProductInfo p) {
        products.remove(p);
        setItems(products);
    }
}
