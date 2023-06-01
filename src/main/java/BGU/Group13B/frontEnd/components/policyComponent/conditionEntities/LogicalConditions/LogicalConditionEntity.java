package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;

import java.util.List;

public class LogicalConditionEntity extends ConditionEntity {
    public enum LogicalOperator{
        AND,
        OR,
        XOR,
        IMPLIES
    }

    private final LogicalOperator operator;

    public LogicalConditionEntity(LogicalConditionEntity parent, LogicalOperator operator){
        super(parent);
        this.operator = operator;
    }


    public String toString(){
        return operator.toString();
    }

    public List<LogicalOperator> getAllLogicalOperators(){
        return List.of(LogicalOperator.values());
    }
    public LogicalOperator getOperator() {
        return operator;
    }

}
