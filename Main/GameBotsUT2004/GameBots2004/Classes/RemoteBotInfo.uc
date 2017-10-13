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
//=============================================================================
// RemoteBotInfo.
//=============================================================================
class RemoteBotInfo extends UnrealMPGameInfo
	config(GameBots2004);

var() 		 string VoiceType[32];
var() 		 string BotFaces[32];
var() config byte	Difficulty;

var() config string BotNames[32];
var() 		 int 	BotTeams[32];
var() config float  BotAccuracy;
var() config float  Alertness;
var	  		 byte   ConfigUsed[32];
var() 		 string BotBodies[32];
var() 		 string BotMeshes[32];
var()		string BotSex[32];
var()		 class<SpeciesType> BotSpecies[32];

var() xUtil.PlayerRecord BotSkin;


function PreBeginPlay()
{
	//DON'T Call parent prebeginplay
}

function PostBeginPlay()
{
	//local String NextBotClass, NextBotDesc;

	Super.PostBeginPlay();
}

function int GetBotTeam(int BotN)
{
	return BotTeams[BotN];
}

function FillBotSkin(int Num)
{
	BotSkin.Species = BotSpecies[Num];
	BotSkin.MeshName = BotMeshes[Num];
	BotSkin.FaceSkinName = BotFaces[Num];
	BotSkin.BodySkinName = BotBodies[Num];
	BotSkin.TeamFace = false;//bool
	BotSkin.Sex = BotSex[Num];
	if (BotSkin.Sex == "Female")
		BotSkin.Skeleton = "HumanFemaleA.Skeleton_Female";
	else
		BotSkin.Skeleton = "HumanMaleA.SkeletonMale";


}

function CHIndividualize(RemoteBot NewBot, int num)
{
	local int n;
	n = num % 32;

	// Set bot's skin
	//NewBot.Static.SetMultiSkin(NewBot, BotMeshes[n], BotFaces[n], BotTeams[n]);

	// Set bot's name.
	if ( (BotNames[n] == "") || (ConfigUsed[n] == 1) )
		BotNames[n] = "RemoteBot";

	Level.Game.ChangeName( NewBot, BotNames[n], false );
	if ( BotNames[n] != NewBot.PlayerReplicationInfo.PlayerName )
		Level.Game.ChangeName( NewBot, ("RemoteBot"$NewBot), false);

	ConfigUsed[n] = 1;

	// adjust bot skill
	NewBot.InitializeSkill(Difficulty);

	NewBot.Accuracy = BotAccuracy;
	NewBot.BaseAlertness = Alertness;


	if ( VoiceType[n] != "" && VoiceType[n] != "None" )
		NewBot.PlayerReplicationInfo.VoiceType = class<VoicePack>(DynamicLoadObject(VoiceType[n], class'Class'));

	if(NewBot.PlayerReplicationInfo.VoiceType == None)
		NewBot.PlayerReplicationInfo.VoiceType = class<VoicePack>(DynamicLoadObject(NewBot.VoiceType, class'Class'));
}

function int ChooseBotInfo()
{
	local int n;

	n = 0;

	while ( (n < 31) && (ConfigUsed[n] == 1) )
		n++;

	return n;
}

function class<RemoteBot> CHGetBotClass(int n)
{
    return class<RemoteBot>( DynamicLoadObject(BotBodies[n], class'Class') );
}

defaultproperties
{
     BotFaces(0)="PlayerSkins.EgyptFemaleAHeadA"
     BotFaces(1)="PlayerSkins.EgyptFemaleAHeadB"
     BotFaces(2)="PlayerSkins.EgyptFemaleBHeadA"
     BotFaces(3)="PlayerSkins.EgyptFemaleBHeadB"
     BotFaces(4)="PlayerSkins.EgyptMaleAHeadA"
     BotFaces(5)="PlayerSkins.EgyptMaleAHeadB"
     BotFaces(6)="PlayerSkins.EgyptMaleBHeadA"
     BotFaces(7)="PlayerSkins.EgyptMaleBHeadB"
     BotFaces(8)="PlayerSkins.MercFemaleAHeadA"
     BotFaces(9)="PlayerSkins.MercFemaleAHeadB"
     BotFaces(10)="PlayerSkins.MercFemaleBHeadA"
     BotFaces(11)="PlayerSkins.MercFemaleBHeadB"
     BotFaces(12)="PlayerSkins.MercFemaleCHeadA"
     BotFaces(13)="PlayerSkins.MercMaleAHeadA"
     BotFaces(14)="PlayerSkins.MercMaleAHeadB"
     BotFaces(15)="PlayerSkins.MercMaleBHeadA"
     BotFaces(16)="PlayerSkins.MercMaleBHeadB"
     BotFaces(17)="PlayerSkins.MercMaleCHeadA"
     BotFaces(18)="PlayerSkins.MercMaleDHeadA"
     BotBodies(0)="PlayerSkins.EgyptFemaleABodyA"
     BotBodies(1)="PlayerSkins.EgyptFemaleABodyA_0"
     BotBodies(2)="PlayerSkins.EgyptFemaleBBodyA"
     BotBodies(3)="PlayerSkins.EgyptFemaleBBodyA_1"
     BotBodies(4)="PlayerSkins.EgyptMaleABodyA"
     BotBodies(5)="PlayerSKins.EgyptMaleABodyA_0"
     BotBodies(6)="PlayerSkins.EgyptMaleBBodyA"
     BotBodies(7)="PlayerSkins.EgyptMaleBBodyA_0"
     BotBodies(8)="PlayerSkins.MercFemaleABodyA"
     BotBodies(9)="PlayerSkins.MercFemaleABodyA_0"
     BotBodies(10)="PlayerSkins.MercFemaleBBodyA"
     BotBodies(11)="PlayerSkins.MercFemaleBBodyA_0"
     BotBodies(12)="PlayerSkins.MercFemaleCBodyA"
     BotBodies(13)="PlayerSkins.MercMaleABodyA"
     BotBodies(14)="PlayerSkins.MercMaleABodyC"
     BotBodies(15)="PlayerSkins.MercMaleABodyD"
     BotBodies(16)="PlayerSkins.MercMaleBBodyA"
     BotBodies(17)="PlayerSkins.MercMaleCBodyA"
     BotBodies(18)="PlayerSkins.MercMaleDBodyA"
     Difficulty=5
     BotNames(0)="Yigal"
     BotNames(1)="Paul"
     BotNames(2)="Jeff"
     BotNames(3)="Lewis"
     BotNames(4)="Milind"
     BotNames(5)="Ed"
     BotNames(6)="Daniel"
     BotNames(7)="Bill"
     BotNames(8)="Jim"
     BotNames(9)="Kevin"
     BotNames(10)="Stacy"
     BotNames(11)="Ulf"
     BotNames(12)="Randy"
     BotNames(13)="Yolanda"
     BotNames(14)="Wei-Min"
     BotNames(15)="Gal"
     BotNames(16)="Sheila"
     BotNames(17)="Andrew"
     BotNames(18)="David"
     BotNames(19)="Andrew M"
     BotNames(20)="Taylor"
     BotNames(21)="Aaron"
     BotNames(22)="Rogelio"
     BotNames(23)="Jay"
     BotNames(24)="Chon"
     BotNames(25)="Jihie"
     BotNames(26)="Jafar"
     BotNames(27)="Benamin"
     BotNames(28)="Mike"
     BotNames(29)="Jose Luis"
     BotNames(30)="Nico"
     BotNames(31)="Hans"
     BotTeams(0)=255
     BotTeams(2)=255
     BotTeams(3)=1
     BotTeams(4)=255
     BotTeams(5)=2
     BotTeams(6)=255
     BotTeams(7)=3
     BotTeams(8)=255
     BotTeams(10)=255
     BotTeams(11)=1
     BotTeams(12)=255
     BotTeams(13)=2
     BotTeams(14)=255
     BotTeams(15)=3
     BotTeams(16)=255
     BotTeams(18)=255
     BotTeams(19)=1
     BotTeams(20)=255
     BotTeams(21)=2
     BotTeams(22)=255
     BotTeams(23)=3
     BotTeams(24)=255
     BotTeams(26)=255
     BotTeams(27)=1
     BotTeams(28)=255
     BotTeams(29)=2
     BotTeams(30)=255
     BotTeams(31)=3
     BotAccuracy=0.500000
     Alertness=0.500000
     BotMeshes(0)="HumanFemaleA.EgyptFemaleA"
     BotMeshes(1)="HumanFemaleA.EgyptFemaleA"
     BotMeshes(2)="HumanFemaleA.EgyptFemaleB"
     BotMeshes(3)="HumanFemaleA.EgyptFemaleB"
     BotMeshes(4)="HumanMaleA.EgyptMaleA"
     BotMeshes(5)="HumanMaleA.EgyptMaleA"
     BotMeshes(6)="HumanMaleA.EgyptMaleB"
     BotMeshes(7)="HumanMaleA.EgyptMaleB"
     BotMeshes(8)="HumanFemaleA.MercFemaleA"
     BotMeshes(9)="HumanFemaleA.MercFemaleA"
     BotMeshes(10)="HumanFemaleA.MercFemaleB"
     BotMeshes(11)="HumanFemaleA.MercFemaleB"
     BotMeshes(12)="HumanFemaleA.MercFemaleC"
     BotMeshes(13)="HumanMaleA.MercMaleA"
     BotMeshes(14)="HumanMaleA.MercMaleA"
     BotMeshes(15)="HumanMaleA.MercMaleA"
     BotMeshes(16)="HumanMaleA.MercMaleB"
     BotMeshes(17)="HumanMaleA.MercMaleC"
     BotMeshes(18)="HumanMaleA.MercMaleD"
     BotSpecies(0)=class'xGame.SPECIES_Egypt'
     BotSpecies(1)=class'xGame.SPECIES_Egypt'
     BotSpecies(2)=class'xGame.SPECIES_Egypt'
     BotSpecies(3)=class'xGame.SPECIES_Egypt'
     BotSpecies(4)=class'xGame.SPECIES_Egypt'
     BotSpecies(5)=class'xGame.SPECIES_Egypt'
     BotSpecies(6)=class'xGame.SPECIES_Egypt'
     BotSpecies(7)=class'xGame.SPECIES_Egypt'
     BotSpecies(8)=class'xGame.SPECIES_Merc'
     BotSpecies(9)=class'xGame.SPECIES_Merc'
     BotSpecies(10)=class'xGame.SPECIES_Merc'
     BotSpecies(11)=class'xGame.SPECIES_Merc'
     BotSpecies(12)=class'xGame.SPECIES_Merc'
     BotSpecies(13)=class'xGame.SPECIES_Merc'
     BotSpecies(14)=class'xGame.SPECIES_Merc'
     BotSpecies(15)=class'xGame.SPECIES_Merc'
     BotSpecies(16)=class'xGame.SPECIES_Merc'
     BotSpecies(17)=class'xGame.SPECIES_Merc'
     BotSpecies(18)=class'xGame.SPECIES_Merc'
     BotSex(0)="Female"
     BotSex(1)="Female"
     BotSex(2)="Female"
     BotSex(3)="Female"
     BotSex(4)="Male"
     BotSex(5)="Male"
     BotSex(6)="Male"
     BotSex(7)="Male"
     BotSex(8)="Female"
     BotSex(9)="Female"
     BotSex(10)="Female"
     BotSex(11)="Female"
     BotSex(12)="Female"
     BotSex(13)="Male"
     BotSex(14)="Male"
     BotSex(15)="Male"
     BotSex(16)="Male"
     BotSex(17)="Male"
     BotSex(18)="Male"
}
