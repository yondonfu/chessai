package chai;

import java.util.Random;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class AlphaBetaAI implements ChessAI {
	public static final int MAXDEPTH = 4;
	
	private int playerNum;
	
	@Override
	public short getMove(Position position) {
		playerNum = position.getToPlay();
		
		return IDAB(position, MAXDEPTH);
	}
	
	public short IDAB(Position position, int maxDepth) {
		MoveWrapper bestMove = new MoveWrapper((short) -1, Integer.MIN_VALUE);
		for (int i = 0; i < maxDepth; i++) {
			MoveWrapper currMove = depthLimitedABSearch(position, i);
			if (currMove.utility > bestMove.utility) {
				bestMove = currMove;
			}
		}
		
		return bestMove.move;
	}
	
	public MoveWrapper depthLimitedABSearch(Position position, int maxDepth) {
		return maxValue(position, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, maxDepth);
	}
	
	public MoveWrapper maxValue(Position position, int alpha, int beta, int depth, int maxDepth) {
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
					MoveWrapper minMove = minValue(position, alpha, beta, depth + 1, maxDepth);
					
					// Maximize utility
					if (bestMax.utility < minMove.utility) {
						bestMax.utility = minMove.utility;
						bestMax.move = minMove.move;
					}
					
					// Undo move for next iteration
					position.undoMove();
					
					if (bestMax.utility >= beta) {
						return bestMax;
					}
					
					// Set alpha to move with highest utility thus far
					alpha = Math.max(alpha, bestMax.utility);
				} else {
					position.undoMove();
				}
				
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return bestMax;
	}
	
	public MoveWrapper minValue(Position position, int alpha, int beta, int depth, int maxDepth) {
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
					MoveWrapper maxMove = maxValue(position, alpha, beta, depth + 1, maxDepth);
					
					// Minimize utility
					if (bestMin.utility > maxMove.utility) {
						bestMin.utility = maxMove.utility;
						bestMin.move = maxMove.move;
					}
					
					// Undo move for next iteration
					position.undoMove();
					
					if (bestMin.utility <= alpha) {
						return bestMin;
					}
					
					// Set beta to move with lowest utility thus far
					beta = Math.min(beta, bestMin.utility);
				} else {
					position.undoMove();
				}
		
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return bestMin;
	}
	
	public int getUtility(Position position) {
		if (position.isTerminal()) {
			if (position.isMate()) {
				if (position.getToPlay() == playerNum) {
					// AI loses
					return Integer.MIN_VALUE;
				} else {
					// AI wins
					return Integer.MAX_VALUE;
				}
			} else if (position.isStaleMate()) {
				return 0;
			} else {
				// Other state beside a draw or a checkmate
//				Random r =  new Random();
//				return r.nextInt();
				return evaluate(position);
			}
		} else {
			// Cut off search early
			return evaluate(position);
		}
	}
	
	public int evaluate(Position position) {
		// getMaterial() and getDomination() will both return either positive or negative values
		// based on which piece has to play
		return position.getMaterial() + ((int) position.getDomination());
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
