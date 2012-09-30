/* InvalidMoveException.java */

package player;

/**
 *  Implements an Exception that signals an attempt to make an invalid move.
 */

public class InvalidMoveException extends Exception {
  protected InvalidMoveException() {
    super();
  }

  protected InvalidMoveException(String s) {
    super(s);
  }
}
