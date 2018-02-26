package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.ai.SimpleAI;
import com.mygdx.collision.GameCollision;
import com.mygdx.maze.Maze;
import com.mygdx.maze.MazeCell;
import com.mygdx.model.GameModel;
import com.mygdx.model.Sword;
import com.mygdx.util2.TreeNode;
import com.mygdx.view.CustomCameraController;
import com.mygdx.view.RenderData;
import java.util.ArrayList;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter {

    // camera
    public PerspectiveCamera cam;
    public CustomCameraController camController;
    public ModelBatch modelBatch;
    // models
    public Model simpleAI;
    public List<GameModel> instances;
    //lighting
    public Environment environment;

    // ai
    private SimpleAI ai;
    // the current maze
    private Maze maze;
    // renders fps and visible obejcts
    private RenderData data;

    // player weapon
    private Sword sword;
    private GameModel swordI;

    private GameModel marble;
    private GameCollision col;

    @Override
    public void create() {
        col = new GameCollision();
        col.onCreate();

        initEnviroment();
        initCamera();

        maze = new Maze(5, 5);

        data = new RenderData();
        data.onCreate();

        modelBatch = new ModelBatch();
        instances = new ArrayList<GameModel>();
        instances.addAll(maze.getMazeParts());

        initAI();

        sword = new Sword();
        sword.initModel();
        swordI = sword.getModelInstance();
        instances.add(swordI);

        marble = col.getModelInstance();
        instances.add(marble);

        for (btRigidBody body : maze.getMazeCollisionParts()) {
                col.addObject(body);
        }
    }

    private void initCamera() {
        // 67
        cam = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //am.position.set(10f, Settings.MAZE_SCALE/4f, 10f);
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.up.set(0, 1, 0);
        cam.near = 0.01f;
        //  cam.far = 300f;
        cam.far = 50f;
        cam.update();

        camController = new CustomCameraController(cam);
        Gdx.input.setInputProcessor(camController);
    }

    private PointLight pLight;
    private DirectionalLight dLight;

    private void initEnviroment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        dLight = new DirectionalLight().set(Color.RED, new Vector3(1, 1, 1));
        environment.add(dLight);

        pLight = new PointLight();
        pLight.color.add(1, 1, 1, 1);
        pLight.position.set(0, 0, 0);
        pLight.intensity = 10;
        environment.add(pLight);
    }

    private GameModel aimodel;

    private void initAI() {
        ModelBuilder mb = new ModelBuilder();
        simpleAI = mb.createBox(0.25f, 2f, 0.25f,
                new Material(ColorAttribute.createDiffuse(Color.PURPLE)),
                Usage.Position | Usage.Normal);

        aimodel = new GameModel(simpleAI);
        instances.add(aimodel);
        TreeNode<MazeCell> tree = maze.getMazeAsTree();
        ai = new SimpleAI(tree, tree.getChildren().get(0));
    }

    @Override
    public void render() {
        /*
        * =================================================
        * UPDATE
        *=================================================
         */
        ai.update();
        ModelInstance mi = instances.get(instances.indexOf(aimodel));
        mi.transform.setTranslation(ai.getPosition().y, 1, ai.getPosition().x);
        Vector3 t = new Vector3();
        mi.transform.getTranslation(t);
        pLight.setPosition(t);
        
        marble.transform.getTranslation(cam.position);

        camController.update();
        cam.update();

        sword.pointAtMouse(cam, Gdx.input.getX(), Gdx.input.getY());
        /*
        * =================================================
        * COLLISION
        *=================================================
         */
        final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());

        col.step(delta, 5, 1f / 60f);

        /*
        * =================================================
        * RENDER
        *=================================================
         */
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glCullFace(GL20.GL_BACK);

        sword.getTexture().bind();

        modelBatch.begin(cam);
        for (GameModel i : instances) {
          //  if (i.isVisibleByCamera(cam)) {
                modelBatch.render(i, environment);
                data.setVisibleCount(data.getVisibleCount() + 1);
           // }
        }
        modelBatch.end();

        data.onDraw();
        data.setVisibleCount(0);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();

        simpleAI.dispose();

        // clean up collision objects
        for (btCollisionObject i : maze.getMazeCollisionParts()) {
            i.dispose();
        }

        // clean up game models
        for (GameModel gm : maze.getMazeParts()) {
            gm.dispose();
        }

        // clean up bullet physics
        col.onDispose();
    }
}
