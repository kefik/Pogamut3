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


//-----------------------------------------------------------
// GBSquadAI
//-----------------------------------------------------------
// We need to inherit and modify this class, so it is not affecting our bots
// This class is needed so the game mechanics can work properly

class GBCTFSquadAI extends CTFSquadAI;


function AssignCombo(Bot B)
{

}

function bool AllowDetourTo(Bot B,NavigationPoint N)
{
	return false;
}

function bool FindPathToObjective(Bot B, Actor O)
{
	return false;
}

function bool AllowTranslocationBy(Bot B)
{
	return false;
}

function bool GoPickupFlag(Bot B)
{
	return false;
}

function actor FormationCenter()
{
 	return none;
}

function bool VisibleToEnemiesOf(Actor A, Bot B)
{
	return false;
}

function NavigationPoint FindHidePathFor(Bot B)
{
 	return none;
}

function bool CheckVehicle(Bot B)
{
	return false;
}

function bool OrdersForFlagCarrier(Bot B)
{
 	return false;
}

function bool MustKeepEnemy(Pawn E)
{
 	return false;
}

function bool NearEnemyBase(Bot B)
{
 	return false;
}

function bool NearHomeBase(Bot B)
{
 	return false;
}

function bool FlagNearBase()
{
	return false;
}

function bool OverrideFollowPlayer(Bot B)
{
	return false;
}

function bool CheckSquadObjectives(Bot B)
{
 	return false;
}

function EnemyFlagTakenBy(Controller C)
{

}

function bool AllowTaunt(Bot B)
{
	return true;
}

function bool ShouldDeferTo(Controller C)
{
	return false;
}

function byte PriorityObjective(Bot B)
{
	return 0;
}

function float ModifyThreat(float current, Pawn NewThreat, bool bThreatVisible, Bot B)
{
	return 0;
}

function bool MergeEnemiesFrom(SquadAI S)
{
	return false;
}

function bool LostEnemy(Bot B)
{
 	return false;
}

function bool AddEnemy(Pawn NewEnemy)
{
	return false;
}

function bool ValidEnemy(Pawn NewEnemy)
{
	return false;
}

function bool SetEnemy( Bot B, Pawn NewEnemy )
{
	return false;
}

function SetAlternatePath(bool bResetSquad)
{

}

function bool FindNewEnemyFor(Bot B, bool bSeeEnemy)
{
 	return true;
}

function MergeWith(SquadAI S)
{

}

function bool TryToIntercept(Bot B, Pawn P, Actor RouteGoal)
{
 	return false;
}

function SetLeader(Controller C)
{

}

function SetObjective(GameObjective O, bool bForceUpdate)
{

}

function Retask(bot B)
{

}

function PickNewLeader()
{
}

function bool TellBotToFollow(Bot B, Controller C)
{
	return true;
}

function bool AssignSquadResponsibility(Bot B)
{
	return true;
}

function bot PickBotToReassign()
{
	return none;
}

function bool ClearPathFor(Controller C)
{
	return true;
}

function Vehicle GetLinkVehicle(Bot B)
{
	return none;
}

defaultproperties
{

}
