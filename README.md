# A Two-Stage Regularization Framework for Heterogeneous Event Networks

This repository contains supplementary material of the paper "A Two-Stage Regularization Framework for Heterogeneous Event Networks" submitted to the  Pattern Recognition Letters journal (under review).

**Abstract**: *Event analysis from news and social networks is a promising way to understand complex social phenomena. Each event consists of different components, which indicate what happened, when, where, and the people and organizations involved. Heterogeneous networks are useful for modeling large event datasets, where we map different types of objects (e.g. events and their components), as well as the different relationships between objects. However, there is a lack of machine learning methods to properly handle event classification from heterogeneous networks. In this paper, we present a two-stage regularization framework for network-based event classification using label propagation. The first stage of our regularization framework aims to learn the importance level of each relationship between events and their components. In the second stage, the regularization process considers the importance levels of each relationship to propagate labels on the network. Thus, the classification process is improved by considering the domain characteristics of the event dataset, such as temporal seasonality and geographical distribution. In both stages, our approach also deals with noisy data through parameters that define the confidence level of labeled events during label propagation. Experimental results involving twelve event networks from different application domains show that our proposal outperforms existing regularization frameworks.*

# Resources

- [Source-code](src/) of the proposed regularization framework, as well as the other existing frameworks (GFHF, LLGC, GNetMine, and LPHN)
- [Datasets](datasets/): twelve heterogeneous event networks from different domains (extracted from Reuters Corpus).


# How to run

	> java -Xmx4G -cp dist/regframeworks.jar reg <method> <dataset> <options>

Example:

	> java -Xmx4G -cp dist/regframeworks.jar reg TSRF inflation.network

- Available methods: TSRF, GFHF, LLGC, GNetMine, LPHN
- Remove \<options> to use default parameters. 
- See the Readme for each regularization framework for more details on the parameters.

