package nub.wi1helm.guis.inventory;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import nub.wi1helm.guis.item.kits.AssassinItem;
import nub.wi1helm.template.inventory.TemplateInventory;

public class KitSelectorMenu extends TemplateInventory {

    public KitSelectorMenu() {
        super(Component.text("Kit Selector"), InventoryType.CHEST_5_ROW);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void personalize(Player player) {
        setItem(13, new AssassinItem());
    }
}
