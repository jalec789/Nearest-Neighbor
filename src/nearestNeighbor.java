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
//            System.out.println("On the " + (i+1) + "th level of the search tree");

            best_so_far_accuracy = Double.valueOf(0);
            for (int k = 1; k < data.get(0).size(); k++){
                if(!current_set_of_features.contains(k)){
//                    System.out.println("--Considering adding the "+ (k+1) + " feature");
                    accuracy = leave_one_out_cross_validation(data, current_set_of_features, k);
                    if(accuracy > best_so_far_accuracy){
                        best_so_far_accuracy = accuracy;
                        feature_to_add_at_this_level = k;
                    }
                }
            }
            current_set_of_features.add(feature_to_add_at_this_level);
//            System.out.println("On level " + (i+1) + " i, added feature "
//                    + (feature_to_add_at_this_level+1) + " to current set\n");
        }
    }

    static Double leave_one_out_cross_validation(ArrayList<ArrayList<Double>> data,
                                                ArrayList<Integer> current_set, //make sure to add the feature to this set to track
                                                Integer feature_to_add){


        ArrayList<ArrayList<Double>> local_copy = new ArrayList<ArrayList<Double>>();
        //Copy Here so we don't alter Arraylist data
        for (int i = 0; i < data.size(); i++) {
             local_copy.add(new ArrayList<Double>(data.get(i)));// = new ArrayList<Double>(data.get(i));
        }

        //zero all columns we are not using
        for (int i = 1; i < local_copy.get(i).size(); i++) {//skip over first column
            if(!feature_to_add.equals(i)){
                if( !current_set.contains(i)){
                    zero_a_feature(local_copy,i);
                }
            }
        }



        int number_correctly_classified = 0;
        ArrayList<Double> object_to_classify;
        Double label_object_to_classify;

        Double nearest_neighbor_distance;
        Integer nearest_neighbor_location;
        Double distance;

        Double nearest_neighbor_label = null;

        //loop over number of object/rows
        for(int i = 0; i < local_copy.size(); i++){
            //sublist to skip over the validation column
            object_to_classify = new ArrayList<Double>(local_copy.get(i).subList(1,local_copy.get(i).size()));
            label_object_to_classify = local_copy.get(i).get(0);//validation value
//            System.out.println("\tLooping over i, at the " + (i+1) + " location");
//            System.out.println("\tThe " + (i+1) + "th object is in class " + (label_object_to_classify+1));
            nearest_neighbor_distance = Double.MAX_VALUE;
            nearest_neighbor_location = Integer.MAX_VALUE;
            for(int k = 0; k < local_copy.size(); k++){
                if(k != i){
//                    System.out.println("\tAsk if " + (i+1) + " is nearest neighbor with " + (k+1));
                    ArrayList<Double> neighbor_test = new ArrayList<>(local_copy.get(k).subList(1,local_copy.get(k).size()));
                    distance = calc_euclidean_distance(object_to_classify, neighbor_test);
                    if(distance < nearest_neighbor_distance){
                        nearest_neighbor_distance = distance;
                        nearest_neighbor_location = k;
                        nearest_neighbor_label = local_copy.get(nearest_neighbor_location).get(0);//remember that label is first column
                    }
                }
            }
            if(label_object_to_classify.equals(nearest_neighbor_label)){
                number_correctly_classified++;
            }
//            System.out.println("Object " + (i+1) + " is class " + (label_object_to_classify+1));
//            System.out.println("Its nearest neighbor is " + (nearest_neighbor_location+1)
//                    + " which is in class " + (nearest_neighbor_label+1));
        }

        Double solution = Double.valueOf(number_correctly_classified) / Double.valueOf(data.size());
        //BEST DEBUG OUTPUT HERE
        System.out.println("k: " + feature_to_add + " current set: " + current_set + " accuracy: " + solution);
        return solution;
//        Random r = new Random();
//        return r.nextDouble();
    }






    ////----------------------------------------------------------------------------------------------------
    ////------------------------------         HELPER FUNCTIONS     ----------------------------------------
    ////----------------------------------------------------------------------------------------------------
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
//        ArrayList<ArrayList<Double>> l = load("CS170_SMALLtestdata__1.txt");
        ArrayList<ArrayList<Double>> l = load("CS170_small_special_testdata__95.txt");
//        ArrayList<ArrayList<Double>> l = load("CS170_small_special_testdata__99.txt");
//        ArrayList<ArrayList<Double>> l = load("smalltest");

        // l.get(0).size() returns col count which is # of features
        //This assumes that all column counts are the same
        // l.size() returns row count which is # of entries

        search(l);

    }
}
