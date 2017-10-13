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
//
//-----------------------------------------------------------
class PlayerListener extends Actor;

var PlayerListener NextListener;
var Controller MyController;

event Destroyed() {
	RemoveListener();
	super.Destroyed();
}

function bool AddListener(Controller c) {
	if (c.IsA('ObservedPlayer')) {
		ObservedPlayer(c).AddListener(self);
		return true;
	}
	if (c.IsA('ObservedRemoteBot')) {
		ObservedRemoteBot(c).AddListener(self);
		return true;
	}
	return false;
}

function bool RemoveListener() {
	if (MyController == none) {
		return true;
	}
	if (MyController.IsA('ObservedPlayer')) {
		ObservedPlayer(MyController).RemoveListener(self);
		return true;
	}
	if (MyController.IsA('ObservedRemoteBot')) {
		ObservedRemoteBot(MyController).RemoveListener(self);
		return true;
	}
	return false;
}

function bool IsListening() {
	return MyController != none;
}

event Added() {}
event AdrenalineGained(float amount) {}
event AutoTraceRayResult(string Id, vector from, vector to, bool fastTrace, bool ProvideFloorCorrection, bool result, vector hitNormal, vector hitLocation, bool traceActors, string hitId) {}
event BumpedActor(Actor actor) {}
event BumpedWall(Actor wall, vector hitNormal) {}
event Combo(class<Combo> comboClass) {}
event ControllerDestroyed() {}
event ChangedTeam(int oldTeam, int newTeam) {}
event ChangedWeapon(Weapon oldWeapon, Weapon newWeapon) {}
event Died(Controller killer, class<DamageType> damageType) {}
event DroppedWeapon() {}
event GotHit(Pawn shooter, int damage, class<DamageType> damageType, vector hitLocation, vector hitMomentum) {}
event HearNoise(float Loudness, Actor noiseMaker) {}
event HearSound(Actor actor, sound s, vector soundLocation) {}
event Hit(Controller target, int damage, class<DamageType> damageType) {}
event InventoryAdded(Inventory inventory) {}
event Jump(bool doubleJump, float delay, float force) {}
event Killed(Controller killed, class<DamageType> damageType) {}
event Landed(vector hitNormal) {}
event NotifyControlMessage(string type, string ps1, string ps2, string ps3, string pi1, string pi2, string pi3, string pf1, string pf2, string pf3, string pb1, string pb2, string pb3) {}
event PickedUp(Pickup pickup) {}
event PlayerDied(Controller killer, Controller killed, Pawn killedPawn, class<DamageType> damageType) {}
event PlayerJoined(Controller joined) {}
event PlayerKeyEvent(PlayerController PC, Interactions.EInputKey key, Interactions.EInputAction action) {}
event PlayerLeft(Controller left) {}
event IncomingProjectile(Projectile projectile, vector direction, Pawn shooter) {}
event ReceivedGlobalMessage(Actor sender, string message) {}
event ReceivedTeamMessage(Actor sender, string message) {}
event Removed() {}
event Respawned(vector location, rotator rotation) {}
event StartShooting(bool altShooting) {}
event StopShooting(bool altShooting) {}
event Touched(Actor actor) {}
event Triggered(Actor trigger, Actor instigator) {}
event VolumeChanged(PhysicsVolume newVolume) {}
event WillFall(bool fall) {}
event ZoneChanged(ZoneInfo newZone) {}

defaultproperties {
}
