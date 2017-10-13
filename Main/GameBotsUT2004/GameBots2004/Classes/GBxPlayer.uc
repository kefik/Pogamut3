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

//We've got this class here to allow specators to go through walls
class GBxPlayer extends xPlayer;

//var NameMarker pickup;
var ScriptedTexture Tex;
var GBhud GBHUD;
var GBHUDInteraction GBHUDInter;

var vector NextStartLocation;
var rotator NextStartRotation;

//For handling the mouse cursor
var vector PlayerMouse;
var float LastHUDSizeX;
var float LastHUDSizeY;

var Actor FocusActor;

//Actor selected by mouse cursor
var Actor SelectedActor;

// UO Object selected by fire
var UsableObject SelectedObject;

var string DialogId;
var string DialogBotName;
var string DialogText;
var string DialogOptions[10];

var bool bStoryCamera;

var int myCamDirX;
var int myCamDirY;
var int myCamDirZ;

var float myCamSpeed;
var rotator myCamTurnRot;
var float camVerticalTurn, camHorizontalTurn, camRollTurn;

//UO
enum ItemEnum { ITEM_NONE, ITEM_COOKER, ITEM_CUP, ITEM_BROCCOLI, ITEM_B, ITEM_PLATE,
 ITEM_BOILING_WATER, ITEM_COFFEE, ITEM_MILK, ITEM_SUGAR};
//UO
var ItemEnum holdedItem;
var class<UsableObject> holdedItemClass;


replication
{
    // Functions server can call + variables to replicate.
    reliable if ( Role==ROLE_Authority )
		Test, DrawLine, DrawTraceLine, DialogBotName, DialogId, DialogText, DialogOptions,
		SelectedActor, myCamDirX, myCamDirY, myCamDirZ, myCamTurnRot, camRollTurn, myCamSpeed,
		camVerticalTurn, camHorizontalTurn, bStoryCamera, holdedItem, ClientConsoleCommand,
		ClientDrawStayingDebugLines, UpdateRemainingTime;
	// Functions client can call on the server + variables to replicate from client to server
	reliable if ( Role < ROLE_Authority)
		HandleKeyInput, SelectActorOnServer, ClearDialogOnServer, PlayerKeyEvent, pickupObject,
        spawnObject, handleEventCommand;
}

/**
* DeathMatch HUDCDeathmatch is taking info about remaining time from GRI stored
* at PlayerController. Need to update remaining time, when the game gets restarted.
* Need to update it on client as well - this fc is replicated
*/
function UpdateRemainingTime(int RemainingTime) {
	GameReplicationInfo.RemainingTime = RemainingTime;
	GameReplicationInfo.RemainingMinute = RemainingTime;
	GameReplicationInfo.ElapsedTime = 0;
}

function ItemEnum getHoldedItem(string debug) {
         Log(debug);
         return holdedItem;
}

function setHoldedItem(ItemEnum item) {
         local string logString;
         logString = "setting item : " $ item;
         Log(logString);
         holdedItem = item;
}

function string getItemLabel(ItemEnum item) {
         if(item == ITEM_COOKER) return "Cooker";
         if(item == ITEM_CUP) return "Cup";
         if(item == ITEM_BROCCOLI) return "Broccoli";
         if(item == ITEM_PLATE) return "Plate";
         if(item == ITEM_BOILING_WATER) return "Boiling water";
         if(item == ITEM_MILK) return "Milk";
         if(item == ITEM_COFFEE) return "Coffee";
         if(item == ITEM_SUGAR) return "Sugar";

         return "None";
}

function NotifyTakeHit(pawn InstigatedBy, vector HitLocation, int Damage, class<DamageType> damageType, vector Momentum)
{
	super.NotifyTakeHit(InstigatedBy, HitLocation, Damage, damageType, Momentum);

	if(InstigatedBy != none && InstigatedBy.Controller != none) {
        if (InstigatedBy.Controller.isA('RemoteBot')) {
    	    RemoteBot(InstigatedBy.Controller).RemoteNotifyHit(self, Damage, DamageType);
    	}
    	if (InstigatedBy.Controller.IsA('ObservedPlayer')) {
    	   ObservedPlayer(InstigatedBy.Controller).NotifyHit(self, Damage, DamageType);
    	}
	}
}

//this function is replicated - means it can be called from server on client
//main usage - server issued client side screenshots
function ClientConsoleCommand(string command, bool boolean) {
	ConsoleCommand(command, boolean);
}

//this function is replicated - means it can be called from server on client
//main usage - drawing staying debug lines on clients
function ClientDrawStayingDebugLines(String lines, vector lineColor, bool bClear) {
	local array<string> Parts;
	local array<string> StringVector;
	local array<vector> LineVectors;
	local int count, i, vecLength;
	local vector temp;

	if (bClear) {
		//clearing everything
		ClearStayingDebugLines();
	}

	count = Split(lines, ";", Parts );
	for (i = 0; i < count; i++) {
		vecLength = Split(Parts[i], ",", StringVector);
		if (vecLength == 3) {
			temp = vect(0,0,0);
			temp.x = float(StringVector[0]);
			temp.y = float(StringVector[1]);
			temp.z = float(StringVector[2]);
        	LineVectors[LineVectors.length] = temp;
		} else
			break;
	}

 	for (i = 0; i < LineVectors.Length; i = i + 2) {
 		if (i + 1 >= LineVectors.Length) {
 			return;
 		}

		DrawStayingDebugLine(LineVectors[i],LineVectors[i+1],lineColor.x,lineColor.y,lineColor.z);
	 }
}

function RespawnPlayer(optional vector startLocation, optional rotator startRotation)
{

	NextStartLocation = startLocation;
	NextStartRotation = startRotation;

	//determine if we are spectator, if yes, we just set position and that's it

	if (PlayerReplicationInfo.bIsSpectator || IsInState('Spectating'))
	{
		//log("We recognized that we are spectationg");
		if (startLocation != vect(0,0,0))
		{
			bBehindView = false;
			bFrozen = false;
			ServerViewSelf();

   			SetLocation(startLocation);
   			ClientSetRotation(startRotation);

		}

		return;

	}

	//if we are not spectator, we will kill the pawn and call respawn function

   	if (Pawn != None)
   	{
		Pawn.Died(self, class'DamageType', Pawn.Location);
	}

	BotDeathMatch(Level.Game).RestartPlayer( self );
}

/** Kills the bot */
function KillBot() {
	if (Pawn != None)
		Pawn.Died(none, class'DamageType', Pawn.Location);
}

//We override this to disable pausing of the game when pressing ESC key
function ShowMidGameMenu(bool bPause)
{
	// Pause if not already
	//if(Level.Pauser == None)
	//	SetPause(true);

	if ( Level.NetMode != NM_DedicatedServer )
		StopForceFeedback();  // jdf - no way to pause feedback

	// Open menu

	if (bDemoOwner)
		ClientopenMenu(DemoMenuClass);

	else if ( LoginMenuClass != "" )
		ClientOpenMenu(LoginMenuClass);

	else ClientOpenMenu(MidGameMenuClass);
}

function Test()
{
	DrawStayingDebugLine(Pawn.Location,Pawn.Location + 500 * vector(Pawn.Rotation),255,0,0);
	log("In GBxPLayer test() this location: "$Pawn.Location);
}

simulated function DrawTraceLine(vector LineStart,vector LineEnd,bool LineHit)
{
	if (LineHit)
	{
		DrawDebugLine(LineStart,LineEnd,255,0,0);
		log("Draw Line hit");
	}
	else
	{
		DrawDebugLine(LineStart,LineEnd,0,255,0);
		log("Draw Line miss");
	}

}

simulated function DrawLine(vector LineStart,vector LineEnd)
{

	DrawStayingDebugLine(LineStart,LineEnd,255,0,0);
	log("Drawing Line");

}

//Create spectator from player and set him as a camera
function BecomeStoryCamera()
{
	//Will be called just on server anyway
	if (Role < ROLE_Authority)
		return;

	//We could delete this, if it would cause trouble in our games, but shouldnt
	if ( !Level.Game.BecomeSpectator(self) )
		return;

	//Kill the Pawn properly if any
	if ( Pawn != None )
		Pawn.Died(self, class'DamageType', Pawn.Location);

	//Remove from team
	if ( PlayerReplicationInfo.Team != None )
		PlayerReplicationInfo.Team.RemoveFromTeam(self);
	//Set some vars	(UT stuff)
	PlayerReplicationInfo.Team = None;
	PlayerReplicationInfo.Score = 0;
	PlayerReplicationInfo.Deaths = 0;
	PlayerReplicationInfo.GoalsScored = 0;
	PlayerReplicationInfo.Kills = 0;

	GotoState('Spectating');
	bFreeCam = true;
	bStoryCamera = true;

	//This is UT stuff, leaving intact for now
	BroadcastLocalizedMessage(Level.Game.GameMessageClass, 14, PlayerReplicationInfo);
	ClientBecameSpectator();
}


//We need to change this fc a little bit so our bots can receive msgs from players
function ServerSay( string Msg )
{
	local controller C;

	// center print admin messages which start with #
	if (PlayerReplicationInfo.bAdmin && left(Msg,1) == "#" )
	{
		Msg = right(Msg,len(Msg)-1);
		for( C=Level.ControllerList; C!=None; C=C.nextController )
		{
			if( C.IsA('PlayerController') )
			{
				PlayerController(C).ClearProgressMessages();
				PlayerController(C).SetProgressTime(6);
				PlayerController(C).SetProgressMessage(0, Msg, class'Canvas'.Static.MakeColor(255,255,255));
			}
			if( C.IsA('RemoteBot') )
			{
				RemoteBot(C).RemoteNotifyClientMessage( self, Msg );
			}

		}
		return;
	}

	for( C=Level.ControllerList; C!=None; C=C.nextController )
	{
		if( C.IsA('RemoteBot') )
		{
			RemoteBot(C).RemoteNotifyClientMessage( self, Msg );
		}
		if (C.IsA('ObservedPlayer')) {
		    ObservedPlayer(C).ReceivedGlobalMessage(self, Msg);
		}
	}

	Level.Game.Broadcast(self, Msg, 'Say');
}

function ServerTeamSay( string Msg )
{
	local Controller C;

	LastActiveTime = Level.TimeSeconds;

	if( !GameReplicationInfo.bTeamGame )
	{
		Say( Msg );
		return;
	}

    for( C=Level.ControllerList; C!=None; C=C.nextController )
	{
		if( C.IsA('RemoteBot') && (C.PlayerReplicationInfo.Team.TeamIndex == PlayerReplicationInfo.Team.TeamIndex) )
		{
			RemoteBot(C).RemoteNotifyTeamMessage( self, Msg );
		}

		if (C.IsA('ObservedPlayer') && SameTeamAs(C)) {
		    ObservedPlayer(C).ReceivedTeamMessage(self, Msg);
		}
	}

    Level.Game.BroadcastTeam( self, Level.Game.ParseMessageString( Level.Game.BaseMutator , self, Msg ) , 'TeamSay');
}

//forwarding key press to BotDeathMatch for distributing to control servers
function PlayerKeyEvent(Interactions.EInputKey key, Interactions.EInputAction action) {
	BotDeathMatch(Level.Game).PlayerKeyEvent(self, key, action);
}

simulated event Destroyed()
{

	local LinkedReplicationInfo PRI;

    //Destroying all linked replication infos - just on client
    if (ROLE < ROLE_AUTHORITY)
		for (PRI = PlayerReplicationInfo.CustomReplicationInfo; PRI != none; PRI = PRI.NextReplicationInfo)
		{
			PRI.Destroy();
		}

	Super.Destroyed();

	if (GBHUD != none)
	{
		log("Detroying hud");
		GBHUD.Destroy();
	}

}
/*
event PlayerCalcView(out actor ViewActor, out vector CameraLocation, out rotator CameraRotation )
{
    local Pawn PTarget;

    PTarget = Pawn(ViewTarget);
    if ( PTarget != none && PTarget != Pawn )
    {
	    CameraRotation = ViewTarget.Rotation;
        CameraRotation.Pitch = int(PTarget.ViewPitch) * 65556/255;

        if ( (Level.NetMode == NM_Client) || (bDemoOwner && (Level.NetMode != NM_Standalone)) )
        {
            PTarget.SetViewRotation(TargetViewRotation);
            CameraRotation = BlendedTargetViewRotation;
            CameraRotation.Pitch = int(PTarget.ViewPitch) * 65556/255;

            PTarget.EyeHeight = TargetEyeHeight;
        }
        else if ( PTarget.IsPlayerPawn() ) {
            CameraRotation = PTarget.GetViewRotation();
            CameraRotation.Pitch = int(PTarget.ViewPitch) * 65556/255;
        }

		if (PTarget.bSpecialCalcView && PTarget.SpectatorSpecialCalcView(self, ViewActor, CameraLocation, CameraRotation))
		{
			CacheCalcView(ViewActor, CameraLocation, CameraRotation);
			return;
		}

        if ( !bBehindView )
            CameraLocation += PTarget.EyePosition();

    	if ( bBehindView )
	    {
    	    CameraLocation = CameraLocation + (ViewTarget.Default.CollisionHeight - ViewTarget.CollisionHeight) * vect(0,0,1);
        	CalcBehindView(CameraLocation, CameraRotation, CameraDist * ViewTarget.Default.CollisionRadius);
	    }
	    CacheCalcView(ViewActor,CameraLocation,CameraRotation);
	} else
		super.PlayerCalcView(ViewActor, CameraLocation, CameraRotation );



}*/
state Spectating {
    ignores SwitchWeapon, RestartLevel, ClientRestart, Suicide,
     ThrowWeapon, NotifyPhysicsVolumeChange, NotifyHeadVolumeChange;

    exec function Fire( optional float F ) {
    	if (!bStoryCamera) {
	    	if ( bFrozen ) {
				if ( (TimerRate <= 0.0) || (TimerRate > 1.0) )
					bFrozen = false;
				return;
			}
        	ServerViewNextPlayer();
        }
    }

    // return to spectator's own camera.
    exec function AltFire( optional float F ) {
    	if (!bStoryCamera) {
	        bBehindView = false;
    	    ServerViewSelf();
    	}
    }

	//We'll set the timer, the camera will move just for the amount specified by SetTimer()
    function Timer() {
    	bFrozen = false;

    	myCamDirX = 0;
    	myCamDirY = 0;
    	myCamDirZ = 0;

    	camVerticalTurn = 0;
    	camHorizontalTurn = 0;
    	camRollTurn = 0;
    }

    function BeginState() {
        if ( Pawn != None )
        {
            SetLocation(Pawn.Location);
            UnPossess();
        }
		//log("We are in spect. state, setting collisions off");
        bCollideWorld = false;

		CameraDist = Default.CameraDist;
    }

    function UpdateRotation(float DeltaTime, float maxPitch) {
    	local rotator ViewRotation;

		if (bStoryCamera) {
			ViewRotation = Rotation;
			TurnTarget = None;
            bRotateToDesired = false;
            bSetTurnRot = false;

            ViewRotation.Yaw += DeltaTime * camHorizontalTurn;
            ViewRotation.Pitch += DeltaTime * camVerticalTurn;
            ViewRotation.Roll += DeltaTime * camRollTurn;

            SetRotation(ViewRotation);
		}
		else {
			//log("GBxPlayer:updateRot");
			super.UpdateRotation(DeltaTime, maxPitch);
		}
	}

    function PlayerMove(float DeltaTime) {

    	if (!bStoryCamera)
		{
		    super.PlayerMove(DeltaTime);
			return;
		}
		//Rotating will be handled here
        UpdateRotation(DeltaTime, 1);

		//Because of replication - numbers behind . are not replicated
		//acceleration will be normalized in ProcessMove()
		//Due to some reason, this still does not help - perhaps because the acceleration
		//variable is also replicated, so the . part of the number is lost anyway
		//It is necessary to support whole numbers - with no real parts.
        Acceleration.x = myCamDirX / 1000;
        Acceleration.Y = myCamDirY / 1000;
        Acceleration.z = myCamDirZ / 1000;

        SpectateSpeed = myCamSpeed;

        if ( Role < ROLE_Authority ) // then save this move and replicate it
            ReplicateMove(DeltaTime, Acceleration, DCLICK_None, rot(0,0,0));
        else
            ProcessMove(DeltaTime, Acceleration, DCLICK_None, rot(0,0,0));
    }

    function EndState()
    {
        PlayerReplicationInfo.bIsSpectator = false;
        bCollideWorld = false;
        //We've stopped being camera
        bStoryCamera = false;
        //reset spectating speed to default value
        SpectateSpeed = default.SpectateSpeed;
    }
}

auto state PlayerWaiting
{
ignores SeePlayer, HearNoise, NotifyBump, TakeDamage, PhysicsVolumeChange, NextWeapon, PrevWeapon, SwitchToBestWeapon;

    exec function Jump( optional float F )
    {
    }

    exec function Suicide()
    {
    }

    function ServerRestartPlayer()
    {
        if ( Level.TimeSeconds < WaitDelay )
            return;
        if ( Level.NetMode == NM_Client )
            return;
        if ( Level.Game.bWaitingToStartMatch )
            PlayerReplicationInfo.bReadyToPlay = true;
        else
            Level.Game.RestartPlayer(self);
        }

    exec function Fire(optional float F)
    {
        LoadPlayers();
        if ( !bForcePrecache && (Level.TimeSeconds > 0.2) )
			ServerReStartPlayer();
    }

    exec function AltFire(optional float F)
    {
        Fire(F);
    }

    function EndState()
    {
        if ( Pawn != None )
            Pawn.SetMesh();
        if ( PlayerReplicationInfo != None )
			PlayerReplicationInfo.SetWaitingPlayer(false);
        bCollideWorld = false;
    }

    function BeginState()
    {
		CameraDist = Default.CameraDist;
        if ( PlayerReplicationInfo != None )
            PlayerReplicationInfo.SetWaitingPlayer(true);
		//log("We are in playerwaiting, setting collisions off");
        bCollideWorld = false;


    }
}

simulated function SetDialog(string Id, string Text, string BotName, string Options[10])
{
	local int i;

	DialogId = Id;
	DialogText = Text;
	DialogBotName = BotName;

	for (i=0;i<10;i++)
		DialogOptions[i] = Options[i];
}

function SelectActorOnServer(Actor inputActor, vector HitLocation)
{
	local GBClientClass G;
	local ControlConnection CC;

	SelectedActor = inputActor;

	//notify control servers
	if (BotDeathMatch(Level.Game).theControlServer != none)
	{
		for (G = BotDeathMatch(Level.Game).theControlServer.ChildList; G != None; G = G.Next )
		{
			CC = ControlConnection(G);
			if (CC != none)
				CC.HandlePlayerActorSelect(self, inputActor, HitLocation);
		}
	}
}

simulated function ClearDialogOnClient()
{
	local int i;

	DialogId = "";
	DialogBotName = "";
	DialogText = "";
	for (i=0;i<10;i++)
		DialogOptions[i]="";
}

function ClearDialogOnServer()
{
	local int i;

	DialogId = "";
	DialogBotName = "";
	DialogText = "";
	for (i=0;i<10;i++)
		DialogOptions[i]="";
}

function HandleKeyInput(int Key)
{
	local RemoteBot theBot;

	//Check if we have some RemoteBot selected
	if (SelectedActor.IsA('Pawn') && Pawn(SelectedActor).Controller != none && Pawn(SelectedActor).Controller.IsA('RemoteBot'))
	{
		theBot = RemoteBot(Pawn(SelectedActor).Controller);

	    theBot.HandlePlayerInput(self, Key);
	}
}

//UO
function handleEventCommand(int e) {
     Log("Handle event command");
     Log(e);
     if ((SelectedActor != None) && (SelectedActor.IsA('UsableObject'))) {
         switch(e) {
         case -1:
              spawnObject();
              break;
         case 0:
              pickUpObject();
              break;
         default:
         UsableObject(SelectedActor).handleEventCommand(e, Pawn );
         }
    }
}

//UO
function deleteObject() {
  local string logString;

  if(SelectedActor.IsA('UsableObject')) {
    logString = "IsUsable";
    Log(logString);

    UsableObject(SelectedActor).testCommand();
  }
}

//UO
 function pickUpObject() {
  local UsableObject selectedObject;

  Log("Pickup");
  if(! SelectedActor.IsA('UsableObject'))
  {
    Log("Is not UO");
    return;
  }



  selectedObject = UsableObject(SelectedActor);

  if(!selectedObject.enablePickup) {
    Log("Is not pickable");
    return;
    }

  if(holdedItemClass != None) {
    Log("Already have object");
    return;
  }


  Log("Is pickable");
  holdedItem = selectedObject.getItemType();
  holdedItemClass = selectedObject.getClass();
  selectedObject.pickup();

}

//UO
function spawnObject() {
  Log("Spawn object");

  if(holdedItemClass == None)
    return;

  Log("Not null Class");
  Spawn (holdedItemClass, self, ,Pawn.Location);
  holdedItemClass = None;
  holdedItem = ITEM_NONE;
}


//When we are using mouse cursor to select objects
state PlayerMousing
{
	simulated function EndState()
	{
		//SelectedActor = None;
		//SelectActorOnServer(None,vect(0,0,0));
		ClearDialogOnClient();
		ClearDialogOnServer();
	}

	//Select actor we have cursor over
	exec function Fire(float f)
	{
      // do stuff here for when players click their fire/select button

		local vector TraceEnd, HitLocation, HitNormal, Dir, CameraLoc;
		local rotator CameraRot;
		local actor ViewActor;
		local float Fov, FovDist;
		local float MouseX, MouseY;
		local actor Other, A;

		MouseX = PlayerMouse.X + LastHUDSizeX * 0.5;

		MouseY = PlayerMouse.Y + LastHUDSizeY * 0.5;

		CameraRot = GetViewRotation();
		CameraLoc = vect(0,0,0);

		PlayerCalcView(ViewActor, CameraLoc, CameraRot);

		Fov = (FovAngle/2) * Pi / 180.00;
		FovDist = (LastHUDSizeX/2) / Tan(Fov);

		Dir.X = FovDist;
		Dir.Y = MouseX - LastHUDSizeX / 2;
		Dir.Z = -(MouseY - LastHUDSizeY / 2);

		Dir = Dir >> CameraRot;

		TraceEnd = (CameraLoc) + 3000 * Normal(Dir);
		//TODO: this is client side trace - we should do it on server and send result to client
		// wasnt able to trace medkits, adrenaline, etc. this way

		//DrawStayingDebugLine(CameraLoc,TraceEnd,255,0,0);

		foreach TraceActors(class'Actor', A, HitLocation, HitNormal, TraceEnd, CameraLoc,)
		{
			// Prevent tracing my own Pawn by accident or PhysicsVolumes (this happened when testing)
			if ((A != none) && (A != Pawn) && !A.IsA('Volume'))
			{
				Other = A;
				break;
			}
		}

		//Other = Trace(HitLocation, HitNormal, TraceEnd, CameraLoc, true);

		if( Other != None)
		{
			SelectedActor = Other;
			SelectActorOnServer(SelectedActor, HitLocation);
		}

		return;

	}

    //Deselect actor.
    exec function AltFire( optional float F )
    {
		SelectedActor = None;
		SelectActorOnServer(None, vect(0,0,0));
    }

   simulated function PlayerMove(float DeltaTime)
   {
      local vector MouseV, ScreenV;

      // get the new mouse position offset
      MouseV.X = DeltaTime * aMouseX / (InputClass.default.MouseSensitivity * DesiredFOV * 0.01111);
      MouseV.Y = DeltaTime * aMouseY / (InputClass.default.MouseSensitivity * DesiredFOV * -0.01111);

      // update mouse position
      PlayerMouse += MouseV;

      // convert mouse position to screen coords, but only if we have good screen sizes
      if ((LastHUDSizeX > 0) && (LastHUDSizeY > 0))
      {
         ScreenV.X = PlayerMouse.X + LastHUDSizeX * 0.5;
         ScreenV.Y = PlayerMouse.Y + LastHUDSizeY * 0.5;
         // here is where you would use the screen coords to do a trace or check HUD elements
      }

      return;
   }
}


defaultproperties
{

}

