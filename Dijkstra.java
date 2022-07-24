import java.io.*;
import java.util.*;

public class Dijkstra {
    // adjacency list
    Map<Integer, List<Integer[]>> adj = new HashMap<>(); // node, <distance, destinationNode>

    // calcualte the SSSP;
    public void getSSSP(String fileName) {
        ArrayList<String> data = readFile(fileName);
        int numOfVertices = Integer.parseInt(data.get(0));

        // Build the adjacency list
        for (int i = 1; i < data.size(); i++) {
            String s = data.get(i);
            int[] cur = toIntArray(s);
            int src = cur[0];
            for (int j = 1; j < cur.length; j += 2) {
                int dest = cur[j];
                int weight = cur[j + 1];
                // weight 0 means no direct path to the destination node
                if (weight != 0) {
                    adj.putIfAbsent(src, new ArrayList<>());
                    adj.get(src).add(new Integer[] { weight, dest });
                }
            }
        }

        int[] dist = new int[numOfVertices + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);

        dijkstra(dist, 1);

        // print the shortest path to each node
        for (int i = 2; i < dist.length; i++) {
            System.out.println("Shortest distance to " + i + ": " + dist[i]);
        }

    }

    // run the dijkstra algorithm, store the distances in dist
    // print the path to each node
    private void dijkstra(int[] dist, int src) {
        Queue<Integer[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.add(new Integer[] { 0, src });
        // Distance to sorce node itself is 0
        dist[src] = 0;
        int[] prev = new int[dist.length];
        prev[src] = 0;
        boolean[] visited = new boolean[dist.length];

        // always pick the lightest edge to relax
        while (!pq.isEmpty()) {
            Integer[] topPair = pq.remove();
            int currNode = topPair[1];
            int currNodeTime = topPair[0];

            // skip if the new path cost more than the old one
            if (currNodeTime > dist[currNode] || !adj.containsKey(currNode)) {
                continue;
            }

            // Broadcast the signal to adjacent nodes
            for (Integer[] edge : adj.get(currNode)) {
                int time = edge[0];
                int neighborNode = edge[1];

                if (dist[neighborNode] >= currNodeTime + time) {
                    dist[neighborNode] = currNodeTime + time;
                    pq.add(new Integer[] { dist[neighborNode], neighborNode });
                    prev[neighborNode] = currNode;
                }
                if (!visited[neighborNode]) {
                    for (int i = 1; i < dist.length; i++) {
                        System.out.print(dist[i] + " ");
                    }
                    System.out.println();
                    visited[neighborNode] = true;
                }
            }

        }

        // print the results
        for (int i = 2; i < prev.length; i++) {
            System.out.print("Shorest path to " + i + ": ");
            int cur = i;
            List<Integer> temp = new ArrayList<>();
            while (cur != src) {
                temp.add(cur);
                cur = prev[cur];
            }
            System.out.print(cur + "->");
            for (int j = temp.size() - 1; j >= 0; j--) {
                System.out.print(temp.get(j));
                if (j != 0) {
                    System.out.print("->");
                }
            }
            System.out.println();
        }
    }

    // read the file and store each line of data in ArrayList
    public ArrayList<String> readFile(String fileName) {
        ArrayList<String> res = new ArrayList<>();
        try {
            File obj = new File(fileName);
            Scanner scan = new Scanner(obj);

            while (scan.hasNextLine()) {
                String data = scan.nextLine();
                res.add(data);
            }
            scan.close();
            System.out.println();
            return res;
        } catch (FileNotFoundException e) {
            System.out.print("File not found");
            e.printStackTrace();
        }
        return res;
    }

    // convert a line of data into int array
    public int[] toIntArray(String s) {
        String[] arr = s.split(" ");
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = Integer.parseInt(arr[i]);
        }
        return res;
    }

    public static void main(String[] args) {
        Dijkstra dij = new Dijkstra();
        dij.getSSSP("./input.txt");
    }

}
