build: src
	mvn install

run: build
	java -jar bin/TwoPlayerGameServer.jar

ci: build
