# java-uno
First project for Mindswap academy

![image](https://user-images.githubusercontent.com/64026232/155902157-e8b333dc-c3fa-42be-ab44-2bbbe35314a7.png)

When a client connects to the server, he enters a lobby, where he can chat, writing in the console, with all the other 
players that are in the lobby. He can also create a gaming room or join a previous created room. Inside the room, all 
the players of the room can chat with each other, starting the messages with "-". Otherwise, the server assumes that the 
message is a game command.
When all the players in a room are ready, our version of UNO game starts.

About our version Uno:
Uno is the highly popular card game played by millions around the world. This game is played by matching and then 
discarding the cards in your hand until there are none left.

The objective:
The goal of UNO is to be the first person to play the last card in your hand. The fun of UNO is the requirement to shout 
"UNO!" when you are down to the last card.

Game Setup: 
The game is for 2-10 players, each player starts with seven cards, you must match by number, color or symbol/action. For 
example, if the last card played is a red card that is an 8, you must place a red card or a card with an 8 on it. If it has more 
8 cards, he can play all the 8 cards in the same round.
If the player has no matches or chooses not to play any of his cards, even though he may have a match, he must draw a 
card from the deck.
Play continues until a player has a card left. The moment a player has only one card left, they must shout "UNO!". If 
they don't shout "Uno" before finishing the turn or play the last card, that player must draw two new cards as a penalty.
Once a player has no more cards, the game ends and the player wins and the all player are redirected to the lobby.

Special Cards: 
SKIP - Blocks the next player.
Reverse - If clockwise, change to counterclockwise or vice versa.
+2 - When one person draws this card, the next player must pick up two cards.
+4 - When one person draws this card, the next player must pick up four cards. This card represents all four colors 
and can be placed on any card. The player must indicate which color he or she will represent to the next player.


Steps to start UNO in the IDE:
1 - You should go to Edit Settings.
2 - Select Modify Options.
3 - Add Allow Multiple Instance.
4 - Open and start the ServerLauncher Class.
5 - Open and start Client Class, you can open as many Clients as players that want to play.
6 - And after that, you can have fun with UNO!

When you are in chat you can write "/help" to see the available server commands; You can't use server commands while you
are playing a game.
While you are playing a game, you can write "help" in the console to see the available game commands;

Have fun. :D