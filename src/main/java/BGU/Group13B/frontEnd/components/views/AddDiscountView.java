package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.components.policyComponent.ConditionTreeGrid;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;


@Route("addDiscount")
public class AddDiscountView extends VerticalLayout {

    private final ConditionTreeGrid conditionTreeGrid = new ConditionTreeGrid();

    public AddDiscountView(Session session) {
        add(conditionTreeGrid);
    }
}
