# simple_chat_room
A simple chat room built with java

## How to start

```sh
$jar_path='./jar/ezmorph-1.0.3.jar;./jar/commons-logging.jar;./jar/commons-lang-2.5.jar;./jar/json-lib-2.4-jdk15.jar;./jar/commons-beanutils-1.7.0.jar;./jar/commons-collections-3.1.jar;./jar/mysql-connector-java-5.1.39-bin.jar'

# compile
javac -encoding utf8 -cp "./src;$jar_path" -d out ./src/User.java ./src/UserDB.java ./src/Message.java ./src/MessageDB.java  ./src/ProcessSQL.java ./src/API.java ./src/Server.java ./src/Client.java ./src/LoginPanel.java

# start server
java -cp "./out;$jar_path" Server

# start client
java -cp "./out;./src;$jar_path" Client
```