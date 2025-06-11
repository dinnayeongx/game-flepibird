import java.awt.*;
import javax.swing.*;

public class FlepiBird extends JFrame {
    private GameForm gamePanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public FlepiBird(String karakterPath) {
        setTitle("FlepiBird");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        gamePanel = new GameForm(this, karakterPath);
        mainPanel.add(gamePanel, "Game");
        
        add(mainPanel);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Menu().setVisible(true);
    }
}