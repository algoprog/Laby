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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**Displays a maze as a colored grid
 * @author Christos Darisaplis
 * @version 1
 */
public class MazePanel extends JPanel{
        private Point selection;//mouse selection
        private boolean needsRedraw;//redraw flag
        private Maze aMaze;//linked maze
        private MouseAdapter mousePainter;//paints maze cells
        private MouseAdapter mouseSelector;//selects maze cells
        private boolean drawArrows;//arrows on solution
        
        /**
         * Default constructor
            * @param aMaze
         */
        public MazePanel (Maze aMaze){
            super();
            selection = null;
            mousePainter = null;
            needsRedraw = true;
            drawArrows = false;
            this.aMaze = aMaze;
            mouseSelector = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int width, height;
                    width = getWidth();
                    height = getHeight();
            
                    int cellWidth, cellHeight;
                    cellWidth = width/aMaze.getColumns();
                    cellHeight = height/aMaze.getRows();
                    int xOff, yOff;
            
                    if (cellWidth< cellHeight){
                        cellHeight = cellWidth;
                    }
                    else{
                        cellWidth = cellHeight;
                    }
            
                    xOff = (width - aMaze.getColumns()*cellWidth)/2;
                    yOff = (height - aMaze.getRows()*cellHeight)/2;
                    
                    if (e.getX()>= xOff && e.getX()<= width - xOff &&
                            e.getY()>= yOff && e.getY()<= height - yOff){
                        selection = new Point();
                        selection.x = (e.getY() - yOff)/cellHeight;
                        selection.y = (e.getX() - xOff)/cellWidth;;
                    }
                    else{
                        selection = null;
                    }

                }
                
                @Override
                public void mouseDragged(MouseEvent e){
                                        int width, height;
                    width = getWidth();
                    height = getHeight();
            
                    int cellWidth, cellHeight;
                    cellWidth = width/aMaze.getColumns();
                    cellHeight = height/aMaze.getRows();
                    int xOff, yOff;
            
                    if (cellWidth< cellHeight){
                        cellHeight = cellWidth;
                    }
                    else{
                        cellWidth = cellHeight;
                    }
            
                    xOff = (width - aMaze.getColumns()*cellWidth)/2;
                    yOff = (height - aMaze.getRows()*cellHeight)/2;
                    
                    if (e.getX()>= xOff && e.getX()<= width - xOff &&
                            e.getY()>= yOff && e.getY()<= height - yOff){
                        selection = new Point();
                        selection.x = (e.getY() - yOff)/cellHeight;
                        selection.y = (e.getX() - xOff)/cellWidth;;
                    }
                    else{
                        selection = null;
                    }
                }
            };
            addMouseMotionListener(mouseSelector);
        }
        
        /**
         * Needs repaint
         */
        @Override
        public void invalidate(){
            selection = null;
            needsRedraw = true;
            super.invalidate();
        }
        

        /**
         * Preffered size
         * @return preferred size
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(15*aMaze.getColumns(), 15*aMaze.getRows());
        }
        
        /**
         * Paint all the maze cells accordingly to linked maze data
         * @param g 
         */
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2D;
            g2D = (Graphics2D)g.create();
            
            int width, height;
            width = getWidth();
            height = getHeight();
            
            int cellWidth, cellHeight;
            cellWidth = width/aMaze.getColumns();
            cellHeight = height/aMaze.getRows();
            int xOff, yOff;
            
            if (cellWidth< cellHeight){
                cellHeight = cellWidth;
            }
            else{
                cellWidth = cellHeight;
            }
            
            xOff = (width - aMaze.getColumns()*cellWidth)/2;
            yOff = (height - aMaze.getRows()*cellHeight)/2;
            
            if (needsRedraw){
                for (int i = 0;i< aMaze.getRows();i++){
                    for (int j = 0;j< aMaze.getColumns();j++){
                        Rectangle cell;
                        cell = new Rectangle(xOff + j*cellWidth,
                               yOff + i*cellHeight, cellWidth, cellHeight);
                        aMaze.getMazeLogic()[i][j].putCell(cell);
                    }
                }
            }
            needsRedraw = false;
            
            if (selection != null){
                
            }
            
            
            for (int i = 0;i< aMaze.getRows();i++){
                for (int j = 0;j< aMaze.getColumns();j++){
                    g2D.setColor(Color.BLACK);
                    g2D.draw(aMaze.getMazeLogic()[i][j].getCell());
                    if (aMaze.getMazeLogic()[i][j].isObstacle()){
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
                    }
                    if (aMaze.getMazeLogic()[i][j].isIsFront()){
                        g2D.setColor(Color.CYAN);
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
                        g2D.setColor(Color.BLACK);
                    }
                    if (aMaze.getMazeLogic()[i][j].isVisited()){
                        g2D.setColor(Color.RED);
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
                        g2D.setColor(Color.BLACK);
                    }
                    
                }
                
            }
            
            
            
            g2D.setColor(Color.GREEN);
            MazeBox previous = null;
            MazeBox aSolutionBox = null;
            if (aMaze.getSolution() != null){
                Iterator<MazeBox> it = aMaze.getSolution().iterator();
                while (it.hasNext()){
                    previous = aSolutionBox;
                    aSolutionBox = it.next();
                     g2D.fill(aMaze.getMazeLogic()[aSolutionBox.y]
                            [aSolutionBox.x].getCell());
                     if (previous != null && drawArrows){
                         g2D.setColor(Color.BLACK);
                         Rectangle cell = aMaze.getMazeLogic()[previous.y][previous.x].getCell();
                         if (previous.x > aSolutionBox.x){
                             g2D.fillPolygon(new int[]{cell.x,
                                 cell.x - cell.width/4, cell.x}, new int[]{
                                 cell.y + cell.height/4, cell.y + cell.height/2,
                                 cell.y + (3*cell.height)/4}, 3
                             );
                         }
                         else if (previous.x< aSolutionBox.x){
                             g2D.fillPolygon(new int[]{cell.x + cell.width,
                                 cell.x + cell.width + cell.width/4, cell.x + cell.width}, new int[]{
                                 cell.y + cell.height/4, cell.y + cell.height/2,
                                 cell.y + (3*cell.height)/4}, 3
                             );
                         }
                         else if (previous.y > aSolutionBox.y){
                             g2D.fillPolygon(new int[]{cell.x + cell.width/4,
                                 cell.x + cell.width/2, cell.x + (3*cell.height)/4}, new int[]{
                                 cell.y, cell.y - cell.height/4,
                                 cell.y}, 3
                             );
                         }
                         else{
                             g2D.fillPolygon(new int[]{cell.x + cell.width/4,
                                 cell.x + cell.width/2, cell.x + (3*cell.height)/4}, new int[]{
                                 cell.y + cell.height, cell.y + cell.height + cell.height/4,
                                 cell.y + cell.height}, 3
                             );
                         }
                         g2D.setColor(Color.GREEN);
                     }
                }
            }
            
            if (aMaze.getCurrent() != null){
                g2D.setColor(Color.MAGENTA);
                g2D.fill(aMaze.getMazeLogic()[aMaze.getCurrent().x]
                        [aMaze.getCurrent().y].getCell());
            }
            
            
            if (aMaze.getStart() != null){
                g2D.setColor(Color.YELLOW);
                g2D.fill(aMaze.getMazeLogic()[aMaze.getStart().x]
                        [aMaze.getStart().y].getCell());
            }
            if (aMaze.getGoal() != null){
                g2D.setColor(Color.BLUE);
                g2D.fill(aMaze.getMazeLogic()[aMaze.getGoal().x]
                        [aMaze.getGoal().y].getCell());
            }
            
            g2D.setColor(Color.BLACK);
            for (int i = 0;i< aMaze.getRows();i++){
                for (int j = 0;j< aMaze.getColumns();j++){
                    g2D.draw(aMaze.getMazeLogic()[i][j].getCell());
                }
            }
            
            
           
            
            
            g2D.dispose();
            
        }
        
        /**
         * Link this panel with a new maze
         * @param aMaze maze to link
         */
        public void setMaze(Maze aMaze){
            removeMouseMotionListener(mouseSelector);
            this.aMaze = aMaze;
            mouseSelector = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int width, height;
                    width = getWidth();
                    height = getHeight();
            
                    int cellWidth, cellHeight;
                    cellWidth = width/aMaze.getColumns();
                    cellHeight = height/aMaze.getRows();
                    int xOff, yOff;
            
                    if (cellWidth< cellHeight){
                        cellHeight = cellWidth;
                    }
                    else{
                        cellWidth = cellHeight;
                    }
            
                    xOff = (width - aMaze.getColumns()*cellWidth)/2;
                    yOff = (height - aMaze.getRows()*cellHeight)/2;
                    
                    if (e.getX()>= xOff && e.getX()<= width - xOff &&
                            e.getY()>= yOff && e.getY()<= height - yOff){
                        selection = new Point();
                        selection.x = (e.getY() - yOff)/cellHeight;
                        selection.y = (e.getX() - xOff)/cellWidth;;
                    }
                    else{
                        selection = null;
                    }

                }
                @Override
                public void mouseDragged(MouseEvent e){
                    
                    int width, height;
                    width = getWidth();
                    height = getHeight();
            
                    int cellWidth, cellHeight;
                    cellWidth = width/aMaze.getColumns();
                    cellHeight = height/aMaze.getRows();
                    int xOff, yOff;
            
                    if (cellWidth< cellHeight){
                        cellHeight = cellWidth;
                    }
                    else{
                        cellWidth = cellHeight;
                    }
            
                    xOff = (width - aMaze.getColumns()*cellWidth)/2;
                    yOff = (height - aMaze.getRows()*cellHeight)/2;
                    
                    if (e.getX()>= xOff && e.getX()<= width - xOff &&
                            e.getY()>= yOff && e.getY()<= height - yOff){
                        selection = new Point();
                        selection.x = (e.getY() - yOff)/cellHeight;
                        selection.y = (e.getX() - xOff)/cellWidth;;
                    }
                    else{
                        selection = null;
                    }
                }
            };
            addMouseMotionListener(mouseSelector);
            invalidate();
        }
        
        /**
         * Returns linked maze's data as a 2d array
         * @return  2d array
         */
        public int[][] getMazeData(){
            int[][] mazeData = new int[aMaze.getRows()][aMaze.getColumns()];
            for (int i = 0;i< aMaze.getRows();i++){
                for (int j = 0;j< aMaze.getColumns();j++){
                    mazeData[i][j] = 0;
                    if (aMaze.getStart().x == i && aMaze.getStart().y == j){
                        mazeData[i][j] = 1;
                    }
                    if (aMaze.getGoal().x == i && aMaze.getGoal().y == j){
                        mazeData[i][j] = 2;
                    }
                    if (aMaze.getMazeLogic()[i][j].isObstacle()){
                        mazeData[i][j] = 3;
                    }
                }
            }
            return mazeData;
        }
        
        public Maze getMaze(){
            return aMaze;
        }
        
        /**
         * Transforms all cells to obstacles
         */
        public void blacken(){
            if (aMaze.getRows()%2 == 0 && aMaze.getColumns()%2 == 0){
                aMaze = new Maze(aMaze.getRows()+1, aMaze.getColumns()+1);
            }
            if (aMaze.getRows()%2 == 0){
                aMaze = new Maze(aMaze.getRows()+1, aMaze.getColumns());
            }
            if (aMaze.getColumns()%2 == 0){
                aMaze = new Maze(aMaze.getRows(), aMaze.getColumns()+1);
            }
            this.invalidate();
            aMaze.blacken();
        }
        
        /**
         * Sets mouse painter to edit this maze
         * @param modification modification identifier
         */
        public void setMousepainter(int modification){
            removeMouseListener(mousePainter);
            removeMouseMotionListener(mousePainter);
            mousePainter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e){
                    if (selection != null){
                        if (modification == 0){
                            if (SwingUtilities.isLeftMouseButton(e)){
                                if (!selection.equals(aMaze.getStart()) && !selection.equals(aMaze.getGoal())){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = true;
                                }
                            }
                            else if (SwingUtilities.isRightMouseButton(e)){
                                aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = false;
                            }
                            
                        }
                        else if (modification == 1){
                            if (SwingUtilities.isLeftMouseButton(e)){
                                if (!selection.equals(aMaze.getGoal())){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                            isObstacle = false;
                                    aMaze.setStart(selection.x, selection.y);
                                }
                                
                            }
                            else if (SwingUtilities.isRightMouseButton(e)){
                                if (selection.equals(aMaze.getStart())){
                                    aMaze.setStart(null);
                                }
                            }
                        }
                        else{
                            if (SwingUtilities.isLeftMouseButton(e)){
                                if (!selection.equals(aMaze.getStart())){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                            isObstacle = false;
                                    aMaze.setGoal(selection.x, selection.y);
                                }
                            }
                            else if (SwingUtilities.isRightMouseButton(e)){
                                if (selection.equals(aMaze.getGoal())){
                                    aMaze.setGoal(null);
                                }
                            }
                        }
                    }
                    repaint();
                }
                
                @Override
                public void mouseDragged(MouseEvent e){
                    if (selection!= null && modification == 0){
                        if (SwingUtilities.isLeftMouseButton(e)){
                           if (!selection.equals(aMaze.getStart()) && !selection.equals(aMaze.getGoal())){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = true;
                                }
                            }
                            else if (SwingUtilities.isRightMouseButton(e)){
                                aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = false;
                            }
                        repaint();
                    }
                }
            };
            
            addMouseListener(mousePainter);
            addMouseMotionListener(mousePainter);
        }
        
        /**
         * removes all mouse listeners
         */
        public void removePainter(){
            removeMouseListener(mousePainter);
            removeMouseMotionListener(mousePainter);
        }
        
        public void setDrawArros(boolean arrows){
            this.drawArrows = arrows;
        }
        
    }
