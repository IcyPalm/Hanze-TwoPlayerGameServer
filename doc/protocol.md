# Strategic Game Server - Protocol Documentation
---

### Commands overview
##### Overview of server-messages:

```
OK          Command accepted
ERR         Command denied
SVR [HELP | GAME [MATCH {GAMETYPE, PLAYERTOMOVE, OPPONENT} | YOURTURN | MOVE | CHALLENGE | [WIN | LOSS | DRAW]] | MESSAGE | GAMELIST | PLAYERLIST]

	Message from server
    HELP            Message with help information
    MESSAGE			Receive a message from another client
    GAME            Message concerning the game
    GAMELIST 		Returns a list of games as ["<gametype>",..]
    PLAYERLIST 		Returns a list of players as ["<player>",..]
    MATCH           Assigning a match with {GAMETYPE, PLAYERTOMOVE, OPPONENT}
    YOURTURN        Notification of turns during the match
    MOVE            Moving during the match
    CHALLENGE       Message about a challenge
    WIN             Receiver won the game
    LOSS            Receiver lost the game
    DRAW            Match ended in a draw
```
##### Overview of client-commands:
```
login           					Login as a player
logout | exit | quit | disconnect | bye
		        					Logout or disconnect
message | msg						Send a message to another client
get <gamelist | playerlist>			Data retrieval
	gamelist            			Requesting the list of supported 							game modes
	playerlist          			Requesting the list of registered players
subscribe							Subscribe for a gametype
unsubscribe         				Unsubscribe for a gametype
move                				Do a move in a match
challenge [accept | forfeit]		Processing a challenge
	accept              			Accepting a challenge
	forfeit							Forfeit on the current match
help [command]						Display help
```

### Commands in detail

C = Client  
S = Server

###### Notes for server responses:
Items between brackets ('[' and ']') represent a list.
Items between braces ('{' and '}') represent a map. As with all maps, the order has not been determined.

###### Notes for client commands:
The commands and arguments are not case sensitive. With exception from the names of players and gametypes

**Not supported command:**  
C: <not supported command>  
S: ERR <reason>  
->no action.  

**Login:**  
C: login <player>  
S: OK  
->Now logged in with <player>.  

**Logout/disconnect:**  
C: logout | exit | quit | disconnect | bye  
S: -  
->disconnected.  

**Retreiving the list of supported games:**  
C: get gamelist  
S: OK  
S: SVR GAMELIST ["<gametype>", ...]  
->List with games received.  

**Retreiving the list of connected players:**  
C: get playerlist  
S: OK  
S: SVR PLAYERLIST ["<player>", ...]  
->List with all connected players received.  

**Subscribing to a gametype:**  
C: subscribe <gametype>  
S: OK  
->Subscribed for <gametype>.  

**Unsubscribe:**  
C: unsubscribe  
S: OK  
->Unsubscribed from the previously subscribed game.  

**Message:**  
C: message | msg "name" <the message>  
S: OK  
-> Message has been send to the client

**Match offered, message to both players:**  
S: SVR GAME MATCH {GAMETYPE: "<gametype>", PLAYERTOMOVE: "<name player1>", OPPONENT: "<name opponent>"}  
->Now playing the match, subscription for a gametype has expired.  

**Getting the turn in a match:**  
S: SVR GAME YOURTURN {TURNMESSAGE: "<message for this turn>"}  
->Now the possibility to do a turn.  

**Making a move after you get the possibility to do a turn:**  
C: move <move>  
S: OK  
->The move is accepted by the server, result for the game will follow.  

**Result from move received, message to both players:**  
S: SVR GAME MOVE {PLAYER: "<player>", DETAILS: "<reaction on move>", MOVE: "<move>"}  
->A move has been done, this message indicates who did the turn, what the turn is and wat the reaction from the game is.  

**Result from receiving a match, message to both players:**  
S: SVR GAME <player result> {PLAYERONESCORE: "<score player1>", PLAYERTWOSCORE: "<score player2>", COMMENT: "<comment on the result>"}  
->The match has ended, <player result> contains the value 'WIN', 'LOSS' or 'DRAW'.  

**Forfeit a match:**   
C: forfeit  
S: OK  
->The player has given up, the server will send the result of the match to both players.  

**Result of a match that is forfeited by one of the players, message to both players:**  
S: SVR GAME <player result> {PLAYERONESCORE: "<score player1>", PLAYERTWOSCORE: "<score player2>", COMMENT: "Player forfeited match"}  
->The match ended, <player> forfeited.   

**Result of a match, player disconnected:**  
S: SVR GAME <speler result> {PLAYERONESCORE: "<score player1>", PLAYERTWOSCORE: "<score player2>", COMMENT: "Client disconnected"}  
->The match has ended, <player> disconnected.  

**Challenging player for a game with default's server turntime:**
C: challenge "<player>" "<gametype>"  
S: OK  
->The player is now challenged for a game. Previous challenges will be cancelled.  

**Challenging a player for a game with custom turntime:**
C: challenge "<player>" "<gametype>" n  
S: OK  
-> Whereas 'n' can be written as an Integer, without quotationmarks.
->The player is now challanged for a game. Previous challanges will be cancelled.

**Receiving a challange:**  
S: SVR GAME CHALLENGE {CHALLENGER: <player>, GAMETYPE: <gametype>, CHALLENGENUMBER: <challangenumber>, TURNTIME: <turntime>}  
->Now the possibility to accept the challenge.

**Result of a challenge that has expired:**  
S: SVR GAME CHALLENGE CANCELLED {CHALLENGENUMBER: "<challenge number>"}  
->Challenge has expired. Possible causes: player started another challenge, player started a match or the player disconnected from the game.  

**Accepting a challenge:**  
C: challenge accept <challenge number>  
S: OK  
->The challenge has been accepted. The match will be started, message will follow.  

**Ask for help:**  
C: help  
S: OK  
->The client asked for information, the server will answer with the information.  

**Ask help for a specific command:**  
C: help <command>  
S: OK  
->The client asked for information for the <command> command, the server will answer with the information.  

**Help information received:**  
S: SVR HELP <help information>  
->Help information is received, can contain multiple consecutive responses.  
