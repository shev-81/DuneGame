package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ProjectesController extends ObjectPool<Bullet>{

    private TextureRegion [] sphereTexture;

    public ProjectesController(GameController gameController) {
        super(gameController);
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

    public void setup(float angle, Vector2 tmpV, int damage){
        Bullet bullet = getActiveElement();
        bullet.setup(angle, tmpV, sphereTexture, damage);
        bullet.setActive(true);
    }

    @Override
    protected Bullet newObject() {
        return new Bullet(gameController);
    }
}
