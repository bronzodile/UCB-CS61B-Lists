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
    private Random generator;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    board = new int[SIDE][SIDE];    
    myCount = 0;
    opponentCount = 0;
    for (int i = 0; i < SIDE; i++) {
        for (int j = 0; j < SIDE; j++) {      
            board[i][j] = EMPTY;
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
    int moveColor = this.color;
    if (myCount < 10) {
        do {
            try {
                int x = generator.nextInt(SIDE);
                int y = generator.nextInt(SIDE);
                m = new Move(x,y);
                makeMove(x, y, moveColor);
                moveFound = true;
            } catch (InvalidMoveException e) {
                System.out.println(e);
            }
        } while (!moveFound);
    } else {
        // there is already 10 chips on the board
        // now - randomly pick one of them
        
        int targetChip = generator.nextInt(10) + 1;
        int currChip = 0;
        int chipX = 0;
        int chipY = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (board[i][j] == color) {
                    currChip++;
                    if (currChip == targetChip) {
                        chipX = i;
                        chipY = j;
                    }
                }
            }
        }
        // then hide the selected chip and pick new position
        board[chipX][chipY] = EMPTY;
        myCount--;
        do {
            try {
                int x = generator.nextInt(SIDE);
                int y = generator.nextInt(SIDE);
                if (x != chipX || y != chipY) {
                    m = new Move(x,y,chipX,chipY);
                    makeMove(x, y, moveColor);
                    moveFound = true;
                }
            } catch (InvalidMoveException e) {
                System.out.println(e);
            }
        } while (!moveFound);
    }
    myCount++;
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
    switch (m.moveKind) {
        case Move.QUIT:
            System.out.println("Quit move is not supported yet.");
            return false;
            // break;
        case Move.ADD:
            try {
                makeMove(m.x1, m.y1, moveColor);
            } catch (InvalidMoveException e) {
                System.out.println(e);
                return false;
            }
            opponentCount++;
            return true;
            // break;
        case Move.STEP:
            if (board[m.x2][m.y2] != moveColor) {
                System.out.println("No appropriate chip in the selected square.");
                return false;
            }
            int saveChip = board[m.x2][m.y2];
            board[m.x2][m.y2] = EMPTY;
            opponentCount--;
            try {
                makeMove(m.x1, m.y1, moveColor);
            } catch (InvalidMoveException e) {
                System.out.println(e);
                board[m.x2][m.y2] = saveChip;
                opponentCount++;
                return false;
            }
            opponentCount++;
            return true;
            // break;
        default:
            return false;
            // break;
            
    }
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    return false;
  }

  // Try the move as described by the parameters and update the internal state
  // if the move is legal; throw an exception otherwise
    private void makeMove(int x, int y, int moveColor) throws InvalidMoveException {
        if (moveColor == color &&  myCount >= 10) {
            throw new InvalidMoveException("Cannot play more than 10 chips.");
        }
        if (moveColor != color &&  opponentCount >= 10) {
            throw new InvalidMoveException("Cannot play more than 10 chips.");
        }
        if ((x == 0 && y == 0) ||
            (x == SIDE - 1 && y == SIDE - 1) ||
            (x == 0 && y == SIDE - 1) ||
            (x == SIDE - 1 && y == 0)) {
            throw new InvalidMoveException("Cannot place a chip in a corner.");
        }
        if (moveColor == BLACK) {
            if (x == 0 || x == SIDE - 1) {
                throw new InvalidMoveException("Black cannot move here.");
            }
        }
        if (moveColor == WHITE) {
            if (y == 0 || y == SIDE - 1) {
                throw new InvalidMoveException("White cannot move here.");
            }
        }
        if (board[x][y] != -1) {
            throw new InvalidMoveException("Cell is already occupied.");
        }
        if (countNeighbours(x, y, moveColor, true) > 1) {
            throw new InvalidMoveException("Connected group is too big.");
        }
        board[x][y] = moveColor;
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
