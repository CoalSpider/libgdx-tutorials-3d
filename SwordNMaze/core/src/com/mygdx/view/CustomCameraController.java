/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;

/**
 *
 * @author Ben Norman
 */
public class CustomCameraController extends CameraInputController{
    public CustomCameraController(Camera camera) {
        super(camera);
    }

    // might cause some cache misses
    private final Quaternion rotQCamHelp = new Quaternion();
    @Override
    public void update() {
        float x = 0;
        float z = 0;
        float rotY = 0;
        float rotY2 = camera.view.getRotation(rotQCamHelp).getAngleAround(0, 1, 0);
        float rad = (float) Math.toRadians(rotY2);
        float sin = (float) Math.sin(rad) * 0.1f;
        float cos = (float) Math.cos(rad) * 0.1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            x += sin;
            z -= cos;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            x -= sin;
            z += cos;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            rotY = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            rotY = -1;
        }
        // uses degrees
        camera.rotate(rotY, 0, 1, 0);
        camera.translate(x, 0, z);
    }
}
