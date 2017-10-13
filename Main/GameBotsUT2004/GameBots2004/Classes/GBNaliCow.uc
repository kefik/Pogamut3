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
// Created Nali cow for Pogamut purposes
// Note: Should be child of Monter - but there are some problems with controlling
// the monster through RemoteBot class, so did it this way
//-----------------------------------------------------------
class GBNaliCow extends GBxPawn;


simulated function Setup(xUtil.PlayerRecord rec, bool bLoadNow)
{
	//Didnt work in default properties.. :-/
	Skins[0] = Texture(DynamicLoadObject("SkaarjPack_rc.JCow1", class'Texture'));
	Skins[1] = Texture(DynamicLoadObject("SkaarjPack_rc.JCow1", class'Texture'));
}

defaultproperties
{
/* Not supported in Pawn - just in Monster
     DeathAnim(0)="Dead"
     DeathAnim(1)="Dead2"
     DeathAnim(2)="Dead3"
     DeathAnim(3)="Dead3"
     InjureAnim(0)="TakeHit"
     InjureAnim(1)="TakeHit2"
     InjureAnim(2)="BigHit"
     InjureAnim(3)="BigHit"
     IdleAnim(0)="Breath"
     IdleAnim(1)="root"
     IdleAnim(2)="Chew"
     IdleAnim(3)="Poop"
     IdleAnim(4)="Shake"
     IdleAnim(5)="Swish"
     IdleSounds(0)=Sound'SkaarjPack_rc.Cow.ambCow'
     IdleSounds(1)=Sound'SkaarjPack_rc.Pupae.munch1p'
     IdleSounds(2)=Sound'SkaarjPack_rc.Pupae.munch1p'
     IdleSounds(3)=Sound'SkaarjPack_rc.Cow.cMoo2c'
     IdleSounds(4)=Sound'SkaarjPack_rc.Cow.shakeC'
     IdleSounds(5)=Sound'SkaarjPack_rc.Cow.swishC'
     StartingAnim="Breath"
     bMeleeFighter=False
     bCanDodge=False
     HitSound(0)=Sound'SkaarjPack_rc.Cow.injurC1c'
     HitSound(1)=Sound'SkaarjPack_rc.Cow.injurC2c'
     HitSound(2)=Sound'SkaarjPack_rc.Cow.injurC1c'
     HitSound(3)=Sound'SkaarjPack_rc.Cow.injurC2c'
     DeathSound(0)=Sound'SkaarjPack_rc.Cow.cMoo2c'
     DeathSound(1)=Sound'SkaarjPack_rc.Cow.DeathC1c'
     DeathSound(2)=Sound'SkaarjPack_rc.Cow.DeathC2c'
     DeathSound(3)=Sound'SkaarjPack_rc.Cow.DeathC2c'
     */
     bCanDodgeDoubleJump=False
     bCanPickupInventory=False
     WallDodgeAnims(0)="Landed"
     WallDodgeAnims(1)="Landed"
     WallDodgeAnims(2)="Landed"
     WallDodgeAnims(3)="Landed"
     IdleHeavyAnim="Breath"
     IdleRifleAnim="Breath"
     bCanJump=False
     bCanClimbLadders=False
     bCanStrafe=False
     bCanDoubleJump=False
     bCanUse=False
     MeleeRange=80.000000
     GroundSpeed=100.000000
     WaterSpeed=75.000000
     JumpZ=75.000000

     MovementAnims(0)="Run"
     MovementAnims(1)="Run"
     MovementAnims(2)="Run"
     MovementAnims(3)="Run"
     TurnLeftAnim="Walk"
     TurnRightAnim="Walk"
     SwimAnims(0)="Run"
     SwimAnims(1)="Run"
     SwimAnims(2)="Run"
     SwimAnims(3)="Run"
     WalkAnims(0)="Walk"
     WalkAnims(1)="Walk"
     WalkAnims(2)="Walk"
     WalkAnims(3)="Walk"
     AirAnims(0)="Landed"
     AirAnims(1)="Landed"
     AirAnims(2)="Landed"
     AirAnims(3)="Landed"
     TakeoffAnims(0)="Landed"
     TakeoffAnims(1)="Landed"
     TakeoffAnims(2)="Landed"
     TakeoffAnims(3)="Landed"
     LandAnims(0)="Landed"
     LandAnims(1)="Landed"
     LandAnims(2)="Landed"
     LandAnims(3)="Landed"
     DoubleJumpAnims(0)="Landed"
     DoubleJumpAnims(1)="Landed"
     DoubleJumpAnims(2)="Landed"
     DoubleJumpAnims(3)="Landed"
     DodgeAnims(0)="Landed"
     DodgeAnims(1)="Landed"
     DodgeAnims(2)="Landed"
     DodgeAnims(3)="Landed"
     AirStillAnim="Landed"
     TakeoffStillAnim="Landed"
     IdleCrouchAnim="Breath"
     IdleSwimAnim="Walk"
     IdleWeaponAnim="Breath"
     IdleRestAnim="Breath"
     Mesh=VertMesh'SkaarjPack_rc.NaliCow'
     PrePivot=(Z=0.000000)
     CollisionRadius=34.000000
     CollisionHeight=34.000000
}

