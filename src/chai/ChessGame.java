package chai;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;

public class ChessGame {

	public Position position;

	public int rows = 8;
	public int columns = 8;

	public ChessGame() {
		// Original full game
//		position = new Position(
//				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		// Black wins in 3
//		position = new Position(
//				"r5k1/p3Qpbp/2p3p1/1p6/q3bN2/6PP/PP3P2/K2RR3 b - - 0 1");
		// Black wins in 3
//		position = new Position(
//				"6k1/Q4pp1/2pq2rp/3p2n1/4r3/2PN4/P4PPP/2R2R1K b - - 0 1");
		// Black wins in 3
		position = new Position(
				"8/4B2p/7n/4pqpk/P2n1b2/R3P2P/2r3P1/1Q3R1K b - - 0 1");
		// Black wins in 3
//		position = new Position(
//				"2kr3r/1bbp1p2/p3pp2/1p4q1/4P3/PNN1PBP1/1PP3KP/1R1Q1R2 b - - 0 1");
		// Black wins in 3
//		position = new Position(
//				"5rk1/p4ppp/2b2q1P/7B/4p1Q1/5PR1/PPPb2R1/1K6 b - - 0 1");
		// Black wins
//		position = new Position(
//				"7K/8/k1R5/7R/8/8/8/8 b - - 0 1");
		

	}

	public int getStone(int col, int row) {
		return position.getStone(Chess.coorToSqi(col, row));
	}
	
	public boolean squareOccupied(int sqi) {
		return position.getStone(sqi) != 0;
		
	}

	public boolean legalMove(short move) {
		
		for(short m: position.getAllMoves()) {
			if(m == move) return true;
		}
		System.out.println(java.util.Arrays.toString(position.getAllMoves()));
		System.out.println(move);
		return false;
	
	}

	// find a move from the list of legal moves from fromSqi to toSqi
	// return 0 if none available
	public short findMove(int fromSqi, int toSqi) {
		
		for(short move: position.getAllMoves()) {
			if(Move.getFromSqi(move) == fromSqi && 
					Move.getToSqi(move) == toSqi) return move;
		}
		return 0;
	}
	
	public void doMove(short move) {
		try {

			System.out.println("making move " + move);

			position.doMove(move);
			System.out.println(position);
		} catch (IllegalMoveException e) {
			System.out.println("illegal move!");
		}
	}

	public static void main(String[] args) {
		System.out.println();

		// Create a starting position using "Forsyth–Edwards Notation". (See
		// Wikipedia.)
		Position position = new Position(
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

		System.out.println(position);

	}
	
	

}
