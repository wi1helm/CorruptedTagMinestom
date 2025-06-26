package nub.wi1helm.player;

import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagPlayer extends net.minestom.server.entity.Player {

    private static final Logger log = LoggerFactory.getLogger(TagPlayer.class);
    TagPlayerState state = TagPlayerState.LOBBY;
    TagKit kit = TagKit.NONE;
    TagSkin skin = TagSkin.NONE;
    TagWeapon weapon = TagWeapon.DEFAULT;
    public TagPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
    }


    public void join() {
        sendMessage("Joines the game:");
        log.debug(state.name());
        //TODO Add game check for spectators

        state.applyState(this);
    }


    public void setKit(TagKit kit) {
        this.kit = kit;
    }

    public void setState(TagPlayerState state) {
        this.state = state;
        state.applyState(this);
    }

    public void setSkin(TagSkin skin) {
        this.skin = skin;
    }

    public void setWeapon(TagWeapon weapon) {
        this.weapon = weapon;
    }
}
