package nub.wi1helm.player;

import net.minestom.server.inventory.PlayerInventory;
import nub.wi1helm.abilities.AbstractAbility;

import java.util.List;

public enum TagKit {
    NONE(TagWeapon.NONE, TagSkin.NONE, AbstractAbility.empty()),
    ASSASSIN(TagWeapon.NONE, TagSkin.NONE, AbstractAbility.empty());

    private final List<AbstractAbility> abilities;
    private final TagWeapon weapon;
    private final TagSkin skin;

    TagKit(TagWeapon weapon, TagSkin skin, AbstractAbility... abilities) {
        this.weapon = weapon; this.skin = skin; this.abilities = List.of(abilities);
    }

    public void applyKit(PlayerInventory inventory) {

    }

}
