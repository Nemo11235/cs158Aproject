import java.io.*;
import java.util.*;

public class Dijkstra {
    // adjacency list
    Map<Integer, List<Integer[]>> adj = new HashMap<>(); // node, <distance, destinationNode>

    // calcualte the SSSP;
    public void getSSSP() {
        ArrayList<String> data = readFile("./input.txt");
        int numOfVertices = Integer.parseInt(data.get(0));
        // Build the adjacency list
        for (int i = 1; i < data.size(); i++) {
            String s = data.get(i);
            int[] cur = toIntArray(s);
            int src = cur[0];

            for (int j = 1; j < cur.length; j += 2) {
                int dest = cur[j];
                int weight = cur[j + 1];
                if (weight != 0) {
                    adj.putIfAbsent(src, new ArrayList<>());
                    adj.get(src).add(new Integer[] { weight, dest });
                }

            }
        }

        int[] dist = new int[numOfVertices + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);

        dijkstra(dist, 1);

        for (int i = 2; i < dist.length; i++) {
            System.out.println("Shortest distance to " + i + ": " + dist[i]);
        }

    }

    // calculate the
    private void dijkstra(int[] signalReceivedAt, int src) {

        Queue<Integer[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.add(new Integer[] { 0, src });
        // Time for starting node is 0
        signalReceivedAt[src] = 0;
        int[] prev = new int[signalReceivedAt.length];
        prev[src] = -1;

        while (!pq.isEmpty()) {

            Integer[] topPair = pq.remove();
            int currNode = topPair[1];
            int currNodeTime = topPair[0];

            if (currNodeTime > signalReceivedAt[currNode] || !adj.containsKey(currNode)) {
                continue;
            }

            // Broadcast the signal to adjacent nodes
            for (Integer[] edge : adj.get(currNode)) {
                int time = edge[0];
                int neighborNode = edge[1];

                // Fastest signal time for neighborNode so far
                // signalReceivedAt[currNode] + time :
                // time when signal reaches neighborNode
                if (signalReceivedAt[neighborNode] > currNodeTime + time) {
                    signalReceivedAt[neighborNode] = currNodeTime + time;
                    pq.add(new Integer[] { signalReceivedAt[neighborNode], neighborNode });
                    prev[neighborNode] = currNode;
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
        dij.getSSSP();
    }

}
