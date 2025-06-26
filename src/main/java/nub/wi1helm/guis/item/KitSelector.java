package nub.wi1helm.guis.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import nub.wi1helm.guis.inventory.KitSelectorMenu;
import nub.wi1helm.template.inventory.TemplateInventoryEvent;
import nub.wi1helm.template.inventory.TemplateItem;

public class KitSelector extends TemplateItem {
    public KitSelector() {
        super(Material.COMPASS);
    }

    @Override
    protected void initialize() {
        setName(Component.text("Kit Selector"));
    }

    @Override
    protected void personalize(Player player) {

    }

    @Override
    public void onUse(TemplateInventoryEvent templateInventoryEvent) {
        templateInventoryEvent.getPlayer().openInventory(new KitSelectorMenu().constructInventory(templateInventoryEvent.getPlayer()));
    }

    @Override
    public void onDrop(TemplateInventoryEvent templateInventoryEvent) {

    }
}
