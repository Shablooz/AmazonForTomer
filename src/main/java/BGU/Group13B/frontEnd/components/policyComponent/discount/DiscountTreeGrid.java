package BGU.Group13B.frontEnd.components.policyComponent.discount;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;

import java.util.stream.Stream;

public class DiscountTreeGrid extends TreeGrid<DiscountNodeEntity>{
    private final TreeData<DiscountNodeEntity> data = new TreeData<>();

    public DiscountTreeGrid(){

        this.setDataProvider(new TreeDataProvider<>(data));
        this.addHierarchyColumn(DiscountNodeEntity::toString);
        //style
        setAllRowsVisible(true);
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }
    public void addToRoot(DiscountOperatorNodeEntity operatorRoot, LeafDiscountNodeEntity newNode){
        if(data.getRootItems().size() != 1)
            throw new RuntimeException("there should be only one root, system error");
        DiscountNodeEntity preRoot = data.getRootItems().get(0); //should only be one

        data.addItem(null, operatorRoot);
        data.setParent(preRoot, operatorRoot);
        data.addItem(operatorRoot, newNode);
        expandRecursively(Stream.of(operatorRoot), 2);

        refreshAll();
    }
    public void setRoot(DiscountNodeEntity node){
        data.clear();
        data.addItem(null, node);
        refreshAll();
    }

    public void reset(){
        data.clear();
        refreshAll();
    }

    private void refreshAll() {
        getDataProvider().refreshAll();
    }

    public DiscountNodeEntity getRoot(){
        return data.getRootItems().get(0);
    }


}
