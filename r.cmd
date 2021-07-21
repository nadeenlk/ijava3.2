@javac -cp javaparser.jar *.java
@java -cp .;javaparser.jar Main
@del *.class