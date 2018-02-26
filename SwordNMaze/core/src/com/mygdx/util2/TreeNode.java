package com.mygdx.util2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben Norman on 1/28/2018.
 */
public class TreeNode<T> {

    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;

    public TreeNode() {
        // default no-args constructor
    }

    public TreeNode(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }

    public void addChild(TreeNode<T> child) {
        child.setParent(this);
        if (children == null) {
            children = new ArrayList<TreeNode<T>>();
        }
        children.add(child);
    }

    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
    
    public boolean isRoot(){
        return parent==null;
    }
}
