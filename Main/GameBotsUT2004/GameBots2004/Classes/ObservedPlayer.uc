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
// TODO: Jump(doubleJump = true)
//       Respawned
//-----------------------------------------------------------
class ObservedPlayer extends GBxPlayer;

var float LastBump; // Last time we sent a BMP message.
var float LastWallHit; // Last time we sent a WAL message.

var bool bIsFiring;
var bool bIsAltFiring;

var PlayerListener Listeners; // Linked list of PlayerListeners observing this player.

replication {
    // Functions server can call.
    reliable if (Role == ROLE_Authority)
		bIsFiring, bIsAltFiring;
    reliable if (Role < ROLE_Authority)
		NotifyFire, NotifyAltFire, NotifyStopFire, NotifyJump;//, NotifyClientHearSound;
}

function PostBeginPlay() {
	super.PostBeginPlay();
}

/****************************************************
* Hooks to trigger server functions from the client *
****************************************************/
/*
event ClientHearSound(actor Actor, int Id, sound S, vector SoundLocation, vector Parameters, bool Attenuate) {
	NotifyClientHearSound(Actor, Id, S, SoundLocation, Parameters, Attenuate);
	super.ClientHearSound(Actor, Id, S, SoundLocation, Parameters, Attenuate);
}*/

exec function AltFire(optional float F) {
	bIsAltFiring = true;
	NotifyAltFire(F);
	super.AltFire(F);
}

exec function Fire(optional float F) {
	bIsFiring = true;
	NotifyFire(F);
	super.Fire(F);
}

exec function Jump(optional float f) {
	super.Jump(F);
	NotifyJump(bDoubleJump);
}

event PlayerTick(float DeltaTime) {
	super.PlayerTick(DeltaTime);

	if (bIsFiring && bFire == 0) {
		bIsFiring = false;
		NotifyStopFire(false);
    }
    if (bIsAltFiring && bAltFire == 0) {
		bIsAltFiring = false;
		NotifyStopFire(true);
    }
}

/*****************************
* Listener support functions *
*****************************/

event Destroyed() {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.ControllerDestroyed();
	}
	super.Destroyed();
}

function AddListener(PlayerListener listener) {
	local PlayerListener l;

	if (listener.MyController != none) {
		log("Attempting to add a listener which is already listening.");
		return;
	}

	if (Listeners == none) {
		Listeners = listener;
	} else {
		for (l = Listeners; l.NextListener != none; l = l.NextListener) {}
		l.NextListener = listener;
	}
	listener.MyController = self;
	listener.Added();
}

function RemoveListener(PlayerListener listener) {
	local PlayerListener l;

	if (listener.MyController != self) {
		log("Attempting to remove a listener which is listening another Controller.");
		return;
	}

	if (Listeners == listener) {
		Listeners = listener.NextListener;
		listener.Removed();
		listener.MyController = none;
	} else {
		for (l = Listeners; l != none; l = l.NextListener) {
			if (l.NextListener == listener) {
				l.NextListener = listener.NextListener;
				listener.Removed();
				listener.MyController = none;
				break;
			}
		}
	}
}

/******************************
* Dispatching Listener events *
******************************/

function PlayerKeyEvent(Interactions.EInputKey key, Interactions.EInputAction action) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.PlayerKeyEvent(self, key, action);
	}
	super.PlayerKeyEvent(key, action);
}

function AwardAdrenaline(float amount) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.AdrenalineGained(amount);
	}
	super.AwardAdrenaline(amount);
}

function ChangedWeapon() {
	local Weapon oldWeapon;
	local PlayerListener l;

	if (Pawn != none) {
		oldWeapon = Pawn.Weapon;
		super.ChangedWeapon();
		for (l = Listeners; l != none; l = l.NextListener) {
			l.ChangedWeapon(oldWeapon, Pawn.Weapon);
		}
	} else {
		super.ChangedWeapon();
	}
}

event RemoteDied(Controller killer, class<DamageType> damageType) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Died(killer, damageType);
		l.PlayerDied(killer, self, Pawn, damageType);
	}
}

function ServerDoCombo(class<Combo> comboClass) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Combo(comboClass);
	}
	super.ServerDoCombo(comboClass);
}

function HandlePickup(Pickup pickup) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.PickedUp(pickup);
	}
	super.HandlePickup(pickup);
}

event HearNoise(float Loudness, Actor noiseMaker) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.HearNoise(Loudness, noiseMaker);
	}
	super.HearNoise(Loudness, noiseMaker);
}

event Killed(Controller Killer, Controller Killed, Pawn killedPawn, class<DamageType> DamageType) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.PlayerDied(Killer, Killed, killedPawn, DamageType);
		if (Killer == self) {
			l.Killed(Killed, DamageType);
		}
	}
}

event MayFall() {
	local PlayerListener l;
	if (Pawn != none) {
		for (l = Listeners; l != none; l = l.NextListener) {
			l.WillFall(!Pawn.bIsWalking);
		}
	}
	super.MayFall();
}

function NotifyAddInventory(Inventory inv) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.InventoryAdded(inv);
	}
	super.NotifyAddInventory(inv);
}

function NotifyAltFire(optional float f) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.StartShooting(true);
	}
}

event bool NotifyBump(Actor actor) {
	local PlayerListener l;
	if (Level.TimeSeconds > LastBump + 0.5) {
		LastBump = Level.TimeSeconds;
		for (l = Listeners; l != none; l = l.NextListener) {
			l.BumpedActor(actor);
		}
	}
	return super.NotifyBump(actor);
}
/*
function NotifyClientHearSound(Actor actor, int Id, sound S, vector SoundLocation, vector Parameters, bool Attenuate) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.HearSound(actor, S, SoundLocation);
	}
}*/

function NotifyFire(optional float f) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.StartShooting(false);
	}
}

event NotifyHit(Controller Victim, int Damage, class<DamageType> DamageType) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Hit(Victim, Damage, DamageType);
	}
}

event bool NotifyHitWall(vector hitNormal, actor hitWall) {
	local PlayerListener l;
	if (Level.TimeSeconds > LastWallHit + 0.5) {
		LastWallHit = Level.TimeSeconds;
		for (l = Listeners; l != none; l = l.NextListener) {
			l.BumpedWall(hitWall, hitNormal);
		}
	}
	return super.NotifyHitWall(hitNormal, hitWall);
}

function NotifyJump(bool doubleJump) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Jump(doubleJump,0,0);
	}
}

function bool NotifyLanded(vector hitNormal) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Landed(hitNormal);
	}
	return super.NotifyLanded(hitNormal);
}

function bool NotifyPhysicsVolumeChange(PhysicsVolume newVolume) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.VolumeChanged(newVolume);
	}
	return super.NotifyPhysicsVolumeChange(newVolume);
}

function NotifyStopFire(bool bAltFire) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.StopShooting(bAltFire);
	}
}

function NotifyTakeHit(Pawn instigator, vector hitLocation, int damage, class<DamageType> damageType, vector momentum) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.GotHit(instigator, damage, damageType, hitLocation, momentum);
	}
	super.NotifyTakeHit(instigator, hitLocation, damage, damageType, momentum);
}
//Not USED - instead we are sending global message now!
/*
event PlayerJoined(Controller joined) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.PlayerJoined(joined);
	}
}

event PlayerLeft(Controller left) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.PlayerLeft(left);
	}
}*/

function ReceiveProjectileWarning(Projectile proj) {
	local Pawn shooter;
	local vector direction;
	local PlayerListener l;

	shooter = proj.Instigator;

	if (proj.IsA('FlakChunk')) {
		direction = Normal(proj.Velocity);
	} else {
		direction = vector(proj.Rotation);
	}

	for (l = Listeners; l != none; l = l.NextListener) {
		l.IncomingProjectile(proj, direction, shooter);
	}

	super.ReceiveProjectileWarning(proj);
}

event ReceivedGlobalMessage(Actor sender, coerce string message) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.ReceivedGlobalMessage(sender, message);
	}
}

event ReceivedTeamMessage(Actor sender, coerce string message) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.ReceivedTeamMessage(sender, message);
	}
}

function Restart() {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Respawned(location, rotation);
	}
	super.Restart();
}

exec function ThrowWeapon() {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.DroppedWeapon();
	}
	super.ThrowWeapon();
}

event Touch(Actor Other) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Touched(Other);
	}
	super.Touch(Other);
}

function Trigger(Actor other, Pawn EventInstigator) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Triggered(other, EventInstigator);
	}
	super.Trigger(other, EventInstigator);
}

event ZoneChange(ZoneInfo newZone) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.ZoneChanged(newZone);
	}
	super.ZoneChange(newZone);
}

state PlayerWalking {
	ignores SeePlayer;
	// Do not ignore HearNoise.
}

defaultproperties {}

