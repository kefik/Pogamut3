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


class GBxPawn extends xPawn;
//	dependsOn(xUtil);

#exec OBJ LOAD FILE=PlayerSkins.utx
#exec OBJ LOAD FILE=HumanMaleA.ukx

#exec OBJ LOAD FILE=SkaarjPackSkins.utx


var string DesiredSkin;

//last command received by GB
var string LastGBCommand;

//last path sent by GB
var string LastGBPath;

//text bubble that appears above the bot
var string TextBubble;
var bool bDrawTextBubble;

replication
{
	reliable if (Role == ROLE_Authority)
		DesiredSkin, TextBubble, LastGBCommand, LastGBPath, bDrawTextBubble;
	reliable if (ROLE < ROLE_Authority )
		ServerSetup;
}

simulated function Destroyed()
{
    Super.Destroyed();
}

/*
* Need to set bSpecialCalcView in order for this fc to be executed. Fixes spectator view - pitch is now taken into account
* This fc NEEDs to be simulated - will be called on clients!
*/
simulated function bool SpectatorSpecialCalcView(PlayerController PC, out Actor ViewActor, out vector CameraLocation, out rotator CameraRotation) {
	ViewActor = self;
	if (PC.bBehindView) //behind view should be handled as normal
		return false;

	CameraLocation = Location;
    //CameraLocation += (CollisionHeight) * vect(0,0,1);
	CameraLocation += EyePosition();
	//CameraLocation.z += EyeHeight;
	CameraRotation = Rotation;
	CameraRotation.Pitch = int(ViewPitch) * 65535/255;

	CameraLocation += vector(CameraRotation) * 30; //so we don't see through bots head

	return true;
}

// called if bScriptPostRender is true, overrides native team beacon drawing code
simulated event PostRender2D(Canvas C, float ScreenLocX, float ScreenLocY)
{
	local PlayerController PC;

	PC = Level.GetLocalPlayerController();

	if ( (PC != None) && (PC.myHUD != None) )
	{
		GBxPlayer(PC).GBHUD.NotifySeePawn(C,self,ScreenLocX, ScreenLocY);
	}
}


//We override this function so we can set bot skins

simulated function Setup(xUtil.PlayerRecord rec, optional bool bLoadNow)
{
	local string meshName, temp;
	local xUtil.PlayerRecord PRE;

	//native bots sets skins differently.
	if (Controller.IsA('GBxBot')) {
		super.Setup(rec,bLoadNow);
		return;
	}

	PRE = class'xUtil'.static.FindPlayerRecord(DesiredSkin);

	if (PRE.Species != none)
	{
		//We've found record - that means valid player record supported
		//Setup it and end
		super.Setup(PRE, true);

		return;
	}

	//Player record not found
	LinkMesh(Mesh(DynamicLoadObject(DesiredSkin, class'Mesh')),false);
	//bScriptPostRender = true;
	if (DesiredSkin == "ThunderCrash.JakobM")
	{
		//Default character for GB (histrical reasons)
		//It is stored elsewhere then other skins
		Skins[0]=Texture(DynamicLoadObject("DemoPlayerSkins.Jakob_Body", class'Texture'));
		Skins[1]=Texture(DynamicLoadObject("DemoPlayerSkins.Jakob_NewHead", class'Texture'));
	}
	else
	{
	    Divide(DesiredSkin, ".", temp, meshName);

		// This should make most of default built-in UT2004 player meshes work with
		// good textures.
		// TODO: allow to set also these two variables?
		Skins[0]=Texture(DynamicLoadObject("PlayerSkins."$meshName$"BodyA", class'Texture'));
		Skins[1]=Texture(DynamicLoadObject("PlayerSkins."$meshName$"HeadA", class'Texture'));
	}

	ResetPhysicsBasedAnim();
	//set the skins on the server
	ServerSetup();

}

//Same code as the function above. We need this, so the skins are set also on ther
//server. Otherwise ppl running server through UT2004 wouldn't see changed skins
//on the server instance
function ServerSetup()
{
	local string meshName, temp;
	local xUtil.PlayerRecord PRE;

	PRE = class'xUtil'.static.FindPlayerRecord(DesiredSkin);

	if (PRE.Species != none)
	{
		//We've found record - that means valid player record supported
		//Setup it and end
		super.Setup(PRE, true);
		return;
	}

	//Player record not found
	LinkMesh(Mesh(DynamicLoadObject(DesiredSkin, class'Mesh')),false);
	//bScriptPostRender = true;
	if (DesiredSkin == "ThunderCrash.JakobM")
	{
		//Default character for GB (histrical reasons)
		//It is stored elsewhere then other skins
		Skins[0]=Texture(DynamicLoadObject("DemoPlayerSkins.Jakob_Body", class'Texture'));
		Skins[1]=Texture(DynamicLoadObject("DemoPlayerSkins.Jakob_NewHead", class'Texture'));
	}
	else
	{
	    Divide(DesiredSkin, ".", temp, meshName);

		// This should make most of default built-in UT2004 player meshes work with
		// good textures.
		// TODO: allow to set also these two variables?
		Skins[0]=Texture(DynamicLoadObject("PlayerSkins."$meshName$"BodyA", class'Texture'));
		Skins[1]=Texture(DynamicLoadObject("PlayerSkins."$meshName$"HeadA", class'Texture'));
	}

	ResetPhysicsBasedAnim();
}

//overriding this because we need to export also the LAST DAM message that caused death!
function TakeDamage(int Damage, Pawn instigatedBy, Vector hitlocation, Vector momentum, class<DamageType> damageType)
{
	local int actualDamage;
	local Controller Killer;

	if ( damagetype == None )
	{
		if ( InstigatedBy != None )
			warn("No damagetype for damage by "$instigatedby$" with weapon "$InstigatedBy.Weapon);
		DamageType = class'DamageType';
	}

	if ( Role < ROLE_Authority )
	{
		log(self$" client damage type "$damageType$" by "$instigatedBy);
		return;
	}

	if ( Health <= 0 )
		return;

	if ((instigatedBy == None || instigatedBy.Controller == None) && DamageType.default.bDelayedDamage && DelayedDamageInstigatorController != None)
		instigatedBy = DelayedDamageInstigatorController.Pawn;

	if ( (Physics == PHYS_None) && (DrivenVehicle == None) )
		SetMovementPhysics();
	if (Physics == PHYS_Walking && damageType.default.bExtraMomentumZ)
		momentum.Z = FMax(momentum.Z, 0.4 * VSize(momentum));
	if ( instigatedBy == self )
		momentum *= 0.6;
	momentum = momentum/Mass;

	if (Weapon != None)
		Weapon.AdjustPlayerDamage( Damage, InstigatedBy, HitLocation, Momentum, DamageType );
	if (DrivenVehicle != None)
        	DrivenVehicle.AdjustDriverDamage( Damage, InstigatedBy, HitLocation, Momentum, DamageType );
	if ( (InstigatedBy != None) && InstigatedBy.HasUDamage() )
		Damage *= 2;
	actualDamage = Level.Game.ReduceDamage(Damage, self, instigatedBy, HitLocation, Momentum, DamageType);
	if( DamageType.default.bArmorStops && (actualDamage > 0) )
		actualDamage = ShieldAbsorb(actualDamage);

	Health -= actualDamage;
	if ( HitLocation == vect(0,0,0) )
		HitLocation = Location;

	PlayHit(actualDamage,InstigatedBy, hitLocation, damageType, Momentum);
	if ( Health <= 0 )
	{
		// pawn died
		if ( DamageType.default.bCausedByWorld && (instigatedBy == None || instigatedBy == self) && LastHitBy != None )
			Killer = LastHitBy;
		else if ( instigatedBy != None )
			Killer = instigatedBy.GetKillerController();
		if ( Killer == None && DamageType.Default.bDelayedDamage )
			Killer = DelayedDamageInstigatorController;
		if ( bPhysicsAnimUpdate )
			TearOffMomentum = momentum;
		//only added following line here:
		Controller.NotifyTakeHit(instigatedBy, HitLocation, actualDamage, DamageType, Momentum);
		Died(Killer, damageType, HitLocation);
	}
	else
	{
		AddVelocity( momentum );
		if ( Controller != None )
			Controller.NotifyTakeHit(instigatedBy, HitLocation, actualDamage, DamageType, Momentum);
		if ( instigatedBy != None && instigatedBy != self )
			LastHitBy = instigatedBy.Controller;
	}
	MakeNoise(1.0);
}

//Overriding function in UnrealPawn
//Code here does the same thing and exports info about created items to RemoteBot
//class which exports it to the client
function CreateInventory(string InventoryClassName)
{
	local Inventory Inv;
	local Weapon Weap;
	local class<Inventory> InventoryClass;

	//native bots sets skins differently.
	if (Controller.IsA('GBxBot')) {
		super.CreateInventory(InventoryClassName);
		return;
	}

	InventoryClass = Level.Game.BaseMutator.GetInventoryClass(InventoryClassName);
	if( (InventoryClass!=None) && (FindInventoryType(InventoryClass)==None) )
	{
		Inv = Spawn(InventoryClass);
		if( Inv != None )
		{
			Inv.GiveTo(self);
			if ( Inv != None )
				Inv.PickupFunction(self);
		}
	}

	Weap = Weapon(Inv);
	if (Weap != none) {
		RemoteBot(Controller).HandleStartPickup(InventoryClass.default.PickupClass, Weap.AmmoAmount(0), Weap.AmmoAmount(1));
	} else {
		RemoteBot(Controller).HandleStartPickup(InventoryClass.default.PickupClass,,);
	}

}

simulated function SetTextBubble(string Id, string Msg, bool bGlobal, optional float FadeOut)
{
	bDrawTextBubble = true;
	SetTimer(FadeOut,false);
	if (Id == "")
		TextBubble = Msg;
	else
		TextBubble = "To "$Id$":"$Msg;
}

simulated function Timer()
{
	bDrawTextBubble = false;
}

simulated function SetLastGBCommand(string command)
{
	LastGBCommand = command;
}

simulated function SetLastGBPath(string path)
{
	LastGBPath = path;
}

simulated function SetSkin(string skin, optional xUtil.PlayerRecord rec)
{
	DesiredSkin = skin;
}

defaultproperties
{
    bSpecialCalcView=true
	bScriptPostRender=True
	TextBubble="Testing bubble"
}
