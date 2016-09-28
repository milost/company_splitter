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
