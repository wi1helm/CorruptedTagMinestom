package nub.wi1helm.event;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import nub.wi1helm.Main;
import nub.wi1helm.player.TagPlayer;

public class PlayerEventHandler {

    private final EventNode<PlayerEvent> node = EventNode.type("player", EventFilter.PLAYER);

    private static PlayerEventHandler instance;

    private PlayerEventHandler(EventNode<Event> node) {
        node.addChild(this.node);

        join();
    }

    public static void init(EventNode<Event> node) {
        if (instance == null) instance = new PlayerEventHandler(node);
    }

    private void join(){
        node.addListener(PlayerSpawnEvent.class, event -> {
            event.getPlayer().sendMessage("Jeö");
            final TagPlayer tagPlayer = (TagPlayer) event.getPlayer();
            tagPlayer.join();
        });
    }
}
