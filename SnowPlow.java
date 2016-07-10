/*
 * [SnowPlow.java]
 * Description: This program creates a 2D array with values of 1 or 2 randomly assigned, and checks the surrounding 
 *              values of the first 1 found, then subsequently found 1's recursively. All the found values will be 
 *              changed to a 0 and displayed on a GUI.
 * Allison Chow
 * October 21, 2015
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class SnowPlow extends JFrame implements ActionListener {
  
  //creates class variables
  
  //initialize scanner and create row and column variables
  static Scanner user = new Scanner(System.in);
  static int r;
  static int c;
  
  //frame for an output dialogue box to be put on
  JFrame frame = new JFrame();
  
  //grid of buttons to replicate the snow plow area and a grid of integers to store the values which will be displayed on the buttons
  int[][] grid = new int[r][c];
  static JLabel[][] board = new JLabel[r][c];
  
  //panel to hold the grid of buttons
  JPanel pan1 = new JPanel();
  
  //buttons to be added to fill the grid with numbers and change the 1s touching each other or the first occurrence to 0s
  JButton originalButton = new JButton("Create Area");
  JButton plowButton = new JButton("Plow");
  
  //layouts to be applied to the panel and frame
  GridLayout layout1 = new GridLayout(r , c);
  FlowLayout layout2 = new FlowLayout();
  
  //create array to store the positions of the 0s 
  static int gridseq[][] = new int[r*r][2];
  static int seqLen;
  static int rowFinal, colFinal;
  static int seq_index;
  static Timer timer;
  
  /*
   * The class constructor adds everything to the frame
   */
  public SnowPlow() {
    
    //set layout
    setLayout(layout2);
    
    setTitle("BSS Snow Plow");
    setSize(800 , 800);
    
    //adds action listeners to the single buttons
    originalButton.addActionListener(this);
    plowButton.addActionListener(this);      
    
    //set layout of the panel for the grid of buttons
    pan1.setLayout(layout1);
    
    //creates an array of JLabels
    for (int i = 0; i < r; i++) {
      for (int j = 0; j < c; j++) {
        board[i][j] = new JLabel();
        //sets a border around each label
        board[i][j].setBorder(BorderFactory.createLineBorder(Color.gray));
        //resizes the buttons
        board[i][j].setPreferredSize(new Dimension(70, 70));
        //adds the buttons to the panel
        pan1.add(board[i][j]);
      }
    }
    
    plowButton.setEnabled(false);
    
    //adds components to the frame
    add(originalButton);
    add(plowButton);
    add(pan1);
    
    
    //create timer 
    timer = new Timer(300, new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        //find the position of the 0s in the grid 
        rowFinal=gridseq[seq_index][0];
        colFinal=gridseq[seq_index][1];
        if (rowFinal > -1 && colFinal > -1) {
          //change the text to display 0 and change colour to red 
          board[rowFinal][colFinal].setText("0");
          board[rowFinal][colFinal].setHorizontalAlignment(SwingConstants.CENTER);
          board[rowFinal][colFinal].setForeground(Color.orange);
          board[rowFinal][colFinal].setOpaque(false);
          seqLen--;
          seq_index++;
          //keep doing until all the 0s are displayed 
          timer.restart();
        }
      }
    });
    timer.setRepeats(false);
    timer.setInitialDelay(300);
    
    setVisible(true); 
    setResizable(true);
  }
  
  /* 
   * A method to receive the desired row and column numbers from the user. These values must be greater than or equal
   * to 1. The array of JLabels will be set to the new dimensions and so will the gridsequence variable.
   */
  public static void initialize() {
    
    System.out.println("Please enter the number of rows:");
    r = user.nextInt();
    
    //will not allow the user to enter a value less than 1
    while (r < 1) {
      System.out.println("Invalid number of rows, please enter a new number.");
      r = user.nextInt();
    }
    
    System.out.println("Please enter the number of columns:");
    c = user.nextInt();
    
    while (c < 1) {
      System.out.println("Invalid number of columns, please enter a new number.");
      c = user.nextInt();
    }
    
    board = new JLabel[r][c];
    gridseq = new int[r*r][2];
    
  }
  
  /*
   * A method to perform the action received from the event listener.
   * If the command is "Create Area" the grid of buttons will be randomly assigned values of 1 or 2 by being sent to a 
   * generate method.
   * If the command is "Plow" the first occurrence of 1 will be found and sent to the clear method for the 1s to be 
   * changed to 0s. If no occurrence of 1 is found, a dialogue frame will be outputted saying that the plow has not 
   * been used.
   */
  public void actionPerformed(ActionEvent event) {
    
    String command = event.getActionCommand();
    
    if (command.equals("Create Area")) {
      
      generate(grid);
      display(grid); //call on method to display grid on gui
      plowButton.setEnabled(true);
      
    } else if (command.equals("Plow")) {
      
      findFirst(grid);
      
    }  
  } //end event method
  
  /*
   * A method to find the first value of a 1 in the first row in order to send that coordinate by index to the clear 
   * method. If no value of 1 is found in the first row, a dialogue box will be outputted to the user identifying 
   * that the snow plow has not been used (nothing has been changed)
   */
  public void findFirst(int[][] grid) {
    //finds the first occurrence of a 1 in the first row
    plowButton.setEnabled(false);
    int found = 0;
    int row = 0;
    boolean find = false;
    
    for (int i = 0; i < c; i++) {
      if (grid[row][i] == 1) {
        
        found = i;
        find = true;
        break;
        
      }
    }
    
    //calls the clear method if a 1 is found in the first row
    if (find == true) {
      
      clear(grid, row, found);
      display(grid); //call on method to display grid 
      seq_index=0;
      timer.start();
      
      //outputs a message to the user if no 1s are found in the first row
    } else if (find == false) {
      
      JOptionPane.showMessageDialog(frame, "The snow plow is not used.");
      
    }
  } //end of findFirst method.
  
  /*
   * A method that takes the grid of buttons, and indexes of the row and column of the first found 1 as parameters.
   * It will change the first 1 to a 0, and then the rest of the touching 1s until the area is surrounded by 2s.
   */
  public void clear(int[][] board, int row, int col) {
    
    if (grid[row][col] == 1) {
      
      grid[row][col] = 0;
      //store the location of the 0 
      gridseq[seq_index][0]=row;
      gridseq[seq_index][1]=col;
      seqLen++;
      seq_index++; 
      
    } else {
      
      return;
      
    }
    
    for (int i = row-1; i < row+2; i++) {
      if (i < r && i >= 0) {
        for (int j = col-1; j < col+2; j++) {
          if (j < c && j >= 0 && grid[i][j] == 1) {  
            
            clear(grid, i, j);
            
          }          
        }
      }
    }
  } //end of clear method
  
  /*
   * A method that assigns a value of either 1 or 2 to each button on the board as a String.
   */
  public static void generate(int[][] grid) {
    
    for(int i=0; i< r; i++){
      for(int y=0; y<c; y++){
        grid[i][y]=(int)(Math.random()*2)+1;
      }
    }
    //fill secondary array with -1 (values may be changed later on) 
    for (int n=0; n<2; n++){
      for (int m=0; m<(r*r); m++){
        gridseq[m][n]=-1;}}
    seqLen=0;
    seq_index=0;
  } //end of generate method
  
  public static void display(int[][] grid) {
    
    String temp;
    
    for (int i = 0; i < r; i++) {
      for (int j = 0; j < c; j++) {
        board[i][j].setBackground(Color.WHITE);
        board[i][j].setOpaque(true);
        temp = String.valueOf(grid[i][j]);
        if (temp.equals("1")) {
          board[i][j].setText("1");
          board[i][j].setHorizontalAlignment(SwingConstants.CENTER);
          board[i][j].setForeground(Color.gray);
        } else if (temp.equals("2")) {
          board[i][j].setText("2");
          board[i][j].setHorizontalAlignment(SwingConstants.CENTER);
          board[i][j].setForeground(Color.BLUE);
        }
      }
      
    }
  }
  
  /*
   * Main method to create the frame.
   */
  public static void main(String args[]) {
    
    initialize();
    SnowPlow frame1 = new SnowPlow();
    
  } //end main
  
}













