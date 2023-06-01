package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.policyComponent.ConditionTreeGrid;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;


@Route("addDiscount")
public class AddDiscountView extends VerticalLayout implements HasUrlParameter<Integer>, ResponseHandler {
    private int storeId;
    private final Session session;
    private final Button addDiscountBtn = new Button("Add Discount");
    private final Button resetBtn = new Button("reset");

    public AddDiscountView(Session session) {
        this.session = session;
    }

    private void start() {
        ConditionTreeGrid conditionTreeGrid = new ConditionTreeGrid(session, storeId);
        resetBtn.addClickListener(e -> {
            conditionTreeGrid.reset();
        });
        addDiscountBtn.addClickListener(e -> {
            int conditionId = conditionTreeGrid.confirmCondition();
            if (conditionId != -1) {
                notifySuccess("noder neder " + conditionId);
            }

        });
        add(conditionTreeGrid, new HorizontalLayout(resetBtn, addDiscountBtn));
    }

    @Override
    public void setParameter(BeforeEvent event, Integer storeId) {
        this.storeId = storeId;
        start();
    }
}
