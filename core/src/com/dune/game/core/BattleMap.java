package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;


public class BattleMap extends GameObject{
    private TextureRegion grassTexture;
    private TextureRegion [] spicesTextures;
    private float moveTimer;
    private float timePerFrame;
    private Cell [][] cells;
    public static final int COLUMNS_COUNT = 16;
    public static final int ROWS_COUNT = 9;
    public static final int CELL_SIZE = 80;

    private class Cell{
        private int cellX, cellY;
        private int resource;
        private float resourceRegenerate;
        private float resourceRegenerateTime;

        public Cell(int cellX, int cellY) {
            this.cellX = cellX;
            this.cellY = cellY;
            resourceRegenerate = MathUtils.random(5.0f)-4.5f;
            if(resourceRegenerate<0.0f){
                resourceRegenerate = 0.0f;
            }else{
                resource = 1;
                resourceRegenerate *= 150.0f;  // скорость регенерации ресурса на карте
                resourceRegenerate += 10.0f;
            }
        }

        private void upDate(float dt){
            if(resourceRegenerate > 0.01f){     // регенерация ресурса
                resourceRegenerateTime+=dt;
                if (resourceRegenerateTime > resourceRegenerate){
                    resourceRegenerateTime = 0.0f;
                    resource++;
                    if(resource>5){
                        resource = 5;
                    }
                }
            }
        }

        private void render(SpriteBatch batch){
            if (resource > 0) {               // если есть ресурс
                float scale = 0.5f + 0.15f * resource;
                batch.draw(getCurrentFrame(), cellX*CELL_SIZE, cellY*CELL_SIZE, 40, 40,80,80,scale,scale,0);
            }else {
                if(resourceRegenerate>0.01){
                    batch.draw(getCurrentFrame(), cellX*CELL_SIZE, cellY*CELL_SIZE, 40, 40,80,80, 0.1f, 0.1f,0);
                }
            }
        }
    }

    public BattleMap (GameController gameController){
        super(gameController);
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.spicesTextures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("spices")).split(80, 80)[0];
        this.timePerFrame = 0.2f; // время на показ 1 региона рисунка из анимации (иначе скорость анимации)
        this.cells = new Cell[COLUMNS_COUNT][ROWS_COUNT];        // координаты спайса на карте по ячейкам 80 * 80
        loadSpices();
    }

    // выгружаем spices на игровую карту и определяем позиции
    private void loadSpices() {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j] = new Cell(i,j);
            }
        }
    }

    // проверяем есть ли ресурсс  на позиции танка на карте
    public int getResourceCount(Tank harvester){
        return cells[harvester.getCellX()][harvester.getCellY()].resource;
    }

    // сбор ресурсов на позиции танка на позиции ресурса на карте
    public int HarvestResource(Tank harvester, int power){
        int value = 0;
        if(cells[harvester.getCellX()][harvester.getCellY()].resource >= power ){
            value = power;
            cells[harvester.getCellX()][harvester.getCellY()].resource -= power;
        }else{
            value = cells[harvester.getCellX()][harvester.getCellY()].resource;
            cells[harvester.getCellX()][harvester.getCellY()].resource =0;
        }
        return value;
    }

    // метод вызывается из WorldRender
    public void render(SpriteBatch batch){
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                batch.draw(grassTexture, i*CELL_SIZE,j*CELL_SIZE);
                cells[i][j].render(batch);
            }
        }
    }

    // метод выдающий по порядку скрины анимации
    private TextureRegion getCurrentFrame() {
        int frameIndex = (int) (moveTimer / timePerFrame) % spicesTextures.length;
        return spicesTextures[frameIndex];
    }

    public void upDate(float dt) {
        moveTimer +=dt;
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j].upDate(dt);
            }
        }

    }
}
