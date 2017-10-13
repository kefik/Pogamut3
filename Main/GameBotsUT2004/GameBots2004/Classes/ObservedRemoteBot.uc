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
// TODO: DroppedWeapon
//-----------------------------------------------------------
class ObservedRemoteBot extends RemoteBot;

var float LastBump; // Last time we sent a BMP message.
var float LastWallHit; // Last time we sent a WAL message.

var PlayerListener Listeners; // Linked list of PlayerListeners observing this player.

function PostBeginPlay() {
	super.PostBeginPlay();
}

/*****************************
* Listener support functions *
*****************************/

simulated event Destroyed() {

	local PlayerListener l;

	super.Destroyed();

	for (l = Listeners; l != none; l = l.NextListener) {
		l.ControllerDestroyed();
	}
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

event RemoteAIHearSound(Actor actor, int Id, sound S, vector SoundLocation, vector Parameters, bool Attenuate) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.HearSound(actor, S, soundLocation);
	}
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

function RemoteJump(bool bDouble, float delay, float force) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Jump(bDouble, delay, force);
	}
	super.RemoteJump(bDouble, delay, force);
}

function RemoteCombo(string ComboClassName) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Combo(class<Combo>(DynamicLoadObject(ComboClassName, class'Class')));
	}
	super.RemoteCombo(ComboClassName);
}

function HaltFiring() {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.StopShooting(false);
	}
	super.HaltFiring();
}

function HandlePickup(Pickup pickup) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.PickedUp(pickup);
	}
	super.HandlePickup(pickup);
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

event NotifyAutoTraceRayResult(
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
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.AutoTraceRayResult(Id, from, to, fastTrace, ProvideFloorCorrection, result, hitNormal, hitLocation, traceActors, hitId);
	}
	super.NotifyAutoTraceRayResult(Id, from, to, fastTrace, ProvideFloorCorrection, result, hitNormal, hitLocation, traceActors, hitId);
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

/** FC called from ControlConnection - arbitraty control server message, invisible to players
* ps - parameter string, pi - integer, pf - float, pb - boolean
*/
function NotifyControlMessage(string type, string ps1, string ps2, string ps3, string pi1, string pi2, string pi3, string pf1, string pf2, string pf3, string pb1, string pb2, string pb3) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.NotifyControlMessage(type, ps1, ps2, ps3, pi1, pi2, pi3, pf1, pf2, pf3, pb1, pb2, pb3);
	}
	super.NotifyControlMessage(type, ps1, ps2, ps3, pi1, pi2, pi3, pf1, pf2, pf3, pb1, pb2, pb3);
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

function bool NotifyLanded(vector HitNormal) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Landed(HitNormal);
	}
	return super.NotifyLanded(HitNormal);
}

event bool NotifyPhysicsVolumeChange(PhysicsVolume newVolume) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.VolumeChanged(newVolume);
	}
	return super.NotifyPhysicsVolumeChange(newVolume);
}

function NotifyTakeHit(Pawn instigator, vector hitLocation, int damage, class<DamageType> damageType, vector momentum) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.GotHit(instigator, damage, damageType, hitLocation, momentum);
	}
	super.NotifyTakeHit(instigator, hitLocation, damage, damageType, momentum);
}

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
}

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

function RemoteDied(Controller Killer, class<DamageType> damageType) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Died(killer, damageType);
		l.PlayerDied(killer, self, Pawn, damageType);
	}
	super.RemoteDied(Killer, damageType);
}

function RemoteFireWeapon(bool bUseAltMode) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.StartShooting(bUseAltMode);
	}
	super.RemoteFireWeapon(bUseAltMode);
}

function RemoteKilled(Controller Killer, Controller Killed, Pawn killedPawn, class<DamageType> damageType) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.PlayerDied(Killer, Killed, killedPawn, DamageType);
		if (Killer == self) {
			l.Killed(Killed, DamageType);
		}
	}
	super.RemoteKilled(Killer, Killed, killedPawn, damageType);
}

event RemoteNotifyClientMessage(Actor sender, coerce string message) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.ReceivedGlobalMessage(sender, message);
	}
	super.RemoteNotifyClientMessage(sender, message);
}

function RemoteNotifyHit(Controller Victim, int Damage, class<DamageType> DamageType) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Hit(Victim, Damage, DamageType);
	}
	super.RemoteNotifyHit(Victim, Damage, DamageType);
}

event RemoteNotifyTeamMessage(Actor sender, coerce string message) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.ReceivedTeamMessage(sender, message);
	}
	super.RemoteNotifyTeamMessage(sender, message);
}


function RemoteRestartPlayer(optional vector startLocation, optional rotator startRotation) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.Respawned(startLocation, startRotation);
	}
	super.RemoteRestartPlayer(startLocation, startRotation);
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

function ZoneChange(ZoneInfo newZone) {
	local PlayerListener l;
	for (l = Listeners; l != none; l = l.NextListener) {
		l.ZoneChanged(newZone);
	}
	super.ZoneChange(newZone);
}

defaultproperties {}

