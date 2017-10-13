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


class GBReplicationInfo extends LinkedReplicationInfo;

var PlayerReplicationInfo MyPRI;

var vector myLocation;

var vector myVelocity;

var vector myFocus;

var rotator myRotation;

var bool myPawnIsNone;

var string myFocusName;

var float myAdrenaline;

var vector CustomRoute[32];

var Pawn myPawn;

replication
{
	reliable if (ROLE == ROLE_Authority)
		MyPRI, myFocus, myFocusName, myPawnIsNone, myLocation, myRotation,
		myVelocity, CustomRoute, myPawn, myAdrenaline;

}

event PostBeginPlay()
{
	if ( Role < ROLE_Authority )
		return;

	Timer();
	SetTimer(0.05, true);

}

simulated function SetCustomRoute(vector v, int i)
{
	CustomRoute[i] = v;
}

simulated function vector GetCustomRoute(int i)
{
	return CustomRoute[i];
}

simulated function vector GetVelocity()
{
	return myVelocity;
}

simulated function rotator GetRotation()
{
	if (myPawn != none)
		return myPawn.Rotation;
	return myRotation;
}

simulated function vector GetLocation()
{
	if (myPawn != none)
		return myPawn.Location;
	return myLocation;
}

simulated function bool PawnIsNone()
{
	return myPawnIsNone;
}

simulated function vector GetFocus()
{
	return myFocus;
}

simulated function string GetFocusName()
{
	return myFocusName;
}

simulated function Pawn GetPawn()
{
	return myPawn;
}

simulated function float GetAdrenaline()
{
	return myAdrenaline;
}

function UpdatePosition()
{
    local Pawn P;
    local Controller C;

    C = Controller(Owner);

    if( C != None )
        P = C.Pawn;

    if( P == none )
    {
    	myPawnIsNone = true;
    	myPawn = P;
        return;
    }
    else
    	myPawnIsNone = false;

	myLocation = P.Location;
	myRotation = P.Rotation;
	myRotation.Pitch = int(P.ViewPitch) * 65535/255;
	myVelocity = P.Velocity;
	myPawn = P;
	myAdrenaline = C.Adrenaline;

	if (C.Focus != none)
	{
		myFocusName = string(C.Focus);
		myFocus = C.Focus.Location;
	}
	else
	{
		myFocusName = "";
		myFocus = C.FocalPoint;
	}
}

function Timer()
{
	UpdatePosition();
	SetTimer(0.05, true);
}

// should be called right after the PlayerReplicationInfo was spawned
static function GBReplicationInfo SpawnFor(PlayerReplicationInfo OwnerPRI)
{
	local LinkedReplicationInfo LinkedRI;

	// check for existing linked RI
	LinkedRI = FindFor(OwnerPRI);
	if ( LinkedRI != None )
		return GBReplicationInfo(LinkedRI);

	// spawn a new one
	if ( OwnerPRI != None && OwnerPRI.Owner != None )
	{
		LinkedRI = OwnerPRI.Spawn(default.Class, OwnerPRI.Owner);
		LinkedRI.NextReplicationInfo = OwnerPRI.CustomReplicationInfo;
		OwnerPRI.CustomReplicationInfo = LinkedRI;
	}
	return GBReplicationInfo(LinkedRI);
}

// use this function to find your existing linked RI
static function GBReplicationInfo FindFor(PlayerReplicationInfo OwnerPRI)
{
	local LinkedReplicationInfo LinkedRI;

	if ( OwnerPRI == None )
		return None;

	for (LinkedRI = OwnerPRI.CustomReplicationInfo; LinkedRI != None; LinkedRI = LinkedRI.NextReplicationInfo)
		if ( GBReplicationInfo(LinkedRI) != None )
			return GBReplicationInfo(LinkedRI);

	return None;
}

defaultproperties
{
	NetUpdateFrequency=50
}

