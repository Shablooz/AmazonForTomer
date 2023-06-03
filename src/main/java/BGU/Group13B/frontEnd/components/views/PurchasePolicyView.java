package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.policyComponent.ConditionTreeGrid;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Purchase Policy")
@Route(value = "managePurchasePolicy", layout = MainLayout.class)
public class PurchasePolicyView extends VerticalLayout implements HasUrlParameter<Integer>, ResponseHandler {
    private int storeId;
    private final Session session;
    private ConditionTreeGrid conditionTreeGrid;
    private HorizontalLayout controlButtons;
    private Button resetBtn;
    private Button confirmBtn;
    private Button backToStoreBtn;
    private final int userId;

    public PurchasePolicyView(Session session) {
        this.session = session;
        this.userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    }

    private void start() {
        conditionTreeGrid = new ConditionTreeGrid(session, storeId);
        controlButtons = new HorizontalLayout();
        confirmBtn = new Button("confirm");
        confirmBtnListener();
        resetBtn = new Button("reset");
        resetBtnListener();
        backToStoreBtn = new Button("back to store");
        backToStoreBtnListener();
        controlButtons.add(resetBtn, confirmBtn, backToStoreBtn);
        controlButtons.getStyle().set("margin-top", "auto");
        add(conditionTreeGrid, controlButtons);

        setupData();
    }

    private void setupData(){
        var res = handleResponse(session.getStorePurchasePolicy(storeId, userId));
        if (res != null) {
            conditionTreeGrid.setConditionsData(res);
        }
    }

    private void backToStoreBtnListener() {
        backToStoreBtn.addClickListener(buttonClickEvent -> navigate("store/" + storeId));
    }

    private void resetBtnListener() {
        resetBtn.addClickListener(buttonClickEvent -> conditionTreeGrid.reset());
    }

    private void confirmBtnListener() {
        confirmBtn.addClickListener(buttonClickEvent -> {
            int conditionId = conditionTreeGrid.confirmCondition();
            if (conditionId == -1) {
                return;
            }
            var res = handleResponse(session.setPurchasePolicyCondition(storeId, userId, conditionId));
            if (res != null) {
                notifySuccess("Purchase Policy was set successfully");
                navigate("store/" + storeId);
            }
        });
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer storeId) {
        this.storeId = storeId;
        start();
    }
}
