/*

Gamebots UT Copyright (c) 2002, Andrew N. Marshal, Gal Kaminka
GameBots2004 - Pogamut3 derivation Copyright (c) 2010, Michal Bida

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

   * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This software must also be in compliance with the Epic Games Inc. license for mods which states the following: "Your mods must be distributed solely for free, period. Neither you, nor any other person or party, may sell them to anyone, commercially exploit them in any way, or charge anyone for receiving or using them without prior written consent of Epic Games Inc. You may exchange them at no charge among other end-users and distribute them to others over the Internet, on magazine cover disks, or otherwise for free." Please see http://www.epicgames.com/ut2k4_eula.html for more information.

*/
class ControlConnection extends GBClientClass;

//Parent server
var ControlServer Parent;

var config float UpdateTime;

var() class<Pickup> tempPickupClass; //for inventory spawns

var() class<Actor> tempActorClass; //for actor spawns

//switches for exporting information after READY command
var config bool bExportGameInfo;
var config bool bExportMutators;
var config bool bExportITC;
var config bool bExportNavPoints;
var config bool bExportMovers;
var config bool bExportInventory;
var config bool bExportPlayers;

var config bool bExportKeyEvents;

//Accepted connection to a socket
event Accepted()
{
	super.Accepted();

	log("Control Connection established.");

	if(bDebug)
		log("Accepted ControlConnection");


	SendLine("HELLO_CONTROL_SERVER");

    if (BotDeathMatch(Level.Game).bPasswordProtected)
		gotoState('checkingPassword','Waiting');

}

//Connection closed at remote end
event Closed()
{
	//log("In ControlConnection, event Closed()");
	Destroy();
}

event Destroyed()
{
	//log("In ControlConnection, event Destroyed()");
	SendLine("FIN");
}

function PostBeginPlay()
{
	Parent = ControlServer(Owner);
	if(bDebug)
		log("Spawned ControlConnection");
}

//triggered when weve gotten ready message from client, or after succesfull password check
//we will send game nfo, all navpoints and all items in a map
function ExportStatus()
{
	SendLine("SHS"); // StartHandShake message
	//Methods defined in super class
	if (bExportGameInfo)
		SendGameInfo();  //NFO message
	if (bExportMutators)
		ExportMutators(); //SMUT, MUT, EMUT messages
	if (bExportITC)
		ExportItemClasses(); //SITC, ITC, EITC messages
	if (bExportNavPoints)
		ExportNavPoints(); //SNAV, NAV, ENAV messages
	if (bExportMovers)
		ExportMovers();
	if (bExportInventory)
		ExportInventory(); //SINV, IINV, EINV messages
	if (bExportPlayers) {
		SendLine("SPLR");
		ExportPlayers(false); //false for complete info - including location, etc.
		SendLine("EPLR");
	}
	SendLine("EHS"); // EndHandShake message

}

//Handles commands for ControlServer
function ProcessAction(string cmdType)
{
	if(bDebug)
		log("comandType:"@cmdType);

	if (IsInState('checkingPassword'))
	{
		switch(cmdType)
		{
			case "PASSWORD":
				ReceivedPassword();
			break;
			case "READY":
				ReceivedReady();
			break;
		}
	}
	else
	{
		switch(cmdType)
		{
			case "ADDBOT":
				ReceivedAddBot();
			break;
			case "ADDINV":
				ReceivedAddInv();
			break;
			case "CHANGEMAP":
				ReceivedChangeMap();
			break;
			case "CHANGETEAM":
				ReceivedChangeTeam();
			break;
			case "CHATRR":
				ReceivedChAtrr();
			break;
			case "CLEAR":
				ReceivedClear();
			case "CONF":
				ReceivedConf();
			break;
			case "CONFGAME":
				ReceivedConfGame();
			break;
			case "CONSOLE":
				ReceivedConsole();
			break;
			case "DRAWLINES":
				ReceivedDrawStayingDebugLines();
			break;
			case "ENDPLRS":
				gotoState('running','Waiting');
			break;
			case "FTRACE":
				ReceivedFTrace();
			break;
			case "GETINVS":
				ExportInventory();
			break;
			case "GETMAPS":
				ReceivedGetMaps();
			break;
			case "GETNAVS":
				ExportNavPoints();
			break;
			case "GETPLRS":
			    SendLine("SPLR");
				ExportPlayers();
				SendLine("EPLR");
			break;
			case "MESSAGE":
				ReceivedMessage();
			break;
			case "PAUSE":
				ReceivedPause();
			break;
			case "PING":
				SendLine("PONG");
			break;
			case "QUIT":
				Destroy();
			break;
			case "KICK":
				ReceivedKick();
			break;
			case "KILL":
				ReceivedKillBot();
			break;
			case "READY":
				ReceivedReady();
			break;
			case "REC":
				ReceivedRec();
			break;
			case "RESPAWN":
				ReceivedRespawn();
			break;
			case "SENDCTRLMSG":
				ReceivedSendCtrlMsg();
			break;
			case "SETGAMESPEED":
				ReceivedSetGameSpeed();
			break;
			case "SETLOCK":
				ReceivedSetLock();
			break;
			case "SETPASS":
				ReceivedSetPass();
			break;
			case "SPAWNACTOR":
				ReceivedSpawnActor();
			break;
			case "STARTPLRS":
				ReceivedStartPlrs();
			break;
			case "STOPREC":
				ReceivedStopRec();
			break;
			case "TRACE":
				ReceivedTrace();
			break;
		}//end switch
	}//end if
}

function ReceivedAddBot()
{
	local string target;
	local vector vector1;
	local rotator rotator1;
	local float floatNumber;
	local int intNumber;

	target = GetArgVal("Name");
	ParseVector(vector1,"StartLocation");
	ParseRot(rotator1,"StartRotation");
	floatNumber = float(GetArgVal("Skill"));
	intNumber = int(GetArgVal("Team"));
	if( Level.Game.isA('BotDeathMatch')) {
		BotDeathMatch(Level.Game).AddEpicBot(target, intNumber, vector1, rotator1, floatNumber);
	}
}

function ReceivedAddInv()
{
	local string target, string1;
	local Controller C;
	local bool bAlreadyExported;
	local int i;

	target = GetArgVal("Id");

	if (target == "")
	{
		return;
	}
	string1 = GetArgVal("Type");
	string1 = "class'"$string1$"'";
	setPropertyText("tempPickupClass",string1);
	//log("Added inventory will be "$tempInventory);

	//Allow to add just Pickup classes
	if (tempPickupClass != None)
		for(C = Level.ControllerList; C != None; C = C.NextController )
		{
			if( (C$C.PlayerReplicationInfo.PlayerID) == target )
			{
				//First check if we haven't exported this already
				bAlreadyExported = false;
				for (i = 0; i < RemoteBot(C).myConnection.ExportedPickup.Length; i++)
				{
					if (RemoteBot(C).myConnection.ExportedPickup[i].PickupClass == tempPickupClass)
					{
						bAlreadyExported = true;
						break;
					}
				}

				if (!bAlreadyExported)
				{
					RemoteBot(C).myConnection.SendLine(GetPickupInfoToITC(tempPickupClass));

					//Add exported pickup class to list holding exported classes
					RemoteBot(C).myConnection.ExportedPickup.Insert(RemoteBot(C).myConnection.ExportedPickup.Length,1);
					RemoteBot(C).myConnection.ExportedPickup[RemoteBot(C).myConnection.ExportedPickup.Length - 1].PickupClass = tempPickupClass;
				}

				//log("Added inventory will be "$tempInventory);

				RemoteBot(C).SpawnInventory(tempPickupClass);

			}

		}//end for
}

function ReceivedChangeMap()
{
	local string target;
	local bool bResult;
	local GBClientClass G;
	local Controller C;
	local int i;

	target = GetArgVal("MapName");
	if (target != "" && !Level.bLevelChange)
	{
		//Check if the map exists in current map list
		bResult = false;
		for (i=0; i < BotDeathMatch(Level.Game).Maps.Length; i++ )
		{
			if (target==BotDeathMatch(Level.Game).Maps[i])
			{
			 	bResult = true;
			 	break;
			}
		}
    	//Change just when the map is in MapList
		if (bResult)
		{
    		//We refuse another connections when changing map
			BotDeathMatch(Level.Game).theBotServer.bClosed = true;
			BotDeathMatch(Level.Game).theBotServer.Close();

			if (BotDeathMatch(Level.Game).theControlServer != none) {
				BotDeathMatch(Level.Game).theControlServer.bClosed = true;
				BotDeathMatch(Level.Game).theControlServer.Close();
			}

			if (BotDeathMatch(Level.Game).theObservingServer != none) {
				BotDeathMatch(Level.Game).theObservingServer.bClosed = true;
				BotDeathMatch(Level.Game).theObservingServer.Close();
			}

			//SendLine("MAPCHANGE"$ib$as$"MapName"$ib$target$ae);
			for(C = Level.ControllerList; C != None; C = C.NextController )
			{
				if (C.isA('RemoteBot'))
				{
					RemoteBot(C).myConnection.SendLine("MAPCHANGE {MapName "$target$"}");
					RemoteBot(C).myConnection.SendLine("FIN");
				}

			}
			for (G = Parent.ChildList; G != None; G = G.Next )
			{
				G.SendLine("MAPCHANGE {MapName "$target$"}");
				G.SendLine("FIN");
			}

			Level.ServerTravel(target,false);
		}
	}
}

function ReceivedChangeTeam()
{
	local string temp, target;
	local Controller C, TargetBot;
	local bool result;

	if (!Level.Game.IsA('BotTeamGame'))
		return;

	target = GetArgVal("Id");

	if (target == "")
		return;

	for(C = Level.ControllerList; C != None; C = C.NextController )
	{
		if( GetUniqueId(C) == target )
		{
			if (C.IsA('RemoteBot')) { //|| C.IsA('GBxBot') TODO - for GBxBot does not work properly right now
				TargetBot = C;
				break;
			} else
				return;
		}
	}

	if (TargetBot != none)
	{
		temp = GetArgVal("Team");
		if (temp != "")
		{
	    	result = BotTeamGame(Level.Game).ChangeTeam(TargetBot, int(temp), true);

			if (TargetBot.IsA('RemoteBot'))
				RemoteBot(TargetBot).myConnection.SendLine("TEAMCHANGE {Success " $ result $
					"} {DesiredTeam " $ temp $
					"}");

			GlobalSendLine("TEAMCHANGE {Id " $ GetUniqueId(TargetBot ) $
				"} {Success " $ result $
				"} {DesiredTeam " $ temp $ "}", true, false, false);
	    }
	}
}

// this will do a dirty clear, sometimes bots remain on the server after disconnect
// this delete all actors that are pawns or controllers on the server
//
function ReceivedClear()
{
	local GBxPawn P;
	local RemoteBot R;
	local BotConnection C;
	//local Controller C;

    foreach AllActors (class 'BotConnection', C, )
	{
		C.Destroy();
	}

	foreach AllActors (class 'RemoteBot', R, )
	{
		R.Destroy();
	}

	foreach AllActors (class 'GBxPawn', P, )
	{
		P.Destroy();
	}


}

function ReceivedChAtrr()
{
	local string target;
	local Controller C;

	target = GetArgVal("Id");
	if (target == "")
		return;

	for(C = Level.ControllerList; C != None; C = C.NextController )
	{
		if( (C$C.PlayerReplicationInfo.PlayerID) == target )
		{
			break;
		}

	}
	if (C == none)
		return;

	target = GetArgVal("Health");
	if (target != "")
	{
		RemoteBot(C).SetHealth(int(target));
	}

	target = GetArgVal("Adrenaline");
	if (target != "")
	{
		RemoteBot(C).SetAdrenaline(int(target));
	}

}

function ReceivedConf() {
	local string target, string1, argName, argValue;
	local float floatNumber;
	local Controller C;
	local rotator r;
	local int i;

	target = GetArgVal("Id");
	if (target == "")
		return;

	for(C = Level.ControllerList; C != None; C = C.NextController )	{
		if( GetUniqueId(C) == target ) {
			break;
		}
	}
	if (C == none)
		return;

	for (i = 0; i < ArgsMaxCount; i++) {
		argName = ReceivedArgs[i];
		argValue = ReceivedVals[i];
		if (argName != "") {
			if (argName == "Name") {
				target = argValue;
				RemoteBot(C).PlayerReplicationInfo.PlayerName = target;
				Level.Game.changeName( RemoteBot(C), target, true );
			} else if (argName == "AutoTrace")
				RemoteBot(C).bAutoTrace = bool(argValue);
			else if (argName == "DrawTraceLines")
				RemoteBot(C).bDrawTraceLines = bool(argValue);
			else if (argName == "ManualSpawn")
				RemoteBot(C).bAutoSpawn = !bool(argValue);
			else if (argName == "ShowFocalPoint")
				RemoteBot(C).bShowFocalPoint = bool(argValue);
			else if (argName == "ShowDebug")
				RemoteBot(C).bDebug = bool(argValue);
			else if (argName == "SpeedMultiplier") {
				floatNumber = float(argValue);
				if ( (floatNumber >= 0.1) && (floatNumber <= RemoteBot(C).MaxSpeed) )
				{
					RemoteBot(C).SpeedMultiplier = floatNumber;
				    if (RemoteBot(C).Pawn != none) {
						RemoteBot(C).Pawn.GroundSpeed = floatNumber * RemoteBot(C).Pawn.Default.GroundSpeed;
						RemoteBot(C).Pawn.WaterSpeed = floatNumber * RemoteBot(C).Pawn.Default.WaterSpeed;
						RemoteBot(C).Pawn.AirSpeed = floatNumber * RemoteBot(C).Pawn.Default.AirSpeed;
						RemoteBot(C).Pawn.LadderSpeed = floatNumber * RemoteBot(C).Pawn.Default.LadderSpeed;
					}
				}
			} else if(argName == "RotationRate") {
				ParseRot(r,"RotationRate");
				if (r != rot(0,0,0)) {
					RemoteBot(C).RotationRate = r;
					if (RemoteBot(C).Pawn != none) {
						RemoteBot(C).Pawn.RotationRate = r;
					}
				}
			} else if ((argName == "Invulnerable") && (bAllowCheats == true))
				RemoteBot(C).bGodMode = bool(argValue);
			else if (argName == "VisionTime") {
				floatNumber = float(argValue);
				if ((floatNumber >= 0.1) && (floatNumber <= 2)) {
					RemoteBot(C).myConnection.visionTime = floatNumber;
					RemoteBot(C).myConnection.sleepTime = floatNumber / RemoteBot(C).myConnection.locUpdateMultiplier;
				}
			} else if (argName == "SynchronousOff") {
				RemoteBot(C).myConnection.bSynchronousMessagesOff = bool(argValue);
			} else if (argName == "SyncNavPointsOff") {
				RemoteBot(C).myConnection.bSynchronousNavPoints = !bool(argValue);
			} else if (argName == "AutoPickupOff") {
				RemoteBot(C).bDisableAutoPickup = bool(argValue);
			if (RemoteBot(C).Pawn != none)
				RemoteBot(C).Pawn.bCanPickupInventory = !RemoteBot(C).bDisableAutoPickup;
			}
		}
	}

	SendNotifyConf(RemoteBot(C));

}

function ReceivedConfGame()
{
	local int intNumber;

	if (bool(GetArgVal("Restart")))
	{
		GlobalSendLine("GAMERESTART {Started True}",true,true,true,false);
		BotDeathMatch(Level.Game).RemoteRestartGameState();
		GlobalSendLine("GAMERESTART {Finished True}",true,true,true,false);
	}
	if (GetArgVal("WeaponStay") != "")
	{
		Level.Game.bWeaponStay = bool(GetArgVal("WeaponStay"));
	}
	if (GetArgVal("WeaponThrowing") != "")
	{
		Level.Game.bAllowWeaponThrowing = bool(GetArgVal("WeaponThrowing"));
	}
	if (GetArgVal("GoalScore") != "")
	{
		intNumber = int(GetArgVal("GoalScore"));
		if (intNumber >= 1)
			Level.Game.GoalScore = intNumber;
	}
	if (GetArgVal("TimeLimit") != "")
	{
		intNumber = int(GetArgVal("TimeLimit"));
		if (intNumber >= 1)
			Level.Game.TimeLimit = intNumber;
	}
	if (GetArgVal("MaxLives") != "")
	{
		intNumber = int(GetArgVal("MaxLives"));
		if (intNumber >= 1)
			Level.Game.MaxLives = intNumber;
	}
	if (GetArgVal("GameMessage") != "")
	{
		if (GetArgVal("GameMessageTime") != "")
		{
			BotDeathMatch(Level.Game).SetGameMessage(GetArgVal("GameMessage"), float(GetArgVal("GameMessageTime")));
		}
	}
	Level.Game.SaveConfig();
	Level.Game.GameReplicationInfo.SaveConfig();

}

function ReceivedConsole()
{
	local string target;
	local string id;
	local Controller C;

	target = GetArgVal("Command");
	id = GetArgVal("Id");

	if (id == "") {
		ConsoleCommand(target, True);
	} else {
		for(C = Level.ControllerList; C != None; C = C.NextController )	{
			if (GetUniqueId(C) == id ) {
				if (C.IsA('GBxPlayer')) {
					GBxPlayer(C).ClientConsoleCommand(target, true);
				//for screenshots, command has to be issued from this class
				} else
					C.ConsoleCommand(target, true);
				break;
			}
		}
	}
}

function ReceivedDrawStayingDebugLines()
{
	local string temp, lines;
	local Controller C;
	local vector lineColor;
	local bool bClear;

	lines = GetArgVal("Vectors");
	temp = GetArgVal("Color");
	bClear = bool(GetArgVal("ClearAll"));

	if (temp != "")
		ParseVector(lineColor,"Color");
	else
		lineColor=vect(255,255,255);


	for(C = Level.ControllerList; C != None; C = C.NextController )	{
        if (C.IsA('GBxPlayer')) {
			GBxPlayer(C).ClientDrawStayingDebugLines(lines, lineColor, bClear);
		}
	}

}

function ReceivedFTrace()
{
	local vector v,v2;
	local string target;

	if (GetArgVal("From") == "")
	{
		return;
	}
	else
	{
		ParseVector(v,"From");
	}
	if (GetArgVal("To") == "")
	{
		return;
	}

	ParseVector(v2,"To");
	target = GetArgVal("Id");

	SendLine("FTR {Id " $ target $
		"} {From " $ v $
		"} {To " $ v2 $
		"} {Result " $ !FastTrace(v2,v) $
		"}");
}


function ReceivedGetMaps()
{
	local int i;

	SendLine("SMAP");
	for (i=0; i < BotDeathMatch(Level.Game).Maps.Length; i++ ) {
		SendLine("IMAP {Name " $ BotDeathMatch(Level.Game).Maps[i] $ "}");

	}
	SendLine("EMAP");
}

function ReceivedMessage()
{
	local string target, text;
	local bool boolResult;
	local float FadeOut;
	local int TeamIndex;

	//Note - currently only allow messages under 256 chars
	target = GetArgVal("Id");
	text = GetArgVal("Text");
	boolResult = bool(GetArgVal("Global"));
	TeamIndex = int(GetArgVal("TeamIndex"));
	if(text != "") {
		BroadcastTextMessage(target,text,TeamIndex,boolResult);
	}

}

function ReceivedKick()
{
	local string target;
	local Controller C;
	local Bot b;

	target = GetArgVal("Id");
	if (target == "")
		return;
    for(C = Level.ControllerList; C != None; C = C.NextController )
	{
		if( (C$C.PlayerReplicationInfo.PlayerID) == target )
		{
			if (C.isA('RemoteBot'))
			{
				//TODO: Send "Kicked"?
				RemoteBot(C).myConnection.SendLine("FIN");
				RemoteBot(C).myConnection.Closed();
			}
			else if (C.isA('Bot'))
			{
				b = Bot(C);

				//probably wont need this line with decreasing number of min players...
				UnrealMPGameInfo(Level.Game).MinPlayers = Max(UnrealMPGameInfo(Level.Game).MinPlayers - 1, UnrealMPGameInfo(Level.Game).NumPlayers + UnrealMPGameInfo(Level.Game).NumBots - 1);
				if ( (Vehicle(b.Pawn) != None) && (Vehicle(b.Pawn).Driver != None) )
					Vehicle(b.Pawn).Driver.KilledBy(Vehicle(b.Pawn).Driver);
				else if (b.Pawn != None)
					b.Pawn.KilledBy( b.Pawn );
				if (b != None)
				b.Destroy();
			}
			break;

		}

	}
}

function ReceivedKillBot()
{
	local string target;
	local Controller C;

	target = GetArgVal("Id");

	if (target == "") {
		return;
	}

	for(C = Level.ControllerList; C != None; C = C.NextController )
	{
		if( GetUniqueId(C) == target )
		{
			if (C.IsA('RemoteBot'))
				RemoteBot(C).KillBot();
		    if (C.IsA('GBxPlayer'))
   				GBxPlayer(C).KillBot();
		    if (C.IsA('GBxBot'))
   				GBxBot(C).KillBot();
		}

	}

}

function ReceivedPause()
{
	local bool bWasPaused;

	if (!bAllowPause)
		return;
	if ((Level.bPlayersOnly == true) || (Level.Pauser != none))
		bWasPaused = true;
	else
		bWasPaused = false;


	if (GetArgVal("PauseBots")!="")
	{
		Level.bPlayersOnly = bool(GetArgVal("PauseBots"));
	}
	if (GetArgVal("PauseAll")!="")
	{
		if (bool(GetArgVal("PauseAll")))
		{
			if (Level.Pauser == None)
			{
				Level.Pauser = BotDeathMatch(Level.Game).LevelPauserFeed;
			}
		}
		else
		{
			Level.Pauser = None;
		}
	}

	if (bWasPaused == true)
	{
		if ((Level.bPlayersOnly == false) && (Level.Pauser == none))
			SendNotifyPause(false); //send resume message
	}
	else
	{
		if ((Level.bPlayersOnly == true) || (Level.Pauser != none))
			SendNotifyPause(true); //send pause message
	}

}

function ReceivedPassword()
{
	local string target;

	target = GetArgVal("Password");

	if (target == BotDeathMatch(Level.Game).Password)
	{
		SendLine("PASSWDOK");
		ExportStatus();
		gotoState('running','Running');
	}
	else
	{
		SendLine("PASSWDWRONG");
		Closed();
	}
}

function ReceivedReady()
{

	if ( IsInState('checkingPassword') )
		SendLine("PASSWORD {BlockedByIP " $ BotDeathMatch(Level.Game).PasswordByIP $ "}");
	else
	{
		ExportStatus();
		gotoState('running','Running');
	}

}

function ReceivedRec()
{
	local string target;

	target = GetArgVal("FileName");
	ConsoleCommand("demorec "$target, True);
	sendLine("RECSTART");
}

function ReceivedRespawn()
{
	local string target;
	local vector vector1;
	local rotator rotator1;
	local Controller C;

	target = GetArgVal("Id");

	if (target == "") {
		return;
	}
	ParseVector(vector1,"StartLocation");
	ParseRot(rotator1,"StartRotation");
	for(C = Level.ControllerList; C != None; C = C.NextController )
	{
		if( GetUniqueId(C) == target )
		{
			if (C.IsA('RemoteBot'))
				RemoteBot(C).RespawnPlayer(vector1,rotator1);
		    if (C.IsA('GBxPlayer'))
   				GBxPlayer(C).RespawnPlayer(vector1,rotator1);
		    if (C.IsA('GBxBot'))
   				GBxBot(C).RespawnPlayer(vector1,rotator1);
		}

	}

}

function ReceivedSendCtrlMsg()
{
	local string target, type, ps1, ps2, ps3, pi1, pi2, pi3, pf1, pf2, pf3, pb1, pb2, pb3;
	local Controller C;
	local bool bSendAll;


    if (!bool(GetArgVal("SendAll"))) {
		target = GetArgVal("BotId");
		if (target == "")
			return;
	} else
		bSendAll = true;

	type = GetArgVal("Type");
	ps1 = GetArgVal("PS1");
	ps2 = GetArgVal("PS2");
	ps3 = GetArgVal("PS3");
	pi1 = GetArgVal("PI1");
	pi2 = GetArgVal("PI2");
	pi3 = GetArgVal("PI3");
	pf1 = GetArgVal("PF1");
	pf2 = GetArgVal("PF2");
	pf3 = GetArgVal("PF3");
	pb1 = GetArgVal("PB1");
	pb2 = GetArgVal("PB2");
	pb3 = GetArgVal("PB3");

	for(C = Level.ControllerList; C != None; C = C.NextController )	{
		if (bSendAll) {
			if (C.IsA('RemoteBot')) {
				RemoteBot(C).NotifyControlMessage(type, ps1, ps2, ps3, pi1, pi2, pi3, pf1, pf2, pf3, pb1, pb2, pb3);
			}
		} else if( GetUniqueId(C) == target ) {
 			RemoteBot(C).NotifyControlMessage(type, ps1, ps2, ps3, pi1, pi2, pi3, pf1, pf2, pf3, pb1, pb2, pb3);
			break;
		}
	}
}


function ReceivedSetGameSpeed()
{
	local float floatNumber;

	floatNumber = float(GetArgVal("Speed"));
	if ( (floatNumber >= 0.01) && (floatNumber <= 50 ) )
	{
		Level.Game.bAllowMPGameSpeed = true;
		Level.Game.SetGameSpeed(floatNumber);
		Level.Game.SaveConfig();
		Level.Game.GameReplicationInfo.SaveConfig();
	}
}

function ReceivedSetLock()
{
	local string target;

	target = GetArgVal("BotServer");
	if (target != "")
	{
		if (bool(target))
		{
			if (BotDeathMatch(Level.Game).theBotServer.LinkState == STATE_Listening)
			{
				BotDeathMatch(Level.Game).theBotServer.bClosed = true;
				BotDeathMatch(Level.Game).theBotServer.Close();
			}
		}
		else
		{
			if (BotDeathMatch(Level.Game).theBotServer.LinkState != STATE_Listening)
			{
				BotDeathMatch(Level.Game).theBotServer.bClosed = false;
				BotDeathMatch(Level.Game).theBotServer.Listen();
			}
		}
	}

	target = GetArgVal("ControlServer");
	if (target != "")
	{
		if (bool(target))
		{
			if (BotDeathMatch(Level.Game).theControlServer.LinkState == STATE_Listening)
			{
				BotDeathMatch(Level.Game).theControlServer.bClosed = true;
				BotDeathMatch(Level.Game).theControlServer.Close();
			}
		}
		else
		{
			if (BotDeathMatch(Level.Game).theControlServer.LinkState != STATE_Listening)
			{
				BotDeathMatch(Level.Game).theControlServer.bClosed = false;
				BotDeathMatch(Level.Game).theControlServer.Listen();
			}
		}
	}

}

function ReceivedSetPass()
{
	local string target;

	target = GetArgVal("Password");

	if (target != "")
	{
		BotDeathMatch(Level.Game).bPasswordProtected = true;
		BotDeathMatch(Level.Game).PasswordByIP = string(RemoteAddr.Addr)$":"$string(RemoteAddr.Port);
		BotDeathMatch(Level.Game).Password = target;
	}
	else
	{
		BotDeathMatch(Level.Game).bPasswordProtected = false;
		BotDeathMatch(Level.Game).PasswordByIP = "";
		BotDeathMatch(Level.Game).Password = "";
	}

}

function ReceivedSpawnActor()
{
	local string tmp;
	local vector v;
	local rotator r;
	local Actor SpawnedActor;

	ParseVector(v,"Location");
	ParseRot(r,"Rotation");
	tmp = GetArgVal("Type");

	if (tmp != "")
	{
		tmp = "class'"$tmp$"'";
		setPropertyText("tempActorClass",tmp);
		SpawnedActor = Spawn(tempActorClass,self,,v,r);
	}

	//HACK: For now, so we can enter vehicles
	if (SpawnedActor.IsA('Vehicle'))
	{
		Vehicle(SpawnedActor).bTeamLocked = false;
		Vehicle(SpawnedActor).bEnterringUnlocks = true;
	}

}

function ReceivedStartPlrs()
{
	local string tmp;

	tmp = GetArgVal("Humans");
	if (tmp != "")
	{
		bExportHumanPlayers = bool(tmp);
	}
	tmp = GetArgVal("GBBots");
	if (tmp != "")
	{
		bExportRemoteBots = bool(tmp);
	}
	tmp = GetArgVal("UnrealBots");
	if (tmp != "")
	{
		bExportUnrealBots = bool(tmp);
	}
	gotoState('running','Running');
}

function ReceivedStopRec()
{
	ConsoleCommand("stopdemo", True);
	sendLine("RECEND");
}

function ReceivedTrace()
{
	local bool boolResult;
	local vector v, v2, HitLocation, HitNormal;
	local string target, unrealId;

	if (GetArgVal("From") == "")
	{
		return;
	}else
	{
		ParseVector(v,"From");
	}
	if (GetArgVal("To") == "")
	{
		return;
	}
	ParseVector(v2,"To");
	boolResult = bool(GetArgVal("TraceActors"));
	target = GetArgVal("Id");

	tempActor = Trace(HitLocation,HitNormal,v2,v,boolResult, , );
	if (tempActor == None)
		boolResult = false;
	else
		boolResult = true;

	SendLine("TRC {Id " $ target $
		"} {From " $ v $
		"} {To " $ v2 $
		"} {Result " $ boolResult $
		"} {HitId " $ GetUniqueId(tempActor) $
		"} {HitLocation " $ HitLocation $
		"} {HitNormal " $ HitNormal $
		"}");
}

function SendNotifyConf(RemoteBot theBot)
{
	local string outstring;
	local string confchId;

	confchId = GetUniqueId(theBot) $ "_CONFCH";

	outstring="CONFCH {Id " $ confchId $
		"} {BotId " $ GetUniqueId(theBot) $
		"} {ManualSpawn " $ !theBot.bAutoSpawn $
		"} {AutoTrace " $ theBot.bAutoTrace $
		"} {Invulnerable " $ theBot.bGodMode $
		"} {Name " $ theBot.PlayerReplicationInfo.PlayerName $
		"} {SpeedMultiplier " $ theBot.SpeedMultiplier $
		"} {RotationRate " $ theBot.RotationRate $
		"} {VisionTime " $ theBot.myConnection.VisionTime $
		"} {LocUpdateMultiplier " $ theBot.myConnection.locUpdateMultiplier $
		"} {ShowDebug " $ theBot.bDebug $
		"} {ShowFocalPoint " $ theBot.bShowFocalPoint $
		"} {DrawTraceLines " $ theBot.bDrawTraceLines $
		"} {SynchronousOff " $ theBot.myConnection.bSynchronousMessagesOff $
		"} {AutoPickupOff " $ theBot.bDisableAutoPickup $
		"} {SyncNavpoints " $ theBot.myConnection.bSynchronousNavPoints $
		"} {VisionFOV " $ theBot.FovAngle $
		"}";


	//Notify bot about his variables changing
	theBot.myConnection.SendLine(outstring);

	//Notify all control servers about bot variables chaning
	GlobalSendLine(outstring,true,false,false);
}

function SendNotifyPause ( bool bGamePaused )
{
	if (bGamePaused) {
		GlobalSendLine("PAUSED",true,true,true);
		GlobalSendLine(GetGameInfo(),true,true,true);
	}
	else {
		GlobalSendLine("RESUMED",true,true,true);
		GlobalSendLine(GetGameInfo(),true,true,true);
	}
}

function BroadcastTextMessage( string Id, coerce string Msg, int TeamIndex, bool bGlobal)
{
	local Controller C;

	if (Id != "") {
		for (C = Level.ControllerList; C != none; C=C.NextController) {
			if ( GetUniqueId(C) == Id) {
				if (C.IsA('RemoteBot'))
					RemoteBot(C).RemoteNotifyTeamMessage(self,"Private:"$Msg);
				if (C.IsA('PlayerController'))
					PlayerController(C).TeamMessage(BotDeathMatch(Level.Game).LevelPauserFeed, GetUniqueId(self)$":Private:"$Msg, '');
       			return;
			}
		}
		//If we haven't found Id or Id doesn't belong to RemoteBot we will end
		return;
	}

	if ( bGlobal ) {
	    //Send the message to RemoteBots
		for( C = Level.ControllerList; C != none; C = C.NextController ) {
			if( C.isA('RemoteBot')) {
				RemoteBot(C).RemoteNotifyClientMessage(self , Msg);
			} else if (C.IsA('ObservedPlayer')) {
			    ObservedPlayer(C).ReceivedGlobalMessage(self, Msg);
				Level.Game.BroadcastHandler.BroadcastText(BotDeathMatch(Level.Game).LevelPauserFeed, PlayerController(C), Msg);
			} else if (C.IsA('PlayerController')) {
				Level.Game.BroadcastHandler.BroadcastText(BotDeathMatch(Level.Game).LevelPauserFeed, PlayerController(C), Msg);
			}
		}
	    return;
	}

	if (Level.Game.bTeamGame) {
		//Send the message to RemoteBots
		for( C = Level.ControllerList; C != None; C = C.NextController ) {
			if( C.PlayerReplicationInfo.Team != none ) {
				if (C.PlayerReplicationInfo.Team.TeamIndex == TeamIndex) {
					if( C.isA('RemoteBot')) {
						RemoteBot(C).RemoteNotifyTeamMessage(self, Msg);
					} else if (C.IsA('ObservedPlayer')) {
				    	ObservedPlayer(C).ReceivedTeamMessage(self, Msg);
						Level.Game.BroadcastHandler.BroadcastText(BotDeathMatch(Level.Game).LevelPauserFeed, PlayerController(C), Msg);
					} else if (C.IsA('PlayerController')) {
						Level.Game.BroadcastHandler.BroadcastText(BotDeathMatch(Level.Game).LevelPauserFeed, PlayerController(C), Msg);
					}
				}
			}
		}
		return;
	}
}

//called from BotDeathMatch when player presses a button
function PlayerKeyEvent(PlayerController PC, Interactions.EInputKey key, Interactions.EInputAction action) {
	local string outstring;

	if (bExportKeyEvents) {
    	outstring = "KEYEVENT {PlayerId " $ GetUniqueId(PC) $
			"} {PlayerName " $ PC.PlayerReplicationInfo.PlayerName $
	        "} {Key " $ Mid( GetEnum(enum'EInputKey', key), 3 ) $
    	    "} {Action " $ Caps(Mid( GetEnum(enum'EInputAction', action), 4)) $ "}";

		if (PC.ViewTarget != none) {
			outstring = outstring $ " {ViewTarget " $ GetUniqueId(PC.ViewTarget) $ "}";
		}

		SendLine(outstring);
	}
}

function HandlePlayerActorSelect(PlayerController pc, Actor inputActor, vector HitLocation) {
	local string outstring;

	outstring = "SEL {PlayerId " $ GetUniqueId(pc) $
		"} {PlayerName " $ pc.PlayerReplicationInfo.PlayerName $
		"} {ObjectId " $ GetUniqueId(inputActor) $
		"}";

	if (inputActor != none) {
		outstring $= " {ObjectLocation " $ inputActor.Location $
			"} {ObjectHitLocation " $ HitLocation $
			"}";
	}
	SendLine(outstring);
}

//from CheatManager class - for inspiration
/*
function Summon( string ClassName )
{
	local class<actor> NewClass;
	local vector SpawnLoc;

	//if (!areCheatsEnabled()) return;

	log( "Fabricate " $ ClassName );
	NewClass = class<actor>( DynamicLoadObject( ClassName, class'Class' ) );
	if( NewClass!=None )
	{
		if ( Pawn != None )
			SpawnLoc = Pawn.Location;
		else
			SpawnLoc = Location;
		Spawn( NewClass,,,SpawnLoc + 72 * Vector(Rotation) + vect(0,0,1) * 15 );
	}
	ReportCheat("Summon");
}
*/

//Default State
auto state waiting
{
Begin:
	sleep(5.0);
	goto 'Begin';
}

//State for receiving commands
state running
{
Begin:
	sleep(5.0);
	goto 'Begin';
Waiting:
	sleep(1.0);
	SendLine("ALIVE {Time " $ Level.TimeSeconds $ "}");
	goto 'Waiting';
Running:
	SendLine("ALIVE {Time " $ Level.TimeSeconds $ "}");
	SendLine("BEG {Time " $ Level.TimeSeconds $"}");
	ExportGameStatus();
	ExportFlagInfo();
	ExportDomPointInfo();
	ExportPlayers();
	SendLine("END {Time " $ Level.TimeSeconds $"}");
	sleep(UpdateTime);
	goto 'Running';
}

state checkingPassword
{
Begin:
Waiting:
	sleep(5.0);
	goto 'Waiting';
}

defaultproperties
{
	UpdateTime=0.3
    bAllowPause=True

	bExportGameInfo=true
	bExportMutators=true
	bExportITC=true
	bExportNavPoints=true
	bExportMovers=true
	bExportInventory=true
	bExportPlayers=true
	bExportKeyEvents=true
}
