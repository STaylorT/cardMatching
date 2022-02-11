import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class CardMatch extends JFrame
        implements ActionListener {

    private static String cardSuits[] = {"clubs", "diamonds", "hearts", "spades"}; // array of suits
    private ArrayList<String> cardNames =  new ArrayList<String>() ; // arraylist of file names of card_icons
    private JPanel cardLayout = new JPanel();    // where all the cards will be placed
    private ArrayList<JButton> buttons = new ArrayList<JButton>();
    private ImageIcon backOfCard = new ImageIcon("cardIcons/back_of_card.png");
    private boolean firstLoad = true;


    // buttons for user
    private JButton resetButton;
    private JButton shuffleButton;
    private JButton giveUpButton;
    private JButton quitButton;
    // JPanel for buttons
    private JPanel buttonLayout;

    // text for displaying score
    private JLabel score;

    private int matches = 0;
    private int lastGuess = -1;

    public void setCards()
    {
        // iterate through entire deck and create button and icon components
        // then add to the JPanel holding cards (cardLayout)
        int whichSuit = 0;
        for (int i = 1; i <= 52; i++) {
            if (firstLoad) { // on first load
                // initialize JButtons and cardNames
                JButton button = new JButton();
                button.addActionListener(this);
                buttons.add(button);
                // change index in accordance to suit
                if (i == 13 || i == 26 || i == 39){
                    whichSuit++;
                    System.out.println(cardSuits[whichSuit]);
                }

                cardNames.add("cardIcons/" + (i%13) + "_of_" + cardSuits[whichSuit] + "_icon.png");
            }
            // add default icon to buttons
            buttons.get(i-1).setIcon(backOfCard);
        }
        firstLoad = false;
        displayCards();
    }

    public void displayCards(){
        cardLayout.removeAll();
        for (int i = 0 ; i < buttons.size() ; i++){
            cardLayout.add(buttons.get(i));
        }
    }
    public void showAllCards(){
        for (int i = 0 ; i < buttons.size() ; i++){
            buttons.get(i).setIcon(new ImageIcon(cardNames.get(i)));
        }
        repaint();
    }

    public void displayCard(int index){
        buttons.get(index).setIcon(new ImageIcon(cardNames.get(index)));
//        buttons.get(index).getIcon().setDescription(cardNames.get(index));
        displayCards();
    }

    public void hideCard(int index){
        buttons.get(index).setIcon(backOfCard);
        displayCards();
    }

    // method to shuffle cards into random order
    public void shuffle()
    {
        System.out.println("Shuffling Cards..");
        ArrayList<String> cardNames_copy = new ArrayList<String>(cardNames);
        cardNames.clear();
        int i = 0 ;
        while (cardNames_copy.size() > 0) {
            int randomCard = (int) (Math.random() * cardNames_copy.size());
            cardNames.add(cardNames_copy.get(randomCard));
            cardNames_copy.remove(randomCard);
            i++;
        }
    }

    public CardMatch() {
        super("Cards");
        quitButton = new JButton("Quit");
        resetButton = new JButton("Reset");
        shuffleButton = new JButton("Shuffle");
        giveUpButton = new JButton("Give up");

        // add event listeners
        quitButton.addActionListener(this);
        resetButton.addActionListener(this);
        shuffleButton.addActionListener(this);
        giveUpButton.addActionListener(this);

        // add buttons to button JPanel;
        buttonLayout = new JPanel();
        buttonLayout.add(resetButton);
        buttonLayout.add(shuffleButton);
        buttonLayout.add(quitButton);
        buttonLayout.add(giveUpButton);

        // add score to JPanel
        score = new JLabel();
        score.setText("Score: " + matches);
        buttonLayout.add(score);

        cardLayout.setLayout( new GridLayout( 4, 13, 0, 5 ) );

        // Sort cards into natural order
        setCards();

        // get the content pane, onto which everything is eventually added
        Container c = getContentPane();
        // Add panels to the container
        c.add(cardLayout, BorderLayout.NORTH);
        c.add(buttonLayout);


        setSize(1000, 1000);
        setVisible(true);
    }

    public void actionPerformed (ActionEvent e) {
        // check which button is being clicked.
        if (e.getSource() == resetButton){ // reset
            setCards();
        }
        else if (e.getSource() == quitButton){ // quit
            System.exit(0);
        }
        else if (e.getSource() == shuffleButton) { // shuffle cards
            shuffle();
        }
        else if (e.getSource() == giveUpButton){
            showAllCards();
        }
        else {
            // iterate through button ArrayList to see which button was clicked
            for (int i = 0 ; i < buttons.size() ; i++){
                if (e.getSource() == buttons.get(i) && i != lastGuess){
                    // show card
                    displayCard(i);

                    if (lastGuess != -1 ){ // check to see if there is a previous guess in queue
                            // check chars at 10th and 11th position (1 = ace, 02 = 2, ... 13=king )
                        if (buttons.get(lastGuess).getIcon().toString().charAt(10) == buttons.get(i).getIcon().toString().charAt(10)
                            && buttons.get(lastGuess).getIcon().toString().charAt(11) == buttons.get(i).getIcon().toString().charAt(11)) {
                                System.out.println("Match!");
                                matches += 1;
                                // designate matches by not allowing clicks anymore!
                                buttons.get(i).setEnabled(false);
                                buttons.get(lastGuess).setEnabled(false);
                                score.setText("Score: " + matches);

                        }
                        else{ // If they don't share the same chars, they're not a match
                            System.out.println("Not a Match!");
                            // hide both cards
                            hideCard(i);
                            hideCard(lastGuess);
                        }
                        lastGuess = -1;
                    }
                    else{ // if there is no previous guess in queue, add this guess to queue
                        lastGuess = i;
                        System.out.println("No previous guesses. Last guess now: " + lastGuess);
                    }
                }
            }
        }
    }

    public static void main(String args[])
    {
        CardMatch M = new CardMatch();
        M.addWindowListener(
                new WindowAdapter(){
                    public void windowClosing(WindowEvent e)
                    {
                        System.exit(0);
                    }
                }
        );
    }
}
