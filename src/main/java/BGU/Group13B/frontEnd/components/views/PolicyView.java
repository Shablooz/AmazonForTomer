package BGU.Group13B.frontEnd.components.views;

import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import com.vaadin.flow.router.BeforeEnterObserver;

import java.util.List;

public interface PolicyView extends ResponseHandler, BeforeEnterObserver {
    default boolean hasAccess(Session session, int storeId){
        int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
        List<WorkerCard> res = handleResponse(session.getStoreWorkersInfo(1 /*mafhhhiiddd*/, storeId));
        if(res == null){
            return false;
        }
        return res.stream().anyMatch(wc -> wc.userId() == userId && wc.userPermissions().contains(UserPermissions.IndividualPermission.POLICIES));
    }
}
