package BGU.Group13B.frontEnd.components.policyComponent;

import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.ConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.LogicalConditions.LogicalConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.PlusConditionEntity;
import BGU.Group13B.frontEnd.components.policyComponent.conditionEntities.leaves.*;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;


public class ConditionTreeGrid extends TreeGrid<ConditionEntity> {
    private final TreeData<ConditionEntity> conditionsData = new TreeData<>();

    public ConditionTreeGrid() {
        this.setDataProvider(new TreeDataProvider<>(conditionsData));
        this.addComponentHierarchyColumn(conditionEntity -> {
            VerticalLayout layout = new VerticalLayout();
            Button plusBtn = new Button(new Icon(VaadinIcon.PLUS));
            Div text = new Div(new Text(conditionEntity.toString()));
            layout.add(plusBtn, text);
            if (conditionEntity instanceof PlusConditionEntity) {
                //show only the plus btn
                text.setVisible(false);
                plusBtn.setVisible(true);
            } else {
                //show only the text field
                text.setVisible(true);
                plusBtn.setVisible(false);
            }

            //style
            plusBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
            plusBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

            //actions
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setTarget(plusBtn);
            contextMenu.setOpenOnClick(true);

            contextMenu.addItem("AND", e -> {
                conditionsData.removeItem(conditionEntity);
                addAND(conditionEntity.getParent());

            });
            contextMenu.addItem("OR", e -> {
                conditionsData.removeItem(conditionEntity);
                addOR(conditionEntity.getParent());

            });
            contextMenu.addItem("XOR", e -> {
                conditionsData.removeItem(conditionEntity);
                addXOR(conditionEntity.getParent());

            });
            contextMenu.addItem("IMPLY", e -> {
                conditionsData.removeItem(conditionEntity);//magic
                addIMPLY(conditionEntity.getParent());

            });

            //add divider line to context menu
            contextMenu.add(new Hr()); //nodered
            //add leaves
            contextMenu.addItem("Age", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new UserAgeConditionEntity(conditionEntity.getParent())).open();

            });
            contextMenu.addItem("Time", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new TimeConditionEntity(conditionEntity.getParent())).open();

            });
            contextMenu.addItem("Date", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new DateConditionEntity(conditionEntity.getParent())).open();

            });
            contextMenu.addItem("Total price", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new StorePriceConditionEntity(conditionEntity.getParent())).open();

            });
            contextMenu.addItem("Category price", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new CategoryPriceConditionEntity(conditionEntity.getParent())).open();

            });
            contextMenu.addItem("Product price", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new ProductPriceConditionEntity(conditionEntity.getParent())).open();

            });
            contextMenu.addItem("Total quantity", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new StoreQuantityConditionEntity(conditionEntity.getParent())).open();

            });
            contextMenu.addItem("Category quantity", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new CategoryQuantityConditionEntity(conditionEntity.getParent())).open();

            });
            contextMenu.addItem("Product quantity", e -> {
                conditionsData.removeItem(conditionEntity);
                new ConditionDialog(this, new ProductQuantityConditionEntity(conditionEntity.getParent())).open();

            });

            layout.setSpacing(false);
            layout.setPadding(false);
            layout.setMargin(false);

            return layout;


        });

        //add plus at the start
        conditionsData.addItem(null, new PlusConditionEntity(null));

        //style
        setAllRowsVisible(true);
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);



    }

    public void addLogicalCondition(LogicalConditionEntity parent, LogicalConditionEntity.LogicalOperator operator) {
        //no dialog
        LogicalConditionEntity op = new LogicalConditionEntity(parent, operator);

        conditionsData.addItem(parent, op);
        conditionsData.addItem(op, new PlusConditionEntity(op));
        conditionsData.addItem(op, new PlusConditionEntity(op));
        this.getDataProvider().refreshAll();  // Refresh the UI

    }

    public void addAND(LogicalConditionEntity parent) {
        addLogicalCondition(parent, LogicalConditionEntity.LogicalOperator.AND);
    }

    public void addOR(LogicalConditionEntity parent) {
        addLogicalCondition(parent, LogicalConditionEntity.LogicalOperator.OR);
    }

    public void addXOR(LogicalConditionEntity parent) {
        addLogicalCondition(parent, LogicalConditionEntity.LogicalOperator.XOR);
    }

    public void addIMPLY(LogicalConditionEntity parent) {
        addLogicalCondition(parent, LogicalConditionEntity.LogicalOperator.IMPLIES);
    }

    public void addLeafCondition(ConditionEntity conditionEntityLeaf) {
        conditionsData.addItem(conditionEntityLeaf.getParent(), conditionEntityLeaf);
        this.getDataProvider().refreshAll();  // Refresh the UI
    }

    private void updateItemsGrid() {
    }


}
