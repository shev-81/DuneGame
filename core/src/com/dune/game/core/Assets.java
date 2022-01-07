package com.dune.game.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {

    private static final Assets ourInstance = new Assets();
    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    private Assets() {
        assetManager = new AssetManager();
    }
    public static Assets getInstance() {
        return ourInstance;
    }

    public TextureAtlas getAtlas() {
        return textureAtlas;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void loadAssets() {
        assetManager.load("game.pack", TextureAtlas.class);
        assetManager.finishLoading();
        textureAtlas = assetManager.get("game.pack");
    }

//    public void createStandardFont(int size) {
//        FileHandleResolver resolver = new InternalFileHandleResolver();
//        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
//        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
//        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
//        fontParameter.fontFileName = "fonts/gomarice.ttf";
//        fontParameter.fontParameters.size = size;
//        fontParameter.fontParameters.color = Color.WHITE;
//        fontParameter.fontParameters.borderWidth = 1;
//        fontParameter.fontParameters.borderColor = Color.BLACK;
//        fontParameter.fontParameters.shadowOffsetX = 1;
//        fontParameter.fontParameters.shadowOffsetY = 1;
//        fontParameter.fontParameters.shadowColor = Color.BLACK;
//        assetManager.load("fonts/zorque" + size + ".ttf", BitmapFont.class, fontParameter);
//    }

//    public void makeLinks() {
//        textureAtlas = assetManager.get("images/game.pack", TextureAtlas.class);
//    }

    public void clear() {
        assetManager.clear();
    }
}