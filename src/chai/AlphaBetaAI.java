package chai;

import java.util.HashMap;
import java.util.Random;

import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;

public class AlphaBetaAI implements ChessAI {
	public static final int MAXDEPTH = 5;
	
	private int playerNum;
	private int visitedStates = 0;
	private HashMap<Integer, TableNode> transTable;
	private enum ScoreType {
		EXACT, UPPER, LOWER
	};
	
	private OpeningBook openingBook;
	private boolean isOpeningMove;
	
	public AlphaBetaAI(boolean fullGame) {
		transTable = new HashMap<Integer, TableNode>();
		openingBook = new OpeningBook("book.pgn");
		isOpeningMove = fullGame;
	}
	
	@Override
	public short getMove(Position position) {
		playerNum = position.getToPlay();
		visitedStates = 0;
		
		if (isOpeningMove) {
			isOpeningMove = false;
			System.out.println("Player " + playerNum + " using opening book move");
			return openingBook.getOpeningMove(playerNum);
		} else {
			return IDAB(position, MAXDEPTH);
		}
	}
	
	public short IDAB(Position position, int maxDepth) {
		int maxDepthReached = 0;
		MoveWrapper bestMove = new MoveWrapper((short) 0, Integer.MIN_VALUE);
		for (int i = 1; i <= maxDepth; i++) {
			MoveWrapper currMove = depthLimitedABSearch(position, i);
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
	
	public MoveWrapper depthLimitedABSearch(Position position, int maxDepth) {
		return maxValue(position, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, maxDepth);
	}
	
	public MoveWrapper maxValue(Position position, int alpha, int beta, int depth, int maxDepth) {
		if (position.isTerminal() || depth == maxDepth) {
			return new MoveWrapper(position.getLastShortMove(), getUtility(position, depth, alpha, beta));
		}
		
		MoveWrapper bestMax = new MoveWrapper((short) 0, Integer.MIN_VALUE);
		short[] moves = position.getAllMoves();
		for (short move : moves) {
			try {
				position.doMove(move);
				
				MoveWrapper minMove;
				if (transTable.containsKey(position.hashCode()) && transTable.get(position.hashCode()).depth > maxDepth - depth) {
					int prevScore = transTable.get(position.hashCode()).utility;
					ScoreType prevType = transTable.get(position.hashCode()).scoreType;
					if (prevType == ScoreType.EXACT || (prevType == ScoreType.LOWER && prevScore >= beta) ||
							(prevType == ScoreType.UPPER && prevScore <= alpha) || beta <= alpha) {
						minMove = new MoveWrapper(move, prevScore);
					} else {
						visitedStates++;
						minMove = minValue(position, alpha, beta, depth + 1, maxDepth);
						
						if (minMove.utility <= alpha) {
							transTable.put(position.hashCode(), new TableNode(minMove.utility, maxDepth - depth, ScoreType.UPPER));
						} else if (minMove.utility >= beta) {
							transTable.put(position.hashCode(), new TableNode(minMove.utility, maxDepth - depth, ScoreType.LOWER));
						} else {
							transTable.put(position.hashCode(), new TableNode(minMove.utility, maxDepth - depth, ScoreType.EXACT));
						}
					}
				} else {
					visitedStates++;
					minMove = minValue(position, alpha, beta, depth + 1, maxDepth);
					
					if (minMove.utility <= alpha) {
						transTable.put(position.hashCode(), new TableNode(minMove.utility, depth, ScoreType.UPPER));
					} else if (minMove.utility >= beta) {
						transTable.put(position.hashCode(), new TableNode(minMove.utility, depth, ScoreType.LOWER));
					} else {
						transTable.put(position.hashCode(), new TableNode(minMove.utility, depth, ScoreType.EXACT));
					}
				}
				
				// Maximize utility
				if (bestMax.utility < minMove.utility) {
					bestMax.utility = minMove.utility;
					bestMax.move = move;
				}
				
				// Undo move for next iteration
				position.undoMove();
				
				if (bestMax.utility >= beta) {
					return bestMax;
				}
				
				// Set alpha to move with highest utility thus far
				alpha = Math.max(alpha, bestMax.utility);
				
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return bestMax;
	}
	
	public MoveWrapper minValue(Position position, int alpha, int beta, int depth, int maxDepth) {
		if (position.isTerminal() || depth == maxDepth) {
			return new MoveWrapper(position.getLastShortMove(), getUtility(position, depth, alpha, beta));
		}
		
		MoveWrapper bestMin = new MoveWrapper((short) 0, Integer.MAX_VALUE);
		short[] moves = position.getAllMoves();
		for (short move : moves) {
			try {
				position.doMove(move);
				
				MoveWrapper maxMove;
				if (transTable.containsKey(position) && transTable.get(position).depth > maxDepth - depth) {
					int prevScore = transTable.get(position.hashCode()).utility;
					ScoreType prevType = transTable.get(position.hashCode()).scoreType;
					if (prevType == ScoreType.EXACT || (prevType == ScoreType.LOWER && prevScore >= beta) ||
							(prevType == ScoreType.UPPER && prevScore <= alpha) || beta <= alpha) {
						maxMove = new MoveWrapper(move, prevScore);
					} else {
						visitedStates++;
						maxMove = maxValue(position, alpha, beta, depth + 1, maxDepth);
						
						if (maxMove.utility <= alpha) {
							transTable.put(position.hashCode(), new TableNode(maxMove.utility, maxDepth - depth, ScoreType.UPPER));
						} else if (maxMove.utility >= beta) {
							transTable.put(position.hashCode(), new TableNode(maxMove.utility, maxDepth - depth, ScoreType.LOWER));
						} else {
							transTable.put(position.hashCode(), new TableNode(maxMove.utility, maxDepth - depth, ScoreType.EXACT));
						}
					}
				} else {
					visitedStates++;
					maxMove = maxValue(position, alpha, beta, depth + 1, maxDepth);
					
					if (maxMove.utility <= alpha) {
						transTable.put(position.hashCode(), new TableNode(maxMove.utility, depth, ScoreType.UPPER));
					} else if (maxMove.utility >= beta) {
						transTable.put(position.hashCode(), new TableNode(maxMove.utility, depth, ScoreType.LOWER));
					} else {
						transTable.put(position.hashCode(), new TableNode(maxMove.utility, depth, ScoreType.EXACT));
					}
				}
				
				// Minimize utility
				if (bestMin.utility > maxMove.utility) {
					bestMin.utility = maxMove.utility;
					bestMin.move = move;
				}
				
				// Undo move for next iteration
				position.undoMove();
				
				if (bestMin.utility <= alpha) {
					return bestMin;
				}
				
				// Set beta to move with lowest utility thus far
				beta = Math.min(beta, bestMin.utility);
		
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return bestMin;
	}
	
	public int getUtility(Position position, int depth, int alpha, int beta) {
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
			return evaluate(position, depth, alpha, beta);
		}
		
	}
	
	public int evaluate(Position position, int depth, int alpha, int beta) {
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
	
	public class TableNode {
		protected int utility;
		protected int depth;
		protected ScoreType scoreType;
		
		public TableNode(int u, int d, ScoreType type) {
			utility = u;
			depth = d;
			scoreType = type;
		}
	}
}
