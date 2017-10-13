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


//This class spawns GBHudInteraction class for every player in the game
class GBHUDMutator extends Mutator;

var bool bAffectSpectators; // If this is set to true, an interaction will be created for spectators
var bool bAffectPlayers; // If this is set to true, an interaction will be created for players
var bool bHasInteraction;

function PreBeginPlay()
{
    //Log("ICU Mutator Started"); // Always comment out your logs unless they're errors
}

//Here we add GBHUDINTERCATION for our local PlayerController
simulated function Tick(float DeltaTime)
{
    local PlayerController PC;

    // If the player has an interaction already, exit function.
    if (bHasInteraction)
        Return;
    PC = Level.GetLocalPlayerController();

    // Run a check to see whether this mutator should create an interaction for the player
    if ( PC != None && ((PC.PlayerReplicationInfo.bIsSpectator && bAffectSpectators) || (bAffectPlayers && !PC.PlayerReplicationInfo.bIsSpectator)) )
    {
        PC.Player.InteractionMaster.AddInteraction("GameBots2004.GBHUDInteraction", PC.Player); // Create the interaction
        bHasInteraction = True; // Set the variable so this lot isn't called again
    }
	Disable('Tick');
}

//This is called when the map is changed on the server - we will need to reinitialize
function ServerTraveling(string URL, bool bItems)
{
	bHasInteraction = false;
    Enable('Tick');
	super.ServerTraveling(URL,bItems);
}

DefaultProperties
{
     bAffectSpectators=true
     bAffectPlayers=true
     RemoteRole=ROLE_SimulatedProxy
     bAlwaysRelevant=true
     bAddToServerPackages=True
     //ConfigMenuClassName=""
     GroupName="GBHUDMutator"
     FriendlyName="GBHUDMutator"
     Description="GameBots2004 HUD mutator, that spawns GBHUD for all PlayerControllers.||v1.0"
}
