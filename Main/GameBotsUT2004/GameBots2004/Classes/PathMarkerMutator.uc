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
// Mutator.
//=============================================================================
class PathMarkerMutator extends Mutator;

var bool bInited;

event PostBeginPlay()
{
	local NavigationPoint N;
	local PathMarker PM;

	Super.PostBeginPlay();

    if(!bInited)
    {
        bInited = true;

    	for ( N=Level.NavigationPointList; N!=None; N=N.NextNavigationPoint )
    	{
    		if(!N.IsA('InventorySpot')) {
   				PM = Spawn(class'GameBots2004.PathMarker',N,,N.Location);
   				PM.bHidden = false;
			}

			/*
			S = ScriptedTexture(DynamicLoadObject("Engine.ScriptedTexture", class'ScriptedTexture'));

			S = ScriptedTexture(new(None) ObjectClass);  //ScriptedTexture(Level.ObjectPool.AllocateObject(class'ScriptedTexture'));

			S.DrawText(1, 1,string(N), CurrentFont ,C);




			class'BotAPI.CalendarMesh'.default.DisplayedText = string(N);
			class'BotAPI.CalendarMesh'.static.StaticSaveConfig();

			class'BotAPI.CalendarMesh'.static.updateText(string(N));
			A = spawn(class'BotAPI.CalendarMesh',N,,N.Location);
			CalendarMesh(A).updateText(string(N));
			A.ResetStaticFilterState();

			NameMarker(A).DisplayedText = string(N);
			*/

    	}
    }
}

defaultproperties
{
}
