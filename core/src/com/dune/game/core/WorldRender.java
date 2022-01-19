package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class WorldRender {
    private SpriteBatch batch;
    private BitmapFont font12;
    private GameController gameController;

    public WorldRender(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.font12 = Assets.getInstance().getAssetManager().get("fonts/Roboto-Medium12.ttf");
        this.gameController = gameController;
    }

    public void render() {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        batch.begin();
        gameController.getBattleMap().render(batch);        // отрисовка карты
        gameController.getUnitsController().render(batch);             // отрисовка танков
        gameController.getProjectesController().render(batch);
        font12.draw(batch,"Dune game", 1, 680, 1280,1,false);
        batch.end();
    }
}
