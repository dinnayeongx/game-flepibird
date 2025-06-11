import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;

public class Character {
    private String karakter;
    private int characterX = 100;
    private int characterY = 300;
    private int velocity = 0;
    private Image characterImage;
    private static final int gravity = 2;
    private static final int jumpStrength = -20;
    
    public Character(String characterPath) {
        this.karakter = characterPath;
        try {
            characterImage = ImageIO.read(getClass().getResource(characterPath));
        } catch (IOException ex) {
            System.out.println("Gambar karakter tidak ditemukan");
        }
    }
    
    public void lompat() {
        velocity = jumpStrength;
    }
    
    public void perbaruiPosisi() {
        velocity += gravity;
        characterY += velocity;
    }
    
    public void pilihKarakter(String newKarakter) {
        this.karakter = newKarakter;
        try {
            characterImage = ImageIO.read(getClass().getResource(newKarakter));
        } catch (IOException ex) {
            System.out.println("Gambar karakter tidak ditemukan");
        }
    }
    
    public void reset() {
        characterX = 100;
        characterY = 300;
        velocity = 0;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(characterX, characterY, 80, 80);
    }
    public int getX() { 
        return characterX; 
    }
    public int getY() { 
        return characterY; 
    }
    public Image getImage() { 
        return characterImage; 
    }
    public String getKarakter() { 
        return karakter; 
    }
}
