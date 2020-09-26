import java.io.*;
import java.util.*;

class Dot {

    List<String> coordinates;
    Dot(String coordinates){
        this.coordinates = Arrays.asList(coordinates.split(","));;
    }

    Dot(List<String> coordinates){
        this.coordinates = coordinates;
    }

    public Dot() {}

    public String toString(){
        return   "coordinates: " + coordinates;
    }
}

public class Main {

    public static void main(String[] args) throws IOException {
        String path = args[0];
        int k = Integer.parseInt(args[1]);

        // basic
        HashMap<Integer, List<Dot>> basicMap = fillArray(path, k);
        HashMap<Integer, List<Dot>> newMap;
        int size = basicMap.get(0).get(0).coordinates.size();
        System.out.println(log(basicMap, k));

        // clustering
        boolean isChanged = true;
        while (isChanged) {
            Dot[] centroids = new Dot[k];

            for (int i = 0; i < k; i++) centroids[i] = moveCentroid(i, size, basicMap);
            
            newMap = classify(k, basicMap, size, centroids);
            System.out.println(log(newMap, k));
            boolean isSizeChanged = false;
            for (int i = 0; i < k; i++) {
                if (!isSizeChanged) {
                    if (basicMap.get(i).size() != newMap.get(i).size()) {
                        isSizeChanged = true;
                    }
                    for (int j = 0; j < basicMap.get(i).size(); j++) {
                        if (!isSizeChanged) {
                            for (int l = 0; l < basicMap.get(i).get(j).coordinates.size() && !isSizeChanged; l++) {
                                if (!basicMap.get(i).get(j).coordinates.get(l).equals(newMap.get(i).get(j).coordinates.get(l)))
                                    isSizeChanged = true;
                            }
                        }
                    }
                    if (i == k - 1) {
                        isChanged = false;
                    }
                }
                basicMap = newMap;
            }
        }
    }

    static String log(HashMap<Integer, List<Dot>> map, int k) {
        String output = "";
        for (int i = 0; i < k; i++) {
            double sum = 0;
            output += " Size of " + i + " is: " + map.get(i).size();
            for (int j = 0; j < map.get(i).size(); j++) {
                for (int l = j + 1; l < map.get(i).size(); l++) {
                    double tmp = 0;
                    for (int m = 0; m < map.get(i).get(j).coordinates.size(); m++) {
                        tmp += Math.pow((Double.parseDouble(map.get(i).get(j).coordinates.get(m)) 
                                         - Double.parseDouble(map.get(i).get(l).coordinates.get(m))), 2);
                    }
                    sum += tmp;
                }
            }
            output += " distance: " + sum;
        }
        return output;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;

    }

    static Dot moveCentroid(int i, int roz, HashMap<Integer, List<Dot>> map) {
        List<Double> tmpList = new ArrayList<>();
        for (int j = 0; j < roz; j++) {
            double tmp = 0;
            for (int l = 0; l < map.get(i).size(); l++) tmp += Double.parseDouble(map.get(i).get(l).coordinates.get(j));
            tmpList.add(tmp / map.get(i).size());
        }
        return new Dot(String.valueOf(tmpList));
    }


    static HashMap<Integer, List<Dot>> fillArray(String path, int k) throws IOException {
        HashMap<Integer, List<Dot>> tmpMap = new HashMap<>();
        Scanner scn = new Scanner(new File(path));
        for (int i = 0; scn.hasNextLine(); i = (i + 1) % k) {
            if (!tmpMap.containsKey(i)) tmpMap.put(i, new ArrayList<>());
            tmpMap.get(i).add(new Dot(scn.nextLine()));
        }
        return tmpMap;
    }

    static HashMap<Integer, List<Dot>> classify(int k, HashMap<Integer, List<Dot>> myMap, int datsetSize, Dot[] centroids) {
        HashMap<Integer, List<Dot>> tmpMap = new HashMap<>();
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < myMap.get(i).size(); j++) {
                double min = Double.MAX_VALUE;
                int key = i;
                for (int centroid = 0; centroid < centroids.length; centroid++) {
                    double tmpDistance = 0;
                    for (int coordinates = 0; coordinates < datsetSize; coordinates++) {
                        tmpDistance += countDistance(myMap.get(i).get(j).coordinates.get(coordinates), centroids[centroid].coordinates.get(coordinates));
                    }
                    if (min > tmpDistance) {
                        min = tmpDistance;
                        key = centroid;
                    }
                }
                if (tmpMap.containsKey(key)) {
                } else {
                    tmpMap.put(key, new ArrayList<>());
                }
                tmpMap.get(key).add(myMap.get(i).get(j));
            }
        }
        return tmpMap;
    }

    static Double countDistance(String coordinateFrom, String coordinateTo) {
        coordinateFrom = coordinateFrom.replace("[", "").replace("]", "");
        coordinateTo = coordinateTo.replace("[", "").replace("]", "");
        Double res = Math.pow((Double.parseDouble(coordinateFrom) - Double.parseDouble(coordinateTo)), 2);

        return res;
    }

}
