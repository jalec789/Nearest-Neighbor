project2

# Feature Selection using Nearest Neighbor
## Introduction
Hello, I am Jason Chan and this repo will showcase the forward and backward feature selection and verify with nearest neighbor.

## What is it

This program runs forward and backward feature selection using nearest neighbor. The file should be formatted with all the following objects
```
class_object_1 feature_1 feature_2 ... feature_n
class_object_2 feature_1 feature_2 ... feature_n
...
class_object_m feature_1 feature_2 ... feature_n
```
Each row is an object. The first column will be used to verify the class. The following columns will be the features of that object.

## Dependencies
Java, just build with java...

## How to Run
This is a less formal way to build but the quick version is here. The argument is the file with the feature and class data
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
Then just follow the menu items on the screen.
