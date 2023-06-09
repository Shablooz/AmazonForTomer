package BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves;


import BGU.Group13B.service.Response;
import BGU.Group13B.service.Session;

public interface LeafConditionEntity{
    Response<Integer> addToBackend(Session session, int storeId, int userId);

}
