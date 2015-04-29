package SourcePackage;

/*
 * The MIT License
 *
 * Copyright 2015 Chris Darisaplis
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

import java.awt.Rectangle;



/**Individual maze cell
 *
 * @author Chris Samarinas
 */
public class MazeBox implements Comparable<MazeBox>{
    public boolean isObstacle;
    public boolean isVisited;
    private boolean solution;
    public MazeBox previous;
    public int x;
    public int y;
    public int so_far;
    public double to_go;
    public boolean isAdded;
    private Rectangle cell;
    
    MazeBox(){
        isAdded = false;
        isObstacle = false;
        isVisited = false;
        previous = null;
    }

    @Override
    public int compareTo(MazeBox o) {
        if(so_far+to_go>o.so_far+o.to_go) return 1;
        else if(so_far+to_go==o.so_far+o.to_go) return 0;
        else return -1;
    }
    
    

    public void isObstacle(boolean b) {
        this.isObstacle = b;
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public void putCell(Rectangle cell) {
        this.cell = cell;
    }

    public Rectangle getCell() {
        return cell;
    }

    public boolean isVisited() {
        return isVisited;
    }
    
        public boolean isSolution() {
        return solution;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }
    private boolean front;





    public boolean isIsFront() {
        return front;
    }

    public void setIsFront(boolean isFront) {
        this.front = isFront;
    }
    
}
