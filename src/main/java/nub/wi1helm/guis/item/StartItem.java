package nub.wi1helm.guis.item;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import nub.wi1helm.game.Game;
import nub.wi1helm.game.GameState; // Import GameState for checking current game state
import nub.wi1helm.template.inventory.TemplateInventoryEvent;
import nub.wi1helm.template.inventory.TemplateItem;

public class StartItem extends TemplateItem {
    public StartItem() {
        super(Material.LIME_DYE); // Default material
    }

    @Override
    protected void initialize() {
        // Initial name. This might change based on game state.
        setName(Component.text("Start/Ready [Click]"));
    }

    @Override
    protected void personalize(Player player) {
        // This method could be used to dynamically change the item's name/lore
        // based on the player's readiness or the game state, if the inventory is updated.
        // For example:
        // Game gameInstance = Game.getInstance();
        // if (gameInstance.getState() == GameState.IDLE) {
        //     setName(Component.text("Start Queue [Click]"));
        // } else if (gameInstance.getState() == GameState.QUEUE || gameInstance.getState() == GameState.COUNTDOWN) {
        //     setName(Component.text("Ready Up [Click]"));
        // }
    }

    @Override
    public void onUse(TemplateInventoryEvent templateInventoryEvent) {
        Player player = templateInventoryEvent.getPlayer();
        Game gameInstance = Game.getInstance(); // Get the singleton Game instance

        // The logic for starting the queue if IDLE, or just marking ready if QUEUE/COUNTDOWN
        // is handled by the setPlayerReady method in the Game class.
        // We simply tell the Game that this player is attempting to become ready.
        gameInstance.setPlayerReady(player, true);

        // Provide feedback to the player based on the current game state after the action.
        GameState currentState = gameInstance.getState();
        if (currentState == GameState.QUEUE) {
            player.sendMessage("You are now ready. Waiting for other players to join the queue.");
        } else if (currentState == GameState.COUNTDOWN) {
            player.sendMessage("You are now ready. Countdown is active!");
        } else if (currentState == GameState.RUNNING || currentState == GameState.ENDING) {
            // This case should ideally be prevented by the setPlayerReady method itself,
            // but adding a message here for clarity.
            player.sendMessage("The game is currently " + currentState.name() + ". You cannot ready up now.");
        } else { // GameState.IDLE (meaning setPlayerReady initiated the queue)
            player.sendMessage("You started the queue and are now ready! Waiting for other players.");
        }
    }

    @Override
    public void onDrop(TemplateInventoryEvent templateInventoryEvent) {
        // This item should probably not be droppable, or dropping it has no effect.
        // You can add a message here if you want.
        templateInventoryEvent.getPlayer().sendMessage("You cannot drop the Start Item!");
        templateInventoryEvent.setCancelled(true); // Prevent the item from actually being dropped
    }
}
