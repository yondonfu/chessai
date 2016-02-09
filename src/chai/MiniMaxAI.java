package chai;

import java.util.Random;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MiniMaxAI implements ChessAI {
	public static final int MAXDEPTH = 4;
	
	private int playerNum;
	private int visitedStates;
	
	public short getMove(Position position) {
		playerNum = position.getToPlay();
		visitedStates = 0;
		
		return IDMiniMax(position, MAXDEPTH);
	}
	
	public short IDMiniMax(Position position, int maxDepth) {
		int maxDepthReached = 0;
		MoveWrapper bestMove = new MoveWrapper((short) 0, Integer.MIN_VALUE);
		for (int i = 0; i <= maxDepth; i++) {
			MoveWrapper currMove = depthLimitedMiniMax(position, i);
			if (currMove.utility > bestMove.utility) {
				maxDepthReached = i;
				bestMove = currMove;
			}
			if (bestMove.utility == Integer.MAX_VALUE) {
				break;
			}
		}
		
		System.out.println("Max depth reached: " + maxDepthReached);
		System.out.println("Best utility found: " + bestMove.utility);
		printStats();
		return bestMove.move;
	}
	
	public MoveWrapper depthLimitedMiniMax(Position position, int maxDepth) {
		return maxValue(position, 0, maxDepth);
	}
	
	public MoveWrapper maxValue(Position position, int depth, int maxDepth) {
		if (position.isTerminal() || depth == maxDepth) {
			return new MoveWrapper(position.getLastShortMove(), getUtility(position));
		}
		
		MoveWrapper bestMax = new MoveWrapper((short) 0, Integer.MIN_VALUE);
		short[] moves = position.getAllMoves();
		for (short move : moves) {
			try {
				position.doMove(move);
				
				visitedStates++;
				
				MoveWrapper minMove = minValue(position, depth + 1, maxDepth);
				
				// Maximize utility
				if (bestMax.utility < minMove.utility) {
					bestMax.utility = minMove.utility;
					bestMax.move = move;
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
		}
		
		MoveWrapper bestMin = new MoveWrapper((short) 0, Integer.MAX_VALUE);
		short[] moves = position.getAllMoves();
		for (short move : moves) {
			try {
				position.doMove(move);
				
				visitedStates++;
				
				MoveWrapper maxMove = maxValue(position, depth + 1, maxDepth);
				
				// Minimize utility
				if (bestMin.utility > maxMove.utility) {
					bestMin.utility = maxMove.utility;
					bestMin.move = move;
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
			// Cut off search early
//			Random r =  new Random();
//			return r.nextInt();
			return evaluate(position);
		}
		
	}
	
	public int evaluate(Position position) {
		if (position.getToPlay() == playerNum) {
			return position.getMaterial() + (int) position.getDomination();
		} else {
			return -1 * position.getMaterial() + (int) position.getDomination();
		}
		
	}
	
	public void printStats() {
		System.out.println("Visited states: " + visitedStates);
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
