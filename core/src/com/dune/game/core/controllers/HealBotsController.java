package com.dune.game.core.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.core.GameController;
import com.dune.game.core.ObjectPool;
import com.dune.game.core.units.HealTank;
import com.dune.game.core.units.Owner;

public class HealBotsController extends ObjectPool<HealTank> {

    public HealBotsController(GameController gameController) {
        super(gameController);
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    public HealTank setup(float x, float y, Owner ownerType) {
        HealTank healTank = getActiveElement();
        healTank.setup(ownerType, x, y);
        return healTank;
    }

    @Override
    protected HealTank newObject() {
        return new HealTank(gameController);
    }
}
