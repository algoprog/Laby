
import java.util.ArrayList;

/**
 *
 * @author Chris Samarinas
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {   
        int w = 16;
        int h = 16;
        MazeGenerator generator = new MazeGenerator(w,h,false);
        int maze[][] = generator.generate(0);
        w = maze[0].length;
        h = maze.length;

        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                System.out.print(maze[i][j]);
                if(j<w-1) System.out.print(" ");
            }
            System.out.print("\n");
        }
        
        System.out.println("-------------------------------------");
        
        
        MazeSolverDBFS solver = new MazeSolverDBFS(maze,false,true);
        ArrayList<MazeBox> solution = solver.solve(0);
        
        if(solution!=null){
            //for(MazeBox box : solution) {
                //System.out.println("("+box.x+","+box.y+")");
            //}
            System.out.println("DFS - solution length: "+ (solution.size()-1) + " steps: " + solver.getSteps() + " max front set size: " + solver.getMaxFront());
            
            MazeSolverDBFS solver2 = new MazeSolverDBFS(maze,false,false);
            solution = solver2.solve(0);
            System.out.println("BFS - solution length: "+ (solution.size()-1) + " steps: " + solver2.getSteps() + " max front set size: " + solver2.getMaxFront());
            
            MazeSolverID solver3 = new MazeSolverID(maze,false,3);
            while(solver3.nextStep(0)){
                //...
            }
            solution = solver3.getSolution();
            if(solution!=null) System.out.println("ID - solution length: "+ (solution.size()-1) + " steps: " + solver3.getSteps() + " max front set size: " + solver3.getMaxFront());
            else System.out.println("ID - no solution!");
            
            MazeSolverHC solver4 = new MazeSolverHC(maze,true);
            solution = solver4.solve(0);
            if(solution!=null) System.out.println("HC - solution length: "+ (solution.size()-1) + " steps: " + solver4.getSteps() + " max front set size: " + solver4.getMaxFront());
            else System.out.println("HC - no solution!");
            
            MazeSolverBF solver5 = new MazeSolverBF(maze,false,true);
            solution = solver5.solve(0);
            System.out.println("Best-First - solution length: "+ (solution.size()-1) + " steps: " + solver5.getSteps() + " max front set size: " + solver5.getMaxFront());
            
            solver5 = new MazeSolverBF(maze,true,true);
            solution = solver5.solve(0);
            System.out.println("A* - solution length: "+ (solution.size()-1) + " steps: " + solver5.getSteps() + " max front set size: " + solver5.getMaxFront());
        }else{
            System.out.println("no solution!");
        }
    }
    
}
