package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dune.game.screens.ScreenManager;

public class WorldRender {
    private SpriteBatch batch;
    private BitmapFont font12;
    private TextureRegion choiceTexture;
    private GameController gameController;
    private Vector2 endSelection;

    public WorldRender(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.font12 = Assets.getInstance().getAssetManager().get("fonts/Roboto-Medium12.ttf");
        this.choiceTexture = Assets.getInstance().getAtlas().findRegion("choiceline");
        this.gameController = gameController;
        this.endSelection = new Vector2();
    }

    public void render() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ScreenManager.getInstance().pointCameraTo(gameController.getPointOfView());
        batch.begin();
        gameController.getBattleMap().render(batch);                    // отрисовка карты
        gameController.getUnitsController().render(batch);             // отрисовка танков
        gameController.getProjectesController().render(batch);
        gameController.getParticleController().render(batch);
        //font12.draw(batch,"Dune game", 1, 680, 1280,1,false);
        drawSelectionFrame();
        batch.end();
        ScreenManager.getInstance().resetCamera();
        gameController.getStage().draw();

    }

    // отрисовка рамки выделения обласи захвата мышкой
    public void drawSelectionFrame() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            endSelection.set(gameController.getMouse());
        }
        if (gameController.getStartSelection().x > 0 && gameController.getStartSelection().y > 0) {
            float minX = Math.min(gameController.getStartSelection().x, endSelection.x);           // методы Math.min  &  Math.max
            float maxX = Math.max(gameController.getStartSelection().x, endSelection.x);           // возвращают мин или макс занчение от переданных аргументов
            float minY = Math.min(gameController.getStartSelection().y, endSelection.y);
            float maxY = Math.max(gameController.getStartSelection().y, endSelection.y);
            for (float i = minX; i < maxX; i += 2.0f) {
                batch.draw(choiceTexture, i, minY, 2, 2);
                batch.draw(choiceTexture, i, maxY, 2, 2);
            }
            for (float i = minY; i < maxY; i += 2.0f) {
                batch.draw(choiceTexture, minX, i, 2, 2);
                batch.draw(choiceTexture, maxX, i, 2, 2);
            }
        }
    }
}
