package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.*;

public abstract class AbstractUnit extends GameObject implements Poolable, Targetable {

    protected UnitType unitType;
    protected TargetType targetType;
    protected Owner ownerType;
    protected Weapon weapon;
    protected Targetable target;

    protected TextureRegion[] tankTextures;
    protected TextureRegion weaponsTextures;
    protected TextureRegion progressBarTextures;
    protected TextureRegion borderline;
    protected Vector2 destination;

    protected int container;
    public static final int CONTAINER_POINT = 10;
    public static final int CONTAINER_CAPACITY = 50;

    protected float angle;
    protected float speed;
    protected float moveTimer;
    protected float timePerFrame;
    protected float rotationSpeed;

    protected float hp;
    protected float hpMax = 100;
    protected float minDstToActiveTarget;

    public AbstractUnit(GameController gameController) {
        super(gameController);
        this.progressBarTextures = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.borderline = Assets.getInstance().getAtlas().findRegion("borderline");
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 180.0f;
        this.container = 0;
    }

    public abstract void setup(Owner owner, float x, float y);

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch) {
        switch (ownerType) {
            case PLAYER:
                batch.draw(getCurrentFrame(), position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, angle);
                batch.draw(weaponsTextures, position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, weapon.getAngle());
                break;
            case AI:
                batch.setColor(1.0f, 0.5f, 0.0f, 1.0f);
                batch.draw(getCurrentFrame(), position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, angle);
                batch.draw(weaponsTextures, position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, weapon.getAngle());
                batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                break;
        }
        // отрисовка HP танка
        batch.setColor(0.2f, 0.0f, 0.0f, 1.0f);
        batch.draw(progressBarTextures, position.x - 32, position.y + 30, 64, 12);
        batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
        batch.draw(progressBarTextures, position.x - 30, position.y + 32, 60 * getHpPercent(), 8);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        renderGui(batch);
    }
    public abstract void renderGui(SpriteBatch batch);

    public void update(Float dt) {
        // если цель выбрана движемся к ней до расстояния 200
        if(target != null){
            destination.set(target.getPosition());
            if(position.dst(target.getPosition()) < minDstToActiveTarget){
                destination.set(position);
            }
        }

        // определение куда повернуться для движения
        if (position.dst(destination) > 3.0f) {
            float angleTo = tmpV.set(destination).sub(position).angle();
            angle = rotateTo(angle, angleTo, rotationSpeed, dt);
            moveTimer += dt;
            //анимация проезда по ресурсам
            if (gameController.getBattleMap().getResourceCount(position) > 0) {
                for (int i = 0; i < gameController.getBattleMap().getResourceCount(position); i++) {
                    gameController.getParticleController().setup(MathUtils.random(getCellX() * 80, getCellX() * 80 + 80), MathUtils.random(getCellY() * 80, getCellY() * 80 + 80), MathUtils.random(-20, 20), MathUtils.random(-20, 20), 0.3f, 0.5f, 0.4f,
                            0, 0, 1, 0.1f, 1, 1, 1, 0.4f);
                }
            }
            tmpV.set(speed, 0).rotate(angle);    // определение направления
            position.mulAdd(tmpV, dt);          // изменения метоположения в зависимости от dt
            if (position.dst(destination) < 120.0f && Math.abs(angleTo - angle) > 10) {     // исключение зацикливаний при повороте
                position.mulAdd(tmpV, -dt);
            }
        }
        updateWeapon(dt);
        chekCollision(dt);
    }

    public void setTarget(AbstractUnit target) {
        this.target = target;
    }

    // метод изменяет угол srcAngle до angleTo за дельту времени dt со скоростью rotationSpeed
    public float rotateTo(float srcAngle, float angleTo, float rSpeed, float dt ) {
        if (Math.abs(angle - angleTo) > 3.0f) {
            if ((srcAngle > angleTo && Math.abs(srcAngle - angleTo) <= 180.0f) || (srcAngle < angleTo && Math.abs(srcAngle - angleTo) > 180.0f)) {
                srcAngle -= rSpeed * dt;
            } else {
                srcAngle += rSpeed * dt;
            }
        }
        if (srcAngle < 0.0f) {
            srcAngle += 360.0f;
        }
        if (srcAngle > 360.0f) {
            srcAngle -= 360.0f;
        }
        return srcAngle;
    }

    // метод определения остановки дистанции на которой произойдет остановка если
    // находимся у точки назначения
    public void moveBy(Vector2 value) {
        boolean stayStill = false;
        if (Math.abs(position.dst(destination)) < 25.0f) {
            stayStill = true;
        }
        position.add(value);
        if (stayStill) {
            destination.set(position);
        }
    }

    public float getHpPercent() {    // процент от текущего хп
        return hp / hpMax;
    }

    // перезарядка оружия танка
    public abstract void updateWeapon(float dt);

    //анимация танка
    private TextureRegion getCurrentFrame() {
        int frameIndex = (int) (moveTimer / timePerFrame) % tankTextures.length;
        return tankTextures[frameIndex];
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getDestination() {
        return destination;
    }

    //блок выезда за границы игрового окна
    public void chekCollision(float dt) {
        // сталкивается ли танк с краем экрана
        if (position.x < 40) {
            position.x = 40;
        }
        if (position.y < 40) {
            position.y = 40;
        }
        if (position.x > BattleMap.MAP_WIDTH_PX - 40) {
            position.x = BattleMap.MAP_WIDTH_PX - 40;
        }
        if (position.y > BattleMap.MAP_HEIGHT_PX - 40) {
            position.y = BattleMap.MAP_HEIGHT_PX - 40;
        }
    }

    public TargetType getType() {
        return targetType.UNIT;
    }

    public Targetable getTarget() {
        return target;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void commandMoveTo(Vector2 point){
        destination.set(point);
        target = null;
    }

    public abstract void commandAttack(Targetable target);

    public Owner getOwnerType() {
        return ownerType;
    }

    public boolean takeDamage(int damage) {
        if(!isActive()){
            return false;
        }
        this.hp -= damage;
        return hp<0;
    }

    public float getHpMax() {
        return hpMax;
    }

    public TextureRegion getBorderline() {
        return borderline;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public float getHp() {
        return hp;
    }

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public int getContainer() {
        return container;
    }

    public void setContainer(int container) {
        this.container = container;
    }
}
