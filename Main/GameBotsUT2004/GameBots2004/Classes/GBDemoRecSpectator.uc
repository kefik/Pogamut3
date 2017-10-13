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
// GBDemoRecSpectator - allows to spectate bots in demo, standard DemoRecSpectator doesnt allow to do this
// needs to be set in UT2004.ini in [Engine.DemoRecDriver] DemoSpectatorClass=GameBots2004.GBDemoRecSpectator
// BEFORE the demo starts to be recorded
//=============================================================================

class GBDemoRecSpectator extends UnrealPlayer;

var bool bTempBehindView;
var bool bFoundPlayer;
var string RemoteViewTarget;	// Used to track targets without a controller

event PostBeginPlay()
{
	local class<HUD> HudClass;
	local class<Scoreboard> ScoreboardClass;

	// We're currently doing demo recording
	if( Role == ROLE_Authority && Level.Game != None )
	{
		HudClass = class<HUD>(DynamicLoadObject(Level.Game.HUDType, class'Class'));
		if( HudClass == None )
			log( "Can't find HUD class "$Level.Game.HUDType, 'Error' );
        ScoreboardClass = class<Scoreboard>(DynamicLoadObject(Level.Game.ScoreBoardType, class'Class'));
		if( ScoreboardClass == None )
			log( "Can't find HUD class "$Level.Game.ScoreBoardType, 'Error' );
		ClientSetHUD( HudClass, ScoreboardClass );
	}

	Super.PostBeginPlay();

	if ( PlayerReplicationInfo != None )
		PlayerReplicationInfo.bOutOfLives = true;
	log("GBDemoRecSpectator:PostBeginPlay()");
}

function InitPlayerReplicationInfo()
{
	Super.InitPlayerReplicationInfo();
	PlayerReplicationInfo.PlayerName="DemoRecSpectator";
	PlayerReplicationInfo.bIsSpectator = true;
	PlayerReplicationInfo.bOnlySpectator = true;
	PlayerReplicationInfo.bOutOfLives = true;
	PlayerReplicationInfo.bWaitingPlayer = false;
}

exec function ViewClass( class<actor> aClass, optional bool bQuiet, optional bool bCheat )
{
	local actor other, first;
	local bool bFound;

	first = None;

	ForEach AllActors( aClass, other )
	{
		if ( bFound || (first == None) )
		{
			first = other;
			if ( bFound )
				break;
		}
		if ( other == ViewTarget )
			bFound = true;
	}

	if ( first != None )
	{
		SetViewTarget(first);
		bBehindView = ( ViewTarget != self );

		if ( bBehindView )
			ViewTarget.BecomeViewTarget();
	}
	else
		SetViewTarget(self);
}

//==== Called during demo playback ============================================

exec function DemoViewNextPlayer()
{
    local Pawn P, Pick;
    local bool bFound;

    // view next player
    if ( PlayerController(RealViewTarget) != None )
		PlayerController(RealViewTarget).DemoViewer = None;

	foreach DynamicActors(class'Pawn', P)
		if ( P != none )
		{
			if ( Pick == None )
				Pick = P;
			if ( bFound )
			{
				Pick = P;
				break;
			}
			else
				bFound = ( (RealViewTarget == P) || (ViewTarget == P) );
		}

    SetViewTarget(Pick);
    //ClientSetViewTarget(Pick);

    if ( PlayerController(RealViewTarget) != None )
		PlayerController(RealViewTarget).DemoViewer = self;
}

auto state Spectating
{
    function BeginState() {
        bCollideWorld = false;
    }

    exec function Fire( optional float F )
    {
        bBehindView = false;
        demoViewNextPlayer();
    }

    exec function AltFire( optional float F )
    {
        bBehindView = !bBehindView;
    }

	event PlayerTick( float DeltaTime )
	{
		Super.PlayerTick( DeltaTime );

		// attempt to find a player to view.
		if( Role == ROLE_AutonomousProxy && (RealViewTarget==None || RealViewTarget==Self) && !bFoundPlayer )
		{
			DemoViewNextPlayer();
			if( ViewTarget!=None )
				bFoundPlayer = true;
		}

		// hack to go to 3rd person during deaths
		if( RealViewTarget!=None && RealViewTarget.Pawn==None )
		{
			if (!bBehindview)
			{
				if( !bTempBehindView )
				{
					bTempBehindView = true;
					bBehindView = true;
				}
			}
		}
		else
		if( bTempBehindView )
		{
			bBehindView = false;
			bTempBehindView = false;
		}
	}
}

event PlayerCalcView(out actor ViewActor, out vector CameraLocation, out rotator CameraRotation )
{
	local Rotator R;

	if( ViewTarget != None )
	{
		R = ViewTarget.Rotation;
	}

	Super.PlayerCalcView(ViewActor, CameraLocation, CameraRotation );

	if( ViewTarget != None )
	{
		if ( !bBehindView )
		{
			CameraRotation = R;
			if ( Pawn(ViewTarget) != None ) {
				CameraLocation.Z += Pawn(ViewTarget).BaseEyeHeight; // FIXME TEMP
				CameraRotation.Roll = 0;
				CameraRotation.Pitch = int(Pawn(ViewTarget).ViewPitch) * 65535/255;
			}
		}

		if (RealViewTarget != none)
			RealViewTarget.SetRotation(R);
	}
}

defaultproperties
{
     RemoteRole=ROLE_AutonomousProxy
     bDemoOwner=True
}

