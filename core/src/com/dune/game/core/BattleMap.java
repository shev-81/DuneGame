package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.interfaces.GameMap;
import com.dune.game.screens.ScreenManager;

public class BattleMap extends GameObject  implements GameMap {

    private class Cell{
        private int cellX, cellY;
        private int resource;
        private float resourceRegenerate;
        private float resourceRegenerateTime;
        private boolean groundPassable;
        private boolean airPassable;
        private int textureTreeNumber;

        public Cell(int cellX, int cellY) {
            this.cellX = cellX;
            this.cellY = cellY;
            this.groundPassable = true;
            this.airPassable = true;
            this.textureTreeNumber = MathUtils.random(0,4);
            // позиция генерации ресурсов у баз
            if((this.cellX >= 1 && this.cellX <=3 && this.cellY >= 4 && this.cellY <= 5) ||
               (this.cellX <= COLUMNS_COUNT - 2 && this.cellX >= COLUMNS_COUNT - 4 &&
                this.cellY <= ROWS_COUNT - 5 && this.cellY >= ROWS_COUNT - 6)){
                resourceRegenerate = 0.5f;    // MathUtils.random(5.0f)-4.5f;
                resourceRegenerate *= 50.0f;  // скорость регенерации ресурса на карте
                resource = 1;
            }else{
                resourceRegenerate = 0;
                resource = 0;
            }
            if (!((this.cellX >= 0 && this.cellX <= 5 && this.cellY >= 0 && this.cellY <= 8) ||
                (this.cellX <= COLUMNS_COUNT && this.cellX >= COLUMNS_COUNT - 5 &&
                 this.cellY <= ROWS_COUNT && this.cellY >= ROWS_COUNT - 8))) {
                if (resourceRegenerate == 0 && MathUtils.random(1, 100) > 70) {  // интенсивность генерации деревьев
                    this.groundPassable = false;
                }
            }
        }

        private void upDate(float dt){
//            blockGroundCell(1,4);

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
            if(!groundPassable){
                batch.draw(treesTextures[textureTreeNumber],cellX*CELL_SIZE,cellY*CELL_SIZE);
            }
        }

        public void blockGroundCell(int cellX, int cellY) {
            cells[cellX][cellY].blockGroundPass();
        }

        public void unblockGroundCell(int cellX, int cellY) {
            cells[cellX][cellY].unblockGroundPass();
        }

        public void blockAirCell(int cellX, int cellY) {
            cells[cellX][cellY].blockAirPass();
        }

        public void unblockAirCell(int cellX, int cellY) {
            cells[cellX][cellY].unblockAirPass();
        }
        public void blockGroundPass() {
            groundPassable = false;
            resourceRegenerate = 0.0f;
            resource = 0;
        }
        public void blockAirPass() {
            airPassable = false;
        }

        public void unblockGroundPass() {
            groundPassable = true;
        }

        public void unblockAirPass() {
            airPassable = true;
        }

    }
    private TextureRegion grassTexture;
    private TextureRegion choiceTexture;
    private TextureRegion [] spicesTextures;
    private TextureRegion [] clickmouseTextures;
    private TextureRegion [] treesTextures;

    private float moveTimer;
    private float timePerFrame;
    private Cell [][] cells;
    private Vector2 startSelection;
    private Vector2 endSelection;
    private Vector2 mouse;

    public static final int COLUMNS_COUNT = 30;
    public static final int ROWS_COUNT = 20;
    public static final int CELL_SIZE = 80;
    public static final int MAP_WIDTH_PX = COLUMNS_COUNT * CELL_SIZE;
    public static final int MAP_HEIGHT_PX = ROWS_COUNT * CELL_SIZE;

    @Override
    public int getSizeX() {
        return COLUMNS_COUNT;
    }

    @Override
    public int getSizeY() {
        return ROWS_COUNT;
    }

    @Override
    public boolean isCellPassable(int cellX, int cellY, boolean isFlyable) {
        if (cellX < 0 || cellY < 0 || cellX >= COLUMNS_COUNT || cellY >= ROWS_COUNT) {
            return false;
        }
        if (cells[cellX][cellY].groundPassable) {
            return true;
        }
        if (cells[cellX][cellY].airPassable && isFlyable) {
            return true;
        }
        return false;
    }

    @Override
    public int getCellCost(int cellX, int cellY) {
        return 1;
    }

    public BattleMap (GameController gameController){
        super(gameController);
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass2");
        this.choiceTexture = Assets.getInstance().getAtlas().findRegion("choiceline");
        this.treesTextures = Assets.getInstance().getAtlas().findRegion("trees1").split(60, 80)[0];
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
    public int getResourceCount(Vector2 point){
        int cx = (int)point.x / CELL_SIZE;
        int cy = (int)point.y / CELL_SIZE;
        try{
            return cells[cx][cy].resource;
        }catch(ArrayIndexOutOfBoundsException e){}
        return 0;
    }

    //метод возвращает позицию ближайшего месторождения спайса для харвестора
    public Vector2 getResourceNearestPosition(Vector2 hPos){
        int x = -1;
        int y = -1;
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                if(cells[i][j].resource > 0){
                    if(x == -1 && y == -1){
                        x = i;
                        y = j;
                    }else{
                        if(hPos.dst(i*CELL_SIZE,j*CELL_SIZE) < hPos.dst(x*CELL_SIZE,y*CELL_SIZE)){
                            x = i;
                            y = j;
                        }
                    }
                }
            }
        }
        if( x == -1 || y == -1){
            return hPos;
        }
        tmpV.set(x*CELL_SIZE+CELL_SIZE/2, y*CELL_SIZE+CELL_SIZE/2);
        return tmpV;
    }

    // сбор ресурсов на позиции танка на позиции ресурса на карте
    public int harvestResource(Vector2 point, int power){
        int cx = (int)point.x / CELL_SIZE;
        int cy = (int)point.y / CELL_SIZE;
        int value = 0;
        if(cells[cx][cy].resource >= power ){
            value = power;
            cells[cx][cy].resource -= power;
        }else{
            value = cells[cx][cy].resource;
            cells[cx][cy].resource =0;
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
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            batch.draw(getCurrentFrame(clickmouseTextures), mouse.x - 12, mouse.y - 12, 12, 12, 24, 24, 1.2f, 1.2f, 0);
        }
    }


    // метод выдающий по порядку скрины анимации
    private TextureRegion getCurrentFrame(TextureRegion [] textureRegions) {
        int frameIndex = (int) (moveTimer / timePerFrame) % textureRegions.length;
        return textureRegions[frameIndex];
    }

    public void update(float dt) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());   //определение координат мыши в зависимости от маштабирования экрана
        ScreenManager.getInstance().getViewport().unproject(mouse);
        moveTimer +=dt;
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j].upDate(dt);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }
}
