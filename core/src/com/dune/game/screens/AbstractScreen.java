package com.dune.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class AbstractScreen implements Screen {
    protected SpriteBatch batch;
    int width;
    int height;

    public AbstractScreen(SpriteBatch batch) {
        this.batch = batch;
    }
    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        ScreenManager.getInstance().resize(width, height);
    }
    @Override
    public void pause() {
        ScreenManager.getInstance().resize(width, height);
    }
    @Override
    public void resume() {
        ScreenManager.getInstance().resize(width, height);
    }
    @Override
    public void hide() {
        ScreenManager.getInstance().resize(width, height);
    }
}
