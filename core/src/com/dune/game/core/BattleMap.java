package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.screens.ScreenManager;

public class BattleMap extends GameObject{
    private TextureRegion grassTexture;
    private TextureRegion choiceTexture;
    private TextureRegion [] spicesTextures;
    private TextureRegion [] clickmouseTextures;

    private float moveTimer;
    private float timePerFrame;
    private Cell [][] cells;
    private Vector2 startSelection;
    private Vector2 endSelection;
    private Vector2 mouse;
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
                batch.draw(getCurrentFrame(spicesTextures), cellX*CELL_SIZE, cellY*CELL_SIZE, 40, 40,80,80,scale,scale,0);
            }else {
                if(resourceRegenerate>0.01){
                    batch.draw(getCurrentFrame(spicesTextures), cellX*CELL_SIZE, cellY*CELL_SIZE, 40, 40,80,80, 0.1f, 0.1f,0);
                }
            }
        }
    }

    public BattleMap (GameController gameController){
        super(gameController);
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.choiceTexture = Assets.getInstance().getAtlas().findRegion("choiceline");
        this.spicesTextures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("spices")).split(80, 80)[0];
        this.clickmouseTextures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("clickmouse")).split(24, 24)[0];
        this.timePerFrame = 0.1f; // время на показ 1 региона рисунка из анимации (иначе скорость анимации)
        this.cells = new Cell[COLUMNS_COUNT][ROWS_COUNT];        // координаты спайса на карте по ячейкам 80 * 80
        this.startSelection = new Vector2();
        this.endSelection = new Vector2();
        this.mouse = new Vector2();
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
    public void render(SpriteBatch batch) {
        // рорисовка тайлов ресурсов
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                batch.draw(grassTexture, i * CELL_SIZE, j * CELL_SIZE);
                cells[i][j].render(batch);
            }
        }
        if(Gdx.input.isButtonPressed (Input.Buttons.RIGHT)){
            batch.draw(getCurrentFrame(clickmouseTextures), mouse.x-12, mouse.y-12, 12, 12,24,24, 1.2f, 1.2f,0);
        }
        // отрисовка рамки выделения обласи захвата мышкой
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            endSelection.set(mouse); // Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()
            startSelection.set(gameController.getStartSelection());
            for (int i = 1; i < Math.abs(endSelection.x - startSelection.x); i++) {
                if (endSelection.x < startSelection.x) {
                    batch.draw(choiceTexture, startSelection.x - i, endSelection.y, 2, 2);    // верх
                    batch.draw(choiceTexture, startSelection.x - i, startSelection.y, 2, 2);  // низ
                } else {
                    batch.draw(choiceTexture, startSelection.x + i, endSelection.y, 2, 2);    // верх
                    batch.draw(choiceTexture, startSelection.x + i, startSelection.y, 2, 2);  // низ
                }
            }
            for (int i = 1; i < Math.abs(endSelection.y - startSelection.y); i++) {
                if (endSelection.y < startSelection.y) {
                    batch.draw(choiceTexture, startSelection.x, startSelection.y - i, 2, 2);  // лево
                    batch.draw(choiceTexture, endSelection.x, startSelection.y - i, 2, 2);    // право
                } else {
                    batch.draw(choiceTexture, startSelection.x, startSelection.y + i, 2, 2);  // лево
                    batch.draw(choiceTexture, endSelection.x, startSelection.y + i, 2, 2);    // право
                }
            }
        }
    }

    // метод выдающий по порядку скрины анимации
    private TextureRegion getCurrentFrame(TextureRegion [] textureRegions) {
        int frameIndex = (int) (moveTimer / timePerFrame) % textureRegions.length;
        return textureRegions[frameIndex];
    }

    public void upDate(float dt) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());   //определение координат мыши в зависимости от маштабирования экрана
        ScreenManager.getInstance().getViewport().unproject(mouse);
        moveTimer +=dt;
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j].upDate(dt);
            }
        }

    }
}
