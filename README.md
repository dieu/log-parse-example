## Run test

`sbt test` to run tests

## Help

`sbt "run help"`

## Local Run

`sbt "run -d src/main/resources/log.txt"` for run with local Spark master

## Remote run

* `vagrant up` for provisioning sandbox with Spark master and worker (may ask password to update `/etc/hosts`)
* `sbt assembly` for make allone jar
* `zip -d target/scala-2.10/SparkExample-assembly-0.1.0.jar META-INF/*.RSA META-INF/*.DSA META-INF/*.SF` for remove broken files from jar
* `spark-submit --class "com.example.spark.StandaloneApp" --master spark://spark1.vagrant:7077 target/scala-2.10/SparkExample-assembly-0.1.0.jar -d src/main/resources/log.txt -m spark://spark1.vagrant:7077` for run example against remote Spark master

if you want to run against other data file, you should share folder of that file with vagrant box by command `DATA_FOLDER="/path/to/data/folder/" vagrant up` or `DATA_FOLDER="/path/to/data/folder/" vagrant reload --provision`.

## Result

`cat /tmp/spark_example/output.json` for results
