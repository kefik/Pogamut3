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


class BotTeamGame extends CopyTeamGame;

#exec OBJ LOAD FILE=TeamSymbols.utx

var int MaxTeams; //TODO: This is a hack, can we get this correctly? Now = 2

function string GetGameInfo()
{
	local string outStr;

	outStr = " {GoalTeamScore " $ GoalScore $
		"} {TimeLimit " $ RemainingTime $
		"} {MaxTeams " $ MaxTeams $
		"} {MaxTeamSize " $ MaxTeamSize $
		"}";

	return outStr;
}

function RemoteRestartGameState() {
	local int i;

	super.RemoteRestartGameState();

	for (i = 0; i < MaxTeams; i++)
	{
		Teams[i].Score = 0;
	}
}

event InitGame( string Options, out string Error )
{
	super.InitGame( Options, Error );

	//Overriding balancing teams here - setting to false;
	bBalanceTeams = false;
	bPlayersBalanceTeams = false;
}

function SendGameStatus(GBClientClass requester)
{

	local int i;

	super.SendGameStatus(requester);

	for (i = 0; i < MaxTeams; i++)
	{
		requester.SendLine("TES {Id " $ Teams[i].TeamIndex $
			"} {Team " $ Teams[i].TeamIndex $
			"} {Score " $ int(Teams[i].Score) $
			"}");
	}

}

function bool AddRemoteBotToTeam(Bot NewBot, int TeamNum)
{
	local int i, DesiredTeam, MinSize, NextBotTeam;

	if ( bBalanceTeams )
	{
		MinSize = Teams[0].Size;
		DesiredTeam = 0;
		for ( i=1; i<MaxTeams; i++ )
			if ( Teams[i].Size < MinSize )
			{
				MinSize = Teams[i].Size;
				DesiredTeam = i;
			}
	}
	else
		DesiredTeam = TeamNum;

	//NewBot.PlayerReplicationInfo.Team = 255;

	NewBot.PlayerReplicationInfo.bBot = True;

	if ( (DesiredTeam != 255) && ChangeTeam(NewBot, DesiredTeam, true) )
	{
		return true;
	}
	else
	{
		log("Can't add bot to team "$DesiredTeam$". Using different.");
		for (NextBotTeam = 0; NextBotTeam < MaxTeams; NextBotTeam++)
		{
			if (ChangeTeam(NewBot, NextBotTeam, true))
			{
				return true;
			}
		}
		log("Adding to teams failed!!");
		return false;
	}
}

function UnrealTeamInfo GetEpicBotTeam(optional int TeamNumber)
{
	if ( bBalanceTeams )
	{
		return GetBotTeam();
	}
	else
	{
	 	if ((TeamNumber >= 0) && (TeamNumber < MaxTeams))
	 	{
			return Teams[TeamNumber];
		}
		return GetBotTeam();
	}

}

defaultproperties
{
	DefaultEnemyRosterClass="xGame.xTeamRoster"
	MapListType="XInterface.MapListTeamDeathMatch"
	DeathMessageClass=XGame.xDeathMessage
	ScreenShotName="UT2004Thumbnails.TDMShots"
	DecoTextName="XGame.TeamGame"
	Acronym="TDM"
	NetWait=2
	CountDown=0
	bAllowControlServer=true
	HUDType="xInterface.HudCTeamDeathMatch"
	GameName="GameBots Team Deathmatch"
	MaxTeams=2  //TODO: Hack, should find out somehow properly
	GameClass="BotTeamGame"
	MapPrefix="DM"

}

