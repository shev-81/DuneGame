package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Tank extends GameObject{

    private float angle;
    private float speed;
    private TextureRegion[] tankTextures;
    private Vector2 destination;
    private float moveTimer;
    private float timePerFrame;
    private float rotationSpeed;

    public Tank(GameController gameController) {
        super(gameController);
        this.tankTextures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("tankanim")).split(64, 64)[0];
        this.position.set(200,200);
        this.destination = new Vector2(position);
        this.speed = 140.0f;
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 180.0f;
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch) {
        batch.draw(getCurrentFrame(), position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
    }

    public void update(Float dt) {
        if (Gdx.input.justTouched()) {
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
            moveTimer +=dt;
            tmpV.set(speed,0).rotate(angle);    // определение направления
            position.mulAdd(tmpV, dt);          // изменения метоположения в зависимости от dt
            if(position.dst(destination) < 120.0f && Math.abs(angleTo- angle) >10){     // исключение зацикливаний при повороте
                position.mulAdd(tmpV, -dt);
            }
        }
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
        // выстрел из пушки по нажатию
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            shoot();
        }
        //блокируем выезд на границы игрового окна
        chekCollisionBorderScreen();
    }

    public void shoot() {     // метод определяет параметры для снаряда который будет выпущен следующим
        tmpV.set(1, 0);
        tmpV.rotate(angle);
        tmpV.scl(20);
        tmpV.add(position);
        for (int i = -90; i < 90; i+=10) {
            gameController.getProjectesController().setup(angle+i, tmpV);
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
