/* MachinePlayer.java */

package player;
import java.util.Random;
import graph.*;
import list.InvalidNodeException;
import list.DList;
import list.DListNode;

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
    public static final boolean COMPUTER = true;
    public static final boolean HUMAN = false;
    public static final double REDUCTION = 0.99;
    public static final double BESTSCORE = 45.0;
    private int color;
    private int oppColor;
    private int myCount;
    private int oppCount;
    private int[][] board;
    private int[][][] chips;
    private Random generator;
    private Graph myGraph;
    private Graph oppGraph;
    private int maxDepth;
    

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    board = new int[SIDE][SIDE];
    chips = new int[2][10][2];    
    myCount = 0;
    oppCount = 0;
    for (int i = 0; i < SIDE; i++) {
        for (int j = 0; j < SIDE; j++) {      
            board[i][j] = EMPTY;
        }
    }
    this.color = color;
    this.oppColor = BLACK;
    if (this.color == BLACK) {
        this.oppColor = WHITE;
    }
    generator = new Random();
    myGraph = new Graph();
    oppGraph = new Graph();    
    maxDepth = 3;
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    Move m;
    // m = chooseRandomMove();
    // m = chooseDepthOneMove();
    m = chooseABmove();
    
    makeMove(m,this.color);
        
    // generateGraph(this.color);
    // graph.debugPrint();
    
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
        System.out.println("My count: " + myCount + " Opp count: " + oppCount);
    }
    private Graph generateGraph(int mColor, int mCount) {
        Graph graph = new Graph();
        for (int i = 0; i < mCount; i++) {
            graph.insertVertex(new Vertex(
                chips[mColor][i][X],
                chips[mColor][i][Y]));
        }
        int x;
        int y;
        int [] destination;
        for (int i = 0; i < mCount; i++) {
            x = chips[mColor][i][X];
            y = chips[mColor][i][Y];
            destination = try0(x,y,mColor);
            if (destination[X] != 0 || destination[Y] != 0) {
                graph.insertEdge(new Edge(
                    0,
                    graph.getVertex(x,y),
                    graph.getVertex(destination[X],destination[Y])));
            }
            destination = try1(x,y,mColor);            
            if (destination[X] != 0 || destination[Y] != 0) {
                graph.insertEdge(new Edge(
                    1,
                    graph.getVertex(x,y),
                    graph.getVertex(destination[X],destination[Y])));
            }
            destination = try3(x,y,mColor);
            if (destination[X] != 0 || destination[Y] != 0) {
                graph.insertEdge(new Edge(
                    3,
                    graph.getVertex(x,y),
                    graph.getVertex(destination[X],destination[Y])));
            }
            destination = try4(x,y,mColor);
            if (destination[X] != 0 || destination[Y] != 0) {
                graph.insertEdge(new Edge(
                    4,
                    graph.getVertex(x,y),
                    graph.getVertex(destination[X],destination[Y])));
            }
        }
        return graph;
    }
    private int[] try0(int x, int y, int mColor) {
        int[] destination = {0, 0};
        int topBorder = 1;
        if (mColor == BLACK) {
            topBorder = 0;
        }
        for (int i = y-1; i >= topBorder; i--) {
            if (board[x][i] == mColor) {
                destination[X] = x;
                destination[Y] = i;
                return destination;
            } else if (board[x][i] != EMPTY) {
                return destination;
            }
        }
        return destination;
     }
    private int[] try1(int x, int y, int mColor) {
        int[] destination = {0, 0};
        int rightBorder = 7;
        int topBorder = 1;
        if (mColor == BLACK) {
            rightBorder = 6;
            topBorder = 0;            
        }
        int i = x + 1;
        int j = y - 1;
        while (i <= rightBorder && j >= topBorder) {
            if (board[i][j] == mColor) {
                destination[X] = i;
                destination[Y] = j;
                return destination;
            } else if (board[i][j] != EMPTY) {
                return destination;
            }
            i++;
            j--;
        }
        return destination;
     }
    private int[] try3(int x, int y, int mColor) {
        int[] destination = {0, 0};
        int rightBorder = 7;
        if (mColor == BLACK) {
            rightBorder = 6;
        }
        for (int i = x+1; i <= rightBorder; i++) {
            if (board[i][y] == mColor) {
                destination[X] = i;
                destination[Y] = y;
                return destination;
            } else if (board[i][y] != EMPTY) {
                return destination;
            }
        }
        return destination;
     }
    private int[] try4(int x, int y, int mColor) {
        int[] destination = {0, 0};
        int rightBorder = 7;
        int bottomBorder = 6;
        if (mColor == BLACK) {
            rightBorder = 6;
            bottomBorder = 7;
        }
        int i = x + 1;
        int j = y + 1;
        while (i <= rightBorder && j <= bottomBorder) {
            if (board[i][j] == mColor) {
                destination[X] = i;
                destination[Y] = j;
                return destination;
            } else if (board[i][j] != EMPTY) {
                return destination;
            }
            i++;
            j++;
        }
        return destination;
     }
    private boolean visitVertex(Vertex v, int depth, int direction, int mColor) {
        if (v.visited()) return false;
        if ((mColor == BLACK && v.y() == 7) || (mColor == WHITE && v.x() == 7)) {
            if (depth >= 5) {
                return true;
            } else {
                return false;
            }
        } else if ((depth > 0) && ((mColor == BLACK && v.y() == 0) || (mColor == WHITE && v.x() == 0))) {
            return false;
        } else {
            v.visit();
            boolean result = tryEdges(v.incidentEdges(),depth,direction,mColor,v);
            v.reset();
            return result;
        }
    }
    private boolean tryEdges(DList edges, int depth, int direction, int mColor, Vertex from) {
        try {
            if (edges.length() == 0) return false;
            DListNode currEdgeNode = (DListNode) edges.front();
            Edge currEdge = (Edge) currEdgeNode.item();
            if (currEdge.direction() == direction) {
                currEdgeNode.remove();
                return tryEdges(edges, depth, direction, mColor, from);
            }
            if (visitVertex(currEdge.opposite(from), depth + 1, currEdge.direction(), mColor)) {
                return true;
            }
            currEdgeNode.remove();
            return tryEdges(edges, depth, direction, mColor, from);
        } catch (InvalidNodeException e) {
            System.out.println(e);
            return false;
        }
    }
    private boolean isWinningGrid(int mColor, Graph graph) {
        int foundStart = 0;
        int foundFinish = 0;
        int[][] startChips = new int[6][2];
        int currCount = oppCount;
        if (mColor == color) {
            currCount = myCount;
        }
        if (currCount < 6) return false;
        for (int i = 0; i < currCount; i++) {
            if (mColor == BLACK) {
                if (chips[mColor][i][Y] == 0) {
                    startChips[foundStart][X] = chips[mColor][i][X];
                    startChips[foundStart][Y] = chips[mColor][i][Y];
                    foundStart++;
                }
                if (chips[mColor][i][Y] == 7) {
                    foundFinish++;
                }
            } else {
                if (chips[mColor][i][X] == 0) {
                    startChips[foundStart][X] = chips[mColor][i][X];
                    startChips[foundStart][Y] = chips[mColor][i][Y];
                    foundStart++;
                }
                if (chips[mColor][i][X] == 7) {
                    foundFinish++;
                }
            }
        }
        if (foundStart == 0 || foundFinish == 0) return false;
        if (currCount - foundStart - foundFinish < 4) return false;
        return checkChips(startChips,0,foundStart,mColor,graph);
    }
    private boolean checkChips(int[][] startChips, int fromChip, int toChip, int mColor, Graph graph) {
        if (fromChip >= toChip) return false;
        graph.reset();
        Vertex v = graph.getVertex(startChips[fromChip][X],startChips[fromChip][Y]);
        if (visitVertex(v,0,-1,mColor)) return true;
        return checkChips(startChips,fromChip + 1,toChip,mColor, graph);
    }
    private Move chooseDepthOneMove(){
        if (myCount == 0) {
            Move m;
            if(color == BLACK) {
                m = new Move(3,0);
            } else {
                m = new Move(0,3);
            }
            return m;
        } else if (myCount == 1) {
            Move m;
            if(color == BLACK) {
                m = new Move(4,7);
            } else {
                m = new Move(7,4);
            }
            return m;
        }
        Move[] movesArray = getMoves(color);
        double bestScore = -2;
        double score;
        Move bestMove = new Move();
        for(Move m: movesArray) {
            makeMove(m,color);
            myGraph = generateGraph(color, myCount);
            oppGraph = generateGraph(oppColor, oppCount);            
            if (isWinningGrid(color,myGraph)) {
                undoMove(m,color);
                System.out.println("Winning move detected!");
                return m;
            }
            score = evaluateBoard(myGraph,oppGraph);
            if (score > bestScore) {
                bestScore = score;
                bestMove = m;
            }
            undoMove(m,color);
        }
        return bestMove;
    }
    private double evaluateBoard(Graph graph, Graph other) {
        double myEdges = graph.getEdgeCount();
        double oppEdges = other.getEdgeCount();
        // return (myEdges - oppEdges) /45.0;
        return (myEdges - oppEdges) * 1.0;
    }
    private Best chooseAB(boolean side, double alpha, double beta, int depth) {
        Best myBest = new Best();
        Best reply;
        Move[] moves;
        int mColor = oppColor;
        if (side == COMPUTER) {
            mColor = color;
        }
        myGraph = generateGraph(color,myCount);
        oppGraph = generateGraph(oppColor,oppCount);

        if (isWinningGrid(oppColor,oppGraph)) {
            myBest.score = -BESTSCORE * Math.pow(REDUCTION,depth - 1);
            return myBest;
        }
        if (isWinningGrid(color,myGraph)) {
            myBest.score = BESTSCORE * Math.pow(REDUCTION,depth - 1);
            return myBest;
        }
        if (depth >= this.maxDepth) {
            myBest.score = evaluateBoard(myGraph,oppGraph) * Math.pow(REDUCTION,depth - 1);
            return myBest;
        }
        if (side == COMPUTER) {
            myBest.score = alpha;
        } else {
            myBest.score = beta;
        }
        moves = getMoves(mColor);
        for (Move m: moves) {
            makeMove(m,mColor);
            reply = chooseAB(!side,alpha,beta,depth + 1);
            undoMove(m,mColor);
            if ((side == COMPUTER) && (reply.score > myBest.score)) {
                myBest.move = m;
                myBest.score = reply.score;
                alpha = reply.score;
            } else if ((side == HUMAN) && (reply.score < myBest.score)) {
                myBest.move = m;
                myBest.score = reply.score;
                beta = reply.score;
            }
            if (alpha >= beta) {
                return myBest;
            }
        }
        return myBest;
    }
    private Move chooseABmove() {
        /*if (myCount == 0) {
            Move m;
            if(color == BLACK) {
                m = new Move(4,7);
            } else {
                m = new Move(7,4);
            }
            return m;
        
        }*//* else if (myCount == 1) {
            Move m;
            if(color == BLACK) {
                m = new Move(0,3);
            } else {
                m = new Move(3,0);
            }
            return m;
        }*/
        Best b = chooseAB(COMPUTER,-BESTSCORE,BESTSCORE,0);
        return b.move;
    }
                        
                
                
            
        
        
 
 
        
        
 
 
 
 
 
 
 
            
}
