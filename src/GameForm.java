import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class GameForm extends JPanel implements ActionListener, KeyListener {
    private Game game;
    private FlepiBird frame;
    private Timer timer;
    private Timer countdownTimer;
    private int countdown = 3;
    private boolean isCountingDown = true;
    
    public GameForm(FlepiBird frame, String karakterPath) {
        this.frame = frame;
        this.game = new Game(karakterPath);
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.CYAN);
        setFocusable(true);
        addKeyListener(this);
        startCountdown();
    }
    
    private void startCountdown() {
        isCountingDown = true;
        countdown = 3;
        repaint();
        
        countdownTimer = new Timer(1000, e -> {
            countdown--;
            repaint();
            if (countdown <= 0) {
                countdownTimer.stop();
                isCountingDown = false;
                timer = new Timer(20, this);
                timer.start();
            }
        });
        countdownTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        Character karakter = game.getKarakter();
        if (karakter.getImage() != null) {
            g.drawImage(karakter.getImage(), karakter.getX(), karakter.getY(), 80, 80, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(karakter.getX(), karakter.getY(), 80, 80);
        }
        
        g.setColor(Color.GREEN.darker());
        for (Rintangan pipe : game.getRintangan()) {
            g.fillRect(pipe.getX(), pipe.getY(), pipe.getLebar(), pipe.getTinggi());
        }
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Score: " + game.dapatkanSkor(), 30, 60);
        
        if (isCountingDown) {
            g.setFont(new Font("Arial", Font.BOLD, 200));
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(countdown), getWidth() / 2 - 100, getHeight() / 2);
            return;
        }
        
        if (game.isGameOver()) {
            String text = "Game Over!";
            Font font = new Font("Arial", Font.BOLD, 72);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(text);
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() / 2) + (fm.getAscent() / 2);
            
            g.setColor(Color.BLACK);
            g.drawString(text, x, y);
            
            String subText = "Tekan R untuk restart atau ESC untuk kembali ke menu";
            Font subFont = new Font("Arial", Font.PLAIN, 36);
            g.setFont(subFont);
            FontMetrics fm2 = g.getFontMetrics(subFont);
            int subTextWidth = fm2.stringWidth(subText);
            int subX = (getWidth() - subTextWidth) / 2;
            int subY = y + fm2.getHeight() + 20;
            
            g.drawString(subText, subX, subY);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        game.update();
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !game.isGameOver() && !isCountingDown) {
            game.ketuk();
        } else if (e.getKeyCode() == KeyEvent.VK_R && game.isGameOver()) {
            resetGame();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && game.isGameOver()) {
            if (timer != null) timer.stop();
            Menu menu = new Menu();
            menu.setVisible(true);
            frame.dispose();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    
    public void resetGame() {
        game.reset();
        if (timer != null) timer.stop();
        if (countdownTimer != null) countdownTimer.stop();
        startCountdown();
    }
}
