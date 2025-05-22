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
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        MainMenuPanel menuPanel = new MainMenuPanel(this);
        String karakterPath = null;
        gamePanel = new GamePanelImpl(this, karakterPath);

        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
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

class MainMenuPanel extends JPanel {

    public MainMenuPanel(FlepiBird frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.ORANGE);
        setLayout(null);

        JLabel title = new JLabel("FLEPIBIRD", SwingConstants.CENTER);
        int titleWidth = (int) (screenWidth * 0.4);
        int titleHeight = (int) (screenHeight * 0.1);
        int titleFontSize = (int) (screenHeight * 0.07);

        title.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        title.setBounds((screenWidth - titleWidth) / 2, (int) (screenHeight * 0.2), titleWidth, titleHeight);
        add(title);

        JButton startButton = new JButton("Mulai");
        int buttonWidth = (int) (screenWidth * 0.13);
        int buttonHeight = (int) (screenHeight * 0.07);
        int buttonFontSize = (int) (screenHeight * 0.035);

        startButton.setFont(new Font("Arial", Font.BOLD, buttonFontSize));
        startButton.setBounds((screenWidth - buttonWidth) / 2, (int) (screenHeight * 0.4), buttonWidth, buttonHeight);
        startButton.addActionListener(e -> frame.showGame());
        add(startButton);

        JButton helpButton = new JButton("Petunjuk");
        helpButton.setFont(new Font("Arial", Font.BOLD, buttonFontSize));
        helpButton.setBounds((screenWidth - buttonWidth) / 2, (int) (screenHeight * 0.52), buttonWidth, buttonHeight);
        helpButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Tekan SPASI untuk melompat\nTekan R untuk mengulang saat Game Over",
                "Petunjuk", JOptionPane.INFORMATION_MESSAGE));
        add(helpButton);

        JButton exitButton = new JButton("Keluar");
        exitButton.setFont(new Font("Arial", Font.BOLD, buttonFontSize));
        exitButton.setBounds((screenWidth - buttonWidth) / 2, (int) (screenHeight * 0.64), buttonWidth, buttonHeight);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);
    }
}

abstract class GamePanel extends JPanel implements ActionListener, KeyListener {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int WIDTH = screenSize.width;
    int HEIGHT = screenSize.height;
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
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.CYAN);
        this.setFocusable(true);
        this.addKeyListener(this);

        try {
            characterImage = ImageIO.read(new File(karakterPath));
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
            g.drawImage(characterImage, characterRect.x, characterRect.y, characterRect.width, characterRect.height, null);
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
        g.drawString("Score: " + score, WIDTH / 100, HEIGHT / 20);

        if (isCountingDown) {
            g.setFont(new Font("Arial", Font.BOLD, 200));
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(countdown), WIDTH / 2 - 100, HEIGHT / 2);
            return;
        }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.drawString("Game Over!", 650, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 36));
            g.drawString("Tekan R untuk restart atau ESC untuk kembali ke menu", 450, HEIGHT / 2 + 100);
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
        characterRect = new Rectangle(300, characterY, 50, 50);
        for (int i = 0; i < 3; i++) {
            spawnPipe(i * pipeSpacing + 800);
        }
        gameOver = false;

        if (timer != null) timer.stop();
        if (countdownTimer != null) countdownTimer.stop();
        startCountdown();
    }
}
