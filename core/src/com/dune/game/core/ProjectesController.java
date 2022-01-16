package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ProjectesController extends ObjectPool<Bullet>{

    private TextureRegion [] sphereTexture;
    private Vector2 tmpV;

    public ProjectesController(GameController gameController) {
        super(gameController);
        this.tmpV = new Vector2();
        this.sphereTexture = new TextureRegion(Assets.getInstance().getAtlas().findRegion("shootBall")).split(16, 16)[0];
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

    public void setup(Tank tank){
        Bullet bullet = getActiveElement();
        bullet.setup(tank, sphereTexture);
        bullet.setActive(true);
    }

    @Override
    protected Bullet newObject() {
        return new Bullet(gameController);
    }
}
