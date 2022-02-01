package com.dune.game.core;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.interfaces.Poolable;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Owner;
import com.dune.game.core.units.UnitType;

public class Bullet extends GameObject implements Poolable {

    private AbstractUnit owner;
    private Owner ownerType;
    private float angle;
    private int lvlUpgrade;
    private TextureRegion roketTexture;
    private float speed = 500.0f;
    private boolean active = false;
    private int damage;
    private Vector2 velocity;


    public Bullet(GameController gameController) {
        super(gameController);
        this.velocity = new Vector2();
        this.roketTexture = Assets.getInstance().getAtlas().findRegion("roket");
        this.lvlUpgrade = 1;
    }

    public void render(SpriteBatch batch) {
        if(lvlUpgrade>1){
            tmpV.set(1,0);
            tmpV.rotate(angle);
            tmpV.scl(30);
            tmpV.add(position);
            batch.draw(roketTexture, tmpV.x-8, tmpV.y-8, 8, 8, 16, 16, 2, 2, angle);
            for (int i = 0; i < 6; i++) {               // выстрелы анимация снарядов
                tmpV.set(1,0);                          // определение точки дула оружия от куда  будет выпущен снаряд
                tmpV.rotate(angle);
                tmpV.scl(20);
                tmpV.add(position);
                gameController.getParticleController().setup(tmpV.x, tmpV.y, MathUtils.random(-30.0f, 30.0f), MathUtils.random(-30.0f, 30.0f), 0.3f,
                        2f, 0.1f, 0.3f, 0.3f, 0, 0.3f, 0.1f, 0, 0, 0.5f);
            }
        }
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);              //this.position.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
        if(owner.getUnitType() != UnitType.HEALTANK){
            if (lvlUpgrade == 1) {
                for (int i = 0; i < 3; i++) {               // выстрелы анимация снарядов
                    tmpV.set(1, 0);                          // определение точки дула оружия от куда  будет выпущен снаряд
                    tmpV.rotate(angle);
                    tmpV.scl(20);
                    tmpV.add(position);
                    gameController.getParticleController().setup(tmpV.x, tmpV.y, MathUtils.random(-30.0f, 30.0f), MathUtils.random(-30.0f, 30.0f), 0.06f,
                            2f, 1f, 1, 1, 0, 1, 0.1f, 0, 0, 0.5f);
                }
            }
        }else{   // если юнит хилл
            for (int i = 0; i < 6; i++) {               // выстрелы анимация снарядов
                tmpV.set(1,0);                          // определение точки дула оружия от куда  будет выпущен снаряд
                tmpV.rotate(angle);
                tmpV.scl(20);
                tmpV.add(position);
                gameController.getParticleController().setup(tmpV.x, tmpV.y, MathUtils.random(-30.0f, 30.0f), MathUtils.random(-50.0f, 50.0f), 0.5f,
                        3f, 0.1f, 0.3f, 0, 0, 0.3f, 0.1f, 0, 0, 0.5f);
            }
        }
        chekCollisionBorderScreen();
    }

    // анимация снаряда
//    private TextureRegion getCurrentFrame() {
//        int frameIndex = (int) (moveTimer / timePerFrame) % sphereTextures.length;
//        return sphereTextures[frameIndex];
//    }

    public void setup(AbstractUnit owner, int lvlUpgrade){
        this.owner = owner;
        this.ownerType = owner.getOwnerType();
        this.lvlUpgrade =  lvlUpgrade;
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
