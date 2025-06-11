import java.awt.*;

public class Rintangan {
    private int lebar;
    private int tinggi;
    private int x;
    private int y;
    private boolean passed = false;
    private static final int pipeSpeed = 9;
    
    public Rintangan(int lebar, int tinggi, int x, int y) {
        this.lebar = lebar;
        this.tinggi = tinggi;
        this.x = x;
        this.y = y;
    }
    
    public void gerak() {
        x -= pipeSpeed;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, lebar, tinggi);
    }
    
    public int getLebar() { 
        return lebar; 
    }
    public int getTinggi() { 
        return tinggi; 
    }
    public int getX() { 
        return x; 
    }
    public void setX(int x) { 
        this.x = x; 
    }
    public int getY() { 
        return y; 
    }
    public boolean isPassed() { 
        return passed; 
    }
    public void setPassed(boolean passed) { 
        this.passed = passed; 
    }
}
