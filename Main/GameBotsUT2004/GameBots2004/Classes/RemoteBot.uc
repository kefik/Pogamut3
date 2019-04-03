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
// RemoteBot.
// Basedon Bot class
//=============================================================================
class RemoteBot extends Bot
	config(GameBots2004);

var config bool bDebug;

//maybe link to bot's periphreal vision? UT bots need much less of an arc though
//like Pawn's PeriphrealVision, this value should be the cosine of the limits
//of visual field. (i.e. 0.707 = 45 degrees to each side, or a 90* arc.)
var config float remoteVisionLimit;

var config rotator DefaultRotationRate;

//The socket to the agent
var BotConnection myConnection;

//force that will be used for second jump command in Timer() function completing
//double jump sequence
var float doubleJumpForce;

//set to true if we want to do a double jump in Timer() function
var bool bShouldDoubleJump;

//set to true if we want to do a double dodge in Timer() function
var bool bShouldDoubleDodge;

//vector that will be used for second dodge command in Timer() function completing
//double dodge sequence
var vector DoubleDodgeDir;

//The three remote vars compliment the my vars right below. The only one
//that ever needs to be duplicated is RemoteEnemy and myTarget
//just need RemoteDestination || myDestination and RFocus || myFocus

//Who the remote bot is trying to shoot at. Used by the aiming code.
var Actor RemoteEnemy;
//Thing the remote bot is trying to move to.
var Actor RemoteDestination;
//Thing the remote bot is looking at.
var Actor myFocus;

//The spot the bot is shooting at
var FocusActorClass myTarget;

//If false, we will update myTarget location according our current focal point
var bool bTargetLocationLocked;

//The spot the bot is moving to
var vector myDestination;
//The spot the bot is looking at
var vector myFocalPoint;

//This is an indicator that we are in StartUp:MoveContinuous state. We need this, so turn
//command work properly when the bot is moving continuous!
var bool movingContinuous;

// time that last sent a WAL message
var float lastWallHitTime;

// time that last sent a BMP message
var float lastBumpTime;

var name BoneName;

var config float DeltaTime;

//var actor HitWall;

//var vector HitNormal;

//var vector HitLocation;

//Used for smooth movement, see StartUp state
var vector pendingDestination;

//If true bot will respawn automatically when killed
var config bool bAutoSpawn;

//When true auto raytracing producing sync. ATR messsages will be on
var config bool bAutoTrace;

//If true we will draw trace lines (results of AutoTrace() functionsin BotConnection)
//in the game
var config bool bDrawTraceLines;

//If we should spawn an actor at the point the bot has set as focal point
var config bool bShowFocalPoint;

//If we should provide aim correct based on bot Skill also when shooting at Location
var config bool bPerfectLocationAim;

//If we should include FadeOut attribute into message text or not. for debug.
var config bool bIncludeFadeOutInMsg;

//maximum number we can multiply BaseSpeed of the bot
var config float MaxSpeed;

//Used for changing the bot appearance. see GBxPawn.Setup() function
var config string DesiredSkin;

//TODO: Probably not used anymore - delete
var bool bSecondJumpPossible;

//For disabling auto pickup, custom varialbe, will affect Pawn.bCanPickupInventory
var config bool bDisableAutoPickup;

//By this default pawn speed will be multiplied ranges from 0.1 to 2
var config float SpeedMultiplier;

//helper variable to store the direction we want to go with CMOVE
var vector cmoveDirection;

//You need also to enable text to speech in UT2004 settings
var config bool bSpeakingBots;

//How far we see items and NavigationPoints, does not affect players and movers
var config int visionRadius;

//this is here, so we can properly debug the bot in java. it is the adress we need
//to connect to in order to debug the bot (in Pogamut the bots are now run each one in
//different JVM)
var string jmx;

//Fov angle in rads divided by two
var float FovAngleRadH;

struct SoundCache {
	var() config Actor Source;
	var() config int Id;
	var() config sound S;
	var() config vector SoundLocation;
	var() config vector Parameters;
	var() config bool Attenuate;
	var() config float ExportTime;
	var() config bool bWasExported;
	var() config bool bWasSet;
};

const SCArrayMaxLength = 50;

var SoundCache SoundCacheArray[SCArrayMaxLength];

function PostBeginPlay()
{
	Super.PostBeginPlay();
	//this is here so the default rotation rate may be configured from ini
	RotationRate = DefaultRotationRate;

	FovAngleRadH = ((FovAngle/180) * Pi)/2;
}

/* UnderLift()
called by mover when it hits a pawn with that mover as its pendingmover while moving to its destination
Not usefull for us, but overriding at least. :-/
*/
function UnderLift(Mover M)
{
//	myConnection.SendLine("UNDLFT {Id " $ M $ "}");
}

//override - using translocator in this function
event NotifyMissedJump()
{
	// SENDMESSAGE
	// !!! possible new message?

}

event NotifyFallingHitWall( vector HitNormal, actor HitActor)
{
	bNotifyFallingHitWall = false;
	//TryWallDodge(HitNormal, HitActor);
}

//Whenever we got some adrenaline
function AwardAdrenaline(float Amount)
{
	super.AwardAdrenaline(Amount);
	//Removed, because can be accesed from SLF message (monitoring the change)
	//myConnection.SendLine("ADG"$ib$as$"Amount"$ib$Amount$ae);
}

//override - using translocator in this function
function Actor FaceActor(float StrafingModifier)
{
	return none;
}

//overriding - performance gain
function Celebrate()
{
	//Pawn.PlayVictoryAnimation();
}

//Called at the start of the match
function StartMatch()
{
	// SENDMESSAGE
	// !!! possible new message
}

//Spawns a pickup class on the bot possition - if able the bot will collect the
//item instantly, otherwise it dissapears after 2 seconds
function pickup SpawnInventory( class<pickup> PickupType )
{
	local Pickup Copy;
	local Inventory Inv;

	if (Pawn == None)
		return None;

	Copy = Spawn(PickupType,Pawn,,Pawn.Location,rot(0,0,0));
	Copy.bDropped = true;
	Copy.LifeSpan = 2;

	if (!Pawn.bCanPickupInventory) {
		//if pawn can't pickup inventory we will do it directly
		Inv = Copy.SpawnCopy(Pawn);
		Copy.AnnouncePickup(Pawn);
        if ( Inv != None )
			Inv.PickupFunction(Pawn);

	}

	return Copy;
}

//This function is called by the RESPAWN command - it makes sure everything will work ok
function RespawnPlayer(optional vector startLocation, optional rotator startRotation)
{
	if (IsInState('Dead') && !bAutoSpawn) //
	{
		RemoteRestartPlayer(startLocation,startRotation);
	}
	else if (bAutoSpawn)
	{
		bAutoSpawn = false; //otherwise it would get respawned in Dead state without specifyed locatin, rotation
		if (Pawn != None)
			Pawn.Died(none, class'DamageType', Pawn.Location);
		if (Pawn != None)
			Pawn.Destroy();
		GotoState('Dead');
		RemoteRestartPlayer(startLocation,startRotation);
		bAutoSpawn = true;

	}
	else if (!IsInState('Dead') && !bAutoSpawn)
	{
		if (Pawn != None)
			Pawn.Died(none, class'DamageType', Pawn.Location);
		if (Pawn != None)
			Pawn.Destroy();
		GotoState('Dead');
		RemoteRestartPlayer(startLocation,startRotation);
	}
}

/** Kills the bot */
function KillBot() {
	if (!IsInState('Dead')) {
		if (Pawn != None)
			Pawn.Died(none, class'DamageType', Pawn.Location);
		if (Pawn != None)
			Pawn.Destroy();
		GotoState('Dead');
	}
}

//This function handles actual bot respawning - when it dies for instance
function RemoteRestartPlayer(optional vector startLocation, optional rotator startRotation)
{
	BotDeathMatch(Level.Game).RemoteRestartPlayer( self, startLocation, startRotation );
	// notify connection that we should export first sync. batch right away (before loc update)
	myConnection.bFirstSyncBAfterSpawnExported = false;
	if (Pawn != None) {
		GotoState('StartUp','DoStop');
	} else {
		log("Pawn is None after ResartPlayer");
	}
}

//Function which determines if our weapon should fire again or stop firing
function bool WeaponFireAgain(float RefireRate, bool bFinishedFire)
{

	if (!RemoteHasAmmo())
	{
		Super.StopFiring();
		return false;
	}

	return true;
}

//Use from local code - stops firing and ticks weapon to let it stop
function HaltFiring()
{
	//Pawn.Weapon.StopFire(Pawn.Weapon.BotMode);
	bFire = 0;
	bAltFire = 0;
	Super.StopFiring();
	//Pawn.Weapon.Tick(0.001);
}

//we have to override this function, otherwise US will reset our vision settings
//and etc..
function ResetSkill()
{
	//log("We are in ResetSkill");
}

//Overriding function, otherwise code from states in Bot.uc would be executed
function HearPickup(Pawn Other)
{
	//myConnection.SendLine("HRP" $ib$as$"Rotation" $ib$ rotator(Location - Other.Location) $ae);
}

//Overriding function, otherwise code from states in Bot.uc would be executed
function HearNoise (float Loudness,actor Actor )
{

}

//Called when our bot may hear some sound.
event AIHearSound( Actor Actor, int Id, sound S, vector SoundLocation, vector Parameters, bool Attenuate ) {
	local float floatDist;
	local SoundCache cache;
	local int i;

 	if (Pawn == none)
		return;

	floatDist = VSize(Pawn.Location - Actor.Location);

	if (floatDist > S.BaseRadius)
		return;

	for (i = 0; i < SCArrayMaxLength; i++) {
		if (SoundCacheArray[i].Source == Actor && SoundCacheArray[i].S == S) {
			if (!SoundCacheArray[i].bWasExported || (Level.TimeSeconds - SoundCacheArray[i].ExportTime < 1)) {
				return; //already present there and exported less than one second ago or not exported at all - won't add it
			}
		}
	}
	for (i = 0; i < SCArrayMaxLength; i++) {
		if ( (SoundCacheArray[i].bWasExported && (Level.TimeSeconds - SoundCacheArray[i].ExportTime > 1) ) || SoundCacheArray[i].Source == none) {
			cache.Source = Actor;
			cache.Id = Id;
			cache.S = S;
			cache.SoundLocation = Actor.Location;
			cache.Parameters = Parameters;
			cache.Attenuate = Attenuate;
			cache.ExportTime = 0;
			cache.bWasExported = false;
			cache.bWasSet = true;
			SoundCacheArray[i] = cache;
			return;
		}
	}
}

function checkSounds() {
	local int i;
	local string prefix;
	local string outstring;
	local vector myLocation, soundLocation;

	for (i = 0; i < SCArrayMaxLength; i++) {
		if (!SoundCacheArray[i].bWasExported && SoundCacheArray[i].Source != none) {
			if( SoundCacheArray[i].Source.IsA('Pickup') )
    			prefix = "HRP";
		    else
    			prefix = "HRN";

			if (Pawn != none){
				myLocation = Pawn.Location;
			} else {
				myLocation = Location;
			}
			soundLocation = SoundCacheArray[i].SoundLocation;

			outstring = prefix $ " {Source " $ myConnection.GetUniqueId(SoundCacheArray[i].Source) $
				"} {Type " $ SoundCacheArray[i].Source.Class $
				"} {Rotation " $ rotator(soundLocation - myLocation) $
				"} {Distance " $ VSize(soundLocation - myLocation) $
				"}";

			RemoteAIHearSound(SoundCacheArray[i].Source, SoundCacheArray[i].Id, SoundCacheArray[i].S, soundLocation, SoundCacheArray[i].Parameters, SoundCacheArray[i].Attenuate);

			myConnection.SendLine(outstring);

			SoundCacheArray[i].bWasExported = true;
			SoundCacheArray[i].ExportTime = Level.TimeSeconds;
		}
	}
}

/* Is here only because of the observers - we notify observers of exported sounds.
*/
event RemoteAIHearSound(Actor actor, int Id, sound S, vector SoundLocation, vector Parameters, bool Attenuate) {

}

event SoakStop(string Problem)
{
	myConnection.SendLine("SOAKED {Problem " $ Problem $"}");
	log("We are Soaked! Problem: "$Problem);
}

//Zone the bot is standing on has changed
singular event BaseChange()
{
	Super(Pawn).BaseChange();
	// !!! possible SENDMESSAGE
	//myConnection.SendLine("BASETEST" $ib$as$ "Actor" $ib$ HitActor $ae$ib$as$"Location" $ib$ HitLocation $ae);
}


//Called right before bot falls off something
//If bot running, set bCanJump to true - let him fall
//if walking, won't fall
event MayFall()
{
	local bool bFall;

	bFall = !Pawn.bIsWalking;

	if(!bfall)
	{
		GotoState('Startup', 'Begin');
	}

	myConnection.SendLine("FAL {Fell " $ bFall $
		"} {Location " $ Pawn.Location $
		"}");

	Pawn.bCanJump = bFall;
}

//Called when the bot land on the ground after falling
function bool NotifyLanded(vector HitNormal)
{
	myConnection.SendLine("LAND {HitNormal " $ HitNormal $ "}");

	//restart, so we continue moving and not going back to our previous
	//destination, as we may be over it because of the fall
	if (movingContinuous)
		gotoState('StartUp','MoveContRepeat');

	return true;
}

//TODO: This function returns wrong numbers - reimplement?
function float GetSpeed()
{
	if(Pawn.bIsWalking)
	{
		return Pawn.WalkingPct;
	}
	else
	{
		return 1.0;
	}
}

//can be autocalled when bot is shot by team mate
function YellAt(Pawn Moron);

function HandlePlayerInput(PlayerController inputPlayer, int Key)
{
	local string temp;

	temp = "";
	if (inputPlayer.IsA('GBxPlayer') && GBxPlayer(inputPlayer).DialogId != "")
		temp = " {DialogId " $ GBxPlayer(inputPlayer).DialogId $ "}";

	myConnection.SendLine( "PLI {Id " $ myConnection.GetUniqueId(inputPlayer) $
		"} {Key " $ Key $
		"}" $ temp);


}

//This is called from GBxPawn, so we get virtual IPK message also for inventory that is
//added for bot at the beginning of the game, otherwise we would have problems with counting
//ammunition and so
function HandleStartPickup(class<Pickup> PickupClass, optional int amount, optional int amountSec)
{
	local string InventoryId, outstring;
	local string temp, CheatId, mapId;
	local bool bDivided;

	local Inventory Inv;

	//HACK - this checks if the default weapons are properly set, if not some mutator maybe
	//prevents them from being added and we shouldn't generate IPK message. Only translocator
	//can be allowed without ammo
/*	TransLocClass = class<Transpickup> (PickupClass);
	if (TransLocClass == none) {
		if (amount == 0 && amountSec == 0)
			return;
	}*/

	Inv = Pawn.FindInventoryType(PickupClass.default.InventoryType);
	if (Inv == none) {
		return;
	}

	InventoryId = string(Inv);

    BotDeathMatch(Level.Game).GameBotsID++;

    //We create custom id that will look like normal id
    bDivided = true;
    CheatId = string(PickupClass);
    while (bDivided)
    {
		bDivided = Divide(CheatId, ".", temp, CheatId);
	}

	Divide(string(Level), ".", mapId, temp);

    CheatId = mapId $ "." $ CheatId $ BotDeathMatch(Level.Game).GameBotsID;

	outstring = "IPK {Id " $ CheatId $
		"} {InventoryId " $ InventoryId $
		"} {Location " $ Pawn.Location $
		"} {Dropped " $ PickupClass.default.bDropped $
		"} {Type " $ PickupClass $
		"} {Amount " $ amount $
		"} {AmountSec " $ amountSec $
		"}";

	myConnection.SendLine(outstring);
}

//Called each time we have picked up something - ammo, weapon, health etc.
function HandlePickup(Pickup pickup)
{
	local string InventoryId;
	local string outstring;

	InventoryId = string(Pawn.FindInventoryType(pickup.InventoryType));

	outstring = "IPK {Id " $ myConnection.GetUniqueId(pickup) $
		"} {InventoryId " $ InventoryId $
		"} {Location " $ pickup.Location $
		"} {Amount " $ myConnection.GetItemAmount(pickup) $
		"} {Dropped " $ pickup.bDropped $
		"} {Type " $ pickup.Class $
		"}";

	if (pickup.IsA('WeaponPickup')) {
		outstring $= " {AmountSec " $ WeaponPickup(pickup).AmmoAmount[1] $ "}";
	}

	myConnection.SendLine(outstring);
}

// Called when get new weapon or ammunition for weapon we do not have yet.
// Called just once per weapon type or per new ammunition type (notify new object in our inventory, NOT pickup)
// For exporting weapons we have new attributes.
function NotifyAddInventory( Inventory inputInventory )
{
	local string outstring;

	Super.NotifyAddInventory(inputInventory);

    outstring = "AIN {Id " $ myConnection.GetUniqueId(inputInventory) $
		"} {Type " $ inputInventory.class $
		"} {PickupType " $ inputInventory.PickupClass $
		"}";

	myConnection.SendLine(outstring);
}

function SetHealth(int amount)
{
	if (Pawn == none)
		return;
	if ((amount > 0) && (amount < 200)) {
		//log("Set health: " $amount);
		Pawn.Health = amount;
	}
}

function SetAdrenaline(int amount)
{
	if ((amount > 0) && (amount <= 100))
		Adrenaline = amount;
}

function BotVoiceMessage(name messagetype, byte MessageID, Controller Sender);

// !!! need functions to send arbitrary string messages

function SendVoiceMessage(PlayerReplicationInfo Sender, PlayerReplicationInfo Recipient, name messageType, byte messageID, name broadcastType)
{
	super.SendVoiceMessage( Sender, Recipient, messageType, messageID, broadcastType );
}

//functions to send tokenized messages
/*
function SendTeamMessage(PlayerReplicationInfo Recipient, name MessageType, byte MessageID, float Wait)
{
	//log(self@"Send message"@MessageType@MessageID@"at"@Level.TimeSeconds);
	if ( (MessageType == OldMessageType) && (MessageID == OldMessageID)
		&& (Level.TimeSeconds - OldMessageTime < Wait) )
		return;

	//log("Passed filter");
	OldMessageID = MessageID;
	OldMessageType = MessageType;

	SendVoiceMessage(PlayerReplicationInfo, Recipient, MessageType, MessageID, 'TEAM');
}*/
/*
function SendGlobalMessage(PlayerReplicationInfo Recipient, name MessageType, byte MessageID, float Wait)
{
	//log(self@"Send message"@MessageType@MessageID@"at"@Level.TimeSeconds);
	if ( (MessageType == OldMessageType) && (MessageID == OldMessageID)
		&& (Level.TimeSeconds - OldMessageTime < Wait) )
		return;

	//log("Passed filter");
	OldMessageID = MessageID;
	OldMessageType = MessageType;

	SendVoiceMessage(PlayerReplicationInfo, Recipient, MessageType, MessageID, 'GLOBAL');
}*/


//handles sending messages to all and to team
function RemoteBroadcast( string Id, coerce string Msg, int TeamIndex, bool bGlobal, float FadeOut )
{
	local Controller C;
	local string MsgForVoice;

	MsgForVoice = Msg;
	if (bIncludeFadeOutInMsg)
		Msg = Msg $ " FadeOut=" $ FadeOut $ " s.";


    //Set the text bubble, which will be shown on the HUDs of Human players
    //when they see the bot
    if (Pawn != none)
   	{
   		if (bSpeakingBots)
	   		Pawn.TextToSpeech( MsgForVoice, 10 );
		GBxPawn(Pawn).SetTextBubble(Id, Msg, bGlobal, FadeOut);
	}

	if (Id != "")
	{
		for (C = Level.ControllerList; C != none; C=C.NextController)
		{
			if ( (C $ C.PlayerReplicationInfo.PlayerId) == Id)
			{
				if (C.IsA('RemoteBot'))
					RemoteBot(C).RemoteNotifyTeamMessage(self,"Private:"$Msg);
				if (C.IsA('PlayerController'))
					PlayerController(C).TeamMessage(self.PlayerReplicationInfo, self.PlayerReplicationInfo.PlayerName$":Private:"$Msg, '');

       			return;
			}
		}

		//If we haven't found Id or Id doesn't belong to RemoteBot we will end
		return;
	}

	if ( bGlobal )
	{
	    //Send the message to the game channel
	    Level.Game.Broadcast(self, Msg, 'Say');
	    //Send the message to RemoteBots
	    SendGlobalMessage( Msg );
	    return;
	}

	if (Level.Game.bTeamGame)
	{
		//Send the message to team channel
		Level.Game.BroadcastTeam(self, Msg, 'TeamSay');
		//Send the message to RemoteBots
		SendTeamMessage( Msg, TeamIndex );
		return;
	}
}

//Event for receiving string messages, called manually
event RemoteNotifyClientMessage( Actor C, coerce string S)
{
	local string plName;
	//Could cause some parsing problems, so replacing
	S = Repl(S, "{", "_", false );
	S = Repl(S, "}", "_", false );

	if (C.IsA('Controller')) {
		plName = Controller(C).PlayerReplicationInfo.PlayerName;
	} else {
		plName = myConnection.GetUniqueId(C);
	}

	myConnection.SendLine("VMS {Id "$ myConnection.GetUniqueId(C) $
		"} {Name " $ plName $
		"} {ControlServer " $ C.IsA('ControlConnection') $
		"} {Text " $ S $ "}");
}

//Event for receiving string messages, called manually
event RemoteNotifyTeamMessage( Actor C, coerce string S )
{
	local string plName;
	//Could cause some parsing problems, so replacing
	S = Repl(S, "{", "_", false );
	S = Repl(S, "}", "_", false );

	if (C.IsA('Controller')) {
		plName = Controller(C).PlayerReplicationInfo.PlayerName;
	} else {
		plName = myConnection.GetUniqueId(C);
	}

	myConnection.SendLine("VMT {Id "$ myConnection.GetUniqueId(C) $
		"} {Name " $ plName $
		"} {ControlServer " $ C.IsA('ControlConnection') $
		"} {Text " $ S $"}");
}

//Send the team message to RemoteBots
function SendTeamMessage( string S, int TeamIndex)
{
	local Controller C;

	for( C = Level.ControllerList; C != None; C = C.NextController ) {
		if( C.PlayerReplicationInfo.Team != none ) {
			if (C.PlayerReplicationInfo.Team.TeamIndex == TeamIndex) {
				if( C.isA('RemoteBot') && C != self ) {
					RemoteBot(C).RemoteNotifyTeamMessage(self, S);
				} else if (C.IsA('ObservedPlayer')) {
				    ObservedPlayer(C).ReceivedTeamMessage(self, S);
				}
			}
		}
	}
}

//Send the global message to RemoteBots
function SendGlobalMessage( string S )
{
	local Controller C;

	for( C = Level.ControllerList; C != none; C = C.NextController )
	{
		if( C.isA('RemoteBot') && C != self )
		{
			RemoteBot(C).RemoteNotifyClientMessage( self , S);
		}
		if (C.IsA('ObservedPlayer')) {
		    ObservedPlayer(C).ReceivedGlobalMessage(self, S);
		}
	}
}

//other code may try to call this. make sure nada happens
//!!! need to change - make sure orders are actually recorded somewhere
//             for reference
function SetOrders(name NewOrders, Controller OrderGiver);

//events that are called when head, feet or whole bot changes zones
//e.g. air to water or lava
//only way to tell if underwater until drowning damage starts
/*
function FootZoneChange(ZoneInfo newFootZone)
{
	Super.FootZoneChange(newFootZone);

	myConnection.SendLine("ZCF" $ib$as$ "Id" $ib$ newFootZone $ae);
}
function HeadZoneChange(ZoneInfo newHeadZone)
{
	Super.HeadZoneChange(newHeadZone);

	myConnection.SendLine("ZCH" $ib$as$ "Id" $ib$ newHeadZone $ae);
} */

//The bot (or some part of the bot - legs) entered new volume
event bool NotifyPhysicsVolumeChange( PhysicsVolume NewVolume )
{

	//this code is taken from super class (Bot). Had to disable automatic jumping out of water
	//super class is not called anymore
	if ( newVolume.bWaterVolume )
	{
		bPlannedJump = false;
		if (!Pawn.bCanSwim)
			MoveTimer = -1.0;
		else if (Pawn.Physics != PHYS_Swimming)
			Pawn.setPhysics(PHYS_Swimming);
	}
	else if (Pawn.Physics == PHYS_Swimming)
	{
		if ( Pawn.bCanFly )
			 Pawn.SetPhysics(PHYS_Flying);
		else
		{
			Pawn.SetPhysics(PHYS_Falling);
			/*if ( Pawn.bCanWalk && (Abs(Pawn.Acceleration.X) + Abs(Pawn.Acceleration.Y) > 0)
				&& (Destination.Z >= Pawn.Location.Z)
				&& Pawn.CheckWaterJump(jumpDir) )
			{
				Pawn.JumpOutOfWater(jumpDir);
				bNotifyApex = true;
				bPendingDoubleJump = true;
			}*/
		}
	}

	myConnection.SendLine("VCH {Id " $ myConnection.GetUniqueId(NewVolume) $
		"} {ZoneVelocity " $ NewVolume.ZoneVelocity $  //vector
		"} {ZoneGravity " $ NewVolume.Gravity $  //vector
		"} {GroundFriction " $ NewVolume.GroundFriction $  //float
		"} {FluidFriction " $ NewVolume.FluidFriction $  //float
		"} {TerminalVelocity " $ NewVolume.TerminalVelocity $  //float
		"} {WaterVolume " $ NewVolume.bWaterVolume $
		"} {PainCausing " $ NewVolume.bPainCausing $
		"} {Destructive " $ NewVolume.bDestructive $
		"} {DamagePerSec " $ NewVolume.DamagePerSec $ //float
		"} {DamageType " $ NewVolume.DamageType $
		"} {NoInventory " $ NewVolume.bNoInventory $
		"} {MoveProjectiles " $ NewVolume.bMoveProjectiles $
		"} {NeutralZone " $ NewVolume.bNeutralZone $
		"}");

	return false;

}

/**
* Can be called from BotConnection or ControlConnection to notify Control Message sent by bot or control server.
* ps - parameter string, pi - integer, pf - float, pb - boolean
*/
function NotifyControlMessage(string type, string ps1, string ps2, string ps3, string pi1, string pi2, string pi3, string pf1, string pf2, string pf3, string pb1, string pb2, string pb3) {
	local string outstring;

	outstring = "CTRLMSG {Type " $ type $ "}";
	if (ps1 != "")
		outstring $= " {PS1 " $ ps1 $ "}";
	if (ps2 != "")
		outstring $= " {PS2 " $ ps2 $ "}";
	if (ps3 != "")
		outstring $= " {PS3 " $ ps3 $ "}";
	if (pi1 != "")
		outstring $= " {PI1 " $ pi1 $ "}";
	if (pi2 != "")
		outstring $= " {PI2 " $ pi2 $ "}";
	if (pi3 != "")
		outstring $= " {PI3 " $ pi3 $ "}";
	if (pf1 != "")
		outstring $= " {PF1 " $ pf1 $ "}";
	if (pf2 != "")
		outstring $= " {PF2 " $ pf2 $ "}";
	if (pf3 != "")
		outstring $= " {PF3 " $ pf3 $ "}";
	if (pb1 != "")
		outstring $= " {PB1 " $ pb1 $ "}";
	if (pb2 != "")
		outstring $= " {PB2 " $ pb2 $ "}";
	if (pb3 != "")
		outstring $= " {PB3 " $ pb3 $ "}";
	myConnection.SendLine(outstring);
}

//The bot entered a new zone
function ZoneChange(ZoneInfo newZone)
{
	Super.ZoneChange(newZone);
	if (myConnection == none)
		return;
	myConnection.SendLine("ZCB {Id " $ myConnection.GetUniqueId(NewZone) $ "}");
}

//Not used yet - should we use this somewhere?
function GetPhysicsVolume()
{
	local PhysicsVolume V, winner;

	winner = none;

	ForEach TouchingActors(class'PhysicsVolume',V)
	{
		if (winner == none)
			winner = V;
		else
		{
			if (winner.Priority < V.Priority )
			{
				winner = V;
			}
		}
	}

	if (winner != none)
		NotifyPhysicsVolumeChange( winner );
	else
		myConnection.SendLine("VCH {Id None}");

}

//Called when weapon is switched - may happen automatically
function ChangedWeapon()
{
//	local int usealt;

	if ( (Pawn.Weapon != none) && (Pawn.Weapon == Pawn.PendingWeapon) )
	{
		SwitchToBestWeapon();
		if ( Pawn.Weapon.GetStateName() == 'DownWeapon' )
			Pawn.Weapon.GotoState('Idle');
		Pawn.PendingWeapon = None;
	}
	else
		super.ChangedWeapon();

	if ( Pawn.Weapon != None )
	{
		if (bFire > 0)
		{
 			bAltFire = 0;
			bFire = 1;
			Pawn.Weapon.Fire(1.0);
		}
 		else if (bAltFire > 0)
 		{
			bAltFire = 0;
			bFire = 1;
			Pawn.Weapon.AltFire(1.0);
		}
		Pawn.Weapon.SetHand(0);

	}
	// !!! use or just make people get from status update
	myConnection.SendLine("CWP {Id " $ myConnection.GetUniqueId(Pawn.Weapon) $
				"} {PrimaryAmmo " $ Pawn.Weapon.AmmoAmount(0) $
				"} {SecondaryAmmo " $ Pawn.Weapon.AmmoAmount(1) $
				"} {Type " $ Pawn.Weapon.Class $"}");
}

event Touch( Actor Other )
{
	//TODO: Add a check if actor is a player or bot
	myConnection.SendLine("TCH {Id " $ myConnection.GetUniqueId(Other) $
							"} {Location " $ Other.Location $
							"} {Rotation " $ Other.Rotation $"}");
}

// called from pathnodes that unitelligent creatures are supposed to avoid
function FearThisSpot(AvoidMarker aSpot);

//called from BotConnection - used to export ray info for observers
event NotifyAutoTraceRayResult(
	string Id,
	vector from,
	vector to,
	bool fastTrace,
	bool ProvideFloorCorrection,
	bool result,
	vector hitNormal,
	vector hitLocation,
	bool traceActors,
	string hitId)
{
	//nothing here, observers override this to get info about ray tracing
}

//called on hitting a wall
event bool NotifyHitWall( vector HitNormal, actor HitWall )
{
	if ( Level.TimeSeconds - 0.5 >= lastWallHitTime )
	{
		myConnection.SendLine("WAL {Id " $ myConnection.GetUniqueId(HitWall) $
			"} {Normal " $ HitNormal $
			"} {Location " $ Pawn.Location $"}");

		lastWallHitTime = Level.TimeSeconds;
	}

	return true;
}


//called on collisions with other actors
event bool NotifyBump(actor Other)
{
	local vector VelDir, OtherDir;
	local float speed;

	if ( TimerRate <= 0 )
		setTimer(1.0, false);

	speed = VSize(Velocity);
	if ( speed > 10 )
	{
		VelDir = Velocity/speed;
		VelDir.Z = 0;
		OtherDir = Other.Location - Location;
		OtherDir.Z = 0;
		OtherDir = Normal(OtherDir);
		if ( (VelDir Dot OtherDir) > 0.8 )
		{
			Velocity.X = VelDir.Y;
			Velocity.Y = -1 * VelDir.X;
			Velocity *= FMax(speed, 280);
		}
	}

	if ( Level.TimeSeconds - 0.5 >= lastBumpTime )
	{

		myConnection.SendLine("BMP {Id " $ myConnection.GetUniqueId(Other) $
			"} {Location " $ Other.Location $"}");

		lastBumpTime = Level.TimeSeconds;
	}

	// Need to disable bumping ???
	//Disable('Bump');
	//TODO: Experiment with this?
	return false;
}

//called periodicaly for each player in view
function SeePlayer(Pawn SeenPlayer)
{
/* Temporary suspended - for now
	local Controller C;
	local string TeamIndex, OutString, WeaponClass;

	if (SeenPlayer == none)
		return;

   	C = SeenPlayer.Controller;

	if(Level.Game.isA('BotTeamGame'))
	{
		TeamIndex = string(C.PlayerReplicationInfo.Team.TeamIndex);
	}
	else
	{
		TeamIndex = "255";
	}
	if (C.Pawn.Weapon != none)
	{
		WeaponClass = string(C.Pawn.Weapon.Class);
	}
	else
	{
		WeaponClass = "None";
	}
	outstring = "PLR {Id " $ myConnection.GetUniqueId(C) $
	  "} {Spectator " $ (C.PlayerReplicationInfo.bIsSpectator || C.IsInState('Spectating')) $
		"} {Rotation " $ C.Pawn.Rotation $
		"} {Location " $ C.Pawn.Location $
		"} {Velocity " $ C.Pawn.Velocity $
		"} {Name " $ C.PlayerReplicationInfo.PlayerName $
		"} {Team " $ TeamIndex $
		"} {Reachable " $ actorReachable(C.Pawn) $
		"} {Weapon " $ WeaponClass $
		"}";

	if((C.Pawn.Weapon != none) && C.Pawn.Weapon.GetFireMode(0).IsFiring() )
		outstring = outstring $" {Firing 1}";
	else if((C.Pawn.Weapon != none) && C.Pawn.Weapon.GetFireMode(1).IsFiring() )
		outstring = outstring $" {Firing 2}";
	else
		outstring = outstring $" {Firing 0}";

	myConnection.sendLine(outstring);
*/
}

//picks up an item if we are touching it
function RemotePickup(string target)
{
	local Pickup P;

	//log("My target "$ target);

	if ((target != "") && (Pawn != none))
		foreach Pawn.TouchingActors(class'Pickup', P) {
			log("First touching: " $ P $ " and his id: " $ myConnection.getUniqueId(P));
			if (target == myConnection.getUniqueId(P))
			{
				Pawn.bCanPickupInventory = true;
				P.Touch(Pawn);
				Pawn.bCanPickupInventory = false;
			    //log("We've got it");
				break;
			}
		}
}

/* Added func RemoteKilled - should make things cleaner, called from GameTypeGame server
	25.10.2006 Michal Bida
*/
function RemoteKilled(Controller Killer, Controller Killed, Pawn KilledPawn, class<DamageType> damageType)
{
	local string outstring;

	outstring = "KIL {Id " $ myConnection.GetUniqueId(Killed) $
		"} {KilledPawn " $ KilledPawn $
        "} {Killer " $ myConnection.GetUniqueId(Killer) $
		"}";
	if (CanSee(KilledPawn))
	{
		outstring = outstring $ " {DamageType " $ damageType $
        "} {DeathString " $ damageType.default.DeathString $
        //"} {WeaponName " $ damageType.static.DamageWeaponName $//commented out. Is always blank in DamageType class...
        "} {Flaming " $ damageType.default.bFlaming $
        "} {CausedByWorld " $ damageType.default.bCausedByWorld $
        "} {DirectDamage " $ damageType.default.bDirectDamage $
        "} {BulletHit " $ damageType.default.bBulletHit $
        "} {VehicleHit " $ damageType.default.bVehicleHit $
		"}";
	}

	myConnection.sendLine(outstring);
}

/* Added func RemoteDied - should make things cleaner, called from GameTypeGame server
	25.10.2006 Michal Bida
*/
function RemoteDied(Controller Killer, class<DamageType> damageType)
{
	myConnection.SendLine("DIE {Killer " $ myConnection.GetUniqueId(Killer) $
		"} {DamageType " $ damageType $
        "} {DeathString " $ damageType.default.DeathString $
        //"} {WeaponName " $ damageType.default.DamageWeaponName $//commented out. Is always blank in DamageType class...
        "} {Flaming " $ damageType.default.bFlaming $
        "} {CausedByWorld " $ damageType.default.bCausedByWorld $
        "} {DirectDamage " $ damageType.default.bDirectDamage $
        "} {BulletHit " $ damageType.default.bBulletHit $
        "} {VehicleHit " $ damageType.default.bVehicleHit $
		"}");
}

/* NearWall() returns true if there is a nearby barrier at eyeheight, and
changes Focus to a suggested value
*/
//Potentially usefull
/*
function bool NearWall(float walldist)
{
	local actor HitActor;
	local vector HitLocation, HitNormal, ViewSpot, ViewDist, LookDir;

	LookDir = vector(Rotation);
	ViewSpot = Location + BaseEyeHeight * vect(0,0,1);
	ViewDist = LookDir * walldist;
	HitActor = Trace(HitLocation, HitNormal, ViewSpot + ViewDist, ViewSpot, false);
	if ( HitActor == None )
		return false;

	ViewDist = Normal(HitNormal Cross vect(0,0,1)) * walldist;
	if (FRand() < 0.5)
		ViewDist *= -1;

	if ( FastTrace(ViewSpot + ViewDist, ViewSpot) )
	{
		Focus = Location + ViewDist;
		return true;
	}

	ViewDist *= -1;

	if ( FastTrace(ViewSpot + ViewDist, ViewSpot) )
	{
		Focus = Location + ViewDist;
		return true;
	}

	Focus = Location - LookDir * 300;
	return true;
}
*/

//Do adrenaline combo
function RemoteCombo( string ComboClassName )
{
	if ((Pawn != none) && !Pawn.InCurrentCombo() && (Adrenaline == AdrenalineMax) )
		Pawn.DoComboName( ComboClassName );
}

//Jumps a bot
function RemoteJump(bool bDouble, float delay, float force)
{
	local xPawn xP;
	xP = xPawn(Pawn);
	if (Pawn == none)
		return;

	if (force == 0) {
		if (bDouble) //our bots had problems when the spots were really on the verge of access with - adding 50 to max.
			force = 2 * Pawn.JumpZ + xP.MultiJumpBoost + 50;
		else
			force = Pawn.JumpZ;
	}

	if (bDouble) {
		doubleJumpForce = force - xP.JumpZ;
		if (doubleJumpForce < 0) {
			doubleJumpForce = 0;
			bDouble = false;
		} else if (doubleJumpForce > xP.JumpZ + xP.MultiJumpBoost + 50) {
			doubleJumpForce = xP.JumpZ + xP.MultiJumpBoost + 50;
		}
	}

	if (force > xP.JumpZ)
		force = xP.JumpZ;

	if (Pawn.Physics != PHYS_Falling)  //TODO: Check all bad physics
	{
		xP.PlayDoubleJump();
		xP.SetPhysics(PHYS_Falling);
		xP.Velocity.Z = force;

		if (bDouble) {
			if (delay == 0)
				delay = 0.5;
			bShouldDoubleJump = true;
			bShouldDoubleDodge = false;
			SetTimer(delay, false);
		} else {
			xP.PlayOwnedSound(xP.GetSound(EST_DoubleJump), SLOT_Pain, xP.GruntVolume,,80);
		}
	}
}

//Make bot dodge
function bool RemoteDodge(vector Dir, bool bDouble, bool bWall)
{
	local vector X,Y,Z;

	if (Pawn == none)
		return false;

	if (bWall) {
		return RemoteTryWallDodge(Dir);
	}

	if ( Abs(X Dot Dir) > Abs(Y Dot Dir) )
	{
		if ( (X Dot Dir) > 0 )
			UnrealPawn(Pawn).CurrentDir = DCLICK_Forward;
		else
			UnrealPawn(Pawn).CurrentDir = DCLICK_Back;
	}
	else if ( (Y Dot Dir) < 0 )
		UnrealPawn(Pawn).CurrentDir = DCLICK_Left;
	else
		UnrealPawn(Pawn).CurrentDir = DCLICK_Right;

	if (bDouble) {
		bShouldDoubleDodge = true;
		bShouldDoubleJump = false;
		DoubleDodgeDir = Dir;
		SetTimer(0.4, false);
	}

    Pawn.PerformDodge(UnrealPawn(Pawn).CurrentDir, Normal(Dir), vect(0,0,0));
	return true;
}

function bool RemoteTryWallDodge(vector WallDirection)
{
	local vector X,Y,Z, Dir, TargetDir, NewHitNormal, HitLocation, Extent;
	local float DP;
	local Actor NewHitActor;

	if (!Pawn.bCanWallDodge)
		return false;

	/*if ( (Pawn.Velocity.Z < -150) && (FRand() < 0.4) )
		return false;
	*/
	// check that it was a legit, visible wall
	Extent = Pawn.CollisionRadius * vect(1,1,0);
	Extent.Z = 0.5 * Pawn.CollisionHeight;
	NewHitActor = Trace(HitLocation, NewHitNormal, Pawn.Location - 32*WallDirection, Pawn.Location, false, Extent);
	if ( NewHitActor == None || (Abs(NewHitNormal.Z) > 0.7))
		return false;

	GetAxes(Pawn.Rotation,X,Y,Z);

	Dir = NewHitNormal;
	Dir.Z = 0;
	Dir = Normal(Dir);
		  /*
	if ( InLatentExecution(LATENT_MOVETOWARD) )
	{
		TargetDir = MoveTarget.Location - Pawn.Location;
		TargetDir.Z = 0;
		TargetDir = Normal(TargetDir);
		DP = HitNormal Dot TargetDir;
		if ( (DP >= 0)
			&& (VSize(MoveTarget.Location - Pawn.Location) > 200) )
		{
			if ( DP < 0.7 )
				Dir = Normal( TargetDir + HitNormal * (1 - DP) );
			else
				Dir = TargetDir;
		}
	}             */
	if ( Abs(X Dot Dir) > Abs(Y Dot Dir) )
	{
		if ( (X Dot Dir) > 0 )
			UnrealPawn(Pawn).CurrentDir = DCLICK_Forward;
		else
			UnrealPawn(Pawn).CurrentDir = DCLICK_Back;
	}
	else if ( (Y Dot Dir) < 0 )
		UnrealPawn(Pawn).CurrentDir = DCLICK_Left;
	else
		UnrealPawn(Pawn).CurrentDir = DCLICK_Right;

 	//bPlannedJump = true;
	Pawn.PerformDodge(UnrealPawn(Pawn).CurrentDir, Dir,Normal(Dir cross vect(0,0,1)));
	return true;
}

event Timer()
{
	local xPawn xP;
	xP = xPawn(Pawn);
	if (bShouldDoubleJump) {
		xP.PlayDoubleJump();
		xP.SetPhysics(PHYS_Falling);
		xP.Velocity.Z = doubleJumpForce;
		xP.PlayOwnedSound(xP.GetSound(EST_DoubleJump), SLOT_Pain, xP.GruntVolume,,80);
		bShouldDoubleJump = false;
		doubleJumpForce = 0;
	}
	if (bShouldDoubleDodge) {
		Pawn.PerformDodge(UnrealPawn(Pawn).CurrentDir, Normal(DoubleDodgeDir), vect(0,0,0));
		bShouldDoubleDodge = false;
		DoubleDodgeDir = vect(0,0,0);
	}
}

//Intercept FireWeapon - is called from other code
function FireWeapon();

function bool RemoteHasAmmo()
{
	if ( (Pawn == none) || (Pawn.Weapon == none) )
		return false;

	if (bFire == 1)
	{
		return (Pawn.Weapon.AmmoAmount(0) >= Pawn.Weapon.GetFireMode(0).AmmoPerFire);
	}

	if (bAltFire == 1)
	{
		return (Pawn.Weapon.AmmoAmount(1) >= Pawn.Weapon.GetFireMode(1).AmmoPerFire);
    }
}

function RemoteFireWeapon(bool bUseAltMode)
{

	if ((Pawn == none) || (Pawn.Weapon == none))
		return;

	//If we are changing from one firing mode to another we need to stopshooting
	if (bUseAltMode && (bFire == 1))
		super.StopFiring();
	else if (!bUseAltMode && (bAltFire == 1))
		super.StopFiring();


	if ( !bUseAltMode )
	{
		bFire = 1;
		bAltFire = 0;
		if (!RemoteHasAmmo())
		{
			bFire = 0;
			return;
		}
    	Pawn.Weapon.BotMode = 0;
    	Pawn.Weapon.StartFire(0);
	}
	else
	{
		bFire = 0;
		bAltFire = 1;
		if (!RemoteHasAmmo())
		{
			bAltFire = 0;
			return;
		}
    	Pawn.Weapon.BotMode = 1;
    	Pawn.Weapon.StartFire(1);
	}

}

// check for line of sight to target deltatime from now.
//used by missle launcher and others to abort firing
// may want to reimplement
function bool CheckFutureSight(float deltatime)
{
	return true;
}

/*
AdjustAim()
Returns a rotation which is the direction the bot should aim - after introducing the appropriate aiming error
*/
function rotator AdjustAim(FireProperties FiredAmmunition, vector projStart, int aimerror)
{
	local rotator FireRotation, TargetLook;
	local float FireDist, TargetDist, ProjSpeed;
	local actor HitActor;
	local vector FireSpot, FireDir, TargetVel, HitLocation, HitNormal;
	local int realYaw;
	local bool bDefendMelee, bClean, bLeadTargetNow;

	if ( FiredAmmunition.ProjectileClass != None )
		projspeed = FiredAmmunition.ProjectileClass.default.speed;

	//log("AIMError is "$aimerror);
	// make sure bot has a valid target

	// This means we want to shoot at location
	if ( Target == None )
	{
		//we will set our own FocusActor as a target
		Target = myTarget;
	}

	//if our target is our FocusActor, we want to update its position - if not locked
	//(according to bots new focal point due to rotation, movement, etc.)
	if ( (Target == myTarget) && !bTargetLocationLocked)
		myTarget.SetLocation(myFocalPoint);

	/*
	if ( Target == None )
	{
		Target = Enemy;
		if ( Target == None ) //shooting at a location
		{

			//HACK: We will set ShotTarget to ourselves, so the projectile calls
			//ReceiveProjectileWarning on us and we will distribute it to other bots
			ShotTarget = Pawn;

			//TODO: Why to do this?
			//SetRotation( Rotator( myFocalPoint ));
			FireSpot = myFocalPoint;
			TargetDist = VSize(myFocalPoint - Pawn.Location);

			if (!bPerfectLocationAim)
				aimerror = AdjustAimError(aimerror,TargetDist,false,FiredAmmunition.bInstantHit, false);

			if ( Pawn(Target) == None )
			{
				if ( !FiredAmmunition.bTossed )
				{

					FireRotation = rotator(myFocalPoint - projstart);
					realYaw = FireRotation.Yaw;
					if (!bPerfectLocationAim)
						FireRotation.Yaw = SetFireYaw(FireRotation.Yaw + aimerror);
    				SetRotation(FireRotation);
    				UpdatePawnViewPitch();
					return FireRotation;
				}
				else
				{
					FireDir = AdjustToss(projspeed,ProjStart,myFocalPoint,true);
					FireRotation = Rotator(FireDir);
					realYaw = FireRotation.Yaw;
					if (!bPerfectLocationAim)
						FireRotation.Yaw = SetFireYaw(FireRotation.Yaw + aimerror);

					SetRotation(FireRotation);
					UpdatePawnViewPitch();
					return FireRotation;
				}
			}

		}
	}             */

	//TODO: We have target defined - Comment lines below out?
	//if ( Pawn(Target) != None )
		//Target = Pawn(Target).GetAimTarget();

	FireSpot = Target.Location;
	TargetDist = VSize(Target.Location - Pawn.Location);


	// Here we are aiming at stationary objects( not a location, not a pawn)
	if ( Pawn(Target) == None )
	{
		// if bPerfectLocationAim is true, it means that no aim correction will be performed on staitonary objects
		if (!bPerfectLocationAim)
			aimerror = AdjustAimError(aimerror,TargetDist,false,FiredAmmunition.bInstantHit, false);

		if ( !FiredAmmunition.bTossed )
		{

			FireRotation = rotator(Target.Location - projstart);
			realYaw = FireRotation.Yaw;
			if (!bPerfectLocationAim)
				FireRotation.Yaw = SetFireYaw(FireRotation.Yaw + aimerror);
			SetRotation(FireRotation);
			UpdatePawnViewPitch();
			return FireRotation;

		}
		else
		{
			FireDir = AdjustToss(projspeed,ProjStart,Target.Location,true);

			FireRotation = Rotator(FireDir);
			realYaw = FireRotation.Yaw;
			if (!bPerfectLocationAim)
				FireRotation.Yaw = SetFireYaw(FireRotation.Yaw + aimerror);

			SetRotation(FireRotation);
			UpdatePawnViewPitch();
			return FireRotation;

		}
	}

	bLeadTargetNow = FiredAmmunition.bLeadTarget && bLeadTarget;
	bDefendMelee = ( (Target == Enemy) && DefendMelee(TargetDist) );
	aimerror = AdjustAimError(aimerror,TargetDist,bDefendMelee,FiredAmmunition.bInstantHit, bLeadTargetNow);

	// lead target with non instant hit projectiles
	if ( bLeadTargetNow )
	{
		TargetVel = Target.Velocity;

		// hack guess at projecting falling velocity of target
		if ( Target.Physics == PHYS_Falling )
		{
			if ( Target.PhysicsVolume.Gravity.Z <= Target.PhysicsVolume.Default.Gravity.Z )
				TargetVel.Z = FMin(TargetVel.Z + FMax(-400, Target.PhysicsVolume.Gravity.Z * FMin(1,TargetDist/projSpeed)),0);
			else
			{
				//TODO: Correct?
				TargetVel.Z = FMin(0, TargetVel.Z);
			}
		}

		if ( bLeadTargetNow )
		{
			// more or less lead target (with some random variation)
			FireSpot += FMin(1, 0.7 + 0.6 * FRand()) * TargetVel * TargetDist/projSpeed;
			FireSpot.Z = FMin(Target.Location.Z, FireSpot.Z);
		}
		if ( (Target.Physics != PHYS_Falling) && (FRand() < 0.55) && (VSize(FireSpot - ProjStart) > 1000) )
		{
			// don't always lead far away targets, especially if they are moving sideways with respect to the bot
			TargetLook = Target.Rotation;
			if ( Target.Physics == PHYS_Walking )
				TargetLook.Pitch = 0;
			bClean = ( ((Vector(TargetLook) Dot Normal(Target.Velocity)) >= 0.71) && FastTrace(FireSpot, ProjStart) );
		}
		else // make sure that bot isn't leading into a wall
			bClean = FastTrace(FireSpot, ProjStart);
		if ( !bClean)
		{
			// reduce amount of leading
			if ( FRand() < 0.3 )
				FireSpot = Target.Location;
			else
				FireSpot = 0.5 * (FireSpot + Target.Location);
		}
	}

	bClean = false; //so will fail first check unless shooting at feet
	if ( FiredAmmunition.bTrySplash && (Pawn(Target) != None) && ((Skill >=4) || bDefendMelee)
		&& (((Target.Physics == PHYS_Falling) && (Pawn.Location.Z + 80 >= Target.Location.Z))
			|| ((Pawn.Location.Z + 19 >= Target.Location.Z) && (bDefendMelee || (skill > 6.5 * FRand() - 0.5)))) )
	{
	 	HitActor = Trace(HitLocation, HitNormal, FireSpot - vect(0,0,1) * (Target.CollisionHeight + 6), FireSpot, false);
 		bClean = (HitActor == None);
		if ( !bClean )
		{
			FireSpot = HitLocation + vect(0,0,3);
			bClean = FastTrace(FireSpot, ProjStart);
		}
		else
			bClean = ( (Target.Physics == PHYS_Falling) && FastTrace(FireSpot, ProjStart) );
	}
	if ( Pawn.Weapon != None && Pawn.Weapon.bSniping && Stopped() && (Skill > 5 + 6 * FRand()) )
	{
		// try head
 		FireSpot.Z = Target.Location.Z + 0.9 * Target.CollisionHeight;
 		bClean = FastTrace(FireSpot, ProjStart);
	}

	if ( !bClean )
	{
		//try middle
		FireSpot.Z = Target.Location.Z;
 		bClean = FastTrace(FireSpot, ProjStart);
	}
	if ( FiredAmmunition.bTossed && !bClean && bEnemyInfoValid )
	{
		FireSpot = LastSeenPos;
	 	HitActor = Trace(HitLocation, HitNormal, FireSpot, ProjStart, false);
		if ( HitActor != None )
		{
			bCanFire = false;
			FireSpot += 2 * Target.CollisionHeight * HitNormal;
		}
		bClean = true;
	}

	if( !bClean )
	{
		// try head
 		FireSpot.Z = Target.Location.Z + 0.9 * Target.CollisionHeight;
 		bClean = FastTrace(FireSpot, ProjStart);
	}
	if ( !bClean && (Target == Enemy) && bEnemyInfoValid )
	{
		FireSpot = LastSeenPos;
		if ( Pawn.Location.Z >= LastSeenPos.Z )
			FireSpot.Z -= 0.4 * Enemy.CollisionHeight;
	 	HitActor = Trace(HitLocation, HitNormal, FireSpot, ProjStart, false);
		if ( HitActor != None )
		{
			FireSpot = LastSeenPos + 2 * Enemy.CollisionHeight * HitNormal;
			if ( Pawn.Weapon != None && Pawn.Weapon.SplashDamage() && (Skill >= 4) )
			{
			 	HitActor = Trace(HitLocation, HitNormal, FireSpot, ProjStart, false);
				if ( HitActor != None )
					FireSpot += 2 * Enemy.CollisionHeight * HitNormal;
			}
			if ( Pawn.Weapon != None && Pawn.Weapon.RefireRate() < 0.99 )
				bCanFire = false;
		}
	}

	// adjust for toss distance
	if ( FiredAmmunition.bTossed )
		FireDir = AdjustToss(projspeed,ProjStart,FireSpot,true);
	else
		FireDir = FireSpot - ProjStart;

	FireRotation = Rotator(FireDir);
	realYaw = FireRotation.Yaw;

	FireRotation.Yaw = SetFireYaw(FireRotation.Yaw + aimerror);
	FireDir = vector(FireRotation);
	// avoid shooting into wall
	FireDist = FMin(VSize(FireSpot-ProjStart), 400);
	FireSpot = ProjStart + FireDist * FireDir;
	HitActor = Trace(HitLocation, HitNormal, FireSpot, ProjStart, false);
	if ( HitActor != None )
	{
		if ( HitNormal.Z < 0.7 )
		{
			FireRotation.Yaw = SetFireYaw(realYaw - aimerror);
			FireDir = vector(FireRotation);
			FireSpot = ProjStart + FireDist * FireDir;
			HitActor = Trace(HitLocation, HitNormal, FireSpot, ProjStart, false);
		}
		if ( HitActor != None )
		{
			FireSpot += HitNormal * 2 * Target.CollisionHeight;
			if ( Skill >= 4 )
			{
				HitActor = Trace(HitLocation, HitNormal, FireSpot, ProjStart, false);
				if ( HitActor != None )
					FireSpot += Target.CollisionHeight * HitNormal;
			}
			FireDir = Normal(FireSpot - ProjStart);
			FireRotation = rotator(FireDir);
		}
	}
	InstantWarnTarget(Target,FiredAmmunition,vector(FireRotation));
	ShotTarget = Pawn(Target);

	SetRotation(FireRotation);
	UpdatePawnViewPitch();
	return FireRotation;
}

//Is this function meant to be called for instant hit projectiles?
function InstantWarnTarget(Actor Target, FireProperties FiredAmmunition, vector FireDir)
{
	super.InstantWarnTarget(Target, FiredAmmunition, FireDir);
    //log("In RemoteBot: InstantWarnTarget");
}

//Here we send PRJ message asynchronously
function ReceiveProjectileWarning(Projectile Proj)
{    // we send this as sync. message now too
	local vector FireDir;
	local float projDistance;
	//local float orientation;
	//local vector Q, HelpVec;
	//local vector aFacing,aToB;

		if (Proj.IsA('FlakChunk'))
			FireDir = Normal(Proj.Velocity);
		else
			FireDir = vector(Proj.Rotation);

		if ( myLineOfSightTo(Proj) && InFOV(Proj.Location))
		{

			projDistance = VSize(Pawn.Location - Proj.Location);

			/*myConnection.SendLine("PRJ {Id " $ myConnection.GetUniqueId(Proj) $
				"} {ImpactTime " $ (projDistance/Proj.Speed) $
				"} {Direction " $ FireDir $
				"} {Speed " $ Proj.Speed $
				"} {Velocity " $ Proj.Velocity $
				"} {Location " $ Proj.Location $
				"} {Origin " $ Proj.Instigator.Location $
				"} {DamageRadius " $ Proj.DamageRadius $
				"} {Type " $ Proj.Class $
				"}");*/
		}

	super.ReceiveProjectileWarning(Proj);

            /*HelpVec = Proj.Location + FireDir * 500;

			//These lines were taken from UnrealWiki
        	//This counts the nearest point to our bot on the line created by origin of the projectile and his direction
			Q = Proj.Location + Normal( HelpVec - Proj.Location ) * ((( HelpVec - Proj.Location )
			dot ( Pawn.Location - Proj.Location )) / VSize( Proj.Location - HelpVec ));

			//Here we find out if Q is in front of shooter or behind him
			aFacing=Normal(FireDir);
 			aToB=Q - Proj.Location;
			orientation = aFacing dot aToB;
		//If the projectile flies at distance 100 or less from our bot we send the message
		//if ((orientation > 0) && (VSize( Pawn.Location - Q ) < 100))
		//{
		//	enemyDistance = VSize(Pawn.Location - shooter.Location);
		 //
		//	myConnection.SendLine("PRJ" $ib$as$ "ImpactTime" $ib$ (enemyDistance/Proj.Speed) $ae$ib$as$
		//		"Direction" $ib$ FireDir $ae$ib$as$
		//		"Origin" $ib$ Shooter.Location $ae$ib$as$
		//		"DamageRadius" $ib$ Proj.DamageRadius $ae$ib$as$
		//		"Type" $ib$ Proj.Class $ae);
		//}

		  */
}


// Projectiles are handled elsewhere
// This function is not just for projectiles, is called also from other code

/* Receive warning now only for instant hit shots and vehicle run-over warnings */

event ReceiveWarning(Pawn shooter, float projSpeed, vector FireDir)
{
	//log("We ve been in ReceiveWarning");
	//TODO: New special projectiles message?

	//Cannot call super - there is a change state issued.
	//super.ReceiveWarning(shooter,projSpeed,FireDir);

}

function bool TryToDuck(vector duckDir, bool bReversed);

// CloseToPointMan - called if orders are 'follow' to check if close enough to point man
function bool CloseToPointMan(Pawn Other)
{
	//return what engine wants to hear
	return true;
}

//Don't need our bots autotaunting
function MaybeTaunt(Pawn Other);

//Called when someone other than this bot dies.
//TODO: We dont need this
function Killed(pawn Killer, pawn Other, name damageType);

//Pointless callback
function EnemyAcquired();

//All kinds of things can call this mostly special trigger points
function Trigger( actor Other, pawn EventInstigator )
{
	myConnection.SendLine("TRG {Actor " $ myConnection.GetUniqueId(Other) $
		"} {EventInstigator " $ myConnection.GetUniqueId(EventInstigator) $
		"}");
	//super.Trigger(Other,EventInstigator);
}

//Much of translocator brains implemented in translocator
//For now better off playing without it - need to research how
//it interacts with path finding
function TranslocateToTarget(Actor Destn)
{
	//MyTranslocator.DesiredTarget = Destn;
}

//Don't let engine pick nodes that must be impact jumped
function bool CanImpactJump()
{
	return false;
}

//Don't handle impact jumps or low gravity manuevers for bots
function ImpactJump();
function BigJump(Actor JumpDest);

//Don't have engine direct to Ambush
function bool FindAmbushSpot()
{
	return false;
}

//Called when bot is injured
function NotifyTakeHit
(
	pawn InstigatedBy,
	vector HitLocation,
	int Damage,
	class<DamageType> damageType,
	vector Momentum
)
{
	local string messageString;

	//Super.NotifyTakeHit(InstigatedBy, HitLocation, Damage, damageType, Momentum);
	//todo - this have to be here, otherwise sometimes bot damage is not propagated
	super.TakeDamage(Damage, instigatedBy, hitlocation, momentum, damageType);

	messageString = "DAM {Damage " $ Damage $
		"} {DamageType " $ damageType $
        //"} {WeaponName " $ damageType.default.DamageWeaponName $ //was always blank!
        "} {Flaming " $ damageType.default.bFlaming $
        "} {CausedByWorld " $ damageType.default.bCausedByWorld $
        "} {DirectDamage " $ damageType.default.bDirectDamage $
        "} {BulletHit " $ damageType.default.bBulletHit $
        "} {VehicleHit " $ damageType.default.bVehicleHit $
		"}";

	if (InstigatedBy != none && InFOV(InstigatedBy.Location))
		messageString = messageString $" {Instigator " $ myConnection.GetUniqueId(instigatedBy) $"}";

	myConnection.SendLine(messageString);

	//Here we notifyies RemoteBot about they made a hit.
	if( InstigatedBy != none && InstigatedBy.Controller != none && InstigatedBy.Controller.isA('RemoteBot'))
	{
	    RemoteBot(InstigatedBy.Controller).RemoteNotifyHit(self, Damage, DamageType );
    }
    if ( InstigatedBy != none && InstigatedBy.Controller != none && InstigatedBy.Controller.isA('ObservedPlayer')) {
    	ObservedPlayer(InstigatedBy.Controller).NotifyHit(self, Damage, damageType);
    }
}

function RemoteNotifyHit(Controller Victim, int Damage, class<DamageType> damageType)
{
	myConnection.sendLine("HIT {Id " $ myConnection.GetUniqueId(Victim) $
		"} {Damage " $ Damage $
        "} {DamageType " $ DamageType $
        "} {WeaponName " $ damageType.default.DamageWeaponName $
        "} {Flaming " $ damageType.default.bFlaming $
        "} {DirectDamage " $ damageType.default.bDirectDamage $
        "} {BulletHit " $ damageType.default.bBulletHit $
        "} {VehicleHit " $ damageType.default.bVehicleHit $
		"}");
}


function SetFall()
{
	if (Pawn.bCanFly)
	{
		Pawn.SetPhysics(PHYS_Flying);
		return;
	}
	if (bDebug)
		log("In RemoteBot.uc: SetFall() enganged.");
	/*
	if ( Pawn.bNoJumpAdjust )
	{
		Pawn.bNoJumpAdjust = false;
		return;
	}
	else
	{
		bPlannedJump = true;
		Pawn.Velocity = EAdjustJump(Pawn.Velocity.Z,Pawn.GroundSpeed);
		Pawn.Acceleration = vect(0,0,0);
	} */
}


//**********************************************************************************
//Base RemoteBot AI, that controls the Pawn (makes him move)

auto state StartUp
{
	function TakeDamage( int Damage, Pawn instigatedBy, Vector hitlocation,
						vector momentum, class<DamageType> damageType)
	{
		Global.TakeDamage(Damage, instigatedBy, hitlocation, momentum, damageType);
	}

	function BeginState()
	{
		//log("StartUp,BeginState");
		//TODO:HACK: Should examine Squad issues if it should be here

        movingContinuous = false;

		//we need to spawn this class so game mechanics work properly in other game types
		//also we override AI methods called in these classes by various game objects (after picking flag, etc.)
		if (Squad == none) {
			if (Level.Game.IsA('BotDoubleDomination'))
				Squad = spawn(class'GBDOMSquadAI');
			else
				Squad = spawn(class'GBCTFSquadAI'); //works also for deathmatch etc.
		}

		Squad.AddBot(self);

	}
Begin:
	movingContinuous = false;
    //This is necessary, because if someone keeps shooting on our bot,
	//sometimes it starts sliding to side
	//log("Physics "$Physics$"Pawn Physics "$Pawn.Physics);
	if (Pawn != none)
	{
		if (Pawn.Physics != PHYS_Falling)
		{
			Pawn.Velocity = vect(0,0,0);
			Pawn.Acceleration = vect(0,0,0);
			MoveTimer = -1.0; //TODO: Should check what the hell is this
		}
	}
 	sleep(0.5);
	goto 'Begin';
DoStop:
	movingContinuous = false;
	if (Pawn != none)
	{
		if (Pawn.Physics == PHYS_Falling) //TODO: This is just a test, but it should repair one issue.
		{
			WaitForLanding(); //We wait when we are on ground and THEN we will stop.
			Pawn.Velocity = vect(0,0,0);
			Pawn.Acceleration = vect(0,0,0);
			MoveTimer = -1.0;
		} else {
			Pawn.Velocity = vect(0,0,0);
			Pawn.Acceleration = vect(0,0,0);
			MoveTimer = -1.0; //TODO: Should check what the hell is this
		}
	}
	//Pawn.PlayWaiting();
	goto 'Begin';
MoveToActor:
	movingContinuous = false;
	if (Pawn != None) {
		MoveToward(MoveTarget, , , , Pawn.bIsWalking);
		//There is an issue when the bot finish its movement, sometimes he goes a bit
		//over the target point, this caused turning back, because moveTo functions sets focalpoint
		//after it ends to its target point, to prevent this, we will set our own FocalPoint counted in advance
		FocalPoint = myFocalPoint;
	}
	goto 'Begin';
Move: //Moves swiftly between two locations - could be a problem normaly because of TCP/IP delays
	movingContinuous = false;
	if (Pawn != None) {
		if (Focus == none) {
			MoveTo( myDestination, , Pawn.bIsWalking );
			MoveTo( pendingDestination, , Pawn.bIsWalking);
		} else {
			MoveTo( myDestination, Focus, Pawn.bIsWalking );
			MoveTo( pendingDestination, Focus, Pawn.bIsWalking);
		}
		//There is an issue when the bot finish its movement, sometimes he goes a bit
		//over the target point, this caused turning back, because moveTo functions sets focalpoint
		//after it ends to its target point, to prevent this, we will set our own FocalPoint counted in advance
		FocalPoint = myFocalPoint;
	}
	goto 'Begin';
MoveContinuous:
	if (Pawn != None)
	{
		//to prevent that our focal point moves too much above or below us
		//remember we want to move
		myFocalPoint.z = Pawn.Location.z;
	 	cmoveDirection = vector(rotator(myFocalPoint - Pawn.Location));
	}
MoveContRepeat:
	if (Pawn != None)
	{
		movingContinuous = true;
		MoveTo( Pawn.Location + 500 * cmoveDirection, , Pawn.bIsWalking );

		myFocalPoint = Pawn.Location + 500 * cmoveDirection;
		FocalPoint = myFocalPoint;
		goto 'MoveContRepeat';
	}
	goto 'Begin';
}

// This state was called somehow on our bot. That is highly undersirable.
// Overriding
state Roaming
{
	function BeginState()
	{
		//log("In Roaming STATE! Shouldnt be!");
		gotostate('StartUp','Begin');
	}
Begin:
	gotostate('StartUp','Begin');
}

state GameEnded
{
ignores SeePlayer, EnemyNotVisible, HearNoise, TakeDamage, Bump, Trigger, HitWall, ZoneChange, Falling, ReceiveWarning;

	function SpecialFire()
	{
	}
	function bool TryToDuck(vector duckDir, bool bReversed)
	{
		return false;
	}
	function SetFall()
	{
	}
	function LongFall()
	{
	}
	function Killed(pawn Killer, pawn Other, name damageType)
	{
	}
	function ClientDying(class<DamageType> DamageType, vector HitLocation)
	{
	}

	function BeginState()
	{
		Pawn.SimAnim.AnimRate = 0.0;
		bFire = 0;
		bAltFire = 0;

		SetCollision(false,false,false);
		SetPhysics(PHYS_None);
		Velocity = vect(0,0,0);
		myConnection.SendLine("FIN");
	}
}

state Dead
{
ignores SeePlayer, HearNoise, KilledBy;

function BeginState()
{
	log("In: State: Dead, BeginState()");

	//We can be sent to this state sometimes, when it is not desired ( by game mechanics)
	//Escaping here
	if (Pawn != none)
		gotostate('StartUp','Begin');

	//This is taken from working AI in UT2004, probably needed to assure bot
	//will behave normally after restart - 02/03/07 Michal Bida
	movingContinuous = false;
	Enemy = None;
	Focus = None;
	Target = None;
	RouteGoal = None;
	MoveTarget = None;
	bFire = 0;
	bAltFire = 0;
	Super.StopFiring(); //Not needed anymore to call super, but for sure.
	FormerVehicle = None;
	bFrustrated = false;
	BlockedPath = None;
	//bInitLifeMessage = false; //Can cause problems with invulnerability?
	bPlannedJump = false;
	bInDodgeMove = false;
	bReachedGatherPoint = false;
	bFinalStretch = false;
	bWasNearObjective = false;
	bPreparingMove = false;
	bEnemyEngaged = false;
	bPursuingFlag = false;
}
/*
function EndState()
{
	myConnection.sendLine("SPW2");
}*/
Begin:
	//AutoSpawn policy
	if (Pawn != None)
		gotostate('StartUp','Begin');

	if (bAutoSpawn)// && !Level.Game.bWaitingToStartMatch)
	{
		RemoteRestartPlayer();
	//	if (Pawn == none) //bug during restart?
	//		goto('Begin');
		//ServerRestartPlayer(); //Dunno if this is needed here - Could it cause troubles?
	}

	if (Pawn != None)
		gotostate('StartUp','Begin');
/*	if (!Level.Game.bWaitingToStartMatch)
	{
		RemoteRestartPlayer();
	}
	if (Pawn != None)
		gotostate('StartUp','Begin');*/
	sleep(1.0);
	goto('Begin');

}



//-------------RemoteBot Specific Functions--------------------

// True if location is closer than 300 UT units
// True if location loc is in bot's field of view. Does not take into account occlusion by geometry!
// Possible optimization: Precompute cos(obsController.FovAngle / 2) for InFOV - careful if it can change.
function bool InFOV(vector loc) {
	local vector view;   // vector pointing in the direction obsController is looking.
	local vector target; // vector from obsController's position to the target location.

	view = vector(Rotation);

	if (Pawn != none) {
		target = loc - (Pawn.Location + Pawn.EyePosition());		
	} else {
		target = loc - Location;
	}
	
	if (VSize(target) < 300) return true;

	return Acos(Normal(view) dot Normal(target)) < FovAngleRadH ; // Angle between view and target is less than FOV
	// 57.2957795 = 180/pi = 1 radian in degrees  --  convert from radians to degrees
}


//Called by the gametype when someone else is injured by the bot
//TODO: From old gamebots, is not called anymore, TO REMOVE - mb
/*
function int HurtOther(int Damage, name DamageType, pawn injured)
{
	myConnection.SendLine("HIT" $ib$as$ "Id" $ib$ injured $ae$ib$as$
		"Damage" $ib$ Damage $ae$ib$as$
		"DamageType" $ib$ DamageType $ae);
} */
function ExportLocationUpdate() {
	local rotator PawnRotation;
	local string myId, outstring;

 	if( Pawn != none ) {
		PawnRotation = Pawn.Rotation;
		PawnRotation.Pitch = int(Pawn.ViewPitch) * 65535/255;

		myId =  myConnection.GetUniqueId(self);

		outstring = "UPD {Loc " $ Pawn.Location $
			//"} {Bid " $ myId $
			"} {Rot " $ PawnRotation $
			"} {Vel " $ Pawn.Velocity $ "}";

		myConnection.SendLine(outstring);
	}
}

function checkSelf()
{
	local string outstring, TeamIndex, myId;
	local rotator PawnRotation;
	local bool bIsShooting;
	local int priAmmo, secAmmo;

	local vector FloorLocation, FloorNormal;

	if(Level.Game.isA('BotTeamGame'))
	{
		TeamIndex = string(PlayerReplicationInfo.Team.TeamIndex);
	}
	else
	{
    	TeamIndex = "255";
	}


 	if( Pawn != none ) {
		if( Pawn.Weapon != None) {
		    bIsShooting = Pawn.Weapon.IsFiring();
		    priAmmo = Pawn.Weapon.AmmoAmount(0);
			secAmmo = Pawn.Weapon.AmmoAmount(1);
		}

		PawnRotation = Pawn.Rotation;
		PawnRotation.Pitch = int(Pawn.ViewPitch) * 65535/255;

		FloorNormal = vect(0,0,0);
		Trace(FloorLocation,FloorNormal,Pawn.Location + vect(0,0,-1000),Pawn.Location, false, ,);
		myId =  myConnection.GetUniqueId(self);
		outstring = "SLF {Id SELF_" $ myId $  //we need unique id of the self message
			"} {BotId " $ myId $
			"} {Vehicle " $ Vehicle(Pawn) != none $
			"} {Location " $ Pawn.Location $
			"} {Rotation " $ PawnRotation $
			"} {Velocity " $ Pawn.Velocity $
			"} {Name " $ PlayerReplicationInfo.PlayerName $
			"} {Team " $ TeamIndex $
			"} {Health " $ Pawn.Health $
			"} {Weapon " $ Pawn.Weapon $
			"} {Shooting " $ bIsShooting $
			"} {Armor " $ int(Pawn.ShieldStrength) $
			"} {SmallArmor " $ int(xPawn(Pawn).SmallShieldStrength) $
			"} {Adrenaline " $ int(Adrenaline) $
			"} {Crouched " $ Pawn.bIsCrouched $
			"} {Walking " $ Pawn.bIsWalking $
			"} {FloorLocation " $ FloorLocation $
			"} {FloorNormal " $ FloorNormal $
			"} {Combo " $ xPawn(Pawn).CurrentCombo $
			"} {UDamageTime " $ xPawn(Pawn).UDamageTime $
			"} {PrimaryAmmo " $ priAmmo $
			"} {SecondaryAmmo " $ secAmmo $
			"}";


		if( Pawn.Weapon != None) {
			if( Pawn.Weapon.GetFireMode(1).IsFiring() )	{
				outstring = outstring $" {AltFiring True}";
			} else {
				outstring = outstring $" {AltFiring False}";
			}
		}

        //log("Debug: bFire "$bFire$" bAltFire "$bAltFire);
		myConnection.sendLine(outstring);
	} else {
		log("Pawn is none in CheckSelf() ");
	}


}

function checkPlayers() {
	local Controller C;
	local string outstring, TeamIndex, WeaponClass;
	local rotator PawnRotation;
	//!!! view rotation sometimes falls out of synch with rotation? wtf?

	for ( C=Level.ControllerList; C!=None; C=C.NextController ) {
		if( C != self && C.Pawn != none && CanSee(C.Pawn)) {//to match peripheral vision
			if(Level.Game.isA('BotTeamGame')) {
				TeamIndex = string(C.PlayerReplicationInfo.Team.TeamIndex);
			} else {
    			TeamIndex = "255";
			}
			if (C.Pawn.Weapon != none) {
				WeaponClass = string(C.Pawn.Weapon.Class);
			} else {
				WeaponClass = "None";
			}
			PawnRotation = C.Pawn.Rotation;
			PawnRotation.Pitch = int(C.Pawn.ViewPitch) * 65535/255;
			outstring = "PLR {Id " $ myConnection.GetUniqueId(C) $				
				"} {Spectator " $ (C.PlayerReplicationInfo.bIsSpectator || C.IsInState('Spectating')) $
				"} {Rotation " $ PawnRotation $
				"} {Location " $ C.Pawn.Location $
				"} {Velocity " $ C.Pawn.Velocity $
				"} {Name " $ C.PlayerReplicationInfo.PlayerName $
				"} {Team " $ TeamIndex $
				//"} {Reachable " $ actorReachable(C.Pawn) $  //This can consume quite a lot resources
				"} {Crouched " $ C.Pawn.bIsCrouched $
				"} {Weapon " $ WeaponClass $
				"}";

			if((C.Pawn.Weapon != none) && C.Pawn.Weapon.GetFireMode(0).IsFiring() )
				outstring = outstring $" {Firing 1}";
			else if((C.Pawn.Weapon != none) && C.Pawn.Weapon.GetFireMode(1).IsFiring() )
				outstring = outstring $" {Firing 2}";
			else
				outstring = outstring $" {Firing 0}";

			myConnection.sendLine(outstring);

        }//end if
	}//end for P=Level.ControllerList
}

function checkItems() {
	local Pickup Pickup;
	foreach DynamicActors(class'Pickup',Pickup) {
		if( (Pickup.GetStateName() == 'Pickup') && !Pickup.bHidden && (VSize(Pawn.Location - Pickup.Location) <= visionRadius) && InFOV(Pickup.Location) && myLineOfSightTo(Pickup) )	{
			myConnection.SendLine("INV {Id " $ myConnection.GetUniqueId(Pickup) $
				"} {Location " $ Pickup.Location $
				"} {Amount " $ myConnection.GetItemAmount(Pickup) $
				//"} {Reachable " $ actorReachable(Pickup) $
				"} {Visible True" $ //for compatibility reasons, we know its true due to the check up in if statement
				"} {Dropped " $ Pickup.bDropped $
				"} {Type " $ Pickup.Class $
				"}");

		}
	}
}

function checkMovers() {
	local Mover M;
	local string outstring;
	local int l;
	local array<Mover> MoverArray;

	MoverArray = BotDeathMatch(Level.Game).MoverArray;
	for (l = 0; l < MoverArray.Length; l++) {
		M = MoverArray[l];
		if( ( ( Abs(Pawn.Location.x - M.Location.x) < 300 ) && ( Abs(Pawn.Location.y - M.Location.y) < 300 ) &&
			 ( Abs(Pawn.Location.Z - M.Location.Z) < 300 )) || (InFOV(M.Location) && myLineOfSightTo(M) ) )// )
		{
			outstring = "MOV {Id " $ M $
				"} {Location " $ M.Location $
				//"} {Reachable " $ actorReachable(M) $ //not sure if this works for lifts(bots may be forced to wait for lift)
				"} {Visible True" $ //for compatibility reasons, we know its true due to the check up in if statement
				"} {DamageTrig " $ M.bDamageTriggered $
				"} {Type " $ M.Class $
				"} {IsMoving " $ M.bInterpolating $
				"} {Velocity " $ M.Velocity $
				"} {MoveTime " $ M.MoveTime $
				"} {OpenTime " $ M.StayOpenTime $
				"} {State " $ M.GetStateName() $
				"}";
			myConnection.SendLine(outstring);
		}
	}
}

function checkProjectiles() {
	local Projectile Proj;
	local vector FireDir;
	local float projDistance;

	foreach DynamicActors(class'Projectile',Proj) {

		if (Proj.IsA('FlakChunk'))
			FireDir = Normal(Proj.Velocity);
		else
			FireDir = vector(Proj.Rotation);

		if ((VSize(Pawn.Location - Proj.Location) <= visionRadius) && InFOV(Proj.Location) && myLineOfSightTo(Proj) )	{
			projDistance = VSize(Pawn.Location - Proj.Location);

			myConnection.SendLine("PRJ {Id " $ myConnection.GetUniqueId(Proj) $
				"} {ImpactTime " $ (projDistance/Proj.Speed) $
				"} {Direction " $ FireDir $
				"} {Speed " $ Proj.Speed $
				"} {Velocity " $ Proj.Velocity $
				"} {Location " $ Proj.Location $
				"} {Origin " $ Proj.Instigator.Location $
				"} {DamageRadius " $ Proj.DamageRadius $
				"} {Type " $ Proj.Class $
				"}");
		}

	}//end PRJ
}

function bool myLineOfSightTo(Actor N){
	return LineOfSightTo(N);
}

function bool myActorReachable(NavigationPoint N) {
	return actorReachable(N);
}

function checkNavPoints() {
	local string outstring;
	local int temp, i;
	local array<InventorySpot> InvSpotArray;
	local array<LiftCenter> LiftCenterArray;
	local array<xDomPoint> DomPointArray;
	local xDomPoint D;
	local InventorySpot IS;
	local LiftCenter DR;

	InvSpotArray = BotDeathMatch(Level.Game).InvSpotArray;
	LiftCenterArray = BotDeathMatch(Level.Game).LiftCenterArray;
	DomPointArray = BotDeathMatch(Level.Game).xDomPointArray;

	if (myConnection.bSynchronousNavPoints) {
		for (i = 0; i < DomPointArray.Length; i++) {
			D = DomPointArray[i];
			if( D.ControllingTeam == none )
				temp = 255;
			else
				temp = D.ControllingTeam.TeamIndex;

			outstring = "NAV {Id " $ D $
				//"} {Location " $ D.Location $
				"} {Visible " $ InFOV( D.Location ) $
				//"} {Reachable " $ myActorReachable(N) $
				"} {DomPoint True" $
				"} {DomPointController " $ temp $
				"}";
    		myConnection.SendLine(outstring);

		}
		for (i = 0; i < InvSpotArray.Length; i++) {
			IS = InvSpotArray[i];
			if ((IS.markedItem != none) && (VSize(Pawn.Location - IS.Location) <= visionRadius) && ((VSize(Pawn.Location - IS.Location) <= 120) || (InFOV( IS.Location ) && myLineOfSightTo(IS))) ) {
				outstring = "NAV {Id " $ IS $
					//"} {Location " $ IS.Location $
					"} {Visible True" $
					//"} {Reachable " $ myActorReachable(IS) $
					"} {ItemSpawned " $ IS.markedItem.IsInState('Pickup') $ "}";
				myConnection.SendLine(outstring);
			}
		}

		for (i = 0; i < LiftCenterArray.Length; i++) {
			DR = LiftCenterArray[i];
			if( (VSize(Pawn.Location - DR.Location) <= visionRadius) && ((VSize(Pawn.Location - DR.Location) <= 500) || (InFOV( DR.Location ) && myLineOfSightTo(DR)))) {
				outstring = "NAV {Id " $ DR $
					"} {Location " $ DR.Location $
					"} {Visible True" $
					//"} {Reachable " $ myActorReachable(DR) $
				    "}";
				myConnection.SendLine(outstring);
			}
		}
	}
}

function checkObjectives() {
	local CTFFlag F;
	local xBombFlag B, BB;
	local string outstring;

	//BombingRun bomb support
	if (Level.Game.IsA('BotBombingRun')) {
		//TODO: Move this allActors search to some different place,
		// Not to do it that often.
		foreach AllActors (class'xBombFlag', B)	{

        	outstring = "BOM {Id "  $ B $
				//"} {Reachable " $ actorReachable(B) $
				"} {State " $ B.GetStateName() $
				"}";

			//bomb is NOT updated when it is held - we need to use holder to query info about location
			//and visibility!
			if(B.IsInState('Held') && B.Holder != none) {
      			if (InFOV(B.Holder.Location) && myLineOfSightTo(B.Holder)) {
			    	outstring = outstring $" {Visible True} {Location " $ B.Holder.Location $
    			        "} {Holder " $ myConnection.GetUniqueId(B.Holder) $"}";
			  	} else if (B.Holder == Pawn) { //our pawn is holder
        			outstring = outstring $" {Visible True" $
            			"} {Location " $ B.Holder.Location $
		            	"} {Holder " $ myConnection.GetUniqueId(B.Holder) $"}";
        		} else {
		        	outstring = outstring $" {Visible False}";
				}
			} else {
				if (InFOV(B.Location) && myLineOfSightTo(B)) {
		        	outstring = outstring $" {Visible True} {Location " $ B.Location $
	    		        "} {Holder None}";
				} else {
        			outstring = outstring $" {Visible False}";
				}
			}
			myConnection.SendLine(outstring);
		}
	}

	//CTF Game support
	if (Level.Game.IsA('BotCTFGame')) {
		//TODO: Move this allActors search to some different place,
		// Not to do it that often.
		foreach AllActors (class'CTFFlag', F) {

			outstring = "FLG {Id " $ F $
				"} {Team " $ F.Team.TeamIndex $
			//	"} {Reachable " $ actorReachable(F) $
				"} {State " $ F.GetStateName() $
				"}";

			//flag is NOT updated when it is held - we need to use holder to query info about location
			//and visibility!
			if(F.IsInState('Held') && F.Holder != none) {
      			if (InFOV(F.Holder.Location) && myLineOfSightTo(F.Holder)) {
			    	outstring = outstring $" {Visible True} {Location " $ F.Holder.Location $
    			        "} {Holder " $ myConnection.GetUniqueId(F.Holder) $"}";
			  	} else if (F.Holder == Pawn) { //our pawn is holder
        			outstring = outstring $" {Visible True" $
            			"} {Location " $ F.Holder.Location $
		            	"} {Holder " $ myConnection.GetUniqueId(F.Holder) $"}";
        		} else {
		        	outstring = outstring $" {Visible False}";
				}
			} else {
				if (InFOV(F.Location) && myLineOfSightTo(F)) {
		        	outstring = outstring $" {Visible True} {Location " $ F.Location $
	    		        "} {Holder None}";
				} else {
        			outstring = outstring $" {Visible False}";
				}
			}

			myConnection.SendLine(outstring);
		}
	} // end if (Level.Game.IsA('BotCTFGame'))
}

function checkVision()
{
	if( Pawn == none ) {
		log("In CheckVision() - Pawn is none ");
		return;
	}

	checkPlayers();
	checkItems();
	checkMovers();
	checkProjectiles();
	checkNavPoints();
	checkObjectives();

	checkSounds();
}


simulated event Destroyed()
{
	local LinkedReplicationInfo PRI;

	//log("In RemoteBot: event Destroyed()");
    //Destroying all linked replication infos - just on clients
    //if (ROLE < ROLE_AUTHORITY)
	for (PRI = PlayerReplicationInfo.CustomReplicationInfo; PRI != none; PRI = PRI.NextReplicationInfo)
	{
		if (PRI.IsA('GBReplicationInfo'))
		{
			PRI.Destroy();
			break;
		}
	}

	if (Pawn != None)
	{
		Pawn.Died(none, class'DamageType', Pawn.Location);
		if (Pawn != None)
			Pawn.Destroy();//destroy him
    }

	//Destroying actor for aiming our shooting on location
    if (myTarget != none)
    {
		myTarget.Destroy();
	}

	if (myConnection != none)
		myConnection.SendLine("FIN");
	else
		log("Problem with sending FIN, myConnection = none");

	Super.Destroyed();
}

function ChangeWeapon();

state MoveToGoal
{
	function BeginState();
}

function ServerChangedWeapon(Weapon OldWeapon, Weapon NewWeapon);

simulated function Tick(float DeltaTime)
{
							  /*
	local Controller Player;

	Player = Level.GetLocalPlayerController();

	GBxPlayer(Player).GBHUD.DrawLine(Pawn.Location,FocalPoint);
							 */
	super.Tick(DeltaTime);
	if (bShowFocalPoint) {
		myConnection.FocusActor.bHidden = false;
		if (myConnection.FocusActorEmitter == none) {
			if (Pawn != none) {
				myConnection.FocusActorEmitter = Spawn(class'CustomBeamWhite', Pawn,, Pawn.Location, Pawn.Rotation);
				Pawn.AttachToBone(myConnection.FocusActorEmitter, 'spine');
				myConnection.FocusActorEmitter.Instigator = Pawn;
				myConnection.FocusActorEmitter.SetFocusActor(myConnection.FocusActor);
			}
		}
		if (Focus != none) {
		    myConnection.FocusActor.SetLocation(Focus.Location);
		} else {
			myConnection.FocusActor.SetLocation(FocalPoint);
		}
	} else
		myConnection.FocusActor.bHidden = true;
	//log("In RemoteBot: Tick()");
}

//-----------------

defaultproperties
{
	DefaultRotationRate=(Pitch=3072,Yaw=60000,Roll=2048)
	remoteVisionLimit=0.707000
	visionRadius=5000
	SpeedMultiplier=1.0
	bDrawTraceLines=true
	bDisableAutoPickup=false
	bIncludeFadeOutInMsg=false
	bShowFocalPoint=false
	bPerfectLocationAim=false
	bIsPlayer=True
	bSpeakingBots=false
	bAutoTrace=false
	bAutoSpawn=true
	MaxSpeed=2.00000
	DesiredSkin="ThunderCrash.JacobM"
}
