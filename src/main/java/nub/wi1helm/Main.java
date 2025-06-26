package nub.wi1helm;


import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import nub.wi1helm.event.Global;
import nub.wi1helm.game.Game;
import nub.wi1helm.player.TagPlayer;
import nub.wi1helm.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {


    public static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();

        MinecraftServer.getConnectionManager().setPlayerProvider(TagPlayer::new);
        MinecraftServer.setCompressionThreshold(0);
        MinecraftServer.setBrandName("Corrupted Tag");

        Global.init();
        Template.init();

        Game game = Game.getInstance();

        MojangAuth.init();

        server.start("0.0.0.0",25565);

    }
}