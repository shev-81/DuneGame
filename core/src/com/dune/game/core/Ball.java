package com.dune.game.core;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Ball extends GameObject implements Poolable {

    private TextureRegion[] sphereTextures;
    private boolean active = true;
    private float timePerFrame;
    private float moveTimer;

    public Ball(float x, float y, GameController gameController, TextureRegion[] sphereTextures) {
        super(gameController);
        this.position.set(x, y);
        this.sphereTextures = sphereTextures;
        timePerFrame = 0.2f; // время на показ 1 региона рисунка из анимации (иначе скорость анимации)
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch) {
        batch.draw(getCurrentFrame(), position.x - 16, position.y - 16, 16, 16, 32, 32, 2, 2, 0);
    }

    private TextureRegion getCurrentFrame() {
        int frameIndex = (int) (moveTimer / timePerFrame) % sphereTextures.length;
        return sphereTextures[frameIndex];
    }

    public void update(Float dt) {
        moveTimer += dt;          // таймер для анимации
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
