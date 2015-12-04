package SourcePackage;



/*
 * The MIT License
 *
 * Copyright 2015 Chris Samarinas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

/**
 * MazeSearch solving algorithm structure
 * @author Chris Samarinas
 */
public abstract class MazeSearch {
    
    protected int x, y; // current position
    protected int end_x, end_y; // end position
    protected MazeBox[][] maze; // the maze boxes
    protected int width, height; // maze dimensions
    protected int step; // solver step
    protected ArrayList<MazeBox> solution; // maze solution
    protected int maxFront; // max front set size
    protected Maze mazeData;
    
    /**
     * Create maze from input
     * @param mazeInput 2D array with 0 and 1 for obstacles
     * @param x start x coordinate
     * @param y start y coordinate
     */
    MazeSearch(int[][] mazeInput, Maze mazeData){
        x= -1;
        y = -1;
        end_x = -1;
        end_y = -1;
        maxFront = 0;
        solution = new ArrayList<>();
        step = 0;
        width = mazeInput[0].length;
        height = mazeInput.length;
        maze = new MazeBox[height][width];
        this.mazeData = mazeData;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                maze[i][j] = new MazeBox();
                maze[i][j].setIsObstacle(mazeInput[i][j]==3);
                maze[i][j].x = j;
                maze[i][j].y = i;
                if(mazeInput[i][j]==1){
                    x = j;
                    y = i;
                }else if(mazeInput[i][j]==2){
                    end_x = j;
                    end_y = i;
                }
            }
        }
    }
    
    /**
     * Perform the next step of search
     * @param speed in milliseconds
     * @return true if step performed
     * @throws java.lang.InterruptedException
     */
    public abstract boolean nextStep(int speed) throws InterruptedException;
    
    /**
     * Returns the number of steps
     * @return number of steps
     */
    public int getSteps(){
        return step;
    }
    
    /**
     * Returns the max front set size
     * @return max front set size
     */
    public int getMaxFront(){
        return maxFront;
    }
    
    /**
     * Visit MazeBox in x, y position
     * @param x MazeBox x coordinate
     * @param y MazeBox y coordinate
     * @return true if MazeBox is visited
     */
    protected boolean visit(int x, int y){
        if(!validPosition(x, y)){
            return false;
        }
        this.x = x;
        this.y = y;
        maze[y][x].isVisited = true;
        step++;
        //GUI
        mazeData.getMazeLogic()[y][x].isVisited = true;
        mazeData.getMazeLogic()[y][x].setIsFront(false);
        mazeData.setCurrent(new Point(y, x));
        return true;
    }
    
    /**
     * If MazeBox can be visited in x, y position
     * @param x MazeBox x coordinate
     * @param y MazeBox y coordinate
     * @return true if MazeBox can be visited in x, y position
     */
    protected boolean validPosition(int x, int y){
        return x>=0 && x<width && y>=0 && y<height && !maze[y][x].isObstacle();
    }
    
    /**
     * Add MazeBox in x, y position to front set
     * @param x MazeBox x coordinate
     * @param y MazeBox y coordinate
     */
    protected void addFront(int x, int y){
        //GUI
        mazeData.getMazeLogic()[y][x].setIsFront(true);
    }
    
    /**
     * Get current maze solution
     * @return ArrayList with current MazeBox solution (MazeBox items)
     */
    public ArrayList<MazeBox> getSolution(){
        solution.clear();
        if(step==0) return null;
        MazeBox box = maze[y][x];
        int c = 0;
        while(c<2){
            solution.add(box);
            if(box!=null) box = box.previous;
            if(box==null || box.previous==null){
                c++;
            }
        }
        Collections.reverse(solution);
        return solution;
    }
    
    /**
     * Solve the maze
     * @param speed
     * @return
     * @throws InterruptedException 
     */
    public ArrayList<MazeBox> solve(int speed) throws InterruptedException{
        while(nextStep(speed)){
            // continue tree search
        }
        if(isSolved()) return getSolution();
        return null;
    }
    
    /**
     * Checks if the maze is solved
     * @return true if solution is found
     */
    public boolean isSolved(){     
        return x==end_x && y==end_y;
    }
}
