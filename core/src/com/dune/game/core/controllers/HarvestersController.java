package com.dune.game.core.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.core.GameController;
import com.dune.game.core.ObjectPool;
import com.dune.game.core.units.Owner;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.Harvester;

public class HarvestersController extends ObjectPool<Harvester> {

    public HarvestersController(GameController gameController) {
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
        AbstractUnit unit = getActiveElement();
        unit.setup(ownerType, x, y);
    }

    @Override
    protected Harvester newObject() {
        return new Harvester(gameController);
    }
}