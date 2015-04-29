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
import java.util.Collections;

/**
 * DFS maze solver
 * @author Chris Samarinas
 */
public final class MazeSolverDBFS extends MazeSearch{

    private final ArrayList<MazeBox> front;
    private final boolean randomStep;
    private final boolean dfs;
    
    /**
     * DFS initialization
     * @param mazeInput
     * @param randomStep choose random neighboring MazeBox
     * @param dfs true: use DFS, false: use BFS
     */
    MazeSolverDBFS(int mazeInput[][], boolean randomStep, boolean dfs) {
        super(mazeInput);
        front = new ArrayList<>();
        this.randomStep = randomStep;
        this.dfs = dfs;
        addFront(x, y);
    }
    
    @Override
    public boolean nextStep(int speed) throws InterruptedException{
        if(speed>0) Thread.sleep(speed);
        if(!front.isEmpty()){
            MazeBox box;
            if(dfs){
                box = front.get(front.size()-1);
                front.remove(front.size()-1);
            }else{
                box = front.get(0);
                front.remove(0);
            }
            
            if(box.isVisited){
                return nextStep(0);
            }
            
            visit(box.x, box.y);
            
            if(isSolved()){
                return false;
            }
            
            ArrayList<Integer> directions = new ArrayList<>();
            directions.add(0); // right
            directions.add(1); // top
            directions.add(2); // bottom
            directions.add(3); // left
            if(randomStep){
                Collections.shuffle(directions);
            }
            
            for(int i=0;i<4;i++){              
                int direction = directions.get(i);
                if(direction==0) addFront(x+1, y);
                else if(direction==1) addFront(x, y-1);
                else if(direction==2) addFront(x, y+1);
                else addFront(x-1, y);
            }            
            return true;
        }
        return false;
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
