package com.dune.game.core;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends GameObject implements Poolable{

    private float angle;
    private Vector2 shootVector;
    private TextureRegion[] sphereTextures;
    private float timePerFrame;
    private float moveTimer;
    private float speed = 500.0f;
    private boolean active = false;
    private int damage;

    public Bullet(GameController gameController) {
        super(gameController);
        this.shootVector = new Vector2(0,0);
        timePerFrame = 0.07f; // время на показ 1 региона рисунка из анимации (иначе скорость анимации)
    }

    public void render(SpriteBatch batch) {
        batch.draw(getCurrentFrame(), shootVector.x-8, shootVector.y-8, 8, 8, 16, 16, 2, 2, angle);
    }

    public void update(float dt) {
        this.shootVector.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
        moveTimer += dt;          // таймер для анимации
        chekCollisionBorderScreen();
    }

    private TextureRegion getCurrentFrame() {  // анимация снаряда
        int frameIndex = (int) (moveTimer / timePerFrame) % sphereTextures.length;
        return sphereTextures[frameIndex];
    }

    public void setup(float angle, Vector2 tmpVector, TextureRegion [] sphereTextures, int damage){
        this.sphereTextures =  sphereTextures;
        this.shootVector.set(tmpVector);
        this.angle=angle;
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
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
