CRYPTO.COM CANDLESTICK API TESTING
-

**Requirement**
1) Java 11
2) Maven 3.8.1


**How to Run Test**

1) Download or clone from below repository
   ```
   git@github.com:techGen/candlestick-testing.git
   ```
2) Go to the root project folder

3) Run below commands
```
mvn clean install 
mvn -Dmaven.test.failure.ignore=true clean test site
```

**How to check test report**

After you executed the above maven commands then test report will be generated under the root project inside **target** folder
```
<your-local-path>/candlestick-verification/target/site/index.html
```
Logs file will be added on below location
```
<your-local-path>/candlestick-verification/target/logs/testing.log
```
