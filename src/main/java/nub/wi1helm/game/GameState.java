package nub.wi1helm.game;

public enum GameState {
    IDLE,       // No queue active, waiting for someone to start it
    QUEUE,      // Queue active, waiting for all players to ready up
    COUNTDOWN,  // All players ready, countdown to start is active
    RUNNING,    // Game is in progress
    ENDING      // Game is ending, preparing for reset or next round
}