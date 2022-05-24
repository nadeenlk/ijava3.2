# Java source code interpreter (using reflection) #unstable

```
ijava: Java source code interpreter

 ijava [-d] <classpath> <mainclass> [args...]
or
 ijava [-d] tests                       - run tests

 -d                                     - enable debug logging
 <classpath>                            - class search path of directories/jar files/java source files
 <main class>                           - main class name

eg:
 ijava tests ip                         - get your ip
 ijava tests hello
 ijava tests\hello.java hello
 ijava tests\hello.class tests.hello
 ijava tests\hello.jar tests.hello
```

dependencies:
  JavaParser https://github.com/javaparser/javaparser (included)

Nadeen Udantha me@nadeen.lk
