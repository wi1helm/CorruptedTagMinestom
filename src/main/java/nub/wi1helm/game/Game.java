package nub.wi1helm.game;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.coordinate.Pos;
import nub.wi1helm.map.TagMap; // Assuming TagMap provides InstanceContainer and Pos
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import nub.wi1helm.player.TagPlayer;
import nub.wi1helm.player.TagPlayerState;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Game {

    // --- Singleton Instance ---
    private static Game instance;

    // --- Game State Variables ---
    private GameState state;
    private TagMap map = TagMap.BUSY_CITY; // Default map, assuming TagMap has a default or static value
    private List<TagPlayer> gamePlayers; // Players actively participating in the running game

    // --- Queue and Countdown Variables ---
    // Tracks readiness status for each online player who wants to join the queue.
    // Using ConcurrentHashMap for thread safety, as readiness might be updated from player commands.
    private Map<TagPlayer, Boolean> playerReadyStatus;
    private int countdownTimer; // Current value of the countdown
    private AtomicBoolean countdownActive; // Flag to indicate if the countdown is running

    // Minestom's scheduler for periodic tasks
    private Scheduler scheduler;
    private Task queueCheckTask; // Task for the periodic queue check
    private Task countdownTask;  // Task for the countdown loop

    // --- Constructor (Private for Singleton) ---
    private Game() {
        this.state = GameState.IDLE; // Initial state
        this.gamePlayers = new CopyOnWriteArrayList<>(); // Thread-safe list for game players
        this.playerReadyStatus = new ConcurrentHashMap<>();
        this.countdownActive = new AtomicBoolean(false);
        this.scheduler = MinecraftServer.getSchedulerManager(); // Get Minestom's scheduler
        System.out.println("Game instance created. Initial state: " + this.state);
    }

    /**
     * Provides the singleton instance of the Game class.
     * Initializes the instance if it doesn't already exist.
     *
     * @return The single instance of the Game.
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    // --- State Management ---

    /**
     * Gets the current state of the game.
     * @return The current GameState.
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets the state of the game.
     * @param newState The new GameState to set.
     */
    private void setState(GameState newState) {
        System.out.println("Game state changed from " + this.state + " to " + newState);
        this.state = newState;
    }

    // --- Player Readiness Management ---

    /**
     * Sets a player's ready status for the queue.
     * This method would typically be called by a command handler (e.g., /ready).
     *
     * @param player The player whose status is being set.
     * @param ready True if the player is ready, false otherwise.
     */
    public void setPlayerReady(TagPlayer player, boolean ready) {
        // Only allow changing ready status if in IDLE, QUEUE, or COUNTDOWN states
        if (state == GameState.IDLE || state == GameState.QUEUE || state == GameState.COUNTDOWN) {
            if (ready) {
                playerReadyStatus.put(player, true);
                player.sendMessage("You are now ready!");
                // If this is the first player to ready up, and no queue is active, start the queue.
                if (state == GameState.IDLE && playerReadyStatus.size() == 1) {
                    startQueue();
                }
            } else {
                playerReadyStatus.remove(player); // Remove from ready list if unready
                player.sendMessage("You are now unready!");
                // If player unreadies during countdown, cancel countdown
                if (countdownActive.get()) {
                    cancelCountdown("A player unreadied.");
                }
            }
            broadcastQueueStatus();
        } else {
            player.sendMessage("Cannot change ready status while game is " + state.name() + ".");
        }
    }

    /**
     * Broadcasts the current queue status to all online players.
     */
    private void broadcastQueueStatus() {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> {
            boolean isReady = playerReadyStatus.containsKey(p) && playerReadyStatus.get(p);
            p.sendMessage("Queue Status: " + (isReady ? "READY" : "NOT READY") + " (" + playerReadyStatus.size() + "/" + MinecraftServer.getConnectionManager().getOnlinePlayers().size() + " ready)");
        });
    }

    // --- Game Start Conditions ---

    /**
     * Checks if the game can currently transition to a starting state.
     * This considers the instance, number of online players, and game state.
     *
     * @return true if game initiation conditions are met, false otherwise.
     */
    public static boolean canStart() {
        // A game can conceptually 'start' (meaning, initiate its queue/countdown)
        // if there's at least one instance and enough players.
        if (instance == null) {
            System.out.println("Cannot start: Game instance not initialized.");
            return false;
        }
        if (MinecraftServer.getConnectionManager().getOnlinePlayers().size() < 2) {
            System.out.println("Cannot start: Need at least 2 online players.");
            return false;
        }
        // Additional check: If a game is already running or ending, we can't start a new one.
        if (instance.getState() == GameState.RUNNING || instance.getState() == GameState.ENDING) {
            System.out.println("Cannot start: Game is already " + instance.getState().name() + ".");
            return false;
        }
        return true;
    }

    // --- Queue Management ---

    /**
     * Starts the queue process for the game.
     * This will transition the game state to QUEUE and begin checking for player readiness.
     * If the queue is already active, it does nothing.
     */
    public void startQueue() {
        if (!canStart()) {
            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p ->
                    p.sendMessage("Game cannot start queue right now. Need at least 2 players, and game not RUNNING/ENDING.")
            );
            return;
        }
        if (state == GameState.QUEUE || state == GameState.COUNTDOWN) {
            System.out.println("Queue is already active or countdown is running.");
            return;
        }

        setState(GameState.QUEUE);
        System.out.println("Game queue activated. Waiting for players to ready up.");
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p ->
                p.sendMessage("A game queue has started! Type /ready to join. (" + playerReadyStatus.size() + "/" + MinecraftServer.getConnectionManager().getOnlinePlayers().size() + " ready)")
        );

        // Schedule a task to check player readiness every second
        queueCheckTask = scheduler.buildTask(() -> {
            // Get current online players to avoid issues with players disconnecting
            Collection<Player> currentOnlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayers();

            // Clear any disconnected players from playerReadyStatus
            playerReadyStatus.keySet().retainAll(currentOnlinePlayers);

            // Check if all current online players are ready
            boolean allPlayersReady = true;
            if (currentOnlinePlayers.isEmpty()) { // No players online, stop queue
                allPlayersReady = false;
                cancelQueue("No players online.");
                return;
            }

            for (Player p : currentOnlinePlayers) {
                if (!playerReadyStatus.containsKey(p) || !playerReadyStatus.get(p)) {
                    allPlayersReady = false;
                    break;
                }
            }

            if (allPlayersReady && !currentOnlinePlayers.isEmpty()) {
                if (!countdownActive.get()) { // All players ready, start countdown if not already active
                    startCountdown(20); // Start 20-second countdown
                }
            } else {
                // If not all players are ready, ensure countdown is not active
                if (countdownActive.get()) {
                    cancelCountdown("Not all players are ready anymore.");
                }
            }
        }).repeat(Duration.ofSeconds(1)).schedule();
    }

    /**
     * Cancels the active queue and resets related states.
     * @param reason The reason for cancelling the queue.
     */
    public void cancelQueue(String reason) {
        if (queueCheckTask != null) {
            queueCheckTask.cancel();
            queueCheckTask = null;
        }
        cancelCountdown(reason); // Also cancel any active countdown
        playerReadyStatus.clear();
        setState(GameState.IDLE);
        System.out.println("Game queue cancelled: " + reason);
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p ->
                p.sendMessage("Game queue has been cancelled: " + reason)
        );
    }

    // --- Countdown Management ---

    /**
     * Starts a countdown for game start.
     * @param seconds The duration of the countdown in seconds.
     */
    private void startCountdown(int seconds) {
        if (countdownActive.compareAndSet(false, true)) { // Only start if not already active
            setState(GameState.COUNTDOWN);
            countdownTimer = seconds;
            System.out.println("Countdown started for " + seconds + " seconds.");

            countdownTask = scheduler.buildTask(() -> {
                if (countdownTimer > 0) {
                    MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p ->
                            p.sendMessage("Game starting in " + countdownTimer + " seconds...")
                    );
                    countdownTimer--;
                } else {
                    // Countdown finished, proceed to game start
                    countdownTask.cancel();
                    start();
                }
            }).repeat(Duration.ofSeconds(1)).schedule();
        }
    }

    /**
     * Cancels an active countdown and reverts to the QUEUE state.
     * @param reason The reason for cancelling the countdown.
     */
    private void cancelCountdown(String reason) {
        if (countdownActive.compareAndSet(true, false)) { // Only cancel if active
            if (countdownTask != null) {
                countdownTask.cancel();
                countdownTask = null;
            }
            setState(GameState.QUEUE); // Go back to queue state
            System.out.println("Countdown cancelled: " + reason);
            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p ->
                    p.sendMessage("Game countdown cancelled: " + reason + " Back to queue.")
            );
        }
    }

    // --- Game Start ---

    /**
     * Initiates the actual game. This should only be called when all conditions are met
     * and the countdown (if any) has finished.
     */
    public void start() {
        if (!canStart()) {
            System.out.println("Game cannot be started. Pre-conditions not met.");
            return;
        }
        if (state == GameState.RUNNING) {
            System.out.println("Game is already running.");
            return;
        }

        // Cancel any active queue/countdown tasks
        if (queueCheckTask != null) {
            queueCheckTask.cancel();
            queueCheckTask = null;
        }
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        setState(GameState.RUNNING);
        gamePlayers.clear();
        // Populate gamePlayers with those who were ready when the countdown finished
        gamePlayers.addAll(playerReadyStatus.keySet());
        playerReadyStatus.clear(); // Clear ready status for next game

        gamePlayers.forEach(player -> {
            player.setState(TagPlayerState.NORMAL);
        });

        // Get the instance and spawn position from the TagMap
        // Assuming TagMap has methods like getInstanceContainer() and getSpawnPosition()
        Instance gameInstance = map.getInstance();
        Pos spawnPos = map.getSpawn();

        if (gameInstance == null || spawnPos == null) {
            System.err.println("ERROR: Map instance or spawn position not found for " + map.name() + ". Cannot teleport players.");
            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p ->
                    p.sendMessage("Error: Game map not configured correctly. Game start failed.")
            );
            setState(GameState.ENDING); // Transition to ending due to error
            return;
        }

        // Teleport all game players to the map instance
        for (Player player : gamePlayers) {
            if (player.isOnline()) { // Ensure player is still online
                player.setInstance(gameInstance, spawnPos);
                player.sendMessage("Welcome to the " + map.name() + " game!");
            }
        }

        System.out.println("Game started with " + gamePlayers.size() + " players!");
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> {
            if (!gamePlayers.contains(p)) {
                p.sendMessage("Game started! You are not a participant in this round.");
            }
        });

        // TODO: Add actual game logic here (e.g., enable PvP, start score tracking, etc.)
    }

    /**
     * Stops the currently running game.
     * This would typically be called when a game ends (e.g., time limit, objective met).
     */
    public void stop() {
        if (state != GameState.RUNNING && state != GameState.COUNTDOWN) {
            System.out.println("No game is running or in countdown to stop.");
            return;
        }
        setState(GameState.ENDING);
        System.out.println("Game ending...");

        // TODO: Implement game end logic:
        // - Teleport players back to a lobby/spawn point
        // - Reset player stats/inventories
        // - Announce winners/scores
        // - Clear gamePlayers list
        // - Delay before transitioning to IDLE for score display etc.
        // For now, immediately transition to IDLE after a short delay for demonstration
        scheduler.buildTask(() -> {
            gamePlayers.clear(); // Clear the list of players for the next game
            setState(GameState.IDLE);
            System.out.println("Game ended. State reset to IDLE.");
            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p ->
                    p.sendMessage("The game has ended. Ready for the next round!")
            );
        }).delay(Duration.ofSeconds(5)).schedule(); // 5-second delay before reset
    }
}
