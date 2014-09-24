if not exist src\main\java\Runner.java (
    echo Unable to find src\main\java\Runner.java > compilation.log
)

if not exist src\main\java\MyStrategy.java (
    echo Unable to find src\main\java\MyStrategy.java > compilation.log
)

rd /Q /S classes
md classes

javac -encoding UTF-8 -sourcepath "src/main/java" -d classes "src/main/java/Runner.java" > compilation.log

if not exist classes\Runner.class (
    echo Unable to find classes\Runner.class >> compilation.log
)

if not exist classes\MyStrategy.class (
    echo Unable to find classes\MyStrategy.class >> compilation.log
)

jar cvfe "./java-cgdk.jar" Runner -C "./classes" .
