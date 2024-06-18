rm ~/.local/share/ELIS/0.1.0/*
mvn clean package
java -jar target/recycle-1.0-SNAPSHOT.jar
