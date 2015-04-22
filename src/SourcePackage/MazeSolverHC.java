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

import java.util.ArrayList;

/**
 * DFS maze solver
 * @author Chris Samarinas
 */
public final class MazeSolverHC extends MazeSearch{

    private final ArrayList<MazeBox> front;
    
    /**
     * DFS initialization
     * @param mazeInput
     * @param randomStep choose random neighboring MazeBox
     */
    MazeSolverHC(int mazeInput[][], Maze mazeData) {
        super(mazeInput, mazeData);
        front = new ArrayList<>();
        addFront(x, y);
    }
    
    @Override
    public boolean nextStep(int speed) throws InterruptedException{
        if(speed>0) Thread.sleep(speed);
        if(!front.isEmpty()){
            MazeBox box = front.get(0);
            front.remove(0);
            if(box.isVisited){
                return nextStep(0);
            }
            
            visit(box.x, box.y);
            
            if(isSolved()){
                return false;
            }
            
            int minDistance = width*height;
            int distance = manhattanDistance(x+1,y);
            int next_x = -1, next_y = -1;
            if(distance<minDistance && validPosition(x+1,y)){
                minDistance = distance;
                next_x = x+1;
                next_y = y;
            } 
            distance = manhattanDistance(x,y-1);
            if(distance<minDistance && validPosition(x,y-1)){
                minDistance = distance;
                next_x = x;
                next_y = y-1;
            } 
            distance = manhattanDistance(x,y+1);
            if(distance<minDistance && validPosition(x,y+1)){
                minDistance = distance;
                next_x = x;
                next_y = y+1;
            }
            distance = manhattanDistance(x-1,y);
            if(distance<minDistance && validPosition(x-1,y)){
                next_x = x-1;
                next_y = y;
            }
            addFront(next_x, next_y); 
            
            return true;
        }
        return false;
    }
    
    /**
     * Return the manhattan distance from the end
     * @param x MazeBox x coordinate
     * @param y MazeBox y coordinate
     * @return distance
     */
    private int manhattanDistance(int x, int y){
        return Math.abs(x-end_x)+Math.abs(y-end_y);
    }
    
    @Override
    protected void addFront(int x, int y){
        if(validPosition(x, y)){
            if(step>0){
                maze[y][x].previous = maze[this.y][this.x];
            }
            front.add(maze[y][x]);
            int fsize = front.size();
            if(fsize>maxFront){
                maxFront = fsize;
            }
            super.addFront(x, y);
        }
    }
}
