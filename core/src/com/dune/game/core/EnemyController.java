package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class EnemyController extends ObjectPool<Ball> {
    private GameController gameController;

    public EnemyController(GameController gameController) {
        super(gameController);
        this.gameController = gameController;
        activeList.addAll(freeList);
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            freeList.get(i).render(batch);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            freeList.get(i).update(dt);
        }
        checkPool();
    }

//    public void setup(float angle, Vector2 tmpV) {
//        Ball ball = getActiveElement();
//    }

    @Override
    protected Ball newObject() {
        return new Ball((float) Math.random() * 1160 + 80, (float) Math.random() * 560 + 80, gameController);
    }

}
