package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Bullet implements Obstacles {

    private float angle;
    private Vector2 position;
    private TextureRegion bulletTexture;
    private float speed = 400.0f;
    private Tank tank;
    private boolean shoot = false;

    public Bullet(Tank tank, TextureAtlas atlas) {
        this.tank = tank;
        this.angle = tank.getAngle();
        this.position = new Vector2(tank.getPosition().x, tank.getPosition().y);
        this.bulletTexture = atlas.findRegion("bullet");
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    public boolean isShoot() {
        return shoot;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void render(SpriteBatch batch) {
        if(isShoot())
            batch.draw(bulletTexture, position.x, position.y, 8, 8, 16, 16, 2, 2, 0);
    }

    @Override
    public void update(Float dt) {
        if (isShoot()) {
            position.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
            if ((int) position.y > Gdx.graphics.getHeight() - 16) {
                speed = 0;
                shoot = false;
                position.x = tank.getPosition().x;
                position.y = tank.getPosition().y;
            }
            if ((int) position.y < 16) {
                speed = 0;
                shoot = false;
                position.x = tank.getPosition().x;
                position.y = tank.getPosition().y;
            }
            if ((int) position.x > Gdx.graphics.getWidth() - 16) {
                speed = 0;
                shoot = false;
                position.x = tank.getPosition().x;
                position.y = tank.getPosition().y;
                            }
            if ((int) position.x < 16) {
                speed = 0;
                shoot = false;
                position.x = tank.getPosition().x;
                position.y = tank.getPosition().y;
            }
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }
}
