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


//This mutator disables weapons in GameBots game.
class GBNoWeaponMutator extends Mutator;

var bool bInited;

//Called as the first function
simulated function BeginPlay()
{

    if(!bInited)
    {
        bInited = true;

        if (Level.Game.IsA('BotDeathMatch'))
        {
        	//HACK: This mutator works propersly just with GB gametypes
        	//For some reason there wasn't any other possibility how to erase all
        	//the weapons from the map then this (this bool will trigger this behavior)
			BotDeathMatch(Level.Game).bShouldEraseAllWeapons = true;

		}

    }
    Super.BeginPlay();
}

//Default weapon is none
function Class<Weapon> GetDefaultWeapon()
{
	return None;
}

function ModifyPlayer(Pawn Other)
{
	if(Other.Weapon != None)
	{
		Other.Weapon.Destroy();
		Other.Weapon = None;
	}
	Super.ModifyPlayer(Other);
}


function ServerTraveling(string URL, bool bItems)
{
	bInited = false;
	super.ServerTraveling(URL,bItems);

}


defaultproperties
{

     RemoteRole=ROLE_SimulatedProxy
     bAlwaysRelevant=true
     Description="Will remove all weapons from the GameBots game."
}
