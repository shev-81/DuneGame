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
    private TextureRegion [] tankTextures;
    private Vector2 position;
    private float moveTimer;
    private float timePerFrame;
    private TextureAtlas atlas;
    private TextureRegion bulletTexture;
    private GameController gameController;


    public Tank(float x, float y, TextureAtlas atlas, GameController gameController) {
        this.position = new Vector2(x,y);
        this.tankTextures = new TextureRegion(atlas.findRegion("tankanim")).split(64,64)[0];
        this.speed = 140.0f;
        this.timePerFrame = 0.08f;
        this.atlas = atlas;
        this.gameController = gameController;
    }

    //анимация танка
    private TextureRegion getCurrentFrame(){
        int frameIndex = (int)(moveTimer / timePerFrame) % tankTextures.length;
        return tankTextures[frameIndex];
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch) {
        batch.draw(getCurrentFrame(),position.x-40,position.y-40, 40, 40, 80, 80, 1, 1, angle);
    }

    public Vector2 getPosition() {
        return position;
    }

    public GameController getGameController() {
        return gameController;
    }

    public float getAngle() {
        return angle;
    }

    public TextureRegion getBulletTexture() {
        return bulletTexture;
    }

    public void update(Float dt){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            angle += 180 * dt;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            angle -= 180 * dt;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            position.add(speed * MathUtils.cosDeg(angle) * dt,speed * MathUtils.sinDeg(angle) * dt);
            moveTimer += dt;
        }
        // выстрел из пушки по нажатию
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if(!getGameController().getBullet().isShoot()){
                gameController.getBullet().setShoot(true);
                gameController.getBullet().setSpeed(400.0f);
            }
        }

        //блокируем выезд на границы игрового окна
        if((int)position.y > Gdx.graphics.getHeight()-40) {
            position.y = Gdx.graphics.getHeight()-40;
        }
        if((int)position.y < 40){
            position.y = 40;
        }
        if((int)position.x > Gdx.graphics.getWidth()-40){
            position.x = Gdx.graphics.getWidth()-40;
        }
        if((int)position.x < 40){
            position.x = 40;
        }
    }
}
