import javax.swing.plaf.IconUIResource;
import java.io.*;
import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class nearestNeighbor {


    static void backward_search(ArrayList<ArrayList<Double>> data){
        ArrayList<Integer>current_set_of_features = new ArrayList<>();
        //init all features to the set
        for (int i = 1; i < data.get(0).size(); i++) {
            current_set_of_features.add(i);
        }

        Integer feature_to_remove_at_this_level = null;
        Double best_so_far_accuracy;
        Double accuracy;

        System.out.println("Beginning Search.");
        Double overall_best_accuracy = 0.0;
        ArrayList<Integer> overall_best_set = null;

        //go through for each feature/column remove one
        for(int i = 1; i < data.get(0).size()-1; i++){
//            System.out.println("On the " + (i+1) + "th level of the search tree");

            best_so_far_accuracy = Double.valueOf(0);
            for (int k = 1; k < data.get(0).size(); k++){//start at zero for full current set
                if(current_set_of_features.contains(k)){
//                    System.out.println("--Considering removing the "+ k + " feature");
                    accuracy = backward_leave_one_out_cross_validation(data, current_set_of_features, k);
                    //NOTE: that this function removes k from current_set. we should add it when done printing output
                    System.out.println("\tUsing feature(s) " + current_set_of_features + " accuracy is " + String.format("%,.1f", (accuracy*100)) + "%");
                    current_set_of_features.add(k);
                    Collections.sort(current_set_of_features);//I don't think we have to do this but it sorts the list
                    if(accuracy > best_so_far_accuracy){
                        best_so_far_accuracy = accuracy;
                        feature_to_remove_at_this_level = k;
                    }
                }
            }
            //because feature_to_remove_at_this_level is an Integer object and not an int
            //arraylist remove Object searches for that Integer
            current_set_of_features.remove(feature_to_remove_at_this_level);

            System.out.println("Feature set " + current_set_of_features + " was best, accuracy is "
                    + String.format("%,.1f", (best_so_far_accuracy*100)) + "%");
            if(best_so_far_accuracy > overall_best_accuracy){
                overall_best_accuracy = best_so_far_accuracy;
                overall_best_set = new ArrayList<>(current_set_of_features);
            }
        }

        System.out.println("Finished search!! The best feature subset is " + overall_best_set
                + ", which has an accuracy of " + String.format("%,.1f", (overall_best_accuracy*100)) + "%");
    }

    static Double backward_leave_one_out_cross_validation(ArrayList<ArrayList<Double>> data,
                                                         ArrayList<Integer> current_set, //make sure to add the feature to this set to track
                                                         Integer feature_to_remove){
        //Copy Here so we don't alter Arraylist data
        ArrayList<ArrayList<Double>> local_copy = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < data.size(); i++) {
            local_copy.add(new ArrayList<Double>(data.get(i)));
        }

        //zero all columns we are not using but, skip over the first column
        current_set.remove(current_set.indexOf(feature_to_remove));
        for (int i = 1; i < local_copy.get(i).size(); i++) {
            if(!current_set.contains(i)){
                zero_a_feature(local_copy,i);
            }
        }

//        print(local_copy);
//        System.out.println(local_copy.get(0));



        int number_correctly_classified = 0;
        ArrayList<Double> object_to_classify;
        Double label_object_to_classify;

        Double nearest_neighbor_distance;
        Integer nearest_neighbor_location;
        Double distance;

        Double nearest_neighbor_label = null;

        //loop over number of objects/rows
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

        Double accuracy = Double.valueOf(number_correctly_classified) / Double.valueOf(data.size());
        //BEST DEBUG OUTPUT HERE
//        System.out.println("k: " + feature_to_add + " current set: " + current_set + " accuracy: " + accuracy);
//        System.out.println("\tUsing feature(s) " + current_set + " accuracy is " + String.format("%,.1f", (accuracy*100)) + "%");
//        current_set.remove(current_set.size()-1);
        return accuracy;
//        Random r = new Random();
//        return r.nextDouble();
    }









    static void forward_search(ArrayList<ArrayList<Double>> data){
        ArrayList<Integer>current_set_of_features = new ArrayList<>();//Init empty set
        //I think this is a single value...
        Integer feature_to_add_at_this_level = null;
        Double best_so_far_accuracy;
        Double accuracy;

        System.out.println("Beginning Search.");
        Double overall_best_accuracy = 0.0;
        ArrayList<Integer> overall_best_set = null;

        //go through for each feature/column
        for(int i = 0; i < data.get(0).size() - 1; i++){
//            System.out.println("On the " + (i+1) + "th level of the search tree");

            best_so_far_accuracy = Double.valueOf(0);
            for (int k = 1; k < data.get(0).size(); k++){
                if(!current_set_of_features.contains(k)){
//                    System.out.println("--Considering adding the "+ k + " feature");
                    accuracy = forward_leave_one_out_cross_validation(data, current_set_of_features, k);
                    //NOTE that this function adds k to current_set. we should remove it when done printing output
                    System.out.println("\tUsing feature(s) " + current_set_of_features + " accuracy is " + String.format("%,.1f", (accuracy*100)) + "%");
                    current_set_of_features.remove(current_set_of_features.indexOf(k));
                    if(accuracy > best_so_far_accuracy){
                        best_so_far_accuracy = accuracy;
                        feature_to_add_at_this_level = k;
                    }
                }
            }
            current_set_of_features.add(feature_to_add_at_this_level);
//            System.out.println("On level " + (i+1) + " i, added feature "
//                    + (feature_to_add_at_this_level+1) + " to current set\n");

            System.out.println("Feature set " + current_set_of_features + " was best, accuracy is "
                    + String.format("%,.1f", (best_so_far_accuracy*100)) + "%");
            if(best_so_far_accuracy > overall_best_accuracy){
                overall_best_accuracy = best_so_far_accuracy;
                overall_best_set = new ArrayList<>(current_set_of_features);
            }
        }
        System.out.println("Finished search!! The best feature subset is " + overall_best_set
                + ", which has an accuracy of " + String.format("%,.1f", (overall_best_accuracy*100)) + "%");
    }

    //This is nearest neighbor?
    static Double forward_leave_one_out_cross_validation(ArrayList<ArrayList<Double>> data,
                                                ArrayList<Integer> current_set, //make sure to add the feature to this set to track
                                                Integer feature_to_add){

        //Copy Here so we don't alter Arraylist data
        ArrayList<ArrayList<Double>> local_copy = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < data.size(); i++) {
             local_copy.add(new ArrayList<Double>(data.get(i)));
        }

        current_set.add(feature_to_add);
        //zero all columns we are not using but, skip over the first column
        for (int i = 1; i < local_copy.get(i).size(); i++) {
            if( !current_set.contains(i)){
                zero_a_feature(local_copy,i);
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

        Double accuracy = Double.valueOf(number_correctly_classified) / Double.valueOf(data.size());
        //BEST DEBUG OUTPUT HERE
//        System.out.println("k: " + feature_to_add + " current set: " + current_set + " accuracy: " + accuracy);
//        System.out.println("\tUsing feature(s) " + current_set + " accuracy is " + String.format("%,.1f", (accuracy*100)) + "%");
        return accuracy;
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



    // table.get(0).size() returns col count which is # of features
    //This assumes that all column counts are the same
    // table.size() returns row count which is # of entries

    public static void main(String[] args){
        ArrayList<ArrayList<Double>> table = load("CS170_SMALLtestdata__1.txt");
//        ArrayList<ArrayList<Double>> table = load("CS170_small_special_testdata__95.txt");
//        ArrayList<ArrayList<Double>> table = load("CS170_small_special_testdata__99.txt");
//        ArrayList<ArrayList<Double>> table = load("smalltest");
//        ArrayList<ArrayList<Double>> table = load("CS170_largetestdata__49.txt");



        ArrayList<Integer> all_features = new ArrayList<>();
        for (int i = 1; i < table.get(0).size(); i++) {
            all_features.add(i);
        }
        Double full_set_percentage = forward_leave_one_out_cross_validation(table,all_features,null);

//        System.out.println("This dataset has " + (table.get(0).size()-1) + " features (not including the class attribute)" +
//                " with " + table.size() + " instances.\nRunning nearest neighbor with all " + (table.get(0).size()-1) +
//                " features, using \"leaving-one-out\" evaluation, I get an accuracy of "
//                + String.format("%,.1f", (full_set_percentage*100)) + "%");

//        forward_search(table);

        backward_search(table);

    }
}
