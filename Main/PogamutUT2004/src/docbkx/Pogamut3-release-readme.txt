  
********************************
**** Pogamut 3 Beta release ****
********************************

------------------
- Prerequisities -
------------------

1) Unreal Tournament 2004 with patch 3369 (available at http://www.beyondunreal.com/main/ut2004/ut2004essential.php)
2) JDK 1.6 or higher
3) Apache Ant 1.7

----------------
- Installation -
----------------

1) Copy content of gamebots directory (all files and subdirectories in it) to UT2004/System directory.


----------------
- Example bot  -
----------------

One example bot is in directory PogamutUT2004/examples/. The bot can be opened as Netbeans project.
Bot will connect to the UnrealTournament server running on localhost:3000.

To run the bot:
1) run UT server with Gamebots mod - use UT2004/System/startGamebotsDMServer.bat
2) execute the bot - from Netbeans or by typing "ant run" in the shell while being in the directory PogamutUT2004/examples/01-Simplebot, running the bot for first time will cause compilation of gavialib and PogamutUT2004, this may take some time.


------------
- Contacts -
------------

Project homepage:  http://artemis.ms.mff.cuni.cz/pogamut
Forum:             http://artemis.ms.mff.cuni.cz/pogamut/tiki-forums.php

