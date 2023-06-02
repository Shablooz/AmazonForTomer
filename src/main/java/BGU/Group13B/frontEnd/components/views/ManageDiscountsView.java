package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.storePackage.newDiscoutns.DiscountInfo;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.frontEnd.components.policyComponent.discount.DiscountOperatorNodeEntity;
import BGU.Group13B.frontEnd.components.policyComponent.discount.DiscountTreeGrid;
import BGU.Group13B.frontEnd.components.policyComponent.discount.LeafDiscountNodeEntity;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.List;


@Route(value = "manageDiscounts", layout = MainLayout.class)
public class ManageDiscountsView extends VerticalLayout implements HasUrlParameter<Integer>, ResponseHandler {

    public enum Operator {
        ADD,
        MAX,
        XOR
    }

    private Button plusBtn;
    private Button reset;
    private Button addNewDiscount;
    private int storeId;
    private int userId;
    private final Session session;
    private final Dialog addDiscountDialog = new Dialog();
    private ComboBox<Operator> operator;
    private ComboBox<DiscountInfo> discount;
    private DiscountTreeGrid discountTreeGrid;
    private boolean hasRoot = false;
    public ManageDiscountsView(Session session) {
        this.session = session;

    }
    private void start() {
        userId = SessionToIdMapper.getInstance().getCurrentSessionId();
        plusBtn = new Button("add discount to root", new Icon(VaadinIcon.PLUS));
        discountTreeGrid = new DiscountTreeGrid();

        //actions
        plusBtn.addClickListener(e -> {
            updateDialog();
            addDiscountDialog.open();


        });
        reset = new Button("reset discount tree");
        reset.addClickListener(e -> {
            //session.resetStoreDiscounts(storeId, userId);
            //var res = handleResponse(session.discount)
            var res = handleResponse(session.deleteStoreAccumulationTree(storeId, userId));
            if(res != null){
                discountTreeGrid.reset();
                hasRoot = false;
            }

        });

        addNewDiscount = new Button("add new discount", new Icon(VaadinIcon.PLUS));
        addNewDiscount.addClickListener(e->navigate("addDiscount/" + storeId));

        setUpDialog();
        add(discountTreeGrid, plusBtn, reset, addNewDiscount);


    }

    private void updateDiscountTreeGrid() {
        discountTreeGrid.reset();

        var res = handleResponse(session.getDiscountAccumulationTree(storeId, userId), "store/" + storeId);
        List<Operator> operators = res.operators();
        List<DiscountInfo> discounts = res.discounts();

        if(discounts.isEmpty()){
            this.hasRoot = false;
            return;
        }

        this.hasRoot = true;

        discountTreeGrid.setRoot(new LeafDiscountNodeEntity(discounts.get(0)));
        for (int i = 1; i < discounts.size(); i++) {
            discountTreeGrid.addToRoot(new DiscountOperatorNodeEntity(operators.get(i-1)),
                    new LeafDiscountNodeEntity(discounts.get(i)));
        }
    }

    private void setUpDialog() {
        Button confirmBtn = new Button("Confirm");

        operator = new ComboBox<>();
        operator.setItems(Operator.values());
        operator.setItemLabelGenerator(Operator::name);
        operator.setVisible(true);

        discount = new ComboBox<>("discount");
        updateDialog();
        discount.setItemLabelGenerator(DiscountInfo::stringRepresentation);
        discount.setVisible(true);

        addDiscountDialog.setCloseOnEsc(true);
        addDiscountDialog.setCloseOnOutsideClick(true);

        confirmBtn.addClickListener(e -> {
            if(operator.getValue() == null || discount.getValue() == null){
                return;
            }
            int discountId = discount.getValue().discountId();
            if(!hasRoot) {
                //todo add to root
                var res = handleResponse(session.addDiscountAsRoot(storeId, userId, discountId));
                if(res == null){
                    addDiscountDialog.close();
                    return;
                }

                hasRoot = true;
                discountTreeGrid.setRoot(new LeafDiscountNodeEntity(discountId, discount.getValue().stringRepresentation()));
                addDiscountDialog.close();
                return;
            }
            //todo add to root by specific operator
            switch (operator.getValue()) {
                case ADD -> {
                    var res = handleResponse(session.addDiscountToADDRoot(storeId, userId, discountId));
                    if(res == null){
                        addDiscountDialog.close();
                        return;
                    }
                    discountTreeGrid.addToRoot(new DiscountOperatorNodeEntity(Operator.ADD), new LeafDiscountNodeEntity(discountId, discount.getValue().stringRepresentation()));

                }
                case MAX -> {
                    var res = handleResponse(session.addDiscountToMAXRoot(storeId, userId, discountId));
                    if(res == null){
                        addDiscountDialog.close();
                        return;
                    }
                    discountTreeGrid.addToRoot(new DiscountOperatorNodeEntity(Operator.MAX), new LeafDiscountNodeEntity(discountId, discount.getValue().stringRepresentation()));
                }
                case XOR -> {
                    var res = handleResponse(session.addDiscountToXORRoot(storeId, userId, discountId));
                    if(res == null){
                        addDiscountDialog.close();
                        return;
                    }
                    discountTreeGrid.addToRoot(new DiscountOperatorNodeEntity(Operator.XOR), new LeafDiscountNodeEntity(discountId, discount.getValue().stringRepresentation()));
                }
            }
            addDiscountDialog.close();
        });

        addDiscountDialog.add(new VerticalLayout(operator, discount, confirmBtn));




    }
    @Override
    public void setParameter(BeforeEvent event, Integer storeId) {
        this.storeId = storeId;
        start();
    }
}
