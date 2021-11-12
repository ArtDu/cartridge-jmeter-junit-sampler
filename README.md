# Cartridge JMeter JUnit sampler


Sampler for load testing of the tarantool java driver - [cartridge-java](https://github.com/tarantool/cartridge-java).  
To use this sampler, you need to have [jmeter](https://jmeter.apache.org/download_jmeter.cgi) installed.  

## Quickstart

1. Copy all jars dependencies
    ```shell
    cp ~/.m2/repository/io/tarantool/cartridge-driver/0.6.0/cartridge-driver-0.6.0.jar $JMETER_HOME/lib
    cp ~/.m2/repository/io/netty/netty-all/4.1.50.Final/netty-all-4.1.50.Final.jar $JMETER_HOME/lib
    cp ~/.m2/repository/org/msgpack/msgpack-core/0.9.0/msgpack-core-0.9.0.jar $JMETER_HOME/lib
    ```

2. Compile sampler and copy to jmeter junit directory
    ```shell
    mvn test
    mvn jar:test-jar
    cp target/tarantool-jmeter-junit-sampler-1.0-SNAPSHOT-tests.jar $JMETER_HOME/lib/junit
    ```

3. Run jmeter and open one of config from [jmx](jmx)