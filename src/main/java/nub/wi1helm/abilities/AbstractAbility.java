package nub.wi1helm.abilities;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public abstract class AbstractAbility {

    private ItemStack item;
    private int slot;
    private float cooldown;

    public AbstractAbility(ItemStack item, int slot, float cooldown) {
        this.item = item;
        this.slot = slot;
        this.cooldown = cooldown;
    }

    public abstract void execute(Player player);


    public static AbstractAbility empty() {
        return new AbstractAbility(ItemStack.AIR, -1, 0F) {

            @Override
            public void execute(Player player) {

            }
        };
    }

}
