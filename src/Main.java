import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

class Solution {
   int[][] dirs = {{0,1}, {0,-1}, {1,0},{-1,0}};
   int days = 2; //will need to change to -2 since start value of rotten orange in 2
    Queue<int[]> queue = new LinkedList<>();

    public int orangesRotting(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int nr = grid.length;
        int nc = grid[0].length;
        for (int r = 0; r < nr; ++r) {
            for (int c = 0; c < nc; ++c) {
                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                } else if (grid[r][c] == 1){
                    grid[r][c] = -1;
                }
            }
        }
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] d : dirs) {
                int r = cell[0] + d[0], c = cell[1] + d[1];
                if (r < 0 || r >= nr || c < 0 || c >= nc || grid[r][c] != -1) continue;
                queue.add(new int[]{r, c});
                grid[r][c] = grid[cell[0]][cell[1]] + 1;
                days = Math.max(grid[r][c], days);
            }
        }

        for (int r = 0; r < nr; ++r) {
            for (int c = 0; c < nc; ++c) {
                if (grid[r][c] == -1){
                    return -1;
                }
            }
        }
        return (days-2);

    }
}
//////////////////////////////

// O(MN) both time and space
class Solution {
    public int orangesRotting(int[][] grid) {
        Queue<Pair<Integer, Integer>> queue = new ArrayDeque(); // pair class not local to java. would need to build or just replace with an int[2]

        // Step 1). build the initial set of rotten oranges
        int freshOranges = 0;
        int ROWS = grid.length, COLS = grid[0].length;

        for (int r = 0; r < ROWS; ++r) // loop left right top down
            for (int c = 0; c < COLS; ++c)
                if (grid[r][c] == 2)
                    queue.offer(new Pair(r, c)); // add coordinate to que if rotten
                else if (grid[r][c] == 1)
                    freshOranges++; // increment count

        // Mark the round / level, _i.e_ the ticker of timestamp
        queue.offer(new Pair(-1, -1)); // will act as a delimiter such that all original rotten oranges added to que, then
        // delimiter pair which again would just be an int[] with -1 for indexes 0 and 1

        // Step 2). start the rotting process via BFS
        int minutesElapsed = -1; // start at -1 to account for pre-time increment of adding neighbors of rotten oranges
        int[][] directions = { {-1, 0}, {0, 1}, {1, 0}, {0, -1}};

        while (!queue.isEmpty()) {
            Pair<Integer, Integer> p = queue.poll(); //
            int row = p.getKey();
            int col = p.getValue();
            if (row == -1) { // suppose all OG bad oranges polled. minutes elapseed -1 --> 0 after delimeter hit
                minutesElapsed++; // so after first round of neighbors handled, time increment will be 1, etc.
                if (!queue.isEmpty()) // if neighbors still unhandled, add a new delimiter
                    queue.offer(new Pair(-1, -1));
            } else { // que 4-directional neighbors of rotten orange
                for (int[] d : directions) {
                    int neighborRow = row + d[0];
                    int neighborCol = col + d[1];
                    if (neighborRow >= 0 && neighborRow < ROWS &&
                            neighborCol >= 0 && neighborCol < COLS) {
                        if (grid[neighborRow][neighborCol] == 1) { // adjacent orange found, will be rotton
                            grid[neighborRow][neighborCol] = 2; // change value so not revisited
                            freshOranges--;
                            queue.offer(new Pair(neighborRow, neighborCol)); // que it
                        }
                    }
                }
            }
        }

        return freshOranges == 0 ? minutesElapsed : -1; // if no rotten oranges, return time
    }
}

// uses 'in-place' algorithm to eliminate need for que in BFS. i kinda did this to get my time unit, but didn't replace the queue
// time comoplexity = O(M^2 N^2) but space O(1). despite what editorial says is a slower time complexity, this runs the fastest
class Solution {
    // run the rotting process, by marking the rotten oranges with the timestamp
    public boolean runRottingProcess(int timestamp, int[][] grid, int ROWS, int COLS) {
        int[][] directions = { {-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        // flag to indicate if the rotting process should be continued
        boolean toBeContinued = false;
        for (int row = 0; row < ROWS; ++row) //  L R top bottom
            for (int col = 0; col < COLS; ++col)
                if (grid[row][col] == timestamp)

                    for (int[] d : directions) { // test neighbors
                        int nRow = row + d[0], nCol = col + d[1];
                        if (nRow >= 0 && nRow < ROWS && nCol >= 0 && nCol < COLS)
                            if (grid[nRow][nCol] == 1) { // if good orange change val
                                grid[nRow][nCol] = timestamp + 1;
                                toBeContinued = true; // true since will need to test neighbors of new rrotton orange
                                // to see if any of them are valued '1'
                            }
                    }
        return toBeContinued;
    }

    public int orangesRotting(int[][] grid) {
        int ROWS = grid.length, COLS = grid[0].length;
        int timestamp = 2;
        while (runRottingProcess(timestamp, grid, ROWS, COLS)) // while method returns true, rotten oranges exists whose
            timestamp++; // neighbors not been tested, so inc stamp

        // end of process, to check if there are still fresh oranges left
        for (int[] row : grid)
            for (int cell : row)
                // still got a fresh orange left
                if (cell == 1)
                    return -1;


        // return elapsed minutes if no fresh orange left
        return timestamp - 2;
    }
}


// is there only 1 bad orange at beginning?
// can there be multiple 'islands' of oranges?
// sounds like combo of # of islands and 01 matrix problems
// seems BFS will work better for incrementing adjacent cells
// lets assume only 1 rotting orange otherwise too hard
