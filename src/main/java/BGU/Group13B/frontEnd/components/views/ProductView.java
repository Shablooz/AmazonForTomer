package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.service.Session;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@PageTitle("product")
@Route(value = "product/:productId/:storeId", layout = MainLayout.class)
public class ProductView extends VerticalLayout implements HasUrlParameter<Map<String, Integer>> {

    private int productId;
    private int storeId;

    @Autowired
    public ProductView(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setParameter(BeforeEvent event, Map<String, Integer> parameters) {
        if (parameters.containsKey("productId") && parameters.containsKey("storeId")) {
            this.productId = parameters.get("productId");
            this.storeId = parameters.get("storeId");
        }
    }
}
