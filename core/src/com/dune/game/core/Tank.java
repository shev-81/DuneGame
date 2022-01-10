package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Tank extends GameObject implements Poolable {
    public enum Owner {
        PLAYER, AI
    }

    private Owner ownerType;
    private float angle;
    private float speed;
    private TextureRegion[] tankTextures;
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
    private boolean chosenStatus;

    public Tank(GameController gameController) {
        super(gameController);
        this.progressBarTextures = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.borderline = Assets.getInstance().getAtlas().findRegion("borderline");
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 180.0f;
        this.chosenStatus = false;
    }

    public void setup(Owner owner, float x, float y) {
        this.tankTextures = Assets.getInstance().getAtlas().findRegion("tankanim").split(64, 64)[0];
        this.ownerType = owner;
        this.position.set(x, y);
        this.destination = new Vector2(position);
        this.speed = 140.0f;
        this.weapon = new Weapon(Weapon.Type.HARVEST, 3.0f, 1);
        this.hp = 100;
        this.container = 0; // емкость ресурсов

    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch) {
        switch (ownerType) {
            case PLAYER:
                batch.draw(getCurrentFrame(), position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, angle);
                break;
            case AI:
                batch.setColor(1.0f, 0.5f, 0.0f, 1.0f);
                batch.draw(getCurrentFrame(), position.x - 40, position.y - 40, 40, 40, 80, 80, 0.8f, 0.8f, angle);
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
        // зеленая рамка выбора
        if(isChosenStatus()){
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

        // реакция на левый клик мыши
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            tmpV.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            for (Tank tank : gameController.getTankController().getActiveList()) {
                if (Math.abs(tmpV.dst(tank.getPosition())) < 30) {
                    setChosenStatus(true);
                } else {
                    tank.setChosenStatus(false);
                }
            }
        }

        // реакция на правый клик мыши
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && ownerType == Owner.PLAYER && chosenStatus) {
            destination.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        }
        if (position.dst(destination) > 3.0f) {                 // определение куда повернуться для движения
            float angleTo = tmpV.set(destination).sub(position).angle();
            if (Math.abs(angle - angleTo) > 3.0f) {
                if (angle > angleTo) {
                    if (Math.abs(angle - angleTo) <= 180.0f) {
                        angle -= rotationSpeed * dt;
                    } else {
                        angle += rotationSpeed * dt;
                    }
                }
                if (angle < angleTo) {
                    if (Math.abs(angle - angleTo) <= 180.0f) {
                        angle += rotationSpeed * dt;
                    } else {
                        angle -= rotationSpeed * dt;
                    }
                }
            }
            if (angle < 0.0f) {
                angle += 360.0f;
            }
            if (angle > 360.0f) {
                angle -= 360.0f;
            }
            moveTimer += dt;
            tmpV.set(speed, 0).rotate(angle);    // определение направления
            position.mulAdd(tmpV, dt);          // изменения метоположения в зависимости от dt
            if (position.dst(destination) < 120.0f && Math.abs(angleTo - angle) > 10) {     // исключение зацикливаний при повороте
                position.mulAdd(tmpV, -dt);
            }
        }
        updateWeapon(dt);
        chekCollision(dt);
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            angle += 180 * dt;
//        }
//
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            angle -= 180 * dt;
//        }
//
//        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            position.add(speed * MathUtils.cosDeg(angle) * dt, speed * MathUtils.sinDeg(angle) * dt);
//            moveTimer += dt;
//        }
//         выстрел из пушки по нажатию
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            shoot();
        }
    }

    public float getHpPercent() {    // процент от текущего хп
        return hp / 100;
    }

    public void updateWeapon(float dt) {
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

    // метод определяет параметры для снаряда который будет выпущен следующим
    public void shoot() {
        tmpV.set(1, 0);
        tmpV.rotate(angle);
        tmpV.scl(20);
        tmpV.add(position);
        gameController.getProjectesController().setup(angle, tmpV);

    }

    //анимация танка
    private TextureRegion getCurrentFrame() {
        int frameIndex = (int) (moveTimer / timePerFrame) % tankTextures.length;
        return tankTextures[frameIndex];
    }

    public void setChosenStatus(boolean chosenStatus) {
        this.chosenStatus = chosenStatus;
    }

    public boolean isChosenStatus() {
        return chosenStatus;
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

    @Override
    public boolean isActive() {
        return hp > 0;
    }
}
