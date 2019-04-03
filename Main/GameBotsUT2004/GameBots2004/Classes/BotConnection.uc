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
// BotConnection.
// Based connection class
//=============================================================================
class BotConnection extends GBClientClass
	config(GameBots2004);

//------------Variables---------------------------

// delay between visionUpdates
var config float visionTime;

// how more often we will export loc info
var config int locUpdateMultiplier;

// how many loc updates we have exported from the previous sync. batch
var int locInfoCount;

//whether we had already sent first sync. batch after respawn
var bool bFirstSyncBAfterSpawnExported;

// what is our current sleep time
var float sleepTime;

// how oftern are we sending only LOC UPDATES
var float locUpdateMultiplierCurrent;

//if true we will ignore maximum player limit on the server - note this can lead
//to pawn spawning malfunctions
var config bool bIgnoreMaxPlayers;

// on / off all synchronous messages
var config bool bSynchronousMessagesOff;

//used to store info inside function where local wont work
//set property text dosent seem to work with a local
var bool tempBool;

var() class<Pickup> tempPickupClass;

var() class<SpeciesType> TempSpecies;

//use to set name variables - normaly cant convert string to name
var name tempName;

// the one server and the actual bot we're in control of
var BotServer Parent;
var RemoteBot theBot;

//switches for exporting information after READY command
var config bool bExportGameInfo;
var config bool bExportMutators;
var config bool bExportITC;
var config bool bExportNavPoints;
var config bool bExportMovers;
var config bool bExportInventory;
var config bool bExportPlayers;

var config bool bSynchronousNavPoints;

struct CustomRay{
	var() config string Id;
	var() config rotator Rotation;
	var() config float Length;
	var() config bool FastTrace;
	var() config bool TraceActors;
	var() config bool ProvideFloorCorrection;
	var() config TraceLine VisualizerMiss;
	var() config TraceLine VisualizerHit;
};

//for bot focus visualization
var FocusActorClass FocusActor;
//for bot focus visualization
var CustomBeamWhite FocusActorEmitter;

var Vehicle CarVehicle;
var() class<Vehicle> SpawnVec;

var array<CustomRay> CustomRayList;

var name TraceBone;

/** Time of last Dodge command issued (only 1 dodge per second allowed) */
var float lastDodgeTime;

//export game status int - we export status only every fifth batch
var int egs;

//------------Events---------------------------

// triggered when a socket connection with the server is established
event Accepted()
{
	super.Accepted();

	if(bDebug)
		log("Accepted BotConnection" @ self);

	//log("numpl:" $ Level.Game.NumPlayers $ "max:"$Level.Game.MaxPlayers $ "con:"$Parent.ConnectionCount);
	if (!bIgnoreMaxPlayers &&
		(( Level.Game.NumPlayers >= Level.Game.MaxPlayers) ||
		(Parent.ConnectionCount >= Level.Game.MaxPlayers)) )
	{
		SendLine("HELLO_BOT {ServerFull True}");
		Destroy();
	}
	else
		SendLine("HELLO_BOT");

	if (BotDeathMatch(Level.Game).bPasswordProtected)
		gotoState('checkingPassword','Waiting');
	else
		gotoState('waiting','Waiting');
}

event Closed()
{
	//log("In BotConnection, event Closed()");
	//Destroyed();
	Destroy();
}

simulated event Destroyed()
{
	//log("In BotConnection, event Destroyed()");
	if (theBot != None)
	{
		if (theBot.Pawn != none) {
			theBot.bAutoSpawn = false;
			theBot.DestroyPawn();
		}
		theBot.Destroy();
	}

	if (FocusActor != None)
	{
		FocusActor.Destroy();
	}

	//Destroying autotrace visualizers
	RemoveCustomRay("All");
}


//------------Functions---------------------------

function PostBeginPlay()
{
	Parent = BotServer(Owner);

	if(bDebug)
		log("Spawned BotConnection");
	SaveConfig();

	//init sleeptime here
	sleepTime = visionTime / float(locUpdateMultiplier);
	if (sleepTime < 0.05) sleepTime = 0.05; // UT2004 cannot sleep less then this...
	locUpdateMultiplierCurrent = visionTime / sleepTime;
}

//triggered when weve gotten READY message from client, or after succesfull password check
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
		ExportPlayers(true); //true for limited info, PLR messages
		SendLine("EPLR");
	}
	SendLine("EHS"); // HandShakeEnd message
}

//Init recieved from client
function InitBot()
{
	local string clientName, className, temp, DesiredSkin, outstring;
	local int teamNum;
	local vector StartLocation;
	local rotator StartRotation;
	local float DesiredSkill, DesiredAccuracy;
	local bool ShouldLeadTarget;

	clientName = GetArgVal("Name");

	DesiredSkin = GetArgVal("Skin");

	className = GetArgVal("ClassName");

	temp = GetArgVal("Team");
	if( temp != "" )
		teamNum = int(temp);
	else
		teamNum = 255;

	temp = GetArgVal("DesiredSkill");
	if (temp != "")
		DesiredSkill = float(temp);
	else
		DesiredSkill = -1; //that means deufault value will be used

   	temp = GetArgVal("DesiredAccuracy");
	if (temp != "")
		DesiredAccuracy = float(temp);
	else
		DesiredAccuracy = -1; //that means deufault value will be used

	temp = GetArgVal("ShouldLeadTarget");
	if (temp != "")
		ShouldLeadTarget = bool(temp);
	else
		ShouldLeadTarget = false;

	// add the bot into the game
	theBot = BotDeathMatch(Level.Game).AddRemoteBot(
		self,
		clientName,
		teamNum,
		className,
		DesiredSkin,
		DesiredSkill,
		DesiredAccuracy,
		ShouldLeadTarget
	);

	//Here the spawning of the pawn is handled - after weve got controler created
	if(theBot != None)
	{
		if (GetArgVal("ManualSpawn")!="")
			theBot.bAutoSpawn = !bool(GetArgVal("ManualSpawn"));

		if (GetArgVal("AutoPickupOff") != "")
			theBot.bDisableAutoPickup = bool(GetArgVal("AutoPickupOff"));

		theBot.jmx = GetArgVal("Jmx");

		if (GetArgVal("AutoTrace")!="")
			theBot.bAutoTrace = bool(GetArgVal("AutoTrace"));

		if (theBot.bAutoTrace && CustomRayList.Length == 0)
		{
			AddDefaultRays();
		}

		FocusActor = Spawn(class'FocusActorClass',self,,,);
		FocusActor.bHidden = true;

		// for shooting at the location
		theBot.myTarget = Spawn(class'FocusActorClass',theBot,,,);
		theBot.myTarget.bHidden = true;

		SendNotifyConf();
		//FocusActor.Test();
        if (bDebug)
			log("Succesfully Added bot "$theBot);

        outstring = "INITED {BotId " $ GetUniqueId(theBot) $
       		"} {HealthStart " $ theBot.PawnClass.Default.Health $
       		"} {HealthFull " $ int(theBot.PawnClass.Default.HealthMax) $
       		"} {HealthMax " $ int(theBot.PawnClass.Default.SuperHealthMax) $
       		"} {AdrenalineStart " $ int(thebot.Adrenaline) $
       		"} {AdrenalineMax " $ int(thebot.AdrenalineMax) $
       		"} {ShieldStrengthStart " $ int(class'xPawn'.Default.ShieldStrength) $
       		"} {ShieldStrengthMax " $ int(class'xPawn'.Default.ShieldStrengthMax) $
       		"} {MaxMultiJump " $ class'xPawn'.Default.MaxMultiJump $
       		"} {DamageScaling " $ theBot.PawnClass.Default.DamageScaling $
       		"} {GroundSpeed " $ theBot.PawnClass.Default.GroundSpeed $
       		"} {WaterSpeed " $ theBot.PawnClass.Default.WaterSpeed $
       		"} {AirSpeed " $ theBot.PawnClass.Default.AirSpeed $
       		"} {LadderSpeed " $ theBot.PawnClass.Default.LadderSpeed $
       		"} {AccelRate " $ theBot.PawnClass.Default.AccelRate $
       		"} {JumpZ " $ theBot.PawnClass.Default.JumpZ $
       		"} {MultiJumpBoost " $ class'xPawn'.Default.MultiJumpBoost $
       		"} {MaxFallSpeed " $ theBot.PawnClass.Default.MaxFallSpeed $
       		"} {DodgeSpeedFactor " $ theBot.PawnClass.Default.DodgeSpeedFactor $
       		"} {DodgeSpeedZ " $ theBot.PawnClass.Default.DodgeSpeedZ $
       		"} {AirControl " $ theBot.PawnClass.Default.AirControl $
       		"}";

       	SendLine(outstring);

		if ( GetArgVal("Location")!="" )
		{
        	ParseVector(StartLocation,"Location");

        	if ( GetArgVal("Rotation")!="" )
			{
				ParseRot(StartRotation,"Rotation");
				BotDeathMatch(Level.Game).SpawnPawn(theBot,StartLocation,StartRotation);
			}
			else
			{
				BotDeathMatch(Level.Game).SpawnPawn(theBot,StartLocation, );
			}
		}
		else
		{
			if ( GetArgVal("Rotation")!="" )
			{
				ParseRot(StartRotation,"Rotation");
				BotDeathMatch(Level.Game).SpawnPawn(theBot, ,StartRotation);
			}
		}

		if (theBot.Pawn != none)
			theBot.GotoState('StartUp', 'Begin');
		else
			theBot.GotoState('Dead', 'Begin');

		gotoState('monitoring','Running');
	}
	else
	{
		if (bDebug)
			log("In InitReceived() - Error adding bot");
	}

}

//dont look here, some testing stuff :-)
function DoTest()
{
	//GBxPawn(theBot.Pawn).SetSkin();
	//GBxPawn(theBot.Pawn).Test();
	/*
	log(theBot.FindPathTowardNearest(class'NavigationPoint', ));
	for ( i=0; i<16; i++ )
	{
		log("RouteCache: "$i$" is "$theBot.RouteCache[i]);
	}
	*/


	//GBxPawn(theBot.Pawn).DrawLine(theBot.Pawn.Location,theBot.Pawn.Location + 200 * vector(theBot.Pawn.Rotation));
	//Player.InteractionMaster.AddInteraction("BotAPI.GBHUDInteraction", PC.Player);
	//Level.bPlayersOnly = !Level.bPlayersOnly;
	//ParseRot(r,"Rot");
	//theBot.FocalPoint = theBot.Pawn.Location + 500 * vector(r);
	//theBot.setRotation(r);
	//theBot.FinishRotation();
		   /*
	foreach DynamicActors(Class'Engine.Gameinfo', FoundGameInfo) // iterates though all dynamic actors that are 'gameinfos'
      {                                                    // and stores a ref to them in  FoundGameInfo
       if (FoundGameInfo.bAllowVehicles == false)
           FoundGameInfo.bAllowVehicles = True;           // Sets the value FoundGameInfo.bAllowVehicles to true

        }
	SpawnVec = class<Khepera>(DynamicLoadObject("BotAPI.Khepera", class'Class'));
	CarVehicle = Spawn (SpawnVec, theBot,,theBot.Pawn.Location + 500 * vector(theBot.Pawn.rotation),);
		  */
	//Spawn(class<RotMover>(DynamicLoadObject("BotAPI.RotMover",class'Class')),theBot,,theBot.Pawn.Location + 500 * vector(theBot.Pawn.rotation),);
	//Cannot do this - movers have to be spawned not dynamically
}

function DrawTraceLine(int i, vector LineStart, vector LineEnd, bool LineHit)
{
	local Controller C;

    for( C = Level.ControllerList; C != None; C = C.NextController)
	{
		if( C.IsA('GBxPlayer') )
		{
			GBxPlayer(C).DrawTraceLine(LineStart,LineEnd,LineHit);
    	}//end if

	}                    /*
	if ((theBot.Pawn != none) && (theBot.Pawn.IsA('GBxPawn')))
	{
		GBxPawn(theBot.Pawn).DrawTraceLine(i,LineEnd,LineHit);
	}                      */
}

function bool LaunchRay (
	vector From,
	vector RealRayDirection, //current bot rotation
	CustomRay Ray,
	out vector To,
	out optional vector HitNormal,
	out optional vector HitLocation,
	out optional string HitId
	)
{
	local Actor HitActor; //shouldnt be object?

	To = From + (RealRayDirection * Ray.Length);
	HitId = "None";

	if (Ray.FastTrace == true)
	{
		return !FastTrace(To, From);
	}
	else
	{
		HitActor = Trace(HitLocation, HitNormal, To, From, Ray.TraceActors);
		if ( (HitActor != None) && HitActor.IsA('Pawn'))
			HitId = Pawn(HitActor).Controller $ Pawn(HitActor).Controller.PlayerReplicationInfo.PlayerID;
		else
			HitId = string(HitActor);
		if (HitActor != None)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

function AddCustomRay(string Id, vector Direction, float Length, bool FastTrace, bool TraceActors, bool ProvideFloorCorrection)
{
	local CustomRay Ray;
	local int i;

	//if the ray exists we will remove it
	for (i=0; i < CustomRayList.Length; i++)
	{
		if (CustomRayList[i].Id == Id)
		{
			if (CustomRayList[i].VisualizerMiss != none)
				CustomRayList[i].VisualizerMiss.Destroy();
			if (CustomRayList[i].VisualizerHit != none)
				CustomRayList[i].VisualizerHit.Destroy();

			CustomRayList.Remove(i,1);
			break;
		}
	}

	Ray.Id = Id;
	Ray.Rotation = rotator(Direction);
	Ray.Length = Length;
	Ray.FastTrace = FastTrace;
	Ray.TraceActors = TraceActors;
	Ray.ProvideFloorCorrection = ProvideFloorCorrection;

	CustomRayList[CustomRayList.Length] = Ray;
}

function RemoveCustomRay(string Id)
{
	local int i, CustomRayListLength;

	if (Id == "All")
	{
		//destroy the visualizers
		for ( i=0; i < CustomRayList.Length; i++ )
		{
			if (CustomRayList[i].VisualizerMiss != none)
				CustomRayList[i].VisualizerMiss.Destroy();
			if (CustomRayList[i].VisualizerHit != none)
				CustomRayList[i].VisualizerHit.Destroy();

		}

		CustomRayList.Remove(0,CustomRayList.Length);
		return;
	}

	CustomRayListLength = CustomRayList.Length;
	//log ("Length before removing "$CustomRayList.Length);
	i = 0;
	while (i < CustomRayListLength)
	{
		if (CustomRayList[i].Id == Id)
		{
			if (CustomRayList[i].VisualizerMiss != none)
				CustomRayList[i].VisualizerMiss.Destroy();
			if (CustomRayList[i].VisualizerHit != none)
				CustomRayList[i].VisualizerHit.Destroy();
			CustomRayList.Remove(i,1);
			break;
		}
		i++;
	}
		/*
	log ("Length after removing "$CustomRayList.Length);
	log ("ID of last obj in list "$CustomRayList[CustomRayListLength - 2].Id);
	log ("ID of deleted obj in list "$CustomRayList[CustomRayListLength - 1].Id);
	*/
}

function AddDefaultRays()
{
	local CustomRay Ray;
	local int i;

	//destroy the visualizers
	for ( i=0; i < CustomRayList.Length; i++ )
	{
		if (CustomRayList[i].VisualizerMiss != none)
			CustomRayList[i].VisualizerMiss.Destroy();
		if (CustomRayList[i].VisualizerHit != none)
			CustomRayList[i].VisualizerHit.Destroy();

	}

	//delete all rays from list
	CustomRayList.Remove(0,CustomRayList.Length);

	Ray.Id = "StraightAhead";
	Ray.Rotation = rotator(vect(1,0,0));
	Ray.Length = 250;
	Ray.FastTrace = false;
	Ray.TraceActors = false;
	Ray.ProvideFloorCorrection = false;

	CustomRayList[0] = Ray;

	Ray.Id = "45toLeft";
	Ray.Rotation = rotator(vect(1,-1,0));
	Ray.Length = 200;
	Ray.FastTrace = false;
	Ray.TraceActors = false;
	Ray.ProvideFloorCorrection = false;

	CustomRayList[1] = Ray;

	Ray.Id = "45toRight";
	Ray.Rotation = rotator(vect(1,1,0));
	Ray.Length = 200;
	Ray.FastTrace = false;
	Ray.TraceActors = false;
	Ray.ProvideFloorCorrection = false;

	CustomRayList[2] = Ray;
}

function AutoTrace()
{
	local vector from, to, RealRayDirection;
	local bool result;
	local vector hitNormal, hitLocation;
	local string hitId;
	local int i, CustomRayListLength;
	local vector FloorNormal, FloorLocation;

	if (theBot.Pawn == None)
		return;

	//we have to take into account also angle of the floor we are standing on
	FloorNormal = vect(0,0,0);
	Trace(FloorLocation,FloorNormal,theBot.Pawn.Location + vect(0,0,-100),theBot.Pawn.Location, false, ,);

	i = 0;
	CustomRayListLength = CustomRayList.Length;
	while ((i < CustomRayListLength) && (theBot.Pawn != None))
	{
		from = theBot.Pawn.Location;

		RealRayDirection = vector(theBot.Pawn.Rotation + CustomRayList[i].Rotation);
		if (CustomRayList[i].ProvideFloorCorrection)
		{
			//RealRayDirection += FloorNormal * (Normal(RealRayDirection) dot FloorNormal) * -1;
			RealRayDirection += FloorNormal * (RealRayDirection dot FloorNormal) * -1;
		}

		result = LaunchRay(from, RealRayDirection, CustomRayList[i], to, hitNormal, hitLocation, hitId );
		if (theBot.bDrawTraceLines)
		{
			if (theBot.Pawn != none )
			{
				if (CustomRayList[i].VisualizerHit == none)
				{
					CustomRayList[i].VisualizerHit = Spawn(class'TraceLineRed', theBot.Pawn,, theBot.Pawn.Location, theBot.Pawn.Rotation);
					CustomRayList[i].VisualizerHit.Instigator = theBot.Pawn;
					theBot.Pawn.AttachToBone(CustomRayList[i].VisualizerHit, 'spine');
   					CustomRayList[i].VisualizerHit.BeamDirection = CustomRayList[i].Rotation;
   					CustomRayList[i].VisualizerHit.BeamLength = CustomRayList[i].Length;
   					CustomRayList[i].VisualizerHit.bFloorCorrection = CustomRayList[i].ProvideFloorCorrection;
   					CustomRayList[i].VisualizerHit.LifeSpan = Level.Game.TimeLimit * 60;

				}

   				if (CustomRayList[i].VisualizerMiss == none)
   				{
					CustomRayList[i].VisualizerMiss = Spawn(class'TraceLineGreen', theBot.Pawn,, theBot.Pawn.Location, theBot.Pawn.Rotation);
					CustomRayList[i].VisualizerMiss.Instigator = theBot.Pawn;
					theBot.Pawn.AttachToBone(CustomRayList[i].VisualizerMiss, 'spine');
   					CustomRayList[i].VisualizerMiss.BeamDirection = CustomRayList[i].Rotation;
   					CustomRayList[i].VisualizerMiss.BeamLength = CustomRayList[i].Length;
   					CustomRayList[i].VisualizerMiss.bFloorCorrection = CustomRayList[i].ProvideFloorCorrection;
   					CustomRayList[i].VisualizerMiss.LifeSpan = Level.Game.TimeLimit * 60;

				}

				if (result) //use red
				{
					CustomRayList[i].VisualizerMiss.bHidden = true;
					CustomRayList[i].VisualizerHit.bHidden = false;
				}
				else //use green
				{
					CustomRayList[i].VisualizerMiss.bHidden = false;
					CustomRayList[i].VisualizerHit.bHidden = true;
   				}

    				//log("CustomRay Visual. "$CustomRayList[i].Visualizer$"Instigator "$CustomRayList[i].Visualizer.Instigator$"LifeSpan "$CustomRayList[i].Visualizer.LifeSpan);

			}
		}

		//our own custom notification method for observers
		if (theBot != none)
			theBot.NotifyAutoTraceRayResult(CustomRayList[i].Id, from, to, CustomRayList[i].fastTrace, CustomRayList[i].ProvideFloorCorrection, result, hitNormal, hitLocation, CustomRayList[i].traceActors, hitId);

		SendLine("ATR {Id " $ CustomRayList[i].Id $
			"} {From " $ from $
			"} {To " $ to $
			"} {FastTrace " $ CustomRayList[i].fastTrace $
			"} {FloorCorrection " $ CustomRayList[i].ProvideFloorCorrection $
			"} {Result " $ result $
			"} {HitNormal " $ hitNormal $
			"} {HitLocation " $ hitLocation $
			"} {TraceActors " $ CustomRayList[i].traceActors $
			"} {HitId " $ hitId $
			"}");
		i++;
	}

}
function SendNotifyConf()
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

	//notify that variables changed
	SendLine(outstring);

	//notify all control servers that variables changed
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

function SetGBcommand(string cmdType)
{
	if ((theBot!= none) && (theBot.Pawn != none))
	{
		//log("comm set: "$ cmdType);
		if (cmdType!="")
		{
			GBxPawn(theBot.Pawn).SetLastGBCommand(Level.TimeSeconds $ ": " $ lastGBCommand);
		}
	}
}

//Main function for processing commands
function ProcessAction(string cmdType)
{
	if(bDebug)
		log("comandType:"@cmdType);

	if (bIterative)
		Level.Pauser = None;

	//sets the last GB command
	SetGBcommand(cmdType);

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
			case "ACT":
				ReceivedAct();
			break;
			case "ADDINV":
				ReceivedAddInv();
			break;
			case "ADDRAY":
				ReceivedAddRay();
			break;
			case "CHANGETEAM":
				ReceivedChangeTeam();
			break;
			case "CHANGEWEAPON":
				ReceivedChangeWeapon();
			break;
			case "CHATTR":
				ReceivedChAttr();
			break;
			case "CHECKREACH":
				ReceivedCheckReach();
			break;
			case "CMOVE":
				ReceivedCMove();
			break;
			case "COMBO":
				ReceivedCombo();
			break;
			case "CONF":
				ReceivedConf();
			break;
			case "DIALOG":
				ReceivedDialog();
			break;
			case "DISCONNECT":
				ReceivedDisconnect();
			break;
			case "DODGE":
				ReceivedDodge();
			break;
			case "DRIVETO":
				ReceivedDriveTo();
			break;
			case "ENTER":
				ReceivedEnter();
			break;
			case "FTRACE":
				ReceivedFTrace();
			break;
			case "GETINVS":
				ExportInventory();
			break;
			case "GETITC":
				ReceivedGetItc();
			break;
			case "GETNAVS":
				ExportNavPoints();
			break;
			case "GETPATH":
				ReceivedGetPath();
			break;
			case "GIVEINV":
				ReceivedGiveInv();
			break;
			case "INCH":
				ReceivedInch();
    		break;
    		case "INIT":
				ReceivedInit();
			break;
			case "JUMP":
				ReceivedJump();
			break;
			case "LEAVE":
				ReceivedLeave();
			break;
			case "MESSAGE":
				ReceivedMessage();
			break;
			case "MOVE":
				ReceivedMove();
			break;
			case "PAUSE":
				ReceivedPause();
			break;
			case "PICK":
				ReceivedPick();
			break;
			case "PING":
				SendLine("PONG {Time " $ Level.TimeSeconds $ "}");
			break;
			case "QUIT":
				Closed();
			break;
			case "READY":
				ReceivedReady();
			break;
			case "REC":
				ReceivedRec();
			break;
			case "REMOVERAY":
				ReceivedRemoveRay();
			break;
			case "RESPAWN":
				ReceivedRespawn();
			break;
			case "ROTATE":
				ReceivedRotate();
			break;
			case "SENDCTRLMSG":
				ReceivedSendCtrlMsg();
			break;
			case "SETCROUCH":
				ReceivedSetCrouch();
			break;
			case "SETNAME":
				ReceivedSetName();
			break;
			case "SETROUTE":
				ReceivedSetRoute();
			break;
			case "SETSKIN":
				ReceivedSetSkin();
			break;
    	    case "SETWALK":
				ReceivedSetWalk();
			break;
			case "SHOOT":
				ReceivedShoot();
			break;
			case "SPEECH":
				ReceivedSpeech(); //doesnt work right now
			break;
			case "STOP":
				ReceivedStop();
			break;
			case "STOPREC":
				ReceivedStopRec();
			break;
			case "STOPSHOOT":
				ReceivedStopShoot();
			break;
			case "TEST":
				DoTest();
			break;
			case "THROW":
				ReceivedThrow();
			break;
			case "TRACE":
				ReceivedTrace();
			break;
			case "TURNTO":
				ReceivedTurnTo();
			break;
			case "USE":
				ReceivedUse();
			break;
		}//end switch
	}//end if
}

function ReceivedAct()
{
	local string tmp;

	if (theBot == None || theBot.Pawn == None)
		return;

	tmp = GetArgVal("Name");
	if (tmp != "")
	{
		SetPropertyText("tempName",tmp);
		//theBot.Pawn.PlayAnim(tempName,,);
		theBot.Pawn.SetAnimAction(tempName);
		//theBot.Pawn.FinishAnim();
		//SendLine("ACTFIN");
	}

}

function ReceivedAddInv()
{
	local string target;
	local bool bAlreadyExported;
	local int i;

	if (theBot == None || theBot.Pawn == None)
		return;

	if (Level.Game.IsA('BotScenario')) {
		if (!BotScenario(Level.Game).canReceiveInventory(theBot))
		{
			return;
		}
	} else if (bAllowCheats != True) {
		return;
	}

	target = GetArgVal("Type");
	target = "class'"$target$"'";
	setPropertyText("tempPickupClass",target);

	//We allow to add just pickup classes
	if (tempPickupClass != none)
	{

		//First check if we haven't exported this already
		bAlreadyExported = false;
		for (i = 0; i < ExportedPickup.Length; i++)
		{
			if (ExportedPickup[i].PickupClass == tempPickupClass)
			{
				bAlreadyExported = true;
				break;
			}
		}

		if (!bAlreadyExported)
		{
			SendLine(GetPickupInfoToITC(tempPickupClass));

			//Add exported pickup class to list holding exported classes
			ExportedPickup.Insert(ExportedPickup.Length,1);
			ExportedPickup[ExportedPickup.Length - 1].PickupClass = tempPickupClass;
		}

		//log("Added inventory will be "$tempInventory);

		theBot.SpawnInventory(tempPickupClass);

	}
}

function ReceivedAddRay()
{
	local string target;
	local float floatNumber;
	local vector v;
	local bool boolResult, boolResult2, boolResult3;

	target = GetArgVal("Id");

	if (target == "Default")
	{
		AddDefaultRays();
	}
	else
	{
		ParseVector(v,"Direction");
		floatNumber = float(GetArgVal("Length"));
		boolResult = bool(GetArgVal("FastTrace"));
		boolResult2 = bool(GetArgVal("TraceActors"));
		boolResult3 = bool(GetArgVal("FloorCorrection"));
		AddCustomRay(target,v,floatNumber,boolResult,boolResult2,boolResult3);
	}

}

function ReceivedChangeTeam()
{
	local string temp;

	if ((theBot != none) && Level.Game.IsA('BotTeamGame'))
	{
		temp = GetArgVal("Team");
		if (temp != "")
		{
	    	if (BotTeamGame(Level.Game).ChangeTeam(theBot, int(temp), true))
			{
				SendLine("TEAMCHANGE {Success True" $
					"} {DesiredTeam " $ temp $
					"}");
			}
			else
			{
			    SendLine("TEAMCHANGE {Success False" $
					"} {DesiredTeam " $ temp $
					"}");
			}
	    }
	}
}


function ReceivedChangeWeapon()
{
	local string Target;
	local Inventory Inv;
	local Weapon TargetWeapon;
	local string outstring;

    if (theBot == none || theBot.Pawn == None || theBot.Pawn.Inventory == None )
		return;

	Target = GetArgVal("Id");
	if( Target ~= "best" )
	{
		theBot.StopFiring();
		theBot.SwitchToBestWeapon();
	}
	else
	{
		TargetWeapon = none;
		for( Inv=theBot.Pawn.Inventory; Inv!=None; Inv=Inv.Inventory )
		{
			if (target == string(Inv))
			{
				if (Inv.IsA('Weapon'))
					TargetWeapon = Weapon(Inv);
				break;
			}
		}
		if (TargetWeapon == none)
			return;

        theBot.StopFiring();
		//log("Pawns Target Weapon "$TargetWeapon);
		//log("Pawns weapoin: "$theBot.Pawn.Weapon);
		theBot.Pawn.PendingWeapon = TargetWeapon;
		if ( theBot.Pawn.PendingWeapon == theBot.Pawn.Weapon )
			theBot.Pawn.PendingWeapon = None;
		if ( theBot.Pawn.PendingWeapon == None )
			return;

		//log("Strelba: "$theBot.bFire$" a alt fire "$theBot.bAltFire);
       	if ( theBot.Pawn.Weapon == None )
			theBot.Pawn.ChangedWeapon();
		else if ( theBot.Pawn.Weapon != theBot.Pawn.PendingWeapon )
		{
			//notify agent about the weapon state before putdown, so it is up to date
			//SLF message may be inaccurate due to dealy of sync. batch
			outstring = "WUP {Id " $ theBot.Pawn.Weapon $
				"} {PrimaryAmmo " $ theBot.Pawn.Weapon.AmmoAmount(0) $
				"} {InventoryType " $ theBot.Pawn.Weapon.Class $ "}";

			if (theBot.Pawn.Weapon.FireModeClass[1] != None)
			{
				outstring = outstring $ " {SecondaryAmmo " $ theBot.Pawn.Weapon.AmmoAmount(1) $ "}";
			}

			SendLine(outstring);
			//put down weapon
			theBot.Pawn.Weapon.PutDown();
		}


	}
}

function ReceivedChAttr()
{
	local string target;
	local int integer;

	if (theBot == None || theBot.Pawn == None )
		return;

	if (!bAllowCheats) {
		return;
	}

	target = GetArgVal("Health");
	if (target != "") {
		//log("here");
		integer = int(target);
		theBot.SetHealth(integer);
	}

	target = GetArgVal("Adrenaline");
	if (target != "")
	{
		integer = int(target);
		theBot.SetAdrenaline(integer);
	}

}

function ReceivedCheckReach()
{
	local Controller C;
	local string target, id;
	local vector v;
	local bool boolResult;

	if (theBot == None || theBot.Pawn == None)
		return;

	target = GetArgVal("Target");
	id = GetArgVal("Id");
	if(target == "")
	{
		ParseVector(v,"Location");
		boolResult = theBot.PointReachable(v);
		sendLine("RCH {Id " $ id $
			"} {Reachable " $ boolResult $
			"} {From " $ theBot.Pawn.Location $
			"}");
	}
	else
	{
		for(C = Level.ControllerList; C != None; C = C.NextController )
		{
			if( ( C $ C.PlayerReplicationInfo.PlayerID ) == target )
			{
				break;
			}
		}
		if (C != None)
		{
			boolResult = theBot.actorReachable( C.Pawn );
			sendLine("RCH {Id " $ id $
				"} {Reachable " $ boolResult $
				"} {From " $ theBot.Pawn.Location $
				"} {To " $ C.Pawn.Location $
				"}");
		}
		else
		{
			SetPropertyText("tempActor",target);
			if (tempActor != None)
			{
				boolResult = theBot.actorReachable( tempActor );
				sendLine("RCH {Id " $ id $
					"} {Reachable " $ boolResult $
					"} {From " $ theBot.Pawn.Location $
					"} {To " $ tempActor.Location $
					"}");
			}
		}
	}

}

function ReceivedCMove()
{

	local rotator yawRotation;

	if (theBot == None || theBot.Pawn == None)
		return;

	//We need to reset focus, otherwise the focus would reset focal point to its own location
	theBot.Focus = None;
	theBot.myFocus = None;

	yawRotation.Yaw = theBot.Pawn.Rotation.Yaw;
    theBot.myFocalPoint = theBot.Pawn.Location + 500 * vector(yawRotation);
	theBot.GotoState('StartUp','MoveContinuous');
}

function ReceivedDialog()
{
	local string target, text, dialogId;
	local string Options[10];
	local int i;
	local Controller C;
	local bool bDialogSet;

	if (theBot == None )
		return;

	target = GetArgVal("Id");

	dialogId = GetArgVal("DialogId");

	text = GetArgVal("Text");

	i = 0;
	while (GetArgVal("Option"$i) != "")
	{
		Options[i] = GetArgVal("Option"$i);
		i += 1;

		if (i > 9)
			break;
	}


	for(C = Level.ControllerList; C != None; C = C.NextController )
	{
		if( target == (C $ C.PlayerReplicationInfo.PlayerID) )
		{
			break;
		}
	}//end for

	bDialogSet = false;
	if ((C != none) && C.IsA('GBxPlayer'))
	{
		//we will set dialog just if our bot is selected by the player
		if (GBxPlayer(C).SelectedActor == theBot.Pawn)
		{
			GBxPlayer(C).SetDialog(dialogId, text, theBot.PlayerReplicationInfo.PlayerName, Options);
			bDialogSet = true;
		}
	}

	if (bDialogSet)
	{
		SendLine("DOK {Id " $ dialogId $ "}");
	}
	else
	{
		SendLine("DFAIL {Id " $ dialogId $ "}");
	}
}

function ReceivedDisconnect()
{
	if (theBot != None)
	{
		if (theBot.Pawn != none) {
			theBot.bAutoSpawn = false;
			theBot.DestroyPawn();
		}
		theBot.Destroy();
	}

	if (FocusActor != None)
	{
		FocusActor.Destroy();
	}

	Close();
	//Destroy();
}

function ReceivedDodge()
{
	local string temp;
	local vector vec, wallVec, focus;
	local rotator yawRot;

	if (theBot == none || theBot.Pawn == none)
		return;

	if (Level.TimeSeconds - lastDodgeTime < 1)
		return;

	temp = GetArgVal("Direction");
	if (temp != "")
	{
		ParseVector(vec,"Direction");
		if (theBot.RemoteDodge(Normal(vector(theBot.Pawn.Rotation + rotator(vec))), bool(GetArgVal("Double")), bool(GetArgVal("Wall")))) {
			if (GetArgVal("FocusPoint") != "") {
				ParseVector(focus,"FocusPoint");
				theBot.myFocalPoint = focus;
				theBot.FocalPoint = theBot.myFocalPoint;
			} else {
				yawRot.Pitch = 0;
				yawRot.roll = 0;
				yawRot.yaw = theBot.Pawn.Rotation.Yaw;
				focus = theBot.Pawn.Location + (Normal(vector(yawRot)) * 1200);
				theBot.myFocalPoint = focus;
				theBot.FocalPoint = theBot.myFocalPoint;
			}
		}
		lastDodgeTime = Level.TimeSeconds;
	}

}

function ReceivedDriveTo()
{
	local string target;

	if (theBot == None || theBot.Pawn == None)
		return;

	target = GetArgVal("Target");
	if(target != "")
	{
		SetPropertyText("tempActor",target);

		if( tempActor != none )
		{
			theBot.MoveTarget = tempActor;

			theBot.myFocalPoint = tempActor.Location + 500 * vector(rotator(tempActor.Location - theBot.Pawn.Location)); //should reapir issue that the bot is turning after finishing moveto
			theBot.GotoState('Startup', 'MoveToActor');
		}
	}
}


function ReceivedEnter()
{
	local Vehicle Veh;
	local string target;

	target = GetArgVal("Id");

	if (target == "")
		return;

	//if bot is created and we are alive and we are not driving another vehicle
	if ((theBot != none) && (theBot.Pawn != none) && (Vehicle(theBot.Pawn)==None))
		foreach DynamicActors(class'Vehicle', Veh)
		{
			if ((vSize(theBot.Pawn.Location - Veh.Location) < Veh.EntryRadius) && (target == getUniqueId(Veh)))
			{
				if (Veh.TryToDrive(theBot.Pawn))
					SendLine("ENTERED {Id " $ getUniqueId(Veh) $
						"} {Type " $ Veh.Class $
						"} {Location " $ Veh.Location $
						"}");
				else
					SendLine("LOCKED {Id " $ getUniqueId(Veh) $
						"} {Type " $ Veh.Class $
						"} {Location " $ Veh.Location $
						"}");
			}
		}

}

function ReceivedFTrace()
{
	local vector v,v2;
	local string target;

	if (theBot == None || theBot.Pawn == None)
		return;

	if (GetArgVal("From") == "")
	{
		if (theBot.Pawn != None)
			v = theBot.Pawn.Location;
		else
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

function ReceivedCombo()
{
	local string tmp;

	if (theBot == None)
		return;

	tmp = GetArgVal("Type");
	if (tmp != "")
		theBot.RemoteCombo(tmp);

}

function ReceivedConf() {
	local float floatNumber;
	local string target, argName, argValue;
	local rotator r;
	local int i;

	if (theBot == none)
		return;

	for (i = 0; i < ArgsMaxCount; i++) {
		argName = ReceivedArgs[i];
		argValue = ReceivedVals[i];
		if (argName != "") {
			if (argName == "Name") {
				target = argValue;
				theBot.PlayerReplicationInfo.PlayerName = target;
				Level.Game.changeName( theBot, target, true );
			} else if (argName == "AutoTrace")
				theBot.bAutoTrace = bool(argValue);
			else if (argName == "DrawTraceLines")
				theBot.bDrawTraceLines = bool(argValue);
			else if (argName == "ManualSpawn")
				theBot.bAutoSpawn = !bool(argValue);
			else if (argName == "ShowFocalPoint")
				theBot.bShowFocalPoint = bool(argValue);
			else if (argName == "ShowDebug")
				theBot.bDebug = bool(argValue);
			else if (argName == "SpeedMultiplier") {
				floatNumber = float(argValue);
				if ( (floatNumber >= 0.1) && (floatNumber <= theBot.MaxSpeed) )
				{
					theBot.SpeedMultiplier = floatNumber;
				    if (theBot.Pawn != none) {
						theBot.Pawn.GroundSpeed = floatNumber * theBot.Pawn.Default.GroundSpeed;
						theBot.Pawn.WaterSpeed = floatNumber * theBot.Pawn.Default.WaterSpeed;
						theBot.Pawn.AirSpeed = floatNumber * theBot.Pawn.Default.AirSpeed;
						theBot.Pawn.LadderSpeed = floatNumber * theBot.Pawn.Default.LadderSpeed;
					}
				}
			} else if(argName == "RotationRate") {
				ParseRot(r,"RotationRate");
				if (r != rot(0,0,0)) {
					theBot.RotationRate = r;
					if (theBot.Pawn != none) {
						theBot.Pawn.RotationRate = r;
					}
				}
			} else if ((argName == "Invulnerable") && (bAllowCheats == true))
				theBot.bGodMode = bool(argValue);
			else if (argName == "VisionTime") {
				floatNumber = float(argValue);
				if ((floatNumber >= 0.05) && (floatNumber <= 2)) {
					visionTime = floatNumber;
					sleepTime = visionTime / float(locUpdateMultiplier);
					if (sleepTime < 0.05) sleepTime = 0.05; /// UT2004 cannot sleep less then 0.05s
					locUpdateMultiplierCurrent = visionTime / sleepTime;
					log("VisionTime configured to " $ visionTime);
				}
			} else if (argName == "SynchronousOff") {
				theBot.myConnection.bSynchronousMessagesOff = bool(argValue);
			} else if (argName == "SyncNavPointsOff") {
				theBot.myConnection.bSynchronousNavPoints = !bool(argValue);
			} else if (argName == "AutoPickupOff") {
				theBot.bDisableAutoPickup = bool(argValue);
			if (theBot.Pawn != none)
				theBot.Pawn.bCanPickupInventory = !theBot.bDisableAutoPickup;
			}
		}
	}
	SendNotifyConf();
}

function ReceivedGetItc()
{
	local string target;
	local bool bAlreadyExported;
	local int i;

	if (theBot == None)
		return;

	target = GetArgVal("Type");
	target = "class'"$target$"'";
	setPropertyText("tempPickupClass",target);

	//We allow to export just pickup classes
	if (tempPickupClass != none)
	{
        SendLine(GetPickupInfoToITC(tempPickupClass));

		//Now check if we have it in our list, if not add
		bAlreadyExported = false;
		for (i = 0; i < ExportedPickup.Length; i++)
		{
			if (ExportedPickup[i].PickupClass == tempPickupClass)
			{
				bAlreadyExported = true;
				break;
			}
		}

		if (!bAlreadyExported)
		{
			//Add exported pickup class to list holding exported classes
			ExportedPickup.Insert(ExportedPickup.Length,1);
			ExportedPickup[ExportedPickup.Length - 1].PickupClass = tempPickupClass;
		}

	}

}

function ReceivedGetPath()
{
	local vector v;
	local string id, stringPath, tmp, target, left;
	local int i;
	local NavigationPoint N;

	if ( theBot == None )
		return;

	//clear the old path
	for ( i=0; i<16; i++ )
	{
		if ( theBot.RouteCache[i] == None )
			break;
		else
		{
			theBot.RouteCache[i] = None;
		}
	}

    id = GetArgVal("Id");

	target = GetArgVal("Target");
	if (target != "")
	{
		for(N = Level.NavigationPointList; N != None; N = N.nextNavigationPoint )
		{
			if( target == string(N) )
			{
				break;
			}
		}//end for

		if (N != none)
			theBot.FindPathToward(N, true);
	} else {
		if (GetArgVal("Location") != "")
		{
			ParseVector(v,"Location");
			theBot.FindPathTo(v);
		}
	}

	SendLine("SPTH {MessageId " $ id $ "}");
	for ( i=0; i<16; i++ )
	{
		if ( theBot.RouteCache[i] == None )
			break;
		else
		{
			SendLine("IPTH {RouteId " $ theBot.RouteCache[i] $
				"} {Location " $ theBot.RouteCache[i].Location $
				"}");
		}
	}
	SendLine("EPTH");

   	stringPath = Level.TimeSeconds $ ": Id:" $ id $ ", ";
	for ( i=0; i<16; i++ )
	{
		if ( theBot.RouteCache[i] == None )
			break;
		else
		{
			Divide( string(theBot.RouteCache[i]), ".", left , tmp);
			stringPath = stringPath $ i $ ": " $ tmp $ ", ";
		}
	}

	//TODO: This is just for debugging purpose
	if (theBot.Pawn != none) {
		GBxPawn(theBot.Pawn).SetLastGBPath(stringPath);
	}

	/*
	ParseVector(v,"Location");
	theBot.FindPathTo(v);
	id = GetArgVal("Id");

	SendLine("SPTH {MessageId " $ id $ "}");
	for ( i=0; i<16; i++ )
	{
		if ( theBot.RouteCache[i] == None )
			break;
		else
		{
			SendLine("IPTH {RouteId " $ theBot.RouteCache[i] $"}");
		}
	}
	SendLine("EPTH");

   	stringPath = Level.TimeSeconds $ ": Id:" $ id $ ", ";
	for ( i=0; i<16; i++ )
	{
		if ( theBot.RouteCache[i] == None )
			break;
		else
		{
			Divide( string(theBot.RouteCache[i]), ".", left , tmp);
			stringPath = stringPath $ i $ ": " $ tmp $ ", ";
		}
	}

	//TODO: This is just for debugging purpose
	if (theBot.Pawn != none) {
		GBxPawn(theBot.Pawn).SetLastGBPath(stringPath);
	}	*/
}

function ReceivedGiveInv()
{
	local Controller C;
	local string target;
	local Inventory Inv;

	if ((theBot == None) || (theBot.Pawn == None))
		return;

	target = GetArgVal("Target");

	for(C = Level.ControllerList; C != None; C = C.NextController )
		{
		if( (C$C.PlayerReplicationInfo.PlayerID) == target )
		{
			if (C.Pawn != none)
			{
			 	target = GetArgVal("ItemId");
			 	Inv = none;
				for( Inv=theBot.Pawn.Inventory; Inv!=None; Inv=Inv.Inventory )
				{
					if (String(Inv) == target)
						break;
				}
				if (Inv != none)
				{
					if (theBot.Pawn.Weapon == Inv)
						return;
					if (C.Pawn.AddInventory(Inv))
					{
						SendLine("LIN {Id "$Inv$"}");
					 	theBot.Pawn.DeleteInventory( Inv );
					}
				}
			}
       	}

	}

}

function ReceivedInch()
{
	//test function
	/* must deal with target, focus and destination */
	theBot.StopWaiting();
	theBot.myDestination = (100 * vector(theBot.Pawn.Rotation)) + theBot.Pawn.Location;
	theBot.GotoState('Startup', 'MoveToPoint');
}

function ReceivedInit()
{
	if (IsInState('waiting'))
	{
		InitBot();
	}
	else
	{
		if (bDebug)
			log("Bot already spawned");
	}
}



function ReceivedJump()
{
	local string tmp;
	local bool bDouble;
	local float delay;
	local float force;

	if (theBot == None || theBot.Pawn == None)
		return;

	bDouble = false;
	tmp = GetArgVal("DoubleJump");
	if (tmp != "")
		bDouble = bool(tmp);
	tmp = GetArgVal("Delay");
	if (tmp != "")
		delay = float(tmp);
	tmp = GetArgVal("Force");
	if (tmp != "")
		force = float(tmp);

	theBot.RemoteJump(bDouble, delay, force);

}

function ReceivedLeave()
{
	if (Vehicle(theBot.Pawn)!=None)
	{
		Vehicle(theBot.Pawn).KDriverLeave( true );
	}
}

function ReceivedMessage()
{
	local string target, text;
	local bool boolResult;
	local float FadeOut;
	local int TeamIndex;

	if (theBot == None )
		return;

	//Note - currently only allow messages under 256 chars
	target = GetArgVal("Id");
	text = GetArgVal("Text");
	boolResult = bool(GetArgVal("Global"));
	FadeOut = float(GetArgVal("FadeOut"));
	TeamIndex = float(GetArgVal("TeamIndex"));
	if(text != "")
	{
		theBot.RemoteBroadcast(target,text,TeamIndex,boolResult,FadeOut);
	}

}

function ReceivedMove()
{
	local vector v,v2,focusLoc;
	local string focusId;
	local Actor tmpFocus;
	local Controller C;

	if (theBot == None || theBot.Pawn == None)
		return;

	//if first location not specified, we wont move
	if (GetArgVal("FirstLocation")=="")
		return;

	ParseVector(v,"FirstLocation");
	focusId = GetArgVal("FocusTarget");

	//set the destinations we want to traverse
	theBot.myDestination = v;
	theBot.Destination = v;
	if (GetArgVal("SecondLocation")!="") {
		ParseVector(v2,"SecondLocation");
		theBot.pendingDestination = v2;
	} else {
		theBot.pendingDestination = v;
	}

	if(focusId == "")
	{
		if (GetArgVal("FocusLocation")!="")
		{
			ParseVector(focusLoc,"FocusLocation");
			//Cant focus to location, but can to actor, we set position of our helper actor to desired
	        FocusActor.SetLocation(focusLoc);
	        //Lets se the bots focus to our helper actor
			theBot.Focus = FocusActor;
			theBot.myFocus = FocusActor;
			//Set FocalPoint accordingly (it would change to desired values anyway)
			theBot.myFocalPoint = FocusActor.Location;
			theBot.FocalPoint = FocusActor.Location;
		} else {
			//we reset old focus, if none focus set, the bot will turn towards destination
			theBot.Focus = none;
			theBot.myFocus = none;
			//todo: reset also target?

			//set myFocalPoint to prevent unwanted turning back at the end of movement
			//myFocalPoint will be set at the end of movement
			if (GetArgVal("SecondLocation")!="")
				theBot.myFocalPoint = v2 + 500 * vector(rotator(v2 - v));
			else
				theBot.myFocalPoint = v + 500 * vector(rotator(v - theBot.Pawn.Location));
		}
	} else { //We have Id of the object we want to face
		//First we determine if it is a bot id
		tmpFocus = None;
		for( C = Level.ControllerList; C != None; C = C.NextController)
		{
			if( C.PlayerReplicationInfo != None && (C$C.PlayerReplicationInfo.PlayerID) == focusId)
			{
				if( theBot.Pawn.LineOfSightTo(C.Pawn))
				{
					tmpFocus = C.Pawn;
					break;
				}
			}
		}
		//we found it is a bot id, lets set it as our focus
		if (tmpFocus != none)
		{
			//point the bot at the location of the target
			theBot.FocalPoint = tmpFocus.Location;
			theBot.myFocalPoint = tmpFocus.Location;
			theBot.Focus = tmpFocus;
			theBot.myFocus = tmpFocus;
			theBot.Target = tmpFocus;
		}
		else // it was not a bot id
		{
			//lets try if there is an object with this id
			//can be navpoint or some of the items have unique id
			setPropertyText("tempActor", focusId);
			//if succes, then set this object as our focus object
			if ((tempActor != none) && theBot.Pawn.LineOfSightTo(tempActor))
			{
				theBot.myFocus = tempActor;
				theBot.Focus = tempActor;
				theBot.myFocalPoint = tempActor.Location;
				theBot.FocalPoint = tempActor.Location;
			}
		}
	}
	theBot.GotoState('StartUp','Move');
}

function ReceivedPause()
{
	local bool bWasPaused;

	if ((!bAllowPause) || theBot == None)
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
				// we have to blame the pause on somebody
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

function ReceivedPick()
{
	local string target;

	if (theBot == none)
		return;

	target = GetArgVal("Id");
	if (target != "")
		theBot.RemotePickup(target);

}

function ReceivedPassword()
{
	local string target;

	target = GetArgVal("Password");

	if (target == BotDeathMatch(Level.Game).Password)
	{
		SendLine("PASSWDOK");
		ExportStatus();
		gotoState('waiting','Waiting');
	}
	else
	{
		SendLine("PASSWDWRONG");
		Closed();
	}

}


function ReceivedReady()
{
	if (IsInState('waiting'))
		ExportStatus();

	if ( IsInState('checkingPassword') )
		SendLine("PASSWORD {BlockedByIP " $ BotDeathMatch(Level.Game).PasswordByIP $"}");

}

function ReceivedRec()
{
	local string target;

	target = GetArgVal("FileName");
	ConsoleCommand("demorec "$target, True);
	SendLine("RECSTART");
}

function ReceivedRemoveRay()
{
	local string target;

	if (theBot == None)
		return;
	target = GetArgVal("Id");
	RemoveCustomRay(target);
}

function ReceivedRespawn()
{
	local vector v;
	local rotator r;

	if ( theBot == None )
		return;

	ParseVector(v,"StartLocation");
	ParseRot(r,"StartRotation");
	theBot.RespawnPlayer(v,r);
}

function ReceivedRotate()
{
	local string target;
	local rotator r;
	local int i;

	if (theBot.Pawn == None)
		return;

	target = GetArgVal("Axis");
	r = theBot.Pawn.Rotation;
	i = int(GetArgVal("Amount"));
	if(target == "Vertical")
	{
		r.Pitch = int(theBot.Pawn.ViewPitch) * 65535/255 + i;
		//r.Yaw = theBot.Pawn.Rotation.Yaw;
	}
	else
	{
		r.Pitch = int(theBot.Pawn.ViewPitch) * 65535/255;
		r.Yaw += i;
	}

	theBot.myFocalPoint = theBot.Pawn.Location + ( vector(r) * 500);
	theBot.FocalPoint = theBot.myFocalPoint;
	theBot.myFocus = None;
	theBot.Focus = None; //theBot.Pawn.Location + ( vector(r) * 1000);

    if (theBot.movingContinuous)
		theBot.GotoState('StartUp','MoveContinuous');

	//We comment this so turning commands do not interupt moving commands.
	//theBot.StopWaiting();
	//theBot.GotoState('Startup', 'Turning');

}

//sends control message to all control servers only
function ReceivedSendCtrlMsg()
{
	local string target, type, ps1, ps2, ps3, pi1, pi2, pi3, pf1, pf2, pf3, pb1, pb2, pb3;
	local GBClientClass C;
	local BotDeathMatch myGame;

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

	myGame = BotDeathMatch(Level.Game);
	if (myGame.theControlServer != none) {
		for(C = myGame.theControlServer.ChildList; C != None; C = C.Next) {
			C.NotifyControlMessage(type, ps1, ps2, ps3, pi1, pi2, pi3, pf1, pf2, pf3, pb1, pb2, pb3);
		}
	}
}

function ReceivedSetCrouch()
{
	local string target;

	if (theBot == None || theBot.Pawn == None)
		return;

	target = GetArgVal("Crouch");
	SetPropertyText("tempBool",target);
	theBot.Pawn.ShouldCrouch( tempBool );

}

function ReceivedSetRoute()
{
	local vector v;
	local int i;

	if (theBot == None)
		return;

	if (bool(GetArgVal("Erase"))) {
		for (i=0;i<32;i++) {
			GBReplicationInfo(theBot.PlayerReplicationInfo.CustomReplicationInfo).SetCustomRoute(vect(0,0,0),i);
		}
	}

	for (i=0;i<32;i++) {
		ParseVector(v , "P"$i);
		//GBxPawn(theBot.Pawn).CustomRoute[i] = v;
		GBReplicationInfo(theBot.PlayerReplicationInfo.CustomReplicationInfo).SetCustomRoute(v,i);
		//log("Route"$i$": "$GBxPawn(theBot.Pawn).CustomRoute[i]);
	}
}

function ReceivedSetName() {
	local string newName;

	if (theBot == None)
		return;

	newName = GetArgVal("Name");
	if (newName != "") {
		theBot.PlayerReplicationInfo.PlayerName = newName;
		Level.Game.changeName( theBot, newName, true );
	}
}

function ReceivedSetSkin()
{
	if (theBot == None)
		return;
	if (GetArgVal("Skin") != "")
	{

		//theBot.Pawn.LinkMesh(Mesh(DynamicLoadObject(GetArgVal("Skin"), class'Mesh')));
		theBot.DesiredSkin = GetArgVal("Skin");
		//theBot.Pawn.LinkMesh(Mesh(DynamicLoadObject(theBot.DesiredSkin, class'Mesh')));
	}
	theBot.RespawnPlayer();
}

function ReceivedSetWalk()
{

	local string target;

	if (theBot == None || theBot.Pawn == None)
		return;

	target = GetArgVal("Walk");
	SetPropertyText("tempBool",target);
	theBot.Pawn.bIsWalking = tempBool;

}

function ReceivedShoot()
{
	local string Target;
	local Controller C;
	local Projectile Proj;
	local vector v;
	local bool targetLocked;

	if (theBot == None || theBot.Pawn == None)
		return;

	targetLocked = false;
	theBot.bTargetLocationLocked = false;
	Target = GetArgVal("Target");
	if( Target != "") {
        //is it a projectile?
    	if (InStr(Target, "ProID")!=-1) {
    		foreach DynamicActors(class'Projectile',Proj) {
    		   if (Target == getUniqueId(Proj)) {
                   if (theBot.LineOfSightTo(Proj)) {
                        theBot.Focus = Proj;
    				    theBot.myFocus = Proj;
        				theBot.Enemy = None;
        				theBot.RemoteEnemy = None;
        				theBot.Target = Proj;

        				theBot.FocalPoint = Proj.Location;
        				theBot.myFocalPoint = Proj.Location;
        				targetLocked = true;
                   }
      		       break;
    		   }
            }
		} else {
    		for( C=Level.ControllerList; C != None; C = C.NextController)
    		{
    			//We wont start shooting at non visible targets
    			if( ((C $ C.PlayerReplicationInfo.PlayerID) == target) &&
    				(C.Pawn != None) &&
    				(theBot.LineOfSightTo(C.Pawn))
    			)
    			{
    				//We will set desired bot as our enemy
    				theBot.Focus = C.Pawn;
    				theBot.myFocus = C.Pawn;
    				theBot.Enemy = C.Pawn;
    				theBot.RemoteEnemy = C;
    				theBot.Target = C.Pawn;

    				theBot.FocalPoint = C.Pawn.Location;
    				theBot.myFocalPoint = C.Pawn.Location;
    				targetLocked = true;
    				break;
    			}
    		}
		}
	}

	if (!targetLocked && GetArgVal("Location") != "")
	{
		ParseVector(v,"Location");

		theBot.myTarget.SetLocation(v);
		theBot.bTargetLocationLocked = true;

		//We are shooting at a location. We will set the FocalPoint
		theBot.FocalPoint = theBot.myTarget.Location;
		theBot.myFocalPoint = theBot.myTarget.Location;

		theBot.Focus = theBot.myTarget;
		theBot.Enemy = None;
		theBot.Target = theBot.myTarget;
	}

	theBot.RemoteFireWeapon(bool(GetArgVal("Alt")));
}

function ReceivedSpeech()
{
	local string target;

	if (theBot == None)
		return;

	target = GetArgVal("Text");
	theBot.TextToSpeech(target,50); //dont work dunno the reason, maybe volume bigger?
	SendLine(target);
}

function ReceivedStop()
{
	if (theBot == None || theBot.Pawn == None)
		return;

	theBot.GotoState('Startup', 'DoStop');
}

function ReceivedStopRec()
{
	ConsoleCommand("stopdemo", True);
	SendLine("RECEND");
}

function ReceivedStopShoot()
{
	if (theBot == None)
		return;

	theBot.StopWaiting();
	theBot.HaltFiring();
}

function ReceivedThrow()
{
	local vector TossVel;
	local string thrownWeaponId;

	if (theBot == None || theBot.Pawn == None)
		return;

	if (theBot.Pawn.CanThrowWeapon())
	{
		thrownWeaponId = string(theBot.Pawn.Weapon);
		TossVel = Vector(theBot.GetViewRotation());
		TossVel = TossVel * ((theBot.Pawn.Velocity dot TossVel) + 500) + Vect(0,0,200);
		theBot.Pawn.TossWeapon(TossVel);
		theBot.SwitchToBestWeapon();
		SendLine("THROWN {Id " $ thrownWeaponId $ "}");
	}
}
function ReceivedTrace()
{
	local bool boolResult;
	local vector v, v2, HitLocation, HitNormal;
	local string target, unrealId;

	if (GetArgVal("From") == "")
	{
		if (theBot.Pawn != None)
			v = theBot.Pawn.Location;
		else
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

function ReceivedTurnTo()
{
	local Controller C;
	local vector v;
	local rotator r;
	local string target;

	if (theBot == none || theBot.Pawn == None)
		return;

	target = GetArgVal("Target");
	if(target == "")
	{
		ParseRot(r,"Rotation");
		if(r.Yaw == 0 && r.Pitch == 0 && r.Roll == 0)
		{
			//no target or rotation defined
			ParseVector(v,"Location");
			theBot.FocalPoint = v;
			theBot.myFocalPoint = v;
			//We erase possible focus actors
			theBot.myFocus = None;
			theBot.Focus = None;

			if (theBot.movingContinuous)
				theBot.GotoState('StartUp','MoveContinuous');
		}
		else
		{
			//no target, yes rotation
			theBot.myFocalPoint = theBot.Pawn.Location + ( vector(r) * 500);
			theBot.FocalPoint = theBot.myFocalPoint;

			//We erase possible focus actors
			theBot.myFocus = None;
			theBot.Focus = None;

			if (theBot.movingContinuous)
				theBot.GotoState('StartUp','MoveContinuous');
		}
	}
	else
	{
		//target defined

		//First we try to find if we should focus to a player or bot
		for(C = Level.ControllerList; C != None; C = C.NextController )
		{
			//TODO: GetUniqueId here!
			if(C.PlayerReplicationInfo != None && target == (C $ C.PlayerReplicationInfo.PlayerID) )
			{
				break;
			}
		}//end for
		if (C != None)
		{
			//Pawn must exists and must be visible
			if ((C.Pawn != None) && theBot.Pawn.LineOfSightTo(C.Pawn))
			{
				//We set the Controller as our target
				theBot.FocalPoint = C.Pawn.Location;
				theBot.myFocalPoint = C.Pawn.Location;
				theBot.Focus = C.Pawn;
				theBot.myFocus = C.Pawn;

				if (theBot.movingContinuous)
					theBot.GotoState('StartUp','MoveContinuous');

			}
			else
			{
				return;
			}
		}
		else
		{
			tempActor = None;
			SetPropertyText("tempActor",target);
			//Actor must be visible
			if ((tempActor != None) && theBot.Pawn.LineOfSightTo(tempActor))
			{
				theBot.myFocus = tempActor;
				theBot.Focus = tempActor;
				theBot.myFocalPoint = tempActor.Location;
				theBot.FocalPoint = theBot.myFocalPoint;

				if (theBot.movingContinuous)
					theBot.GotoState('StartUp','MoveContinuous');
			}
			else
			{
				return;
			}
		}
	}
	//We comment this so turning commands do not interupt moving commands.
	//theBot.StopWaiting();
	//theBot.GotoState('Startup', 'Turning');

}

function ReceivedUse() {

	if (theBot == none || theBot.Pawn == none)
		return;

	if (Level.Game.IsA('BotScenario')) {
		if (BotScenario(Level.Game).canUseFactory(theBot)) {
			if (BotScenario(Level.Game).atFactory(theBot.Pawn.Location)) {
				BotScenario(Level.Game).UseFactory(theBot);
				SendLine("USED {Success True}");
			} else {
				SendLine("USED {Success False} {Reason out-of-range}");
			}
		} else {
			SendLine("USED {Success False} {Reason cant-use-factory}");
		}
	} else {
		SendLine("USED {Success False} {Reason wrong-game-type}");
	}

}

//Send a line to the client
function SendLine(string Text, optional bool bNoCRLF)
{
	if(bDebug)
		log("    Sending: "$Text);
	if(bNoCRLF)
		SendText(Text);
	else
		SendText(Text$Chr(13)$Chr(10));
}

//----------------- STATES

//Waiting state when connected
auto state waiting
{
Begin:
Waiting:
	sleep(5.0);
	goto 'Waiting';
}

state checkingPassword
{
Begin:
Waiting:
	sleep(5.0);
	goto 'Waiting';
}

state monitoring
{
Begin:
Running:
	if (sleepTime == 0) { //sanity check
		sleepTime = visionTime / float(locUpdateMultiplier);
		if (sleepTime < 0.05) sleepTime = 0.05;
		locUpdateMultiplierCurrent = visionTime / sleepTime;
	}

	if (bSynchronousMessagesOff)
		goto 'SynchronousOff';
	if(theBot != none && Level.Pauser == none && !theBot.IsInState('Dead') && !theBot.IsInState('GameEnded') ) {
		    if (locInfoCount >= locUpdateMultiplierCurrent || !bFirstSyncBAfterSpawnExported) {
			bFirstSyncBAfterSpawnExported = true;
			locInfoCount = 0;
			//export sync. batch
			SendLine("BEG {Time " $ Level.TimeSeconds $"}");
    	    if (egs >= 5) {
        		ExportGameStatus();
        		egs = 0;
	        }
			theBot.checkSelf();
			theBot.checkVision();
			if (theBot.bAutoTrace)
				AutoTrace();
			SendLine("END {Time " $ Level.TimeSeconds $"}");
			egs++;
//			log("EXPORT: SleepTime is " $ sleepTime $ " Level.TimeSeconds is " $ Level.TimeSeconds);
//                log("END EXPORTED");
            }
		locInfoCount++;
		
		theBot.ExportLocationUpdate();		
	}
	
//	log("SleepTime is " $ sleepTime $ ", locInfoCount " $ locInfoCount $ ", Level.TimeSeconds is " $ Level.TimeSeconds $ ", visionTime is " $ visionTime $ ", locUpdateMultiplierCurrent is " $ locUpdateMultiplierCurrent);

	if (bIterative)
		Level.Pauser=theBot.PlayerReplicationInfo;

	sleep(sleepTime);
	goto 'Running';
SynchronousOff:
	if (!bSynchronousMessagesOff)
		goto 'Running';
	sleep(1);
	goto 'SynchronousOff';
}


//-----------------

defaultproperties
{
    visionTime=0.250000
    locUpdateMultiplier=5
    bIgnoreMaxPlayers=false

	bExportGameInfo=true
	bExportMutators=true
	bExportITC=true
	bExportNavPoints=true
	bExportMovers=true
	bExportInventory=true
	bExportPlayers=true
	bSynchronousNavPoints=true
}
