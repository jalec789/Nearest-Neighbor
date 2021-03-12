import java.io.*;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class nearestNeighbor {


    static void feature_search_demo(ArrayList<ArrayList<Float>> data){
        ArrayList<Integer>current_set_of_features = new ArrayList<>();//Init empty set
        //I think this is a single value...
        //ArrayList<Integer>feature_to_add_at_this_level;
        Integer feature_to_add_at_this_level;
        Float best_so_far_accuracy;
        Float accuracy;

        for(int i = 0; i < data.get(0).size(); i++){
            System.out.println("On the " + i + "the level of the search tree");
            //feature_to_add_at_this_level = new ArrayList<>();
            feature_to_add_at_this_level = i;//is this correct???

            best_so_far_accuracy = Float.valueOf(0);
            for (int k = 0; k < data.get(0).size(); k++){
                if(!current_set_of_features.contains(k)){
                    System.out.println("--Considering adding the "+ k + " feature");
                    accuracy = leave_one_out_cross_validation(data,current_set_of_features, k+1);
                    if(accuracy > best_so_far_accuracy){
                        best_so_far_accuracy = accuracy;
                        feature_to_add_at_this_level = k;
                    }
                }
            }
            System.out.println("On level " + i + " i added feature " + feature_to_add_at_this_level + " to current set");
        }
    }

    static Float leave_one_out_cross_validation(ArrayList<ArrayList<Float>> data,
                                                ArrayList<Integer> current_set, //mke sure to add the feature to this set to track
                                                Integer feature_to_add){
        Random r = new Random();
        return r.nextFloat();
    }

    static ArrayList<ArrayList<Float>> load(String filename){
        ArrayList<ArrayList<Float>> table = new ArrayList<>();
        ArrayList<Float> row;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine()) != null){
                row = new ArrayList<Float>();
                try(Scanner scanner = new Scanner(line)){
                    while(scanner.hasNext()){
                        row.add(scanner.nextFloat());
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

    public static void main(String[] args){
        ArrayList<ArrayList<Float>> l = load("smalltest");
        // l.get(0).size() returns col count which is # of features
        //This assumes that all column counts are the same
        // l.size() returns row count which is # of entries
        System.out.println(l.get(0).size());
    }
}
