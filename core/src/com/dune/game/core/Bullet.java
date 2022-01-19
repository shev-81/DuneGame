package com.dune.game.core;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;

public class Bullet extends GameObject implements Poolable{

    private AbstractUnit owner;
    private Owner ownerType;
    private float angle;

    private TextureRegion[] sphereTextures;
    private float timePerFrame;
    private float moveTimer;
    private float speed = 500.0f;
    private boolean active = false;
    private int damage;


    public Bullet(GameController gameController) {
        super(gameController);
        timePerFrame = 0.07f; // время на показ 1 региона рисунка из анимации (иначе скорость анимации)
    }

    public void render(SpriteBatch batch) {
        batch.draw(getCurrentFrame(), position.x-8, position.y-8, 8, 8, 16, 16, 2, 2, angle);
    }

    public void update(float dt) {
        this.position.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
        moveTimer += dt;          // таймер для анимации
        chekCollisionBorderScreen();
    }

    private TextureRegion getCurrentFrame() {  // анимация снаряда
        int frameIndex = (int) (moveTimer / timePerFrame) % sphereTextures.length;
        return sphereTextures[frameIndex];
    }

    public void setup(AbstractUnit owner, TextureRegion [] sphereTextures){
        this.owner = owner;
        this.ownerType = owner.getOwnerType();
        this.sphereTextures =  sphereTextures;
        this.position.set(owner.getPosition());
        this.angle=owner.getWeapon().getAngle();
        this.damage = owner.getWeapon().getPower();
    }

    public int getDamage() {
        return damage;
    }

    public Owner getOwnerType() {
        return ownerType;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector2 getPosition() {
        return position;
    }

    public AbstractUnit getOwner() {
        return owner;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void chekCollisionBorderScreen () {
        if ((int) position.y > Gdx.graphics.getHeight() || (int) position.y < 0) {
            active = false;
        }
        if ((int) position.x > Gdx.graphics.getWidth() || (int) position.x < 0) {
            active = false;
        }
    }
}
