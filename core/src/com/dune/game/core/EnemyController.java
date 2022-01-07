package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EnemyController extends ObjectPool<Ball> {

    private GameController gameController;
    private TextureRegion[] sphereTexture;

    public EnemyController(GameController gameController) {
        super(gameController);
        this.gameController = gameController;
        this.sphereTexture = new TextureRegion(Assets.getInstance().getAtlas().findRegion("SphereAnim")).split(32, 32)[0];
        for (int i = 0; i <initialCapacity; i++) {
            freeList.add(newObject());
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < freeList.size(); i++) {
            freeList.get(i).render(batch);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < freeList.size(); i++) {
            freeList.get(i).update(dt);
        }
        checkPool();
    }

    @Override
    protected Ball newObject() {
        return new Ball((float) Math.random() * 1160 + 80, (float) Math.random() * 560 + 80, gameController, sphereTexture);
    }

}
