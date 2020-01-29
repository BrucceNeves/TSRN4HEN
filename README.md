# A Two-Stage Regularization Framework for Heterogeneous Event Networks

This repository contains supplementary material of the paper "A Two-Stage Regularization Framework for Heterogeneous Event Networks" submitted to the  Pattern Recognition Letters journal (under review).

**Abstract**: *Event analysis is a promising way to classify, monitor and understand different phenomena from news and social networks. Each event consists of different components, which indicate what happened, when, where, and the people and organizations involved. Heterogeneous networks are useful for modeling large event datasets, where we map different types of objects (e.g. events and their components), as well as the different relationships between objects. However, there is a lack of machine learning methods to properly handle event classification from heterogeneous networks. Label propagation-based regularization frameworks are interesting are interesting approaches for classification in networks, where network objects are classified according to the labels of neighboring objects and the network topology --- often used in semi-supervised learning scenarios. In this paper, we present a two-stage regularization framework based on label propagation for event classification in heterogeneous networks. The first stage of our regularization framework aims to learn the importance level of each relationship between events and their components. In the second stage, the regularization process considers the importance levels of each relationship to propagate labels on the network. Thus, the classification process is improved by considering the domain characteristics of the event dataset, such as temporal seasonality and geographical distribution. In both stages, our approach also deals with noise data through parameters that define the confidence level of labeled events during label propagation. Experimental results involving twelve event networks from different domains showed that our proposal outperforms existing regularization frameworks.*

# Resources

- [Source-code](src/) of the proposed regularization framework, as well as the other existing frameworks (GFHF, LLGC, GNetMine, and LPHN)
- [Datasets](datasets/): twelve heterogeneous event networks from different domains (extracted from Reuters Corpus).


# How to run

	> java -Xmx5G -cp TSRN4HEN.jar algoritmos.<method> <options .json>

Example:

	> java -Xmx5G -cp TSRN4HEN.jar algoritmos.TSRF params.json

- Available methods: TSRF, GFHF, LLGC, GNetMine, LPHN

# Configuration file
Configuration file example
```json
{
  "iterations":"1000",
 
  "convergenceThreshold":"0.00005",
 
  "labels": "labels/business_transactions.labeled_events",
 
  "relations": [
    "datasets/business_transactions.edges"
  ],
 
  "output_file": "out.model",

  "mi": "1",

  "weight_relations": {},

  "miBeta": "1"
}
```
For all methods:
- **iterations** Maximum number of iterations, if the algorithm takes time to converge
- **convergenceThreshold** Threshold to consider that the network has converged
- **labels** File containing labels for labeled network nodes. See more details about the format file in section [Labels File](#labels-file)
- **relations** File containing the list of edge. See more details about the format file in section [Edges File](#edges-file)
- **output_file** Output file for the vector F of each node. Note labeled nodes are included in the output file
Method parameters:
- **mi:** Importance of labeled data during the propagation of labels, ranging from 0.1 to 1. Used in **LLGC**, **GNetMine** and **TSRF alpha regularizer**
- **weight_relations:** Weight of the relations between the layers, the name of the layers must be connected by 'underline' and the values will be automatically normalized when running GNetMine, all existing layer relations must be defined. If no pair of layers is specified all pairs of layers will have equal weights. Used only in **GNetMine**
- **miBeta** equal to ** mi **. Used only in **TSRF beta regularizer**

# Edges File
In edges file each line represents an edge in the following format:
```tsv
node1:layer1\tnode2:layer1\tweight
node2:layer1\tnode3:layer2\tweight
node3:layer2\tnode1:layer1\tweight
```
# Labels File
In labels file each line represents a node and its respective label in onehot format, the file format is the following:
```tsv
node1:layer1\tclass1,class2,class3
node2:layer1\tclass1,class2,class3
```