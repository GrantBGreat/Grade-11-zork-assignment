# Grade-11-zork-assignment
Assignment for grade 11 comp-sci (ICS3U-AP) ISP written in java.

# The guidlines:

**The Game** (origonal Zork ~ https://classicreload.com/zork-i.html)

Your task is to design and implement a text adventure game. You have been given a simple framework/Java Project (Zork1 shell) that lets you walk through a couple of rooms and contains some limited commands. You must use this as a starting point. The original code uses a file to store the rooms and its descriptions. It also contains several classes that you will expand on in addition to creating new classes.

**Read the Code**
Reading code is an important skill that you need to practice. You first task is to read some of the existing code and try to understand what it does. By the end of the assignment, you will need to understand most of it. 

**Getting used to the Project by Making some small extensions** 
As a little exercise to get warmed up, make some changes to the code. For example: 
•	change the name of a location to something different. 
•	change the exits or pick a room that currently is to the west of another room and put it to the north 
•	add a simple command like jump
• 	add a room (or two, or three…). These and similar exercises should get you familiar with the game. 


**Design Your Game** 
First, you should decide what the goal of your game is. It could be something along the lines of: You have to find some items and take them to a certain room (or a certain person?). Then you can get another item. If you take that to another room, you win. The game design is completely up to you and your group.
Once you have chosen a story and a goal you will need to start thinking about some of the essential things you will need in your game:
•	What rooms will you need?
•	What items would you like to incorporate into your game?
•	How will you implement items?
•	Do you need doors that can be closed or locked?
•	Are there any other characters in your game other than you?
•	What can your character do (you will need to handle these commands)?
•	What will make items special – do they have weight, special purpose?

The assignment can be as much or as little as you would like it to be. So you will need to make decisions on what you will want your game to accomplish.

The code that you are given contains the room information within a file. You will want to create a visual map of the rooms so that it is easier for you to create your text file and for debugging purposes.

**Implement the Game** 
Before you create your classes you will need to create a class diagram to depict what behavior your classes will have and what relationships you will be between your classes. Before you begin any development you must have your design approved by me.

**Mandatory Functionality**
The base functionality that you have to implement is: 
•	The game must have a minimum of 20 rooms. 
•	The player can walk through the locations. (This was already implemented in the code you were given.) 
•	There are items in some rooms. Every room can hold any number of items. Some items can be picked up by the player, others cannot. 
•	The player can carry some items with them. Every item must have a weight. The player can carry items only up to a certain total weight before having to choose which items they would like to keep in their inventory.
•	The player can win. There has to be some situation that is recognised as the end of the game where the player is informed that he/she has won. 
•	Implement new commands. The game must have a minimum of 12 command words (ie. verbs like take, eat, run, scream) 


**Submission and Assessment** 
All code must be professionally written (using industry standards) and will be marked for 
•	correctness 
•	appropriate use of language constructs 
•	style (commenting, indentation, etc.) 
•	difficulty (extra marks for difficult extensions/tasks) 

# What I did:
My version of the zork game is based in a theme Park. In this theme park you enter and leave through the front gate. You participate in minigames at the theme park to earn tokens. Tokens can be used to buy food and drinks required to survive, or just to go on a ride. They are your score when you end the game by leaving through the front gate or by losing in any other way. Be carful though because you might get kicked out if you do certain things!
