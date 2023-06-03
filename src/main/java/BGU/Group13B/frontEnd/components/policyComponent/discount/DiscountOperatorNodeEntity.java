package BGU.Group13B.frontEnd.components.policyComponent.discount;

import BGU.Group13B.frontEnd.components.views.ManageDiscountsView;

public class DiscountOperatorNodeEntity implements DiscountNodeEntity{

    private ManageDiscountsView.Operator operator;

    public DiscountOperatorNodeEntity(ManageDiscountsView.Operator operator) {
        this.operator = operator;
    }
    public String toString() {
        return operator.toString();
    }

    public ManageDiscountsView.Operator getOperator() {
        return operator;
    }
}
