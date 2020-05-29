package com.darko.plugin.gameclasses;

public class GameManager {

    static Game game;

    public static Game getActiveGame() {
        return game;
    }

    public static void setActiveGame(Game activeGame) {
        game = activeGame;
    }


}
