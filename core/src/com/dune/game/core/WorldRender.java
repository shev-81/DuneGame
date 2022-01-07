package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class WorldRender {
    private SpriteBatch batch;
    private GameController gameController;

    public WorldRender(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
    }

    public void render() {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        batch.begin();
        gameController.getBattleMap().render(batch);        // отрисовка карты
        gameController.getTank().render(batch);             // отрисовка танка
        gameController.getProjectesController().render(batch);
        gameController.getEnemyController().render(batch);
        batch.end();
    }
}
