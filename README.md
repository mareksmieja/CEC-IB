# Semi-supervised cross-entropy clustering with information bottleneck constraint - demo application

This package implements a method described in the paper:

Marek Śmieja, Bernhard C. Geiger: Semi-supervised cross-entropy clustering with information bottleneck constraint, Information Sciences, 421, pp. 245-271, 2017

The code is written in Java with use of Idea Intellij IDE.

To run CEC-IB you need to provide properties file (exemplary file is placed in CEC-IB/run.properties). There are several input parameters to fill in:
-fileInData is a path to vectorized data set
-fileInCategories is a path to partial labeling (first line contains the number of data; label 0 means that the label is unknown; known labels begin from 1)
-fileOutMembership is a path to file with the results (clusters membership)
-clustersNumber is the maximal number of cluster (the algorithm is able to reduce unnecessary clusters)
-initTimes is the number of restarts (the algorithm is non-deterministic and has to be restarted multiple times to find good local minimum)
-beta is a trade-off parameter (default value is 1; it ranges from 0 to 1)
-eps is a threshold used to determine cluster reduction (is the fraction of data that belongs to a given cluster falls below eps then this cluster is reduced)

To run CEC-IB with "run.properties" file, open the console in "CEC-IB-demo" directory and type: 

java -jar "out/artifacts/CEC_IB_demo_jar/CEC-IB-demo.jar" run.properties

The clustering result will be placed in "fileOutMembership" file provided in your properties.

Directory "data" contains 5 examples from UCI repository to test this implementation.
