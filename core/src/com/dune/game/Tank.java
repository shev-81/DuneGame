package com.dune.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Tank {
    float x;
    float y;
    float angle;
    float speed;
    ArrayList<Obstacles> obstacles;
    private Texture tankTexture;
    Vector2 position;
    Vector2 positionObstacle;


    public Tank(float x, float y, ArrayList<Obstacles> obstacles ) {
        this.position = new Vector2(x,y);
        this.tankTexture = new Texture("tank.png");
        this.speed = 200.0f;
        this.obstacles = obstacles;
    }

    public void render(SpriteBatch batch) {
        batch.draw(tankTexture,position.x-40,position.y-40, 40, 40, 80, 80, 1, 1, angle,0,0, 80, 80,false,false);
    }

    public void update(Float dt){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            angle += 180 * dt;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            angle -= 180 * dt;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            position.x += speed * MathUtils.cosDeg(angle) * dt;
            position.y += speed * MathUtils.sinDeg(angle) * dt;
        }
        for (Obstacles o : obstacles){
            positionObstacle = o.getPosition();
            if (position.x > positionObstacle.x-40.0f && position.x < positionObstacle.x+40.0f &&
                position.y > positionObstacle.y-40.0f && position.y < positionObstacle.y+40.0f) {
                positionObstacle.x = (float) Math.random() * 1160 + 80;
                positionObstacle.y = (float) Math.random() * 560 + 80;

            }
        }
        if((int)position.y > Gdx.graphics.getHeight() || (int)position.y < 0 || (int)position.x > Gdx.graphics.getWidth() || (int)position.x < 0) {
            position.x = (float) Math.random() * 1160 + 80;
            position.y = (float) Math.random() * 560 + 80;
        }
    }

    public void dispose() {
        tankTexture.dispose();
    }
}
