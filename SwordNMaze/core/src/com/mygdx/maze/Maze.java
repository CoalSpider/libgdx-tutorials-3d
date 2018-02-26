/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.maze;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.model.GameModel;
import com.mygdx.util2.TreeNode;
import java.util.List;

/**
 *
 * @author Ben Norman
 *
 * For now this is just a wrapper around a maze generator and maze model builder
 */
public class Maze {

    private final MazeGenerator generator;
    private final MazeModelBuilder modelBuilder;
    private List<GameModel> mazeRenderParts;
    private List<btRigidBody> mazeCollisionParts;

    public Maze(int rows, int columns) {
        generator = new MazeGenerator(rows, columns);
        generator.generate(); // should be plenty fast to generate without lag
        modelBuilder = new MazeModelBuilder();
    }

    public Maze(int rows, int columns, int seed) {
        generator = new MazeGenerator(rows, columns, seed);
        generator.generate(); // should be plenety fast to generate without lag
        modelBuilder = new MazeModelBuilder();
    }

    public int getRows() {
        return generator.getRows();
    }

    public int getColumns() {
        return generator.getColumns();
    }

    public TreeNode<MazeCell> getMazeAsTree() {
        return generator.getMazeAsTree();
    }

    public MazeCell[][] getMazeAsGrid() {
        return generator.getMazeAsGrid();
    }

    public List<GameModel> getMazeParts() {
        if (mazeRenderParts == null) {
            initModelsAndCollidables();
        }
        return mazeRenderParts;
    }

    public List<btRigidBody> getMazeCollisionParts() {
        if (mazeCollisionParts == null) {
            initModelsAndCollidables();
        }
        return mazeCollisionParts;
    }

    /**
     * slow operation the first time its called
     */
    private void initModelsAndCollidables() {
        modelBuilder.loadMergedInstances(getMazeAsGrid());
        mazeRenderParts = modelBuilder.getMazeParts();
        mazeCollisionParts = modelBuilder.getMazeCollides();
    }
}
