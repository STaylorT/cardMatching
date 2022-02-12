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
    private boolean[] buttonsMatched = new boolean[52]; // array to see if certain button is matched
    private ImageIcon backOfCard = new ImageIcon("cardIcons/back_of_card.png");
    private boolean firstLoad = true;
    private boolean msgShown = false;

    // initialize timers
    private Timer elapsedTimer;
    private Timer flipTimer;

    // buttons for user
    private JButton resetButton;
    private JButton giveUpButton;
    private JButton quitButton;

    // JPanel for buttons
    private JPanel buttonLayout;
    // JPanel for messages
    private JPanel messageLayout = new JPanel();

    // text for displaying score
    private JLabel score = new JLabel(" | Score: 0");
    // number of GUESSES?
    private JLabel showGuesses = new JLabel(" | Guesses: 0");
    // display time elapsed
    private JLabel timeElapsed = new JLabel(" | Time Elapsed: 0");
    // End Game Message
    private JLabel message = new JLabel(" ");

    private int time = 0;
    private int matches = 0;
    private int guesses = 0;
    private int lastGuess = -1;
    private int currGuess = -1;

    public void setCards()
    {
        time = 0;
        matches = 0;
        lastGuess = -1;
        currGuess = -1;
        elapsedTimer.start();
        message.setText("");
        score.setText("Score: " + matches);
        guesses = 0;
        showGuesses.setText(" | Guesses: " + guesses);

        removeMatches();
        enableButtons();
        repaint();
        revalidate();
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
                if (i == 14 || i == 27 || i == 40){
                    whichSuit++;
                }
                // initialize cardNames and buttons Mathced
                cardNames.add("cardIcons/" + (i%13) + "_of_" + cardSuits[whichSuit] + "_icon.png");
                buttonsMatched[i-1] = false;
                displayCards();
            }
            // add default icon to buttons
            buttons.get(i-1).setIcon(backOfCard);
        }
        firstLoad = false;
        shuffle();

    }
    private void removeMatches(){
        for (int i = 0 ; i < buttonsMatched.length ; i++){
            buttonsMatched[i] = false;
        }
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
        repaint();
    }

    public void hideCard(int index){
        if (index >= 0) {
            buttons.get(index).setIcon(backOfCard);
            repaint();
        }
    }

    public void disableButtons(int index1, int index2){
        for (int i = 0 ; i < buttons.size() ; i++) {
            if (i != index1 && index2 != i && !buttonsMatched[i]){
                if (index1 == -1){
                    buttons.get(i).setDisabledIcon(new ImageIcon(cardNames.get(i)));
                }
                else {
                    buttons.get(i).setDisabledIcon(backOfCard);
                }
                buttons.get(i).setEnabled(false);
            }
        }
    }

    public void enableButtons(){
        for (int i = 0 ; i < buttons.size() ; i++) {
            if (!buttonsMatched[i]) {
                buttons.get(i).setEnabled(true);
            }
        }
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

        setUpTimers();
        quitButton = new JButton("Quit");
        resetButton = new JButton("Reset");
        giveUpButton = new JButton("Give up");

        // add event listeners
        quitButton.addActionListener(this);
        resetButton.addActionListener(this);
        giveUpButton.addActionListener(this);

        // add buttons to button JPanel;
        buttonLayout = new JPanel();
        buttonLayout.add(resetButton);
        buttonLayout.add(quitButton);
        buttonLayout.add(giveUpButton);

        // add score to JPanel

        buttonLayout.add(score);
        buttonLayout.add(showGuesses);
        buttonLayout.add(timeElapsed);

        cardLayout.setLayout( new GridLayout( 4, 13, 0, 5 ) );

        // start from beginning
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
        else if (e.getSource() == giveUpButton){ // give up
            disableButtons(-1, -1);
            gameOver();
        }
        else { // a card is being clicked
            // iterate through button ArrayList to see which button was clicked
            for (int i = 0 ; i < buttons.size() ; i++){
                if (e.getSource() == buttons.get(i) && i != lastGuess){

                    displayCard(i);// show card
                    if (lastGuess != -1 ){ // check to see if there is a previous guess in queue
                        guesses += 1;
                        showGuesses.setText("Guesses: " + guesses);
                            // check chars at 10th and 11th position (1 = ace, 02 = 2, ... 13=king )
                        if (buttons.get(lastGuess).getIcon().toString().charAt(10) == buttons.get(i).getIcon().toString().charAt(10)
                            && buttons.get(lastGuess).getIcon().toString().charAt(11) == buttons.get(i).getIcon().toString().charAt(11)) {
                                correctMatch(i);

                        }
                        else{ // If they don't share the same chars, they're not a match
                            System.out.println("Not a Match!");
                            disableButtons(i, lastGuess);
                            giveUpButton.setEnabled(false);
                            currGuess = i;
                            flipTimer.start();
                        }

                    }
                    else{ // if there is no previous guess in queue, add this guess to queue
                        lastGuess = i;
                    }
                }
            }
        }
    }
    private void setUpTimers(){
        // timer object for counting how many seconds elapsed
        elapsedTimer = new Timer(1000, new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                time += 1;
                timeElapsed.setText(" | Time Elapsed: " + time);
                repaint();
            }
        }
        );
        // timer to handle flipping of card
        flipTimer = new Timer(2000, new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                // hide both cards
                hideCard(lastGuess);
                hideCard(currGuess);
                flipTimer.stop();
                lastGuess = -1;
                enableButtons();
                giveUpButton.setEnabled(true);
            }
        }
        );
    }
    private void correctMatch(int i){ // method ran when a correct match is clicked
        System.out.println("Match!");
        matches += 1;
        // designate matches by not allowing clicks anymore!
        buttons.get(i).setDisabledIcon(buttons.get(i).getIcon());
        buttons.get(i).setEnabled(false);
        buttons.get(lastGuess).setDisabledIcon(buttons.get(lastGuess).getIcon());
        buttons.get(lastGuess).setEnabled(false);
        // encode which buttons have been matched
        buttonsMatched[i] = true;
        buttonsMatched[lastGuess] = true;
        score.setText(" | Score: " + matches);
        lastGuess = -1;
        if (matches == 26){
            gameOver();
        }
    }
    private void gameOver(){
        elapsedTimer.stop();
        Container c = getContentPane();
        messageLayout.setSize(1000, 300);
        if (matches == 0){
            message.setText("| You Won! Your Time was: " + time +  ".  |  Good job!");
            showAllCards();
            if (!msgShown) {
                messageLayout.add(message);
                msgShown = true;
            }
            resetButton.setVisible(false);
        }
        else{
            disableButtons(-1, -1);
            showAllCards();
            message.setText("| You lost. Try again by clicking 'Reset' | ");
            if (!msgShown) {
                messageLayout.add(message);
                msgShown = true;
            }
        }
        buttonLayout.add(messageLayout, BorderLayout.NORTH);
        repaint();
        revalidate();
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
