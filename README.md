# differ-regression-test-suite-assignment
Regression Test Suite for the **differ-for-testers** assignment

## The Setup
- JAVA 8+ jdk must be [installed](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)  
- MAVEN must be [installed](https://maven.apache.org/download.cgi) 
- Web browser must be installed
- **differ-for-tester** service must be running

## Running the Test Suite
1. Get **host** and **port** the **differ-for-tester** service is running on
2. From the project root folder, run `mvn clean test -Dserver.host=<host> -Dserver.port=<port>`

        If host is http://localhost and port is 8081,
        run "mvn clean test -Dserver.host=http://localhost -Dserver.port=8081"


## Viewing Test Report
1. Go to test report location: `<project_root_folder>\test-output`
2. Open `ExtentReportsTestNG.html` in a web browser
