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

class TraceLine extends xEmitter
	abstract;

#exec OBJ LOAD FILE=XEffectMat.utx

var vector StartEffect, EndEffect;

//----------

var rotator BeamDirection; //Relative Beam Direction
var float BeamLength; //Beam Length
var bool bFloorCorrection; //if we should adjust ray according to floor normal

//----------


replication
{

    reliable if (Role == ROLE_Authority) //&& (!bNetOwner || bDemoRecording || bRepClientDemo)  )
        StartEffect, EndEffect;
}


simulated function Destroyed()
{
    Super.Destroyed();
}

simulated function SetBeamLocation()
{
	StartEffect = Instigator.Location;
	//SetLocation(Instigator.Location);
}

simulated function updateColor(bool bResult)
{

/*	if (bResult) //hit something
	{
   		Skins[0]=FinalBlend(DynamicLoadObject("XEffectMat.Link.LinkBeamRedFB", class'FinalBlend'));
		mColorRange[0]=class'Canvas'.Static.MakeColor(255,0,0);
		mColorRange[1]=class'Canvas'.Static.MakeColor(255,0,0);
	}
	else
	{
		Skins[0]=FinalBlend(DynamicLoadObject("XEffectMat.Link.LinkBeamGreenFB", class'FinalBlend'));
		mColorRange[0]=class'Canvas'.Static.MakeColor(240,240,240);
		mColorRange[1]=class'Canvas'.Static.MakeColor(240,240,240);
	}*/
}

simulated function Vector SetBeamRotation()
{
	local vector  FloorLocation, FloorNormal;
	local vector  RealRayDirection;


	RealRayDirection = vector(Owner.Rotation + BeamDirection);
	//we have to take into account also angle of the floor we are standing on
	if (bFloorCorrection)
	{
		FloorNormal = vect(0,0,0);
		Trace(FloorLocation,FloorNormal, Owner.Location + vect(0,0,-100), Owner.Location, false, ,);

		RealRayDirection += FloorNormal * (RealRayDirection dot FloorNormal) * -1;
	}

	EndEffect = StartEffect + (RealRayDirection * BeamLength);

    SetRotation( Rotator(EndEffect - Location) );

	return Normal(EndEffect - Location);
}

simulated function Tick(float dt)
{

	if ( Instigator == none || RemoteBot(Instigator.Controller).bAutoTrace == false || RemoteBot(Instigator.Controller).bDrawTraceLines == false)
    {
    	Destroy();
		return;
    }

	// set beam start location, rotation

	SetBeamLocation();
	SetBeamRotation();
    //mSpawnVecB = StartEffect;
    mSpawnVecA = EndEffect;
}

defaultproperties
{
     mParticleType=PT_Beam
     mMaxParticles=3
     mSpinRange(0)=45000.000000
     mSizeRange(0)=11.000000
    // mColorRange(0)=(B=240,G=240,R=240)
    // mColorRange(1)=(B=240,G=240,R=240)
     mAttenuate=False
     mAttenKa=0.000000
     mBendStrength=3.000000
     mWaveLockEnd=True
     LightType=LT_Steady
     LightHue=100
     LightSaturation=100
     LightBrightness=255.000000
     LightRadius=4.000000
     bDynamicLight=True
     bNetTemporary=False
     LifeSpan=60.750000
     bReplicateInstigator=True
     bAlwaysRelevant=True
     RemoteRole=ROLE_SimulatedProxy
     //Skins(0)=FinalBlend'XEffectMat.Link.LinkBeamGreenFB'
     Style=STY_Additive
}





