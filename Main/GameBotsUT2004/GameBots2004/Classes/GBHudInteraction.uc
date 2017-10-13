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


// Interaction class creates custom GB Hud and handles keyInput
// The reachability grid is drawn here
class GBHUDInteraction extends Interaction;

//Holds our GBHUD
var GBHUD MyHUD;

var bool bNavPointsGridDrawn;

var name LastPlayerState;

var private bool bPressedCtrl;

var array<PathMarker> pathMarkers;

//for exporting key presses
var EInputKey lastInputKey;
var EInputAction lastInputAction;

//Called as the first function. Prepare everything here.
event Initialized()
{
	bNavPointsGridDrawn = false;
	InitHud();
	if (MyHUD.DrawNavPointsGrid >= 1)
	{
		DrawNavPointsGrid();
	}
	if (MyHUD.bDisplayNavCubes)
	{
		SpawnNavCubes();
	}
}

//Called when the server will change the map - we will have to destroy everything
event NotifyLevelChange()
{
	ViewportOwner.Actor.ClearStayingDebugLines();
	bNavPointsGridDrawn = false;

	MyHUD.Destroy();
    if (ViewportOwner.Actor.isA('GBxPlayer'))
    {
		GBxPlayer(ViewportOwner.Actor).GBHUD = None;
	}
    ViewportOwner.InteractionMaster.RemoveInteraction(self);
	/*
	InitHud();
	if (DrawNavPointsGrid >= 1)
	{
		DrawNavPointsGrid();
	}
	*/
}

function SpawnNavCubes() {
	local NavigationPoint N;
	local int i;

	i = 0;
	for ( N=ViewportOwner.Actor.Level.NavigationPointList; N!=None; N=N.NextNavigationPoint )
	{
    	if(!N.IsA('InventorySpot')) {
   			pathMarkers[i] = ViewportOwner.Actor.Level.Spawn(class'GameBots2004.PathMarker',N,,N.Location);
   			pathMarkers[i].bHidden = false;
   			i++;
		}
	}
}

function DestroyNavCubes() {
	local int i;
	for (i=0;i<pathMarkers.Length;i++) {
 		if (pathMarkers[i] != none) {
			pathMarkers[i].Destroy();
		}
	}
}

//Here we will spawn our GBHUD and set it also to the Player
function InitHud()
{
    MyHUD = ViewportOwner.Actor.Spawn(class'GameBots2004.GBHud', ViewportOwner.Actor);
    //ViewportOwner.Actor.myHUD = MyHUD;

    if (ViewportOwner.Actor.isA('GBxPlayer'))
    {
		GBxPlayer(ViewportOwner.Actor).GBHUD = MyHUD;
		//GBxPlayer(ViewportOwner.Actor).GBHUDInter = self;
	}
}

//Here we will draw the navigation points GRID in the game by DrawStayingDebugLine function
function DrawNavPointsGrid()
{
	local NavigationPoint N;
	local int i,j, PathListLength, red, green, blue;
	local int bPlayersOnly, bForced, bProscribed, bLadder, bSpecial, bDoor, bJump, bSwim, bFly, bWalk;
	local vector resVect, resNormal;

	for ( N=ViewportOwner.Actor.Level.NavigationPointList; N!=None; N=N.NextNavigationPoint )
	{
		i = 0;
		PathListLength = N.PathList.Length;
		for (i=0; i < PathListLength; i++)
		{
			//First we will get the flags of this line between two NavPoints
            GetFlags(N.PathList[i].reachFlags, bPlayersOnly, bForced, bProscribed, bLadder, bSpecial, bDoor, bJump, bSwim, bFly, bWalk);


			//Darker Yellow
			red = 255;
			green = 255;
			blue = 0;

			//Here we will design what color we will use - hierarchical
			if ( (bFly == 1) || (bProscribed == 1) || (bPlayersOnly == 1) )
			{
				//Red
				red = 255;
				green = 0;
				blue = 0;
			}
			else if ((bJump == 1) && (bSpecial == 1))
            {
            	//White
				red = 255;
				green = 255;
				blue = 255;
			}
			else if ( bSpecial == 1 )
			{
				//Blue
				red = 0;
				green = 0;
				blue = 255;
			}
			else if (bJump == 1)
			{
				//Yellow with some blue :-)
				red = 255;
				green = 255;
				blue = 150;
			}
			else if ( bDoor == 1 || bLadder == 1 || bSwim == 1 )
			{
				//Black
				red = 0;
				green = 0;
				blue = 0;
			}

			//Draw the line between two NavPoints
			ViewportOwner.Actor.DrawStayingDebugLine(N.Location,N.PathList[i].End.Location,red,green,blue);

			//We need this, so we can draw the arrow at the end of the line correctly
			resNormal = Normal((N.PathList[i].End.Location - N.Location) cross vect(0, 0, 1));
			resVect = N.PathList[i].End.Location - (Normal(N.PathList[i].End.Location - N.Location) * 14);

			if (MyHUD.DrawNavPointsGrid >= 2) {
				//Right line of the width of the edge
				ViewportOwner.Actor.DrawStayingDebugLine(N.Location + resNormal * N.PathList[i].CollisionRadius,N.PathList[i].End.Location + resNormal * N.PathList[i].CollisionRadius,red,green,blue);
				//Left line of the width of the edge
				ViewportOwner.Actor.DrawStayingDebugLine(N.Location - resNormal * N.PathList[i].CollisionRadius,N.PathList[i].End.Location - resNormal * N.PathList[i].CollisionRadius,red,green,blue);
			}

			//Right line of the arrow
			ViewportOwner.Actor.DrawStayingDebugLine(resVect + resNormal * 5,N.PathList[i].End.Location,red,green,blue);
			//Left line of the arrow
			ViewportOwner.Actor.DrawStayingDebugLine(resVect - resNormal * 5,N.PathList[i].End.Location,red,green,blue);
			//Line connecting the ends of our arrow lines
			ViewportOwner.Actor.DrawStayingDebugLine(resVect + resNormal * 5,resVect - resNormal * 5,red, green, blue);
			//GBHUD(MyHUD).DrawLine(N.Location,N.PathList[i].End.Location);

			// vizualize jump vector
			if (N.PathList[i].End.IsA('JumpDest')) {
				for (j = 0; j < JumpDest(N.PathList[i].End).NumUpstreamPaths; j++) {
					//Searching the record which refers to the path we are now exporting
					if (JumpDest(N.PathList[i].End).UpstreamPaths[j].Start == N) {
						ViewportOwner.Actor.DrawStayingDebugLine(N.Location,N.Location + JumpDest(N.PathList[i].End).NeededJump[j],255,0,0);
					}
				}

			}
		}

	}
	bNavPointsGridDrawn = true;

}

//Will return the bools from supported flag int
function GetFlags(int flag, out int bPlayerOnly, out int bForced,
out int bProscribed, out int bLadder, out int bSpecial,
out int bDoor, out int bJump, out int bSwim, out int bFly, out int bWalk)
{
	bPlayerOnly = 0;
	bForced = 0;
	bProscribed = 0;
	bLadder = 0;
	bSpecial = 0;
	bDoor = 0;
	bJump = 0;
	bSwim = 0;
	bFly = 0;
	bWalk = 0;

	if ( flag >= 512 )
	{
		flag -= 512;
		bPlayerOnly = 1;
	}
    if ( flag >= 256 )
	{
		flag -= 256;
		//It is FORCED path
		bForced = 1;
	}
	if ( flag >= 128)
	{
		flag -= 128;
		bProscribed = 1;
	}
	if ( flag >= 64)
	{
		flag -= 64;
		bLadder = 1;
	}
	if ( flag >= 32)
	{
		flag -= 32;
		bSpecial = 1;
	}
	if ( flag >= 16)
	{
		flag -= 16;
		bDoor = 1;
	}
	if ( flag >= 8)
	{
		flag -= 8;
		bJump = 1;
	}
	if ( flag >= 4)
	{
		flag -= 4;
		bSwim = 1;
	}
	if ( flag >= 2)
	{
		flag -= 2;
		bFly = 1;
	}
	if ( flag >= 1 )
	{
		flag -= 1;
		bWalk = 1;
	}

}

//exporting key events
function SendKeyEvent(EInputKey key, EInputAction action)
{
    GBxPlayer(ViewportOwner.Actor).PlayerKeyEvent(key, action);
}

//Here we handle our GB key presses
function bool KeyEvent(EInputKey InputKey, EInputAction InputAction, FLOAT Delta )
{
    // don't send mouse events
	if (InStr(GetEnum(enum'EInputKey', InputKey), "Mouse") == -1)
	{
		if ((InputKey != lastInputKey) || (InputAction != lastInputAction)) {
        	SendKeyEvent(InputKey, InputAction);
        	lastInputKey = InputKey;
        	lastInputAction = InputAction;
        	if (InputAction == IST_Press) {
	        	MyHUD.lastKeyPressedMessage.StringMessage = "Pressed " $ Mid( GetEnum(enum'EInputKey', InputKey), 3 );
    	    	MyHUD.lastKeyPressedMessage.EndOfLife = MyHUD.Level.TimeSeconds + 3;
			}
        }
	}

	if (InputKey == IK_Ctrl)
    	if (InputAction == IST_Press)
			bPressedCtrl = True;
    	else if (InputAction == IST_Release)
			bPressedCtrl = False;

	 if (bPressedCtrl && InputAction == IST_Press)
	 {
		switch (InputKey)
		{
			case IK_B:
				MyHUD.bDisplayHealthBar = !MyHUD.bDisplayHealthBar;
			break;
			case IK_C:
				MyHUD.bDisplayNavCubes = !MyHUD.bDisplayNavCubes;
				if (MyHUD.bDisplayNavCubes)
					SpawnNavCubes();
				else
					DestroyNavCubes();
			break;
			case IK_D:
				MyHUD.bDisplayDebug = !MyHUD.bDisplayDebug;
			break;
		    case IK_G:
				MyHUD.DrawNavPointsGrid += 1;
				if (MyHUD.DrawNavPointsGrid > 2)
					MyHUD.DrawNavPointsGrid = 0;

		    	if (MyHUD.DrawNavPointsGrid >= 1) {
					if (!bNavPointsGridDrawn) {
						DrawNavPointsGrid();
					} else {
						ViewportOwner.Actor.ClearStayingDebugLines();
						DrawNavPointsGrid();
					}
				} else {
					if (bNavPointsGridDrawn) {
						ViewportOwner.Actor.ClearStayingDebugLines();
						bNavPointsGridDrawn = false;
					}
				}
			break;
		    case IK_H:
		    	MyHUD.bDisplayHelp = !MyHUD.bDisplayHelp;
			break;
			case IK_I:
				MyHUD.bDisplayInformation = !MyHUD.bDisplayInformation;
			break;
			case IK_K:
				MyHUD.bDisplayLastKeyPressed = !MyHUD.bDisplayLastKeyPressed;
			break;
			case IK_L:
				MyHUD.bDisplayPlayerList = !MyHUD.bDisplayPlayerList;
			break;
			case IK_M:
				MyHUD.bDisplayMyLocation = !MyHUD.bDisplayMyLocation;
			break;
			case IK_N:
				MyHUD.bDrawNavPointsNames = !MyHUD.bDrawNavPointsNames;
			break;
			case IK_P:
				MyHUD.DisplayPlayerPositions += 1;
				if (MyHUD.DisplayPlayerPositions > 2)
					MyHUD.DisplayPlayerPositions = 0;
			break;
			case IK_R:
				MyHUD.bDisplayRoute = !MyHUD.bDisplayRoute;
			break;
			case IK_U:
				MyHUD.bDisplayTextBubble = !MyHUD.bDisplayTextBubble;
			break;
    		case IK_LeftBracket :
				if (MyHUD.NavPointBeaconDrawDistance < 4000)
					MyHUD.NavPointBeaconDrawDistance += 100;
			break;
		    case IK_RightBracket:
				if (MyHUD.NavPointBeaconDrawDistance > 100)
					MyHUD.NavPointBeaconDrawDistance -= 100;
			break;
			case IK_Shift:
				if (!ViewportOwner.Actor.IsInState('PlayerMousing'))
				{
					LastPlayerState = ViewportOwner.Actor.GetStateName();
					ViewportOwner.Actor.GotoState('PlayerMousing');
				}
				else
				{
					ViewportOwner.Actor.GotoState(LastPlayerState);
				}

			break;
		}
		return true; //This means that this key combinations won't be parsed by other KeyEvents
	}

	if (ViewportOwner.Actor.IsInState('PlayerMousing'))
	{
		if (InputAction == IST_Press)
	 	{

			switch (InputKey)
			{
				case IK_0:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(0);
					return true;
				break;
				case IK_1:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(1);
					return true;
				break;
				case IK_2:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(2);
					return true;
				break;
				case IK_3:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(3);
					return true;
				break;
				case IK_4:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(4);
					return true;
				break;
				case IK_5:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(5);
					return true;
				break;
				case IK_6:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(6);
					return true;
				break;
				case IK_7:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(7);
					return true;
				break;
				case IK_8:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(8);
					return true;
				break;
				case IK_9:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(9);
					return true;
				break;
				case IK_T:
					GBxPlayer(ViewportOwner.Actor).HandleKeyInput(10);
					return true;
			    break;
			    case IK_E:
                    GBxPlayer(ViewportOwner.Actor).handleEventCommand(-1);
                    return true;
                break;
                case IK_G:
                    GBxPlayer(ViewportOwner.Actor).handleEventCommand(1);
                    return true;
                break;
                case IK_F:
                    GBxPlayer(ViewportOwner.Actor).handleEventCommand(2);
                    return true;
                break;
                case IK_Q:
                    GBxPlayer(ViewportOwner.Actor).handleEventCommand(0);
                    return true;
                break;
			}

		}
	}
    return super.KeyEvent(InputKey, InputAction, Delta );
}

/* This function is called by engine, we will forward this to our HUD, so we can
	draw information on it.
*/
function PostRender( canvas Canvas )
{
	if (MyHUD != none)
    	MyHUD.PostRender(Canvas);
/*    if (MyHUD != none)
    	MyHUD.WorldSpaceOverlays();
    	*/
}

defaultproperties
{
    bVisible=true
    bActive=true
}
