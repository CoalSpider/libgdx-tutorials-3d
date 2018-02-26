/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.mygdx.model.GameModel;

/**
 *
 * @author Ben Norman
 */
public class GameCollision {

    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private btBroadphaseInterface broadphase;
    private btDynamicsWorld dynamicsWorld;
    private btConstraintSolver constraintSolver;

    private Model collisionTesterModel;
    private GameModel collisionTester;
    private btRigidBody collisionTesterBody;
    
    private btRigidBody floor;

    public void onCreate() {
        Bullet.init();
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
//        dynamicsWorld.setGravity(new Vector3(0, 0, 0));

        collisionTesterBody = createTestBody();
        collisionTesterBody.setWorldTransform(collisionTester.transform);
        collisionTesterBody.setFlags(collisionTesterBody.getFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        addObject(collisionTesterBody);
        
        floor = createFloor();
        floor.setWorldTransform(new Matrix4().idt().translate(50, 0, 50));
        addObject(floor);
    }
    
    private btRigidBody createFloor(){
        float mass = 0;
        btBoxShape shape = new btBoxShape(new Vector3(50,0.05f,50));
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);
        return new btRigidBody(mass, null, shape, localInertia);
    }

    private btRigidBody createTestBody() {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        MeshPartBuilder builder = mb.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material());
        SphereShapeBuilder.build(builder, 1, 1, 1, 10, 10);
        collisionTesterModel = mb.end();
        
        collisionTester = new GameModel(collisionTesterModel);
        collisionTester.transform.translate(3, 2, 3);
        
        float mass = 1f;
        btSphereShape shape = new btSphereShape(0.5f);
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);
        return new btRigidBody(mass, null, shape, localInertia);
    }

    public GameModel getModelInstance() {
        return collisionTester;
    }

    public void step(float timeStep, int maxSubSteps, float fixedTimesteps) {
        
        collisionTesterBody.activate();
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)){collisionTesterBody.applyCentralImpulse(new Vector3(1,0,0));}
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)){collisionTesterBody.applyCentralImpulse(new Vector3(0,0,1));}
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)){collisionTesterBody.applyCentralImpulse(new Vector3(0,0,-1));}
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)){collisionTesterBody.applyCentralImpulse(new Vector3(-1,0,0));}
        
        dynamicsWorld.stepSimulation(timeStep, maxSubSteps, fixedTimesteps);

        collisionTesterBody.getWorldTransform(collisionTester.transform);

    }

    public void onDispose() {
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();

        collisionTesterModel.dispose();
    }

    public void addObject(btRigidBody body) {
        dynamicsWorld.addRigidBody(body);
    }

    public void addObject(btRigidBody body, int group, int mask) {
        dynamicsWorld.addRigidBody(body, group, mask);
    }
    
    public static btRigidBody createStaticBox(Matrix4 transform, BoundingBox box){
        float mass = 0f; // 0 mass is static
        btBoxShape shape = new btBoxShape(new Vector3(box.getWidth() / 2f, box.getHeight() / 2f, box.getDepth() / 2f));
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);
        btRigidBody body = new btRigidBody(mass, null, shape, localInertia);
        
        body.setWorldTransform(transform);
        
        body.setFlags(body.getFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        
        return body;
    }
}
