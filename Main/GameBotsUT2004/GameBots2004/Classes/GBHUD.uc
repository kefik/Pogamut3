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


// This is the class, where we add additional functionality to Player HUD.
// We draw on the HUD additional debug information about bots and player/s.
class GBHud extends HudBase
	config(GameBots2004);

//We will be drawing names of navigation points that are in radius below
var float NavPointBeaconDrawDistance;

//How many points we should shift in y coordinate to write next line on the HUD
//properly
var float shift;

//Some variables for setting what should be visible on the HUD
var config bool bDrawNavPointsNames;
var config bool bDisplayDebug;
var config bool bDisplayHelp;
var config bool bDisplayInformation;
var config bool bDisplayPlayerList;
var config bool bDisplayRoute;
var config bool bDisplayHealthBar;
var config int DrawNavPointsGrid;
var config bool bDisplayTextBubble;
var config bool bDisplayMyLocation;
var config bool bDisplayNavCubes;
var config int DisplayPlayerPositions;
var config bool bDisplayLastKeyPressed;
var config bool bDisplayUsableObjects;
var config bool bDisplayHoldedObject;

var HUDLocalizedMessage lastKeyPressedMessage;

//Mouse cursor texture
var Texture MouseCursorTexture;

//hold information about players current view position
var vector ViewLocation;
var rotator ViewRotation;

/* This is our entry point function. This function is called periodically (from
interaction class).
*/
simulated function PostRender(Canvas C)
{
	local float XPos, YPos;
	local GBReplicationInfo MyRepInfo;
	//C.bNoSmooth = true;
	C.Style = 10000;
	SetFont(C,0,155,55);

	GetPlayerViewInformation(ViewLocation, ViewRotation);  //sets player view information

	XPos = 25;
	YPos = 25;

	if (bDrawNavPointsNames)
		DrawNavPointsNames(C);

    // UO
	if(bDisplayUsableObjects)
        DrawUsableObjects(C);

    // UO
    if(bDisplayHoldedObject)
        DrawHoldedObject(C);

	if (bDisplayHelp)
		DrawHelp(C,XPos,YPos);

    SetFont(C,0,155,55);
    if (bDisplayMyLocation)
		DrawMyLocation(C, XPos, YPos);

	foreach DynamicActors(Class'GBReplicationInfo', MyRepInfo)
	{
		if (DisplayPlayerPositions >= 1)
			DrawPlayerDebug(C, MyRepInfo);

		if (bDisplayHealthBar)
			DrawHealthBar(C, MyRepInfo);

		if (bDisplayRoute)
			DrawCustomRoute(C, MyRepInfo, XPos, YPos);
	}


	if (bDisplayPlayerList)
		DrawPlayerList(C, XPos, YPos);


	//PlayedDebug is drawn in different color
	SetFont(C,0,155,55);

	//This have to be last - large ammount of text written
	if (bDisplayInformation)
		DrawInformation(C, XPos, YPos);

	//Handling the mouse cursor.
	GBxPlayer(PlayerOwner).LastHUDSizeX = C.SizeX;
	GBxPlayer(PlayerOwner).LastHUDSizeY = C.SizeY;

	if (PlayerOwner.IsInState('PlayerMousing'))
		DrawMouseCursor(C);

	if (bDisplayLastKeyPressed)
		DisplayLastKeyPressed(C);

	DisplayGameMessage(C);
    //Draw3DLine(PlayerOwner.ViewTarget.Location + Pawn(PlayerOwner.ViewTarget).BaseEyeHeight * vect(0,0,1),PlayerOwner.FocalPoint,class'Canvas'.Static.MakeColor(255,0,0));

	//DrawNavPointsGrid(C);

	//PlayerOwner.ViewTarget.DisplayDebug(C, XPos, YPos);
}

//Just in this event function Draw3DLine works
simulated event WorldSpaceOverlays()
{
	//Additional lines for DrawPlayerPositions information
	//if (DisplayPlayerPositions >= 1)
		//DrawPlayerDebugLines();

	//TEST
	//if (bDisplayRoute)
		//DrawRoute();
}
/*
function PostBeginPlay()
{
	super.PostBeginPlay();
	PlayerOwner.myHUD = self; //why is this necesssary?
}
*/
simulated function DrawMouseCursor(Canvas C)
{
	local float XPos, YPos;
	local int i;

	C.SetDrawColor(255, 255, 255);
	C.Style = ERenderStyle.STY_Alpha;

	// find position of cursor, and clamp it to screen
	XPos = GBxPlayer(PlayerOwner).PlayerMouse.X + C.SizeX / 2.0;
	YPos = GBxPlayer(PlayerOwner).PlayerMouse.Y + C.SizeY / 2.0;
	//TODO: Weird, if here is 0 we cannot move cursor to upperleft corner


	/*if (XPos < -50)
	{
    	GBxPlayer(PlayerOwner).PlayerMouse.X -= (XPos + 50);
    	XPos = -50;
	}
	else if (XPos >= C.SizeX)
	{
    	GBxPlayer(PlayerOwner).PlayerMouse.X -= (XPos - C.SizeX);
    	XPos = C.SizeX - 1;
	}

	//TODO: Weird, if here is 0 we cannot move cursor to upperleft corner
	if (YPos < -50)
	{
    	GBxPlayer(PlayerOwner).PlayerMouse.Y -= (YPos + 50);
    	YPos = -50;
	}
	else if (YPos >= C.SizeY)
	{
    	GBxPlayer(PlayerOwner).PlayerMouse.Y -= (YPos - C.SizeY);
    	YPos = C.SizeY - 1;
	}*/

	// render mouse cursor
	C.SetPos(XPos, YPos);
	C.DrawText("X");
	//C.DrawIcon(MouseCursorTexture,1);

	// Draw Selected actor
	C.SetPos(XPos , YPos);
	C.DrawText("  " $ GBxPlayer(PlayerOwner).SelectedActor);

	// Draw Dialog
	C.DrawText(GBxPlayer(PlayerOwner).DialogBotName $ ": " $ GBxPlayer(PlayerOwner).DialogText);
	for (i=0;i<10;i++)
		C.DrawText(GBxPlayer(PlayerOwner).DialogOptions[i]);

	return;
}

 /*
function DrawEnemyName(Canvas C)
{
	local actor HitActor;
	local vector HitLocation,HitNormal,ViewPos;

	if ( PlayerOwner.bBehindView || bNoEnemyNames || (PawnOwner.Controller == None) )
		return;
	ViewPos = PawnOwner.Location + PawnOwner.BaseEyeHeight * vect(0,0,1);
	HitActor = trace(HitLocation,HitNormal,ViewPos+1200*vector(PawnOwner.Controller.Rotation),ViewPos,true);
	if ( (Pawn(HitActor) != None) && (Pawn(HitActor).PlayerReplicationInfo != None)
		&& (HitActor != PawnOwner)
		&& ( (PawnOwner.PlayerReplicationInfo.Team == None) || (PawnOwner.PlayerReplicationInfo.Team != Pawn(HitActor).PlayerReplicationInfo.Team)) )
	{
		if ( (NamedPlayer != Pawn(HitActor).PlayerReplicationInfo) || (Level.TimeSeconds - NameTime > 0.5) )
		{
			DisplayEnemyName(C, Pawn(HitActor).PlayerReplicationInfo);
			NameTime = Level.TimeSeconds;
		}
		NamedPlayer = Pawn(HitActor).PlayerReplicationInfo;
	}
}       */

//Here we are setting the size of the font according to the resolution
function SetFont(Canvas Canvas, int red, int green, int blue)
{
	local float XL;

	Canvas.Font = GetConsoleFont(Canvas);
	Canvas.Style = ERenderStyle.STY_Alpha;
    Canvas.DrawColor = class'Canvas'.Static.MakeColor(red,green,blue);
    Canvas.StrLen("TEST", XL, shift);

	if (Canvas.SizeX >= 1024 )
	{
		Canvas.Font = Canvas.MedFont;
		Canvas.FontScaleX=1;
		Canvas.FontScaleY=1;
	    Canvas.StrLen("TEST", XL, shift);
	}
}

function DisplayGameMessage(Canvas Canvas) {
 	local float myWidth, myHeight;
	local float startX, startY;
	local string message;
	local GBGameReplicationInfo MyRepInfo;

	foreach DynamicActors(Class'GBGameReplicationInfo', MyRepInfo)
	{
		message =  MyRepInfo.GetGameMessage();

		if (message != "") {
    		SetFont(Canvas, 255, 255, 255);
    		Canvas.Font = Canvas.MedFont;
			Canvas.Style = ERenderStyle.STY_Alpha;

			Canvas.StrLen(message, myWidth, myHeight);

			startX = Canvas.SizeX / 2 - myWidth / 2;
			startY = Canvas.SizeY / 2 - myHeight / 2;

			Canvas.SetPos(startX, startY);
			Canvas.DrawText(message);
		}
		break;
	}
}

function DrawCustomRoute(Canvas C, GBReplicationInfo MyRepInfo, out float ScreenLocX, out float ScreenLocY)
{
	local int i;
	local vector lastPoint, currentPoint, resVect, resNormal;
	local vector CanvasPosOne, CanvasPosTwo;


	    	for ( i=0; i<32; i++ )
	        {
    	        currentPoint = MyRepInfo.GetCustomRoute(i);

        		//ScreenLocY += shift;
				//C.SetPos(ScreenLocX, ScreenLocY);
				//C.DrawText("Route"$i$": "$currentPoint,true);

			    //if ( currentPoint == vect(0,0,0) )
			    //	break;

				if ( (lastPoint != vect(0,0,0)) && (currentPoint != vect(0,0,0))
					&& InFOV(lastPoint, PlayerOwner.FovAngle, ViewLocation, ViewRotation)
					&& InFOV(currentPoint, PlayerOwner.FovAngle, ViewLocation, ViewRotation))
				{
					//C.DrawText("From: "$lastPoint$" To: "$theBot.GetCustomRoute(i));
					CanvasPosOne = C.WorldToScreen(lastPoint);
					CanvasPosTwo = C.WorldToScreen(currentPoint);

					DrawCanvasLine(CanvasPosOne.x, CanvasPosOne.y, CanvasPosTwo.x, CanvasPosTwo.y,class'Canvas'.Static.MakeColor(255,0,0));
					//DrawDebugLine(lastPoint,currentPoint,0,255,0);

					resNormal = Normal((currentPoint - lastPoint) cross vect(0, 0, 1));
				    resVect = currentPoint - (Normal(currentPoint - lastPoint) * 14);

					//Right line of the arrow
					CanvasPosOne = C.WorldToScreen(resVect + resNormal * 5);
					CanvasPosTwo = C.WorldToScreen(currentPoint);

					DrawCanvasLine(CanvasPosOne.x, CanvasPosOne.y, CanvasPosTwo.x, CanvasPosTwo.y,class'Canvas'.Static.MakeColor(255,0,0));
					//DrawDebugLine(resVect + resNormal * 5,currentPoint,0,255,0);


					//Left line of the arrow
					CanvasPosOne = C.WorldToScreen(resVect - resNormal * 5);
					CanvasPosTwo = C.WorldToScreen(currentPoint);

					DrawCanvasLine(CanvasPosOne.x, CanvasPosOne.y, CanvasPosTwo.x, CanvasPosTwo.y,class'Canvas'.Static.MakeColor(255,0,0));
					//DrawDebugLine(resVect - resNormal * 5,currentPoint,0,255,0);


					//Line connecting the ends of our arrow lines
					//DrawDebugLine(resVect + resNormal * 5,resVect - resNormal * 5,0, 255, 0);
				}
	            lastPoint = currentPoint;
    	    }


}

//Will display Help (keys for GB and/or HUD control)
function DrawHelp(Canvas C, out float ScreenLocX, out float ScreenLocY)
{
	SetFont(C,0,155,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("GameBots 2004 HUD Help (Red features are off, green on):",true);
	ScreenLocY += shift;

	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + H - Enables/Disables this help",true);
	ScreenLocY += shift;

    if (bDisplayInformation)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + I - Enables/Disables additional info (about reachability GRID, etc.)",true);
	ScreenLocY += shift;

    if (bDisplayMyLocation)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
    C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + M - Enables/Disables my location and rotation info.",true);
	ScreenLocY += shift;

    if (bDrawNavPointsNames)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
   	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + N - Enables/Disables NavPoint names.",true);
	ScreenLocY += shift;

    SetFont(C,0,155,55);
   	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + '[' or ']' - Incerase/Decrease drawing range (" $ NavPointBeaconDrawDistance $ ")of NavPoint names.",true);
	ScreenLocY += shift;

    if (bDisplayNavCubes)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + C - Enables/Disables Navigation Points cubes visualization.",true);
	ScreenLocY += shift;

    if (DrawNavPointsGrid >= 1)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + G - Enables/Disables reachability GRID.",true);
	ScreenLocY += shift;

    if (bDisplayLastKeyPressed)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + K - Enables/Disables displaying last key press.",true);
	ScreenLocY += shift;

    if (bDisplayPlayerList)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + L - Enables/Disables Player List.",true);
	ScreenLocY += shift;

    if (DisplayPlayerPositions > 0)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + P - Cycles through additional player info modes.",true);
	ScreenLocY += shift;

    if (bDisplayRoute)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + R - Enables/Disables route drawing (when spectating the bot)",true);
	ScreenLocY += shift;

    if (bDisplayHealthBar)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + B - Enables/Disables HealthBar",true);
	ScreenLocY += shift;

    if (bDisplayTextBubble)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + U - Enables/Disables text bubbles",true);
	ScreenLocY += shift;

    if (bDisplayDebug)
	    SetFont(C,0,155,55);
	else
		SetFont(C,155,0,55);
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("CTRL + D - Enables/Disables debug information",true);
	ScreenLocY += shift;
}

//Some comment to GBHUD will be written
function DrawInformation(Canvas C, out float ScreenLocX, out float ScreenLocY)
{
	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("Reachability grid now has oriented edges. Colours info: if"$
	" one of the flags is R_PROSCRIBED or R_PLAYERONLY or R_FLY the colour of the"$
	" edge will be red regardless of any other flags. if the flag is R_JUMP and R_SPECIAL"$
	" the colour will be white. if the flag is R_JUMP the colour will be dark yellow."$
	" if the flag is not R_JUMP and if it is R_SPECIAL the colour will be blue."$
	" if none of these conditions were fullfilled yet and the flag is R_DOOR,"$
	" R_LADDER or R_SWIM the colour will be black. In other cases (flag can be"$
	" R_WALK or R_FORCED) the colour will be yellow. ",true);
	//ScreenLocY += shift;
}

//Will draw our current location, rotation and velocity on the HUD
function DrawMyLocation(canvas C, out float ScreenLocX, out float ScreenLocY)
{
	local vector PlayerLocation, PlayerVelocity;
	local rotator myRotation;
	local string PlayerRotation;

	//If we currently control Pawn, we will take its coordinates
	if (PlayerOwner.Pawn != none)
	{
		PlayerLocation = PlayerOwner.Pawn.Location;
		myRotation = PlayerOwner.Pawn.Rotation;
		//The ViewPitch is something else then Pawn rotation!!!
		myRotation.Pitch = int(PlayerOwner.Pawn.ViewPitch) * 65535/255;
		PlayerRotation = string(myRotation);
		PlayerVelocity = PlayerOwner.Pawn.Velocity;
	}
	//If are spectating someone, we will put his coordinates
	else if (PlayerOwner.ViewTarget != none)
	{
		PlayerLocation = PlayerOwner.ViewTarget.Location;
		PlayerRotation = string(PlayerOwner.ViewTarget.Rotation);
		PlayerVelocity = PlayerOwner.ViewTarget.Velocity;
	}
	//Otherwise put coordinates of the Controller class (we are spectating now)
	//don't have the body
	else
	{
		PlayerLocation = PlayerOwner.Location;
		PlayerRotation = string(PlayerOwner.Rotation);
		PlayerVelocity = PlayerOwner.Velocity;
	}

	C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("My Location: "$PlayerLocation$" My Rotation:"$PlayerRotation$" My Velocity:"$PlayerVelocity,true);
	ScreenLocY += shift;
}

//This function is called from each GBxPawn, when we may see him - this doesnt mean
//that we actually see him, when the function is called
function NotifySeePawn(Canvas C, GBxPawn P, float ScreenLocX, float ScreenLocY)
{
 	//TODO: Check also distance?
	if (!InFOV(P.Location, PlayerOwner.FovAngle, ViewLocation, ViewRotation) || !PlayerOwner.LineOfSightTo(P))
		return;

	if (bDisplayTextBubble)
		DrawTextBubble(C, P, ScreenLocX, ScreenLocY);
	if (bDisplayDebug)
		DrawGBDebug(C, P, ScreenLocX, ScreenLocY);
}

//Will draw the text bubble containing the last string the bot was sending to communicate
function DrawTextBubble(Canvas C, GBxPawn P,out float ScreenLocX,out float ScreenLocY)
{
	local float XL,YL;

	if ( P.bDrawTextBubble )
	{
		C.SetDrawColor(255, 255, 255);
		C.StrLen(P.TextBubble, XL, YL);
		C.SetPos(ScreenLocX - 0.5 * XL - 10, ScreenLocY - YL - 5);
		C.DrawRect(Texture'engine.WhiteSquareTexture',XL + 10,YL + 5);
		C.SetPos(ScreenLocX - 0.5 * XL - 10, ScreenLocY - YL - 5);
		SetFont(C,0,0,0);
		C.DrawBox(C,XL + 10,YL + 5);

		C.SetPos(ScreenLocX - 0.5*XL , ScreenLocY - YL);
		C.DrawText(P.TextBubble,false);
	}
}

function DrawGBDebug(Canvas C, GBxPawn P, float ScreenLocX, float ScreenLocY)
{
	local float XL,YL;

	//last GB command
	SetFont(C,0,255,255);
	C.StrLen(P.LastGBCommand, XL, YL);
	ScreenLocY += 50;
	C.SetPos(ScreenLocX - 0.5*XL , ScreenLocY - YL);
	C.DrawText(P.LastGBCommand,true);

	//last GB path
	C.StrLen(P.LastGBPath, XL, YL);
	ScreenLocY += 50;
	C.SetPos(ScreenLocX - 0.5*XL , ScreenLocY - YL);
	C.DrawText(P.LastGBPath,true);
}

//Will draw the bots current health ammount using text and health bar
function DrawHealthBar(Canvas C, GBReplicationInfo MyRepInfo)
{
	local texture HealthTex;
	local vector PawnLocation, CanvasBarEndLocation, CanvasHealthTexLocation;
	local float WhiteBarLength, BarLength, adrenaline;
	local int health, armor;

	HealthTex = Texture'engine.WhiteSquareTexture';
	if ( HealthTex == None)
		return;

	PawnLocation = MyRepInfo.GetLocation();
	if (!InFOV(PawnLocation, PlayerOwner.FovAngle, ViewLocation, ViewRotation) || !PlayerOwner.LineOfSightTo(MyRepInfo.getPawn())
		|| ((PlayerOwner.Pawn != none) && (PlayerOwner.Pawn == MyRepInfo.GetPawn()) )
	)
		return;
	//We will draw HealthTex a little bit higher
	PawnLocation.z += 20;
    CanvasHealthTexLocation = C.WorldToScreen(PawnLocation);

	health = MyRepInfo.getPawn().Health;
	armor = int(MyRepInfo.getPawn().ShieldStrength);
	adrenaline = MyRepInfo.getAdrenaline();

    //Health bar will be 100 ut units big
    //We want to scale the bar according to the distance
	PawnLocation.z -= 100;
	CanvasBarEndLocation = C.WorldToScreen(PawnLocation);
	WhiteBarLength = CanvasBarEndLocation.y - CanvasHealthTexLocation.y;

	C.SetPos(CanvasHealthTexLocation.x, CanvasHealthTexLocation.y + WhiteBarLength + 10);
	SetFont(C,155,0,0);
	C.DrawText(health $ "% " $ armor $ "%" $ adrenaline $ "%",true);

	C.SetPos( CanvasHealthTexLocation.x, CanvasHealthTexLocation.y );

	//First we will draw white bar showing 100 health
	SetFont(C,255,255,255);
	C.DrawTile( HealthTex, 5, WhiteBarLength, 0, 0, HealthTex.USize, HealthTex.VSize );

    BarLength = health * WhiteBarLength;
	//Then prepare everything for the second red health bar
	C.SetPos( CanvasHealthTexLocation.x, CanvasHealthTexLocation.y + Round(WhiteBarLength - BarLength / 100));
	SetFont(C,155,0,0);

	//We have to do this so (division later in DrawTile fc) because we loose
	//everything behind . because of replication (all floats truncated)
	if (health > 0)
		C.DrawTile(HealthTex, 5, Round(BarLength / 100), 0, 0, HealthTex.USize, HealthTex.VSize );

	C.SetPos( CanvasHealthTexLocation.x + 5, CanvasHealthTexLocation.y );
	//First we will draw white bar showing 100 armor
	SetFont(C,255,255,255);
	C.DrawTile( HealthTex, 5, WhiteBarLength, 0, 0, HealthTex.USize, HealthTex.VSize );

	//Then prepare everything for the second orange armor bar
	BarLength = armor * WhiteBarLength;

	C.SetPos( CanvasHealthTexLocation.x + 5,  CanvasHealthTexLocation.y + Round(WhiteBarLength - BarLength / 100));
	SetFont(C,255,140,0);

	if (armor > 0)
		C.DrawTile(HealthTex, 5, Round(BarLength / 100), 0, 0, HealthTex.USize, HealthTex.VSize );

	C.SetPos( CanvasHealthTexLocation.x + 10, CanvasHealthTexLocation.y );
	//First we will draw white bar showing 100 armor
	SetFont(C,255,255,255);
	C.DrawTile( HealthTex, 5, WhiteBarLength, 0, 0, HealthTex.USize, HealthTex.VSize );

	//Then prepare everything for the second orange armor bar
	BarLength = adrenaline * WhiteBarLength;

	C.SetPos( CanvasHealthTexLocation.x + 10,  CanvasHealthTexLocation.y + Round(WhiteBarLength - BarLength / 100));
	SetFont(C,0,0,200);

	if (adrenaline > 0)
		C.DrawTile(HealthTex, 5, Round(BarLength / 100), 0, 0, HealthTex.USize, HealthTex.VSize );
}

/*
	Will draw some debug information for each bot or player in the game (distance, name)
	The information about distance and bots name will be drawn even if the player/bot
	are behind wall (good if we want to find them in the map).

	The lines drawn: speed vector (red), bot focus (white) and his field of view (green).
*/

function DrawPlayerDebug(Canvas Canvas, GBReplicationInfo MyRepInfo)
{
	//some needed vectors
	local vector PawnVelocity, PawnPosition;
	//some vectors for canvas position counting
	local vector CanvasPawnPosition, CanvasPawnFocus;

	local vector CanvasPosTwo;
	local string PlayerName, FocusName;
	local rotator fovLimit, PawnRotation;
	local float XL, YL;

		//We want to show here just relevant information
		if (!MyRepInfo.PawnIsNone() && (MyRepInfo.MyPRI != PlayerOwner.PlayerReplicationInfo) )
		{
			/*if ( (PlayerOwner.ViewTarget != none) && (PlayerOwner.ViewTarget.Controller != none) && (PlayerOwner.ViewTarget.Controller.PlayerReplicationInfo != none ) && (PlayerOwner.ViewTarget.Controller.PlayerReplicationInfo == MyRepInfo.MyPRI) )
			{
				continue;
			}*/
			PawnPosition = MyRepInfo.GetLocation();

			if (InFOV(PawnPosition, PlayerOwner.FovAngle, ViewLocation, ViewRotation))
			{

				PlayerName = MyRepInfo.MyPRI.GetHumanReadableName();

				PawnVelocity = MyRepInfo.GetVelocity();

				//Need to draw the name and distance properly
                CanvasPawnPosition = Canvas.WorldToScreen(PawnPosition);


				//Add information about distance and PlayerName
				if (MyRepInfo.MyPRI.Team != none)
				{
					if (MyRepInfo.MyPRI.Team.TeamIndex == 0)
						SetFont(Canvas,200,55,55); //red
					else
						SetFont(Canvas,55,55,200);  //blue
				}
				else
					SetFont(Canvas,200,55,55);

				PlayerName = VSize(PawnPosition - ViewLocation) $ " " $ PlayerName;
				Canvas.StrLen(PlayerName, XL, YL);
				Canvas.SetPos(CanvasPawnPosition.x - 0.5*XL , CanvasPawnPosition.y - YL);
				Canvas.DrawText(PlayerName,true);

				if (MyRepInfo.MyPRI.HasFlag != none) {
					SetFont(Canvas,255,255,255);
					Canvas.SetPos(CanvasPawnPosition.x - 0.5 * XL - 10, CanvasPawnPosition.y - YL - 5);
                	Canvas.DrawBox(Canvas,XL + 10,YL + 5);
                }

				//draw velocity line
				if (InFOV(PawnPosition + PawnVelocity, PlayerOwner.FovAngle, ViewLocation, ViewRotation))
				{
					CanvasPosTwo = Canvas.WorldToScreen(PawnPosition + PawnVelocity);

					DrawCanvasLine(CanvasPawnPosition.x, CanvasPawnPosition.y, CanvasPosTwo.x, CanvasPosTwo.y,class'Canvas'.Static.MakeColor(255,0,0));
				}
                //DrawDebugLine(PawnPosition, PawnPosition + PawnVelocity, 255,0,0);

				//Draw3DLine(PawnPosition,PawnPosition + MyRepInfo.GetVelocity(),class'Canvas'.Static.MakeColor(255,0,0));


				if (DisplayPlayerPositions >= 2)
				{
					//DrawFocus
					CanvasPawnFocus = Canvas.WorldToScreen(MyRepInfo.GetFocus());
					FocusName = MyRepInfo.GetFocusName();

					if (InFOV(MyRepInfo.GetFocus(), PlayerOwner.FovAngle, ViewLocation, ViewRotation))
					{
						CanvasPosTwo = Canvas.WorldToScreen(MyRepInfo.GetFocus());

						DrawCanvasLine(CanvasPawnPosition.x, CanvasPawnPosition.y, CanvasPosTwo.x, CanvasPosTwo.y,class'Canvas'.Static.MakeColor(255,255,255));
					}
        	        //DrawDebugLine(PawnPosition, MyRepInfo.GetFocus(),255,255,255);

            	    if (FocusName != "")
                	{
	                	Canvas.StrLen(FocusName, XL, YL);
						Canvas.SetPos(CanvasPawnFocus.x - 0.5*XL , CanvasPawnFocus.y - YL);
						Canvas.DrawText(FocusName,true);
					}

					//DrawFOV - approx right now
					fovLimit.pitch = 0;
					fovLimit.Yaw = ((107 / 2) * 182.1);
					fovLimit.Roll = 0;

					PawnRotation = MyRepInfo.GetRotation();

					//First FOV line
					if (InFOV(PawnPosition + vector(PawnRotation - fovLimit) * 300, PlayerOwner.FovAngle, ViewLocation, ViewRotation))
					{
						CanvasPosTwo = Canvas.WorldToScreen(PawnPosition + vector(PawnRotation - fovLimit) * 300);

						DrawCanvasLine(CanvasPawnPosition.x, CanvasPawnPosition.y, CanvasPosTwo.x, CanvasPosTwo.y,class'Canvas'.Static.MakeColor(255,255,0));
					}
	       	        //DrawDebugLine(PawnPosition, PawnPosition + vector(PawnRotation - fovLimit) * 300, 255,255,0);

					//Second FOV line
					if (InFOV(PawnPosition + vector(PawnRotation + fovLimit) * 300, PlayerOwner.FovAngle, ViewLocation, ViewRotation))
					{
						CanvasPosTwo = Canvas.WorldToScreen(PawnPosition + vector(PawnRotation + fovLimit) * 300);

						DrawCanvasLine(CanvasPawnPosition.x, CanvasPawnPosition.y, CanvasPosTwo.x, CanvasPosTwo.y,class'Canvas'.Static.MakeColor(255,255,0));
					}
	       	        //DrawDebugLine(PawnPosition, PawnPosition + vector(PawnRotation + fovLimit) * 300, 255,255,0);

				}
			}
		}

}
	/*
function DrawPlayerDebugLines()
{
	local GBReplicationInfo MyRepInfo;
	//some needed vectors
	local vector PawnVelocity, PawnPosition, resNormal, resVect;
	//some vectors for canvas position counting
	local vector FirstLine, SecondLine, CanvasPawnPosition, CanvasPawnVelocity, CanvasPawnFocus, CanvasFOVLimit;

	local string PlayerName, FocusName;
	local rotator fovLimit, PawnRotation;
	local int bIsCamera;
	local float XL, YL;

	foreach DynamicActors(Class'GBReplicationInfo', MyRepInfo)
	{
		//We want to show here just relevant information
		if (!MyRepInfo.PawnIsNone() && (MyRepInfo.MyPRI != PlayerOwner.PlayerReplicationInfo) )
		{

			PawnPosition = MyRepInfo.GetLocation();

			if (inFront(PawnPosition, ViewLocation, ViewRotation, bIsCamera))
			{
				//Draw speed vector
				PawnVelocity = MyRepInfo.GetVelocity();
				Draw3DLine(PawnPosition,PawnPosition + PawnVelocity,class'Canvas'.Static.MakeColor(255,0,0));

				//We don't want to draw arrow if the velocity is too small...
				if ( VSize( (Normal(PawnVelocity) * 7) ) < VSize( PawnVelocity ) )
				{
                	resNormal = Normal(PawnVelocity cross vect(0, 0, 1));
					resVect = (PawnPosition + PawnVelocity) - (Normal(PawnVelocity) * 7);

					FirstLine = resVect + resNormal * 3;
					SecondLine = resVect - resNormal * 3;
					//First arrow line
                    Draw3DLine(FirstLine, PawnPosition + PawnVelocity,class'Canvas'.Static.MakeColor(255,0,0));
                    //Second arrow line
                    Draw3DLine(SecondLine, PawnPosition + PawnVelocity,class'Canvas'.Static.MakeColor(255,0,0));
				}

				if (DisplayPlayerPositions >= 2)
				{
					//DrawFocus
                    Draw3DLine(PawnPosition, MyRepInfo.GetFocus(),class'Canvas'.Static.MakeColor(255,255,255));

					//DrawFOV - approx right now
					fovLimit.pitch = 0;
					fovLimit.Yaw = ((107 / 2) * 182.1);
					fovLimit.Roll = 0;

					PawnRotation = MyRepInfo.GetRotation();

					//First FOV line
	       	        Draw3DLine(PawnPosition,PawnPosition + vector(PawnRotation - fovLimit) * 300,class'Canvas'.Static.MakeColor(255,255,0));
					//Second FOV line
	       	        Draw3DLine(PawnPosition,PawnPosition + vector(PawnRotation + fovLimit) * 300,class'Canvas'.Static.MakeColor(255,255,0));
				}
			}
		}
	}



}   */

/* Will go through all GBReplicationInfo classes and will display the information
	about players currently on the server.
*/
function DrawPlayerList(canvas C, out float ScreenLocX, out float ScreenLocY)
{
	local GBReplicationInfo MyRepInfo;

    C.SetPos(ScreenLocX, ScreenLocY);
	C.DrawText("Player List: ",true);

	foreach DynamicActors(Class'GBReplicationInfo', MyRepInfo)
	{
		//We want to show here just relevant information
		if (!MyRepInfo.PawnIsNone())
		{
			ScreenLocY += shift;
			C.SetPos(ScreenLocX, ScreenLocY);
			C.DrawText("Name: "$MyRepInfo.MyPRI.GetHumanReadableName() $
				" Location: "$MyRepInfo.GetLocation() $
				" Rotation: "$MyRepInfo.GetRotation() $
				" Velocity: "$MyRepInfo.GetVelocity(),true);
		}
	}

	ScreenLocY += shift;
}

function DisplayLastKeyPressed(Canvas C) {
	local float myWidth, myHeight, startX, startY;

	if (lastKeyPressedMessage.EndOfLife > Level.TimeSeconds) {
		C.StrLen(lastKeyPressedMessage.StringMessage, myWidth, myHeight);

		startX = C.SizeX - 80 - myWidth;
		startY = 40 - myHeight;
		C.SetPos(startX, startY);
		SetFont(C,255,255,255);
    	C.Font = C.MedFont;
		C.Style = ERenderStyle.STY_Alpha;
		C.DrawText(lastKeyPressedMessage.StringMessage);
	}
}

//Will go through all navigation points in the level and find those we can see
//and display their Ids
function DrawNavPointsNames(canvas Canvas)
{
	local NavigationPoint N;
	local vector CanvasPosition;
	local vector distance;

	local float XL,YL,floatDist;

	for ( N=Owner.Level.NavigationPointList; N!=None; N=N.NextNavigationPoint )
	{
		distance = N.Location - ViewLocation;
		floatDist = VSize(distance);
		if (floatDist <= NavPointBeaconDrawDistance) {
			if(InFOV(N.Location, PlayerOwner.FovAngle, ViewLocation, ViewRotation) && PlayerOwner.FastTrace(N.Location, ViewLocation) )
			{
				CanvasPosition = Canvas.WorldToScreen(N.Location);
				//This will draw the game Id of the NavPoint
				Canvas.StrLen(string(N), XL, YL);
				Canvas.SetPos(CanvasPosition.X - 0.5*XL , CanvasPosition.Y - YL);
				Canvas.DrawText(string(N),true);
			}
		}
	}
}

// UO
function DrawUsableObjects(canvas Canvas) {
   	local UsableObject O;
	local vector CanvasPosition;
	local vector distance;
	local string objName;

	local float XL,YL,floatDist;

	SetFont(Canvas,255,255,255);
    Canvas.Font = Canvas.MedFont;
	Canvas.Style = ERenderStyle.STY_Alpha;

	foreach Owner.Level.ZoneActors(class'UsableObject', O) {
	Log("Iteration");

		distance = O.Location - ViewLocation;
		floatDist = sqrt(square(O.Location.x - ViewLocation.x) + square(O.Location.y - ViewLocation.y) + square(O.Location.z - ViewLocation.z));
		Log("floatDist = " $ floatDist $ " NavDrawDist = " $ NavPointBeaconDrawDistance);
		if (floatDist <= NavPointBeaconDrawDistance) {
			if(InFOV(O.Location, PlayerOwner.FovAngle, ViewLocation, ViewRotation) && PlayerOwner.FastTrace(O.Location, ViewLocation) )
			{
                objName = O.objectName;
				CanvasPosition = Canvas.WorldToScreen(O.Location);

				Canvas.StrLen(objName, XL, YL);
				Canvas.SetPos(CanvasPosition.X - 0.5*XL , CanvasPosition.Y - YL);
				Canvas.DrawText(objName,true);
			}
		}

	}

}

// UO
function DrawHoldedObject(canvas Canvas) {

    local GBxPlayer gbxCauser;
    local GBxPlayer.ItemEnum item;
    local string displayString;
    local float myWidth, myHeight, startX, startY;
    local string unrealId;

    if(PlayerOwner.IsA('GBxPlayer')) {
      unrealId = PlayerOwner $ PlayerOwner.PlayerReplicationInfo.PlayerID;
      gbxCauser = GBxPlayer(PlayerOwner);

      item = gbxCauser.holdedItem;
      displayString = " Currently holding: " $ gbxCauser.getItemLabel(item);

      Canvas.StrLen(displayString, myWidth, myHeight);

      startX = Canvas.SizeX - 80 - myWidth;
	  startY = 40 - myHeight;
	  Canvas.SetPos(startX, startY);
	  SetFont(Canvas,255,255,255);
      Canvas.Font = Canvas.MedFont;
	  Canvas.Style = ERenderStyle.STY_Alpha;
	  Canvas.DrawText(displayString);
    }
}

/* Returns our current view - camera location, rotation and bool (by int) bIsCamera if we are currently
	spectating and don't have any player selected.
*/
function GetPlayerViewInformation(out vector CameraLocation, out rotator CameraRotation)
{
	if (PlayerOwner.Pawn != none)
	{
		CameraLocation = PlayerOwner.Pawn.Location;
		CameraRotation = PlayerOwner.Pawn.Rotation;
		CameraRotation.Pitch = int(PlayerOwner.Pawn.ViewPitch) * 65535/255;
	}
	else if (PlayerOwner.ViewTarget != none)
	{
		CameraLocation = PlayerOwner.CalcViewLocation;
		CameraRotation = PlayerOwner.CalcViewRotation;
	}
	else
	{
		CameraLocation = PlayerOwner.Location;
		CameraRotation = PlayerOwner.Rotation;
	}
}

// True if location loc is in camera's field of view. Does not take into account occlusion by geometry!
// Possible optimization: Precompute cos(obsController.FovAngle / 2) for InFOV - careful if it can change.
function bool InFOV(vector loc, float FovAngle, vector CameraLocation, rotator CameraRotation) {
	local vector view;   // vector pointing in the direction obsController is looking.
	local vector target; // vector from obsController's position to the target location.

	view = vector(CameraRotation);

	target = loc - CameraLocation;

	return Acos(Normal(view) dot Normal(target)) * 57.2957795 < FovAngle / 2; // Angle between view and target is less than FOV
	// 57.2957795 = 180/pi = 1 radian in degrees  --  convert from radians to degrees
}

defaultproperties
{
	bDrawNavPointsNames = true;
	bDisplayPlayerList = true;
	bDisplayHelp = true;
	bDisplayHealthBar = false;
	bDisplayTextbubble = true;
	bDisplayMyLocation = true;
	bDisplayNavCubes = true;
	bDisplayLastKeyPressed = false;
	NavPointBeaconDrawDistance = 500;
	bDisplayUsableObjects = false;
	bDisplayHoldedObject = false;
	MouseCursorTexture=Texture'Crosshairs.HUD.Crosshair_Cross1'
}
