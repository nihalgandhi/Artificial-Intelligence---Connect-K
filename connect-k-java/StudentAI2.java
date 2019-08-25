import java.util.*;

//The following part should be completed by students.

//Students can modify anything except the class name and exisiting functions and varibles.

public class StudentAI extends AI

{

	private int row;
	private int col;
	private int g;
	private int k;
	private int nextMoveLocation = -1;
	private int nextMoveLocationRow = -1;

	Board b;

	public StudentAI(int col, int row, int k, int g)

	{

		super(col, row, k, g);
		this.row = row;
		this.col = col;
		this.g = g;
		this.k = k;
		b = new Board(col, row, k, g);

	}

	public Move GetMove(Move move)

	{

		try {

			if (!(move.row == -1 || move.col == -1))

				b = b.MakeMove(new Move(move.col, move.row), 2);

		} catch (InvalidMoveError e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		Move result = null;
		if (this.g == 1) {
			getAIMove();

			// b=new Board(b.col,b.row,b.k,b.g);
			try {
				int next_row_number = getRowNumber(nextMoveLocation);
				if (next_row_number != -1)
					result = new Move(nextMoveLocation, next_row_number);
				b = b.MakeMove(result, 1);
			} catch (InvalidMoveError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			result = findBestMove();
			try {
				b = b.MakeMove(result, 1);
			} catch (InvalidMoveError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}

	public int getAIMove() {

		nextMoveLocation = -1;

		minimax(0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

		return nextMoveLocation;

	}

	public boolean isLegalMove(int row, int column) {

		return b.board.get(row).get(column) == 0;

	}
	
	public boolean isLegalMove(int column) {

		return b.board.get(0).get(column) == 0;

	}

	public int getRowNumber(int column) {

//        if(!isLegalMove(column)) {System.out.println("Illegal move!"); return false;}

		for (int i = row - 1; i >= 0; --i) {

			if (b.board.get(i).get(column) == 0) {

				// nextRow=i;

				return i;

			}

		}

		return -1;

	}

	public boolean placeMove(int row, int column, int player) {

		if (!isLegalMove(row, column)) {
			System.out.println("Illegal move!");
			return false;
		}


		try {
			b = b.MakeMove(new Move(column, row), player);
		} catch (InvalidMoveError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;

	}
	
	public boolean placeMove(int column, int player) {

		if (!isLegalMove(column)) {
			System.out.println("Illegal move!");
			return false;
		}

		for (int i = row - 1; i >= 0; --i) {

			if (b.board.get(i).get(column) == 0) {

				// nextRow=i;

				// b.board.get(i).set(column,player);
				try {
					b = b.MakeMove(new Move(column, i), player);
				} catch (InvalidMoveError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;

			}

		}

		return false;

	}

	public void undoMove(int row, int column) {

		b.board.get(row).set(column, 0);

	}
	
	public void undoMove(int column) {

		for (int i = 0; i <= row - 1; ++i) {

			if (b.board.get(i).get(column) != 0) {
				// b.board.get(i).set(column,0);
				b.board.get(i).set(column, 0);
				break;

			}

		}

	}

	int calculateScore(int aiScore, int moreMoves) {

		int moveScore = k - moreMoves;

		if (aiScore == 0)
			return 0;
		/*
		 * else if(aiScore==1)return 1*moveScore;
		 * 
		 * else if(aiScore==2)return 10*moveScore;
		 * 
		 * else if(aiScore==3)return 100*moveScore;
		 */

		else if (aiScore >= 1 && aiScore <= k - 1) {
			return (int) Math.pow(10, aiScore - 1) * (moveScore);
		} else
			return (int) Math.pow(10, k - 1);

	}

	public boolean isMovesLeft(Board b) {
		for (int i = 0; i < b.row; i++)
			for (int j = 0; j < b.col; j++)
				if (b.board.get(i).get(j) == 0)
					return true;
		return false;
	}

	int minimaxwoGrav(int depth, int turn, int alpha, int beta) {

		if (beta <= alpha) {
			if (turn == 1)
				return Integer.MAX_VALUE;
			else
				return Integer.MIN_VALUE;
		}

		int gameResult = gameResult(b);

		if (gameResult == 1)
			return Integer.MAX_VALUE / 2;

		else if (gameResult == 2)
			return Integer.MIN_VALUE / 2;

		else if (gameResult == 0)
			return 0;

		if (depth == 4)
			return evaluateBoardGrave(b);

		int maxScore = Integer.MIN_VALUE, minScore = Integer.MAX_VALUE;
		for (int i = 0; i <= row - 1; ++i) {
			boolean isMaxScore = false;
			for (int j = 0; j <= col - 1; ++j) {

				int currentScore = 0;

				if (!isLegalMove(i, j))
					continue;

				if (turn == 1) {

					placeMove(i, j, 1);

					// System.out.println
					currentScore = minimaxwoGrav(depth + 1, 2, alpha, beta);

					if (depth == 0) {

						// System.out.println("Score for location "+j+" = "+currentScore);

						if (currentScore > maxScore) {
							nextMoveLocation = j;
							nextMoveLocationRow = i;

						}

						if (currentScore == Integer.MAX_VALUE / 2) {
							undoMove(i, j);
							break;
						}

					}

					maxScore = Math.max(currentScore, maxScore);

					alpha = Math.max(currentScore, alpha);

				}

				else if (turn == 2) {

					placeMove(i, j, 2);

					currentScore = minimaxwoGrav(depth + 1, 1, alpha, beta);

					minScore = Math.min(currentScore, minScore);

					beta = Math.min(currentScore, beta);

				}

				undoMove(i, j);

				if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) {
					isMaxScore = true;
					break;
				}

			}
			if(isMaxScore)
				break;
		}

		return turn == 1 ? maxScore : minScore;

	}

	private int evaluate(Board b) {
		int sum;
		for (int i = 0; i < b.row; i++) {
			sum = 0;
			for (int j = 0; j < b.col; j++) {
				sum = sum + b.board.get(i).get(j);

				if (sum == b.k)
					return 10;
				else if (sum == b.k * 2)
					return -10;
			}
		}

		sum = 0;
		for (int j = 0; j < b.col; j++) {
			sum = 0;
			for (int i = 0; i < b.row; i++) {
				sum = sum + b.board.get(i).get(j);

				if (sum == b.k)
					return 10;
				else if (sum == (b.k * 2))
					return -10;
			}
		}

		/*
		 * int tRow = b.row-1; int tCol = b.col-1; // temp variables for current row and
		 * column for use in loops int count_1 = 0; int count_2 = 0; // -------------
		 * check for a \ type diagonal win --------------------- while (tRow<row-1 &&
		 * tCol>0) { // while the indices are within the bounds of the board tRow++;
		 * tCol--; } // this loop will trace the diagonal up and to the left, to
		 * whatever edge it hits first while (tRow>=0 && tCol<=col-1) { // again while
		 * the indecies are within the bounds of the board
		 * if(b.board.get(tRow).get(tCol)==1) count_1++; else count_1 = 0;
		 * 
		 * if(b.board.get(tRow).get(tCol)==2) count_2++; else count_2 = 0; if(count_1 ==
		 * k) { return 10; } if(count_2==k) { return -10; } tRow--; tCol++; } // this
		 * loop traces the diagonal back down and to the right, checking for a win combo
		 * 
		 * // now to reset the temp variables and run this again // ----------- this
		 * time mirrored to check for / wins ----------------- tRow = b.row-1; tCol =
		 * b.col-1; count_1 = 0; count_2=0; // check for a / type diagonal win while
		 * (tRow>0 && tCol>0) { // while the indices are within the bounds of the board
		 * tRow--; tCol--; } // this loop will trace the diagonal down and to the left,
		 * to whatever edge it hits first while (tRow<=row-1 && tCol<=col-1) { // again
		 * while the indecies are within the bounds of the board
		 * if(b.board.get(tRow).get(tCol)==1) count_1++; else count_1 = 0;
		 * 
		 * if(b.board.get(tRow).get(tCol)==2) count_2++; else count_2 = 0; if(count_1 ==
		 * k) { return 10; } if(count_2==k) { return -10; } tRow++; tCol++; // this loop
		 * traces the diagonal back up and to the right, checking for a win combo } //
		 * end if, checking for a diagonal win
		 */
		int aiScore = 0;
		int humanScore = 0;

		for (int i = row - 1; i >= 0; --i) {

			for (int j = 0; j <= col - 1; ++j) {

				if (b.board.get(i).get(j) == 0 || b.board.get(i).get(j) == 2)
					continue;

				if (j <= (col - this.k) && i >= this.k - 1) {

					for (int k = 0; k < this.k; ++k) {

						if (b.board.get(i - k).get(j + k) == 1)
							aiScore++;

						else if (b.board.get(i - k).get(j + k) == 2)
							humanScore++;

						if (aiScore == k)
							return 10;
						else if (humanScore == k)
							return -10;
					}

				}

				aiScore = 0;
				humanScore = 0;

				// Checking diagonal up-left

				if (j >= this.k - 1 && i >= (this.k - 1)) {

					for (int k = 0; k < this.k; ++k) {

						if (b.board.get(i - k).get(j - k) == 1)
							aiScore++;

						else if (b.board.get(i - k).get(j - k) == 2)
							humanScore++;

						if (aiScore == k)
							return 10;
						else if (humanScore == k)
							return -10;
					}

				}
				aiScore = 0;
				humanScore = 0;
			}
		}

		return 0;
	} // now to reset the temp variables and run this again
// ----------- this time mirrored to check for / wins -----------------

	Move findBestMove() {
		nextMoveLocation = -1;
		nextMoveLocationRow = -1;

		minimaxwoGrav(0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

		return new Move(nextMoveLocation, nextMoveLocationRow);
	}
	
	Move findBestMove(Board b, int player) {
		int bestVal = (int) Double.NEGATIVE_INFINITY;
		Move bestMove = new Move();

		// Traverse all cells, evaluate minimax function for
		// all empty cells. And return the cell with optimal
		// value.
		for (int i = 0; i < b.row; i++) {
			for (int j = 0; j < b.col; j++) {
				// Check if cell is empty
				if (b.board.get(i).get(j) == 0) {
					// Make the move
					b.board.get(i).set(j, player);

					// compute evaluation function for this
					// move.
					int moveVal = minimaxwoGrav(0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

					// Undo the move
					b.board.get(i).set(j, 0);

					// If the value of the current move is
					// more than the best value, then update
					// best/
					if (moveVal > bestVal) {
						bestMove.row = i;
						bestMove.col = j;
						bestVal = moveVal;
					}
				}
			}
		}

		System.out.println("The value of the best Move is " + bestVal + "column" + bestMove.col + "row" + bestMove.row);

		return bestMove;
	}

	// Evaluate board favorableness for AI

	public int evaluateBoard(Board b) {

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

						// else blanks++;

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
						heuristicScore += calculateScore(myAiScore, moreMoves);

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
						heuristicScore += calculateScore(myAiScore, moreMoves);

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
						heuristicScore += calculateScore(myAiScore, moreMoves);

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
									;

								else
									break;

							}

						}

						if (moreMoves != 0)
							heuristicScore += calculateScore(myAiScore, moreMoves);

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
									;

								else
									break;

							}

						}

						if (moreMoves != 0)
							heuristicScore += calculateScore(myAiScore, moreMoves);

						myAiScore = 1;

						blanks = 0;

					}

				}

			}

		}

		return heuristicScore;

	}

	public int evaluateBoardGrave(Board b) {

		int myAiScore = 1;

		int heuristicScore = 0;

		int k = 0, moreMoves = 0;

		for (int i = row - 1; i >= 0; --i) {

			for (int j = 0; j <= col - 1; ++j) {

				if (b.board.get(i).get(j) == 0 || b.board.get(i).get(j) == 2)
					continue;

//Right side checking
				if (j <= (col - this.k)) {

					for (k = 1; k < this.k; ++k) {

						if (b.board.get(i).get(j + k) == 1)
							myAiScore++;

						else if (b.board.get(i).get(j + k) == 2) {
							myAiScore = 0;
							break;
						}

						// else blanks++;

					}

					moreMoves = 0;

					if (myAiScore > 0)

						for (int c = j + 1; c <= j + k - 1; ++c) {

							// int column = j+c;

							// for(int m=i; m<= row-1;m++){

							if (b.board.get(i).get(c) == 0)
								moreMoves++;

							// else break;

						}

					if (moreMoves != 0)
						heuristicScore += calculateScore(myAiScore, moreMoves);

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
						heuristicScore += calculateScore(myAiScore, moreMoves);

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
						heuristicScore += calculateScore(myAiScore, moreMoves);

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

						// else blanks++;

					}

					moreMoves = 0;

					if (myAiScore > 0)

						for (int c = j - 1; c <= j - k + 1; c--) {

							// int column = j- c;

							// for(int m=i; m<= row-1;m++){

							if (b.board.get(i).get(c) == 0)
								moreMoves++;

							// else break;

						}

					if (moreMoves != 0)
						heuristicScore += calculateScore(myAiScore, moreMoves);

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

						// else blanks++;

					}

					moreMoves = 0;

					if (myAiScore > 0) {

						for (int c = 1; c < this.k; ++c) {

							int column = j + c, row = i - c;

							// for(int m=row;m<=row-1;++m){

							if (b.board.get(row).get(column) == 0)
								moreMoves++;

							// else if(b.board.get(m).get(column)==1);

							// else break;

						}

						if (moreMoves != 0)
							heuristicScore += calculateScore(myAiScore, moreMoves);

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

						// else blanks++;

					}

					moreMoves = 0;

					if (myAiScore > 0) {

						for (int c = 1; c < this.k; ++c) {

							int column = j - c, row = i - c;

							// for(int m=row;m<=row-1;++m){

							if (b.board.get(row).get(column) == 0)
								moreMoves++;

							// else if(b.board.get(m).get(column)==1);

							// else break;

						}

						if (moreMoves != 0)
							heuristicScore += calculateScore(myAiScore, moreMoves);

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

						// else blanks++;

					}

					moreMoves = 0;

					if (myAiScore > 0) {

						for (int c = 1; c < this.k; ++c) {

							int column = j - c, row = i + c;

							// for(int m=row;m<=row-1;++m){

							if (b.board.get(row).get(column) == 0)
								moreMoves++;

							// else if(b.board.get(m).get(column)==1);

							// else break;

						}

						if (moreMoves != 0)
							heuristicScore += calculateScore(myAiScore, moreMoves);

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

						// else blanks++;

					}

					moreMoves = 0;

					if (myAiScore > 0) {

						for (int c = 1; c < this.k; ++c) {

							int column = j + c, row = i + c;

							// for(int m=row;m<=row-1;++m){

							if (b.board.get(row).get(column) == 0)
								moreMoves++;

							// else if(b.board.get(m).get(column)==1);

							// else break;

						}

						if (moreMoves != 0)
							heuristicScore += calculateScore(myAiScore, moreMoves);

						myAiScore = 1;


					}

				}

			}

		}

		return heuristicScore;

	}

	public int gameResult(Board b) {

		int aiScore = 0, humanScore = 0;

		// System.out.println(b.board.size());

		// System.out.println(b.board.get(0).size());

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

		for (int j = 0; j < this.col; ++j) {

			// Game has not ended yet

			if (b.board.get(0).get(j) == 0)
				return -1;

		}

		// Game draw!

		return 0;

	}

	public int minimax(int depth, int turn, int alpha, int beta) {

		if (beta <= alpha) {
			if (turn == 1)
				return Integer.MAX_VALUE;
			else
				return Integer.MIN_VALUE;
		}

		int gameResult = gameResult(b);

		if (gameResult == 1)
			return Integer.MAX_VALUE / 2;

		else if (gameResult == 2)
			return Integer.MIN_VALUE / 2;

		else if (gameResult == 0)
			return 0;

		if (depth == this.row)
			return evaluateBoard(b);

		int maxScore = Integer.MIN_VALUE, minScore = Integer.MAX_VALUE;

		for (int j = 0; j <= col - 1; ++j) {

			int currentScore = 0;

			if (!isLegalMove(j))
				continue;

			if (turn == 1) {

				placeMove(j, 1);

				// System.out.println
				currentScore = minimax(depth + 1, 2, alpha, beta);

				if (depth == 0) {

					// System.out.println("Score for location "+j+" = "+currentScore);

					if (currentScore > maxScore) {
						nextMoveLocation = j;

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

				placeMove(j, 2);

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