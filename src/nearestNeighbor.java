import java.io.*;
import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class nearestNeighbor {


    static void search(ArrayList<ArrayList<Double>> data){
        ArrayList<Integer>current_set_of_features = new ArrayList<>();//Init empty set
        //I think this is a single value...
        //ArrayList<Integer>feature_to_add_at_this_level;
        Integer feature_to_add_at_this_level = null;
        Double best_so_far_accuracy;
        Double accuracy;

        //go through for each feature/column
        for(int i = 0; i < data.get(0).size(); i++){
//            System.out.println("On the " + i + "th level of the search tree");

            best_so_far_accuracy = Double.valueOf(0);
            for (int k = 0; k < data.get(0).size(); k++){
                if(!current_set_of_features.contains(k)){
//                    System.out.println("--Considering adding the "+ k + " feature");
//                    accuracy = leave_one_out_cross_validation(data, current_set_of_features, k + 1);
                    accuracy = 0.0;
                    if(accuracy > best_so_far_accuracy){
                        best_so_far_accuracy = accuracy;
                        feature_to_add_at_this_level = k;
                    }
                }
            }
            System.out.println("On level " + i + " i, added feature " + feature_to_add_at_this_level + " to current set");
        }
    }

    static Double leave_one_out_cross_validation(ArrayList<ArrayList<Double>> data){
//            ,
//                                                ArrayList<Integer> current_set, //mke sure to add the feature to this set to track
//                                                Integer feature_to_add){
        int number_correctly_classified = 0;
        ArrayList<Double> object_to_classify;
        Double label_object_to_classify;

        Double nearest_neighbor_distance;
        Integer nearest_neighbor_location;
        Double distance;

        Double nearest_neighbor_label = null;

        //loop over number of object/rows
        for(int i = 0; i < data.size(); i++){
            object_to_classify = new ArrayList<Double>(data.get(i).subList(1,data.get(i).size()));
            label_object_to_classify = data.get(i).get(0);
//            System.out.println("Looping over i, at the " + i + " location");
//            System.out.println("The " + i + "th object is in class " + label_object_to_classify);
            nearest_neighbor_distance = Double.MAX_VALUE;
            nearest_neighbor_location = Integer.MAX_VALUE;
            for(int k = 0; k < data.size(); k++){
                if(k != i){
//                    System.out.println("Ask if " + i + " is nearest neighbor with " + k);
                    ArrayList<Double> neighbor_test = new ArrayList<>(data.get(k).subList(1,data.get(k).size()));
                    distance = calc_euclidean_distance(object_to_classify, neighbor_test);
                    if(distance < nearest_neighbor_distance){
                        nearest_neighbor_distance = distance;
                        nearest_neighbor_location = k;
                        nearest_neighbor_label = data.get(nearest_neighbor_location).get(0);//remember that label is first column
                    }
                }
            }
            if(label_object_to_classify.equals(nearest_neighbor_label)){
                number_correctly_classified++;
            }
//            System.out.println("Object " + i + " is class " + label_object_to_classify);
//            System.out.println("Its nearest neighbor is " + nearest_neighbor_location + " which is in class " + nearest_neighbor_label);
        }

        return Double.valueOf(number_correctly_classified) / Double.valueOf(data.size());
//        Random r = new Random();
//        return r.nextDouble();
    }







    //The following are helper functions that are less related to the lab. YOU CAN SKIP THIS SECTION TO MAIN

    //Calculates the euclidean distance
    static Double calc_euclidean_distance(ArrayList<Double> a, ArrayList<Double> b){
        Double diff_squared_sum = 0.0;
        for(int i = 0; i < a.size(); i++){
            diff_squared_sum += Math.pow(a.get(i) - b.get(i), 2.0);
        }
        return Math.sqrt(diff_squared_sum);
    }

    //takes a dataset and makes an entire column 0, remember that 1st column is column 0 and it is the classifiers
    static void zero_a_feature(ArrayList<ArrayList<Double>> data, int column){
        for(int i = 0; i < data.size(); i++) {
            data.get(i).set(column, 0.0);
        }
//        print(data);
    }

    //Loads a file into a table, multi-dimensional array
    static ArrayList<ArrayList<Double>> load(String filename){
        ArrayList<ArrayList<Double>> table = new ArrayList<>();
        ArrayList<Double> row;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine()) != null){
                row = new ArrayList<Double>();
                try(Scanner scanner = new Scanner(line)){
                    while(scanner.hasNext()){
                        row.add(scanner.nextDouble());
                    }
                }
                table.add(row);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(table);
        return table;
    }

    //adds the newline to our print
    static void print(ArrayList<ArrayList<Double>> data){
        for(int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i));
        }
    }




    public static void main(String[] args){
        ArrayList<ArrayList<Double>> l = load("CS170_SMALLtestdata__1.txt");
//        ArrayList<ArrayList<Double>> l = load("CS170_small_special_testdata__99.txt");
//        ArrayList<ArrayList<Double>> l = load("CS170_small_special_testdata__95.txt");
//        ArrayList<ArrayList<Double>> l = load("smalltest");
        // l.get(0).size() returns col count which is # of features
        //This assumes that all column counts are the same
        // l.size() returns row count which is # of entries
//        feature_search_demo(l);
        //zero_a_feature(l, 1);
        //print(l);
        ArrayList<Integer> f = new ArrayList<>();
        Integer add = new Integer(0);
        System.out.println("Accuracy is: " + leave_one_out_cross_validation(l));
    }
}
