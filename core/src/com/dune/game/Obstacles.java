package com.dune.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Obstacles {
    void render(SpriteBatch batch);
    void update(Float dt);
    void dispose();
    Vector2 getPosition();
}
