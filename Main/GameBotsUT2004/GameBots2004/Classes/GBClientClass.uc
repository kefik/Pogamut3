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

class GBClientClass extends TcpLink
	config(GameBots2004);

//Maximum number of arguments
const ArgsMaxCount = 32;

// the main variables where we have incoming messages stored
var string ReceivedData;
var string ReceivedArgs[ArgsMaxCount];
var string ReceivedVals[ArgsMaxCount];

// constants holding CR and LF chars.
var string cr, lf, crlf;

//for logging purposes
var string lastGBCommand;

//used to store info inside function where local wont work
//set property text dosent seem to work with a local
var actor tempActor;

// set true for verbose debug logs
var config bool bDebug;

// set true for iterative mode
var config bool bIterative;

// enables disables cheating - invulnerability, spawning items for bots
var config bool bAllowCheats;

// if control server or bots can or cannot pause the game
var config bool bAllowPause;

//this is helper variable so we can cast from class<Inventory> to class<Weapon> in fc. GetItemAmount
var class<Weapon> curWeapon;

//For bot exporting (ExportPlayers())
var bool bExportHumanPlayers;
var bool bExportRemoteBots;
var bool bExportUnrealBots;

var GBClientClass Next; //create list of all classes

struct PickupStruct
{
	var() class<Pickup> PickupClass;
};

var array<PickupStruct> ExportedPickup;


//Here is where we handle incoming commands
/* Commands expected to look like:
runto {Argument value} {Arg value}...
Currently hard coded to take no more than ArgsMaxCount args
Command type and arguments can be
any length, but first space terminates the name. Values can
have spaces or any other kind of character.
*/
function ReceivedLine(string S)
{
	local string cmdType, argBody, rem;
	local int endloc, wordsplit, attrNum;

	if(bDebug)
		log(S);

	wordsplit = InStr(S," ");
	if( wordsplit == -1)
		wordsplit = Len(S);

	cmdType = left(S,wordsplit);
	rem = mid(S,InStr(S,"{"));

	attrNum = 0;
	// clear previously received attr/val pairs
	while(attrNum < ArgsMaxCount)
	{
		if (ReceivedArgs[attrNum] == "")
			break;

		ReceivedArgs[attrNum] = "";
		ReceivedVals[attrNum] = "";

		attrNum++;
	}

	attrNum = 0;

	//iterate through attr/val pairs, storring them in the
	//parallel arrays ReceivedArgs and ReceivedVals
	while(attrNum < ArgsMaxCount && rem != "")
	{
		endloc = InStr(rem,"}");
		argBody = mid(rem,1,(endloc - 1));

		wordsplit = InStr(argBody," ");
		ReceivedArgs[attrNum] = left(argBody,wordsplit);
		ReceivedVals[attrNum] = mid(argBody,(wordsplit + 1));

		rem = mid(rem,1); //advance
		rem = mid(rem,InStr(rem,"{"));
		attrNum++;
	}

	cmdType = Caps(cmdType);

	ProcessAction(cmdType);
}

//Recieve info - parse into lines and call RecievedLine
event ReceivedText( string Text )
{
	local int i;
	local string S;

    if(bDebug)
    	log("Recieved:"$Text);

	ReceivedData = ReceivedData $ Text;
	//for logging purposes
	lastGBCommand = Text;

	// remove a LF which arrived in a new packet
	// and thus didn't get cleaned up by the code below
	if(Left(ReceivedData, 1) == lf)
		ReceivedData = Mid(ReceivedData, 1);
	i = InStr(ReceivedData, cr);
	while(i != -1)
	{
		S = Left(ReceivedData, i);
		i++;
		// check for any LF following the CR.
		if(Mid(ReceivedData, i, 1) == lf)
			i++;

		ReceivedData = Mid(ReceivedData, i);

		ReceivedLine(S);

		if(LinkState != STATE_Connected)
			return;

		i = InStr(ReceivedData, cr);
	}
}

//Called from child classes, here we set CR and LF chars.
//so we don't need to compute them all the time
event Accepted() {
	cr = Chr(13);
	lf = Chr(10);
	crlf = Chr(13)$Chr(10);
}

function ProcessAction(string cmdType)
{
	//Shouldnt be called, just for inheritance
	log("Error - we are in GBClientClass, ProcessAction() ");
}

// function for getting string value of input received attribute
function string GetArgVal(string argName)
{
	local int i;
	while (i < ArgsMaxCount && ReceivedArgs[i] != "")
	{
		if (ReceivedArgs[i] ~= argName)
			return ReceivedVals[i];
		i++;
	}

	return "";
}

// should use int's for locations rather than floats
// we don't need to be that precise
function ParseVector(out vector v, string vecName)
{
	local int i;
	local string rem;
	local string delim;

	delim = " ";

	rem = GetArgVal(vecName);
	if(rem != "")
	{
		if( InStr(rem,delim) == -1 )
			delim = ",";
		i = InStr(rem,delim);
		v.X = float(left(rem,i));
		rem = mid(rem,i+1);
		i = InStr(rem,delim);
		v.Y = float(left(rem,i));
		v.Z = float(mid(rem,i+1));
	}
	else
	{
		v.x = float( GetArgVal("x") );
		v.y = float( GetArgVal("y") );
		v.z = float( GetArgVal("z") );
	}
}

// function for parsing rotation
function ParseRot(out rotator rot, string rotName)
{
    local int i;
    local string rem;
    //local float y,p,r;
    local string delim;

    delim = " ";

	rem = GetArgVal(rotName);
	if(rem != "")
	{
        if( InStr(rem,delim) == -1 )
        	delim = ",";
        i = InStr(rem,delim);
        rot.Pitch = float(left(rem,i));
        rem = mid(rem,i+1);
        i = InStr(rem,delim);
        rot.Yaw = float(left(rem,i));
        rot.Roll = float(mid(rem,i+1));
	}
	else
	{
		rot.Pitch = float( GetArgVal("pitch") );
		rot.Yaw = float( GetArgVal("yaw") );
		rot.Roll = float( GetArgVal("roll") );
	}
}

//Send a line to the client
function SendLine(string Text, optional bool bNoCRLF)
{
	if(bNoCRLF)
		SendText(Text);
	else
		SendText(Text$crlf);
}

//When we need to tell something to all
function GlobalSendLine(string Text, bool bNotifyAllControlServers, bool bNotifyAllOBservers, bool bNotifyAllBots, optional bool bNoCRLF)
{
	BotDeathMatch(Level.Game).GlobalSendLine(Text, bNotifyAllControlServers, bNotifyAllOBservers, bNotifyAllBots, bNoCRLF);
}

//sends NFO message
function SendGameInfo()
{
	SendLine(GetGameInfo());   //last part may differ according to the game type
}

//gets NFO message
function string GetGameInfo() {
	local string gameInfoStr, levelName, PauseResult;
	local int i;

	gameInfoStr = BotDeathMatch(Level.Game).GetGameInfo();
	levelName = string(Level);
	i = InStr(Caps(levelName), ".LEVELINFO");

	if(i != -1)
		levelName = Left(levelName, i);

	if (Level.Pauser != None)
		PauseResult = "True";
	else
		PauseResult = "False";

	return "NFO {Gametype " $ BotDeathMatch(Level.Game).GameClass $
		"} {WeaponStay " $ Level.Game.bWeaponStay $
		"} {GamePaused " $ PauseResult $
		"} {BotsPaused " $ Level.bPlayersOnly $
		"} {Level " $ levelName $
		"}" $ gameInfoStr;   //last part may differ according to the game type
}

//Get amount of goodies we are provided when we pick various items
function int GetItemAmount(Pickup Pickup)
{
	local int amount;

	amount = 0;
	if (Pickup.IsA('TournamentHealth'))
	{
		amount = TournamentHealth(Pickup).HealingAmount;
	}
	else if (Pickup.IsA('ArmorPickup'))
	{
		amount = class<Armor>(Pickup.InventoryType).default.ArmorAbsorption;
	}
	else if (Pickup.IsA('AdrenalinePickup'))
	{
		amount = AdrenalinePickup(Pickup).AdrenalineAmount;
	}
	else if (Pickup.IsA('ShieldPickup'))
	{
		amount = ShieldPickup(Pickup).ShieldAmount;
	}
	else if (Pickup.IsA('Ammo'))
	{
		amount = Ammo(Pickup).AmmoAmount;
	}
	else if (Pickup.IsA('WeaponPickup'))
	{
		if (pickup.PickUpBase != none) {
			// Spawned weapon.
			amount = class<Weapon>(pickup.InventoryType).default.FireModeClass[0].default.AmmoClass.default.InitialAmount;
			// Is there a more obvious way of getting a gun's initial ammo amount?
		} else {
			// Dropped weapon.
			amount = WeaponPickup(pickup).AmmoAmount[0];
		}
	}
	return amount;
}

//Here the unique IDs are created from objects in UT
function string GetUniqueId(Actor inputActor)
{
	local string temp;

	if (inputActor == none)
		return "None";

	if (inputActor.IsA('Controller'))
	{
		if (Controller(inputActor).PlayerReplicationInfo != none)
		{
			return inputActor $ Controller(inputActor).PlayerReplicationInfo.PlayerID;
		}
	}//vehicle has to be above Pawn, cause Vehicle is a child of Pawn!!
	else if (inputActor.IsA('Vehicle'))
    {
   		if (string(inputActor.Group) == "None")
		{
			BotDeathMatch(Level.Game).GameBotsID++;
			inputActor.SetPropertyText("Group","VehID" $ string(BotDeathMatch(Level.Game).GameBotsID));
		}

		return inputActor $ inputActor.Group;

    }
    else if (inputActor.IsA('Projectile'))
	{
		if (string(inputActor.Group) == "None")
		{
			BotDeathMatch(Level.Game).GameBotsID++;
			inputActor.SetPropertyText("Group","ProID" $ string(BotDeathMatch(Level.Game).GameBotsID));
		}

		return inputActor $ inputActor.Group;
	}
	else if (inputActor.IsA('Pawn'))
	{
    	if ((Pawn(inputActor).Controller != none) && (Pawn(inputActor).Controller.PlayerReplicationInfo != none))
    	{
			return Pawn(inputActor).Controller $ Pawn(inputActor).Controller.PlayerReplicationInfo.PlayerID;
		}
	}
	else if (inputActor.IsA('Pickup'))
	{
		//The pickups that have pickup base doesn't have unique Id
		//We will send the Id of their pickup base that is unique instead
		if (Pickup(inputActor).PickUpBase == none) {
			//we don't have base, so we will construct unique Id manually
			temp = string(inputActor.Group);
			if (Left(temp, 1) != "i")
			{
				BotDeathMatch(Level.Game).GameBotsID++;
				inputActor.SetPropertyText("Group","id" $ string(BotDeathMatch(Level.Game).GameBotsID));
			}
			return inputActor $ inputActor.Group;
			//return string(inputActor);
		} else {
			return string(Pickup(inputActor).PickUpBase);
		}

	}

	//If we dont recognize the object, we simply return it as it is
	return string(inputActor);
}

//Get Info about firing mode
function string GetFireModeInfo(string i, class<WeaponFire> curWeaponFireClass)
{
	local string outstring;

	local bool PriSplashDamage, PriSplashJump, PriRecSplashDamage, PriTossed, PriLeadTarget, PriInstantHit;
	local bool PriFireOnRelease, PriWaitForRelease, PriModeExclusive;
	local float PriFireRate, PriBotRefireRate;
	local int PriAmmoPerFire, PriAmmoClipSize;
	local float PriAimError, PriSpread;
	local string PriSpreadStyle;
	local int PriFireCount;
	local float PriDamageAtten;
	local int PriDamageMin, PriDamageMax;

    outstring = " {"$i$"FireModeType " $ curWeaponFireClass $ "}";

	if (curWeaponFireClass != none)
	{

		//bools
		PriSplashDamage = curWeaponFireClass.Default.bSplashDamage;
		PriSplashJump = curWeaponFireClass.Default.bSplashJump;
		PriRecSplashDamage = curWeaponFireClass.Default.bRecommendSplashDamage;
		PriTossed = curWeaponFireClass.Default.bTossed;
		PriLeadTarget = curWeaponFireClass.Default.bLeadTarget;
		PriInstantHit = curWeaponFireClass.Default.bInstantHit;

		PriFireOnRelease = curWeaponFireClass.Default.bFireOnRelease;// if true, shot will be fired when button is released, HoldTime will be the time the button was held for
		PriWaitForRelease = curWeaponFireClass.Default.bWaitForRelease;// if true, fire button must be released between each shot
		PriModeExclusive = curWeaponFireClass.Default.bModeExclusive;// if true, no other fire modes can be active at the same time as this one

		PriFireRate = curWeaponFireClass.Default.FireRate; //float
		PriBotRefireRate = curWeaponFireClass.Default.BotRefireRate; //float
		PriAmmoPerFire = curWeaponFireClass.Default.AmmoPerFire; //int
		PriAmmoClipSize = curWeaponFireClass.Default.AmmoClipSize; //int

		PriAimError = curWeaponFireClass.Default.AimError; //float 0=none 1000=quite a bit
		PriSpread = curWeaponFireClass.Default.Spread; //float rotator units. no relation to AimError
		PriSpreadStyle = string(curWeaponFireClass.Default.SpreadStyle); //ESpreadStyle

		PriFireCount = curWeaponFireClass.Default.FireCount; //int
		PriDamageAtten = curWeaponFireClass.default.DamageAtten; //float, attenuate instant-hit/projectile damage by this multiplier

		//Export info to outstring message
		outstring = outstring $ " {"$i$"SplashDamage " $ PriSplashDamage $
			"} {"$i$"SplashJump " $ PriSplashJump $
			"} {"$i$"RecomSplashDamage " $ PriRecSplashDamage $
			"} {"$i$"Tossed " $ PriTossed $
       		"} {"$i$"LeadTarget " $ PriLeadTarget $
			"} {"$i$"InstantHit " $ PriInstantHit $
			"} {"$i$"FireOnRelease " $ PriFireOnRelease $
			"} {"$i$"WaitForRelease " $ PriWaitForRelease $
			"} {"$i$"ModeExclusive " $ PriModeExclusive $
			"} {"$i$"FireRate " $ PriFireRate $
			"} {"$i$"BotRefireRate " $ PriBotRefireRate  $
			"} {"$i$"AmmoPerFire " $ PriAmmoPerFire $
			"} {"$i$"AmmoClipSize " $ PriAmmoClipSize $
			"} {"$i$"AimError " $ PriAimError $
			"} {"$i$"Spread " $ PriSpread $
			"} {"$i$"SpreadStyle " $ PriSpreadStyle $
			"} {"$i$"FireCount " $ PriFireCount $
			"} {"$i$"DamageAtten " $ PriDamageAtten $
			"}";


		if (class<InstantFire>(curWeaponFireClass) != none)
		{
			PriDamageMin = class<InstantFire>(curWeaponFireClass).default.DamageMin;
			PriDamageMax = class<InstantFire>(curWeaponFireClass).default.DamageMax;

			//Export info to outstring message
			outstring = outstring $ " {"$i$"DamageMin " $ PriDamageMin $
			"} {"$i$"DamageMax " $ PriDamageMax $ "}";
		}
		//ShieldGun HACK - the attributes there are different :-/
		if (class<ShieldFire>(curWeaponFireClass) != none)
		{
			PriDamageMin = class<ShieldFire>(curWeaponFireClass).default.MinDamage;
			PriDamageMax = class<ShieldFire>(curWeaponFireClass).default.MaxDamage;

			//Export info to outstring message
			outstring = outstring $ " {"$i$"DamageMin " $ PriDamageMin $
			"} {"$i$"DamageMax " $ PriDamageMax $ "}";
		}
	}
	return outstring;
}

function string GetProjectileInfo(string i, class<Projectile> curProjClass)
{
	local string outstring;

	local string PriProjClass;
	local float PriDamage, PriSpeed, PriMaxSpeed, PriLifeSpan, PriDamageRadius, PriTossZ, PriMaxEffectDistance;

	PriProjClass = string(curProjClass);

	outstring = " {"$i$"ProjType " $ PriProjClass $ "}";

	if (curProjClass != none)
	{
		PriDamage = curProjClass.Default.Damage; //float
		PriSpeed = curProjClass.default.Speed; //float
		PriMaxSpeed = curProjClass.default.MaxSpeed; //float
		PriLifeSpan = curProjClass.default.LifeSpan; //float
		PriDamageRadius = curProjClass.default.DamageRadius; //float
		PriTossZ = curProjClass.default.TossZ; //float
		PriMaxEffectDistance = curProjClass.default.MaxEffectDistance;

		//Export info to outstring message
    	outstring = outstring $ " {"$i$"Damage " $ PriDamage $
			"} {"$i$"Speed " $ PriSpeed $
			"} {"$i$"MaxSpeed " $ PriMaxSpeed $
			"} {"$i$"LifeSpan " $ PriLifeSpan $
			"} {"$i$"DamageRadius " $ PriDamageRadius $
			"} {"$i$"TossZ " $ PriTossZ $
			"} {"$i$"MaxEffectDistance " $ PriMaxEffectDistance $
			"}";
	}

	return outstring;
}

function string ExportWeaponInfo(class<Weapon> currentWeaponClass)
{
	local string outstring;

	local bool bUseAlternateAmmo;

    //determines if the weapon uses two separate ammos
    bUseAlternateAmmo = false;
	if (currentWeaponClass.default.FireModeClass[0] != None && currentWeaponClass.default.FireModeClass[0].default.AmmoClass != none)
	{
		if (currentWeaponClass.default.FireModeClass[1] != None && currentWeaponClass.default.FireModeClass[1].default.AmmoClass != none)
		{
			if (currentWeaponClass.default.FireModeClass[0].default.AmmoClass != currentWeaponClass.default.FireModeClass[1].default.AmmoClass)
			{
                bUseAlternateAmmo = true;
			}
		}
    }

	outstring = " {Melee " $ currentWeaponClass.default.bMeleeWeapon $
		"} {Sniping " $ currentWeaponClass.default.bSniping $
		"} {UsesAltAmmo " $ bUseAlternateAmmo $ "}";

	//Get Info about primary firing mode
    outstring = outstring $ GetFireModeInfo("Pri", currentWeaponClass.default.FireModeClass[0]);
	//info from Ammunition class
    outstring = outstring $ GetAmmunitionInfo("Pri", currentWeaponClass.default.FireModeClass[0].default.AmmoClass);
	//info from projectile class
	outstring = outstring $ GetProjectileInfo("Pri", currentWeaponClass.default.FireModeClass[0].default.ProjectileClass);


	//Export info about secondary firing mode
    outstring = outstring $ GetFireModeInfo("Sec", currentWeaponClass.default.FireModeClass[1]);
    //info from Ammunition class
    outstring = outstring $ GetAmmunitionInfo("Sec", currentWeaponClass.default.FireModeClass[1].default.AmmoClass);
    //info from projectile class
    outstring = outstring $ GetProjectileInfo("Sec", currentWeaponClass.default.FireModeClass[1].default.ProjectileClass);

	return outstring;
}

//Exports complete info about Ammunition - including Projectile info
function string ExportAmmoInfo(class<Ammunition> curAmmunitionClass)
{
	local string outstring;

	outstring = GetAmmunitionInfo("Pri", curAmmunitionClass);
	//Add info about projectile if any

    outstring = outstring $ GetProjectileInfo("Pri", curAmmunitionClass.default.ProjectileClass);

	outstring = outstring $ " {Amount " $ class<Ammo>(curAmmunitionClass.default.PickupClass).default.AmmoAmount $ "}";

	return outstring;
}

//Export info just from Ammunition class and DamageType class
function string GetAmmunitionInfo(string i, class<Ammunition> curAmmunitionClass)
{
	local string outstring;

	//helper class
	local class<DamageType> curDamageType;

	local string PriAmmoClass, PriDamageType;
	local int PriInitialAmount, PRiMaxAmount;
	local float PriMaxRange;
	local bool PriArmorStops, PriAlwaysGibs, PriSpecial, PriDetonatesGoop, PriSuperWeapon, PriExtraMomZ;

	PriAmmoClass = string(curAmmunitionClass);
    outstring = " {"$i$"AmmoType " $ PriAmmoClass $ "}";

    if (curAmmunitionClass != none)
    {

		PriInitialAmount = curAmmunitionClass.Default.InitialAmount; //int
		PriMaxAmount = curAmmunitionClass.Default.MaxAmmo; //int
		PriMaxRange = curAmmunitionClass.Default.MaxRange; //float for autoaim

		//Export info to outstring message
	    outstring = outstring $ " {"$i$"InitialAmount " $ PriInitialAmount $
			"} {"$i$"MaxAmount " $ PriMaxAmount $
			"} {"$i$"MaxRange " $ PriMaxRange $
			"}";

		curDamageType = curAmmunitionClass.default.MyDamageType;
		PriDamageType = string(curDamageType);

		//Add information about DamageType
		if (curDamageType != none)
		{
			//bools
			PriArmorStops = curDamageType.default.bArmorStops;
			PriAlwaysGibs =	curDamageType.default.bAlwaysGibs;
			PriSpecial = curDamageType.default.bSpecial;
			PriDetonatesGoop = curDamageType.default.bDetonatesGoop;
			PriSuperWeapon = curDamageType.default.bSuperWeapon;		// if true, also damages teammates even if no friendlyfire
			PriExtraMomZ = curDamageType.default.bExtraMomentumZ;	// Add extra Z to momentum on walking pawns

			//Export info to outstring message
    	    outstring = outstring $ " {"$i$"DamageType " $ PriDamageType $
				"} {"$i$"ArmorStops " $ PriArmorStops $
				"} {"$i$"AlwaysGibs " $ PriAlwaysGibs $
				"} {"$i$"Special " $ PriSpecial $
				"} {"$i$"DetonatesGoop " $ PriDetonatesGoop $
				"} {"$i$"SuperWeapon " $ PriSuperWeapon $
				"} {"$i$"ExtraMomZ " $ PriExtraMomZ $
				"}";
		}
	}

	return outstring;
}

function ExportGameStatus()
{
	BotDeathMatch(Level.Game).SendGameStatus(self);
}

function ExportFlagInfo()
{
	local CTFFlag F;
	local string outstring;

	if (Level.Game.IsA('BotCTFGame')) {
		foreach AllActors (class'CTFFlag', F)
		{
			outstring = "FLG {Id " $ F $
				"} {Team " $ F.Team.TeamIndex $
				"} {State " $ F.GetStateName() $
				"}";

			//when a flag is held its location is not updated by engine =(
			if(F.IsInState('Held') && F.Holder != none)
			{
				outstring = outstring $ " {Location " $ F.Holder.Location $
					"} {Holder " $ GetUniqueId(F.Holder) $"}";
			}
			else
			{
				outstring = outstring $" {Location " $ F.Location $"}";
			}

			SendLine(outstring);
		}
	}
}

function ExportDomPointInfo()
{
	local int i, temp;
	local BotDoubleDomination DomGame;
	local xDomPoint DP;
	local string outstring;

	if (Level.Game.IsA('BotDoubleDomination')) {
		DomGame = BotDoubleDomination(Level.Game);
		for (i = 0; i<2; i++) {
			DP = DomGame.xDomPoints[i];
			if( DP.ControllingTeam == none )
				temp = 255;
			else
				temp = DP.ControllingTeam.TeamIndex;

			outstring = "NAV {Id " $ DP $
				"} {Location " $ DP.Location $
				"} {DomPoint True" $
				"} {DomPointController " $ temp $
				"}";
    		SendLine(outstring);
		}
	}
}

//Sends list of all mutators on the server (MUT batch info mess.)
function ExportMutators()
{
	local Mutator M;

	SendLine("SMUT");
	for (M = BotDeathMatch(Level.Game).BaseMutator; M != None; M = M.NextMutator)
	{
		SendLine("MUT {Id " $ M $
			"} {Name " $ M.Name $
			"}");
	}
	SendLine("EMUT");

}

//Sends list of all movers on the server (MOV batch info mess.)
function ExportMovers()
{
	local Mover Mov;
	local string outstring;

	SendLine("SMOV");
	foreach AllActors(class'Mover',Mov)
	{
		outstring = "MOV {Id " $ Mov $
			"} {Location " $ Mov.Location $
			"} {DamageTrig " $ Mov.bDamageTriggered $
			"} {Type " $ Mov.Class $
			"} {IsMoving " $ Mov.bInterpolating $
			"} {Velocity " $ Mov.Velocity $
			"} {MoveTime " $ Mov.MoveTime $
			"} {OpenTime " $ Mov.StayOpenTime $
			"} {State " $ Mov.GetStateName() $
			"} {BasePos " $ Mov.BasePos $
			"} {BaseRot " $ Mov.BaseRot $
			"} {DelayTime " $ Mov.DelayTime $
			"}";

		 /* Does not work - error. context expression: Variable is too large...
		if (Mov.KeyPos[0] != none) {
			outstring = outstring $ " {KeyPosOne " $ Mov.KeyPos[0] $
				"} {KeyRotOne " $ Mov.KeyRot[0] $
				"}";
		}
		if (Mov.KeyPos[1] != none) {
			outstring = outstring $ " {KeyPosTwo " $ Mov.KeyPos[1] $
				"} {KeyRotTwo " $ Mov.KeyRot[1] $
				"}";
		}
		if (Mov.KeyPos[2] != none) {
			outstring = outstring $ " {KeyPosThree " $ Mov.KeyPos[2] $
				"} {KeyRotThree " $ Mov.KeyRot[2] $
				"}";
		}*/

		if (Mov.myMarker != none) {
			outstring = outstring $ " {NavPointMarker " $ Mov.myMarker $ "}";
		}

		SendLine(outstring);

	}
	SendLine("EMOV");
}

//Exports all navpoints in a level also with their reachable graph
function ExportNavPoints()
{
	local string message;
	local NavigationPoint N, First;
	local int i,j,PathListLength, temp;


	SendLine("SNAV");
	First = Level.NavigationPointList.NextNavigationPoint; //will be used in second loop, to eliminate already procesed objects

	for ( N=Level.NavigationPointList; N!=None; N=N.NextNavigationPoint )
	{
		message = "NAV {Id " $ N $
			"} {Location " $ N.Location $
			"} {Visible false}";

        if (N.IsA('xDomPoint'))
        {
			if( xDomPoint(N).ControllingTeam == none )
				temp = 255;
			else
				temp = xDomPoint(N).ControllingTeam.TeamIndex;

			message = message $ " {DomPoint True" $
					"} {DomPointController " $ temp $
					"}";
        }
		else if (N.IsA('InventorySpot'))
		{
			// The items, which have pickup base DOES NOT have unique id.
			// We need to send unique id, so sending id of pickup base.
			message = message $ " {InvSpot True}";

			if (InventorySpot(N).markedItem != none) {
				message = message $ " {Item " $ GetUniqueID(InventorySpot(N).markedItem) $
					"} {ItemClass " $ InventorySpot(N).markedItem.class $
					"} {ItemSpawned " $ InventorySpot(N).markedItem.IsInState('Pickup') $ "}";
			}
		}
		else if (N.IsA('PlayerStart'))
		{
			message = message $ " {PlayerStart True" $
				"} {TeamNumber " $ PlayerStart(N).TeamNumber $ "}";

		}
		else if (N.IsA('Door'))
		{
		 	message = message $ " {Door True" $
			 	"} {Mover " $ Door(N).MyDoor $ "}";
		}
		else if (N.IsA('LiftCenter'))
		{
			message = message $ " {LiftCenter True" $
				"} {Mover " $ LiftCenter(N).MyLift $
				"} {LiftOffset " $ LiftCenter(N).LiftOffset $ "}";	// starting vector between MyLift location and LiftCenter location
		}
		else if (N.IsA('LiftExit'))
		{
		    message = message $ " {LiftExit True" $
				"} {Mover " $ LiftExit(N).MyLift $
				"} {LiftJumpExit " $ LiftExit(N).bLiftJumpExit $
				"} {NoDoubleJump " $ LiftExit(N).bNoDoubleJump $ "}";
		}
		else if (N.IsA('AIMarker') && AIMarker(N).markedScript.IsA('UnrealScriptedSequence'))
		{
			message = message $ " {AIMarker True" $
					"} {Rotation " $ UnrealScriptedSequence(AIMarker(N).markedScript).Rotation $
					"} {RoamingSpot " $ UnrealScriptedSequence(AIMarker(N).markedScript).bRoamingScript $
					"} {SnipingSpot " $ UnrealScriptedSequence(AIMarker(N).markedScript).bSniping $
					"} {PreferedWeapon " $ UnrealScriptedSequence(AIMarker(N).markedScript).WeaponPreference $
					"}";
		}
		else if (N.IsA('JumpSpot'))
		{
        	message = message $ " {JumpSpot True}";
		}
		else if (N.IsA('JumpDest'))
		{
        	message = message $ " {JumpDest True}";
		}
		else if (N.IsA('UTJumpPad'))
		{
        	message = message $ " {JumpPad True}";
		}
		else if (N.IsA('Teleporter'))
		{
        	message = message $ " {Teleporter True}";
		}

		SendLine(message);

		i = 0;
		PathListLength = N.PathList.Length;
		SendLine("SNGP");
		while (i < PathListLength)
		{
			message = "INGP {Id " $ N.PathList[i].End $
				"} {Flags " $ N.PathList[i].reachFlags $
				"} {CollisionR " $ N.PathList[i].CollisionRadius $
				"} {CollisionH " $ N.PathList[i].CollisionHeight $
				"}";

			if (N.PathList[i].End.IsA('JumpDest'))
			{
				message = message $" {ForceDoubleJump " $ JumpDest(N.PathList[i].End).bForceDoubleJump $
					"}" ;
				for (j = 0; j < JumpDest(N.PathList[i].End).NumUpstreamPaths; j++)
				{
					//message = message $ " {Path"$j$" "$ JumpDest(N.PathList[i].End).UpstreamPaths[j].Start $"}";

					//Searching the record which refers to the path we are now exporting
					if (JumpDest(N.PathList[i].End).UpstreamPaths[j].Start == N)
					{
						message = message $ " {CalculatedGravityZ " $ JumpDest(N.PathList[i].End).CalculatedGravityZ[j] $
							"} {NeededJump " $ JumpDest(N.PathList[i].End).NeededJump[j] $
							"}";
					}
				}

			}

			//Jump spot is a child of jump dest
			if (N.PathList[i].End.IsA('JumpSpot'))
				message = message $ " {NeverImpactJump " $ JumpSpot(N.PathList[i].End).bNeverImpactJump $
					"} {NoLowGrav " $ JumpSpot(N.PathList[i].End).bNoLowGrav $
					"} {OnlyTranslocator " $ JumpSpot(N.PathList[i].End).bOnlyTranslocator $
					"} {TranslocTargetTag " $ JumpSpot(N.PathList[i].End).TranslocTargetTag $
					"} {TranslocZOffset " $ JumpSpot(N.PathList[i].End).TranslocZOffset $
					"}";

			SendLine(message);
			i++;
		}
		SendLine("ENGP");

	}
	SendLine("ENAV");

}

function ExportInventory()
{
	local Pickup Pickup;
	local string outstring;

	SendLine("SINV");

	foreach AllActors(class'Pickup',Pickup)
	{
		outstring = "INV {Id " $ GetUniqueId(Pickup) $
			"} {Location " $ Pickup.Location $
			"} {Amount " $ GetItemAmount(Pickup) $
			"} {Dropped " $ Pickup.bDropped $
			"} {Type " $ Pickup.Class $
			"} {Visible false}";
		if (Pickup.MyMarker != none)
			outstring $= " {NavPointId " $ Pickup.MyMarker $ "}";
		SendLine(outstring);
	}

	SendLine("EINV");

}

//Process Pickup and builds ITC message
function string GetPickupInfoToITC(class<Pickup> P)
{
	local string outstring;

	outstring = "ITC {InventoryType " $ P.default.InventoryType $
		"} {PickupType " $ P $ "}";
	//We determine what it is and call appropriate functions
	if (class<Ammo>(P) != none)
    {
    	outstring = outstring $ " {ItemCategory Ammo}";
    	outstring = outstring $ ExportAmmoInfo(class<Ammunition>(class<Ammo>(P).default.InventoryType));
	}
	else if (class<WeaponPickup>(P) != none)
	{
		outstring = outstring $ " {ItemCategory Weapon}";
        outstring = outstring $ ExportWeaponInfo(class<Weapon>(class<WeaponPickup>(P).default.InventoryType));
	}
	else if (class<TournamentHealth>(P) != none)
	{
		outstring = outstring $ " {ItemCategory Health}";
		outstring = outstring $ " {Amount " $ class<TournamentHealth>(P).default.HealingAmount $
			"} {SuperHeal " $ class<TournamentHealth>(P).default.bSuperHeal $
			"}";
	}
	else if (class<ArmorPickup>(P) != none)
	{
		outstring = outstring $ " {ItemCategory Armor}";
		outstring = outstring $ " {Amount " $ class<Armor>(class<ArmorPickup>(P).default.InventoryType).default.ArmorAbsorption $
			"}";
	}
	else if (class<AdrenalinePickup>(P)!= none)
	{
		outstring = outstring $ " {ItemCategory Adrenaline}";
		outstring = outstring $ " {Amount " $ int(class<AdrenalinePickup>(P).default.AdrenalineAmount) $
			"}";
	}
	else if (class<ShieldPickup>(P) != none)
	{
		outstring = outstring $ " {ItemCategory Shield}";
		outstring = outstring $ " {Amount " $ class<ShieldPickup>(P).default.ShieldAmount $
			"}";
	}
	else
	{
		outstring = outstring $ " {ItemCategory Other}";
	}

	return outstring;
}

//Exports all items with their attributes in ITC message
function ExportItemClasses()
{
	local Pickup P;
	local int i;
	local bool bAlreadyExported;

	local class<ShieldGun> ShieldGunClass;
	local class<AssaultRifle> AssaultRifleClass;
	local class<Translauncher> TranslocatorClass;



	SendLine("SITC");

    //Export ShieldGun - we get this gun automatically at the beginning
    ShieldGunClass = class<ShieldGun>(DynamicLoadObject("XWeapons.ShieldGun",class'Class',true));
	SendLine(GetPickupInfoToITC(ShieldGunClass.default.PickupClass));
	ExportedPickup.Insert(ExportedPickup.Length,1);
	ExportedPickup[ExportedPickup.Length - 1].PickupClass = ShieldGunClass.default.PickupClass;

    //Export AssaultRifle - we get this gun automatically at the beginning
    AssaultRifleClass = class<AssaultRifle>(DynamicLoadObject("XWeapons.AssaultRifle",class'Class',true));
	SendLine(GetPickupInfoToITC(AssaultRifleClass.default.PickupClass));
	ExportedPickup.Insert(ExportedPickup.Length,1);
	ExportedPickup[ExportedPickup.Length - 1].PickupClass = AssaultRifleClass.default.PickupClass;

	//if we are in CTF game we have to export Translocator as well!
    //Export Translocator - if we are in a team game we get it automatically at the beginning of the game
	TranslocatorClass = class<Translauncher>(DynamicLoadObject("XWeapons.Translauncher",class'Class',true));
	SendLine(GetPickupInfoToITC(TranslocatorClass.default.PickupClass));
	ExportedPickup.Insert(ExportedPickup.Length,1);
	ExportedPickup[ExportedPickup.Length - 1].PickupClass = TranslocatorClass.default.PickupClass;

	foreach AllActors(class'Pickup',P)
	{
		//First check if we haven't exported this already
		bAlreadyExported = false;
		for (i = 0; i < ExportedPickup.Length; i++)
		{
			if (ExportedPickup[i].PickupClass == P.Class)
			{
				bAlreadyExported = true;
				break;
			}
		}

		if (!bAlreadyExported)
		{
			SendLine(GetPickupInfoToITC(P.class));

			//Add exported pickup class to list holding exported classes
			ExportedPickup.Insert(ExportedPickup.Length,1);
			ExportedPickup[ExportedPickup.Length - 1].PickupClass = P.Class;
		}
	}
	SendLine("EITC");
}

//Exports all players in the level
function ExportPlayers(optional bool bLimitedInfo)
{
	local Controller C;
	local string message, TeamIndex, WeaponClass;
	local rotator PawnRotation;

	for(C = Level.ControllerList; C != None; C = C.NextController )
	{
		if ((bExportRemoteBots && C.IsA('RemoteBot')) || (bExportHumanPlayers && C.IsA('GBxPlayer')) || (bExportUnrealBots && C.IsA('GBxBot')))
		{

			if (C.PlayerReplicationInfo.Team != none)
				TeamIndex = string(C.PlayerReplicationInfo.Team.TeamIndex);
			else
    			TeamIndex = "255";

			if ((C.Pawn != none) && (C.Pawn.Weapon != none))
				WeaponClass = string(C.Pawn.Weapon.Class);
			else
				WeaponClass = "None";

			message = "PLR {Id " $ GetUniqueId(C) $
				"} {Spectator " $ (C.PlayerReplicationInfo.bIsSpectator || C.IsInState('Spectating')) $
				"} {Name " $ C.PlayerReplicationInfo.PlayerName $
				"} {Team " $ TeamIndex $				
				"}";

			if (!bLimitedInfo)
			{
				if (C.IsA('RemoteBot')) {
					if (RemoteBot(C).jmx != "") {
		            	message = message $ " {Jmx " $ RemoteBot(C).jmx $ "}";
		            }
	            }

				if (C.Pawn != none) {
					PawnRotation = C.Pawn.Rotation;
					PawnRotation.Pitch = int(C.Pawn.ViewPitch) * 65535/255;
					message = message$" {Location " $ C.Pawn.Location $
						"} {Rotation " $ PawnRotation $
						"} {Velocity " $ C.Pawn.Velocity $
						"} {Weapon " $ WeaponClass $
						"}";
				}
				else if ((C.IsA('PlayerController')) && (PlayerController(C).ViewTarget != none))
					message = message$" {Location " $ PlayerController(C).CalcViewLocation $
						"} {Rotation " $ PlayerController(C).CalcViewRotation $
						"} {Velocity " $ PlayerController(C).ViewTarget.Velocity $
						"}";
				else
					message = message$" {Location " $ C.Location $
						"} {Rotation " $ C.Rotation $
						"} {Velocity " $ C.Velocity $
						"}";

			}
			SendLine(message);
		}

    }
}

function GetPlayers()
{
	local Controller C;
	local string message;
	local string Dead;

	for(C = Level.ControllerList; C != None; C = C.NextController )
	{
		if (C.IsA('RemoteBot') || C.IsA('GBxPlayer') || C.IsA('GBxBot'))
		{
			/*if (C.IsA('RemoteBot')
			{
				Type = "RemoteBot";
			}*/

			if (C.Pawn == None)
				Dead = "True";
			else
				Dead = "False";

			message = "PLR {Id " $ GetUniqueId(C) $
			  "} {Spectator " $ (C.PlayerReplicationInfo.bIsSpectator || C.IsInState('Spectating')) $
				"} {Name " $ C.PlayerReplicationInfo.PlayerName $
				"} {PlayerType " $ C $
				"} {PlayerDead " $ Dead $"}";

			if (C.Pawn != none)
			{
				message = message $ " {Location " $ C.Pawn.Location $
					"} {Rotation " $ C.Pawn.Rotation $ "}";
			}

			SendLine(message);
		}

    }
}

/**
* Can be called from BotConnection or ControlConnection to notify Control Message sent by bot or control server.
* ps - parameter string, pi - integer, pf - float, pb - boolean
*/
function NotifyControlMessage(string type, string ps1, string ps2, string ps3, string pi1, string pi2, string pi3, string pf1, string pf2, string pf3, string pb1, string pb2, string pb3) {
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
	SendLine(outstring);
}

defaultproperties
{
	bAllowPause=true
	bExportHumanPlayers=true
	bExportRemoteBots=true
	bExportUnrealBots=true

}
