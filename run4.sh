#!/bin/zsh

# Create a folder to store log files in
LOG_FOLDER=log
mkdir -p $LOG_FOLDER
# Create a folder to store binary class files in
BIN_FOLDER=bin
mkdir -p $BIN_FOLDER

# Compile all files
javac -d $BIN_FOLDER src/Server/*.java src/common/*.java src/client/*.java

# Run the server, send output to log file
java -classpath $BIN_FOLDER Server.Server > $LOG_FOLDER/server.log 2>&1 &
SERVER_PID=$!
echo "Server started. PID: $SERVER_PID"

# Run the clients, send output to log file
java -classpath $BIN_FOLDER client.Client > $LOG_FOLDER/client1.log 2>&1 &
CLIENT1_PID=$!
echo "Client 1 started. PID: $CLIENT1_PID"
java -classpath $BIN_FOLDER client.Client > $LOG_FOLDER/client2.log 2>&1 &
CLIENT2_PID=$!
echo "Client 2 started. PID: $CLIENT2_PID"

java -classpath $BIN_FOLDER client.Client > $LOG_FOLDER/client3.log 2>&1 &
CLIENT3_PID=$!
echo "Client 3 started. PID: $CLIENT3_PID"
java -classpath $BIN_FOLDER client.Client > $LOG_FOLDER/client4.log 2>&1 &
CLIENT4_PID=$!
echo "Client 4 started. PID: $CLIENT4_PID"

# Kill all command prompt
while true; do
  # Prompt for a single letter for yes/no
  if read -q '?Kill all processes (y/n)? '; then
    # Kill processes
    echo ""
    kill $SERVER_PID $CLIENT1_PID $CLIENT2_PID $CLIENT3_PID $CLIENT4_PID
    exit 0
  else
    echo ""
  fi
done