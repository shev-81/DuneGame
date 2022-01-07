package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class BattleMap extends GameObject{
    private TextureRegion grassTexture;
    private TextureRegion [] spicesTextures;
    private float moveTimer;
    private float timePerFrame;
    private int [][] posSpices;
    private int counterSpices;
    private ArrayList <Vector2> spiceArr;

    public BattleMap (GameController gameController){
        super(gameController);
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.spicesTextures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("spices")).split(80, 80)[0];
        this.timePerFrame = 0.2f; // время на показ 1 региона рисунка из анимации (иначе скорость анимации)
        this.posSpices = new int[16][9];
        this.spiceArr = new ArrayList<>();
        loadSpices();
    }

    private void loadSpices() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                counterSpices = (int)(Math.random()*20);     // на сколько часто будет выведен Spices
                if (counterSpices <= 5){
                    posSpices [i][j] = counterSpices;
                    spiceArr.add(new Vector2(i*80+40,j*80+40));
                }
                else posSpices [i][j] =0;
            }
        }
    }

    public ArrayList<Vector2> getSpiceArr() {
        return spiceArr;
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch){
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                batch.draw(grassTexture, i*80,j*80);
                if(posSpices [i][j] == 1)
                    batch.draw(getCurrentFrame(), i*80, j*80);
            }
        }
    }

    public int[][] getPosSpices() {
        return posSpices;
    }

    private TextureRegion getCurrentFrame() {
        int frameIndex = (int) (moveTimer / timePerFrame) % spicesTextures.length;
        return spicesTextures[frameIndex];
    }

    public void upDate(float dt) {
        moveTimer +=dt;
    }
}
