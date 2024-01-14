package com.darko.plugin.gameclasses;

import java.util.Optional;

public class GameManager {

    private static Game game = null;

    public static Optional<Game> getActiveGame() {
        return game == null ? Optional.empty() : Optional.of(game);
    }

    public static void setActiveGame(Game activeGame) {
        game = activeGame;
    }


}
