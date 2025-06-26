package nub.wi1helm.event;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import nub.wi1helm.map.TagMap;
import nub.wi1helm.player.TagPlayer;

public class Global {

    private final EventNode<Event> node = MinecraftServer.getGlobalEventHandler();

    private static Global instance;

    private Global() {
        PlayerEventHandler.init(node);

        loadPlayer();
    }

    public static void init() {
        if (instance == null) instance = new Global();
    }


    private void loadPlayer() {
        node.addListener(AsyncPlayerConfigurationEvent.class, event -> {
           final TagPlayer tagPlayer = (TagPlayer) event.getPlayer();

           event.setSpawningInstance(TagMap.LOBBY.getInstance());
           tagPlayer.setRespawnPoint(TagMap.LOBBY.getSpawn());
        });
    }

}
