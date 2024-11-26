import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;
import javax.swing.Timer;

public class MemoryGame extends JFrame {
    private final int NUM_PAIRS = 8; // Number of pairs
    private final int NUM_CARDS = NUM_PAIRS * 2;
    private final int CARD_SIZE = 100; // Adjust the size of the cards
    private Card[] cards = new Card[NUM_CARDS];
    private Card selectedCard1 = null;
    private Card selectedCard2 = null;
    private int matchedPairs = 0;
    private int currentPlayer = 1;
    private int player1Score = 0;
    private int player2Score = 0;
    private String player1Name;
    private String player2Name;
    private JLabel scoreLabel;

    public MemoryGame() {
        // Prompt for player names
        player1Name = JOptionPane.showInputDialog(null, "Enter Player 1's Name:");
        player2Name = JOptionPane.showInputDialog(null, "Enter Player 2's Name:");

        setTitle("Memory Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(4, 4));
        add(gridPanel, BorderLayout.CENTER);

        scoreLabel = new JLabel(player1Name + ": 0  " + player2Name + ": 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Serif", Font.BOLD, 20));
        add(scoreLabel, BorderLayout.SOUTH);

        // Initialize cards
        ImageIcon[] images = loadImages();
        int[] values = new int[NUM_CARDS];
        for (int i = 0; i < NUM_PAIRS; i++) {
            values[2 * i] = i;
            values[2 * i + 1] = i;
        }
        shuffleArray(values);

        // Create card buttons
        for (int i = 0; i < NUM_CARDS; i++) {
            cards[i] = new Card(values[i], images[values[i]]);
            cards[i].addActionListener(new CardClickListener());
            gridPanel.add(cards[i]);
        }
    }

    private ImageIcon[] loadImages() {
        ImageIcon[] images = new ImageIcon[NUM_PAIRS];
        for (int i = 0; i < NUM_PAIRS; i++) {
            try {
                BufferedImage img = ImageIO.read(new File("img" + (i + 1) + ".png"));
                Image scaledImg = img.getScaledInstance(CARD_SIZE, CARD_SIZE, Image.SCALE_SMOOTH);
                images[i] = new ImageIcon(scaledImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    private void shuffleArray(int[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private class CardClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Card clickedCard = (Card) e.getSource();
            if (selectedCard1 == null) {
                selectedCard1 = clickedCard;
                selectedCard1.showImage();
            } else if (selectedCard2 == null && clickedCard != selectedCard1) {
                selectedCard2 = clickedCard;
                selectedCard2.showImage();
                checkMatch();
            }
        }
    }

    private void checkMatch() {
        if (selectedCard1.getValue() == selectedCard2.getValue()) {
            selectedCard1.setMatched(true);
            selectedCard2.setMatched(true);
            matchedPairs++;
            if (currentPlayer == 1) {
                player1Score++;
            } else {
                player2Score++;
            }
            updateScore();
            if (matchedPairs == NUM_PAIRS) {
                // Determine winner
                String winner;
                if (player1Score > player2Score) {
                    winner = player1Name;
                } else if (player2Score > player1Score) {
                    winner = player2Name;
                } else {
                    winner = "It's a tie!";
                }
                JOptionPane.showMessageDialog(null, "Game Over!\nWinner: " + winner + "\nScores:\n"
                        + player1Name + ": " + player1Score + "\n" + player2Name + ": " + player2Score);
            }
            resetSelection();
        } else {
            Timer timer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectedCard1.hideImage();
                    selectedCard2.hideImage();
                    resetSelection();
                    switchPlayer();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void resetSelection() {
        selectedCard1 = null;
        selectedCard2 = null;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    private void updateScore() {
        scoreLabel.setText(player1Name + ": " + player1Score + "  " + player2Name + ": " + player2Score);
    }

    private class Card extends JButton {
        private int value;
        private boolean matched = false;
        private ImageIcon image;

        public Card(int value, ImageIcon image) {
            this.value = value;
            this.image = image;
            hideImage();
        }

        public int getValue() {
            return value;
        }

        public void showImage() {
            setIcon(image);
        }

        public void hideImage() {
            if (!matched) {
                setIcon(null);
            }
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
            if (matched) {
                setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MemoryGame().setVisible(true);
            }
        });
    }
}
