/*

Gamebots UT Copyright (c) 2002, Andrew N. Marshal, Gal Kaminka
GameBots2004 - Pogamut3 derivation Copyright (c) 2010, Michal Bida, Josef Jirasek

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

   * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This software must also be in compliance with the Epic Games Inc. license for mods which states the following: "Your mods must be distributed solely for free, period. Neither you, nor any other person or party, may sell them to anyone, commercially exploit them in any way, or charge anyone for receiving or using them without prior written consent of Epic Games Inc. You may exchange them at no charge among other end-users and distribute them to others over the Internet, on magazine cover disks, or otherwise for free." Please see http://www.epicgames.com/ut2k4_eula.html for more information.

*/
//-----------------------------------------------------------
//
//-----------------------------------------------------------
class Observer extends PlayerListener;

// The connection to use.
var ObservingConnection myConnection;

var array<Mover> movers;

event Destroyed() {
	super.Destroyed();
}

function PostBeginPlay() {
	local Mover M;

	myConnection = ObservingConnection(Owner);

	foreach AllActors(class'Mover',M) {
		movers[movers.Length] = M;
    }
}

/*******************
* Helper functions *
*******************/

// True if location closer than 100 UT units
// True if location loc is in MyController's field of view. Does not take into account occlusion by geometry!
// Possible optimization: Precompute cos(obsController.FovAngle / 2) for InFOV - careful if it can change.
function bool InFOV(vector loc) {
	local vector view;   // vector pointing in the direction obsController is looking.
	local vector target; // vector from obsController's position to the target location.

	view = vector(MyController.GetViewRotation());

	if (MyController.Pawn != none) {
		target = loc - (MyController.Pawn.Location + MyController.Pawn.EyePosition());
	} else {
		target = loc - MyController.Location;
	}
	
	if (VSize(target) < 300) return true;

	return Acos(Normal(view) dot Normal(target)) * 57.2957795 < MyController.FovAngle / 2; // Angle between view and target is less than FOV
	// 57.2957795 = 180/pi = 1 radian in degrees  --  convert from radians to degrees
}

// True if the observed player can see the given actor.
function bool Visible(Actor actor) {
	if (actor == none)
		return false;
	if (actor.IsA('Pawn')) {
		// We can use more precise method CanSee only for Pawns.
		return InFOV(actor.Location) && MyController.CanSee(Pawn(actor));
	} else {
		return InFOV(actor.Location) && MyController.LineOfSightTo(actor);
	}
}

function vector MyLocation() {
	if (MyController.Pawn != none) {
		return MyController.Pawn.Location;
	} else {
		return MyController.Location;
	}
}

/***********************
* Synchronous messages *
***********************/

function ExportSelf(bool sync) {
	if (MyController == none || MyController.Pawn == none) return;
	SendSLF();
	if (sync) myconnection.SendSyncMessage("SMYINV");
	SendMYINV();
	if (sync) myconnection.SendSyncMessage("EMYINV");
}

function ExportSee(bool sync) {
	if (MyController == none || MyController.Pawn == none) return;
	if (sync) myconnection.SendSyncMessage("SPLR");
	SendPLR();
	if (sync) myconnection.SendSyncMessage("EPLR");
	if (sync) myconnection.SendSyncMessage("SINV");
	SendINV();
	if (sync) myconnection.SendSyncMessage("EINV");
	if (sync) myconnection.SendSyncMessage("SNAV");
	SendNAV();
	if (sync) myconnection.SendSyncMessage("ENAV");
	if (sync) myconnection.SendSyncMessage("SMOV");
	SendMOV();
	if (sync) myconnection.SendSyncMessage("EMOV");
	if (sync) myconnection.SendSyncMessage("SPRJ");
	SendPRJ();
	if (sync) myconnection.SendSyncMessage("EPRJ");
	if (sync) myconnection.SendSyncMessage("SVEH");
	SendVEH();
	if (sync) myconnection.SendSyncMessage("EVEH");
}

function ExportSpecial(bool sync) {
	if (MyController == none || MyController.Pawn == none) return;
	if (sync) myconnection.SendSyncMessage("SFLG");
	SendFLG();
	if (sync) myconnection.SendSyncMessage("EFLG");
	if (sync) myconnection.SendSyncMessage("SBOM");
	SendBOM();
	if (sync) myconnection.SendSyncMessage("EBOM");
	if (sync) myconnection.SendSyncMessage("SDOM");
	SendDOM();
	if (sync) myconnection.SendSyncMessage("EDOM");
}

function SendBOM() {
	local string outstring;
	local xBombFlag B;

	if (Level.Game.IsA('BotBombingRun')) {
		//TODO: Move this allActors search to some different place,
		// Not to do it that often.
		foreach AllActors (class'xBombFlag', B)	{
        	outstring = "BOM {Id "  $ B $
				//"} {Reachable " $ actorReachable(B) $
				"} {State " $ B.GetStateName() $
				"}";

			//bomb is NOT updated when it is held - we need to use holder to query info about location
			//and visibility!
			if(B.IsInState('Held') && B.Holder != none) {
      			if (Visible(B.Holder)) {
			    	outstring = outstring $" {Visible True} {Location " $ B.Holder.Location $
    			        "} {Holder " $ myConnection.GetUniqueId(B.Holder) $"}";
			  	} else if (B.Holder == MyController.Pawn) { //our pawn is holder
        			outstring = outstring $" {Visible True" $
            			"} {Location " $ B.Holder.Location $
		            	"} {Holder " $ myConnection.GetUniqueId(B.Holder) $"}";
        		} else {
		        	outstring = outstring $" {Visible False}";
				}
			} else {
				if (Visible(B)) {
		        	outstring = outstring $" {Visible True} {Location " $ B.Location $
	    		        "} {Holder None}";
				} else {
        			outstring = outstring $" {Visible False}";
				}
			}
			myConnection.SendSyncMessage(outstring);
		}
	}
}

function SendDOM() {
	local NavigationPoint nav;
	local int TeamIndex;

	if (Level.Game.IsA('BotDoubleDomination')) {
		for (nav = Level.NavigationPointList; nav != none; nav = nav.nextNavigationPoint) {
			if (nav.IsA('xDomPoint') && Visible(nav)) {

				if(xDomPoint(nav).ControllingTeam != none) {
					TeamIndex = xDomPoint(nav).Controllingteam.TeamIndex;
				} else {
					TeamIndex = 255;
				}

				myConnection.SendSyncMessage("DOM {Id " $ myConnection.GetUniqueId(nav)
					$ "} {Location " $ nav.Location
					$ "} {Visible True"
					$ "} {Controller " $ TeamIndex
					$ "}");
			}
		}
	}
}

function SendFLG() {
	local string outstring;
	local CTFFlag F;

	//CTF Game support
	if (Level.Game.IsA('BotCTFGame')) {
		//TODO: Move this allActors search to some different place,
		// Not to do it that often.
		foreach AllActors (class'CTFFlag', F) {

			outstring = "FLG {Id " $ F $
				"} {Team " $ F.Team.TeamIndex $
			//	"} {Reachable " $ actorReachable(F) $
				"} {State " $ F.GetStateName() $
				"}";

			//flag is NOT updated when it is held - we need to use holder to query info about location
			//and visibility!
			if(F.IsInState('Held') && F.Holder != none) {
      			if (Visible(F.Holder)) {
			    	outstring = outstring $" {Visible True} {Location " $ F.Holder.Location $
    			        "} {Holder " $ myConnection.GetUniqueId(F.Holder) $"}";
			  	} else if (F.Holder == MyController.Pawn) { //our pawn is holder
        			outstring = outstring $" {Visible True" $
            			"} {Location " $ F.Holder.Location $
		            	"} {Holder " $ myConnection.GetUniqueId(F.Holder) $"}";
        		} else {
		        	outstring = outstring $" {Visible False}";
				}
			} else {
				if (Visible(F)) {
		        	outstring = outstring $" {Visible True} {Location " $ F.Location $
	    		        "} {Holder None}";
				} else {
        			outstring = outstring $" {Visible False}";
				}
			}

			myConnection.SendSyncMessage(outstring);
		}
	} // end if (Level.Game.IsA('BotCTFGame'))
}

function SendINV() {
	local Pickup pickup;

	foreach AllActors(class'Pickup', pickup)	{
		if (pickup.ReadyToPickup(0) && Visible(pickup)) {
			myConnection.SendSyncMessage("INV {Id " $ myConnection.GetUniqueId(pickup)
				$ "} {Location " $ pickup.Location
				$ "} {Amount " $ myConnection.GetItemAmount(pickup)
				$ "} {Visible True"
				$ "} {Dropped " $ pickup.bDropped
				$ "} {Type " $ pickup.Class
				$ "}");
		}
	}
}

function SendMOV() {
	local int i;
	local Mover m;

	for (i = 0; i < movers.Length; ++i) {
		m = movers[i];
		if (Visible(m)) {
			myConnection.SendSyncMessage("MOV {Id " $ m
				$ "} {Location " $ m.Location
				$ "} {Visible True"
				$ "} {DamageTrig " $ m.bDamageTriggered
				$ "} {Type " $ m.Class
				$ "} {IsMoving " $ m.bInterpolating
				$ "} {Velocity " $ m.Velocity
				$ "} {MoveTime " $ m.MoveTime
				$ "} {OpenTime " $ m.StayOpenTime
				$ "} {State " $ m.GetStateName()
				$ "}");
		}
	}
}

function SendMYINV() {
	local Inventory inv;
	local string message;

	if (MyController.Pawn == none) {
	   // Observing a spectator, no inventory.
	   return;
	} else {
		// Obseving a player with a Pawn.
		for (inv = MyController.Pawn.Inventory; inv != none; inv = inv.Inventory) {
			message = "MYINV {Id " $ myConnection.GetUniqueId(inv)
				$ "} {Type " $ inv.Class
				$ "}";

			if (inv.IsA('Weapon')) {
				message $= " {CurrentAmmo " $ Weapon(inv).AmmoAmount(0)
					$ "} {CurrentAltAmmo " $ Weapon(inv).AmmoAmount(1)
					$ "}";
			}

			if (inv.IsA('Ammunition')) {
				message $= " {Amount " $ Ammunition(inv).AmmoAmount $ "}";
			}

			if (inv.IsA('Armor')) {
				message $= " {Amount " $ Armor(inv).Charge $ "}";
			}

			myConnection.SendSyncMessage(message);
		}
	}
}

function SendNAV() {
	local string outstring;
	local int temp, i, visionRadius;
	local array<InventorySpot> InvSpotArray;
	local array<LiftCenter> LiftCenterArray;
	local array<xDomPoint> DomPointArray;
	local xDomPoint D;
	local InventorySpot IS;
	local LiftCenter DR;

	InvSpotArray = BotDeathMatch(Level.Game).InvSpotArray;
	LiftCenterArray = BotDeathMatch(Level.Game).LiftCenterArray;
	DomPointArray = BotDeathMatch(Level.Game).xDomPointArray;

	if (MyController.IsA('RemoteBot'))
		visionRadius = RemoteBot(MyController).visionRadius;
	else
		visionRadius = 5000;

		for (i = 0; i < DomPointArray.Length; i++) {
			D = DomPointArray[i];
			if( D.ControllingTeam == none )
				temp = 255;
			else
				temp = D.ControllingTeam.TeamIndex;

			outstring = "NAV {Id " $ D $
				//"} {Location " $ D.Location $
				"} {Visible " $ InFOV( D.Location ) $
				//"} {Reachable " $ myActorReachable(N) $
				"} {DomPoint True" $
				"} {DomPointController " $ temp $
				"}";
    		myConnection.SendSyncMessage(outstring);

		}
		for (i = 0; i < InvSpotArray.Length; i++) {
			IS = InvSpotArray[i];
			if ((IS.markedItem != none) && (VSize(MyLocation() - IS.Location) <= visionRadius) && ((VSize(MyLocation() - IS.Location) <= 120) || Visible(IS)) ) {
				outstring = "NAV {Id " $ IS $
					//"} {Location " $ IS.Location $
					"} {Visible True" $
					//"} {Reachable " $ myActorReachable(IS) $
					"} {ItemSpawned " $ IS.markedItem.IsInState('Pickup') $ "}";
				myConnection.SendSyncMessage(outstring);
			}
		}

		for (i = 0; i < LiftCenterArray.Length; i++) {
			DR = LiftCenterArray[i];
			if( (VSize(MyLocation() - DR.Location) <= visionRadius) && ((VSize(MyLocation() - DR.Location) <= 500) || Visible(DR))) {
				outstring = "NAV {Id " $ DR $
					"} {Location " $ DR.Location $
					"} {Visible True" $
					//"} {Reachable " $ myActorReachable(DR) $
				    "}";
				myConnection.SendSyncMessage(outstring);
			}
		}


}

function SendPLR() {
	local Controller c;
	local Pawn p;
	local int TeamIndex, firing;
	local rotator PawnRotation;

	for (c = Level.ControllerList; c != none; c = c.nextController) {
		if (c != MyController && c.Pawn != none && Visible(c.Pawn)) {
			p = c.Pawn;

			if(Level.Game.isA('BotTeamGame')) {
				TeamIndex = c.GetTeamNum();
			} else {
				TeamIndex = 255;
			}

			if (p.Weapon != none) {
				if (p.Weapon.GetFireMode(0).IsFiring()) {
					firing = 1; // Primary fire.
				} else if (p.Weapon.GetFireMode(1).IsFiring()) {
					firing = 2; // Alternate fire.
				} else {
					firing = 0; // Not firing.
				}
			}

			PawnRotation = p.Rotation;
			PawnRotation.Pitch = int(p.ViewPitch) * 65535/255;

			myConnection.SendSyncMessage("PLR {Id " $ myConnection.GetUniqueId(c)
			  $ "} {Spectator false" // TODO 
				$ "} {Rotation " $ PawnRotation
				$ "} {Location " $ p.Location
				$ "} {Velocity " $ p.Velocity
				$ "} {Name " $ c.GetHumanReadableName()
				$ "} {Team " $ TeamIndex
				$ "} {Crouched " $ p.bIsCrouched
				$ "} {Weapon " $ p.Weapon
				$ "} {Firing " $ firing
				$ "}");
		}
	}
}

function SendPRJ() {
	local Projectile Proj;
	local vector FireDir;

	foreach DynamicActors(class'Projectile',Proj) {
		if (Proj.IsA('FlakChunk')) {
			FireDir = Normal(Proj.Velocity);
		} else {
			FireDir = vector(Proj.Rotation);
		}

		if (Visible(proj)) {
			myConnection.SendAsyncMessage("PRJ {Id " $ myConnection.GetUniqueId(Proj)
				$ "} {ImpactTime " $ (VSize(MyController.Pawn.Location - Proj.Location) / Proj.Speed)
				$ "} {Direction " $ FireDir
				$ "} {Speed " $ Proj.Speed
				$ "} {Velocity " $ Proj.Velocity
				$ "} {Location " $ Proj.Location
				$ "} {Origin " $ Proj.Instigator.Location
				$ "} {DamageRadius " $ Proj.DamageRadius
				$ "} {Type " $ Proj.Class
				$ "}");
		}
	}
}

function SendSLF() {
	local Pawn p;
	local string message;
	local int teamIndex;
	local vector FloorLocation, FloorNormal;
	local rotator PawnRotation;

	if(Level.Game.isA('BotTeamGame')) {
		teamIndex = MyController.GetTeamNum();
	} else {
		teamIndex = 255; // No team.
	}

	if (MyController.Pawn == none) {
		// Observing a spectator.
		message = "SLF {Id SELF_" $ myConnection.GetUniqueId(self)
			$ "} {BotId " $ myConnection.GetUniqueId(MyController)
			$ "} {Rotation " $ MyController.GetViewRotation()
			$ "} {Location " $ MyController.Location
			$ "} {Velocity " $ MyController.Velocity
			$ "} {Name " $ MyController.GetHumanReadableName()
			$ "} {Team " $ teamIndex
			$ "}";
	} else {
		// Observing a player with a Pawn.
		p = MyController.Pawn;
		FloorNormal = vect(0,0,0);
		Trace(FloorLocation, FloorNormal, p.Location + vect(0,0,-1000), p.Location, false,,);
		PawnRotation = p.Rotation;
		PawnRotation.Pitch = int(p.ViewPitch) * 65535/255;

		message = "SLF {Id SELF_" $ myConnection.GetUniqueId(self) $
			"} {BotId " $ myConnection.GetUniqueId(MyController) $
			"} {Vehicle False" $
			"} {Rotation " $ PawnRotation $
			"} {Location " $ p.Location $
			"} {Velocity " $ p.Velocity $
			"} {Name " $ MyController.PlayerReplicationInfo.PlayerName $
			"} {Team " $ teamIndex $
			"} {Health " $ p.Health $
			"} {Weapon " $ p.Weapon $
			"} {Armor " $ int(p.GetShieldStrength()) $
			"} {SmallArmor " $ int(xPawn(p).SmallShieldStrength) $
			"} {Adrenaline " $ int(MyController.Adrenaline) $
			"} {Crouched " $ p.bIsCrouched $
			"} {Walking " $ p.bIsWalking $
			"} {FloorLocation " $ FloorLocation $
			"} {FloorNormal " $ FloorNormal $
			"} {Combo " $ xPawn(p).CurrentCombo $
			"} {UDamageTime " $ xPawn(p).UDamageTime $
			"}";

		if (p.Weapon != None) {
			message $= " {Shooting " $ p.Weapon.IsFiring()
				$ "} {PrimaryAmmo " $ p.Weapon.AmmoAmount(0)
				$ "} {SecondaryAmmo " $ p.Weapon.AmmoAmount(1)
				$ "}";

			if(p.Weapon.GetFireMode(1).IsFiring()) {
				message $= " {AltFiring True}";
			} else {
				message $= " {AltFiring False}";
			}
		}
	}

	myConnection.SendSyncMessage(message);
}

function SendVEH() {
	local Vehicle Veh;

	foreach DynamicActors(Class'Vehicle', Veh) {
		if (Visible(Veh)){
		     myConnection.SendAsyncMessage("VEH {Id " $ myConnection.GetUniqueId(Veh) $
				"} {Location " $ Veh.Location $
				"} {Rotation " $ Veh.Rotation $
				"} {Velocity " $ Veh.Velocity $
				"} {Health " $ Veh.Health $
				"} {Armor " $ int(Veh.ShieldStrength) $
				"} {Driver " $ myConnection.getUniqueId(Veh.Controller) $
				"} {Team " $ Veh.Team $
				"} {TeamLocked " $ Veh.bTeamLocked $
				"} {Type " $ Veh.Class $
				"}");
		}
	}
}

/***************************************************************
* Implementing PlayerListener - sending asynchronous messages. *
***************************************************************/

event AdrenalineGained(float Amount) {
	myConnection.SendAsyncMessage("ADG {Amount " $ int(Amount) $ "}");
}

//normally this is sync message, but for observers its exported asynchronously
event AutoTraceRayResult(
	string Id,
	vector from,
	vector to,
	bool fastTrace,
	bool ProvideFloorCorrection,
	bool result,
	vector hitNormal,
	vector hitLocation,
	bool traceActors,
	string hitId)
{
	myConnection.SendAsyncMessage("ATR {Id " $ Id $
			"} {From " $ from $
			"} {To " $ to $
			"} {FastTrace " $ fastTrace $
			"} {FloorCorrection " $ ProvideFloorCorrection $
			"} {Result " $ result $
			"} {HitNormal " $ hitNormal $
			"} {HitLocation " $ hitLocation $
			"} {TraceActors " $ traceActors $
			"} {HitId " $ hitId $
			"}");
}

event BumpedActor(Actor actor) {
	myConnection.SendAsyncMessage("BMP {Id " $ myConnection.GetUniqueId(actor)
		$ "} {Location " $ actor.Location
		$ "}");
}

event BumpedWall(Actor wall, vector hitNormal) {
	local vector loc;

	if (MyController.Pawn != none) {
		loc = MyController.Pawn.Location;
	} else {
		loc = MyController.Location;
	}

	myConnection.SendAsyncMessage("WAL {Id " $ myConnection.GetUniqueId(wall)
		$ "} {Normal " $ hitNormal
		$ "} {Location " $ loc
		$ "}");
}

event ChangedWeapon(Weapon oldWeapon, Weapon newWeapon) {
	myConnection.SendAsyncMessage("CWP {Id " $ myConnection.GetUniqueId(newWeapon)
		$ "} {PrimaryAmmo " $ newWeapon.AmmoAmount(0)
		$ "} {SecondaryAmmo " $ newWeapon.AmmoAmount(1)
		$ "} {Type " $ newWeapon.Class
		$ "}");
}

event Combo(class<Combo> comboClass) {
	myConnection.SendAsyncMessage("COMBO {Type " $ comboClass $ "}");
}

event ControllerDestroyed() {
	RemoveListener();
	myConnection.ChildDied();
}

event Died(Controller killer, class<DamageType> damageType) {
	local string message;

	message = "DIE {Killer " $ myConnection.GetUniqueId(Killer)
		$ "} {DamageType " $ damageType
		$ "} {DeathString " $ damageType.default.DeathString
		$ "} {Flaming " $ damageType.default.bFlaming
		$ "} {CausedByWorld " $ damageType.default.bCausedByWorld
		$ "} {DirectDamage " $ damageType.default.bDirectDamage
		$ "} {BulletHit " $ damageType.default.bBulletHit
		$ "} {VehicleHit " $ damageType.default.bVehicleHit
		$ "}";

	if (damageType.default.DamageWeaponName != "") {
		message $= " {WeaponName " $ damageType.default.DamageWeaponName $ "}";
	}

	myConnection.SendAsyncMessage(message);
}

event DroppedWeapon() {
	myConnection.SendAsyncMessage("THROWN");
}

event GotHit(Pawn shooter, int damage, class<DamageType> damageType, vector hitLocation, vector hitMomentum) {
	local string message;

	message = "DAM {Damage " $ damage
		$ "} {DamageType " $ damageType
		$ "} {Flaming " $ damageType.default.bFlaming
		$ "} {CausedByWorld " $ damageType.default.bCausedByWorld
		$ "} {DirectDamage " $ damageType.default.bDirectDamage
		$ "} {BulletHit " $ damageType.default.bBulletHit
		$ "} {VehicleHit " $ damageType.default.bVehicleHit
		$ "}";

	if (damageType.default.DamageWeaponName != "") {
		message $= " {WeaponName " $ damageType.default.DamageWeaponName $ "}";
	}

	if (Visible(shooter)) {
		message $= " {Instigator " $ myConnection.GetUniqueId(shooter) $ "}";
	}

	myConnection.SendAsyncMessage(message);
}

event HearNoise(float Loudness, Actor noiseMaker) {
	myConnection.SendAsyncMessage("HRN {Source " $ myConnection.GetUniqueId(noiseMaker)
		$ "} {Type " $ noiseMaker.Class
		$ "} {Rotation " $ rotator(noiseMaker.Location)
		$ "}");
}

event HearSound(Actor actor, sound s, vector soundLocation) {
    local vector vec;

	if (actor == MyController) return;
	if (MyController.Pawn != none && actor == MyController.Pawn) return;

	if (actor == none) {
		vec = soundLocation - MyLocation();
	} else {
		vec = actor.Location - MyLocation();
	}

	if (VSize(vec) <= s.BaseRadius) { // Otherwise probably sound out of range?
    	if (actor != none && actor.IsA('Pickup')) {
    		myConnection.SendAsyncMessage("HRP {Source " $ myConnection.GetUniqueId(actor)
				$ "} {Type " $ actor.Class
				$ "} {Rotation " $ rotator(vec)
				$ "}");

    	} else if (actor != none) {
    		myConnection.SendAsyncMessage("HRN {Source " $ myConnection.GetUniqueId(actor)
				$ "} {Type " $ actor.Class
				$ "} {Rotation " $ rotator(vec)
				$ "}");

    	} else {
    		myConnection.SendAsyncMessage("HRN {Source None"
				$ "} {Type None"
				$ "} {Rotation " $ rotator(vec)
				$ "}");
    	}
    }
}

event Hit(Controller target, int damage, class<DamageType> damageType) {
	myConnection.SendAsyncMessage("HIT {Id " $ myConnection.GetUniqueId(target) $
		"} {Damage " $ damage $
        "} {DamageType " $ damageType $
        "} {WeaponName " $ damageType.default.DamageWeaponName $
        "} {Flaming " $ damageType.default.bFlaming $
        "} {DirectDamage " $ damageType.default.bDirectDamage $
        "} {BulletHit " $ damageType.default.bBulletHit $
        "} {VehicleHit " $ damageType.default.bVehicleHit $
		"}");
}

event IncomingProjectile(Projectile projectile, vector direction, Pawn shooter) {
	if (MyController.Pawn != none && MyController.Pawn.Health > 0) { // No need to warn observers or dead people.
		if (Visible(projectile)) {
			/*myConnection.SendAsyncMessage("PRJ {Id " $ myConnection.GetUniqueId(projectile) $
				"} {Time " $ (VSize(MyController.Pawn.Location - projectile.Location) / projectile.Speed) $
				"} {Direction " $ direction $
				"} {Speed " $ projectile.Speed $
				"} {Velocity " $ projectile.Velocity $
				"} {Location " $ projectile.Location $
				"} {Origin " $ projectile.Instigator.Location $
				"} {DamageRadius " $ projectile.DamageRadius $
				"} {Type " $ projectile.Class $
				"}");*/
		}
	}
}

event InventoryAdded(Inventory inv) {
	local string message;
	local Weapon wep;

	message = "AIN {Id " $ myConnection.GetUniqueId(inv)
		$ "} {Type " $ inv.class
		$ "} {PickupType " $ inv.PickupClass
		$ "}";

	if (inv.IsA('Weapon')) {
		wep = Weapon(inv);

		message $= " {Sniping " $ wep.bSniping
			$ "} {Melee " $ wep.bMeleeWeapon
			$ "}";

		if (wep.AmmoClass[0] != none) {
			message $= " {PrimaryInitialAmmo " $ wep.AmmoClass[0].default.InitialAmount
				$ "} {MaxPrimaryAmmo " $ wep.AmmoClass[0].default.MaxAmmo
				$ "}";
		}

		if (wep.AmmoClass[1] != none) {
			message $= " {SecondaryInitialAmmo " $ wep.AmmoClass[1].default.InitialAmount
				$ "} {MaxSecondaryAmmo " $ wep.AmmoClass[1].default.MaxAmmo
				$ "}";
		}
	}
	myConnection.SendAsyncMessage(message);
}

event Jump(bool doubleJump, float delay, float force) {
	myConnection.SendAsyncMessage("JUMP {DoubleJump " $ doubleJump $ "}");
}

event Landed(vector hitnormal) {
	myConnection.SendAsyncMessage("LAND {HitNormal " $ HitNormal $ "}");
}

event NotifyControlMessage(string type, string ps1, string ps2, string ps3, string pi1, string pi2, string pi3, string pf1, string pf2, string pf3, string pb1, string pb2, string pb3) {
	local string outstring;

	outstring = "CTRLMSG {Type " $ type $ "}";
	if (ps1 != "")
		outstring $= " {PS1 " $ ps1 $ "}";
	if (ps2 != "")
		outstring $= " {PS2 " $ ps2 $ "}";
	if (ps3 != "")
		outstring $= " {PS3 " $ ps3 $ "}";
	if (pi1 != "")
		outstring $= " {PI1 " $ pi1 $ "}";
	if (pi2 != "")
		outstring $= " {PI2 " $ pi2 $ "}";
	if (pi3 != "")
		outstring $= " {PI3 " $ pi3 $ "}";
	if (pf1 != "")
		outstring $= " {PF1 " $ pf1 $ "}";
	if (pf2 != "")
		outstring $= " {PF2 " $ pf2 $ "}";
	if (pf3 != "")
		outstring $= " {PF3 " $ pf3 $ "}";
	if (pb1 != "")
		outstring $= " {PB1 " $ pb1 $ "}";
	if (pb2 != "")
		outstring $= " {PB2 " $ pb2 $ "}";
	if (pb3 != "")
		outstring $= " {PB3 " $ pb3 $ "}";
	myConnection.SendAsyncMessage(outstring);
}

event PickedUp(Pickup pickup) {
	local string InventoryId;

	InventoryId = string(MyController.Pawn.FindInventoryType(Pickup.InventoryType));

	myConnection.SendAsyncMessage("IPK {Id " $ myConnection.GetUniqueId(Pickup) $
		"} {InventoryId " $ InventoryId $
		"} {Location " $ pickup.Location $
		"} {Amount " $ myConnection.GetItemAmount(Pickup) $
		"} {Dropped " $ pickup.bDropped $
		"} {Type " $ pickup.Class $
		"}");
}

event PlayerDied(Controller killer, Controller killed, Pawn killedPawn, class<DamageType> damageType) {
	local string message;

	message = "KIL {Id " $ myConnection.GetUniqueId(killed) $
		"} {KilledPawn " $ myConnection.GetUniqueId(killedPawn) $
        "} {Killer " $ myConnection.GetUniqueId(killer) $
		"}";

	if (Visible(KilledPawn)) {
		message $= " {DamageType " $ damageType
			$ "} {DeathString " $ damageType.default.DeathString
			$ "} {Flaming " $ damageType.default.bFlaming
			$ "} {CausedByWorld " $ damageType.default.bCausedByWorld
			$ "} {DirectDamage " $ damageType.default.bDirectDamage
			$ "} {BulletHit " $ damageType.default.bBulletHit
			$ "} {VehicleHit " $ damageType.default.bVehicleHit
			$ "}";

		if (damageType.default.DamageWeaponName != "") {
			message $= " {WeaponName " $ damageType.default.DamageWeaponName $ "}";
		}
	}

	myConnection.SendAsyncMessage(message);
}

event PlayerJoined(Controller joined) {
	myConnection.SendAsyncMessage("JOIN {Id " $ myConnection.GetUniqueId(joined)
		$ "} {Name " $ joined.GetHumanReadableName()
		$ "}");
}

event PlayerKeyEvent(PlayerController PC, Interactions.EInputKey key, Interactions.EInputAction action) {
	local string outstring;

    outstring = "KEYEVENT {PlayerId " $ myConnection.GetUniqueId(PC) $
		"} {PlayerName " $ PC.PlayerReplicationInfo.PlayerName $
	    "} {Key " $ Mid( GetEnum(enum'EInputKey', key), 3 ) $
		"} {Action " $ Caps(Mid( GetEnum(enum'EInputAction', action), 4)) $ "}";

	if (PC.ViewTarget != none) {
		outstring = outstring $ " {ViewTarget " $ myConnection.GetUniqueId(PC.ViewTarget) $ "}";
	}

	myConnection.SendAsyncMessage(outstring);
}

event PlayerLeft(Controller left) {
	myConnection.SendAsyncMessage("LEFT {Id " $ myConnection.GetUniqueId(left)
		$ "} {Name " $ left.GetHumanReadableName()
		$ "}");
}

event ReceivedGlobalMessage(Actor sender, string message) {
	local string plName;

	message = Repl(message, "{", "_");
	message = Repl(message, "}", "_");

	if (sender.IsA('Controller')) {
		plName = Controller(sender).PlayerReplicationInfo.PlayerName;
	} else {
		plName = myConnection.GetUniqueId(sender);
	}

	myConnection.SendAsyncMessage("VMS {Id "$ myConnection.GetUniqueId(sender)
	 	$ "} {Name " $ plName
		$ "} {ControlServer " $ sender.IsA('ControlConnection')
		$ "} {Text " $ message
		$ "}");
}

event ReceivedTeamMessage(Actor sender, string message) {
	local string plName;

	message = Repl(message, "{", "_");
	message = Repl(message, "}", "_");

	if (sender.IsA('Controller')) {
		plName = Controller(sender).PlayerReplicationInfo.PlayerName;
	} else {
		plName = myConnection.GetUniqueId(sender);
	}

	myConnection.SendAsyncMessage("VMT {Id " $ myConnection.GetUniqueId(sender)
		$ "} {Name " $ plName
		$ "} {ControlServer " $ sender.IsA('ControlConnection')
		$ "} {Text " $ message
		$ "}");
}

event Respawned(vector location, rotator rotation) {
	myConnection.SendAsyncMessage("SPW");
}

event StartShooting(bool altShooting) {
	myConnection.SendAsyncMessage("SHOOT {Alt " $ altShooting $ "}");
}

event StopShooting(bool altShooting) {
	myConnection.SendAsyncMessage("STOPSHOOT {Alt " $ altShooting $ "}");
}

event Touched(Actor actor) {
	myConnection.SendAsyncMessage("TCH {Id " $ myConnection.GetUniqueId(actor)
		$ "} {Location " $ actor.Location
		$ "} {Rotation " $ actor.Rotation
		$ "}");
}

event Triggered(Actor trigger, Actor instigator) {
	myConnection.SendAsyncMessage("TRG {Actor " $ myConnection.GetUniqueId(trigger)
		$ "} {EventInstigator " $ myConnection.GetUniqueId(instigator)
		$ "}");
}

event VolumeChanged(PhysicsVolume NewVolume) {
	myConnection.sendAsyncMessage("VCH {Id " $ myConnection.GetUniqueId(NewVolume) $
		"} {ZoneVelocity " $ NewVolume.ZoneVelocity $  //vector
		"} {ZoneGravity " $ NewVolume.Gravity $  //vector
		"} {GroundFriction " $ NewVolume.GroundFriction $  //float
		"} {FluidFriction " $ NewVolume.FluidFriction $  //float
		"} {TerminalVelocity " $ NewVolume.TerminalVelocity $  //float
		"} {WaterVolume " $ NewVolume.bWaterVolume $
		"} {PainCausing " $ NewVolume.bPainCausing $
		"} {Destructive " $ NewVolume.bDestructive $
		"} {DamagePerSec " $ NewVolume.DamagePerSec $ //float
		"} {DamageType " $ NewVolume.DamageType $
		"} {NoInventory " $ NewVolume.bNoInventory $
		"} {MoveProjectiles " $ NewVolume.bMoveProjectiles $
		"} {NeutralZone " $ NewVolume.bNeutralZone $
		"}");
}

event WillFall(bool fell) {
	myConnection.SendAsyncMessage("FAL {Fell " $ fell
		$ "} {Location " $ MyController.Pawn.Location
		$ "}");
}

event ZoneChanged(ZoneInfo newZone) {
	myConnection.SendAsyncMessage("ZCB {Id " $ myConnection.GetUniqueId(newZone) $ "}");
}


defaultproperties {

}

