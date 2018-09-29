

This repository contains the code to the paper [Dissecting Company Names using Sequence Labeling (LWDA), 2018](https://www.semanticscholar.org/paper/Dissecting-Company-Names-using-Sequence-Labeling-Loster-Hegner/88e21023dab6c6f312153912b0ddef56faa953d8)

# Abstract
Understanding the inherent structure of company names by identifying their constituent parts yields valuable insights that can be leveraged by other tasks, such as named entity recognition, data cleansing, or deduplication. Unfortunately, segmenting company names poses a hard problem due to their high structural heterogeneity. Besides obvious elements, such as the core name or legal form, company names often contain additional elements, such as personal and location names, abbreviations, and other unexpected elements. While others have addressed the segmentation of person names, we are the first to address the segmentation of the more complex company names. We present a solution to the problem of automatically labeling the constituent name parts and their semantic role within German company names. To this end we propose and evaluate a collection of novel features used with a conditional random field classifier. In identifying the constituent parts of company names we achieve an accuracy of 84%, while classifying the colloquial names resulted in an F1 measure of 88%.

# Examples
Move jar in same folder as models:

```Batchfile
java -jar companies.jar Friedrich Schiller GmbH
```

How to call a classifier:

```Java
AClassifier<Tag> classifier = In.file("StanfordCRFClassifier-Tag.bin").readObject();
List<Pair<Token, Tag>> results = classifier.getTags(name);
```
