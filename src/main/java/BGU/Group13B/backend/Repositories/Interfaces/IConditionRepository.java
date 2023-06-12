package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.Condition;

import java.time.LocalDateTime;

public interface IConditionRepository {

    int addORCondition(int condition1, int condition2);
    int addANDCondition(int condition1, int condition2);
    int addXORCondition(int condition1, int condition2);
    int addIMPLYCondition(int condition1, int condition2);

    int addStorePriceCondition(double lowerBound, double upperBound);
    int addStorePriceCondition(double lowerBound);

    int addStoreQuantityCondition(int lowerBound, int upperBound);
    int addStoreQuantityCondition(int lowerBound);

    int addCategoryPriceCondition(String category, double lowerBound, double upperBound);
    int addCategoryPriceCondition(String category, double lowerBound);

    int addCategoryQuantityCondition(String category, int lowerBound, int upperBound);
    int addCategoryQuantityCondition(String category, int lowerBound);

    int addDateCondition(LocalDateTime lowerBound, LocalDateTime upperBound);
    int addDateCondition(LocalDateTime lowerBound);

    int addProductPriceCondition(int productId, double lowerBound, double upperBound);
    int addProductPriceCondition(int productId, double lowerBound);

    int addProductQuantityCondition(int productId, int lowerBound, int upperBound);
    int addProductQuantityCondition(int productId, int lowerBound);

    int addTimeCondition(LocalDateTime lowerBound, LocalDateTime upperBound);
    int addTimeCondition(LocalDateTime lowerBound);

    int addUserAgeCondition(int lowerBound, int upperBound);
    int addUserAgeCondition(int lowerBound);

    Condition getCondition(int conditionId);
    void removeCondition(int conditionId);
    void reset();

    void setSaveMode(boolean saveMode);
}
