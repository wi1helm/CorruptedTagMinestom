package nub.wi1helm.player;

import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;

public enum TagSkin {
    NONE(ItemStack.AIR, ItemStack.AIR, ItemStack.AIR, ItemStack.AIR);

    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;

    TagSkin(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.helmet = helmet; this.chestplate = chestplate; this.leggings = leggings; this.boots = boots;
    }


    public void applySkin(PlayerInventory inventory) {
        inventory.setEquipment(EquipmentSlot.HELMET, (byte) 0, getHelmet());
        inventory.setEquipment(EquipmentSlot.CHESTPLATE, (byte) 0, getChestplate());
        inventory.setEquipment(EquipmentSlot.LEGGINGS, (byte) 0, getLeggings());
        inventory.setEquipment(EquipmentSlot.BOOTS, (byte) 0, getBoots());
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }
}
