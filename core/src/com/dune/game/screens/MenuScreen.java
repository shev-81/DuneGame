package com.dune.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.Assets;

public class MenuScreen extends AbstractScreen {

    private TextureRegion menuOneTexture;
    private TextureRegion menuThowTexture;
    private TextureRegion menuchoiceTexture;
    private TextureRegion tmp1Texture;
    private TextureRegion tmp2Texture;
    private BitmapFont font24;
    private Vector2 mouse;

    public MenuScreen(SpriteBatch batch) {
        super(batch);

    }

    @Override
    public void show() {
        this.menuOneTexture = Assets.getInstance().getAtlas().findRegion("menu");
        this.menuThowTexture = Assets.getInstance().getAtlas().findRegion("menu");
        this.menuchoiceTexture = Assets.getInstance().getAtlas().findRegion("menuchoice");
        this.font24 =  Assets.getInstance().getAssetManager().get("fonts/Roboto-Medium24.ttf");
        this.tmp1Texture = this.menuOneTexture;
        this.tmp2Texture = this.menuThowTexture;
        this.mouse = new Vector2();
        prepareSelection();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(menuOneTexture, Gdx.graphics.getWidth()/2-75, 300);
        font24.draw(batch,"Play game", 0, 340, 1280,1,false);
        batch.draw(menuThowTexture, Gdx.graphics.getWidth()/2-75, 240);
        font24.draw(batch,"Exit game", 0, 280, 1280,1,false);
        batch.end();
    }

    public void update(float dt) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());              // привязка координат курсора мыши к окну в игре
        ScreenManager.getInstance().getViewport().unproject(mouse);
        if (Gdx.input.justTouched()) {
            if(mouse.x > Gdx.graphics.getWidth()/2-75 && mouse.x < Gdx.graphics.getWidth()/2+75 && mouse.y > 300 && mouse.y < 360){
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
            if(mouse.x > Gdx.graphics.getWidth()/2-75 && mouse.x < Gdx.graphics.getWidth()/2+75 && mouse.y > 240 && mouse.y < 300){
                System.exit(0);
            }
        }
    }

    public void prepareSelection() {
        InputProcessor ip = new InputAdapter() { // отвечает за массовое выделение юнитов на карте
            @Override
            public boolean mouseMoved (int screenX, int screenY) {
                int x = screenX;
                int y = Gdx.graphics.getHeight() - screenY;
                if(x > Gdx.graphics.getWidth()/2-75 && x < Gdx.graphics.getWidth()/2+75 && y > 300 && y < 360){
                    menuOneTexture = menuchoiceTexture;
                }else{
                    menuOneTexture = tmp1Texture;
                }
                if(x > Gdx.graphics.getWidth()/2-75 && x < Gdx.graphics.getWidth()/2+75 && y > 240 && y < 300){
                    menuThowTexture = menuchoiceTexture;
                }else{
                    menuThowTexture = tmp2Texture;
                }
                return true;
            }
        };
        Gdx.input.setInputProcessor(ip);
    }
    @Override
    public void dispose() {
    }
}