package com.dune.game.screens;

import com.badlogic.gdx.Gdx;
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
    private TextureRegion startScreenTexture;
    private TextureRegion tmp1Texture;
    private TextureRegion tmp2Texture;
    private BitmapFont font24;
    private Vector2 mouse;

    public MenuScreen(SpriteBatch batch) {
        super(batch);

    }

    @Override
    public void show() {

        this.startScreenTexture = Assets.getInstance().getAtlas().findRegion("dunestart");
        this.menuOneTexture = Assets.getInstance().getAtlas().findRegion("menu");
        this.menuThowTexture = Assets.getInstance().getAtlas().findRegion("menu");
        this.menuchoiceTexture = Assets.getInstance().getAtlas().findRegion("menuchoice");
        this.font24 =  Assets.getInstance().getAssetManager().get("fonts/Roboto-Medium24.ttf");
        this.tmp1Texture = this.menuOneTexture;
        this.tmp2Texture = this.menuThowTexture;
        this.mouse = new Vector2();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(startScreenTexture,250,300);
        batch.draw(menuOneTexture, 1280/2-75, 200);
        font24.draw(batch,"Play game", 0, 240, 1280,1,false);
        batch.draw(menuThowTexture, 1280/2-75, 140);
        font24.draw(batch,"Exit game", 0, 180, 1280,1,false);
        batch.end();
    }

    public void update(float dt) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());              // привязка координат курсора мыши к окну в игре
        ScreenManager.getInstance().getViewport().unproject(mouse);
        int x = (int)mouse.x;
        int y = (int)mouse.y;
        if(x > 1280/2-75 && x < 1280/2+75 && y > 200 && y < 260){
            menuOneTexture = menuchoiceTexture;
        }else{
            menuOneTexture = tmp1Texture;
        }
        if(x > 1280/2-75 && x < 1280/2+75 && y > 140 && y < 200){
            menuThowTexture = menuchoiceTexture;
        }else{
            menuThowTexture = tmp2Texture;
        }
        if (Gdx.input.justTouched()) {
            if(mouse.x > 1280/2-75 && mouse.x < 1280/2+75 && mouse.y > 200 && mouse.y < 260){
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
            if(mouse.x > 1280/2-75 && mouse.x < 1280/2+75 && mouse.y > 140 && mouse.y < 200){
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void dispose() {
    }
}