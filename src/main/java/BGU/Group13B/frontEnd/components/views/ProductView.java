package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.ProductInfo;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@PageTitle("product")
@Route(value = "product", layout = MainLayout.class)
public class ProductView extends VerticalLayout implements HasUrlParameter<String> {
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int productId;
    private int storeId;
    private Session session;
    private TextField seller;
    private TextField name;
    private TextField category;
    private NumberField price;
    private NumberField stockQuantity;
    private TextField description;
    private NumberField score;

    @Autowired
    public ProductView(Session session) {
        this.session=session;
        price = new NumberField();
        stockQuantity = new NumberField();
        score = new NumberField();
    }

    @Override
    public void setParameter(BeforeEvent event, String parameters) {
        String[] params = parameters.split("/");
        productId = Integer.parseInt(params[0]);
        storeId = Integer.parseInt(params[1]);
        start();
    }
    private void start(){
        ProductInfo info = session.getStoreProductInfo(userId,storeId,productId).getData(); //TODO: CHECK ON ERRORS
        seller = new TextField(info.seller());
        name = new TextField(info.name());
        category = new TextField(info.category());
        price.setValue(info.price());
        stockQuantity.setValue((double) info.stockQuantity());
        description = new TextField(info.description());
        score.setValue((double) info.score());
    }
}
