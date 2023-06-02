package BGU.Group13B.service.info;

import BGU.Group13B.backend.storePackage.newDiscoutns.DiscountInfo;
import BGU.Group13B.frontEnd.components.views.ManageDiscountsView;

import java.util.List;

public record DiscountAccumulationTreeInfo(List<ManageDiscountsView.Operator> operators, List<DiscountInfo> discounts) {
}
