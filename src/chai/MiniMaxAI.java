package chai;

import java.util.Random;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MiniMaxAI implements ChessAI {
	public static final int MAXDEPTH = 4;
	
	public short getMove(Position position) {
		return IDMiniMax(position, MAXDEPTH);
	}
	
	public short IDMiniMax(Position position, int maxDepth) {
		MoveWrapper bestMove = new MoveWrapper((short) -1, Integer.MIN_VALUE);
		for (int i = 0; i < maxDepth; i++) {
			MoveWrapper currMove = depthLimitedMiniMax(position, i);
			if (currMove.utility > bestMove.utility) {
				bestMove = currMove;
			}
		}
		
		return bestMove.move;
	}
	
	public MoveWrapper depthLimitedMiniMax(Position position, int maxDepth) {
		return maxValue(position, 0, maxDepth);
	}
	
	public MoveWrapper maxValue(Position position, int depth, int maxDepth) {
		if (position.isTerminal() || depth == maxDepth) {
			return new MoveWrapper(position.getLastShortMove(), getUtility(position));
//			return new MoveWrapper(position.getLastShortMove(), evaluate(position));
		}
		
		MoveWrapper bestMax = new MoveWrapper((short) -1, Integer.MIN_VALUE);
		short[] moves = position.getAllMoves();
		for (short move : moves) {
			try {
				position.doMove(move);
				
				if (position.isLegal()) {
					MoveWrapper minMove = minValue(position, depth + 1, maxDepth);
					
					// Maximize utility
					if (bestMax.utility < minMove.utility) {
						bestMax = minMove;
					}
				}
				
				// Undo move for next iteration
				position.undoMove();
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return bestMax;
	}
	
	public MoveWrapper minValue(Position position, int depth, int maxDepth) {
		if (position.isTerminal() || depth == maxDepth) {
			return new MoveWrapper(position.getLastShortMove(), getUtility(position));
//			return new MoveWrapper(position.getLastShortMove(), evaluate(position));
		}
		
		MoveWrapper bestMin = new MoveWrapper((short) -1, Integer.MAX_VALUE);
		short[] moves = position.getAllMoves();
		for (short move : moves) {
			try {
				position.doMove(move);
				
				if (position.isLegal()) {
					MoveWrapper maxMove = maxValue(position, depth + 1, maxDepth);
					
					// Minimize utility
					if (bestMin.utility > maxMove.utility) {
						bestMin = maxMove;
					}
				}
				
				// Undo move for next iteration
				position.undoMove();
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return bestMin;
	}
	
	public int getUtility(Position position) {
		if (position.isMate()) {
			if (position.getToPlay() == 1) {
				// You win
				return Integer.MAX_VALUE;
			} else {
				// Opponent wins
				return Integer.MIN_VALUE;
			}
		} else if (position.isStaleMate()) {
			return 0;
		} else {
			// Other state beside a draw or a checkmate
			Random r =  new Random();
			return r.nextInt();
		}
	}
	
	public int evaluate(Position position) {
		if (position.getToPlay() == 1) {
			return position.getMaterial();
		} else {
			return -1 * position.getMaterial();
		}
	}
	
	/*
	 * Wrapper class to bundle together moves and their utilities
	 */
	public class MoveWrapper {
		protected short move;
		protected int utility;
		
		public MoveWrapper(short m,  int u) {
			move = m;
			utility = u;
		}
	}
}
