package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.recursivecorruption.thelastblue.graphics.GameMenu;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

public class TheLastBlueGame implements ApplicationListener {
    private static GameState state = GameState.MENU;
    private Preferences prefs;
    private OrthographicCamera cam;
    private Renderer renderer;
    private SoundManager soundManager;
    private World world;
    GameMenu mainMenu;

    private static GameMenu createMainMenu() {
        GameMenu menu = new GameMenu();
        menu.addTitle("The Last Blue");
        menu.addButton("Play", GameState.PLAY, Color.ORANGE);
        menu.addButton("Instructions", GameState.PLAY, Color.GREEN);
        menu.calcPositions();
        return menu;
    }

    @Override
    public void create() {
        cam = new OrthographicCamera(Graphics.getSX(), Graphics.getSY());
        cam.setToOrtho(true, Graphics.getSX(), Graphics.getSY());
        renderer = new Renderer(cam);
        soundManager = new SoundManager();
        prefs = Gdx.app.getPreferences("Settings");
        World.init(prefs);
        world = new World();
        Graphics.updateScaleConstant();
        mainMenu = createMainMenu();
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(true, (int) (width * Graphics.getScaleConstant()), (int) (height * Graphics.getScaleConstant()));
        renderer.resize((int) (width * Graphics.getScaleConstant()), (int) (height * Graphics.getScaleConstant()), cam);
        Graphics.updateScaleConstant();
        mainMenu.resize();
    }

    void updateState() {
        GameState newState = state.update(world, mainMenu);
        if (newState != null) {
            state = newState;
            state.onEnter(world, soundManager, mainMenu);
        }
    }

    public void update() {
        InputProcessor.update();
        soundManager.update();
        updateState();
    }

    private void drawBackground() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0.2f, 0.3f, 1f);
    }

    @Override
    public void render() {
        update();
        drawBackground();
        renderer.begin();
        state.render(world, renderer, mainMenu);
        renderer.end();
    }

    @Override
    public void pause() {
        world.updateHighScore();
        World.pause(prefs);
        prefs.flush();
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        renderer.dispose();
        soundManager.dispose();
    }

}