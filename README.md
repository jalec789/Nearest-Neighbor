project2

# Feature Selection using Nearest Neighbor
## Introduction
Hello, I am Jason Chan and this repo will showcase the forward and backward feature selection and verify with nearest neighbor.

## What is it

This program runs forward and backward feature selection using nearest neighbor. The input file should be formatted with all the following objects
```
class_object_1 feature_1 feature_2 ... feature_n
class_object_2 feature_1 feature_2 ... feature_n
...
class_object_m feature_1 feature_2 ... feature_n
```
Each row is an object. The first column will be used to verify the class. The following columns will be the features of that object. The example files in this repo are a good example of what to model after. The example files the first/leftmost column is the classifier, used to verify if the feature selection correctly guessed the class that object belongs to. Then the program will measure the accuracy of that feature/set of features to determine what the best set of features should be used to distingush which class an object belongs to.

## Dependencies
Java, just build with java...

## How to Run
This is a less formal way to build but the quick version is here. The argument is the input_file with the feature and class data
```
java nearestNeighbor <input_file>
```
An example would be
```
cd src
javac nearestNeighbor.java 
java nearestNeighbor ../CS170_SMALLtestdata__49.txt
```
OR a different file
```
cd src
javac nearestNeighbor.java 
java nearestNeighbor ../CS170_largetestdata__79.txt
```
Then just follow the menu items on the screen. You should be given the choice to run forward and backward selection. Once done you have the choice to exit or run the other algorithm as well. An excel output file (with tabs) is also provided in case you would like to graph all feature sets processed.
