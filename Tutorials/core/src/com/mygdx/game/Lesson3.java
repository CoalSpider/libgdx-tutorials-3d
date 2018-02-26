/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ben Norman
 */
public class Lesson3 extends ApplicationAdapter {

    private Environment environment;

    private PerspectiveCamera cam;
    private CameraInputController camController;

    private ModelBatch modelBatch;
    private List<ModelInstance> instances = new ArrayList<ModelInstance>();

    private AssetManager assets;
    private boolean loading;

    private ModelInstance player;

    private PlayerController pc;

    @Override
    public void create() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(1f, 1f, 1f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        assets = new AssetManager();
        assets.load("crazy_ground.g3db", Model.class);
        loading = true;

        player = new ModelInstance(new ModelBuilder()
        //        .createBox(1, 1, 1, new Material(ColorAttribute.createAmbient(Color.BLACK)), Usage.Normal | Usage.Position)
                .createCylinder(1, 1, 1,10, new Material(ColorAttribute.createAmbient(Color.BLACK)), Usage.Normal | Usage.Position)
        );
        player.transform.translate(0, 1, 0);
        instances.add(player);

        pc = new PlayerController(player);
    }

    private void doneLoading() {
        Model ground = assets.get("crazy_ground.g3db", Model.class);
        ModelInstance groundInstance = new ModelInstance(ground);
        instances.add(groundInstance);
        loading = false;
    }

    @Override
    public void render() {
        if (loading && assets.update()) {
            doneLoading();
        }
        
        pc.update();
        
        camController.update();
        
        Gdx.gl20.glClearColor(0, 0.5f, 1, 1);
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }

    private class PlayerController {

        private ModelInstance player;
        private boolean canJump = true;
        private Vector3 velocity = new Vector3();

        PlayerController(ModelInstance player) {
            this.player = player;
        }

        private float speed = 10;
        public void update() {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                velocity.x = speed*Gdx.graphics.getDeltaTime();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                velocity.x = -speed*Gdx.graphics.getDeltaTime();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocity.z = speed*Gdx.graphics.getDeltaTime();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocity.z = -speed*Gdx.graphics.getDeltaTime();
            }
            if(!canJump && player.transform.getTranslation(new Vector3()).y <= 1f){
                canJump = true;
                velocity.y = 0;
            }
            if (canJump && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                velocity.y = 1;
                canJump = false;
            }
            if(!canJump && velocity.y <= 0.001f){
                velocity.y = -1;
            }
            velocity = velocity.scl(0.9f);
            player.transform.translate(velocity);
        }
    }
}
