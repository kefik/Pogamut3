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
// BotDomination.
//=============================================================================
class BotDoubleDomination extends CopyxDoubleDom;

function string GetGameInfo()
{
	local string outStr;

	outStr = " {GoalTeamScore " $ GoalScore $
		"} {TimeLimit " $ RemainingTime $
		"} {MaxTeams " $ MaxTeams $
		"} {MaxTeamSize " $ MaxTeamSize $
		"} {FirstDomPointLocation "$ xDomPoints[0].Location $
		"} {SecondDomPointLocation "$ xDomPoints[1].Location $
		"}";

	return outStr;
}

function RemoteRestartGameState() {
	local int j;

	super.RemoteRestartGameState();

	for ( j=0; j<2; j++ )
	{
		if ( xDomPoints[j] != none )
		{
			xDomPoints[j].ResetPoint(true);
		}
	}
}

function SendGameStatus(GBClientClass requester)
{

//	local int j, teamNum;

//	super.SendGameStatus(requester);

	//Control Points changed to xDomPoints and just 2 of them in a map now
	//for ( j=0; j<2; j++ )
	//{
		//TODO: Export DOM points here? Or in checkvision?
		/*if ( xDomPoints[j] != none )
		{
			if( xDomPoints[j].ControllingTeam == none )
				teamNum = 255;
			else
				teamNum = xDomPoints[j].ControllingTeam.TeamIndex;

			outStr = ( outStr $ib$as$ xDomPoints[j] $ib$ teamNum $ae );
		}   */
	//}

}

defaultproperties
{
	bBalanceTeams=False
    DefaultEnemyRosterClass="xGame.xTeamRoster"
	MapListType="XInterface.MapListDoubleDomination"
	GoalScore=3
	ScreenShotName="UT2004Thumbnails.DOMShots"
	DecoTextName="XGame.DoubleDom"
	NetWait=2
	CountDown=0
	Acronym="DOM2"
	bPauseable=True
	bAllowControlServer=true
	HUDType="XInterface.HudCDoubleDomination"
	GameName="GameBots Double Domination"
	GameClass="BotDoubleDomination"
	MapPrefix="DOM"
}

