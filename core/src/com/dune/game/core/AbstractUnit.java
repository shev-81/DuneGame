package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractUnit extends GameObject implements Poolable{
    public enum Owner {
        PLAYER, AI
    }

    private Owner ownerType;
    private float angle;
    private float speed;
    private TextureRegion[] tankTextures;
    private TextureRegion[] weaponsTextures;
    private TextureRegion progressBarTextures;
    private TextureRegion borderline;
    private Vector2 destination;
    private float moveTimer;
    private float timePerFrame;
    private float rotationSpeed;
    private Weapon weapon;
    private int container;
    public static final int CONTAINER_POINT = 10;
    public static final int CONTAINER_CAPACITY = 50;
    private float hp;
    private float hpMax = 100;
    private Tank target;

    public AbstractUnit(GameController gameController) {
        super(gameController);
        this.progressBarTextures = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.borderline = Assets.getInstance().getAtlas().findRegion("borderline");
        this.weaponsTextures = new TextureRegion[]{
                new TextureRegion(Assets.getInstance().getAtlas().findRegion("turret")),
                new TextureRegion(Assets.getInstance().getAtlas().findRegion("harvester"))
        };
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 180.0f;
    }

    public void setup(Owner owner, float x, float y) {
        this.tankTextures = Assets.getInstance().getAtlas().findRegion("tankcore").split(64, 64)[0];
        this.ownerType = owner;
        this.position.set(x, y);
        this.destination = new Vector2(position);
        this.speed = 140.0f;
        if(MathUtils.random()<0.5){
            this.weapon = new Weapon(Weapon.Type.HARVEST, 3.0f, 1);
        }else{
            this.weapon = new Weapon(Weapon.Type.GROUND, 1.5f, 10);
        }
        this.hp = hpMax;
        this.container = 0; // емкость ресурсов
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch) {

        switch (ownerType) {
            case PLAYER:
                batch.draw(getCurrentFrame(), position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, angle);
                batch.draw(weaponsTextures[weapon.getType().getImageIndex()], position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, weapon.getAngle());
                break;
            case AI:
                batch.setColor(1.0f, 0.5f, 0.0f, 1.0f);
                batch.draw(getCurrentFrame(), position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, angle);
                batch.draw(weaponsTextures[weapon.getType().getImageIndex()], position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, weapon.getAngle());
                batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                break;
        }

        // если активен сбор ресурса отрисовка прогресса тика сбора
        if(container <= CONTAINER_CAPACITY){
            if (weapon.getType() == Weapon.Type.HARVEST && weapon.getWeaponPercentage() > 0.0f) {
                batch.setColor(0.2f, 0.0f, 0.0f, 1.0f);
                batch.draw(progressBarTextures, position.x - 32, position.y + 43, 64, 8);
                batch.setColor(1.0f, 1.0f, 0.0f, 1.0f);
                batch.draw(progressBarTextures, position.x - 30, position.y + 45, 60 * weapon.getWeaponPercentage(), 4);
                batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }

        // отрисовка HP танка
        batch.setColor(0.2f, 0.0f, 0.0f, 1.0f);
        batch.draw(progressBarTextures, position.x - 32, position.y + 30, 64, 12);
        batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
        batch.draw(progressBarTextures, position.x - 30, position.y + 32, 60 * getHpPercent(), 8);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        // отрисовка заполнения контейнера харвестера ресурсом
        for (int i = 1; i <= container / CONTAINER_POINT; i++) {
            batch.setColor(0.2f, 0.0f, 0.0f, 1.0f);
            batch.draw(progressBarTextures, position.x + 34, position.y - 30 + (10 * (i - 1)), 10, 10);
            batch.setColor(0.0f, 1.0f, 1.0f, 1.0f);
            batch.draw(progressBarTextures, position.x + 36, position.y - 28 + (10 * (i - 1)), 6, 6);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        // рамка выбора
        if(gameController.isTankSelection(this)){
            switch (ownerType){
                case PLAYER:
                    batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
                    batch.draw(borderline, position.x+38, position.y + 46, 0, 0, 78,2,1,1,-180);   // верхняя
                    batch.draw(borderline, position.x-40, position.y - 40, 0, 0, 78,2,1,1,0);      // нижняя
                    batch.draw(borderline, position.x+40, position.y - 38, 0, 0, 82,2,1,1,90);     // правая
                    batch.draw(borderline, position.x-80, position.y + 2, 40, 1, 82,2,1,1,-90);    // левая
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case AI:
                    batch.setColor(1.0f, 0.0f, 0.0f, 1.0f);
                    batch.draw(borderline, position.x+38, position.y + 46, 0, 0, 78,2,1,1,-180);   // верхняя
                    batch.draw(borderline, position.x-40, position.y - 40, 0, 0, 78,2,1,1,0);      // нижняя
                    batch.draw(borderline, position.x+40, position.y - 38, 0, 0, 82,2,1,1,90);     // правая
                    batch.draw(borderline, position.x-80, position.y + 2, 40, 1, 82,2,1,1,-90);    // левая
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    break;
            }
        }
    }

    public void update(Float dt) {
        // если цель выбрана движемся к ней до расстояния 200
        if(target != null){
            destination.set(target.position);
            if(position.dst(target.position) < 200){
                destination.set(position);
            }
        }
        // если цель танка мертва то сбрасываем ему цель
        if(target != null && !target.isActive()){
            target = null;
        }
        // определение куда повернуться для движения
        if (position.dst(destination) > 3.0f) {
            float angleTo = tmpV.set(destination).sub(position).angle();
            angle = rotateTo(angle, angleTo, rotationSpeed, dt);
            moveTimer += dt;
            tmpV.set(speed, 0).rotate(angle);    // определение направления
            position.mulAdd(tmpV, dt);          // изменения метоположения в зависимости от dt
            if (position.dst(destination) < 120.0f && Math.abs(angleTo - angle) > 10) {     // исключение зацикливаний при повороте
                position.mulAdd(tmpV, -dt);
            }
        }
        updateWeapon(dt);
        chekCollision(dt);
    }

    public void setTarget(Tank target) {
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
        return hp / 100;
    }

    // перезарядка оружия танка
    public void updateWeapon(float dt) {
        if (weapon.getType() == Weapon.Type.GROUND && target != null) {
            float angleTo = tmpV.set(target.position).sub(position).angle();
            weapon.setAngle(rotateTo(weapon.getAngle(), angleTo, 180.0f, dt));
            int power = weapon.use(dt);
            if(power > -1 && Math.abs(weapon.getAngle() - angleTo) < 10){
                gameController.getProjectesController().setup(this);
            }
        }
        if(target == null){
            float angleTo = tmpV.set(destination).sub(position).angle(); // tmpV.set(destination).sub(position).angle();
            weapon.setAngle(rotateTo(weapon.getAngle(), angleTo, 180.0f, dt));
        }
        if (weapon.getType() == Weapon.Type.HARVEST) {
            if (gameController.getBattleMap().getResourceCount(this) > 0) {
                int result = weapon.use(dt);
                if (result > -1 && container <= CONTAINER_CAPACITY) {
                    container += gameController.getBattleMap().HarvestResource(this, result); // наполняем контейнер
                }
            } else {
                weapon.reset();
            }
        }
    }

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
        if ((int) position.y > 680) {
            position.y = 680;
        }
        if ((int) position.y < 40) {
            position.y = 40;
        }
        if ((int) position.x > 1240) {
            position.x = 1240;
        }
        if ((int) position.x < 40) {
            position.x = 40;
        }
    }

    public Tank getTarget() {
        return target;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void commandMoveTo(Vector2 point){
        destination.set(point);
        target = null;
    }
    public void commandAttack(Tank target){
        this.target = target;
    }

    public Owner getOwnerType() {
        return ownerType;
    }

    public float setHp(float damage) {
        this.hp -= damage;
        return hp;
    }

    public float getHp() {
        return hp;
    }

    @Override
    public boolean isActive() {
        return hp > 0;
    }
}
