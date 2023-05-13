package BGU.Group13B.frontEnd.components.views.Searcher;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;

public class PriceRangeView extends CustomField<PriceRange> {
    private NumberField minPrice;
    private NumberField maxPrice;

    public PriceRangeView() {
        this.minPrice = new NumberField();
        minPrice.setPlaceholder("min price");
        this.maxPrice = new NumberField();
        maxPrice.setPlaceholder("max price");
        add(minPrice, new Text(" – "), maxPrice);
    }

    @Override
    protected PriceRange generateModelValue() {
        return new PriceRange(minPrice.getValue(), maxPrice.getValue());
    }

    @Override
    protected void setPresentationValue(PriceRange priceRange) {
        minPrice.setValue(priceRange.getMinPrice());
        maxPrice.setValue(priceRange.getMaxPrice());

    }

}
