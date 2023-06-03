package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;

public abstract class ConditionEntity{
    private LogicalConditionEntity parent;

    protected ConditionEntity(LogicalConditionEntity parent){
        this.parent = parent;
    }

    public LogicalConditionEntity getParent() {
        return parent;
    }

}
