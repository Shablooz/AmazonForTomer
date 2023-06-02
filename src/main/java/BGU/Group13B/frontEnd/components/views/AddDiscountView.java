package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.policyComponent.AddDiscountComponent;
import BGU.Group13B.frontEnd.components.policyComponent.ConditionTreeGrid;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;


@Route(value = "addDiscount", layout = MainLayout.class)
public class AddDiscountView extends VerticalLayout implements HasUrlParameter<Integer>, ResponseHandler {

    private int storeId;
    private final Session session;
    private final Button addDiscountBtn = new Button("Add Discount");
    private final Button showConditionBtn = new Button("Add Condition");
    private final Button hideConditionBtn = new Button("Remove Condition");
    private final Button resetBtn = new Button("Reset condition");
    private boolean hasCondition;
    private ConditionTreeGrid conditionTreeGrid;
    private AddDiscountComponent addDiscountComponent;

    public AddDiscountView(Session session) {
        this.session = session;

        // Adjust the vertical layout properties
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private void start() {
        conditionTreeGrid = new ConditionTreeGrid(session, storeId);
        addDiscountComponent = new AddDiscountComponent(session, storeId);
        conditionTreeGrid.setVisible(false);
        hideConditionBtn.setVisible(false);
        showConditionBtn.setVisible(true);
        hasCondition = false;

        // Size the ConditionTreeGrid and AddDiscountComponent
        conditionTreeGrid.setWidth("50%");
        HorizontalLayout discount = new HorizontalLayout();
        discount.add(addDiscountComponent);
        addDiscountComponent.setWidth("auto");
        discount.setJustifyContentMode(JustifyContentMode.CENTER);

        // Button actions
        showConditionBtn.addClickListener(e -> {
            conditionTreeGrid.setVisible(true);
            showConditionBtn.setVisible(false);
            hideConditionBtn.setVisible(true);
            hasCondition = true;
        });

        hideConditionBtn.addClickListener(e -> {
            conditionTreeGrid.setVisible(false);
            showConditionBtn.setVisible(true);
            hideConditionBtn.setVisible(false);
            hasCondition = false;
        });

        resetBtn.addClickListener(e -> conditionTreeGrid.reset());

        addDiscountBtn.addClickListener(e -> {
            if (hasCondition) {
                int conditionId = conditionTreeGrid.confirmCondition();
                if (conditionId != -1 && addDiscountComponent.addDiscount(conditionId)) {
                    navigate("manageDiscounts/" + storeId);
                }
            } else if(addDiscountComponent.addDiscount()){
                navigate("manageDiscounts/" + storeId);
            }
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout(showConditionBtn, hideConditionBtn, resetBtn, addDiscountBtn);
        buttonsLayout.setWidth("100%");
        buttonsLayout.setPadding(true);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Main layout
        FlexLayout mainLayout = new FlexLayout(conditionTreeGrid, addDiscountComponent);
        mainLayout.setSizeFull();

        add(mainLayout, buttonsLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, Integer storeId) {
        this.storeId = storeId;
        start();
    }
}


