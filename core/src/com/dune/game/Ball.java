package com.dune.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Ball implements Obstacles{
    private float x;
    private float y;
    private Texture ballTexture;
    public Vector2 position;

    public Ball(float x, float y) {
        this.position = new Vector2(x,y);
        this.ballTexture = new Texture("circle.png");
    }

    public void render(SpriteBatch batch) {
        batch.draw(ballTexture,position.x-40,position.y-40);
    }

    public void update(Float dt){

    }

    public void dispose() {
        ballTexture.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }

}
