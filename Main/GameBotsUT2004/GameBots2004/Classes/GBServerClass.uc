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


class GBServerClass extends TcpLink
	config(GameBots2004);

//Port we want to connect to
var int DesiredPort;

//Port we are connected to
var int ListenPort;

var config int MaxConnections;

var bool bBound;
var config bool bDebug;

var int ConnectionCount;

//List of all connections spawned by this server
var GBClientClass ChildList;

var bool bClosed;

//shouldn't happen
event ReceivedText( string Text )
{
    if(bDebug)
    	log("RecievedTest in Server - "$Text);
}

//should never happen - accepted connections should be forwarded to a botconnection
event Accepted()
{
    if(bDebug)
    	log("Accepted connection in BotServer");
}

//called everytime a new botconnection is spawned
event GainedChild( Actor C )
{
	local GBClientClass NewChild;
	local GBClientClass IteratorChild;

	if (bDebug)
		log("We are in gained child 1, it is "$ C);

	Super.GainedChild(C);

	log("We are in gained child 2, it is "$ C);

	if (C.IsA('GBClientClass')) {
		NewChild = GBClientClass(C);

	}else {
		log("This should never happen. In ChainedChild event 1");
		return;
	}

	if (ConnectionCount == 0) {
		ChildList = NewChild;

	}else {
		IteratorChild = ChildList;
		while (IteratorChild.Next != None) { //add child to the end of the list
			IteratorChild = IteratorChild.Next;
		}
		IteratorChild.Next = NewChild;
	}

	ConnectionCount++;

	// if too many connections, close down listen.
	if(MaxConnections > 0 && ConnectionCount >= MaxConnections && LinkState == STATE_Listening)
	{
		if(bDebug)
			Log("BotServer: Too many connections - closing down Listen.");
		Close();
	}
}

event LostChild( Actor C )
{
	local GBClientClass LostedChild;
	local GBClientClass IteratorChild, Previous;

	Super.LostChild(C);

	if (C.IsA('GBClientClass')) {
		LostedChild = GBClientClass(C);

	}
	else
	{
		log("This should never happen. In LostChild event 0");
		return;
	}

	if (ConnectionCount == 0)
	{
		log("This should never happen. 1 in event LostChild");
		return;
	}

	if (ConnectionCount == 1) {
		ChildList = None;
	}else {
		IteratorChild = ChildList;
		Previous = None;

		while (IteratorChild != LostedChild) {
			Previous = IteratorChild;
			IteratorChild = IteratorChild.Next;
		}
		if (IteratorChild == None)
		{
			log("This should never happen. 2 in event LostChild");
			return;
		}
		else {
			if (Previous == None) {
				ChildList = IteratorChild.Next;
			}else {
				Previous.Next = IteratorChild.Next;
			}

		}

	}

	ConnectionCount--;

	log("Lost Child "$C);
	// if closed due to too many connections, start listening again.
	if (ConnectionCount < MaxConnections && LinkState != STATE_Listening)
	{
		if (!bClosed)
		{
			Listen();
			if (bDebug)
				log("Start listening again LinkState: "$LinkState$"ConnectionCount: "$ConnectionCount);
		}
	}

}

function Initiate()
{
	log("GBServerClass - Initate() should not be called");
}

function BeginPlay()
{
	Super.BeginPlay();
}

defaultproperties
{
	MaxConnections=3
	bClosed=false
	AcceptClass=Class'ControlConnection'
}
