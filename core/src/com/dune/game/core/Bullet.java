package com.dune.game.core;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Bullet implements Poolable{

    private float angle;
    private Vector2 shootVector;
    private TextureRegion bulletTexture;
    private float speed = 400.0f;
    private boolean active = false;

    public Bullet(TextureAtlas atlas) {
        this.bulletTexture = atlas.findRegion("bullet2");
        this.shootVector = new Vector2(0,0);
    }

    public void render(SpriteBatch batch) {
        if (isActive())
            batch.draw(bulletTexture, shootVector.x, shootVector.y, 8, 8, 16, 16, 2, 2, 0);
    }

    public void update(float dt) {
        if (isActive()) {
            this.shootVector.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
            chekCollisionBorderScreen ();
        }
    }
    public void setup(float angle, Vector2 tmpVector){
        this.shootVector.set(tmpVector);
        this.angle=angle;

    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector2 getShootVector() {
        return shootVector;
    }
    @Override
    public boolean isActive() {
        return active;
    }

    public void chekCollisionBorderScreen () {
        if ((int) shootVector.y > Gdx.graphics.getHeight() || (int) shootVector.y < 0) {
            active = false;
        }
        if ((int) shootVector.x > Gdx.graphics.getWidth() || (int) shootVector.x < 0) {
            active = false;
        }
    }
}
