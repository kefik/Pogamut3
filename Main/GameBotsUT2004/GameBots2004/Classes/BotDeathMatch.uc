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


//=============================================================================
// DeathMatchPlus.
//=============================================================================
class BotDeathMatch extends DeathMatch
	config(GameBots2004);


#exec OBJ LOAD FILE=TeamSymbols_UT2003.utx
#exec OBJ LOAD FILE=TeamSymbols.utx				// needed right now for Link symbols, etc.
#exec OBJ LOAD File=AnnouncerMain.uax


var ControlServer	theControlServer;
var config bool		bAllowControlServer;
var BotServer		theBotServer;
var bool		bServerLoaded, bBoolResult;
var RemoteBotInfo	RemoteBotConfig;
var int			NumRemoteBots;
var string GameClass;

var config string RemoteBotController;

var config string BotServerClass;

var config string HudMutatorClass;

var config string ControlServerClass;

var ObservingServer theObservingServer;
var config bool bAllowObservingServer;
var config string ObservingServerClass;

//this is ID counter that is used for objects in UnrealScript that doesn't have
//exportable unique ID - (dropped weapons, vehicles or projectiles)
var int GameBotsID;

//This is used for logging used ports for bot and control server
var FileLog Logger;
var config string PortsLog;

var config int BotServerPort;
var config int ControlServerPort;
var config int ObservingServerPort;

var class<RemoteBot> BotClass;
var class<GBxPlayer> PlayerClass;

//if true we will pick some random free ports for bot and control server and write
//them down to a file in UserLog directory (name is set in LoggerFileName)
var config bool bRandomPorts;

//enables vehicles in the game
var config bool bVehiclesEnabled;

//if set to true all weapons from map will be erased
var bool bShouldEraseAllWeapons;

//here we store if our connections to bot and cotrol server are protected by pass
//will force a bit changed initial protocol (to check password)
var bool bPasswordProtected;

//here we store Password for bot and control connections
var string Password;

//who initiated the password protection
var string PasswordByIP;

// This class is used for pausing the game. We supply it to Level.Pauser variable.
var PauserFeed LevelPauserFeed;
//Here we store all available maps
var array<string> Maps;

/** Our custom game replication info */
var GBGameReplicationInfo myGBGameReplicationInfo;

//will be filled up with all movers in the level
var array<Mover> MoverArray;
//will be filled up with all inv spots in the level
var array<InventorySpot> InvSpotArray;
//will be filled up with all doors in the level
var array<Door> DoorArray;
//will be filled up with all dom points in the level
var array<xDomPoint> xDomPointArray;
//will be filled up with all lift centers in the level
var array<LiftCenter> LiftCenterArray;


//movers are static, we will put all movers to the dynamic array, this
//significantly decrease the time we need to process them all in checkVision() fc
//the same holds for navigation points
function initStaticObjectsArrays()
{
	local Mover M;
	local NavigationPoint N;

	foreach AllActors(class'Mover',M) {
		MoverArray[MoverArray.Length] = M;
    }

    for ( N=Level.NavigationPointList; N!=None; N=N.NextNavigationPoint ) {
		if(N.IsA('xDomPoint')) {
			xDomPointArray[xDomPointArray.Length] = xDomPoint(N);
		} else if (N.IsA('InventorySpot')) {
			InvSpotArray[InvSpotArray.Length] = InventorySpot(N);
		} else if (N.IsA('Door')) {
			DoorArray[DoorArray.Length] = Door(N);
		} else if (N.IsA('LiftCenter')) {
			LiftCenterArray[LiftCenterArray.Length] = LiftCenter(N);
		}
	}
}

//this function is called even before PreBeginPlay, we parse GB parameters from
//command line here
event InitGame(string Options, out string Error )
{
	local string InOpt;
	local Mutator M;
	local bool bFound;
	local class<Mutator> mutclass;

	super.InitGame(Options, Error);

	//Make sure the GBHUDMutator is on!
	bFound = false;
	mutclass = class<Mutator>(DynamicLoadObject(HudMutatorClass, class'Class'));
	for (M = BaseMutator; M != None; M = M.NextMutator)
	{
    	if (M.IsA(mutclass.Name))
    	{
    		bFound = true;
			break;
		}

	}
	if (!bFound) {
		AddMutator(HudMutatorClass);
	}

	BotServerPort = Clamp(GetIntOption( Options, "BotServerPort", BotServerPort ),2000,32000);
	ControlServerPort = Clamp(GetIntOption( Options, "ControlServerPort", ControlServerPort ),2000,32000);
	ObservingServerPort = Clamp(GetIntOption( Options, "ObservingServerPort", ObservingServerPort ), 2000, 32000);

	InOpt = ParseOption( Options,"bRandomPorts");
	if ( InOpt != "" )
		bRandomPorts = bool(InOpt);

	InOpt = ParseOption( Options,"PortsLog");
	if ( InOpt != "" )
		PortsLog = InOpt;

	InOpt = ParseOption( Options, "Password");
	if (InOpt != "")
	{
	    bPasswordProtected = true;
	    PasswordByIP = Level.GetAddressURL();
	    Password = InOpt;
	}
	else
	{
		bPasswordProtected = false;
	}

}

function SetGameMessage(string message, float time) {
	myGBGameReplicationInfo.SetGameMessage(message);
	myGBGameReplicationInfo.SetGameMessageTime(time);
}

//called from GBxPlayer when player press a button
function PlayerKeyEvent(PlayerController PC, Interactions.EInputKey key, Interactions.EInputAction action) {
	local GBClientClass G;

	if (theControlServer != none)
	{
		for (G = theControlServer.ChildList; G != None; G = G.Next )
		{
			if (ControlConnection(G).IsInState('running'))
				ControlConnection(G).PlayerKeyEvent(PC, key, action);
		}
	}
}

//This function is automaticaly called after beginning of the game
function PostBeginPlay()
{
	local GameInfo FoundGameInfo; //for vehicle support

	local string portInfoString;

	Super.PostBeginPlay();

	myGBGameReplicationInfo = Spawn(class'GBGameReplicationInfo',self);

	if(!bServerLoaded)
    {
		if (bAllowControlServer)
        {
			theControlServer = spawn(class<ControlServer>(DynamicLoadObject(ControlServerClass, class'class')),self);
		}

		if (bAllowObservingServer)
		{
		    theObservingServer = spawn(class<ObservingServer>(DynamicLoadObject(ObservingServerClass, class 'Class')), self);
		    PlayerControllerClassName = "GameBots2004.ObservedPlayer";
		    RemoteBotController = "GameBots2004.ObservedRemoteBot";
		}

		BotClass = class<RemoteBot>(DynamicLoadObject(RemoteBotController, class'class'));
		PlayerClass = class<GBxPlayer>(DynamicLoadObject(PlayerControllerClassName, class'Class'));

		theBotServer = Spawn(class<BotServer>(DynamicLoadObject(BotServerClass, class'Class')),self);

		bServerLoaded = true;
	}
	else
	{
		theBotServer.bClosed = false;
		theBotServer.Listen();

		if (theControlServer != none)
		{
			theControlServer.bClosed = false;
			theControlServer.Listen();
		}

        if (theObservingServer != none)
        {
		     theObservingServer.bClosed = false;
    	     theObservingServer.Listen();
        }
	}

	//init static objects arrays for faster browsing for checkVision fc in RemoteBot class.
	initStaticObjectsArrays();
	//HACK? Set to true, so the match will start imediatelly
	//We should implement support for StartMatch function...
    bQuickStart = true;

    //Vehicle support
    if (bVehiclesEnabled)
		foreach DynamicActors(Class'Engine.Gameinfo', FoundGameInfo) // iterates though all dynamic actors that are 'gameinfos'
		{                                                    // and stores a ref to them in  FoundGameInfo
			if (FoundGameInfo.bAllowVehicles == false)
				FoundGameInfo.bAllowVehicles = True;           // Sets the value FoundGameInfo.bAllowVehicles to true

		}

    GameBotsID = 0;
	RemoteBotConfig = Spawn(class'RemoteBotInfo');//spawned however not used right now
	LevelPauserFeed = Spawn(class'PauserFeed');
	LoadMapsFromPrefix(MapPrefix);
	RemoteBotConfig.Difficulty = AdjustedDifficulty;
	//Commented, not compatible with linux. Passing message to console
	//LogPorts();
	Log("GB server on.");
	portInfoString = "BotServerPort:" $ theBotServer.ListenPort;
	if (theControlServer != none)
		portInfoString = portInfoString $ " ControlServerPort:" $ theControlServer.ListenPort;
	if (theObservingServer != none)
		portInfoString = portInfoString $ " ObservingServerPort:" $ theObservingServer.ListenPort;
	Log(portInfoString);
	//Test();

}

//we override this to disable accepting of connections when game is ending
function EndGame(PlayerReplicationInfo Winner, string Reason )
{
	log("In EndGame");
	if (theBotServer.LinkState == STATE_Listening) {
		theBotServer.bClosed = true;
		theBotServer.Close();
	}

	if (theControlServer != none && theControlServer.LinkState == STATE_Listening) {
		theControlServer.bClosed = true;
		theControlServer.Close();
	}

	if (theObservingServer != none && theObservingServer.LinkState == STATE_Listening) {
		theObservingServer.bClosed = true;
		theObservingServer.Close();
	}

	Super.EndGame(Winner,Reason);
}

//When we need to tell something to all
function GlobalSendLine(string Text, bool bNotifyAllControlServers, bool bNotifyAllObservers, bool bNotifyAllBots, optional bool bNoCRLF)
{
	local GBClientClass G;

	if ((theControlServer != none) && bNotifyAllControlServers)
	{
		for (G = theControlServer.ChildList; G != None; G = G.Next )
		{
			if (ControlConnection(G).IsInState('running'))
				G.SendLine(Text, bNoCRLF);
		}
	}

	if (bNotifyAllBots)
	{
		for (G = theBotServer.ChildList; G != None; G = G.Next )
		{
		    if ((BotConnection(G).theBot != none) && BotConnection(G).IsInState('monitoring'))
				G.SendLine(Text, bNoCRLF);
		}
	}

	if ((theObservingServer != none) && bNotifyAllObservers) {
		for (G = theObservingServer.ChildList; G != None; G = G.Next )
		{
			if (ObservingConnection(G).IsInState('observing'))
				G.SendLine(Text, bNoCRLF);
		}
	}
}

function Test()
{
	local AIMarker A;
	local UnrealScriptedSequence U;

	ForEach AllActors( class 'AIMarker', A,  )
	{
		log("Ai marker is "$A);
		log("rotation is "$A.Rotation);
	}

	ForEach AllActors( class 'UnrealScriptedSequence', U,  )
	{
		log("Sequence is "$U);
		log("rotation is "$U.Rotation);
		log("AI marker rotation is "$U.myMarker.Rotation);
	}

}

function StartMatch()
{
	local UTWeaponPickup A;

	if (bShouldEraseAllWeapons == true)
	{
		ForEach DynamicActors( class 'xWeapons.UTWeaponPickup', A,  )
		{
			A.GotoState('Disabled');
		}
	}

	super.StartMatch();

}

//Here we store information about our game server into special file
function LogPorts()
{
	Logger = Spawn(class'Engine.FileLog',self);
	Logger.OpenLog(PortsLog);

	Logger.Logf(Level.Year$"/"$Level.Month$"/"$Level.Day$" "$Level.Hour$":"$Level.Minute$":"$Level.Second);
	Logger.Logf(Level.GetAddressURL());
	Logger.Logf("BotServerPort: "$theBotServer.ListenPort);

	if (theControlServer != None)
		Logger.Logf("ControlServerPort: "$theControlServer.ListenPort);
	else
		Logger.Logf("ControlServerPort: None");

	if (theObservingServer != none) {
		Logger.Logf("ObservingServerPort: " $ theObservingServer.ListenPort);
	} else {
		Logger.Logf("ObservingServerPort: None");
	}

	Logger.Logf("//------------------------");
	Logger.CloseLog();
	Logger.Destroy();

	ConsoleCommand("flush", True);

}

//This function returns text information about game
function string GetGameInfo()
{
	local string outStr;

	outStr = " {FragLimit " $ GoalScore $
		"} {TimeLimit " $ TimeLimit $ "}";

	return outStr;
}

//Returns game status - list of players and their score
function SendGameStatus(GBClientClass requester)
{
	local Controller C;

	for ( C=Level.ControllerList; C!=None; C=C.NextController )
	{
		if( (C.IsA('RemoteBot') || C.IsA('GBxPlayer') || C.IsA('GBxBot')) && !C.IsA('Spectator') )
		{
			requester.SendLine("PLS {Id " $ requester.GetUniqueId(C) $
				"} {Score " $ int(C.PlayerReplicationInfo.Score) $
				"} {Deaths " $ int(C.PlayerReplicationInfo.Deaths) $
				"}");
		}
	}
}

function LoadMapsFromPreFix(string Prefix)
{
   local string FirstMap,NextMap,MapName,TestMap;
   local int i, z;

   FirstMap = Level.GetMapName(PreFix, "", 0);
   NextMap = FirstMap;
   i = 0;
   while(!(FirstMap ~= TestMap))
   {
      MapName = NextMap;
      z = InStr(Caps(MapName), ".UT2");
      if(z != -1)
         MapName = Left(MapName, z);  // remove ".UT2"

      Maps[i] = MapName;

      NextMap = Level.GetMapName(PreFix, NextMap, 1);
      TestMap = NextMap;
      i += 1;
   }
}

//Used for restarting our RemoteBots
function RemoteRestartPlayer( Controller aPlayer, optional vector startLocation, optional rotator startRotation )
{
	log("We are in RemoteRestartPlayer");

	SpawnPawn( RemoteBot(aPlayer), startLocation, startRotation);

}

//Rewriten so we can set the position and respawn playercontrollers
function RestartPlayer(Controller aPlayer)
{

    local NavigationPoint startSpot;
    local int TeamNum;
    local class<Pawn> DefaultPlayerClass;
	local Vehicle V, Best;
	local vector ViewDir;
	local float BestDist, Dist;

	//for remote bot we have different function (SpawnPawn)
	if (aPlayer.IsA('RemoteBot'))
		return;

	if (aPlayer.IsA('GBxPlayer'))
	{

	    if( bRestartLevel && Level.NetMode!=NM_DedicatedServer && Level.NetMode!=NM_ListenServer )
        	return;

	    if ( (aPlayer.PlayerReplicationInfo == None) || (aPlayer.PlayerReplicationInfo.Team == None) )
    	    TeamNum = 255;
	    else
    	    TeamNum = aPlayer.PlayerReplicationInfo.Team.TeamIndex;

		if (GBxPlayer(aPlayer).NextStartLocation == vect(0,0,0))
		{
		    startSpot = FindPlayerStart(aPlayer, TeamNum);
		    if( startSpot == None )
		    {
        		log(" Player start not found!!!");
		        return;
		    }
		}

		if (aPlayer.PreviousPawnClass!=None && aPlayer.PawnClass != aPlayer.PreviousPawnClass)
	        BaseMutator.PlayerChangedClass(aPlayer);

    	if ( aPlayer.PawnClass != None )
    	{
    		if (GBxPlayer(aPlayer).NextStartLocation != vect(0,0,0))
    		{
	        	aPlayer.Pawn = Spawn(aPlayer.PawnClass,,,GBxPlayer(aPlayer).NextStartLocation,GBxPlayer(aPlayer).NextStartRotation);
	        	GBxPlayer(aPlayer).NextStartLocation = vect(0,0,0);
	        	GBxPlayer(aPlayer).NextStartRotation = rot(0,0,0);
	        }
	        else
		        aPlayer.Pawn = Spawn(aPlayer.PawnClass,,,StartSpot.Location,StartSpot.Rotation);
        }

	    if( aPlayer.Pawn==None )
    	{
        	DefaultPlayerClass = GetDefaultPlayerClass(aPlayer);
    		if (GBxPlayer(aPlayer).NextStartLocation != vect(0,0,0))
    		{
	        	aPlayer.Pawn = Spawn(DefaultPlayerClass,,,GBxPlayer(aPlayer).NextStartLocation,GBxPlayer(aPlayer).NextStartRotation);
	        	GBxPlayer(aPlayer).NextStartLocation = vect(0,0,0);
	        	GBxPlayer(aPlayer).NextStartRotation = rot(0,0,0);
	        }
	        else
		        aPlayer.Pawn = Spawn(DefaultPlayerClass,,,StartSpot.Location,StartSpot.Rotation);
    	}
	    if ( aPlayer.Pawn == None )
    	{
    		if (StartSpot != none)
	        	log("Couldn't spawn player of type "$aPlayer.PawnClass$" at "$StartSpot);
	        aPlayer.GotoState('Dead');
	        if ( PlayerController(aPlayer) != None )
				PlayerController(aPlayer).ClientGotoState('Dead','Begin');
        	return;
    	}

	    if ( PlayerController(aPlayer) != None )
			PlayerController(aPlayer).TimeMargin = -0.1;
		if (StartSpot != none)
		{
		    aPlayer.Pawn.Anchor = startSpot;
			aPlayer.Pawn.LastStartSpot = PlayerStart(startSpot);
		}
		aPlayer.Pawn.LastStartTime = Level.TimeSeconds;
    	aPlayer.PreviousPawnClass = aPlayer.Pawn.Class;

	    aPlayer.Possess(aPlayer.Pawn);
    	aPlayer.PawnClass = aPlayer.Pawn.Class;

	    aPlayer.Pawn.PlayTeleportEffect(true, true);
    	aPlayer.ClientSetRotation(aPlayer.Pawn.Rotation);
	    AddDefaultInventory(aPlayer.Pawn);
   		if (StartSpot != none)
	    	TriggerEvent( StartSpot.Event, StartSpot, aPlayer.Pawn);

	    if ( bAllowVehicles && (Level.NetMode == NM_Standalone) && (PlayerController(aPlayer) != None) )
    	{
			// tell bots not to get into nearby vehicles for a little while
			BestDist = 2000;
			ViewDir = vector(aPlayer.Pawn.Rotation);
			for ( V=VehicleList; V!=None; V=V.NextVehicle )
				if ( V.bTeamLocked && (aPlayer.GetTeamNum() == V.Team) )
				{
					Dist = VSize(V.Location - aPlayer.Pawn.Location);
					if ( (ViewDir Dot (V.Location - aPlayer.Pawn.Location)) < 0 )
						Dist *= 2;
					if ( Dist < BestDist )
					{
						Best = V;
						BestDist = Dist;
					}
				}

			if ( Best != None )
				Best.PlayerStartTime = Level.TimeSeconds + 8;
		}


	}
	else
	{
		super.RestartPlayer(aPlayer);
	}

}

/*
function AddBotToList(RemoteBot newBot)
{
	local int i;

	for (i=0; i < 32; i++)
	{
		if (RemoteBots[i] == none)
			break;
	}
	if (i <= 31)
	{
		RemoteBots[i] = newBot;
	}
}

function RemoveBotFromList(RemoteBot newBot)
{
	local int i;

	for (i=0; i < 32; i++)
	{
		if (RemoteBots[i] == newBot)
		{
		 	RemoteBots[i] = none;
		 	break;
		}
	}
}*/

//Main function for adding bot to the game (creates also controller)
function RemoteBot AddRemoteBot
(
	BotConnection theConnection,
	string clientName,
	int TeamNum,
	optional string className,
	optional string DesiredSkin,
	optional float DesiredSkill,
	optional float DesiredAccuracy,
	optional bool ShouldLeadTarget
)
{
	local RemoteBot NewBot;
	local GBReplicationInfo repInfo;

	//I dont think location here is necessary. Its just controller class
	NewBot = Spawn(BotClass, self);

	if ( NewBot == None )
	{
		log("In AddRemoteBot() - Cant spawn RemoteBot ");
		return None;
	}

	//AddBotToList(NewBot); //Test

	//hook up connection to socket
	NewBot.myConnection = theConnection;

	NewBot.bIsPlayer = true;
	NewBot.bHidden = false;
	//TODO prevent engine to return TransLoc paths?
	NewBot.bTranslocatorHop = false;

	if (DesiredSkin != "")
		NewBot.DesiredSkin = DesiredSkin;
	else
	    NewBot.DesiredSkin = "ThunderCrash.JakobM";

	// Set the player's ID.
	NewBot.PlayerReplicationInfo.PlayerID = CurrentID++;

	// Add custom GBReplicationInfo
	repInfo = class'GBReplicationInfo'.Static.SpawnFor(NewBot.PlayerReplicationInfo);
	repInfo.MyPRI = NewBot.PlayerReplicationInfo;

	//Increase numbers properly, so no epic bot will join the game
	MinPlayers = Max(MinPlayers+1, NumPlayers + NumBots + 1);
	NumRemoteBots++;
	NumPlayers++;

	if ( clientName != "" )
	{
		NewBot.PlayerReplicationInfo.PlayerName = clientName;
		changeName( newBot, clientName, true );
	}

	if (!AddRemoteBotToTeam(NewBot,TeamNum))
	{
		log("In AddRemoteBot() - In Team Game Bot without team!");
		//NewBot.Destroy();
		//return None;
	}

	//Here we set values of the bot difficulty, it affects how good he will be
    //TODO: Really here?! But where then?
    //Maximum is 1.0
    if ( (DesiredAccuracy > 0) && (DesiredAccuracy <= 1) )
		Newbot.Accuracy = DesiredAccuracy;
	else
		NewBot.Accuracy = 1.0;

	//Turns on strafing ability
	Newbot.StrafingAbility = 1.0;
    //From 0 to 7
    if ((DesiredSkill > 0) && (DesiredSkill <= 7))
    	Newbot.Skill = DesiredSkill;
    else
		Newbot.Skill = 7;
	//Shooting ahead of targets - disabled? - aiming is thing of a client
	NewBot.bLeadTarget = ShouldLeadTarget;

	//Need to Spawn PawnClass, default should work , but for now we will be using just xPawn
	if(className != "")
	{
		NewBot.PawnClass = class<xPawn>(DynamicLoadObject(className, class'Class'));
	}
	else
	{
		if(newBot.PawnClass == None)
		{
			log("newBot.PawnClass is None. Using GBxPawn.");
			newBot.PawnClass = class<xPawn>(DynamicLoadObject("GameBots2004.GBxPawn", class'Class'));
		}
	}

	//We will let the bots know that new bots came to server
	RemoteNotifyLoging(newBot);

	return NewBot;
}

//Here we spawn and respawn the bot Pawn - thats the visible avatar of the bot
function SpawnPawn
(
	RemoteBot NewBot,
	optional vector startLocation,
	optional rotator startRotation
)
{
	local NavigationPoint startSpot;

	if (NewBot == None)
	{
    	log("In SpawnPawn(), - NewBot is None! ");
    	return;
	}

	if (NewBot.Pawn != None)
	{
		log("In SpawnPawn(), - "$NewBot$" Pawn already spawned ");
		return;
	}

	if ( StartLocation == vect(0,0,0) )
	{
		StartSpot = FindPlayerStart(NewBot);
    	if( startSpot != None )
	    	newBot.Pawn = Spawn(NewBot.PawnClass,newBot,,StartSpot.Location,StartSpot.Rotation);
	    else
	        log(" Player start not found! for:"$NewBot);
	}
	else
	{
		if (StartRotation != rot(0,0,0))
		{
			NewBot.Pawn = Spawn(NewBot.PawnClass,newBot,,StartLocation,StartRotation);
		}
		else
		{
			newBot.Pawn = Spawn(NewBot.PawnClass,newBot,,StartLocation, );
		}

	}

	if (NewBot.Pawn == None)
	{
		log("In SpawnPawn() - Cant spawn the pawn of bot "$NewBot$" Somebody on startspot?");
    	//log("In SpawnPawn() - Cant spawn the pawn of bot "$NewBot$" setting autospawn false");
    	//NewBot.bAutoSpawn = false;
    	//notifying about the config change
    	//NewBot.myConnection.SendNotifyConf();
		return;
	}


	//For disabling automatic pickup (items picked through command PICK)
    newBot.Pawn.bCanPickupInventory = !newBot.bDisableAutoPickup;

    //Multiply Pawn GroundSpeed by our custom GB SpeedMultiplier
    newBot.Pawn.GroundSpeed = newBot.SpeedMultiplier * newBot.Pawn.Default.GroundSpeed;
    newBot.Pawn.AirSpeed = newBot.SpeedMultiplier * newBot.Pawn.Default.AirSpeed;
    newBot.Pawn.WaterSpeed = newBot.SpeedMultiplier * newBot.Pawn.Default.WaterSpeed;
    newBot.Pawn.LadderSpeed = newBot.SpeedMultiplier * newBot.Pawn.Default.LadderSpeed;

    //set Pawn rotation rate to our custom set RotationRate
    newBot.Pawn.RotationRate = newBot.RotationRate;

	GBxPawn(NewBot.Pawn).SetSkin(NewBot.DesiredSkin);//, NewBot.myConnection.BotSkin);
	newBot.Pawn.Controller = newBot;

	//Taken from Spawning class of Epic bots
	if (StartSpot != none) //TODO: Find here nearest NavPoint?
	{
    	NewBot.Pawn.Anchor = startSpot;
		NewBot.Pawn.LastStartSpot = PlayerStart(startSpot);
	}
	NewBot.Pawn.LastStartTime = Level.TimeSeconds;
    NewBot.PreviousPawnClass = NewBot.Pawn.Class;

    NewBot.Possess(NewBot.Pawn);
    NewBot.PawnClass = NewBot.Pawn.Class;

    NewBot.Pawn.PlayTeleportEffect(true, true);

	//Notify spawning
	NewBot.myConnection.SendLine("SPW");

    //NewBot.ClientSetRotation(aPlayer.Pawn.Rotation); //We want to preserve our rotation
    if (!bShouldEraseAllWeapons)
    	AddDefaultInventory(NewBot.Pawn);

    if (StartSpot != none)
    	TriggerEvent( StartSpot.Event, StartSpot, NewBot.Pawn );

   	//Setting some initial Pawn properties
	Newbot.Pawn.PeripheralVision = -0.3;
	Newbot.Pawn.bAvoidLedges = false;

	Newbot.Pawn.bStopAtLedges = false;
	Newbot.Pawn.bCanJump = true;
	NewBot.Pawn.DeactivateSpawnProtection(); //to prevent bot being unkillable?
	//NewBot.Pawn.b

 	// broadcast a welcome message.
	BroadcastLocalizedMessage(GameMessageClass, 1, NewBot.PlayerReplicationInfo);

	NewBot.GotoState('StartUp', 'DoStop'); //TODO: Really here?
}

function bool AddRemoteBotToTeam(Bot NewBot, int TeamNum)
{
	local UnrealTeamInfo BotTeam;

	BotTeam = GetBotTeam();
	BotTeam.AddToTeam(NewBot);

	return true;
}

//This function is called, when somebody on the server dies.
function Killed( Controller Killer, Controller Killed, Pawn KilledPawn, class<DamageType> damageType )
{
	local Controller C;

	Super.Killed(Killer, Killed, KilledPawn, damageType);

	//Send DIE message to killed bot
	if( (Killed != none)) {
		if (Killed.isA('RemoteBot')) {
			RemoteBot(Killed).RemoteDied(Killer, damageType);
		}
		if (Killed.IsA('ObservedPlayer')) {
			ObservedPlayer(Killed).RemoteDied(Killer, damageType);
		}
    }

	//Send KIL message to other bots
	for(C = Level.ControllerList; C != none; C = C.NextController)
	{
		if( C.isA('RemoteBot') && C != Killed )
		{
			RemoteBot(C).RemoteKilled(Killer, Killed, KilledPawn, damageType);
		}
		if (C.IsA('ObservedPlayer') && C != Killed) {
            ObservedPlayer(C).Killed(Killer, Killed, KilledPawn, damageType);
		}
	}
}

//We get a team for our epic bot here
//this function should not be called in BotDeathMatch
function UnrealTeamInfo GetEpicBotTeam(optional int TeamNumber)
{
	log("EpicGetBotTeam() - In BotDeathMatch - should not be called");
	return None;
}

//Next two functions handles spawning of epic bot, sometimes we could want them
//running in our game
//A lot of code collected from a lot of ingame classes acros the game types and so
function bool AddEpicBot(
	optional string BotName,
	optional int TeamNumber,
	optional vector StartLocation,
	optional rotator StartRotation,
	optional float skill
)
{
	local Bot NewBot;
	local RosterEntry Chosen;
	local UnrealTeamInfo BotTeam;
	local GBReplicationInfo repInfo;

	MinPlayers = Max(MinPlayers+1, NumPlayers + NumBots + 1);

	if ( Level.Game.IsA('BotTeamGame') )
	{
		BotTeam = GetEpicBotTeam(TeamNumber);
	}
	else
	{
		BotTeam = GetBotTeam();
	}

	Chosen = BotTeam.ChooseBotClass("");

	if (Chosen.PawnClass == None)
		Chosen.Init(); //amb
	// log("Chose pawn class "$Chosen.PawnClass);
	NewBot = Spawn(class'GBxBot');

	if ( NewBot != None )
	{

		if (skill > 0 && skill <= 7)
			NewBot.InitializeSkill(skill);
		else
			NewBot.InitializeSkill(AdjustedDifficulty);
		Chosen.InitBot(NewBot);

		BotTeam.AddToTeam(NewBot);


		if ( BotName != "" )
			ChangeName(NewBot, BotName, false);
		else
			ChangeName(NewBot, Chosen.PlayerName, false);

		NewBot.StrafingAbility = 1;
		if ( bEpicNames && (NewBot.PlayerReplicationInfo.PlayerName ~= "The_Reaper") )
		{
			NewBot.Accuracy = 1;
			NewBot.StrafingAbility = 1;
			NewBot.Tactics = 1;
			NewBot.InitializeSkill(AdjustedDifficulty+2);
		}
		BotTeam.SetBotOrders(NewBot,Chosen);
	}

	if ( NewBot == None )
	{
		warn("Failed to spawn bot.");
		return false;
	}

	// broadcast a welcome message.
	BroadcastLocalizedMessage(GameMessageClass, 1, NewBot.PlayerReplicationInfo);

	NewBot.PlayerReplicationInfo.PlayerID = CurrentID++;

	// Add custom GBReplicationInfo
	repInfo = class'GBReplicationInfo'.Static.SpawnFor(NewBot.PlayerReplicationInfo);
	repInfo.MyPRI = NewBot.PlayerReplicationInfo;

	NumBots++;

	RemoteNotifyLoging(newBot);

	SpawnEpicBot(NewBot,StartLocation,StartRotation);

	return true;

}

//Here we spawn a Pawn for our epic bot, it is used just for the first spawning
//restart of the bot is handled by RestartPlayer inherited from DeathMatch
function SpawnEpicBot(Controller aPlayer, optional vector StartLocation, optional rotator StartRotation)
{
	local NavigationPoint startSpot;
	local class<Pawn> DefaultPlayerClass;
	local Vehicle V, Best;
	local vector ViewDir;
	local float BestDist, Dist;

    if( bRestartLevel && Level.NetMode!=NM_DedicatedServer && Level.NetMode!=NM_ListenServer )
        return;

    if (StartLocation == vect(0,0,0))
	{
		startSpot = FindPlayerStart(aPlayer);
		if( startSpot == None )
    	{
        	log(" Player start not found!!!");
        	return;
    	}
		StartLocation = startSpot.Location;
		StartRotation = startSpot.Rotation;
	}


    if (aPlayer.PreviousPawnClass!=None && aPlayer.PawnClass != aPlayer.PreviousPawnClass)
        BaseMutator.PlayerChangedClass(aPlayer);

    if ( aPlayer.PawnClass != None )
        aPlayer.Pawn = Spawn(aPlayer.PawnClass,,,StartLocation,StartRotation);

    if( aPlayer.Pawn==None )
    {
        DefaultPlayerClass = GetDefaultPlayerClass(aPlayer);
        aPlayer.Pawn = Spawn(DefaultPlayerClass,,,StartLocation,StartRotation);
    }
    if ( aPlayer.Pawn == None )
    {
        log("Couldn't spawn player of type "$aPlayer.PawnClass$" at "$StartSpot);
        aPlayer.GotoState('Dead');
        if ( PlayerController(aPlayer) != None )
			PlayerController(aPlayer).ClientGotoState('Dead','Begin');
        return;
    }
    if ( PlayerController(aPlayer) != None )
		PlayerController(aPlayer).TimeMargin = -0.1;

	if (startSpot != none) //TODO: Find nearest navpoint?
	{
    	aPlayer.Pawn.Anchor = startSpot;
		aPlayer.Pawn.LastStartSpot = PlayerStart(startSpot);
	}
	aPlayer.Pawn.LastStartTime = Level.TimeSeconds;
    aPlayer.PreviousPawnClass = aPlayer.Pawn.Class;

    aPlayer.Possess(aPlayer.Pawn);
    aPlayer.PawnClass = aPlayer.Pawn.Class;

    aPlayer.Pawn.PlayTeleportEffect(true, true);
    aPlayer.ClientSetRotation(aPlayer.Pawn.Rotation);

    if (!bShouldEraseAllWeapons)
	    AddDefaultInventory(aPlayer.Pawn);

    if (startSpot != none)
	    TriggerEvent( StartSpot.Event, StartSpot, aPlayer.Pawn); //startSpot can be None!

    if ( bAllowVehicles && (Level.NetMode == NM_Standalone) && (PlayerController(aPlayer) != None) )
    {
		// tell bots not to get into nearby vehicles for a little while
		BestDist = 2000;
		ViewDir = vector(aPlayer.Pawn.Rotation);
		for ( V=VehicleList; V!=None; V=V.NextVehicle )
			if ( V.bTeamLocked && (aPlayer.GetTeamNum() == V.Team) )
			{
				Dist = VSize(V.Location - aPlayer.Pawn.Location);
				if ( (ViewDir Dot (V.Location - aPlayer.Pawn.Location)) < 0 )
					Dist *= 2;
				if ( Dist < BestDist )
				{
					Best = V;
					BestDist = Dist;
				}
			}

		if ( Best != None )
			Best.PlayerStartTime = Level.TimeSeconds + 8;
	}
}

//Called when new human player enters the game
event PlayerController Login( string Portal, string Options, out string Error )
{
	local PlayerController Loging;
	local GBReplicationInfo repInfo;

	Loging = super.Login( Portal, Options, Error );

	//Add our custom replication info

	repInfo = class'GBReplicationInfo'.Static.SpawnFor(Loging.PlayerReplicationInfo);
	repInfo.MyPRI = Loging.PlayerReplicationInfo;

	RemoteNotifyLoging(Loging);

	return Loging;
}

function RemoteRestartGameState() {
	local Controller C;
	local NavigationPoint N;
	local InventorySpot I;
	local Pickup P;

	//taken from restart functions
    bGameEnded = false;
    bOverTime = false;
    ElapsedTime = 0;
    RemainingTime = 60 * TimeLimit;

	for ( N=Level.NavigationPointList; N!=None; N=N.NextNavigationPoint )
	{
		if (N.IsA('InventorySpot')) {
        	I = InventorySpot(N);
			if (I.myPickupBase != none) {
				if (I.myPickupBase.myPickUp != none) { //&& !I.myPickupBase.myPickUp.IsInState('Pickup')
					I.myPickupBase.myPickUp.reset();
				}
			} else if (I.markedItem != none) {
				I.markedItem.reset();
				//if (!I.markedItem.IsInState('Pickup') ) {
					//I.markedItem.GotoState('Sleeping','Respawn');
				//}
			}
		}
	}

	for ( C=Level.ControllerList; C!=None; C=C.NextController )
	{
		if (C.PlayerReplicationInfo != none) {
			//disable weapon throw
			if (C.Pawn != none && C.Pawn.Weapon != none) {
				C.Pawn.Weapon.bCanThrow = false;
			}

			if (C.IsA('RemoteBot'))
				RemoteBot(C).RespawnPlayer();
		    if (C.IsA('GBxPlayer')) {
   				GBxPlayer(C).RespawnPlayer();
 				GBxPlayer(C).UpdateRemainingTime(RemainingTime);
   			}
		    if (C.IsA('GBxBot'))
   				GBxBot(C).RespawnPlayer();

			C.PlayerReplicationInfo.Score = 0;
			C.PlayerReplicationInfo.Deaths = 0;
			C.PlayerReplicationInfo.reset();
		}
	}

	foreach DynamicActors(class'Pickup',P)
	{
		if (P.bDropped)
			P.Destroy();
	}

	GameReplicationInfo.RemainingTime = RemainingTime;
	GameReplicationInfo.RemainingMinute = RemainingTime;
	GameReplicationInfo.ElapsedTime = 0;
	GameReplicationInfo.reset();
	myGBGameReplicationInfo.RemainingTime = RemainingTime;
	myGBGameReplicationInfo.RemainingMinute = RemainingTime;
	myGBGameReplicationInfo.RemainingMinute = 0;
	myGBGameReplicationInfo.ElapsedTime = 0;
	myGBGameReplicationInfo.resetGameTime(RemainingTime);
	myGBGameReplicationInfo.reset();

	log("Restarting, TimeLimit:" $ TimeLimit $ ";RemainingTime:" $ RemainingTime);
	PlayStartupMessage();
}

//We send a notification to our remote bots, that new player joined the server
function RemoteNotifyLoging( Controller Loging )
{
	local string outstring;

	outstring = "JOIN {Id " $ Loging$Loging.PlayerReplicationInfo.PlayerID $
		"} {Name " $ Loging.PlayerReplicationInfo.PlayerName $
		"}";

    GlobalSendLine(outstring,true,true,true);

	/*
    for (c = Level.ControllerList; c != none; c = c.nextController) {
        if (c.IsA('ObservedPlayer')) ObservedPlayer(c).PlayerJoined(Loging);
        if (c.IsA('ObservedRemoteBot')) ObservedRemoteBot(c).PlayerJoined(Loging);
    } */
}

//Notification about player leaving the server
function RemoteNotifyLogout(Controller Exiting)
{
	local string outstring;

	outstring = "LEFT {Id " $ Exiting$Exiting.PlayerReplicationInfo.PlayerID $
		"} {Name " $ Exiting.PlayerReplicationInfo.PlayerName $
		"}";

    GlobalSendLine(outstring,true,true,true);

    /*
    for (c = Level.ControllerList; c != none; c = c.nextController) {
        if (c.IsA('ObservedPlayer')) ObservedPlayer(c).PlayerLeft(Exiting);
        if (c.IsA('ObservedRemoteBot')) ObservedRemoteBot(c).PlayerLeft(Exiting);
    }*/
}

//Called when somebody (bot/player) leaves game
function Logout(controller Exiting)
{
	RemoteNotifyLogout(Exiting);
	if(!exiting.IsA('RemoteBot'))
	{
		Super.Logout(Exiting);
	}
	else
	{
		//RemoveBotFromList(RemoteBot(Exiting)); //test
		Super(GameInfo).Logout(Exiting);
		NumRemoteBots--;
		NumPlayers--; //we count RemoteBots as players too
	}
}

//Prevents epic bots from automatically joining the game
function bool NeedPlayers()
{
	return false;
}

//Prevents epic bots from automatically joining the game
function bool BecomeSpectator(PlayerController P)
{
	if ( !Super.BecomeSpectator(P) )
		return false;

	return true;
}

defaultproperties
{
	NetWait=2
	CountDown=0
	bPauseable=True
	DefaultEnemyRosterClass="XGame.xDMRoster"
	PlayerControllerClassName="GameBots2004.GBxPlayer"
	RemoteBotController="GameBots2004.RemoteBot"
    HudMutatorClass="GameBots2004.GBHudMutator"
	BotServerClass="GameBots2004.BotServer"
    ControlServerClass="GameBots2004.ControlServer"
    ObservingServerClass="GameBots2004.ObservingServer"
	bAllowControlServer=True
	bAllowObservingServer=True
	DecoTextName="XGame.DeathMatch"
	ScreenShotName="UT2004Thumbnails.DMShots"
	HUDType="xInterface.HudCDeathmatch"
	GameName="GameBots DeathMatch"
	GameClass="BotDeathMatch"
	Acronym="DM"
	MapPrefix="DM"
	BotServerPort=3000
	ControlServerPort=3001
	ObservingServerPort=3002
	PortsLog="GBPortsLog"
	bVehiclesEnabled=True
}
