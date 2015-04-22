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



import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**Maze logic
 *
 * @author Christos Darisaplis
 * @version 1
 */
public class Maze {
    
    private int rows;//rows of this maze
    private int columns;//columns of this maze
    private MazeBox[][] mazeLogic;//maze data
    private Point start;//startig poiint
    private Point goal;//goal point
    private Point current;//current point
    private ArrayList<MazeBox> solution;//current solution
    private int maxFront;//maximum front size
    private int visited;//number of visited nodes

    /**
     * empty constructor
     */
    public Maze() {
        rows = 0;
        columns = 0;
        maxFront = 0;
        visited = 0;
        mazeLogic = null;
        start = null;
        goal = null;
        current = null;
        solution = null;
    }

    
    
    /**
     * Builds a new empty maze of specific dimensions
     * @param rows number of rows in the maze
     * @param columns number of columns in the maze
     */
    public Maze(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        mazeLogic = new MazeBox[rows][columns];
        for (int i = 0;i< rows;i++){
            for (int j = 0;j< columns;j++){
                mazeLogic[i][j] = new MazeBox();
            }
        }
        maxFront = 0;
        visited = 0;
        start = null;
        goal = null;
        current = null;
        solution = null;
    }
    

    
    /**
     * Builds a new maze from file
     * @param path file path
     */
    public Maze (String path){
        this();
        try (Scanner scanner = new Scanner(new File(path))){
            int input;
            int rows = scanner.nextInt();
            int columns = scanner.nextInt();
            this.rows = rows;
            this.columns = columns;
            
            mazeLogic = new MazeBox[rows][columns];
            for (int i = 0;i< rows;i++){
                for (int j = 0;j< columns;j++){
                    mazeLogic[i][j] = new MazeBox();
                }
            }
            
            for (int i = 0;i< rows;i++){
                for (int j = 0;j< columns;j++){
                    if (scanner.hasNextInt()){
                        input = scanner.nextInt();
                        if (input == 0){
                            mazeLogic[i][j].isObstacle(false);
                        }
                        else if (input == 1){
                            mazeLogic[i][j].isObstacle(false);
                            start = new Point(i, j);
                        }
                        else if (input == 2){
                            mazeLogic[i][j].isObstacle(false);
                            goal = new Point(i, j);
                        }
                        else{
                            mazeLogic[i][j].isObstacle(true);
                        }
                                    
                        
                    }
                }
            }

            
            
        } catch (IOException e) {
            System.out.println("Input issue!");
        }
    }
    
    /**
     * Saves this maze to a text file
     * @param path file path
     * @return true if completed without IO errors
     */
    public boolean saveMaze (String path){

        try (PrintWriter printer = new PrintWriter(new FileWriter(new File(path)) {
        })) {
            
            
            printer.println(rows);
            printer.println(columns);
            
            
            
            
            for (int i = 0;i< rows;i++){
                for (int j = 0;j< columns;j++){
                    if (start != null && start.x == i && start.y == j){
                        printer.print("1 ");
                    }
                    else if (goal != null && goal.x == i && goal.y == j){
                        printer.print("2 ");
                    }
                    else if (mazeLogic[i][j].isObstacle()){
                        printer.print("3 ");
                    }
                    else{
                        printer.print("0 ");
                    }
                }
                printer.println();
            }
            
        } catch (Exception e) {
            System.out.println("Output issue!");
            return false;
        }


        return true;
    }
    
    
    /**
     * Set if a cell is obstacle
     * @param x row
     * @param y column
     * @param obstacle obstacle or not 
     */
    public void isObstacle(int x, int y, boolean obstacle){
        mazeLogic[x][y].isObstacle(obstacle);
    }
    
    /**
     * Set start of this maze
     * @param x row
     * @param y column
     */
    public void setStart(int x, int y){
        if (start != null){
            start.x = x;
            start.y = y;
        }
        else{
            start = new Point(x, y);
        }
    }
    
    /**
     * Set goal of this maze
     * @param x row 
     * @param y column
     */
    public void setGoal(int x, int y){
        if (goal != null){
            goal.x = x;
            goal.y = y;
        }
        else{
            goal = new Point(x, y);
        }
    }
    
    /**
     * gets rows
     * @return rows
     */
    public int getRows(){
        return rows;
    }
    
    /**
     * gets columns
     * @return columns
     */
    public int getColumns(){
        return columns;
    }
    
    /**
     * get an array with all the maze's cells
     * @return 2d array
     */
    public MazeBox[][] getMazeLogic(){
        return mazeLogic;
    }
    
    /**
     * gets maze start
     * @return maze start
     */
    public Point getStart(){
        return start;
    }
    
    /**
     * gets maze goal
     * @return maze goal
     */
    public Point getGoal(){
        return goal;
    }
    

    /**
     * sets current solution
     * @param solution new solution
     */
    public void setSolution(ArrayList<MazeBox> solution) {
        this.solution = solution;
    }
    
    /**
     * Sets all cells as obstacles
     */
    public void blacken(){
        for (int i = 0;i< rows;i++){
            for (int j = 0;j< columns;j++){
                mazeLogic[i][j].isObstacle(true);
            }
        }
        start = null;
        goal = null;
    }
    
    /**
     * Clears this maze
     */
    public void whiten(){
        for (int i = 0;i< rows;i++){
            for (int j = 0;j<columns;j++){
                mazeLogic[i][j].isObstacle(false);
            }
        }
        start = null;
        goal = null;
    }
    
    /**
     * Gets max front
     * @return max front
     */
    public int getMaxFront(){
        return maxFront;
    }
    
    /**
     * Sets max front
     * @param maxFront new max front
     */
    public void setMaxFront(int maxFront){
        this.maxFront = maxFront;
    }
    
    /**
     * Calculates max front
     */
    public void calculateMaxFront(){
        int newFront = 0;
        for (int i = 0;i< rows;i++){
            for (int j = 0;j< columns;j++){
                if (mazeLogic[i][j].isIsFront()){
                    newFront++;
                }
            }
        }
        if (newFront > maxFront){
            maxFront = newFront;
        }
    }
    
    public void setVisited(int visited){
        this.visited = visited;
        
    }
    
    public int getVisited(){
        return this.visited;
    }
    
    public void setStart(Point newStartPoint){
        start = newStartPoint;
    }
    
    public void setGoal(Point newGoalPoint){
        goal = newGoalPoint;
    }
    
        public ArrayList<MazeBox> getSolution() {
        return solution;
    }
        
    public Point getCurrent() {
        return current;
    }

    public void setCurrent(Point current) {
        this.current = current;
    }

}
