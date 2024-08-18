import java.util.*;

public class Qno_4a {
    static class Edge {
        int to, weight;
        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public static void main(String[] args) {
        int n = 5;
        int[][] roads = {
                {4, 1, -1},
                {2, 0, -1},
                {0, 3, -1},
                {4, 3, -1}
        };
        int source = 0;
        int destination = 1;
        int targetTime = 5;

        List<int[]> result = findValidModification(n, roads, source, destination, targetTime);

        // Print the modified roads in a readable format
        System.out.println("Modified road network to achieve the target travel time:");
        for (int[] road : result) {
            System.out.printf("Road from %d to %d with modified time %d%n", road[0], road[1], road[2]);
        }
    }

    public static List<int[]> findValidModification(int n, int[][] roads, int source, int destination, int targetTime) {
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        List<int[]> underConstruction = new ArrayList<>();
        Map<String, Integer> roadMap = new HashMap<>();

        // Initialize the graph with known roads and keep track of under construction roads
        for (int[] road : roads) {
            if (road[2] == -1) {
                underConstruction.add(road);
            } else {
                graph.get(road[0]).add(new Edge(road[1], road[2]));
                graph.get(road[1]).add(new Edge(road[0], road[2]));
                roadMap.put(road[0] + "-" + road[1], road[2]);
                roadMap.put(road[1] + "-" + road[0], road[2]);
            }
        }

        // Apply minimum time to all under construction roads to check feasibility
        for (int[] road : underConstruction) {
            road[2] = 1;
            graph.get(road[0]).add(new Edge(road[1], road[2]));
            graph.get(road[1]).add(new Edge(road[0], road[2]));
        }

        int initialDistance = dijkstra(graph, source, destination, n);
        System.out.println("Initial distance with minimum times: " + initialDistance);

        // Check if the initial distance meets the target
        if (initialDistance == targetTime) {
            System.out.println("The initial setup meets the target time.");
            return Arrays.asList(roads);
        } else {
            // Calculate the extra time needed to reach the target
            int extraTime = targetTime - initialDistance;
            System.out.println("Need to add extra time of: " + extraTime);

            // Adjust the road weights to meet the target time
            for (int[] road : underConstruction) {
                if (extraTime > 0) {
                    int additionalTime = Math.min(extraTime, Integer.MAX_VALUE - road[2]);
                    road[2] += additionalTime;
                    extraTime -= additionalTime;
                }
            }

            // Rebuild the graph with updated weights
            for (int[] road : roads) {
                graph.get(road[0]).clear();
                graph.get(road[1]).clear();
            }
            for (int[] road : roads) {
                graph.get(road[0]).add(new Edge(road[1], road[2]));
                graph.get(road[1]).add(new Edge(road[0], road[2]));
            }

            System.out.println("Final adjusted roads to meet the target travel time:");
            return Arrays.asList(roads);
        }
    }

    public static int dijkstra(List<List<Edge>> graph, int source, int destination, int n) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(edge -> edge.weight));
        pq.add(new Edge(source, 0));

        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int u = current.to;
            int d = current.weight;

            if (d > dist[u]) continue;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                int weight = edge.weight;
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pq.add(new Edge(v, dist[v]));
                }
            }
        }
        return dist[destination];
    }
}
