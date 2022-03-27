CRYPTO.COM CANDLESTICK API TESTING
-

**Requirement**
1) Java 11
2) Maven 3.8.1


**How to Run Test**

1) Download from below repository
   ```
   https://github.com/techGen/candlestick-testing
   ```
2) Go to the root project folder

3) Run below commands
```
mvn -Dmaven.test.failure.ignore=true clean test site
```

**How to check test report**

After you executed the above maven commands then test report will be generated under the root project inside **target** folder
```
<your-local-path>/<root-project>/target/site/index.html
```
Logs file will be added on below location
```
<your-local-path>/<root-project>/target/logs/testing.log
```
