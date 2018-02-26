/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *
 * @author Ben Norman
 *
 *
 * The players main weapon
 */
public class Sword {

    private Model swordModel;
    private GameModel swordInstance;
    private Texture texture;

    public void initModel() {
        ModelLoader loader = new ObjLoader();
        swordModel = loader.loadModel(Gdx.files.local("sword4.obj"));
        swordModel.meshes.get(0).scale(0.1f, 0.1f, 0.1f);
        texture = new Texture(Gdx.files.local("sword_tex.png"));

        swordInstance = new GameModel(swordModel);
        swordInstance.materials.get(0).set(new TextureAttribute(TextureAttribute.Diffuse, texture));
    }

    private final Quaternion rotation = new Quaternion();
    private final Vector3 yAxis = new Vector3(0, 1, 0);

    public void pointAtMouse(Camera cam, float mouseX, float mouseY) {
        // get rotation to current mouse location
        Vector3 posA = swordInstance.transform.getTranslation(new Vector3());
        Vector3 posB = cam.unproject(new Vector3(mouseX, mouseY, 1));
        Vector3 dir = posB.sub(posA).nor().scl(1, -1, 1); // flip y axis
        Quaternion rot = new Quaternion().setFromMatrix(new Matrix4().setToRotation(dir, yAxis));

        // get the camera rotation around Y
        float rotY = cam.view.getRotation(rotation).getAngleAround(yAxis);

        // translate 1 unit out from the current camera position
        float rad = (float) Math.toRadians(rotY); // shift right a little
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        Vector3 newPos = new Vector3(sin, 0, -cos).scl(0.20f).add(cam.position);

        // negate camera rotation (will be used for edge allign)
        // then shift downward
        // then point at the mouse
        // then translate in front of camera
        Vector3 yShift = new Vector3(0, 0, 0);
        swordInstance.transform.idt().translate(newPos).rotate(rot).translate(yShift).rotate(yAxis, -rotY);
    }

    public GameModel getModelInstance() {
        return this.swordInstance;
    }

    public Texture getTexture() {
        return texture;
    }

    public void onDispose() {
        swordModel.dispose();
    }
}
