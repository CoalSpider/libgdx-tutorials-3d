/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.collision.GameCollision;
import com.mygdx.model.GameModel;
import java.util.ArrayList;
import java.util.List;

import static com.mygdx.util2.Settings.*;

/**
 *
 * @author Ben Norman
 *
 * Your one stop shop for all game models
 */
class MazeModelBuilder {

    private static final float WALL_THICKNESS = 0.1f * MAZE_SCALE;
    private static final float HEIGHT_SHIFT = 1.0f * MAZE_SCALE * 0.5f;
    private static final float SHIFT_X = MAZE_SCALE * 0.5f - WALL_THICKNESS * 0.5f;
    private static final float SHIFT_Z = MAZE_SCALE * 0.5f - WALL_THICKNESS * 0.5f;

    private final ModelLoader loader = new ObjLoader();
    private Model bottomWall;
    private Model rightWall;
    private Model cornerWall;

    private BoundingBox bottomWallBox;
    private BoundingBox rightWallBox;
    private BoundingBox cornerWallBox;

    private final Material mat = new Material(ColorAttribute.createDiffuse(Color.GRAY));
    private final long attributes = Usage.Position | Usage.Normal;
    private final int primitiveType = GL20.GL_TRIANGLES;

    private final List<GameModel> mazeParts = new ArrayList<GameModel>();

    private final List<btRigidBody> rigidBodies = new ArrayList<btRigidBody>();

    void loadMergedInstances(MazeCell[][] grid) {
        mazeParts.clear();

        bottomWall = loader.loadModel(Gdx.files.local("bottomWall.obj"));
        rightWall = loader.loadModel(Gdx.files.local("rightWall.obj"));
        cornerWall = loader.loadModel(Gdx.files.local("cornerWall.obj"));

        bottomWall.meshes.get(0).scale(MAZE_SCALE, MAZE_SCALE, MAZE_SCALE);
        rightWall.meshes.get(0).scale(MAZE_SCALE, MAZE_SCALE, MAZE_SCALE);
        cornerWall.meshes.get(0).scale(MAZE_SCALE, MAZE_SCALE, MAZE_SCALE);

        bottomWallBox = bottomWall.calculateBoundingBox(new BoundingBox());
        rightWallBox = rightWall.calculateBoundingBox(new BoundingBox());
        cornerWallBox = cornerWall.calculateBoundingBox(new BoundingBox());

        this.loadMergedInstances(grid, 1);

        // This may be a bad thing to do. Im not sure
        bottomWall.dispose();
        rightWall.dispose();
        cornerWall.dispose();
    }

    /**
     * chunks maze into equal size parts for rendering purposes
     */
    private void loadMergedInstances(MazeCell[][] grid, int size) {
        if (grid.length % size != 0 || grid[0].length % size != 0) {
            throw new IllegalArgumentException("grid len x or len y not divisible by " + size);
        }

        int startI = 0;
        int startJ = 0;
        while (startI < grid.length && startJ < grid[0].length) {
            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            for (int i = startI; i < startI + size; i++) {
                for (int j = startJ; j < startJ + size; j++) {
                    buildCell(modelBuilder, grid, i, j);
                }
            }
            mazeParts.add(new GameModel(modelBuilder.end()));
            if (startI == grid.length - size) {
                startJ += size;
                startI = 0;
            } else {
                startI += size;
            }
        }
        System.out.println("mergedPartsSize == " + mazeParts.size());
    }

    private void buildCell(ModelBuilder modelBuilder, MazeCell[][] grid, int row, int column) {
        float x = row * MAZE_SCALE;
        float z = column * MAZE_SCALE;
        float y = HEIGHT_SHIFT;
        MazeCell cell = grid[row][column];
        MeshPartBuilder meshBuilder;
        String id = cell.getRow() + "" + cell.getColumn();

        if (cell.hasBottomWall()) {
            meshBuilder = modelBuilder.part(id + "Bottom", primitiveType, attributes, mat);
            BoundingBox box = bottomWallBox;
            BoxShapeBuilder.build(meshBuilder, x + SHIFT_X, y, z, box.getWidth(), box.getHeight(), box.getDepth());
            
            // create bodies
            rigidBodies.add(GameCollision.createStaticBox(new Matrix4().translate(x + SHIFT_X, y, z), box));
        }

        if (cell.hasRightWall()) {
            meshBuilder = modelBuilder.part(id + "Right", primitiveType, attributes, mat);
            BoundingBox box = rightWallBox;
            BoxShapeBuilder.build(meshBuilder, x, y, z + SHIFT_Z, box.getWidth(), box.getHeight(), box.getDepth());

            // create bodies
            rigidBodies.add(GameCollision.createStaticBox(new Matrix4().translate(x, y, z + SHIFT_Z), box));
        }

        if (!cell.hasBottomWall() && !cell.hasRightWall()) {
            meshBuilder = modelBuilder.part(id + "Corner", primitiveType, attributes, mat);
            BoundingBox box = cornerWallBox;
            BoxShapeBuilder.build(meshBuilder, x + SHIFT_X, y, z + SHIFT_Z, box.getWidth(), box.getHeight(), box.getDepth());

            // create bodies
            rigidBodies.add(GameCollision.createStaticBox(new Matrix4().translate(x + SHIFT_X, y, z + SHIFT_Z), box));
        }
    }

    List<GameModel> getMazeParts() {
        return mazeParts;
    }

    List<btRigidBody> getMazeCollides() {
        return rigidBodies;
    }
}
