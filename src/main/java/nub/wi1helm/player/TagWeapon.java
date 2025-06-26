package nub.wi1helm.player;

import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public enum TagWeapon {
    NONE(ItemStack.AIR),
    DEFAULT(ItemStack.builder(Material.IRON_SWORD).build());


    private final ItemStack weapon;

    TagWeapon(ItemStack weapon) {
        this.weapon = weapon;
    }

    public void applyWeapon(PlayerInventory inventory) {
        inventory.setItemStack(0, getWeapon());
    }

    public ItemStack getWeapon() {
        return weapon;
    }
}
