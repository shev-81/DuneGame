package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.core.units.BattleTank;


public class BattleTankController extends ObjectPool<BattleTank>{

    public BattleTankController(GameController gameController) {
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

    public void setup(float x, float y, Owner ownerType){
        BattleTank tank = getActiveElement();
        tank.setup(ownerType, x, y);
    }



    @Override
    protected BattleTank newObject() {
        return new BattleTank(gameController);
    }
}
