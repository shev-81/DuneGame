package com.dune.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class DuneGame extends ApplicationAdapter {
    private SpriteBatch batch;
    Tank tank;
    ArrayList<Obstacles> obstacles;

    @Override
    public void create() {
        batch = new SpriteBatch();
        obstacles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            obstacles.add(new Ball((float) Math.random() * 1160 + 80, (float) Math.random() * 560 + 80));
        }
        tank = new Tank(200, 200, obstacles);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.1f, 0.2f, 0, 1);
        batch.begin();
        update(dt);
        tank.render(batch);
        for (Obstacles o : obstacles)
            o.render(batch);
        batch.end();
    }

    public void update(Float dt) {
        tank.update(dt);
        for (Obstacles o : obstacles)
            o.update(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (Obstacles o : obstacles)
            o.dispose();
        tank.dispose();
    }
}
