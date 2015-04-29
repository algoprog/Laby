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



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**GUI
 *
 * @author Christos Darisaplis
 * @version 1.1
 */
public class GUI {
    

    private JFrame mainFrame;//main window
    private MazePanel mazePanel;//displays maze
    private boolean saved;//maze saved
    private String directory;//save directory
    private Maze maze;//open maze
    private MazeSearch solver;//solves the open maze
    private Thread runThread;//runs to solve the maze
    private boolean pause;//thread paused
    private MazeGenerator generator;//generates maze
    private boolean helpOpen;//open help window
    
    /**
     * Constructor, builds GUI
     */
    public GUI(){
        saved = true;
        directory = null;
        solver = null;
        pause = false;
        helpOpen = false;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Using Java default look&feel");
        }
        
        mainFrame = new JFrame("Laby");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(15, 10));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        maze = new Maze(16, 16);
        mazePanel = new MazePanel(maze);
        
        JPanel mainPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(mazePanel);
        
        
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem newMaze = new JMenuItem("New Maze...");
        JMenuItem openMaze = new JMenuItem("Open Maze...");
        JMenuItem saveMazeAs = new JMenuItem("Save As...");
        JMenuItem saveMaze = new JMenuItem("Save");
        saveMazeAs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });
        saveMaze.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                savePrompt(mainFrame);
                runThread.interrupt();
                mainFrame.dispose();
            }
        });
        
        JMenuItem clear = new JMenuItem("Clear Maze");
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.getMaze().whiten();
                mazePanel.repaint();
            }
        });
        
        fileMenu.add(newMaze);
        fileMenu.add(openMaze);
        fileMenu.addSeparator();
        fileMenu.add(saveMaze);
        fileMenu.add(saveMazeAs);
        fileMenu.addSeparator();
        fileMenu.add(clear);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        

        
        JMenu helpMenu  = new JMenu("Help");
        JMenuItem legend = new JMenuItem("Legend");
        JMenuItem about = new JMenuItem("About...");
        helpMenu.add(legend);
        helpMenu.addSeparator();
        helpMenu.add(about);
        menuBar.add(helpMenu);
        legend.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!helpOpen){
                    JDialog helpDialog = new JDialog(mainFrame, "Help", false);
                
                    JLabel iconLabel = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
                    "/Icons/legend.png"))));
                
                    helpDialog.add(iconLabel, BorderLayout.CENTER);
                    helpDialog.pack();
                    helpDialog.setLocationRelativeTo(mainFrame);
                    helpDialog.setResizable(false);
                    helpDialog.setVisible(true);
                    helpOpen = true;
                    helpDialog.addWindowListener(new WindowAdapter() {

                        @Override
                        public void windowClosing(WindowEvent e){
                            helpOpen = false;
                        }
                    });
                }
            }
        });
        
        about.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog aboutDialog = new JDialog(mainFrame, "About", true);
                
                JPanel aboutPanel = new JPanel();
                aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
                JLabel iconLabel = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
                "/Icons/laby.png"))));
                aboutPanel.add(iconLabel);
                aboutPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                
                JLabel aboutLabel = new JLabel("<html> version 1.1 <br><br>"
                        + "GUI: Chris Darisaplis<br>"
                        + "Algorithms implementation: Chris Samarinas<br><br>"
                        + "Department of Informatics<br>"
                        + "School of Applied Sciences<br>"
                        + "Aristotle University of Thessaloniki</html>");
                aboutLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                aboutPanel.add(aboutLabel);
                
                JEditorPane linkPane= new JEditorPane();
                linkPane.setContentType("text/html");
                linkPane.setText("<p align='center'>Source code on <a href='https://github.com/greekdev/Laby/'>GitHub</a>.</p>");
                linkPane.setEditable(false);
                linkPane.setOpaque(false);
                linkPane.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                linkPane.addHyperlinkListener(new HyperlinkListener() {

                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent hle) {
                        if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                            System.out.println(hle.getURL());
                            Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse(hle.getURL().toURI());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
                
                aboutPanel.add(linkPane);
                
                aboutPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,0));
                aboutDialog.add(aboutPanel);
                
                
                aboutDialog.pack();
                aboutDialog.setLocationRelativeTo(mainFrame);
                aboutDialog.setResizable(false);
                aboutDialog.setVisible(true);
            }
        });
        
        
        JPanel randomPanel = new JPanel(new BorderLayout());
        JCheckBox random = new JCheckBox("Choose cells randomly");
        randomPanel.add(random);
        randomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JPanel runPanel = new JPanel();
        runPanel.setLayout(new BoxLayout(runPanel, BoxLayout.Y_AXIS));
        
        JLabel IDLabel = new JLabel("Set depth increment", JLabel.CENTER);
        IDLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        IDLabel.setVisible(false);
        JSpinner IDSpinner = new JSpinner(new SpinnerNumberModel(2, 1, Integer.MAX_VALUE, 1));
        IDSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        IDSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        IDSpinner.setVisible(false);
        
        JLabel algoLabel = new JLabel("Algorithm");
        algoLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        String[] algorithms = {"DFS", "BFS", "ID", "Best-First", "Hill Climbing",
            "A*", "Maze Generator"};
        JComboBox<String> algoSelection = new JComboBox<>(algorithms);
        algoSelection.setAlignmentX(Component.CENTER_ALIGNMENT);
        algoSelection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel distanceSelectionPanel = new JPanel(new GridLayout(0, 1));
        JLabel distanceLabel = new JLabel("Heuristic Funciton", JLabel.CENTER);
        String[] distanceFunctions = {"Manhattan", "Euclidean"};
        JComboBox<String> distanceSelection = new JComboBox<>(distanceFunctions);
        distanceSelectionPanel.add(distanceLabel);
        distanceSelectionPanel.add(distanceSelection);
        distanceSelectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        distanceSelectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        distanceSelectionPanel.setVisible(false);
        
        JLabel stepInfoLabel = new JLabel();
        stepInfoLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JLabel maxFrontLabel = new JLabel();
        maxFrontLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JLabel solutionLabel = new JLabel();
        solutionLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(stepInfoLabel);
        infoPanel.add(solutionLabel);
        infoPanel.add(maxFrontLabel);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Stats"));
        infoPanel.setVisible(false);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel stepLabel = new JLabel("Step delay (msec):", JLabel.CENTER);
        stepLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        JSlider speedSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 2000, 1000);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        JSpinner speedSpinner = new JSpinner(new SpinnerNumberModel(1000, 0,
                2000, 1));
        speedSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                speedSlider.setValue((Integer)speedSpinner.getValue());
            }
        });
        
        
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer(0), new JLabel("0") );
        labelTable.put( new Integer(1000), new JLabel("1000"));
        labelTable.put( new Integer(2000), new JLabel("2000"));
        speedSlider.setLabelTable( labelTable );
        speedSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                speedSpinner.setValue(speedSlider.getValue());
            }
        });
        JPanel speedPanel = new JPanel(new GridLayout(1, 2));
        speedPanel.add(stepLabel);
        speedPanel.add(speedSpinner);
        speedPanel.setMaximumSize(new Dimension(9000, 100));
        
        JCheckBox arrowBox = new JCheckBox("Arrows from predecessors");
        arrowBox.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);
        
        arrowBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.setDrawArros(arrowBox.isSelected());
                mazePanel.repaint();
            }
        });
        
        JLabel completedLabel = new JLabel("Completed!");
        completedLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        completedLabel.setVisible(false);
        
        
        JButton runButton = new JButton("Run");
        JButton stopButton = new JButton("Reset");
        JButton pauseButton = new JButton("Pause");
        JButton nextButton = new JButton("Next Step");
        runButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        pauseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pauseButton.setEnabled(false);
                runButton.setEnabled(true);
                stopButton.setEnabled(true);
                nextButton.setEnabled(true);
                pause = true;
            }
        });
        
        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.setEditable(true);
                if (runThread != null){
                    runThread.interrupt();
                }
                random.setEnabled(true);
                algoSelection.setEnabled(true);
                IDSpinner.setEnabled(true);
                runButton.setEnabled(true);
                nextButton.setEnabled(true);
                speedSlider.setEnabled(true);
                speedSpinner.setEnabled(true);
                stopButton.setEnabled(false);
                pause = false;
                clearMaze();
                if (algoSelection.getSelectedItem().equals("Maze Generator")){
                    mazePanel.getMaze().whiten();
                }
                infoPanel.setVisible(false);
                completedLabel.setVisible(false);
                
            }
        });
        runButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (algoSelection.getSelectedItem().toString().equals("Maze Generator")){
                    random.setEnabled(false);
                    algoSelection.setEnabled(false);
                    IDSpinner.setEnabled(false);
                    stopButton.setEnabled(true);
                    pauseButton.setEnabled(true);
                    nextButton.setEnabled(false);
                    runButton.setEnabled(false);
                    if (!pause){
                        mazePanel.blacken();
                        generator = new MazeGenerator(mazePanel.getMaze().getColumns(),
                            mazePanel.getMaze().getRows(), random.isSelected(),
                            mazePanel.getMaze());
                            mazePanel.repaint();
                            mazePanel.setMaze(mazePanel.getMaze());
                    }
                    pause = false;
                    runThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                while(generator.nextStep(speedSlider.getValue())){
                                    if (pause){
                                        runThread.interrupt();
                                    }
                                    mazePanel.repaint();
                                }
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }
                            pauseButton.setEnabled(false);
                            algoSelection.setEnabled(true);
                            if (!pause){
                                speedSlider.setEnabled(false);
                                speedSpinner.setEnabled(false);
                                completedLabel.setVisible(true);
                            }
                            mazePanel.repaint();
                        }
                    });
                    runThread.start();
                }
                else if (canSolve()){
                    mazePanel.setEditable(false);
                    pause = false;
                    random.setEnabled(false);
                    algoSelection.setEnabled(false);
                    IDSpinner.setEnabled(false);
                    stopButton.setEnabled(true);
                    pauseButton.setEnabled(true);
                    nextButton.setEnabled(false);
                    runButton.setEnabled(false);
                    if (solver == null){
                        solver = setSolver(algoSelection.getSelectedItem().toString(),
                                random.isSelected(), (Integer)IDSpinner.getValue(),
                                distanceSelection);
                    }
                    runThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                while (!solver.isSolved() && solver.nextStep(speedSlider.
                                    getValue())){
                                    if (pause){
                                        runThread.interrupt();
                                    }
                                    mazePanel.getMaze().setSolution(solver.getSolution());
                                    stepInfoLabel.setText("Steps: "+solver.getSteps());
                                    solutionLabel.setText("Solution Length: "+
                                        mazePanel.getMaze().getSolution().size());
                                    maxFrontLabel.setText("Max frontier size: "+
                                        solver.getMaxFront());
                                    infoPanel.setVisible(true);
                                    infoPanel.repaint();
                                    mazePanel.repaint();
                            }
                            } catch (InterruptedException p) {
                                System.out.println("Interrupted!");
                            }
                            mazePanel.getMaze().setSolution(solver.getSolution());
                            mazePanel.repaint();
                            if (!pause){
                                pauseButton.setEnabled(false);
                                speedSlider.setEnabled(false);
                                speedSpinner.setEnabled(false);
                                stepInfoLabel.setText("Steps: "+solver.getSteps());
                                solutionLabel.setText("Solution Length: "+
                                        mazePanel.getMaze().getSolution().size());
                                maxFrontLabel.setText("Max frontier size: "+
                                        solver.getMaxFront());
                                infoPanel.setVisible(true);
                                solver = null;
                                completedLabel.setVisible(true);
                                
                            }
                            
                        }
                    });
                    runThread.start();
                    
                }
            }
        });
        runButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        pauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        pauseButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        pauseButton.setEnabled(false);
        stopButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        stopButton.setEnabled(false);
        stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (algoSelection.getSelectedItem().toString().equals("Maze Generator")){
                    if (!pause){
                        stopButton.setEnabled(true);
                        mazePanel.blacken();
                        generator = new MazeGenerator(mazePanel.getMaze().getColumns(),
                            mazePanel.getMaze().getRows(), random.isSelected(),
                            mazePanel.getMaze());
                            mazePanel.repaint();
                    }
                    pause = true;
                    try {
                        if (!generator.nextStep(0)){
                            runButton.setEnabled(false);
                            pauseButton.setEnabled(false);
                            speedSlider.setEnabled(false);
                            speedSpinner.setEnabled(false);
                            random.setEnabled(false);
                            nextButton.setEnabled(false);
                            stopButton.setEnabled(true);
                            completedLabel.setVisible(true);
                        }
                    } catch (InterruptedException n) {
                        System.out.println("Interrupted");
                    }
                    random.setEnabled(false);
                    mazePanel.repaint();
                }
                else if (canSolve()){
                    mazePanel.setEditable(false);
                    random.setEnabled(false);
                    algoSelection.setEnabled(false);
                    IDSpinner.setEnabled(false);
                    stopButton.setEnabled(true);
                    if (solver == null){
                        solver = setSolver(algoSelection.getSelectedItem().toString(),
                                random.isSelected(), (Integer)IDSpinner.getValue(),
                                distanceSelection);
                        try {
                            solver.nextStep(0);
                            mazePanel.getMaze().setSolution(solver.getSolution());
                            
                        } catch (InterruptedException p) {
                            System.out.println("Interrupted!");
                        }
                    }
                    try {
                        solver.nextStep(0);
                        mazePanel.getMaze().setSolution(solver.getSolution());
                        stepInfoLabel.setText("Steps: "+solver.getSteps());
                        solutionLabel.setText("Solution Length: "+
                                        mazePanel.getMaze().getSolution().size());
                        maxFrontLabel.setText("Max frontier size: "+
                                        solver.getMaxFront());
                        infoPanel.repaint();
                        infoPanel.setVisible(true);
                    } catch (InterruptedException p) {
                        System.out.println("Interrupted!");
                    }
                    if (solver.isSolved()){
                        completedLabel.setVisible(true);
                        nextButton.setEnabled(false);
                        runButton.setEnabled(false);
                        speedSlider.setEnabled(false);
                        speedSpinner.setEnabled(false);
                        stepInfoLabel.setText("Steps: "+solver.getSteps());
                        solutionLabel.setText("Solution Length: "+
                                        mazePanel.getMaze().getSolution().size());
                        maxFrontLabel.setText("Max frontier size: "+
                                        solver.getMaxFront());
                        infoPanel.setVisible(true);
                    }
                    mainFrame.repaint();
                }
            }
        });
        
        algoSelection.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (algoSelection.getSelectedItem().toString().equals("ID")){
                    IDLabel.setVisible(true);
                    IDSpinner.setVisible(true);
                }
                else{
                    IDLabel.setVisible(false);
                    IDSpinner.setVisible(false);
                }
                if (algoSelection.getSelectedItem().toString().equals("Best-First")
                        ||algoSelection.getSelectedItem().toString().equals("Hill Climbing")
                        ||algoSelection.getSelectedItem().toString().equals("A*")){
                            randomPanel.setVisible(false);
                            distanceSelectionPanel.setVisible(true);
                }
                else{
                    randomPanel.setVisible(true);
                    distanceSelectionPanel.setVisible(false);
                }
                if (algoSelection.getSelectedItem().toString().equals("Maze Generator")){
                    random.setText("Classic maze");
                    arrowBox.setVisible(false);
                    random.setSelected(false);
                    
                    mazePanel.repaint();
                }
                else{
                    random.setText("Choose next cells randomly");
                    random.setSelected(false);
                    arrowBox.setVisible(true);
                    mazePanel.repaint();
                }
                completedLabel.setVisible(false);
                pause = false;
                runButton.setEnabled(true);
                IDSpinner.setEnabled(true);
                stopButton.setEnabled(false);
                pauseButton.setEnabled(false);
                nextButton.setEnabled(true);
                random.setEnabled(true);
                speedSlider.setEnabled(true);
                speedSpinner.setEnabled(true);
            }
        });
        
        JPanel editPanel = new JPanel(new GridLayout(1, 2));
        JLabel startLabel = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
                "/Icons/start.png"))));
        JLabel goalLabel = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
                "/Icons/goal.png"))));
        editPanel.add(startLabel);
        editPanel.add(goalLabel);
        editPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        editPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                "Set Start & Goal"));
        
        startLabel.setToolTipText("Start");
        startLabel.setText("S");
        startLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                startLabel.setTransferHandler(new TransferHandler("text"));
                goalLabel.setTransferHandler(new TransferHandler(null));
                JComponent c = (JComponent)e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.setDragImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
                "/Icons/start.png")));
                handler.exportAsDrag(c, e, TransferHandler.COPY);
            }
            
        });
           
        goalLabel.setToolTipText("Goal");
        goalLabel.setText("G");
        goalLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                goalLabel.setTransferHandler(new TransferHandler("text"));
                startLabel.setTransferHandler(new TransferHandler(null));
                JComponent c = (JComponent)e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.setDragImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
                "/Icons/goal.png")));
                handler.exportAsDrag(c, e, TransferHandler.COPY);
            }
        });
        
        
        
        runPanel.add(Box.createVerticalGlue());
        runPanel.add(algoLabel);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(algoSelection);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(IDLabel);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(IDSpinner);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(randomPanel);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(distanceSelectionPanel);
        runPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        runPanel.add(runButton);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(pauseButton);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(stopButton);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(nextButton);
        runPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        runPanel.add(completedLabel);
        runPanel.add(speedPanel);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(speedSlider);
        runPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        runPanel.add(arrowBox);
        runPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        runPanel.add(completedLabel);
        runPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        runPanel.add(infoPanel);
        runPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        runPanel.add(editPanel);
        runPanel.add(Box.createVerticalGlue());
        runPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
       
       fileMenu.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                stopButton.doClick();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                
            }
        });
       
       newMaze.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopButton.doClick();
                newMaze();
            }
        });
       
       openMaze.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopButton.doClick();
                openMaze();
            }
        });
       

        mazePanel.setBorder(new EmptyBorder(10, 10, 10, 20));
        mainFrame.setJMenuBar(menuBar);
        mainFrame.add(mainPanel, BorderLayout.CENTER);
        mainFrame.add(runPanel, BorderLayout.EAST);
        
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                savePrompt(mainFrame);
                if (runThread != null){
                    runThread.interrupt();
                }
                mainFrame.dispose();
            }
        });

        
        

        mainFrame.setMinimumSize(new Dimension(650, 600));
        mainFrame.setPreferredSize(new Dimension(650, 550));
        mainFrame.pack();
        mainFrame.setLocation(screenSize.width/2 - (mainFrame.getWidth())/2,
                screenSize.height/2 - (mainFrame.getHeight()/2));
        mainFrame.setVisible(true);
    }
    
    
    /**
     * Saves maze to a specific location
     */
    private void saveAs (){
        JFileChooser chooser =  new JFileChooser();
        chooser.setDialogTitle("Save As");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text files (.txt)", "txt"));
        int selection = chooser.showSaveDialog(mainFrame);
        if (selection == JFileChooser.APPROVE_OPTION){
            String filename = chooser.getSelectedFile().getPath();
            if (chooser.getSelectedFile().exists()){
                String[] options = {"Yes", "No"};
                int n = JOptionPane.showOptionDialog(chooser, "This file already"
                        + "exists. Overwrite?",
                        "Confirm Overwrite", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                if (n == 0){
                    mazePanel.getMaze().saveMaze(filename);
                    directory = filename;
                    saved = true;
                }
                
            }
            else{
                mazePanel.getMaze().saveMaze(filename + ".txt");
                directory = filename + ".txt";
                saved = true;
            }
        }
        
        
    }
    
    /**
     * Saves maze to save directory
     */
    private void save (){
        if (directory != null){
            File mazeFile = new File(directory);
            if (mazeFile.exists()){
                mazePanel.getMaze().saveMaze(directory);
                saved = true;
            }
            else{
                saveAs();
            }
            
        }
        else{
            saveAs();
        }
    }
    
    /**
     * Prompts the user to save if the maze has been modified
     * @param parent parent component
     */
    private void savePrompt (JFrame parent){
        if (!saved){
            int n = JOptionPane.showConfirmDialog(parent, "This maze has"
                    + " been modified. Save changes?", "Select an option",
            JOptionPane.YES_NO_OPTION);
            if (n == 0){
                save();
            }
        }
        saved = true;
    }
    
    /**
     * Creates a new maze
     */
    private void newMaze(){
        if (runThread != null){
            runThread.interrupt();
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxSize;
        if (screenSize.height > screenSize.width){
            maxSize = screenSize.height/10;
        }
        else{
            maxSize = screenSize.width/10;
        }
        JDialog newDialog = new JDialog(mainFrame, "Build New Maze", true);
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new GridLayout(0, 2, 10, 10));
        
        JSpinner rowSpinner = new JSpinner(new SpinnerNumberModel(16, 4, maxSize, 1));
        JSpinner columnSpinner = new JSpinner(new SpinnerNumberModel(16, 4, maxSize, 1));
       
        newPanel.add(new JLabel("Rows: "));
        newPanel.add(rowSpinner);
        newPanel.add(new JLabel("Columns: "));
        newPanel.add(columnSpinner);
        

        JButton buildButton = new JButton("Build");
        buildButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                maze =  new Maze((Integer)rowSpinner.getValue(),
                        (Integer)columnSpinner.getValue());
                mazePanel.setMaze(maze);
                solver = null;
                pause = false;
                directory = null;
                saved = true;
                mainFrame.setPreferredSize(mazePanel.getPreferredSize());
                if (!(mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH))
                {
                    mainFrame.pack();
                    mainFrame.setLocation(screenSize.width/2 - (mainFrame.getWidth())/2,
                        screenSize.height/2 - (mainFrame.getHeight()/2));
                    if((Integer)rowSpinner.getValue()>64||(Integer)columnSpinner.getValue()>64){
                        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    }
                }
                mainFrame.repaint();
                newDialog.dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newDialog.dispose();
            }
        });
        newPanel.add(buildButton);
        newPanel.add(cancelButton);
        
        newPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        newDialog.add(newPanel);
        newDialog.setLocationRelativeTo(mainFrame);
        newDialog.setResizable(false);
        newDialog.pack();
        newDialog.setVisible(true);
    }
    
    /**
     * Opens a maze from a text file
     * @return true if the maze opens successfully, false otherwise
     */
    private boolean openMaze(){
        boolean flag = false;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text files (.txt)",
                "txt"));
        int selection = chooser.showOpenDialog(mainFrame);
        if (selection == JFileChooser.APPROVE_OPTION){
            String filename = chooser.getSelectedFile().getPath();
            savePrompt(mainFrame);
            maze = new Maze(filename);
            mazePanel.setMaze(maze);
            solver = null;
            mainFrame.setPreferredSize(mazePanel.getPreferredSize());
            if (!(mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH))
                {
                    mainFrame.pack();
                    mainFrame.setLocation(screenSize.width/2 - (mainFrame.getWidth())/2,
                        screenSize.height/2 - (mainFrame.getHeight()/2));
                }
            mainFrame.repaint();
            directory = filename;
            saved = true;
            pause = false;
            flag = true;
        }
        return flag;
    }
    
    /**
     * Updates speed label
     * @param speedLabel label to update
     * @param newValue new value for the label to display
     */
    private void updateCurrentSpeedLabel(JLabel speedLabel, int newValue){
        speedLabel.setText("Step delay(msec): "+Integer.toString(newValue));
    }
    
    /**
     * Sets up a solver for this maze
     * @param algorithm type of algorithm to use
     * @param random choose cells randomly
     * @param depth ID depth
     * @return  sover for this maze
     */
    private MazeSearch setSolver (String algorithm, boolean random, int depth,
            JComboBox<String> distanceFunction){
        boolean manhattan;
        if (distanceFunction.getSelectedItem().toString().equals("Euclidean")){
            manhattan = false;
        }
        else{
            manhattan = true;
        }
        if (algorithm.equals("DFS")){
            solver = new MazeSolverDBFS(mazePanel.getMazeData(), random, true,
                        mazePanel.getMaze());
        }
        if (algorithm.equals("BFS")){
            solver = new MazeSolverDBFS(mazePanel.getMazeData(), random, false,
                        mazePanel.getMaze());
        }
        if (algorithm.equals("ID")){
            solver = new MazeSolverID(mazePanel.getMazeData(), random, depth,
                    mazePanel.getMaze());
        }
        if (algorithm.equals("Best-First")){
            solver = new MazeSolverBF(mazePanel.getMazeData(), false,manhattan, mazePanel.getMaze());
        }
        if (algorithm.equals("Hill Climbing")){
            solver = new MazeSolverHC(mazePanel.getMazeData(),manhattan, mazePanel.getMaze());
        }
        if (algorithm.equals("A*")){
            solver = new MazeSolverBF(mazePanel.getMazeData(), true,manhattan, mazePanel.getMaze());
        }
        return solver;
        
    }
    
    /**
     * Checks if this maze is solveable
     * @return true if this maze can be solved, false otherwise
     */
    private boolean canSolve(){
        if (mazePanel.getMaze().getStart() == null){
            JOptionPane.showMessageDialog(mainFrame, "Set up a starting point to"
                    + " solve this maze!");
            return false;
        }
        if (mazePanel.getMaze().getGoal() == null){
            JOptionPane.showMessageDialog(mainFrame, "Set up a goal to"
                    + " solve this maze!");
            return false;
        }
        if (solver != null){
            if (solver.isSolved()){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Clears this maze, 
     */
    private void clearMaze(){
        for (int i = 0;i< mazePanel.getMaze().getRows();i++){
            for (int j = 0;j< mazePanel.getMaze().getColumns();j++){
                mazePanel.getMaze().getMazeLogic()[i][j].setIsFront(false);
                mazePanel.getMaze().getMazeLogic()[i][j].isVisited = false;
                mazePanel.getMaze().setSolution(new ArrayList<>());
                mazePanel.getMaze().setCurrent(null);
                mainFrame.repaint();
                solver = null;
                pause = false;
            }
        }
            
    }
    
    

}
