/*

Gamebots UT Copyright (c) 2002, Andrew N. Marshal, Gal Kaminka
GameBots2004 - Pogamut3 derivation Copyright (c) 2010, Michal Bida, Josef Jirasek

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

   * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This software must also be in compliance with the Epic Games Inc. license for mods which states the following: "Your mods must be distributed solely for free, period. Neither you, nor any other person or party, may sell them to anyone, commercially exploit them in any way, or charge anyone for receiving or using them without prior written consent of Epic Games Inc. You may exchange them at no charge among other end-users and distribute them to others over the Internet, on magazine cover disks, or otherwise for free." Please see http://www.epicgames.com/ut2k4_eula.html for more information.

*/
//-----------------------------------------------------------
//
//-----------------------------------------------------------
class ObservingConnection extends GBClientClass;

// The server we are connected to.
var ObservingServer parent;

// The Observer this connection is using.
var Observer observer;

// Time between sending asynchronous messages.
var config float updateTime;

var config bool bGame;    // Send NFO, PLS messages asynchronously?
var config bool bSelf;    // Send SLF, MYINF messages asynchronously?
var config bool bSee;     // Send PLR, INV, NAV, MOV, VEH, PRJ messages asynchronously?
var config bool bSpecial; // Send BOM, DOM, FLG messages asynchronously?
var config bool bAsync;   // Send other messages (DIE, AIN...) asynchronously?
                          // Note: There is no way to send these messages synchronously. if this is false, these messages will not be sent at all.

function PostBeginPlay() {
	Parent = ObservingServer(Owner);
	observer = spawn(class'GameBots2004.Observer', self,,,);
	if (bDebug) log("Spawned ObservingConnection");
	super.PostBeginPlay();
}

//Accepted connection to a socket.
event Accepted() {
	super.Accepted();
	log("Observing connection established.");

	if(bDebug) log("Accepted ObservingConnection");

	SendLine("HELLO_OBSERVER");

	if (BotDeathMatch(Level.Game).bPasswordProtected) {
		gotoState('checkingPassword','Waiting');
	} else {
		gotoState('waiting','Waiting');
	}
}

event Closed() {
	Destroy();
}

event Destroyed() {
	if (observer != none) {
		if (observer.IsListening()) observer.RemoveListener();
		observer.Destroy();
	}
	super.Destroyed();
}

/*******************
* Helper functions *
*******************/

// Called whenever the Observer terminates unexpectedly (e.g. the observed player left the game).
singular event ChildDied() {
	if (observer.IsListening()) {
		observer.RemoveListener();
		if (bDebug) log("Lost observed player " $ observer.MyController.GetHumanReadableName());
	}

	SendLine("LOST CHILD");
	gotoState('waiting', 'Waiting');
}

// Used by the Observer to send asynchronous messages.
function SendAsyncMessage(string message) {
	if ((bAsync) && IsInState('observing')) SendLine(message);
}

// Used by the Observer to send synchronous messages.
function SendSyncMessage(string message) {
	SendLine(message);
}

//after handshake with client - we will export some basic information
function ExportStatus() {
		SendLine("SHS"); // StartHandShake message
		SendGameInfo();

		//info about connected players
		SendLine("SPLR");
		ExportPlayers();
		SendLine("EPLR");

		SendLine("EHS"); // EndHandShake message
}

function ExportGame(bool sync) {
	SendGameInfo();
}

/*****************************
* Handling received messages *
*****************************/

function ProcessAction(string cmdType) {
	if(bDebug) log("comandType: " $ cmdType);

	if (IsInState('checkingPassword')) {
		switch (cmdType)	{
			case "PASSWORD": ReceivedPassword(); break; // Authehnticate with a passworded server.
			case "READY":	ReceivedReady();	break;
		}
	} else {
		switch (cmdType) {
			// Command messages - can be sent anytime.
			case "GETPLRS": ExportPlayers();   break;
			case "GETNAVS": ExportNavPoints(); break;
			case "GETINVS": ExportInventory(); break;
			case "GETITC":  ExportItemClasses(); break;
			case "INIT":	ReceivedInit();    break; // Connect to a specified player.
			case "PING":	SendLine("PONG");  break; // Check if the connection is alive.
			case "CONF":	ReceivedConf();    break; // Configure the observer.
			case "QUIT":	Destroy();		   break; // Terminate the connection.
			case "READY":   ReceivedReady();   break; // Send game information.
		}

		if (IsInState('observing')) {
			switch (cmdType) {
				// Info messages - can be sent only when we are already connected to an observer.
				case "DISC": ReceivedDisc();                  break; // Disconnect the observer.
				case "GAME": ExportGame(true);                break; // Send game info.
				case "SELF": observer.ExportSelf(true);       break; // Send info about the observed player.
				case "SEE":  observer.ExportSee(true);        break; // Send info about stuff the observed player sees.
				case "SPECIAL": observer.ExportSpecial(true); break; // Send info about special objects (FLG, BOM, DOM).
				case "ALL":
					observer.ExportSelf(true);
					observer.ExportSee(true);
					observer.ExportSpecial(true);
					break;
			}
		}
	}
}

// CONF {UpdateTime float} {Update float}
//	  {GAM bool} {SLF bool} {MYINV bool} {PLR bool} {INV bool} {NAV bool} {MOV bool} {DOM bool} {FLG bool}
//	  {See bool} {All bool} {Async bool}
// Configure the observer.
//
// UpdateTime	 - period between sending asynchronous messages (in seconds).
// Update		 - alias for UpdateTime
// [Message type] - whether to send these messages asynchronously.
// See			- toggle asynchronous sending of visibility messages (PLR, INV, NAV, MOV, DOM, FLG)
// All			- toggle asynchronous sending of all messages on/off at once.
// Async		  - whether to send other asynchronous messages (DIE, PRJ...)
//				  Note: these messages can not be sent synchronously. Setting this to False will cause them never to be sent.
//
// All parameters are optional, ommiting any of them does not change that setting.
function ReceivedConf() {
	local string strTemp;

	strTemp = GetArgVal("UpdateTime");
	if (strTemp != "") updateTime = float(strTemp);

	strTemp = GetArgVal("Update");
	if (strTemp != "") updateTime = float(strTemp);

	strTemp = GetArgVal("Game");
	if (strTemp != "") bGame = bool(strTemp);

	strTemp = GetArgVal("Self");
	if (strTemp != "") bSelf = bool(strTemp);

	strTemp = GetArgVal("See");
	if (strTemp != "") bSee = bool(strTemp);

	strTemp = GetArgVal("Special");
	if (strTemp != "") bSpecial = bool(strTemp);

	strTemp = GetArgVal("All");
	if (strTemp != "") {
		bGame = bool(strTemp);
		bSelf = bool(strTemp);
		bSee = bool(strTemp);
		bSpecial = bool(strTemp);
	}

	strTemp = GetArgVal("Async");
	if (strTemp != "") bAsync = bool(strTemp);
}

// DISC
// Disconnect this observer.
function ReceivedDisc() {
	if (bDebug) log("Disconnected observer from " $ observer.MyController.GetHumanReadableName());
	observer.RemoveListener();
	gotoState('waiting', 'Waiting');
}

// INIT {Id int} {Name string}
// Connect this observer to a player with this Id or Name. Only one of the parameters is needed.
function ReceivedInit() {
	local string playerId;
	local string playerName;
	local Controller c;
	playerId = GetArgVal("Id");
	playerName = GetArgVal("Name");

	for (c = Level.ControllerList; c != none; c = c.NextController) {
		if ((c $ c.PlayerReplicationInfo.PlayerID) ~= playerId || c.GetHumanReadableName() ~= playerName) {
			// This is the player we want.
			if (observer.IsListening()) {
				observer.RemoveListener();
			}

			if (observer.AddListener(c)) {
				observer.SendSlf();
				if (bDebug) log("Started observing player '" $ c.GetHumanReadableName() $ "'");
				gotoState('observing', 'Waiting');
			} else {
				if (bDebug) log("Cannot start observing player '" $ c.GetHumanReadableName() $ "'");
				SendLine("ERROR");
			}
			return;
		}
	}
	SendLine("UNKNOWN PLAYER");
}

// PASSWORD {Password string}
// Authentificate with the server with the specified password.
function ReceivedPassword() {
	local string pwd;
	pwd = GetArgVal("Password");
	if (pwd == BotDeathMatch(Level.Game).Password) {
		SendLine("PASSWDOK");
		ExportStatus();
		gotoState('running','Waiting');
	} else {
		SendLine("PASSWDWRONG");
		Closed();
	}
}

// READY
// Authenticate the user, send game info, start communication.
function ReceivedReady() {
	if (IsInState('checkingPassword')) {
		SendLine("PASSWORD {BlockedByIP " $ BotDeathMatch(Level.Game).PasswordByIP $ "}");
	} else {
		ExportStatus();
	}
}

state checkingPassword {
Begin:
Waiting:
	sleep(5.0);
	goto 'Waiting';
}

state waiting {
Begin:
Waiting:
	if (observer.IsListening()) {
		gotoState('observing', 'Waiting');
	}
	sleep(1.0);
	goto 'Waiting';
}

state observing {
Begin:
Waiting:
	if (!observer.IsListening()) {
		ChildDied();
		gotoState('waiting', 'Waiting');
	}

	// Send requested messages asynchroonously.
	if (bGame || bSelf || bSee || bSpecial) {
		SendLine("BEG {Time " $ Level.TimeSeconds $ "}");
		if (bGame) ExportGame(false);
		if (bSelf) observer.ExportSelf(false);
		if (bSee) observer.ExportSee(false);
		if (bSpecial) observer.ExportSpecial(false);
		SendLine("END {Time " $ Level.TimeSeconds $ "}");
	}

	sleep(updateTime);
	goto 'Waiting';
}

defaultproperties
{
	updateTime=1.000000
	bGame=True
	bSelf=True
	bSee=True
	bSpecial=True
	bAsync=True
}

