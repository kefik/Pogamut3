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


class BotScenario extends BotDeathMatch
	config(GameBots2004);

var config vector FactoryLocation;

var config float FactoryRadius;

var config vector DisperserLocation;

var config float DisperserRadius;

var config float FactoryAdrenalineCount;

var config string FactorySpawnClass;

var config string StrongEnemyName;

var EmotionEmitter FactoryEmitter;

event PostBeginPlay() {
	super.PostBeginPlay();

	if (FactoryEmitter == none) {
		FactoryEmitter = Spawn(class'EmotionEmitter', self,, FactoryLocation, );
		//FactoryEmitter.SetFirstColor(class'Canvas'.Static.MakeColor(int(colorVec.x),int(colorVec.y),int(colorVec.z)));
		//FactoryEmitter.SetSecondColor(class'Canvas'.Static.MakeColor(int(colorVec.x),int(colorVec.y),int(colorVec.z)));
		//FactoryEmitter.SetStartSize(realSizeRange);


	}

	/*	realSizeRange.X.Max = sizeRange.X;
		realSizeRange.X.Min = sizeRange.X;
		realSizeRange.Y.Max = sizeRange.Y;
		realSizeRange.Y.Min = sizeRange.Y;
		realSizeRange.Z.Max = sizeRange.Z;
		realSizeRange.Z.Min = sizeRange.Z;*/
}

function bool atFactory(vector location) {
	if (VSize(FactoryLocation - location) < FactoryRadius)
		return true;
	else
		return false;
}

function string GetGameInfo()
{
	local string outStr;

	outStr = " {FactoryLocation " $ FactoryLocation $
		"} {FactoryRadius " $ FactoryRadius $
		"} {DisperserLocation " $ DisperserLocation $
		"} {DisperserRadius " $ DisperserRadius $
		"} {FactoryAdrenalineCount " $ FactoryAdrenalineCount $
		"} {FactorySpawnType " $ FactorySpawnClass $
		"}";

	return outStr;
}

/* We override this function, so we can restart bot's adrenalin when he respawns */
function SpawnPawn
(
	RemoteBot NewBot,
	optional vector startLocation,
	optional rotator startRotation
)
{
	super.SpawnPawn(NewBot, startLocation, startRotation);
	//reset the adrenaline
	NewBot.Adrenaline = 0;
}

function bool canReceiveInventory(RemoteBot theBot) {
	if ((theBot.PlayerReplicationInfo != none) && (InStr(theBot.PlayerReplicationInfo.PlayerName,StrongEnemyName) != -1))
		return true;
	return false;
}

function bool canUseFactory(RemoteBot thebot) {

	if (theBot.Adrenaline > FactoryAdrenalineCount) {
		return true;
	} else {
		return false;
	}
}

function bool CheckScenarioEndGame() {

	local Controller C;
	local xPawn xP;
	local bool bEndGame;

	//log("here");

	for (C = Level.ControllerList; C != none; C=C.NextController) {
		if (C.IsA('RemoteBot') && (C.Pawn != none)) {
			xP = xPawn(C.Pawn);
			if ((xP != none) && xP.HasUDamage() && (VSize(xP.Location - DisperserLocation) < DisperserRadius) ) {
				bEndGame = true;
				break;
			}
		}
	}
	if (bEndGame) {
		EndGame(C.PlayerReplicationInfo, "ObjectiveSuccessful" );
		GotoState('MatchOver');
	}
	return false;
}


function UseFactory(RemoteBot thebot) {

	local Pickup Copy;

	if (theBot.Pawn == none)
		return;

	if (theBot.Adrenaline >= FactoryAdrenalineCount)
		theBot.Adrenaline -= FactoryAdrenalineCount;
	else
		theBot.Adrenaline = 0;

	Copy = Spawn(class<Pickup>(DynamicLoadObject(FactorySpawnClass, class 'Class')),theBot.Pawn,,theBot.Pawn.Location,rot(0,0,0));
	Copy.bDropped = true;
	Copy.LifeSpan = 20;
}

/* Copy from Deathmatch. Modified for our purposes */
State MatchInProgress
{
    function Timer()
    {
        local Controller P;

        Global.Timer();
		if ( !bFinalStartup )
		{
			bFinalStartup = true;
			PlayStartupMessage();
		}
        if ( bForceRespawn )
            For ( P=Level.ControllerList; P!=None; P=P.NextController )
            {
                if ( (P.Pawn == None) && P.IsA('PlayerController') && !P.PlayerReplicationInfo.bOnlySpectator )
                    PlayerController(P).ServerReStartPlayer();
            }
        if ( NeedPlayers() && AddBot() && (RemainingBots > 0) )
			RemainingBots--;

        if ( bOverTime )
			EndGame(None,"TimeLimit");
        else if ( TimeLimit > 0 )
        {
            GameReplicationInfo.bStopCountDown = false;
            RemainingTime--;
            GameReplicationInfo.RemainingTime = RemainingTime;
            if ( RemainingTime % 60 == 0 )
                GameReplicationInfo.RemainingMinute = RemainingTime;
            if ( RemainingTime <= 0 )
                EndGame(None,"TimeLimit");
        }
        else if ( (MaxLives > 0) && (NumPlayers + NumBots != 1) )
			CheckMaxLives(none);

        ElapsedTime++;
        GameReplicationInfo.ElapsedTime = ElapsedTime;
    }

    function beginstate()
    {
		local PlayerReplicationInfo PRI;

		ForEach DynamicActors(class'PlayerReplicationInfo',PRI)
			PRI.StartTime = 0;
		ElapsedTime = 0;
		bWaitingToStartMatch = false;
        StartupStage = 5;
        PlayStartupMessage();
        StartupStage = 6;
    }
Begin:
      CheckScenarioEndGame();
      sleep(1);
      goto 'Begin';
}

/*
function string GetGameInfo()
{
	return "";
}*/

defaultproperties
{
	FactoryLocation=(X=1326,Y=-567,Z=-78)
	FactoryRadius=300
	DisperserLocation=(X=1326,Y=-567,Z=-78)
	DisperserRadius=300
	FactoryAdrenalineCount=3
	FactorySpawnClass="XPickups.UDamagePack"
	StrongEnemyName="emohawk"
	DefaultEnemyRosterClass="XGame.xDMRoster"
	GoalScore=1000
	Acronym="SCEN"
	NetWait=2
	CountDown=0
	bAllowControlServer=true
	HUDType="xInterface.HudCDeathmatch"
	GameName="GameBots Scenario Game"
	GameClass="BotScenario"
	MapPrefix="DM"
}

