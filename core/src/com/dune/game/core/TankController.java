package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TankController extends ObjectPool<Tank>{

    public TankController(GameController gameController) {
        super(gameController);
    }

    public void render (SpriteBatch batch){
        for(int i = 0; i<activeList.size(); i++){
            activeList.get(i).render(batch);
        }
    }

    public void update(float dt){
        for(int i = 0; i<activeList.size(); i++){
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    public void setup(float x, float y, Tank.Owner ownerType){
        Tank tank = getActiveElement();
        tank.setup(ownerType, x, y);
    }

    @Override
    protected Tank newObject() {
        return new Tank(gameController);
    }
}
