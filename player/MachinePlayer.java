/* MachinePlayer.java */

package player;
import java.util.Random;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public static final int EMPTY = -1;
    public static final int SIDE = 8;
    private int color;
    private int myCount;
    private int opponentCount;
    private int[][] board;
    private int[][] copy;    
    private Random generator;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    board = new int[SIDE][SIDE];    
    copy = new int[SIDE][SIDE]; 
    myCount = 0;
    opponentCount = 0;
    for (int i = 0; i < SIDE; i++) {
        for (int j = 0; j < SIDE; j++) {      
            board[i][j] = -1;
        }
    }
    this.color = color;
    generator = new Random();
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    Move m = new Move(0,0);
    boolean moveFound = false;
    int moveColor = BLACK;
    if (this.color == WHITE) {
        moveColor = WHITE;
    }
    if (myCount < 10) {
        do {
            try {
                int x = generator.nextInt(SIDE);
                int y = generator.nextInt(SIDE);
                m = new Move(x,y);
                makeMove(m, moveColor);
                moveFound = true;
            } catch (InvalidMoveException e) {
                System.out.println(e);
            }
        } while (!moveFound);
        myCount++;
    } else {
        // there is already 10 chips on the board
        // now - randomly pick one of them
        // then pick a direction and calculate the new position
        // check that the new position is not out of bounds
        // try the move
    }
    return m;
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    int moveColor = BLACK;
    if (this.color == BLACK) {
        moveColor = WHITE;
    }
    try {
        makeMove(m, moveColor);
    } catch (InvalidMoveException e) {
        System.out.println(e);
        return false;
    }
    opponentCount++;
    return true;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    return false;
  }

    private void makeMove(Move m, int moveColor) throws InvalidMoveException {
        switch (m.moveKind) {
            case Move.QUIT:
                    throw new InvalidMoveException("Quit move is not supported yet.");
                    // break;
            case Move.ADD:
                    if (moveColor == color &&  myCount >= 10) {
                        throw new InvalidMoveException("Cannot play more than 10 chips.");
                    }
                    if (moveColor != color &&  opponentCount >= 10) {
                        throw new InvalidMoveException("Cannot play more than 10 chips.");
                    }
                    if ((m.x1 == 0 && m.y1 == 0) ||
                        (m.x1 == SIDE - 1 && m.y1 == SIDE - 1) ||
                        (m.x1 == 0 && m.y1 == SIDE - 1) ||
                        (m.x1 == SIDE - 1 && m.y1 == 0)) {
                            throw new InvalidMoveException("Cannot place a chip in a corner.");
                    }
                    if (moveColor == BLACK) {
                        if (m.x1 == 0 || m.x1 == SIDE - 1) {
                            throw new InvalidMoveException("Black cannot move here.");
                        }
                    }                        
                    if (moveColor == WHITE) {
                        if (m.y1 == 0 || m.y1 == SIDE - 1) {
                            throw new InvalidMoveException("White cannot move here.");
                        }
                    }                                           
                    if (board[m.x1][m.y1] != -1) {
                        throw new InvalidMoveException("Cell is already occupied.");
                    }
                    if (countNeighbours(m.x1, m.y1, moveColor, true) > 1) {
                        throw new InvalidMoveException("Connected group is too big.");
                    }                    
                    for (int i = 0; i < SIDE; i++) {
                        for (int j = 0; j < SIDE; j++) {
                            copy[i][j] = board[i][j];
                        }
                    }
                    board[m.x1][m.y1] = moveColor; 
                    break;
            case Move.STEP:
                    throw new InvalidMoveException("Step moves are not supported yet.");
                    // break;
            default:
                    break;              
        }
    }    
    private int countNeighbours(int x, int y, int moveColor, boolean initial) {
        int c = 0;
        for (int i = Math.max(0, x - 1); i <= Math.min(SIDE - 1, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(SIDE - 1, y + 1); j++) {
                if (board[i][j] == moveColor) {
                    if (initial) {
                        int c1 = countNeighbours(i, j, moveColor, false);
                        if (c1 > 1) {
                            return 2;
                        }
                    }
                    c++;
                }
            }
        }
        return c;
    }               
}
