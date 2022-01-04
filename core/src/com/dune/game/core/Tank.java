package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Tank {

    private float angle;
    private float speed;
    private TextureRegion[] tankTextures;
    private Vector2 position;
    private Vector2 tmpV;
    private float moveTimer;
    private float timePerFrame;
    private Bullet bullet;


    public Tank(float x, float y, TextureAtlas atlas) {
        this.position = new Vector2(x, y);
        this.tmpV = new Vector2(0,0);
        this.tankTextures = new TextureRegion(atlas.findRegion("tankanim")).split(64, 64)[0];
        this.speed = 140.0f;
        this.timePerFrame = 0.08f;
        bullet = new Bullet(atlas);
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch) {
        batch.draw(getCurrentFrame(), position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
        bullet.render(batch);
    }

    public void update(Float dt) {

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            angle += 180 * dt;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            angle -= 180 * dt;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            position.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
            moveTimer += dt;
        }
        // выстрел из пушки по нажатию
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (!bullet.isActive()) {
                shoot();
            }
        }
        bullet.update(dt);
        //блокируем выезд на границы игрового окна
        chekCollisionBorderScreen ();
    }

    public void shoot() {
        tmpV.set(1, 0);
        tmpV.rotate(angle);
        tmpV.scl(20);
        tmpV.add(position);
        bullet.setup(angle, tmpV);
        bullet.setActive(true);
    }

    //анимация танка
    private TextureRegion getCurrentFrame() {
        int frameIndex = (int) (moveTimer / timePerFrame) % tankTextures.length;
        return tankTextures[frameIndex];
    }

    public Bullet getBullet() {
        return bullet;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }

    public void chekCollisionBorderScreen (){
        if ((int) position.y > Gdx.graphics.getHeight() - 40) {
            position.y = Gdx.graphics.getHeight() - 40;
        }
        if ((int) position.y < 40) {
            position.y = 40;
        }
        if ((int) position.x > Gdx.graphics.getWidth() - 40) {
            position.x = Gdx.graphics.getWidth() - 40;
        }
        if ((int) position.x < 40) {
            position.x = 40;
        }
    }
}
