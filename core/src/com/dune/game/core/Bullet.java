package com.dune.game.core;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.interfaces.Poolable;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Owner;

public class Bullet extends GameObject implements Poolable {

    private AbstractUnit owner;
    private Owner ownerType;
    private float angle;

    private TextureRegion[] sphereTextures;
    private float timePerFrame;
    private float moveTimer;
    private float speed = 500.0f;
    private boolean active = false;
    private int damage;
    private Vector2 velocity;


    public Bullet(GameController gameController) {
        super(gameController);
        this.velocity = new Vector2();
        timePerFrame = 0.07f; // время на показ 1 региона рисунка из анимации (иначе скорость анимации)
    }

    public void render(SpriteBatch batch) {
        //batch.draw(getCurrentFrame(), position.x-8, position.y-8, 8, 8, 16, 16, 2, 2, angle);
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);              //this.position.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
        for (int i = 0; i < 3; i++) {               // выстрелы анимация снарядов
            tmpV.set(1,0);                          // определение точки дула оружия от куда  будет выпущен снаряд
            tmpV.rotate(angle);
            tmpV.scl(20);
            tmpV.add(position);
            gameController.getParticleController().setup(tmpV.x, tmpV.y, MathUtils.random(-30.0f, 30.0f), MathUtils.random(-30.0f, 30.0f), 0.06f,
                    2f, 1f, 1, 1, 0, 1, 0.1f, 0, 0, 0.5f);
        }
        moveTimer += dt;          // таймер для анимации
        chekCollisionBorderScreen();
    }

    // анимация снаряда
    private TextureRegion getCurrentFrame() {
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
        this.velocity.set(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle));
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

    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void chekCollisionBorderScreen () {
        if ((int) position.y > BattleMap.MAP_HEIGHT_PX || (int) position.y < 0) {
            active = false;
        }
        if ((int) position.x > BattleMap.MAP_WIDTH_PX || (int) position.x < 0) {
            active = false;
        }
    }
}
