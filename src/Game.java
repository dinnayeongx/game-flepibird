import java.util.ArrayList;
import java.util.Random;

public class Game {
    private Character karakter;
    private ArrayList<Rintangan> pipe;
    private Skor skor;
    private boolean gameOver = false;
    private Random rand = new Random();
    
    private static final int PIPE_WIDTH = 100;
    private static final int PIPE_GAP = 300;
    private static final int PIPE_SPACING = 350;
    private static final int PIPE_SPEED = 5;
    
    public Game(String karakterPath) {
        this.karakter = new Character(karakterPath);
        this.pipe = new ArrayList<>();
        this.skor = new Skor();
        spawnInitialPipes();
    }
    
    public void ketuk() {
        karakter.lompat();
    }
    
    public void lewatiRintangan() {
        for (int i = 0; i < pipe.size(); i++) {
            Rintangan pipa = pipe.get(i);
            pipa.setX(pipa.getX() - PIPE_SPEED);
            if (i % 2 == 0 && !pipa.isPassed() && pipa.getX() + pipa.getLebar() < karakter.getX()) {
                skor.tambahSkor();
                pipa.setPassed(true);
                if (i + 1 < pipe.size()) {
                    pipe.get(i + 1).setPassed(true);
                }
            }
        }
    }
    
    public boolean periksaTabrakan() {
        for (Rintangan p : pipe) {
            if (karakter.getBounds().intersects(p.getBounds())) {
                return true;
            }
        }
        
        if (karakter.getY() < 0 || karakter.getY() > 720) {
            return true;
        }
        
        return false;
    }
    
    public int dapatkanSkor() {
        return skor.getSkor();
    }
    
    public void update() {
        if (!gameOver) {
            karakter.perbaruiPosisi();
            updatePipes();
            lewatiRintangan();
            gameOver = periksaTabrakan();
        }
    }
    
    private void spawnInitialPipes() {
        for (int i = 0; i < 3; i++) {
            spawnPipe(i * PIPE_SPACING + 800);
        }
    }
    
    private void spawnPipe(int x) {
        int pipeHeight = 100 + rand.nextInt(300);
        pipe.add(new Rintangan(PIPE_WIDTH, pipeHeight, x, 0));
        pipe.add(new Rintangan(PIPE_WIDTH, 720 - pipeHeight - PIPE_GAP, x, pipeHeight + PIPE_GAP));
    }
    
    private void updatePipes() {
        for (Rintangan p : pipe) {
            p.gerak();
        }
        
        if (!pipe.isEmpty() && pipe.get(0).getX() + PIPE_WIDTH < 0) {
            pipe.remove(0);
            pipe.remove(0);
            spawnPipe(1000);
        }
    }
    
    public void reset() {
        karakter.reset();
        pipe.clear();
        skor.reset();
        gameOver = false;
        spawnInitialPipes();
    }
    
    public Character getKarakter() { 
        return karakter; 
    }
    public ArrayList<Rintangan> getRintangan() { 
        return pipe; 
    }
    public boolean isGameOver() { 
        return gameOver; 
    }
}
