import java.util.Scanner;

// Represents a position on the chessboard
class Position {
    int row, col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

// Abstract base class for chess pieces
abstract class Piece {
    boolean isWhite;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public abstract boolean isValidMove(Position start, Position end, Board board);
}

// Pawn class
class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        int direction = isWhite ? -1 : 1;
        Piece destinationPiece = board.getPiece(end);

        if (start.col == end.col && destinationPiece == null) {
            // Single move forward
            if (end.row == start.row + direction) return true;
            // Double move forward from starting position
            if (end.row == start.row + 2 * direction && 
                (isWhite ? start.row == 6 : start.row == 1) &&
                board.getPiece(new Position(start.row + direction, start.col)) == null) {
                return true;
            }
        }
        // Diagonal capture
        if (Math.abs(end.col - start.col) == 1 && end.row == start.row + direction && destinationPiece != null &&
            destinationPiece.isWhite != this.isWhite) {
            return true;
        }
        return false;
    }
}

// Rook class
class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        return (start.row == end.row || start.col == end.col) && board.isPathClear(start, end);
    }
}

// Knight class
class Knight extends Piece {
    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        int rowDiff = Math.abs(end.row - start.row);
        int colDiff = Math.abs(end.col - start.col);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
}

// Bishop class
class Bishop extends Piece {
    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        return Math.abs(end.row - start.row) == Math.abs(end.col - start.col) && board.isPathClear(start, end);
    }
}

// Queen class
class Queen extends Piece {
    public Queen(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        return (start.row == end.row || start.col == end.col || 
                Math.abs(end.row - start.row) == Math.abs(end.col - start.col)) &&
               board.isPathClear(start, end);
    }
}

// King class
class King extends Piece {
    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Position start, Position end, Board board) {
        int rowDiff = Math.abs(end.row - start.row);
        int colDiff = Math.abs(end.col - start.col);
        return rowDiff <= 1 && colDiff <= 1;
    }
}

// Board class
class Board {
    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initialize();
    }

    private void initialize() {
        // Initialize board with standard chess setup
        board[0] = new Piece[]{new Rook(false), new Knight(false), new Bishop(false), new Queen(false),
                               new King(false), new Bishop(false), new Knight(false), new Rook(false)};
        board[1] = new Piece[]{new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false),
                               new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false)};
        board[6] = new Piece[]{new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true),
                               new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true)};
        board[7] = new Piece[]{new Rook(true), new Knight(true), new Bishop(true), new Queen(true),
                               new King(true), new Bishop(true), new Knight(true), new Rook(true)};
    }

    public Piece getPiece(Position pos) {
        return board[pos.row][pos.col];
    }

    public void setPiece(Position pos, Piece piece) {
        board[pos.row][pos.col] = piece;
    }

    public boolean isPathClear(Position start, Position end) {
        int rowStep = Integer.compare(end.row, start.row);
        int colStep = Integer.compare(end.col, start.col);

        int row = start.row + rowStep;
        int col = start.col + colStep;

        while (row != end.row || col != end.col) {
            if (board[row][col] != null) return false;
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    public void display() {
        System.out.println("    a   b   c   d   e   f   g   h");
        System.out.println("  +---+---+---+---+---+---+---+---+");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " |"); // Row number on the left
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                char symbol = (piece == null) ? ' ' : getPieceSymbol(piece);
                System.out.print(" " + symbol + " |");
            }
            System.out.println(" " + (8 - row)); // Row number on the right
            System.out.println("  +---+---+---+---+---+---+---+---+");
        }
        System.out.println("    a   b   c   d   e   f   g   h");
    }

    private char getPieceSymbol(Piece piece) {
        // Uppercase for white, lowercase for black
        if (piece instanceof Pawn) return piece.isWhite ? 'P' : 'p';
        if (piece instanceof Rook) return piece.isWhite ? 'R' : 'r';
        if (piece instanceof Knight) return piece.isWhite ? 'N' : 'n';
        if (piece instanceof Bishop) return piece.isWhite ? 'B' : 'b';
        if (piece instanceof Queen) return piece.isWhite ? 'Q' : 'q';
        if (piece instanceof King) return piece.isWhite ? 'K' : 'k';
        return '?'; // Fallback for unknown piece
    }
}

// ChessGame class
public class ChessGame {
    private Board board;
    private boolean isWhiteTurn;

    public ChessGame() {
        board = new Board();
        isWhiteTurn = true;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            board.display();
            System.out.println((isWhiteTurn ? "White" : "Black") + "'s turn.");
            System.out.print("Enter move (e.g., e2 e4) or 'restart': ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("restart")) {
                board = new Board();
                isWhiteTurn = true;
                System.out.println("Game restarted.");
                continue;
            }

            if (input.length() != 5 || input.charAt(2) != ' ') {
                System.out.println("Invalid input format. Try again.");
                continue;
            }

            Position start = parsePosition(input.substring(0, 2));
            Position end = parsePosition(input.substring(3, 5));

            if (start == null || end == null) {
                System.out.println("Invalid input format. Try again.");
                continue;
            }

            Piece piece = board.getPiece(start);
            Piece destinationPiece = board.getPiece(end);

            if (piece == null || piece.isWhite != isWhiteTurn) {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            if (piece.isValidMove(start, end, board)) {
                if (destinationPiece instanceof King) {
                    System.out.println((isWhiteTurn ? "White" : "Black") + " wins! Game over.");
                    System.out.print("Do you want to restart? (yes/no): ");
                    String response = scanner.nextLine();
                    if (response.equalsIgnoreCase("yes")) {
                        board = new Board();
                        isWhiteTurn = true;
                        System.out.println("Game restarted.");
                        continue;
                    } else {
                        System.out.println("Goodbye!");
                        break;
                    }
                }
                board.setPiece(end, piece);
                board.setPiece(start, null);
                isWhiteTurn = !isWhiteTurn;
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
        scanner.close();
    }

    private Position parsePosition(String pos) {
        if (pos.length() != 2) return null;
        int row = 8 - (pos.charAt(1) - '0');
        int col = pos.charAt(0) - 'a';
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return null;
        return new Position(row, col);
    }

    public static void main(String[] args) {
        new ChessGame().start();
    }
}

