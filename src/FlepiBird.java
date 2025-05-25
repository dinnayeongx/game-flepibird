import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class FlepiBird extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;
    GamePanelImpl gamePanel;

    public FlepiBird(String sharkpng) {
        setTitle("FlepiBird");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        String karakterPath = sharkpng;
        gamePanel = new GamePanelImpl(this, karakterPath);

        mainPanel.add(gamePanel, "Game");
        
//        Menu menuPanel = new Menu(this);
//        mainPanel.add(menuPanel, "Menu");

        add(mainPanel);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showGame() {
        cardLayout.show(mainPanel, "Game");
        gamePanel.requestFocusInWindow();
        gamePanel.resetGame(); 
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "Menu");
    }

    public static void main(String[] args) {
        new FlepiBird("shark.png");
    }
}

abstract class GamePanel extends JPanel implements ActionListener, KeyListener {
    int WIDTH = 1280;
    int HEIGHT = 720;
}

class GamePanelImpl extends GamePanel {

    Timer timer;
    Timer countdownTimer;
    int countdown = 3;
    boolean isCountingDown = true;

    int characterY = HEIGHT / 2;
    int velocity = 0;
    int gravity = 2;
    int jumpStrength = -20;
    boolean gameOver = false;
    Image characterImage;
    Rectangle characterRect;

    ArrayList<Rectangle> pipes = new ArrayList<>();
    ArrayList<Boolean> pipePassed = new ArrayList<>();
    int pipeSpacing = 350;
    int pipeWidth = 100;
    int pipeGap = 300;
    int pipeSpeed = 9;
    int score = 0;

    Random rand = new Random();
    FlepiBird frame;

    GamePanelImpl(FlepiBird frame, String karakterPath) {
        this.frame = frame;
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.CYAN);
        this.setFocusable(true);
        this.addKeyListener(this);

        try {
            characterImage = ImageIO.read(getClass().getResource(karakterPath));
        } catch (IOException ex) {
            System.out.println("No image.");
        }

        resetGame(); 
    }

    void startCountdown() {
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    void draw(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if (characterImage != null) {
            g.drawImage(characterImage, characterRect.x, characterRect.y, 100, 100, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(characterRect.x, characterRect.y, characterRect.width, characterRect.height);
        }

        g.setColor(Color.GREEN.darker());
        for (Rectangle pipe : pipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Score: " + score, 30, 60);

        if (isCountingDown) {
            g.setFont(new Font("Arial", Font.BOLD, 200));
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(countdown), WIDTH / 2 - 100, HEIGHT / 2);
            return;
        }

        if (gameOver) {
            String text = "Game Over!";
            Font font = new Font("Arial", Font.BOLD, 72);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            int x = (WIDTH - textWidth) / 2;
            int y = (HEIGHT / 2) + (fm.getAscent() / 2);

            g.setColor(Color.BLACK);
            g.drawString(text, x, y);

            String subText = "Tekan R untuk restart atau ESC untuk kembali ke menu";
            Font subFont = new Font("Arial", Font.PLAIN, 36);
            g.setFont(subFont);
            FontMetrics fm2 = g.getFontMetrics(subFont);
            int subTextWidth = fm2.stringWidth(subText);
            int subX = (WIDTH - subTextWidth) / 2;
            int subY = y + fm2.getHeight() + 20;

            g.drawString(subText, subX, subY);
        }
    }

    void spawnPipe(int x) {
        int pipeHeight = 100 + rand.nextInt(HEIGHT / 2);
        pipes.add(new Rectangle(x, 0, pipeWidth, pipeHeight));
        pipes.add(new Rectangle(x, pipeHeight + pipeGap, pipeWidth, HEIGHT - pipeHeight - pipeGap));
        pipePassed.add(false);
        pipePassed.add(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !isCountingDown) {
            velocity += gravity;
            characterY += velocity;
            characterRect.y = characterY;

            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= pipeSpeed;

                if (i % 2 == 0 && !pipePassed.get(i) && pipe.x + pipeWidth < characterRect.x) {
                    score++;
                    pipePassed.set(i, true);
                    if (i + 1 < pipePassed.size()) {
                        pipePassed.set(i + 1, true);
                    }
                }
            }

            if (!pipes.isEmpty() && pipes.get(0).x + pipeWidth < 0) {
                pipes.remove(0);
                pipes.remove(0);
                pipePassed.remove(0);
                pipePassed.remove(0);
                spawnPipe(1000);
            }

            for (Rectangle pipe : pipes) {
                if (pipe.intersects(characterRect)) {
                    gameOver = true;
                }
            }

            if (characterY > HEIGHT || characterY < 0) {
                gameOver = true;
            }
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver && !isCountingDown) {
            velocity = jumpStrength;
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            resetGame();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameOver) {
            if (timer != null) timer.stop();
            frame.showMenu();
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    void resetGame() {
        characterY = HEIGHT / 2;
        velocity = 0;
        score = 0;
        pipes.clear();
        pipePassed.clear();

        if (characterImage != null) {
            characterRect = new Rectangle(100, characterY, 100, 100);
        } else {
            characterRect = new Rectangle(100, characterY, 50, 50);
        }

        for (int i = 0; i < 3; i++) {
            spawnPipe(i * pipeSpacing + 800);
        }
        gameOver = false;

        if (timer != null) timer.stop();
        if (countdownTimer != null) countdownTimer.stop();
        startCountdown();
    }
}