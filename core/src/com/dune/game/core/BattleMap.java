package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BattleMap extends GameObject{
    private TextureRegion grassTexture;

    public BattleMap (GameController gameController){
        super(gameController);
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch){
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                batch.draw(grassTexture, i*80,j*80);
            }
        }
    }
}
