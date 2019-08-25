import java.util.*;

//The following part should be completed by students.

//Students can modify anything except the class name and exisiting functions and varibles.

public class StudentAI extends AI {

	private int row;
	private int col;
	private int g;
	private int k;
	private int moveColumn = -1;
	private int moveRow = -1;

	Board b;

	public StudentAI(int col, int row, int k, int g) {
		super(col, row, k, g);
		this.row = row;
		this.col = col;
		this.g = g;
		this.k = k;
		b = new Board(col, row, k, g);
	}

	public Move GetMove(Move move) {
		try {
			if (!(move.row == -1 || move.col == -1))
				b = b.MakeMove(new Move(move.col, move.row), 2);
		} 
		catch (InvalidMoveError e) {
			e.printStackTrace();
		}

		Move result = new Move();
		if (this.g == 1) {
			getAIKeys();
			try {
				int moveRow = getRowNumber(moveColumn);
				if (moveRow != -1)
					result = new Move(moveColumn, moveRow);
				b = b.MakeMove(result, 1);
			}
			catch (InvalidMoveError e) {
				e.printStackTrace();
			}
		} 
		else {
			
			try {
				//int moveRow = getRowNumber(moveColumn);
				//if (moveRow != -1)
				result = getAiKeysGravOff();
				b = b.MakeMove(result, 1);
			}
			catch (InvalidMoveError e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	public int getAIKeys() {
		moveColumn = -1;
		minimax(0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return moveColumn;
	}

	public boolean isLegalMove(int row, int column) {
		return b.board.get(row).get(column) == 0;
	}

	public boolean isLegalMove(int column) {
		return b.board.get(0).get(column) == 0;
	}

	public int getRowNumber(int column) {
		for (int i = row - 1; i >= 0; --i) {
			if (b.board.get(i).get(column) == 0) {
				return i;
			}
		}
		return -1;
	}

	public boolean placePiece(int row, int column, int player) {
		if (!isLegalMove(row, column)) {
			System.out.println("Illegal move!");
			return false;
		}
		try {
			b = b.MakeMove(new Move(column, row), player);
		}
		catch (InvalidMoveError e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean placeKey(int column, int player) {
		if (!isLegalMove(column)) {
			System.out.println("Illegal move!");
			return false;
		}

		for (int i = row - 1; i >= 0; --i) {
			if (b.board.get(i).get(column) == 0) {
				try {
					b = b.MakeMove(new Move(column, i), player);
				}
				catch (InvalidMoveError e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		return false;
	}

	public void undoPlacedPiece(int row, int column) {
		b.board.get(row).set(column, 0);
	}

	public void undoMove(int column) {
		for (int i = 0; i <= row - 1; ++i) {
			if (b.board.get(i).get(column) != 0) {
				b.board.get(i).set(column, 0);
				break;
			}
		}
	}

	int heuristicScore(int aiScore, int moreMoves) {
		int moveScore = k - moreMoves;
		if (aiScore == 0)
			return 0;
		else if (aiScore >= 1 && aiScore <= k - 1) 
			return (int) Math.pow(10, aiScore - 1) * (moveScore);
		else
			return (int) Math.pow(10, k - 1);
	}


	int minimaxwoGrav(int depth, int playerTurn, int alpha, int beta) {
		if (beta <= alpha) {
			if (playerTurn == 1)
				return Integer.MAX_VALUE;
			else
				return Integer.MIN_VALUE;
		}

		int gameResult = getWinner(b);

		if (gameResult == 1)
			return Integer.MAX_VALUE / 2;
		else if (gameResult == 2)
			return Integer.MIN_VALUE / 2;
		else if (gameResult == 0)
			return 0;

		if (depth == 5)
			return getBoardScoreGarvOff(b);

		int maxScore = Integer.MIN_VALUE, minScore = Integer.MAX_VALUE;
		for (int i = 0; i <= row - 1; ++i) {
			boolean isMaxScore = false;
			for (int j = 0; j <= col - 1; ++j) {
				int currentScore = 0;
				if (!isLegalMove(i, j))
					continue;
				if (playerTurn == 1) {
					placePiece(i, j, 1);
					currentScore = minimaxwoGrav(depth + 1, 2, alpha, beta);
					if (depth == 0) {
						if (currentScore > maxScore) {
							moveColumn = j;
							moveRow = i;
						}
						if (currentScore == Integer.MAX_VALUE / 2) {
							undoPlacedPiece(i, j);
							break;
						}
					}
					maxScore = Math.max(currentScore, maxScore);
					alpha = Math.max(currentScore, alpha);
				}

				else if (playerTurn == 2) {
					placePiece(i, j, 2);
					currentScore = minimaxwoGrav(depth + 1, 1, alpha, beta);
					minScore = Math.min(currentScore, minScore);
					beta = Math.min(currentScore, beta);
				}

				undoPlacedPiece(i, j);

				if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) {
					isMaxScore = true;
					break;
				}

			}
			if (isMaxScore)
				break;
		}
		if (playerTurn == 1)
			return maxScore;
		else
			return minScore;
	}

	Move getAiKeysGravOff() {
		moveColumn = -1;
		moveRow = -1;
		minimaxwoGrav(0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return new Move(moveColumn, moveRow);
	}

	public int getBoardScore(Board b) {
		int myAiScore = 1;
		int heuristicScore = 0;
		int blanks = 0;
		int k = 0, moreMoves = 0;

		for (int i = row - 1; i >= 0; --i) {
			for (int j = 0; j <= col - 1; ++j) {
				if (b.board.get(i).get(j) == 0 || b.board.get(i).get(j) == 2)
					continue;
				if (j <= (col - this.k)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i).get(j + k) == 1)
							myAiScore++;
						else if (b.board.get(i).get(j + k) == 2) {
							myAiScore = 0;
							blanks = 0;
							break;
						}
						else 
							blanks++;
					}

					moreMoves = 0;
					if (myAiScore > 0)
						for (int c = 1; c < this.k; ++c) {
							int column = j + c;
							for (int m = i; m <= row - 1; m++) {
								if (b.board.get(m).get(column) == 0)
									moreMoves++;
								else
									break;
							}
						}

					if (moreMoves != 0)
						heuristicScore += heuristicScore(myAiScore, moreMoves);
					myAiScore = 1;
					blanks = 0;
				}

				if (i >= (this.k - 1)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i - k).get(j) == 1)
							myAiScore++;
						else if (b.board.get(i - k).get(j) == 2) {
							myAiScore = 0;
							break;
						}
					}

					moreMoves = 0;
					if (myAiScore > 0) {
						int column = j;
						for (int m = i - k + 1; m <= i - 1; m++) {
							if (b.board.get(m).get(column) == 0)
								moreMoves++;
							else
								break;
						}
					}

					if (moreMoves != 0)
						heuristicScore += heuristicScore(myAiScore, moreMoves);

					myAiScore = 1;
					blanks = 0;
				}

				if (j >= this.k - 1) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i).get(j - k) == 1)
							myAiScore++;
						else if (b.board.get(i).get(j - k) == 2) {
							myAiScore = 0;
							blanks = 0;
							break;
						}
						else
							blanks++;
					}

					moreMoves = 0;
					if (blanks > 0)
						for (int c = 1; c < this.k; ++c) {
							int column = j - c;
							for (int m = i; m <= row - 1; m++) {
								if (b.board.get(m).get(column) == 0)
									moreMoves++;
								else
									break;
							}
						}

					if (moreMoves != 0)
						heuristicScore += heuristicScore(myAiScore, moreMoves);
					myAiScore = 1;
					blanks = 0;
				}

				if (j <= (col - this.k) && i >= (this.k - 1)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i - k).get(j + k) == 1)
							myAiScore++;
						else if (b.board.get(i - k).get(j + k) == 2) {
							myAiScore = 0;
							blanks = 0;
							break;
						}
						else
							blanks++;
					}

					moreMoves = 0;
					if (blanks > 0) {
						for (int c = 1; c < this.k; ++c) {
							int column = j + c, row = i - c;
							for (int m = row; m <= row - 1; ++m) {
								if (b.board.get(m).get(column) == 0)
									moreMoves++;
								else if (b.board.get(m).get(column) == 1)
									continue;
								else
									break;
							}
						}

						if (moreMoves != 0)
							heuristicScore += heuristicScore(myAiScore, moreMoves);
						myAiScore = 1;
						blanks = 0;
					}
				}

				if (i >= (this.k - 1) && j >= (this.k - 1)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i - k).get(j - k) == 1)
							myAiScore++;
						else if (b.board.get(i - k).get(j - k) == 2) {
							myAiScore = 0;
							blanks = 0;
							break;
						}
						else
							blanks++;
					}

					moreMoves = 0;
					if (blanks > 0) {
						for (int c = 1; c < this.k; ++c) {
							int column = j - c, row = i - c;
							for (int m = row; m <= row - 1; ++m) {
								if (b.board.get(m).get(column) == 0)
									moreMoves++;
								else if (b.board.get(m).get(column) == 1)
									continue;
								else
									break;
							}
						}

						if (moreMoves != 0)
							heuristicScore += heuristicScore(myAiScore, moreMoves);
						myAiScore = 1;
						blanks = 0;
					}
				}
			}
		}

		return heuristicScore;
	}

	public int getBoardScoreGarvOff(Board b) {
		int myAiScore = 1;
		int heuristicScore = 0;
		int k = 0, moreMoves = 0;

		for (int i = row - 1; i >= 0; --i) {
			for (int j = 0; j <= col - 1; ++j) {
				if (b.board.get(i).get(j) == 0 || b.board.get(i).get(j) == 2)
					continue;
				//Downside checking
				if (j <= (col - this.k)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i).get(j + k) == 1)
							myAiScore++;
						else if (b.board.get(i).get(j + k) == 2) {
							myAiScore = 0;
							break;
						}
					}
					moreMoves = 0;
					if (myAiScore > 0)
						for (int c = j + 1; c <= j + k - 1; ++c) {
							if (b.board.get(i).get(c) == 0)
								moreMoves++;
						}
					if (moreMoves != 0)
						heuristicScore += heuristicScore(myAiScore, moreMoves);
					myAiScore = 1;
				}

				//Upsdie checkinhg
				if (i >= (this.k - 1)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i - k).get(j) == 1)
							myAiScore++;
						else if (b.board.get(i - k).get(j) == 2) {
							myAiScore = 0;
							break;
						}
					}
					moreMoves = 0;
					if (myAiScore > 0) {
						int column = j;
						for (int m = i - k + 1; m <= i - 1; m++) {
							if (b.board.get(m).get(column) == 0)
								moreMoves++;
						}
					}
					if (moreMoves != 0)
						heuristicScore += heuristicScore(myAiScore, moreMoves);
					myAiScore = 1;
				}
				
				// Downside Checking
				if (i <= row - this.k) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i + k).get(j) == 1)
							myAiScore++;
						else if (b.board.get(i + k).get(j) == 2) {
							myAiScore = 0;
							break;
						}
					}
					moreMoves = 0;
					if (myAiScore > 0) {
						int column = j;
						for (int m = i + k - 1; m <= i + 1; m--) {
							if (b.board.get(m).get(column) == 0)
								moreMoves++;
						}
					}

					if (moreMoves != 0)
						heuristicScore += heuristicScore(myAiScore, moreMoves);
					myAiScore = 1;
				}

				//Left side checking
				if (j >= this.k - 1) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i).get(j - k) == 1)
							myAiScore++;
						else if (b.board.get(i).get(j - k) == 2) {
							myAiScore = 0;
							break;
						}
					}
					moreMoves = 0;
					if (myAiScore > 0)
						for (int c = j - 1; c <= j - k + 1; c--) {
							if (b.board.get(i).get(c) == 0)
								moreMoves++;
						}
					if (moreMoves != 0)
						heuristicScore += heuristicScore(myAiScore, moreMoves);
					myAiScore = 1;
				}

				// Top right checking
				if (j <= (col - this.k) && i >= (this.k - 1)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i - k).get(j + k) == 1)
							myAiScore++;
						else if (b.board.get(i - k).get(j + k) == 2) {
							myAiScore = 0;
							break;
						}
					}
					moreMoves = 0;
					if (myAiScore > 0) {
						for (int c = 1; c < this.k; ++c) {
							int column = j + c, row = i - c;
							if (b.board.get(row).get(column) == 0)
								moreMoves++;
						}
						if (moreMoves != 0)
							heuristicScore += heuristicScore(myAiScore, moreMoves);
						myAiScore = 1;
					}
				}

				//Top left
				if (i >= (this.k - 1) && j >= (this.k - 1)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i - k).get(j - k) == 1)
							myAiScore++;
						else if (b.board.get(i - k).get(j - k) == 2) {
							myAiScore = 0;
							break;
						}
					}
					moreMoves = 0;
					if (myAiScore > 0) {
						for (int c = 1; c < this.k; ++c) {
							int column = j - c, row = i - c;
							if (b.board.get(row).get(column) == 0)
								moreMoves++;
						}
						if (moreMoves != 0)
							heuristicScore += heuristicScore(myAiScore, moreMoves);
						myAiScore = 1;
					}
				}

				// Bottom left
				if (i <= this.row - this.k && j >= (this.k - 1)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i + k).get(j - k) == 1)
							myAiScore++;
						else if (b.board.get(i + k).get(j - k) == 2) {
							myAiScore = 0;
							break;
						}
					}
					moreMoves = 0;
					if (myAiScore > 0) {
						for (int c = 1; c < this.k; ++c) {
							int column = j - c, row = i + c;
							if (b.board.get(row).get(column) == 0)
								moreMoves++;
						}
						if (moreMoves != 0)
							heuristicScore += heuristicScore(myAiScore, moreMoves);
						myAiScore = 1;
					}
				}

				// Bottom right
				if (i <= this.row - this.k && j <= (this.col - this.k)) {
					for (k = 1; k < this.k; ++k) {
						if (b.board.get(i + k).get(j + k) == 1)
							myAiScore++;
						else if (b.board.get(i + k).get(j + k) == 2) {
							myAiScore = 0;
							break;
						}
					}
					moreMoves = 0;
					if (myAiScore > 0) {
						for (int c = 1; c < this.k; ++c) {
							int column = j + c, row = i + c;
							if (b.board.get(row).get(column) == 0)
								moreMoves++;
						}
						if (moreMoves != 0)
							heuristicScore += heuristicScore(myAiScore, moreMoves);
						myAiScore = 1;
					}
				}
			}
		}
		return heuristicScore;
	}

	public int getWinner(Board b) {
		int aiScore = 0, humanScore = 0;
		for (int i = row - 1; i >= 0; --i) {
			for (int j = 0; j <= col - 1; ++j) {
				if (b.board.get(i).get(j) == 0)
					continue;

				// Checking cells to the right
				if (j <= (col - this.k)) {
					for (int k = 0; k < this.k; ++k) {
						if (b.board.get(i).get(j + k) == 1)
							aiScore++;
						else if (b.board.get(i).get(j + k) == 2)
							humanScore++;
						else
							break;
					}
					if (aiScore == k)
						return 1;
					else if (humanScore == k)
						return 2;
					aiScore = 0;
					humanScore = 0;
				}

				// Checking cells up
				if (i >= (this.k - 1)) {
					for (int k = 0; k < this.k; ++k) {
						if (b.board.get(i - k).get(j) == 1)
							aiScore++;
						else if (b.board.get(i - k).get(j) == 2)
							humanScore++;
						else
							break;
					}
					if (aiScore == k)
						return 1;
					else if (humanScore == k)
						return 2;
					aiScore = 0;
					humanScore = 0;
				}

				// Checking diagonal up-right
				if (j <= (col - this.k) && i >= this.k - 1) {
					for (int k = 0; k < this.k; ++k) {
						if (b.board.get(i - k).get(j + k) == 1)
							aiScore++;
						else if (b.board.get(i - k).get(j + k) == 2)
							humanScore++;
						else
							break;
					}
					if (aiScore == k)
						return 1;
					else if (humanScore == k)
						return 2;
					aiScore = 0;
					humanScore = 0;
				}

				// Checking diagonal up-left
				if (j >= this.k - 1 && i >= (this.k - 1)) {
					for (int k = 0; k < this.k; ++k) {
						if (b.board.get(i - k).get(j - k) == 1)
							aiScore++;
						else if (b.board.get(i - k).get(j - k) == 2)
							humanScore++;
						else
							break;
					}
					if (aiScore == k)
						return 1;
					else if (humanScore == k)
						return 2;
					aiScore = 0;
					humanScore = 0;
				}
			}
		}

		if (this.g == 0)
			for (int i = 0; i < this.row; i++) {
				for (int j = 0; j < this.col; j++) {
					if (b.board.get(i).get(j) == 0)
						return -1;
				}
			}
		else 
			for (int j = 0; j < this.col; ++j) {
				if (b.board.get(0).get(j) == 0)
					return -1;
			}
		return 0;
	}

	public int minimax(int depth, int turn, int alpha, int beta) {
		if (beta <= alpha) {
			if (turn == 1)
				return Integer.MAX_VALUE;
			else
				return Integer.MIN_VALUE;
		}
		int gameResult = getWinner(b);
		if (gameResult == 1)
			return Integer.MAX_VALUE / 2;
		else if (gameResult == 2)
			return Integer.MIN_VALUE / 2;
		else if (gameResult == 0)
			return 0;

		if (depth == 9)
			return getBoardScore(b);

		int maxScore = Integer.MIN_VALUE, minScore = Integer.MAX_VALUE;
		for (int j = 0; j <= col - 1; ++j) {
			int currentScore = 0;
			if (!isLegalMove(j))
				continue;

			if (turn == 1) {
				placeKey(j, 1);
				currentScore = minimax(depth + 1, 2, alpha, beta);
				if (depth == 0) {
					if (currentScore > maxScore) {
						moveColumn = j;
					}
					if (currentScore == Integer.MAX_VALUE / 2) {
						undoMove(j);
						break;
					}
				}
				maxScore = Math.max(currentScore, maxScore);
				alpha = Math.max(currentScore, alpha);
			}

			else if (turn == 2) {
				placeKey(j, 2);
				currentScore = minimax(depth + 1, 1, alpha, beta);
				minScore = Math.min(currentScore, minScore);
				beta = Math.min(currentScore, beta);
			}

			undoMove(j);
			if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE)
				break;
		}
		return turn == 1 ? maxScore : minScore;
	}
}
