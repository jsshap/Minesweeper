/*
* Minesweeper
* Jake Shapiro
* June 2019
 */

import java.awt.Color;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Random;

public class Minesweeper extends JPanel implements MouseListener, ActionListener, ChangeListener {

    private Cell[][] board;
    private JButton[][] visualBoard;

    private int buttonSize=20;

    private int numBoxesRevealed=0;
    private int targetScore;
    private double progress;


    private JSpinner dimensionBox;
    private JSpinner numMinesBox;
    private JComboBox difficultyBox;


    private static int height;
    private static int width;
    private static int numMines;
    private static String difficulty;


    private long initialTime=System.currentTimeMillis();
    private double time;

    private boolean won=false;
    private boolean lost=false;
    //TIMER private static boolean madeFirstClick=false;
   // TIMER private static boolean clickedNewGame;

    private static JFrame frame;

    //TIMER private Thread timer;


    //class TimePainter implements Runnable{
    //    public void run() {
    //        while (!won && !lost && !clickedNewGame) {
    //            repaint();
     //       }
     //   }
   // }


    public static void main(String[] args) {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Minesweeper mainGame = new Minesweeper();
        frame.setContentPane(mainGame);
        frame.pack();
        frame.setVisible(true);
        height=25;
        width=25;
        numMines=75;
        difficulty="Medium";
        mainGame.play(height, width, numMines);
    }

    private Minesweeper(){
        //addMouseListener(this);
        this.setPreferredSize(new Dimension(600, 600));
        //TIMER timer = new Thread(new TimePainter());

    }

    private void play(int height, int width, int numMines){
        won=false;
        lost=false;
        //madeFirstClick=false;
        //clickedNewGame=false;

        //buttonSize=500/width;
        buttonSize=20;//default
        if (100+height*buttonSize>600) {//THIS MAKES THE WINDOW BIGGER. width is kept below 37 in newGame()
            setPreferredSize(new Dimension(100+width*buttonSize, 100+height*buttonSize));
            frame.pack();
        }
        else {//THIS SETS THE MINIMUM WINDOW SIZE
            setPreferredSize(new Dimension(600, 600));
            frame.pack();
        }

        if (100+height*buttonSize<=600){
            buttonSize=Math.min(31, 500/width);//COMMENT THIS OUT TO PREVENT THE BUTTONS FROM GROWING. Current min is 31 because that's where it stops looking good
        }

        board=new Cell[height][width];
        visualBoard= new JButton[height][width];
        fillBoard(board, height, width, numMines);
        buildVisualBoard(visualBoard);
        addMenu();
        targetScore=height*width-numMines;
        numBoxesRevealed=0;
        setVisible(true);
        //TIMER timer = new Thread (new TimePainter());
        initialTime = System.currentTimeMillis();
    }

    private void addMenu(){
        JButton restart=new JButton();
        restart.setBounds(40, 15, 150, 20);
        restart.setText("New Game");
        restart.addActionListener(this);
        add(restart);
        dimensionBox= new JSpinner();
        dimensionBox.addChangeListener(this);
        dimensionBox.setBounds(200, 15, 60, 25);
        dimensionBox.setValue(height);
        add(dimensionBox);


        numMinesBox = new JSpinner();
        numMinesBox.addChangeListener(this);
        numMinesBox.setBounds(275, 15, 60, 25);
        numMinesBox.setValue(numMines);
        add(numMinesBox);

        //DO NOT CHANGE ORDER OF ADDING OF BOXES IN THIS METHOD. THE DIFFICULTY BOX MUST BE ADDED LAST
        //SEE COMMENT IN stateChanged() for more details

        String[] difficultyChoices = new String[] {"Custom", "Easy", "Medium", "Medium++", "Hard", "Extreme"};
        difficultyBox = new JComboBox<>(difficultyChoices);
        difficultyBox.setBounds(340, 16, 120, 25);
        int indexOfChoice=2;
        for (int i=0; i<difficultyChoices.length; i++){
            if (difficultyChoices[i].equals(difficulty)){
                indexOfChoice=i;
            }
        }
        difficultyBox.setSelectedIndex(indexOfChoice);
        add(difficultyBox);

    }
    private void buildVisualBoard(JButton[][] visualBoard) {
        for (int i=0; i<visualBoard.length; i++){
            for (int j=0; j<visualBoard[i].length; j++){
                visualBoard[i][j]= new JButton();
                setLayout(null);
                visualBoard[i][j].setBounds(50+i*buttonSize, 50+j*buttonSize, buttonSize, buttonSize);
                add(visualBoard[i][j]);
                visualBoard[i][j].addActionListener(this);
                setVisible(false);
                visualBoard[i][j].addMouseListener(this);
            }
        }
    }

    private void fillBoard(Cell[][] b, int height, int width, int numMines) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                b[i][j] = new Cell();
            }
        }
        int minesPlaced = 0;
        Random rand = new Random();
        while (minesPlaced < numMines) {
            int h = rand.nextInt(height);
            int w = rand.nextInt(width);
            if (!b[h][w].hasMine()) {
                b[h][w].addMine();
                minesPlaced++;
            }
        }
        setMinesBordered(b, height, width);
    }

    private void setMinesBordered(Cell[][] b, int height, int width){
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                int minesBordered=0;
                if ((i-1)>=0 && j-1>=0 && b[i-1][j-1].hasMine())
                    minesBordered++;
                if (i-1>=0 && b[i-1][j].hasMine())
                    minesBordered++;
                if (i-1>=0 && j+1<width && b[i-1][j+1].hasMine())
                    minesBordered++;
                if (j-1>=0 && b[i][j-1].hasMine())
                    minesBordered++;
                if (j+1< width && b[i][j+1].hasMine())
                    minesBordered++;
                if (i+1 < height && j-1>= 0 && b[i+1][j-1].hasMine())
                    minesBordered++;
                if (i+1 < height && b[i+1][j].hasMine())
                    minesBordered++;
                if (i+1<height && j+1< width && b[i+1][j+1].hasMine())
                    minesBordered++;
                b[i][j].setMinesBordered(minesBordered);
            }
        }
    }

    private void action(ActionEvent e){
        JButton button =  (JButton) e.getSource();
        int x = button.getX();
        int y = button.getY();

        //TIMER if (!madeFirstClick && y>50 ) {
        //    timer.start();
        //    initialTime=System.currentTimeMillis();
        //    madeFirstClick=true;
        //}


       if (y<50) {//if it is the new game button
         //TIMER  madeFirstClick=false;
         //TIMER  clickedNewGame=true;
           newGame();
           return;
       }
       x = (x-50)/buttonSize;
       y = (y-50)/buttonSize;



       reveal(board, x, y);
       drawBoard();
    }


    private void newGame(){
        for (JButton[] jb : visualBoard) {
            for (JButton j : jb) {
                remove(j);
            }
        }
        difficulty=(String) difficultyBox.getSelectedItem();
        if(difficulty==null){
            difficulty="DEFAULT";
        }
        if (difficulty.equals("Custom")) {
            int i = (int) dimensionBox.getValue();
            if (i > 37) {//This is the maximum size my computer can fit without making the buttons smaller
                i = 37;
                numMines=164;
            }
            height = i;
            width = i;
            numMines = (int) (numMinesBox.getValue());
            if (numMines > i * i) {
                numMines = (i * i / 2);
            }
        }
        else{
            switch (difficulty) {
                case "Easy":
                    height = 10;
                    width = 10;
                    numMines = 10;
                    break;
                case "Medium":
                    height = 25;
                    width = 25;
                    numMines = 75;
                    break;
                case "Medium++":
                    height=16;
                    width=16;
                    numMines=40;
                    break;
                case "Hard":
                    height = 30;
                    width = 30;
                    numMines = 160;
                    break;
                case "Extreme":
                    height = 36;
                    width = 36;
                    numMines = 260;
                    break;
                default:
                    height = 25;
                    width = 25;
                    numMines = 75;
                    break;
            }
        }
        removeAll();
        play(height,width,numMines);
    }



    private void reveal(Cell[][] c, int x, int y){
        if (c[x][y].getState()==1)
            return;
        else if (c[x][y].getState()==2&&lost)
            c[x][y].setState(1);
        else if (c[x][y].getState()==0 && !won) {
            c[x][y].setState(1);
            if (c[x][y].hasMine()) {
                if (!lost) {//(yet)
              //      System.out.println("Game over");
                    progress = (double) ((int) (((double) numBoxesRevealed/targetScore)*10000))/100;
                    /*the right most makes a decimal double. It is then multiplied by 100000 to make it a 4 digit number
                    * with digits after the decimal. Casting to an int removes everything after the decimal.
                    * Then it is made a double again (now 4 digits) and divided by 100 to get a number
                    * with exactly two digits after the decimal
                     */
                }
                lost = true;
                for (int i = 0; i < c.length; i++) {
                    for (int j = 0; j < c[1].length; j++) {
                        if (c[i][j].getState() != 1) {//prevents repeatedly trying to reveal same cells, causing stack overflow
                            reveal(c, i, j);
                        }
                    }
                }
                repaint();
                return;
            }
            else {
                numBoxesRevealed++;
            }
            if (c[x][y].getMinesBordered() == 0) {
                revealAllNeighbors(c, x, y);
            }
        }
        if (numBoxesRevealed==targetScore && !lost && !won) {
            //System.out.println("You Win");
            //System.out.printf ("You won in %.3f seconds.\n", ((double) System.currentTimeMillis()-initialTime)/1000);
            //time = (((double) System.currentTimeMillis() - initialTime) / 1000);
            won=true;
            time = (((double) System.currentTimeMillis() - initialTime) / 1000);
            int t = (int) (time*10);
            time = (double) t/10;
            repaint();
        }
    }
    private void revealAllNeighbors(Cell[][] c, int x, int y) {
        if (x - 1 >= 0) {
            reveal(c, x - 1, y);
            if (y - 1 >= 0) {
                reveal(c, x - 1, y - 1);
                reveal(c, x, y - 1);
            }
            if (y+1<c.length){
                reveal(c, x, y+1);
                reveal (c, x-1, y+1);
            }
        }
        if (x+1<c[1].length){
            reveal(c, x+1, y);
            if (y-1>=0){
                reveal (c, x+1, y-1);
                reveal (c, x, y-1);//I believe this line of code is redundant. It works, though, so no reason to change it
            }
            if (y+1<c.length) {
                reveal(c, x + 1, y + 1);
                reveal(c, x, y+1);
            }
        }
        repaint();
    }


    private void revealDeducedSafeSquares(Cell[][] c, int x, int y){
        //THIS IS THE METHOD ACTIVATED by right clicking a square when all mines surrounding it have been flagged
        //THIS IS for when the number of flags surrounding a square equals the number on that square
        //and you want to just reveal everything surrounding it
        //Because it still uses the same reveal method, and that method does not reveal flagged squares, this reveals everything
        //that is not flagged

        //THIS IS ALSO CALLED FROM MOUSE RELEASED SO IT CAN BE DONE WITH LEFT CLICK (COMMENTED OUT AS OF 6/12/19)
        if (c[x][y].getMinesBordered()==getFlagsBordered(c, x, y) && c[x][y].getMinesBordered() != 0 && c[x][y].getState()==1) {
            //System.out.println(c[x][y].getMinesBordered());
            revealAllNeighbors(c, x, y);
            drawBoard();
            repaint();
        }

    }
    private int getFlagsBordered(Cell[][] c, int x, int y){

        int flagsBordered=0;
        if ((x-1)>=0 && y-1>=0 && c[x-1][y-1].getState()==2)
            flagsBordered++;
        if (x-1>=0 && c[x-1][y].getState()==2)
            flagsBordered++;
        if (x-1>=0 && y+1<width && c[x-1][y+1].getState()==2)
            flagsBordered++;
        if (y-1>=0 && c[x][y-1].getState()==2)
            flagsBordered++;
        if (y+1< width && c[x][y+1].getState()==2)
            flagsBordered++;
        if (x+1 < height && y-1>= 0 && c[x+1][y-1].getState()==2)
            flagsBordered++;
        if (x+1 < height && c[x+1][y].getState()==2)
            flagsBordered++;
        if (x+1<height && y+1< width && c[x+1][y+1].getState()==2)
            flagsBordered++;
        //System.out.println(flagsBordered);
        return flagsBordered;

    }



    private void drawBoard(){
        for (int i=0; i<visualBoard.length; i++) {
            for (int j = 0; j < visualBoard[i].length; j++) {
                if (board[i][j].getState() == 1) {
                    visualBoard[i][j].setBackground(Color.GRAY);
                    visualBoard[i][j].setOpaque(true);
                    visualBoard[i][j].setEnabled(false);
                    if (board[i][j].hasMine()) {
                        visualBoard[i][j].setText("M");
                        visualBoard[i][j].setBackground(Color.RED);
                    }
                    else if (board[i][j].getMinesBordered() != 0) {
                        visualBoard[i][j].setText(String.valueOf(board[i][j].getMinesBordered()));
                    }
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawString("Size:", 204, 13);
        g.drawString("Mines:", 279, 13);
        g.drawString("Difficulty:", 346, 13);
        if (won) {
            g.drawString("Time: " + String.valueOf(time) + " seconds", 461, 40);
            g.drawString("YOU WIN!", 461, 25);
        }
        if (lost){
            g.setColor(Color.RED);
            g.drawString("YOU LOSE :(", 461, 25);
            g.drawString(String.valueOf(progress)+"% completion", 461, 40);
        }
        if (!won) {
            time = (((double) System.currentTimeMillis() - initialTime) / 1000);
            int t = (int) (time * 10);
            time = (double) t / 10;
        }
        //TIMER if (!madeFirstClick){
        //    time=0;
        //}
        //if (!lost)
        //   g.drawString("Time: " + String.valueOf(time), 461, 40);
   }

   private void rightClickFunction(MouseEvent e){
       JButton button =  (JButton) e.getSource();
       int x = button.getX();
       int y = button.getY();
       x = (x-50)/buttonSize;
       y = (y-50)/buttonSize;
       if (board[x][y].getState()==0) {
           board[x][y].setState(2);
           button.setText("X");
       }
       else if (board[x][y].getState()==1){
           revealDeducedSafeSquares(board, x, y);
       }
       else if (board[x][y].getState()==2){
           board[x][y].setState(0);
           button.setText("");
       }
   }



    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton()==3) {
            rightClickFunction(e);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() instanceof JButton) {
            action(e);

        }
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(numMinesBox) || e.getSource().equals(dimensionBox)){
            try{difficultyBox.setSelectedIndex(0);}
            catch(Exception a){
                //a.printStackTrace();
            }
            /* This try catch-block fixes the problem of this method firing when the
             * size and number of mines are set the the default values for the difficulty
             * levels. Because the values are set before the difficulty box is created, this will throw a null
             * pointer exception, so difficultyBox will not be set to custom when the method
             * detects the number of mines and the size were set to non-custom values.
             *
             * Essentially, this prevents changes to non-custom values from setting it to custom.
             *
             *
             *
             * IMPORTANT: I'm leaving the comment above, but I actually think it is NOT ACCURATE.
             * the try-catch is needed because actions occur before the difficulty box is instantiated,
             * but the code after it is instantiated in add menu sets the selected index to the correct one
             * after the actions that would set it to custom incorrectly.
             *
             * EXCEPTION IS CAUSED BY THE ACTION OF SETTING VALUES TO NON-custom values occurs before difficulty box exists.
             * The stateChanged method fires but there is no difficulty box.
             */
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        /*

        NOTE: when a hidden cell is revealed, it recognizes the button push first, reveals the square and stuff
        and then recognizes mouseReleased. This means that the below code WILL run even if the cell was previously hidden.
        This should not hurt the player unless they incorrectly flagged something. It might help them because, for example,
        they might flag a square and reveal a neighbor with a 1. That reveal of the one will ALSO activate this and
        reveal the neighbors of the one. This is not really a problem because a player would probably want to do that,
        but it should be noted that this occurs automatically.

        ^^Currently not true because it was commented out on 6/12/19

        */

        /*
        JButton clickedButton = (JButton) e.getSource();
        int x = clickedButton.getX();
        int y = clickedButton.getY();
        x = (x - 50) / buttonSize;
        y = (y - 50) / buttonSize;
        if (e.getButton()==1) {
            if (!clickedButton.isEnabled() && board[x][y].getState() == 1) {//IDEALLY, this "if" prevents what the above comment described, but in practice it doesn't.
                revealDeducedSafeSquares(board, x, y);
            }
        }
        */
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}