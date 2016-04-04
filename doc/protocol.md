# Strategic Game Server - Protocol Documentation
---

### Commands overview
##### Overview of server-messages:

```
OK          Command accepted
ERR         Command denied
SVR [HELP | GAME [MATCH {GAMETYPE, PLAYERTOMOVE, OPPONENT} | YOURTURN | MOVE | CHALLENGE | [WIN | LOSS | DRAW]] | GAMELIST | PLAYERLIST]
            Message from server
    HELP            Message with help information
    GAME            Message concerning the game
    	GAMELIST 	Returns a list of games as ["<gametype>",..]
    	PLAYERLIST 	Returns a list of players as ["<player>",..]
        MATCH           Assigning a match with {GAMETYPE, PLAYERTOMOVE, OPPONENT}
        YOURTURN        Notification of turns during the match
        MOVE            Moving during the match
        CHALLENGE       Message about a challange
            WIN             Receiver won the game
            LOSS            Receiver lost the game
            DRAW            Match ended in a draw
```
##### Overview of client-commands:
```
login           Login as speler
logout | exit | quit | disconnect | bye
		        Logout or disconnect
get <gamelist | playerlist>
                Data retrieval
            gamelist            Requesting the list of supported game modes
	        playerlist          Requesting the list of registered player
            subscribe           Subscribe for a gametype
            unsubscribe         Unsubscribe for a gametype
            move                Do a move in a match
            challenge [accept | forfeit]  
                                Processing a challange
	            accept              Accepting a challange
                forfeit				Forfeit on the current match
            help [command]     Display help
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
->Subscired for <gametype>.  

**Unsubscribe:**  
C: unsubscribe  
S: OK  
->Unsubscribed from the previously subscribed game.  

**Match offered, message to both playerss:**  
S: SVR GAME MATCH {GAMETYPE: "<gametype>", PLAYERTOMOVE: "<name player1>", OPPONENT: "<name opponent>"}  
->Now playing the match, subsciption for a gametype has expired.  

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
S: SVR GAME <player result> {PLAYERONESCORE: "<score player1>", PLAYERTWOSCORE: "<score player2>", COMMENT: "<ccomment on the result>"}  
->The match has ended, <player result> contains the value 'WIN', 'LOSS' or 'DRAW'.  

**Forfeit a macht:**   
C: forfeit  
S: OK  
->The player has given up, the server will send the result of the match to both players.  

**Result of a match that is forfeited by one of the players, message to both players:**  
S: SVR GAME <player result> {PLAYERONESCORE: "<score player1>", PLAYERTWOSCORE: "<score player2>", COMMENT: "Player forfeited match"}  
->The match ended, <player> forfeited.   

**Result of a match, player disconnected:**  
S: SVR GAME <speler result> {PLAYERONESCORE: "<score player1>", PLAYERTWOSCORE: "<score player2>", COMMENT: "Client disconnected"}  
->The match has ended, <player> disconnected.  

**Challanging a player for a game:**  
C: challenge "<player>" "<gametype>"  
S: OK  
->The player is now challanged for a game. Previous challanges will be cancelled.  

**Receiving a challange:**  
S: SVR GAME CHALLENGE {CHALLENGER: <player>, GAMETYPE: <gametype>, CHALLENGENUMBER: <challangenumber>}  
->Now the possibility to accept the challange.  

**Result of a challange that has expired:**  
S: SVR GAME CHALLENGE CANCELLED {CHALLENGENUMBER: "<challange number>"}  
->Challange has expired. Possible causes: player started another challange, player started a match or the player disconnected from the game.  

**Accepting a challange:**  
C: challenge accept <challange number>  
S: OK  
->The challange has been excepted. The match will be started, message will follow.  

**Ask for help:**  
C: help  
S: OK  
->The client asked for information, the server will answer with the information.  

**Ask help for a specific command:**  
C: help <command>  
S: OK  
->The cient asked for information for the <command> command, the server will answer with the information.  

**Help information received:**  
S: SVR HELP <help information>  
->Help information is received, can contain multiple consecutive responses.  
