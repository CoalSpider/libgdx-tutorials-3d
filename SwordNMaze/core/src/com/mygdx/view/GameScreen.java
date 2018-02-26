/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.view;

import com.badlogic.gdx.Screen;

/**
 *
 * @author Ben Norman
 */
public class GameScreen implements Screen {

    @Override
    public void show() {
        System.out.println("show");
    }

    @Override
    public void render(float delta) {
        System.out.println("render " + delta);
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("resize W:" + width + " H:" + height);
    }

    @Override
    public void pause() {
        System.out.println("pause");
    }

    @Override
    public void resume() {
        System.out.println("resume");
    }

    @Override
    public void hide() {
        System.out.println("hide");
    }

    @Override
    public void dispose() {
        System.out.println("dispose");
    }

}
