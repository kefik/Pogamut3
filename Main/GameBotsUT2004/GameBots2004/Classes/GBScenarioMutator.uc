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


//This class modifies game rules for BotScenario gametype
class GBScenarioMutator extends Mutator
	config(GameBots2004);

var config string StrongEnemyName;

var config float DefaultRespawnTime;
var config float AdrenalineRespawnTime;

var config int StrongStartHealth;
var config float StrongMaxHealth;
var config float StrongDamageScaling;
var config float StrongSpeedMultiplier;
var config bool StrongCanPickup;

var config int WeakStartHealth;
var config float WeakMaxHealth;
var config float WeakDamageScaling;
var config float WeakSpeedMultiplier;
var config bool WeakCanPickup;

var config float AdrenalineAmount;

var config int DefaultAmmoAmount;

var config int FlakInitAmmo;

var config int FlakMaxAmmo;
/*
function string GetInventoryClassOverride(string InventoryClassName)
{

}*/

/*
function Class<Weapon> GetDefaultWeapon()
{
	local Class<Weapon> W;

	if ( NextMutator != None )
	{
		W = NextMutator.GetDefaultWeapon();
		if ( W == None )
			W = MyDefaultWeapon();
	}
	else
		W = MyDefaultWeapon();
	return W;
}*/

function bool CheckReplacement(Actor Other, out byte bSuperRelevant)
{
	local class<Ammunition> currentAmmoClass;
	local RemoteBot bot;

	if (Other.IsA('Weapon'))
	{
		if (!Other.IsA('FlakCannon')) {
			ReplaceWith(Other, "xWeapons.FlakCannon");
			return false;
		} else {
			if (Weapon(Other).Owner != none && Weapon(Other).Owner.IsA('RemoteBot')) {
				bot = RemoteBot(Weapon(Other).Owner);
				if (bot.PlayerReplicationInfo != none && InStr(bot.PlayerReplicationInfo.PlayerName, StrongEnemyName) != -1) {
		 			currentAmmoClass = Weapon(Other).FireModeClass[0].Default.AmmoClass;
 					currentAmmoClass.Default.InitialAmount = FlakInitAmmo * 50;
					currentAmmoClass.Default.MaxAmmo = FlakMaxAmmo * 50;

					currentAmmoClass = Weapon(Other).FireModeClass[1].Default.AmmoClass;
		 			currentAmmoClass.Default.InitialAmount = FlakInitAmmo * 50;
					currentAmmoClass.Default.MaxAmmo = FlakMaxAmmo * 50;
		 			Weapon(Other).FireModeClass[1] = Weapon(Other).FireModeClass[0];
				} else {
					// modify flak cannon
	 				currentAmmoClass = Weapon(Other).FireModeClass[0].Default.AmmoClass;
 					currentAmmoClass.Default.InitialAmount = FlakInitAmmo;
					currentAmmoClass.Default.MaxAmmo = FlakMaxAmmo;

					currentAmmoClass = Weapon(Other).FireModeClass[1].Default.AmmoClass;
 					currentAmmoClass.Default.InitialAmount = FlakInitAmmo;
					currentAmmoClass.Default.MaxAmmo = FlakMaxAmmo;
 					Weapon(Other).FireModeClass[1] = Weapon(Other).FireModeClass[0];
				}
			} else {
				// modify flak cannon
 				currentAmmoClass = Weapon(Other).FireModeClass[0].Default.AmmoClass;
 				currentAmmoClass.Default.InitialAmount = FlakInitAmmo;
				currentAmmoClass.Default.MaxAmmo = FlakMaxAmmo;

				currentAmmoClass = Weapon(Other).FireModeClass[1].Default.AmmoClass;
 				currentAmmoClass.Default.InitialAmount = FlakInitAmmo;
				currentAmmoClass.Default.MaxAmmo = FlakMaxAmmo;

 				Weapon(Other).FireModeClass[1] = Weapon(Other).FireModeClass[0];
 			}
		}
	} else if (Other.IsA('Pickup')) {


		if ( Other.IsA('Ammo') )
		{
			if (!Other.IsA('FlakAmmoPickup')) {
				ReplaceWith(Other, "xWeapons.FlakAmmoPickup");
				return false;
			} else {
				Ammo(Other).AmmoAmount = DefaultAmmoAmount;
			}
		}

		if (Other.IsA('AdrenalinePickup')) {
			AdrenalinePickup(Other).AdrenalineAmount = AdrenalineAmount;
			AdrenalinePickup(Other).default.AdrenalineAmount = AdrenalineAmount;
			AdrenalinePickup(Other).RespawnTime = AdrenalineRespawnTime;
			AdrenalinePickup(Other).default.RespawnTime = AdrenalineRespawnTime;
		} else {
			Pickup(Other).RespawnTime = DefaultRespawnTime;
			Pickup(Other).default.RespawnTime = DefaultRespawnTime;
		}

		if (Other.IsA('WeaponPickup')){
			if (!Other.IsA('FlakCannonPickup')) {
 				ReplaceWith(Other, "xWeapons.FlakCannonPickup");
				if (WeaponPickup(Other).PickUpBase != none) {
					if (WeaponPickup(Other).PickUpBase.IsA('xWeaponBase')) {
						xWeaponBase(WeaponPickup(Other).PickUpBase).WeaponType = Weapon(DynamicLoadObject("xWeapons.FlakCannon",class'FlakCannon',True)).Class;
						xWeaponBase(WeaponPickup(Other).PickUpBase).default.WeaponType = Weapon(DynamicLoadObject("xWeapons.FlakCannon",class'FlakCannon',True)).Class;//class<Weapon>(DynamicLoadObject("xWeapons.FlakCannon",class'Class',True));
					}

					/*WeaponPickup(Other).PickUpBase.myPickUp = WeaponPickup(Other);
					WeaponPickup(Other).PickUpBase.default.myPickUp = WeaponPickup(Other);
					WeaponPickup(Other).PickUpBase.PowerUp = WeaponPickup(Other).Class;
					WeaponPickup(Other).PickUpBase.default.PowerUp = WeaponPickup(Other).Class;*/
				}
				return false;
			} else {
				if (WeaponPickup(Other).PickUpBase != none) {
					if (WeaponPickup(Other).PickUpBase.IsA('xWeaponBase')) {
						xWeaponBase(WeaponPickup(Other).PickUpBase).WeaponType = Weapon(DynamicLoadObject("xWeapons.FlakCannon",class'FlakCannon',True)).Class;
						xWeaponBase(WeaponPickup(Other).PickUpBase).default.WeaponType = Weapon(DynamicLoadObject("xWeapons.FlakCannon",class'FlakCannon',True)).Class;
					}
					/*WeaponPickup(Other).PickUpBase.myPickUp = WeaponPickup(Other);
					WeaponPickup(Other).PickUpBase.default.myPickUp = WeaponPickup(Other);
					WeaponPickup(Other).PickUpBase.PowerUp = WeaponPickup(Other).Class;
					WeaponPickup(Other).PickUpBase.default.PowerUp = WeaponPickup(Other).Class;*/
				}
				//Pickup(Other).RespawnTime = DefaultRespawnTime;
				//Pickup(Other).default.RespawnTime = DefaultRespawnTime;
			}
		} else if ( Other.IsA('MiniHealthPack') )
		{
			//TODO
		}
		else if ( Other.IsA('HealthPack') )
		{
			//TODO
		}
		else if ( Other.IsA('SuperHealthPack'))
		{
			ReplaceWith(Other, "xPickups.AdrenalinePickup");
			return false;
		}
		else if ( Other.IsA('ShieldPack'))
		{
			ReplaceWith(Other, "xPickups.AdrenalinePickup");
			return false;
		}
		else if ( Other.IsA('SuperShieldPack') )
		{
			ReplaceWith(Other, "xPickups.AdrenalinePickup");
			return false;
		}
	}
	return true;
}

function ModifyPlayer(Pawn Other)
{
	local xPawn P;
	P = xPawn(Other);

	if (P != none && P.Controller != none && P.PlayerReplicationInfo != none) {

		if (InStr(P.PlayerReplicationInfo.PlayerName, StrongEnemyName) != -1) {
			P.Health = StrongStartHealth;
			P.Default.Health = StrongStartHealth;

			P.HealthMax = StrongStartHealth;
			P.Default.HealthMax = StrongStartHealth;

			P.SuperHealthMax = StrongMaxHealth;
			P.Default.SuperHealthMax = StrongMaxHealth;

			P.DamageScaling = StrongDamageScaling;
			P.Default.DamageScaling = StrongDamageScaling;

			P.GroundSpeed = 440 * StrongSpeedMultiplier;
			P.Default.GroundSpeed = 440 * StrongSpeedMultiplier;

			P.WaterSpeed = 220 * StrongSpeedMultiplier;
			P.Default.WaterSpeed = 220 * StrongSpeedMultiplier;

			P.AirSpeed = 440 * StrongSpeedMultiplier;
			P.Default.AirSpeed = 440 * StrongSpeedMultiplier;

			P.LadderSpeed = 200 * StrongSpeedMultiplier;
			P.Default.LadderSpeed = 200 * StrongSpeedMultiplier;

			P.AccelRate = 2048 * StrongSpeedMultiplier;
			P.Default.AccelRate = 2048 * StrongSpeedMultiplier;

			P.bCanPickupInventory = StrongCanPickup;
			P.Default.bCanPickupInventory = StrongCanPickup;
		} else {
			P.Health = WeakStartHealth;
			P.Default.Health = WeakStartHealth;

			P.HealthMax = WeakStartHealth;
			P.Default.HealthMax = WeakStartHealth;

			P.SuperHealthMax = WeakMaxHealth;
			P.Default.SuperHealthMax = WeakMaxHealth;

			P.DamageScaling = WeakDamageScaling;
			P.Default.DamageScaling = WeakDamageScaling;

			P.GroundSpeed = 440 * WeakSpeedMultiplier;
			P.Default.GroundSpeed = 440 * WeakSpeedMultiplier;

			P.WaterSpeed = 220 * WeakSpeedMultiplier;
			P.Default.WaterSpeed = 220 * WeakSpeedMultiplier;

			P.AirSpeed = 440 * WeakSpeedMultiplier;
			P.Default.AirSpeed = 440 * WeakSpeedMultiplier;

			P.LadderSpeed = 200 * WeakSpeedMultiplier;
			P.Default.LadderSpeed = 200 * WeakSpeedMultiplier;

			P.AccelRate = 2048 * WeakSpeedMultiplier;
			P.Default.AccelRate = 2048 * WeakSpeedMultiplier;

			P.bCanPickupInventory = WeakCanPickup;
			P.Default.bCanPickupInventory = WeakCanPickup;
		}
	}

	if (NextMutator != None)
		NextMutator.ModifyPlayer(Other);
}

DefaultProperties
{
	bAddToServerPackages=True
	//ConfigMenuClassName=""
	GroupName="GBScenarioMutator"
	FriendlyName="GBScenarioMutator"
	Description="GameBots2004 scenario mutator, that modifies UT2004 rules.||v1.0"

	StrongEnemyName="emohawk"

	DefaultRespawnTime=100
	AdrenalineRespawnTime=100

	StrongStartHealth=150
	StrongMaxHealth=300
	StrongDamageScaling=0.5
	StrongSpeedMultiplier=0.8
	StrongCanPickup=false

	WeakStartHealth=50
	WeakMaxHealth=100
	WeakDamageScaling=2
	WeakSpeedMultiplier=1
	WeakCanPickup=true

	AdrenalineAmount=1
	DefaultAmmoAmount=1
	FlakInitAmmo=1
	FlakMaxAmmo=2
}
