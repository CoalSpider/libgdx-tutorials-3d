/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.ai;

import com.mygdx.util2.TreeNode;
import com.mygdx.maze.MazeCell;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ben Norman
 * 
 * Coordinates AI throughout the maze 
 * 
 */
public class TheCoordinator {
    TreeNode<MazeCell> currentTree;
    TheCoordinator(TreeNode<MazeCell> currentTree){
        this.currentTree = currentTree;
    }
    
    List<TreeNode<MazeCell>> getPathChoices(TreeNode<MazeCell> current){
        if(current.isLeaf()){
            List<TreeNode<MazeCell>> list = new ArrayList<TreeNode<MazeCell>>();
            list.add(current.getParent());
            return list;
        } else {
            return current.getChildren();
        }
    }
}
