# A Two-Stage Regularization Framework for Heterogeneous Event Networks

This repository contains supplementary material of the paper "A Two-Stage Regularization Framework for Heterogeneous Event Networks" published in Pattern Recognition Letters journal (https://doi.org/10.1016/j.patrec.2020.08.019).

**Abstract**: *Event analysis from news and social networks is a promising way to understand complex social phenomena. Each event consists of different components, which indicate what happened, when, where, and the people and organizations involved. Heterogeneous networks are useful for modeling large event datasets, where we map different types of objects (e.g. events and their components), as well as the different relationships between objects. Such networks enable the identification of related events, in which users label some events in categories and then use the network’s topological structure to find other events of interest. Although this process can be automated, there is a lack of machine learning methods to properly handle event classification from heterogeneous networks. In this paper, we present the framework named Heterogeneous Event Network Regularization in Two-stages (ENR<sup>2</sup>). The first stage of HENR<sup>2</sup> aims to learn the importance level of each relationship between events and their components. In the second stage, the regularization process considers the importance levels of each relationship to propagate labels on the network. Thus, the classification process is improved by considering the domain characteristics of the event dataset, such as temporal seasonality and geographical distribution. In both stages, our approach also deals with noisy data through parameters that define the confidence level of labeled events during label propagation. Experimental results involving twelve event networks from different application domains show that our proposal outperforms existing regularization frameworks.*

# Resources

- [Source-code](src/) of the proposed regularization framework, as well as the other existing frameworks (GFHF, LLGC, GNetMine, and LPHN)
- [Datasets](datasets/): twelve heterogeneous event networks from different domains (extracted from Reuters Corpus).


# How to run

	> java -Xmx5G -cp TSRN4HEN.jar algorithms.<method> <options .json>

Example:

	> java -Xmx5G -cp TSRN4HEN.jar algorithms.HENR2 params.json

# Available methods
  * HENR2
  * GFHF
  * LLGC
  * GNetMine
  * LPHN

# Configuration file
Configuration file example
```json
{
  "iterations":"1000",

  "convergenceThreshold":"0.00005",

  "labels": "labels/business_transactions.50_labeled_events",

  "relations": [
    "datasets/business_transactions.edges"
  ],

  "output_file": "out.model",

  "mi": "1",

  "weight_relations": {},

  "miBeta": "1",

  "target_layer": "event"
}
```
For all methods:
- **iterations:** Maximum number of iterations, if the algorithm takes time to converge
- **convergenceThreshold:** Threshold to consider that the network has converged
- **labels:** File containing labels for labeled network nodes. See more details about the format file in section [Labels File](#labels-file)
- **relations:** File containing the list of edge. See more details about the format file in section [Edges File](#edges-file)
- **output_file:** Output file for the vector F of each node. Note labeled nodes are included in the output file
Method parameters:
- **mi:** Importance of labeled data during the propagation of labels, ranging from 0.1 to 1. Used in **LLGC**, **GNetMine** and **TSRF alpha regularizer**
- **weight_relations:** Weight of the relations between the layers, the name of the layers must be connected by 'underline' and the values will be automatically normalized when running GNetMine, all existing layer relations must be defined. If no pair of layers is specified all pairs of layers will have equal weights. Used only in **GNetMine**
- **miBeta:** Equal to **mi**. Used only in **TSRF beta regularizer**
- **target_layer:** What is the target layer of the classification. Used only in **TSRF**

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

# Citation
Please cite the following paper when using this code:
```
@article{dos2020two,
  title={A two-stage regularization framework for heterogeneous event networks},
  author={dos Santos, Brucce Neves and Rossi, Rafael Geraldeli and Rezende, Solange Oliveira and Marcacini, Ricardo Marcondes},
  journal={Pattern Recognition Letters},
  volume={138},
  pages={490--496},
  year={2020},
  publisher={Elsevier}
}
```