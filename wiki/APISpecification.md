# API specification

## Connection

The client should connect to the server via websocket at:

`ws://localhost:9000/ws`

The port can be changed by running the server with the -Dhttp.port option, for example to use port 9001, in the sbt console type:

`run -Dhttp.port=9001`

## Structure of the messages

For communication between the server and the client serialized versions of a single case class are used:

`case class EngineMessage(`
  `action: String = "",`
  `name: String = "",`
  `command: String = "",`
  `buffer: String = "",`
  `available: List[String] = List[String]()`
`)`

`action` is the main action that should be performed, this defines what other fields should be filled in

`name` is the unique name of the engine

`command` is the UCI command to be issued to the engine

`buffer` is one line of the engine analysis output

`available` is a list of available engines in the form of unique engine names

## Actions

Possible values of action:

`sendavailable` instructs the server to send a list of available engines

`start` instructs the server to start a given engine `name` ( shut down any previous process and start a fresh one ), at any time always exactly one engine can run

`issue` instructs the server to issue the `command` to the engine, a new line will be auto appended

`thinkingoutput` instructs the client to process the line of analysis stored in `buffer`

`available` informs the client about available engines in `available`

## Serialization

For serialization [uPickle](http://www.lihaoyi.com/upickle-pprint/upickle/) was used, but in the end what is generated is just standard JSON.

## Session example

A typical session would look like this:

`request : {"action":"sendavailable"} // requests available engines`

`response: {"action":"available","available":["Stockfish","Atomkraft"]} // gets available engines`

`request : {"action":"start","name":"Stockfish"} // starts the engine process of Stockfish`

`request : {"action":"issue","command":"go infinite"} // starts engine analysis`

`response : {"action":"thinkingoutput","buffer":"info depth 1 seldepth 1 multipv 1 score cp 104 nodes 20 nps 10000 tbhits 0 time 2 pv e2e3\r"} // a line of analysis sent by the engine`