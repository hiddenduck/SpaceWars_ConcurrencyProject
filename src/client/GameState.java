import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

class Triple{
    public float[] floats;
    public char[] chars;

    public Triple(float x, float y, float z){
        this.floats = new float[3];
        this.floats[0] = x;
        this.floats[1] = y;
        this.floats[2] = z;
    }

    public Triple(float x, float y, char z){
        this.floats = new float[2];
        this.chars = new char[1];
        this.floats[0] = x;
        this.floats[1] = y;
        this.chars[0] = z;
    }

    public Triple(float[] floats, char[] chars){
        this.floats = floats;
        this.chars = chars;
    }

    public Triple clone(){
        return new Triple(this.floats, this.chars);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple triple = (Triple) o;
        return this.floats[0] == triple.floats[0] && this.floats[1] == triple.floats[1] && triple.chars[0] == this.chars[0];
    }

    public int hashCode() {
        int result = Arrays.hashCode(floats);
        result = 31 * result + Arrays.hashCode(chars);
        return result;
    }
}

public class GameState {

    public ReadWriteLock lrw = new ReentrantReadWriteLock();
    public float posX, posY, enemyPosX, enemyPosY, alfa, enemyAlfa;

    public String point, enemyPoint;

    public String gameStatus;

    public Set<Triple> boxes;

    public GameState(){
        this.lrw = new ReentrantReadWriteLock();
        this.point = "0";
        this.enemyPoint = "0";
        this.boxes = new HashSet<>();
    }

    public GameState(float posX, float posY, float enemyPosX, float enemyPosY, float alfa, float enemyAlfa,
                     String point, String enemyPoint, String gameStatus, Set<Triple> boxes){
        this.posX = posX;
        this.posY = posY;
        this.enemyPosX = enemyPosX;
        this.enemyPosY = enemyPosY;
        this.alfa = alfa;
        this.enemyAlfa = enemyAlfa;
        this.point = point;
        this.enemyPoint = enemyPoint;
        this.gameStatus = gameStatus;
        this.boxes = boxes.stream().map(Triple::clone).collect(Collectors.toSet());
    }

    public void putPos(float x, float y, float alfa, boolean enemy){
        if(!enemy){
            this.posX = x;
            this.posY = y;
            this.alfa = alfa;
        } else{
            this.enemyPosX = x;
            this.enemyPosY = y;
            this.enemyAlfa = alfa;
        }
    }

    public void putPoint(String point, String enemyPoint){
        this.point = point;
        this.enemyPoint = enemyPoint;
    }

    public void putBox(Triple newBox){
        this.boxes.add(newBox);
    }

    public void removeBoxes(Set<Triple> oldBoxes){
        for(Triple triple: oldBoxes){
            this.boxes.remove(triple);
        }
    }

    public void removeBox(Triple box){
        this.boxes.remove(box);
    }

    public void setGameStatus(String s){
        this.gameStatus = s;
    }

    public GameState copy(){
        return new GameState(this.posX, this.posY, this.enemyPosX, this.enemyPosY,
                this.alfa, this.enemyAlfa, this.point, this.enemyPoint, this.gameStatus, this.boxes);
    }

}