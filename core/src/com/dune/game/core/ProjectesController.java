package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ProjectesController extends ObjectPool<Bullet>{
    private TextureAtlas atlas;
    private Tank tank;

    public ProjectesController(int initialCapacity, Tank tank, TextureAtlas atlas) {
        super(initialCapacity);
        this.atlas = atlas;
        this.tank = tank;
    }

    @Override
    protected Bullet newObject() {
        return new Bullet(atlas);
    }
}
