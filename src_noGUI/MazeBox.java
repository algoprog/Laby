/**
 *
 * @author Chris Samarinas
 */
public class MazeBox implements Comparable<MazeBox>{
    public boolean isObstacle;
    public boolean isVisited;
    public boolean isAdded;
    public MazeBox previous;
    public int x;
    public int y;
    public int so_far;
    public double to_go;
    MazeBox(){
        isObstacle = true;
        isVisited = false;
        isAdded = false;
        previous = null;
    }

    @Override
    public int compareTo(MazeBox o) {
        if(so_far+to_go>o.so_far+o.to_go) return 1;
        else if(so_far+to_go==o.so_far+o.to_go) return 0;
        else return -1;
    }
}
