package nub.wi1helm.guis.item.kits;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import nub.wi1helm.player.TagKit;
import nub.wi1helm.player.TagPlayer;
import nub.wi1helm.template.inventory.TemplateInventoryEvent;
import nub.wi1helm.template.inventory.TemplateItem;

public class AssassinItem extends TemplateItem {
    public AssassinItem() {
        super(Material.IRON_SWORD);
    }

    @Override
    protected void initialize() {
        setName(Component.text("Assassin [Click]"));
    }

    @Override
    protected void personalize(Player player) {

    }

    @Override
    public void onUse(TemplateInventoryEvent templateInventoryEvent) {
        final TagPlayer tagPlayer = (TagPlayer) templateInventoryEvent.getPlayer();
        tagPlayer.setKit(TagKit.ASSASSIN);
        tagPlayer.sendMessage("Selected " + TagKit.ASSASSIN.name());
        tagPlayer.closeInventory();
    }

    @Override
    public void onDrop(TemplateInventoryEvent templateInventoryEvent) {

    }
}
