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
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;

/**Displays a maze as a colored grid
 * @author Christos Darisaplis
 * @version 1.1
 */
public class MazePanel extends JPanel{
        private Point selection;//mouse selection
        private boolean needsRedraw;//redraw flag
        private Maze aMaze;//linked maze
        private MouseAdapter mousePainter;//paints maze cells
        private MouseAdapter mouseSelector;//selects maze cells
        private boolean drawArrows;//arrows on solution
        private boolean editable;//can be edited
        private boolean drawgrid;//draw maze grid
        
        /**
         * Default constructor
            * @param aMaze
         */
        public MazePanel (Maze aMaze){
            super();
            setTransferHandler(new TransferHandler("text"));
            selection = null;
            mousePainter = null;
            needsRedraw = true;
            drawArrows = false;
            editable = true;
            drawgrid = true;
            this.aMaze = aMaze;
            mouseSelector = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    calculatePosition(e);

                }
                
                @Override
                public void mouseDragged(MouseEvent e){
                    calculatePosition(e);
                }
                
            };
            mousePainter = new MouseInputAdapter() {
                
                @Override
                public void mouseClicked(MouseEvent e){
                    if (selection != null && editable){
                            if (SwingUtilities.isLeftMouseButton(e)){
                                if (!selection.equals(aMaze.getStart()) && !selection.equals(aMaze.getGoal())
                                        && !aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle()){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = true;
                                }
                                else if (aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle()){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = false;
                                }
                                else if (aMaze.getStart() != null && aMaze.getStart().
                                        equals(selection)){
                                    aMaze.setStart(null);
                                }
                                else if (aMaze.getGoal() != null && aMaze.getGoal().
                                        equals(selection)){
                                    aMaze.setGoal(null);
                                }
                            }
                            else if (SwingUtilities.isRightMouseButton(e)){
                                aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = false;
                            }
                    }
                    repaint();
                }
                @Override
                public void mouseDragged(MouseEvent e){
                    if (selection!= null && editable){
                        if (SwingUtilities.isLeftMouseButton(e)){
                           if (!selection.equals(aMaze.getStart()) && !selection.equals(aMaze.getGoal())){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = true;
                                }
                            }
                            else if (SwingUtilities.isRightMouseButton(e)){
                                aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = false;
                                        if (aMaze.getStart() != null && aMaze.getStart().equals(selection)){
                                            aMaze.setStart(null);
                                        }
                                        if (aMaze.getGoal() != null && aMaze.getGoal().equals(selection)){
                                            aMaze.setGoal(null);
                                        }
                            }
                        repaint();
                    }
                }
                
                
                
            };
            addMouseMotionListener(mouseSelector);
            addMouseListener(mousePainter);
            addMouseMotionListener(mousePainter);
            DragSource ds = new DragSource();
            
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
                    if (drawgrid){
                        g2D.setColor(Color.BLACK);
                    }
                    else{
                        g2D.setColor(getBackground());
                    }
                    g2D.draw(aMaze.getMazeLogic()[i][j].getCell());
                    if (aMaze.getMazeLogic()[i][j].isObstacle()){
                        g2D.setColor(Color.BLACK);
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
                    }
                    if (aMaze.getMazeLogic()[i][j].isIsFront()){
                        g2D.setColor(Color.CYAN);
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
                    }
                    if (aMaze.getMazeLogic()[i][j].isVisited()){
                        g2D.setColor(Color.RED);
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
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
                             g2D.drawPolyline(new int[]{cell.x + cell.width/2,
                                cell.x - cell.width/2}, new int[]{cell.y + cell.height/2,
                                cell.y + cell.height/2}, 2);
                         }
                         else if (previous.x< aSolutionBox.x){
                             g2D.fillPolygon(new int[]{cell.x + cell.width,
                                 cell.x + cell.width + cell.width/4, cell.x + cell.width}, new int[]{
                                 cell.y + cell.height/4, cell.y + cell.height/2,
                                 cell.y + (3*cell.height)/4}, 3
                             );
                             g2D.drawPolyline(new int[]{cell.x + 3*cell.width/2,
                                cell.x + cell.width/2}, new int[]{cell.y + cell.height/2,
                                cell.y + cell.height/2}, 2);
                         }
                         else if (previous.y > aSolutionBox.y){
                             g2D.fillPolygon(new int[]{cell.x + cell.width/4,
                                 cell.x + cell.width/2, cell.x + (3*cell.height)/4}, new int[]{
                                 cell.y, cell.y - cell.height/4,
                                 cell.y}, 3
                             );
                             g2D.drawPolyline(new int[]{cell.x + cell.width/2,
                                cell.x + cell.width/2}, new int[]{cell.y + cell.height/2,
                                cell.y - cell.height/2}, 2);
                         }
                         else{
                             g2D.fillPolygon(new int[]{cell.x + cell.width/4,
                                 cell.x + cell.width/2, cell.x + (3*cell.height)/4}, new int[]{
                                 cell.y + cell.height, cell.y + cell.height + cell.height/4,
                                 cell.y + cell.height}, 3
                             );
                             g2D.drawPolyline(new int[]{cell.x + cell.width/2,
                                cell.x + cell.width/2}, new int[]{cell.y + 3*cell.height/2,
                                cell.y + cell.height/2}, 2);
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
            if (drawgrid){
                for (int i = 0;i< aMaze.getRows();i++){
                    for (int j = 0;j< aMaze.getColumns();j++){
                         g2D.draw(aMaze.getMazeLogic()[i][j].getCell());
                    }
                }
            }
            else{
                Rectangle upperLeftCell = aMaze.getMazeLogic()[0][0].getCell();
                Rectangle upperRightCell = aMaze.getMazeLogic()[0][aMaze.getColumns() - 1].getCell();
                Rectangle bottomLeftCell = aMaze.getMazeLogic()[aMaze.getRows() - 1][0].getCell();
                Rectangle bottomRightCell = aMaze.getMazeLogic()[aMaze.getRows() - 1][aMaze.getColumns() - 1].getCell();
                g2D.drawPolyline(new int[]{upperLeftCell.x, upperRightCell.x + cellWidth,
                    bottomRightCell.x + cellWidth, bottomLeftCell.x, upperLeftCell.x}, new int[]{upperLeftCell.y,
                    upperRightCell.y, bottomRightCell.y + cellHeight, bottomLeftCell.y + cellWidth, upperLeftCell.y}, 5);
            }
            
            
           
            
            
            g2D.dispose();
            
        }
        
        /**
         * Link this panel with a new maze
         * @param aMaze maze to link
         */
        public void setMaze(Maze aMaze){
            removeMouseMotionListener(mouseSelector);
            removeMouseMotionListener(mousePainter);
            removeMouseListener(mousePainter);
            this.aMaze = aMaze;
            mouseSelector = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    calculatePosition(e);

                }
                @Override
                public void mouseDragged(MouseEvent e){
                    
                    calculatePosition(e);
                }
            };
            mousePainter = new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent e){
                    if (selection != null && editable){
                            if (SwingUtilities.isLeftMouseButton(e)){
                                if (!selection.equals(aMaze.getStart()) && !selection.equals(aMaze.getGoal())
                                        && !aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle()){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = true;
                                }
                                else if (aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle()){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = false;
                                }
                                else if (aMaze.getStart() != null && aMaze.getStart().
                                        equals(selection)){
                                    aMaze.setStart(null);
                                }
                                else if (aMaze.getGoal() != null && aMaze.getGoal().
                                        equals(selection)){
                                    aMaze.setGoal(null);
                                }
                            }
                            else if (SwingUtilities.isRightMouseButton(e)){
                                aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = false;
                            }
                    }
                    repaint();
                }
                @Override
                public void mouseDragged(MouseEvent e){
                    if (selection!= null && editable){
                        if (SwingUtilities.isLeftMouseButton(e)){
                           if (!selection.equals(aMaze.getStart()) && !selection.equals(aMaze.getGoal())){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = true;
                                }
                            }
                            else if (SwingUtilities.isRightMouseButton(e)){
                                aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle = false;
                                        if (aMaze.getStart() != null && aMaze.getStart().equals(selection)){
                                            aMaze.setStart(null);
                                        }
                                        if (aMaze.getGoal() != null && aMaze.getGoal().equals(selection)){
                                            aMaze.setGoal(null);
                                        }
                            }
                        repaint();
                    }
                }
            };
            addMouseMotionListener(mouseSelector);
            addMouseListener(mousePainter);
            addMouseMotionListener(mousePainter);
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
        
        private void calculatePosition(MouseEvent e){
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
        
        
        public void setDrawArros(boolean arrows){
            this.drawArrows = arrows;
        }
        
        public void setEditable(boolean editable){
            this.editable = editable;
        }
        
        /**
         * Sets start&goal with drag n drop
         * 
         * @param selection string reperesenting user selection (start or goal)
         */
        public void setText(String selection){
            Point pointer = this.getMousePosition();
            Point pointerSelection = new Point();
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
                    
            if (pointer.x>= xOff && pointer.x<= width - xOff &&
                pointer.y>= yOff && pointer.y<= height - yOff){
                pointerSelection.x = (pointer.y - yOff)/cellHeight;
                pointerSelection.y = (pointer.x - xOff)/cellWidth;;
                }
            else{
                pointerSelection = null;
            }
            
            if (pointerSelection != null && editable){
                if (selection.equals("S") && (aMaze.getGoal() == null || 
                        pointerSelection.x != aMaze.getGoal().x)){
                    aMaze.setStart(pointerSelection);
                    aMaze.getMazeLogic()[pointerSelection.x][pointerSelection.y].
                            isObstacle(false);
                }
                else if (aMaze.getStart() == null || pointerSelection.x != aMaze.getStart().x &&
                        pointerSelection.y != aMaze.getStart().y){
                    aMaze.setGoal(pointerSelection);
                    aMaze.getMazeLogic()[pointerSelection.x][pointerSelection.y].
                            isObstacle(false);
                }
            }
            repaint();
            
            
        }
        
        /**
         * dummy, required for drag n drop operations
         * @return 
         */
        public String getText(){
            return null;
        }
        
        public void setDrawGrid(boolean drawGrid){
            this.drawgrid = drawGrid;
            repaint();
        }
        
    }
