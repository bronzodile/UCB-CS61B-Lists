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
    public static final int X = 0;
    public static final int Y = 1;
    private int color;
    private int myCount;
    private int oppCount;
    private int opponentCount;
    private int[][] board;
    private int[][][] chips;
    private Random generator;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    board = new int[SIDE][SIDE];
    chips = new int[2][10][2];    
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
    Move m;
    m = chooseRandomMove();
    makeMove(m,this.color);
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
    if (isMoveValid(m,moveColor)){
        makeMove(m,moveColor);        
        return true;
    } else {
        return false;
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

    private Move chooseRandomMove(){
        Move[] movesArray = getMoves(color);
        return movesArray[generator.nextInt(movesArray.length)];
    }

    private int countNeighbours(int x, int y, int moveColor, boolean initial) {
        int c = 0;
        for (int i = Math.max(0, x - 1); i <= Math.min(SIDE - 1, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(SIDE - 1, y + 1); j++) {
                if ((i != x) || (j != y)) {
                    if (board[i][j] == moveColor) {
                        if (initial) {
                            int c1 = countNeighbours(i, j, moveColor, false);
                            if (c1 > 1) {
                                return c1;
                            }
                        }
                        c++;
                    }
                }
            }
        }
        return c;
    }
    private boolean isMoveValid(Move m, int mColor) {

        // if destination is not free - false
        if (board[m.x1][m.y1] != EMPTY) {
            // System.out.println("Cell is occupied.");
            return false;
        }
        // if position is out of bounds - false
        if ((m.x1 == 0 && m.y1 == 0) ||
            (m.x1 == SIDE - 1 && m.y1 == SIDE - 1) ||
            (m.x1 == 0 && m.y1 == SIDE - 1) ||
            (m.x1 == SIDE - 1 && m.y1 == 0)) {
            // System.out.println("Corner move.");
            return false;
        }
        if (mColor == BLACK) {
            if (m.x1 == 0 || m.x1 == SIDE - 1) {
                // System.out.println("Black cannot move here.");
                return false;
            }
        }
        if (mColor == WHITE) {
            if (m.y1 == 0 || m.y1 == SIDE - 1) {
                // System.out.println("White cannot move here.");
                return false;
            }
        }
        if (m.moveKind == Move.ADD) {
            if (mColor == color) {
                if (this.myCount >= 10) {
                    // System.out.println("Too many chips!");
                    return false;
                }
            } else {
                if (this.oppCount >= 10) {
                    // System.out.println("Too many chips!");
                    return false;
                }
            }
        } else {
            if (mColor == color) {
                if (this.myCount < 10) {
                    // System.out.println("Too few chips!");                
                    return false;
                }
            } else {
                if (this.oppCount < 10) {
                    // System.out.println("Too few chips!");                                
                    return false;
                }
            }
        }
        
        if (m.moveKind == Move.STEP) {
            if (board[m.x2][m.y2] != mColor) {
                // System.out.println("Attempt to move opponent's chip.");                                            
                return false;
            }
            if (m.x1 == m.x2 && m.y1 == m.y2) {
                // System.out.println("Attempt to move a chip onto itself.");                                            
                return false;
            }
            board[m.x2][m.y2] = EMPTY;
        }
        
        board[m.x1][m.y1] = mColor;
        int cNeighbours = countNeighbours(m.x1, m.y1, mColor, true);
        // System.out.println("The count is => " + cNeighbours);
        if (cNeighbours > 1) {
            // System.out.println("Connected group is too big: " + cNeighbours);
            if (m.moveKind == Move.STEP) {
                board[m.x2][m.y2] = mColor;
            }
            board[m.x1][m.y1] = EMPTY;
            return false;
        } else {
            if (m.moveKind == Move.STEP) {
                board[m.x2][m.y2] = mColor;
            }
            board[m.x1][m.y1] = EMPTY;
            return true;
        }
    }
    
    private void undoMove(Move m, int mColor) {
        board[m.x1][m.y1] = EMPTY;
        if (m.moveKind == Move.STEP) {
            board[m.x2][m.y2] = mColor;
            int pos = 0;
            for (int i = 0; i < 10; i++) {
                if (m.x1 == chips[mColor][i][X] && m.y1 == chips[mColor][i][Y]) {
                    pos = i;
                }
            }
            chips[mColor][pos][X] = m.x2;
            chips[mColor][pos][Y] = m.y2;            
        } else if (mColor == this.color) {
            myCount--; 
        } else {
            oppCount--;
        }        
    }
    
    private void makeMove(Move m, int mColor) {
        if (m.moveKind == Move.STEP) {
            int pos = 0;
            for (int i = 0; i < 10; i++) {
                if (m.x2 == chips[mColor][i][X] && m.y2 == chips[mColor][i][Y]) {
                    pos = i;
                }
            }
            chips[mColor][pos][X] = m.x1;
            chips[mColor][pos][Y] = m.y1;            
            board[m.x2][m.y2] = EMPTY;
        } else if (mColor == this.color) {
            chips[mColor][myCount][X] = m.x1;
            chips[mColor][myCount][Y] = m.y1;                     
            myCount++; 
        } else {
            chips[mColor][oppCount][X] = m.x1;
            chips[mColor][oppCount][Y] = m.y1;                     
            oppCount++;
        }
        board[m.x1][m.y1] = mColor;
    }
    private Move[] getMoves(int mColor) {
        int countToCompare = this.oppCount;
        if (this.color == mColor) {
            countToCompare = this.myCount;
        }
        if (countToCompare < 10) {
            return getAddMoves(mColor,0,0);
        } else {
            return getStepMoves(1,0,mColor,0);
        }
    }
    private Move[] getAddMoves(int mColor, int cCount, int mCount) {
        if (cCount == 48) {
            return new Move[mCount];
        } else {
            int x;
            int y;
            if (mColor == WHITE) {
                x = cCount % SIDE;
                y = cCount / SIDE + 1;
            } else {
                x = cCount % 6 + 1;
                y = cCount / 6;
            }
            Move currMove = new Move(x,y);
            if (isMoveValid(currMove, mColor)) {
                Move[] mArr = getAddMoves(mColor, cCount + 1, mCount + 1);
                mArr[mCount] = currMove;
                return mArr;
            } else {
                return getAddMoves(mColor, cCount + 1, mCount);
            }
        }
    }

    private Move[] getStepMoves(int currChip, int currCell, int mColor, int moveCount) {
        if (currChip == 10 && currCell == 48) {
            return new Move[moveCount];
        } else if (currCell == 48) {
            return getStepMoves(currChip + 1, 0, mColor, moveCount);
        } else {
            int x;
            int y;
            if (mColor == WHITE) {
                x = currCell % SIDE;
                y = currCell / SIDE + 1;
            } else {
                x = currCell % 6 + 1;
                y = currCell / 6;
            }
            Move currMove = new Move(x,y,chips[mColor][currChip - 1][X],chips[mColor][currChip -1][Y]);
            if (isMoveValid(currMove,mColor)) {
                Move[] mArr = getStepMoves(currChip,currCell + 1, mColor, moveCount + 1);
                mArr[moveCount] = currMove;
                return mArr;
            } else {
                return getStepMoves(currChip,currCell + 1,mColor,moveCount);
            }
        }
    }
    public void printChips() {
        System.out.print("Black: ");
        for (int i = 0; i < 10; i++) {
            System.out.print("[" + chips[BLACK][i][X] + "," + chips[BLACK][i][Y] + "] ");
        }
        System.out.println();
        System.out.print("White: ");
        for (int i = 0; i < 10; i++) {
            System.out.print("[" + chips[WHITE][i][X] + "," + chips[WHITE][i][Y] + "] ");
        }
        System.out.println();
    }
}
