package com.example.snakeprogame.game;

public class BoardGame {
    private final GameModule module;

    public BoardGame() {
        module = new GameModule();
    }

    public GameModule getModule() {
        return module;
    }
}