package com.dune.game.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Ball implements Obstacles {

    private float angle;
    private Vector2 position;
    private TextureRegion ballTexture;

    public Ball(float x, float y, TextureAtlas atlas) {
        this.position = new Vector2(x, y);
        this.ballTexture = atlas.findRegion("blackhole");
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch) {
        batch.draw(ballTexture, position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
    }

    public void update(Float dt) {
        angle += 180 * dt;
    }
    public void dispose() {

    }

    public Vector2 getPosition() {
        return position;
    }

}
