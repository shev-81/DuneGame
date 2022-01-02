package com.dune.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class DuneGame extends ApplicationAdapter {
    private SpriteBatch batch;
	Tank tank;

    private static class Tank {
        float x;
        float y;
        float angle;
        float speed;
        private Texture tankTexture;
        Vector2 position;

        public Tank(float x, float y) {
            this.position = new Vector2(x,y);
            this.tankTexture = new Texture("tank.png");
            this.speed = 200.0f;
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

        }

        public void dispose() {
            tankTexture.dispose();
        }
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        tank = new Tank(200, 200);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.1f, 0.2f, 0, 1);
        batch.begin();
        update(dt);
		tank.render(batch);
        batch.end();
    }
    public void update(Float dt){
        tank.update(dt);
    }
    @Override
    public void dispose() {
        batch.dispose();
        tank.dispose();
    }
}
