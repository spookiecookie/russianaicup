rd /Q /S classes
md classes

javac -encoding UTF-8 -sourcepath "src/main/java" -d classes "src/main/java/Runner.java" > compilation.log

jar cvfe "./codetroopers.jar" Runner -C "./classes" .
