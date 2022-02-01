package com.dune.game.core.controllers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.core.Bullet;
import com.dune.game.core.GameController;
import com.dune.game.core.ObjectPool;
import com.dune.game.core.units.AbstractUnit;

public class ProjectesController extends ObjectPool<Bullet> {



    public ProjectesController(GameController gameController) {
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

    public void setup(AbstractUnit unit, int lvlUpgrade){
        Bullet bullet = getActiveElement();
        bullet.setup(unit, lvlUpgrade);
        bullet.setActive(true);
    }

    @Override
    protected Bullet newObject() {
        return new Bullet(gameController);
    }
}
