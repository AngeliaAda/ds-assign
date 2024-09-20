Weather Aggregation System

cause they depend on each other, they need to compile together.
for example in local repository, use"javac -cp "lib/json-20240303.jar" -d bin src/main/java/LamportClock.java src/main/java/AggregationServer.java"              
then use "java -cp "bin;lib/json-20240303.jar" AggregationServer 4567"
the test file in the root directory named weather_data.txt which data depends on the introduction in myuni.
Overview
This project implements a distributed weather data aggregation system using Sockets and Lamport clocks for synchronization. The system consists of three main components:

Aggregation Server: Collects weather data from multiple Content Servers and provides it to GET Clients upon request.
Content Servers: Periodically upload weather data in JSON format to the Aggregation Server.
GET Clients: Retrieve weather data from the Aggregation Server and display it to the user.
The system uses Lamport clocks to maintain event ordering and causal relationships between distributed entities.

Features

Aggregation Server:
Accepts weather data from multiple content servers via PUT requests.
Serves weather data to clients via GET requests.
Uses Lamport clocks to ensure causality in event ordering.
Expire outdated data after 30 seconds of no communication from a content server.
Recovers from crashes by restoring the last known state from a file.

Content Server:
Reads weather data from a local file, converts it to JSON format, and uploads it to the Aggregation Server periodically.
Maintains a Lamport clock for synchronization.
Verifies acknowledgments from the Aggregation Server and updates its clock accordingly.

GET Client:
Requests weather data from the Aggregation Server.(acturally all running in local repository whatever the server and the clients)
Optionally requests data for a specific station (via station ID).
Displays the weather data in a human-readable format.
Maintains a Lamport clock to synchronize with the server.

Architecture
The system follows a simple distributed architecture using Sockets for communication. Each entity (Aggregation Server, Content Servers, and GET Clients) operates on its own and interacts with others via sockets.

Aggregation Server:
Listens for incoming connections on a specified port.
Handles multiple concurrent connections using multi-threading.
Ensures consistent data retrieval and storage with event ordering guaranteed by Lamport clocks.

Content Servers:
Run continuously, periodically uploading weather data to the Aggregation Server.
Each content server operates independently and updates its local clock after each interaction with the server.(this part haven't been test)

GET Clients:
Run either continuously (polling for updates) or in an interactive mode (allowing the user to request data).

Components
The system consists of the following main Java classes:

AggregationServer.java: Manages data storage, processes GET/PUT requests, and ensures time synchronization using Lamport clocks.
WeatherContentServer.java: Reads weather data from a local file and periodically sends it to the Aggregation Server.
GETClient.java: Retrieves weather data from the Aggregation Server and displays it.
Running the System
Prerequisites
Java: Ensure you have Java installed on your system.
json library: The project uses the org.json library for JSON parsing. Ensure the json-20240303.jar is available in the lib/ directory.