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

class DrawLineEffect extends xEmitter;

var class<ShockBeamCoil> CoilClass;

function LaunchBeam(vector TargetLocation, bool bHit)
{
	mSpawnVecA = TargetLocation;
    if (Level.NetMode != NM_DedicatedServer)
        SpawnEffects(bHit);
}


simulated function PostNetBeginPlay()
{

}


simulated function bool CheckMaxEffectDistance(PlayerController P, vector SpawnLocation)
{
	return !P.BeyondViewDistance(SpawnLocation,3000);
}

simulated function SpawnEffects(bool bHit)
{
    local ShockBeamCoil Coil;
    /*
	local xWeaponAttachment Attachment;

    if (Instigator != None)
    {
        if ( Instigator.IsFirstPerson() )
        {
			if ( (Instigator.Weapon != None) && (Instigator.Weapon.Instigator == Instigator) )
				SetLocation(Instigator.Weapon.GetEffectStart());
			else
				SetLocation(Instigator.Location);
            Spawn(MuzFlashClass,,, Location);
        }
        else
        {
            Attachment = xPawn(Instigator).WeaponAttachment;
            if (Attachment != None && (Level.TimeSeconds - Attachment.LastRenderTime) < 1)
                SetLocation(Attachment.GetTipLocation());
            else
                SetLocation(Instigator.Location + Instigator.EyeHeight*Vect(0,0,1) + Normal(mSpawnVecA - Instigator.Location) * 25.0);
            Spawn(MuzFlash3Class);
        }
    } */

	/*
    if ( EffectIsRelevant(mSpawnVecA + HitNormal*2,false) && (HitNormal != Vect(0,0,0)) )
		SpawnImpactEffects(Rotator(HitNormal),mSpawnVecA + HitNormal*2);
		*/

    if ( (!Level.bDropDetail && (Level.DetailMode != DM_Low) && (VSize(Location - mSpawnVecA) > 40) && !Level.GetLocalPlayerController().BeyondViewDistance(Location,0))
		|| ((Instigator != None) && Instigator.IsFirstPerson()) )
    {
	    Coil = Spawn(CoilClass,,, Location, Rotation);

	    if (Coil != None)
		    Coil.mSpawnVecA = mSpawnVecA;
		Pawn(Owner).AttachToBone(Coil, 'spine');
    }
}

defaultproperties
{
     CoilClass=class'TraceLineRed'
     mParticleType=PT_Beam
     mMaxParticles=3
     mLifeRange(0)=10.000000
     mRegenDist=0.000000
     mSizeRange(0)=12.000000
     mSizeRange(1)=24.000000
     mAttenKa=0.000000
     bReplicateInstigator=True
     bReplicateMovement=True
     RemoteRole=ROLE_SimulatedProxy
     NetPriority=3.000000
     LifeSpan=0.500000
     Texture=Texture'XWeapons_rc.Effects.ShockBeamTex'
     Skins(0)=Texture'XWeapons_rc.Effects.ShockBeamTex'
     Style=STY_Additive
}

