package com.dune.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.core.GameController;
import com.dune.game.core.WorldRender;

public class GameScreen extends AbstractScreen {
    private GameController gameController;
    private WorldRender worldRender;

    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        this.gameController = new GameController();
        this.worldRender = new WorldRender(batch, gameController);
    }

    @Override
    public void render(float delta) {
        gameController.update(delta);
        worldRender.render();
    }

    @Override
    public void dispose() {
    }
}
