package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.dune.game.core.Assets;
import com.dune.game.core.GameController;
import com.dune.game.core.interfaces.Targetable;

public class Building extends AbstractUnit{
    protected TextureRegion buildingTextures;


    public Building(GameController gameController) {
        super(gameController);
        this.buildingTextures = Assets.getInstance().getAtlas().findRegion("base");
        this.hpMax = 500;
        this.unitType = UnitType.BUILDING;
    }

    @Override
    public void setup(Owner owner, float x, float y) {
        this.ownerType = owner;
        this.position.set(x, y);
        this.hp = hpMax;
    }

    @Override
    public void render(SpriteBatch batch) {
        for (int i = 0; i < 5; i++) {   //  горящий курсор мыши
            gameController.getParticleController().setup(position.x, position.y + 24, MathUtils.random(-15.0f, 15.0f), MathUtils.random(-15.0f, 15.0f), 0.5f,
                    0.3f, 1.4f, 1, 1, 0, 1, 1, 0, 0, 0.5f);
        }
        switch (ownerType) {
            case PLAYER:
                batch.setColor(0.0f, 1f, 0.0f, 0.7f);
                batch.draw(buildingTextures, position.x - 70, position.y - 70);
                batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                break;
            case AI:
                batch.setColor(1f, 0.0f, 0.0f, 0.7f);
                batch.draw(buildingTextures, position.x - 70, position.y - 70);
                batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                break;
        }
        // отрисовка HP
        batch.setColor(0.2f, 0.0f, 0.0f, 1.0f);
        batch.draw(progressBarTextures, position.x - 32, position.y + 30, 64, 12);
        batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
        batch.draw(progressBarTextures, position.x - 30, position.y + 32, 60 * getHpPercent(), 8);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        renderGui(batch);
    }

    public void update(float dt) {
    }

    @Override
    public void renderGui(SpriteBatch batch) {
    }

    @Override
    public void updateWeapon(float dt) {
    }

    @Override
    public void commandAttack(Targetable target) {
    }
}
