package nub.wi1helm.player;

import net.minestom.server.entity.GameMode;
import net.minestom.server.inventory.PlayerInventory;
import nub.wi1helm.guis.item.KitSelector;
import nub.wi1helm.guis.item.StartItem;

public enum TagPlayerState {
    CORRUPTED,
    NORMAL,
    LOBBY,
    SPECTATOR;


    public void applyState(TagPlayer tagPlayer) {

        PlayerInventory inventory = tagPlayer.getInventory();
        inventory.clear();

        tagPlayer.setAutoViewable(true);
        tagPlayer.setGameMode(GameMode.ADVENTURE);

        switch (this) {
            case CORRUPTED -> {
                tagPlayer.skin.applySkin(inventory);
                tagPlayer.weapon.applyWeapon(inventory);
            }
            case NORMAL -> {
                tagPlayer.kit.applyKit(inventory);
            }
            case LOBBY -> {
                inventory.setItemStack(8, new KitSelector().constructItemStack(tagPlayer));
                inventory.setItemStack(4, new StartItem().constructItemStack(tagPlayer));
            }
            default -> {
                tagPlayer.setGameMode(GameMode.SPECTATOR);
                tagPlayer.setAutoViewable(false);
            }
        }
    }

}

