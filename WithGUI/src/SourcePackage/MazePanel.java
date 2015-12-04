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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;

/**Displays a maze as a colored grid
 * @author Christos Darisaplis
 * @version 1.2
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
        private String text;//used in drag n drop operations
        private boolean previewGoal;//previews drop point of goal (DnD operations)
        private boolean previewStart;//previews drop point of start (DnD operations)
        private boolean moveable;//can be moved around with cursor
        private Point movementStartingPoint;//from which point movement occured
        private Maze originalMaze;//old linked maze
        private Point originalMazeStart;//where the old maze is placed in relation to the current one

        
        /**
         * Default constructor
            * @param aMaze maze linked to the panel
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
            moveable = false;
            this.aMaze = aMaze;
            this.originalMaze = aMaze;
            this.originalMazeStart = new Point(0, 0);
            setMouseSelector();
            setMousePainter();
            addMouseMotionListener(mouseSelector);
            addMouseListener(mousePainter);
            addMouseMotionListener(mousePainter);
            
            
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
            super.paintComponent(g);//initialize
            Graphics2D g2D;
            g2D = (Graphics2D)g.create();
            
            int width, height;//calculate width and height along with offsets
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
            
            if (xOff == 0){
                width -=1;
                cellWidth = width/aMaze.getColumns();
            }
            if (yOff == 0){
                height-=1;
                cellHeight = height/aMaze.getRows();
            }
            

            
            if (needsRedraw){//replace cells if needed
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
            
            
            for (int i = 0;i< aMaze.getRows();i++){//draw
                for (int j = 0;j< aMaze.getColumns();j++){
                    if (drawgrid){//draw grid
                        g2D.setColor(Color.BLACK);
                    }
                    else{
                        g2D.setColor(getBackground());
                    }
                    g2D.draw(aMaze.getMazeLogic()[i][j].getCell());
                    if (aMaze.getMazeLogic()[i][j].isObstacle()){
                        g2D.setColor(Color.BLACK);//draw obstacle
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
                    }
                    if (aMaze.getMazeLogic()[i][j].isIsFront()){
                        g2D.setColor(Color.CYAN);//draw front
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
                    }
                    if (aMaze.getMazeLogic()[i][j].isVisited()){
                        g2D.setColor(Color.RED);//draw visited
                        g2D.fill(aMaze.getMazeLogic()[i][j].getCell());
                    }
                    
                }
                
            }
            
            
            
            g2D.setColor(Color.GREEN);//draw solution so far
            MazeBox previous = null;
            MazeBox aSolutionBox = null;
            if (aMaze.getSolution() != null){
                int currentSize = aMaze.getSolution().size();
                for (int i = 0;i< currentSize;i++){
                    previous = aSolutionBox;
                    try {//catch concurrent modifications
                        previous = aSolutionBox;
                        aSolutionBox = aMaze.getSolution().get(i);
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                    if (aSolutionBox != null){
                        g2D.fill(aMaze.getMazeLogic()[aSolutionBox.y]
                            [aSolutionBox.x].getCell());
                    }
                     if (previous != null && aSolutionBox != null && drawArrows){
                         g2D.setColor(Color.BLACK);//draw arrors
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
            
            if (aMaze.getCurrent() != null){//draw current cell
                g2D.setColor(Color.MAGENTA);
                g2D.fill(aMaze.getMazeLogic()[aMaze.getCurrent().x]
                        [aMaze.getCurrent().y].getCell());
            }
            
            
            if (aMaze.getStart() != null && aMaze.getStart().x>= 0 &&
                    aMaze.getStart().y>= 0 && aMaze.getStart().x< aMaze.getRows() &&
                    aMaze.getStart().y< aMaze.getColumns()){
                g2D.setColor(Color.YELLOW);//draw start
                g2D.fill(aMaze.getMazeLogic()[aMaze.getStart().x]
                        [aMaze.getStart().y].getCell());
            }
            if (aMaze.getGoal() != null && aMaze.getGoal().x>= 0 &&
                    aMaze.getGoal().y>=0 && aMaze.getGoal().x<aMaze.getRows() && 
                    aMaze.getGoal().y< aMaze.getColumns()){
                g2D.setColor(Color.BLUE);//draw goal
                g2D.fill(aMaze.getMazeLogic()[aMaze.getGoal().x]
                        [aMaze.getGoal().y].getCell());
            }
            
            
            if (previewStart && selection != null && !selection.equals(aMaze.getGoal())){
                Color previewYellow = new Color(Color.YELLOW.getRed(),
                    Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), 100);
                g2D.setColor(previewYellow);//draw preview of DnD (start)
                g2D.fill(aMaze.getMazeLogic()[selection.x][selection.y].getCell());
            }
            
            if (previewGoal && selection != null && !selection.equals(aMaze.getStart())){
                Color previewBlue = new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(),
                        Color.BLUE.getBlue(), 100);
                g2D.setColor(previewBlue);//draw preview of DnD (goal)
                g2D.fill(aMaze.getMazeLogic()[selection.x][selection.y].getCell());
            }
            
            g2D.setColor(Color.BLACK);
            if (drawgrid){//draw grid
                for (int i = 0;i< aMaze.getRows();i++){
                    for (int j = 0;j< aMaze.getColumns();j++){
                         g2D.draw(aMaze.getMazeLogic()[i][j].getCell());
                    }
                }
            }
            else{//draw outline
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
            setMouseSelector();
            setMousePainter();
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
        
        /**
         * Returns the maze currently linked to this panel
         * @return maze object
         */
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
         * Calculates mouse selection as maze cell
         * @param e mouse movement
         */
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
                        selection.y = (e.getX() - xOff)/cellWidth;
                        if (selection.x>= aMaze.getRows()){
                            selection.x = aMaze.getRows() - 1;
                        }
                        if (selection.y>= aMaze.getColumns()){
                            selection.y = aMaze.getColumns() - 1;
                        }
                    }
                    else{
                        selection = null;
                    }
        }
        
        /**
         * Settter of drawArrows variable
         * @param arrows new value
         */
        public void setDrawArros(boolean arrows){
            this.drawArrows = arrows;
            repaint();
        }
        
        /**
         * Setter of editable variable
         * @param editable new value
         */
        public void setEditable(boolean editable){
            this.editable = editable;
        }
        
        /**
         * Calculates on which maze cell the pointer hovers on, used on DnD operations
         * @return point indicating maze cell
         */
        private Point calculatePointerSelection(){
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
                    
            if (pointer != null && pointer.x>= xOff && pointer.x<= width - xOff &&
                pointer.y>= yOff && pointer.y<= height - yOff){
                pointerSelection.x = (pointer.y - yOff)/cellHeight;
                pointerSelection.y = (pointer.x - xOff)/cellWidth;
                if (pointerSelection.x>= aMaze.getRows()){
                    pointerSelection.x = aMaze.getRows() - 1;
                }
                if (pointerSelection.y>= aMaze.getColumns()){
                    pointerSelection.y = aMaze.getColumns() - 1;
                }
            }
            else{
                pointerSelection = null;
            }
            return pointerSelection;
        }
        
        /**
         * Enables preview of DnD operations according to data transefered
         * @param selection start or goal
         */
        public void setDnDPreview(String selection){
            if (editable){
                this.selection = calculatePointerSelection();
                if (selection.equals("S")){
                    this.previewGoal = false;
                    this.previewStart = true;
                }
                else if (selection.equals("G")){
                    this.previewGoal = true;
                    this.previewStart = false;
                }
                repaint();
            }
        }
        
        
        
        /**
         * Sets start&goal with drag n drop
         * 
         * @param selection string reperesenting user selection (start or goal)
         */
        public void setText(String selection){
            Point pointerSelection = calculatePointerSelection();
            
            if (pointerSelection != null && editable){
                if (selection.equals("S") && (aMaze.getGoal() == null || 
                        !pointerSelection.equals(aMaze.getGoal()))){
                    aMaze.setStart(pointerSelection);
                    aMaze.getMazeLogic()[pointerSelection.x][pointerSelection.y].
                            setIsObstacle(false);
                }
                else if (aMaze.getStart() == null || !pointerSelection.equals(aMaze.getStart())){
                    aMaze.setGoal(pointerSelection);
                    aMaze.getMazeLogic()[pointerSelection.x][pointerSelection.y].
                            setIsObstacle(false);
                }
            }
            else if (editable){
                if (selection.equals("S")){
                    aMaze.setStart(null);
                }
                else if (selection.equals("G")){
                    aMaze.setGoal(null);
                }
            }
            
            repaint();
            
            
        }
        
        /**
         * dummy, required for drag n drop operations
         * @return 
         */
        public String getText(){
            return this.text;
        }
        
        /**
         * Setter of drawGrid variable
         * @param drawGrid new value
         */
        public void setDrawGrid(boolean drawGrid){
            this.drawgrid = drawGrid;
            repaint();
        }
        
        /**
         * Sets up the mouse selector that selects individual maze cells
         */
        private void setMouseSelector(){
            this.mouseSelector = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    calculatePosition(e);

                }
                
                @Override
                public void mouseDragged(MouseEvent e){
                    calculatePosition(e);
                }
                
            };
        }
        
        /**
         * Sets up the mousePainter that modifies individual maze cells
         */
        private void setMousePainter(){
            mousePainter = new MouseInputAdapter() {
                
                
                @Override
                public void mousePressed(MouseEvent e){
                    movementStartingPoint = selection;
                }
                
                @Override
                public void mouseClicked(MouseEvent e){
                    if (selection != null && editable){
                            if (SwingUtilities.isLeftMouseButton(e)){
                                if (!selection.equals(aMaze.getStart()) && !selection.equals(aMaze.getGoal())
                                        && !aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle()){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                        setIsObstacle(true);
                                }
                                else if (aMaze.getMazeLogic()[selection.x][selection.y].
                                        isObstacle()){
                                    aMaze.getMazeLogic()[selection.x][selection.y].
                                            setIsObstacle(false);
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
                                        setIsObstacle(false);
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
                                            setIsObstacle(true);
                                }
                           else if (selection.equals(aMaze.getStart())){
                               text = "S";
                               JComponent c = (JComponent)e.getSource();
                               TransferHandler handler = c.getTransferHandler();
                                handler.setDragImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
                                 "/Icons/start.png")));
                                handler.exportAsDrag(c, e, TransferHandler.COPY);
                            }
                           else if (selection.equals(aMaze.getGoal())){
                               text = "G";
                               JComponent c = (JComponent)e.getSource();
                               TransferHandler handler = c.getTransferHandler();
                                handler.setDragImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
                                 "/Icons/goal.png")));
                                handler.exportAsDrag(c, e, TransferHandler.COPY);
                           }
                        }
                        else if (SwingUtilities.isRightMouseButton(e)){
                                aMaze.getMazeLogic()[selection.x][selection.y].
                                        setIsObstacle(false);
                        }
                    }
                    if (moveable && movementStartingPoint != null &&
                        selection != null &&
                        !movementStartingPoint.equals(selection)){
                        int i = movementStartingPoint.x - selection.x;
                        int j = movementStartingPoint.y - selection.y;
                        originalMazeStart.x += i;
                        originalMazeStart.y += j;
                        if (aMaze.getStart() != null){
                            aMaze.getStart().x -= i;
                            aMaze.getStart().y -= j;
                        }
                        if (aMaze.getGoal() != null){
                            aMaze.getGoal().x -= i;
                            aMaze.getGoal().y -= j;
                        }
                        aMaze.copyMazeObstacles(originalMaze, originalMazeStart.x,
                                originalMazeStart.y);
                        movementStartingPoint = selection;
                    }
                    invalidate();
                    repaint();
                }
                
                @Override
                public void mouseMoved(MouseEvent e){
                    if (moveable && selection != null){
                        setCursor(new Cursor(Cursor.MOVE_CURSOR));
                    }
                    else if (moveable){
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
                
                
                
            };
        }

        /**
         * Ends any and all previews of DnD operations
         */
        public void endPreview(){
            this.previewGoal = false;
            this.previewStart = false;
        }
        
        /**
         * Setter of moveable variable
         * @param moveable new value
         */
        public void setMoveable(boolean moveable){
            this.moveable = moveable;
        }
       
        /**
         * Getter of originalMaze variable
         * @return originalMaze variable
         */
        public Maze getOriginalMaze() {
            return originalMaze;
        }
        
        /**
         * Setter of originalMaze variable
         * @param newOriginalMaze new value
         */
        public void setOriginalMaze(Maze newOriginalMaze){
            this.originalMaze = newOriginalMaze;
        }
        
        /**
         * Getter of drawGrid variable
         * @return drawGrid variable
         */
        public boolean getDrawGrid(){
            return this.drawgrid;
        }
        
        
    }
