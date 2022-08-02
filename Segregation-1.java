/**
 * Simulates Schelling's model of segregation on a board with 2 types of agents
 * User can adjust the parameters of the model by changing the value of
 * variables in main method
 */
//By Fan Yang
public class Segregation {
    public static void main(String[] args) throws InterruptedException {
        int size = 30;          // int var that defines the size of grid
        double similar = 0.5;   // double var that defines the threshold of similarity
        int zeroCount = 90;     // number of empty cells
        int oneCount = 405;     // number of agent One
        int twoCount = 405;     // number of agent Two

        int[] population = createPopulation(zeroCount, oneCount, twoCount);
        shuffle(population);
        int[][] grid = distributePopulation(population, size);
        StdDraw.setScale(-0.5, grid.length - 0.5);
        StdDraw.enableDoubleBuffering();
        drawGrid(grid);
        while (true) {
            updateGrid(grid, similar, zeroCount);
            drawGrid(grid);
            StdDraw.pause(200);
        }
    }

    /**
     * Converts the population distribution into an int[]
     * Parameter:
     *      zeroCount: the number of empty cells
     *      oneCount: the number of agent one
     *      twoCount: the number of agent two
     * Return value:
     *      an int array that records the distribution of population
     */
    public static int[] createPopulation(int zeroCount, int oneCount, int twoCount) {
        int[] result = new int[zeroCount + oneCount + twoCount];
        for (int i = 0; i < zeroCount; i++) {
            result[i] = 0;
        }
        for (int i = zeroCount; i < zeroCount + oneCount; i++) {
            result[i] = 1;
        }
        for (int i = zeroCount + oneCount; i < zeroCount + oneCount + twoCount; i++) {
            result[i] = 2;
        }
        return result;
    }

    /**
     * Shuffles the order of elements in a array
     * Parameter:
     *      a: the array that is going to be shuffled
     */
    public static void shuffle(int[] a) {
        for (int i = a.length - 1; i > 0; i--) {
            int j = StdRandom.uniform(i + 1);
            int temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }
    }

    /**
     * Randomly maps the population to a 2D int[]
     * Parameter:
     *      population: the int[] that records the distribution of population
     *      width: the int var that represents the size of the 2D int arr
     * Return value:
     *      the 2D int[] that records the distribution of the agents
     */
    public static int[][] distributePopulation(int[] population, int width) {
        int[][] result = new int[width][width];
        int i = 0;
        for (int r = 0; r < width; r++) {
            for (int c = 0; c < width; c++) {
                result[r][c] = population[i];
                i++;
            }
        }
        return result;
    }

    /**
     * Draws the grid and marks the agents with corresponding color,
     *      the empty space is represented by white, agent X is represented in
     *      blue and agent O is represented in red
     * Parameter:
     *      grid: the 2D int array that records the location of agents
     */
    public static void drawGrid(int[][] grid) {
        StdDraw.clear();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] == 0) {
                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.filledSquare(x, y, 0.5);
                    StdDraw.setPenColor();
                } else if (grid[x][y] == 1) {
                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.filledSquare(x, y, 0.5);
                    StdDraw.setPenColor();
                } else if (grid[x][y] == 2) {
                    StdDraw.setPenColor(StdDraw.BLUE);
                    StdDraw.filledSquare(x, y, 0.5);
                    StdDraw.setPenColor();
                }
                StdDraw.square(x, y, 0.5);
            }
        }
        StdDraw.show();
    }

    /**
     * Prints the grid to the console
     * Parameter:
     *      grid: the 2D int array that will be printed to the console
     */
    public static void printGrid(int[][] grid) {
        System.out.print("+");
        for (int i = 1; i < grid.length - 1; i++) {
            System.out.print("-");
        }
        System.out.println("+");
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                StdOut.print(grid[r][c]);
            }
            StdOut.println();
        }
    }

    /**
     * Updates the grid by moving unsatisfied agents to empty cells
     * Parameter:
     *      grid: the 2D int array that records the location of agents
     *      similar: the double var that defines the threshold of similarity
     *      zeroCount: the int var that represents the number of empty cells
     */
    public static void updateGrid(int[][] grid, double similar, int zeroCount) {
        int count = countUnsatisfied(grid, similar);
        int[][] unsatisfiedList = findUnsatisfied(grid, similar, count);
        int[][] emptyList = findEmptyCell(grid, zeroCount);
        shuffle2DByRow(unsatisfiedList);
        shuffle2DByRow(emptyList);
        relocateUnsatisfied(grid, unsatisfiedList, emptyList);
    }

    /**
     * Counts unsatisfied agents in the grid
     * Parameter:
     *      grid: the 2D int array that records the location of agents
     *      similar: the double var that defines the threshold of similarity
     * Return value:
     *      an int that represent the number of unsatisfied agents
     */
    public static int countUnsatisfied(int[][] grid, double similar) {
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] > 0) {
                    if (!isSatisfied(grid, i, j, similar)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Determines if an agent at a given location is satisfied; an
     *      agent is satisfied if the percentage of surrounding agents of the
     *      same type at given location exceeds the threshold value, if the cell
     *      at given location is not an agent, throws IllegalArgumentException
     * Parameter:
     *      grid: the 2D int array that records the location of agents
     *      row: the int that represents the row of the coordinate
     *      col: the int that represents the column of the coordinate
     *      similar: the double var that defines the threshold of similarity
     * Return value:
     *      a boolean value of the satisfaction condition of the given
     *      coordinate
     */
    public static boolean isSatisfied(int[][] grid, int row, int col, double similar) {
        if (grid[row][col] == 0) {
            throw new IllegalArgumentException("There is no agent at current" +
                    "location");
        }
        int[] count = countNeighbors(grid, row, col);
        if (count[1] + count[2] == 0) {  // no agent around is considered as satisfied
            return true;
        }
        return 1.0 * count[grid[row][col]] / (count[1] + count[2]) >= similar;
    }

    /**
     * Counts agents and empty space around a given coordinate, and
     *      returns an int array with this information
     * Parameter:
     *     grid: the 2D int array that records the location of agents
     *     row: the int that represents the row of the coordinate
     *     col: the int that represents the column of the coordinate
     * Return value:
     *      an int array of size 3; number of empty space is stored at index 0;
     *      number of O agents is stored at index 1; number of X agents is
     *      stored at index 2;
     */
    public static int[] countNeighbors(int[][] grid, int row, int col) {
        int[] count = new int[3];
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if ((i >= 0 && i < grid.length) &&
                        (j >= 0 && j < grid.length)) {
                    count[Math.abs(grid[i][j])]++;
                }
            }
        }
        count[grid[row][col]]--;
        return count;
    }

    /**
     * Finds all unsatisfied agents in the grid and records the info in a 2D int array
     * Parameters:
     *      grid:the 2D int array that records the location of agents
     *      similar: the double var that defines the threshold of similarity
     *      unsatisfiedCount: int that represents the number of unsatisfied agents
     * Return value:
     *      an int 2D array that records the coords of all unsatisfied agents
     */
    public static int[][] findUnsatisfied(int[][] grid, double similar, int unsatisfiedCount) {
        int[][] unsatisfiedList = new int[unsatisfiedCount][2];
        int listIndex = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] > 0) {
                    if (!isSatisfied(grid, i, j, similar)) {
                        unsatisfiedList[listIndex][0] = i;
                        unsatisfiedList[listIndex][1] = j;
                        listIndex++;
                    }
                }
            }
        }
        return unsatisfiedList;
    }

    /**
     * Finds all empty cells in the grid and records the info in a 2D int array
     * Parameters:
     *      grid: the 2D int array that records the location of agents
     *      zeroCount: the int that represents the number of empty cells
     * Return value:
     *      an 2D int array that records the coords of empty cells
     */
    public static int[][] findEmptyCell(int[][] grid, int zeroCount) {
        int[][] emptyList = new int[zeroCount][2];
        int listIndex = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j] == 0 && listIndex < zeroCount) {
                    emptyList[listIndex][0] = i;
                    emptyList[listIndex][1] = j;
                    listIndex++;
                }
            }
        }
        return emptyList;
    }

    /**
     * Shuffles a 2D int array by row
     * Parameter:
     *      a: 2D int array that will be shuffled
     */
    public static void shuffle2DByRow(int[][] a) {
        for (int i = a.length - 1; i > 0; i--) {
            int j = StdRandom.uniform(i + 1);
            int[] temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }
    }

    /**
     * Randomly relocates unsatisfied agents to empty cells
     * Parameters:
     *      grid: the 2D int array that records the location of agents
     *      unsatisfiedList: the 2D int array that records the coords of unsatisfied agents
     *      emptyList: the 2D int array that records the coords of empty cells
     */
    public static void relocateUnsatisfied(int[][] grid, int[][] unsatisfiedList, int[][] emptyList) {
        int minIndex = Math.min(unsatisfiedList.length, emptyList.length);
        for (int i = 0 ; i < minIndex; i++) {
            int temp = grid[unsatisfiedList[i][0]][unsatisfiedList[i][1]];
            grid[unsatisfiedList[i][0]][unsatisfiedList[i][1]] = 0;
            grid[emptyList[i][0]][emptyList[i][1]] = temp;
        }
    }
}
