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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ben Norman
 */
public class Lesson5 extends ApplicationAdapter {

    // lighting
    private Environment environment;
    // camera
    private PerspectiveCamera cam;
    private CameraInputController camController;
    // model stuff
    private ModelBatch modelBatch;
    private List<ModelInstance> instances = new ArrayList<ModelInstance>();
    private ModelInstance player;
    // model loading
    private AssetManager assets;
    private boolean loading;
    // player/camera movement
    private PlayerController pc;
    // bullet
    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private btBroadphaseInterface broadphase;
    private btConstraintSolver constraintSolver;
    private btDynamicsWorld dynamicsWorld;
    // rigid bodies
    private btRigidBody playerBody;
    private btRigidBody groundBody;
    
    @Override
    public void create() {
        // load enviroment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        // setup camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(1f, 1f, 1f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        // setup controller for camera
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
        // load the models
        assets = new AssetManager();
        assets.load("crazy_ground.g3db", Model.class);
        loading = true;
        modelBatch = new ModelBatch();
        player = new ModelInstance(new ModelBuilder()
                .createCapsule(0.5f, 2, 10, new Material(ColorAttribute.createAmbient(Color.BLACK)), Usage.Normal | Usage.Position)
        );
        player.transform.translate(0, 1, 0);
        instances.add(player);
        // setup player/camera movement
        pc = new PlayerController(player);
        // setup bulletphysics
        Bullet.init();
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
        
        btCapsuleShape playerShape = new btCapsuleShape(0.5f, 1);
        float mass = 1;
        Vector3 localInertia = new Vector3();
        playerShape.calculateLocalInertia(mass, localInertia);
        playerBody = new btRigidBody(mass, null, playerShape, localInertia);
        playerBody.setWorldTransform(player.transform);
        playerBody.setFlags(playerBody.getFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        dynamicsWorld.addRigidBody(playerBody);
    }
    
    private void doneLoading() {
        Model ground = assets.get("crazy_ground.g3db", Model.class);
        ModelInstance groundInstance = new ModelInstance(ground);
        instances.add(groundInstance);
        loading = false;
        // load the rigid body
        float mass = 0; // infinite mass
        btBvhTriangleMeshShape groundShape = new btBvhTriangleMeshShape(groundInstance.model.meshParts);
        Vector3 localInertia = new Vector3();
        groundShape.calculateLocalInertia(mass, localInertia);
        // no motion state
        groundBody = new btRigidBody(mass, null, groundShape, localInertia);
        groundBody.setWorldTransform(groundInstance.transform);
        groundBody.setFlags(groundBody.getFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        dynamicsWorld.addRigidBody(groundBody);
    }
    
    @Override
    public void render() {
        if (loading && assets.update()) {
            doneLoading();
        }
        camController.update();
        
        pc.update();
        // prevent the capsule from falling over
        playerBody.setAngularFactor(new Vector3(0, 0, 0));
        
        Vector3 startPos = player.transform.getTranslation(new Vector3());
        
        final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);
        
        playerBody.getWorldTransform(player.transform);
        
        Vector3 endPos = player.transform.getTranslation(new Vector3());
        cam.translate(endPos.sub(startPos));
        cam.update();
        
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
        private float speed = 10f;
        private boolean canJump = true;
        
        PlayerController(ModelInstance player) {
            this.player = player;
        }
        
        public void update() {
            playerBody.activate();
            Vector3 velocity = new Vector3(0, playerBody.getLinearVelocity().y, 0);
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                velocity.x += speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                velocity.x -= speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocity.z += speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocity.z -= speed;
            }
            
            
            // TODO: can jump if in collision with ground
            // hacky but it works for now
            if(player.transform.getTranslation(new Vector3()).y <= 1){
                canJump = true;
            }
            
            if (canJump && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                velocity.y = 10;
                canJump = false;
            }
            playerBody.setLinearVelocity(velocity);
        }
    }
    
    private class SimpleMotionState extends btMotionState {
        
        private final Matrix4 worldTrans = new Matrix4();
        
        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
            this.worldTrans.set(worldTrans);
        }
        
        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
            worldTrans.set(this.worldTrans);
        }
        
    }
    
}
