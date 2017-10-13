class UsableObject extends Actor placeable;

enum CustomEvent { Event1, Event2};

var int counter;
var (UsableObject) int counterLimit;
var (UsableObject) string objectName;
var bool enablePickup;

event UsedBy ( Pawn user )
{
     // processCustomEvent(Event1, user);
}

event TakeDamage ( int Damage, Pawn EventInstigator, vector HitLocation, vector Momentum, class<DamageType> DamageType )
{
     // processCustomEvent(Event2, EventInstigator);
}

function PostBeginPlay()
{
  Super.PostBeginPlay(); // Run the super class function (Mutator.PostBeginPlay).
  counter = 0;
}

function bool canBePickedUp() {return self.enablePickup;}
function GBxPlayer.ItemEnum getItemType();
function class<UsableObject> getClass() {
  return self.class;
}

function handleCustomEvent( CustomEvent e, GBxPlayer causer);
function processCustomEvent( CustomEvent e, Pawn causer)
{

 local string report;
 local string unrealId;
 local string actionLabel;
 local GBxPlayer gbxCauser;

 local string logString;

 local GBxPlayer.ItemEnum item;


 unrealId = causer.Controller  $ causer.Controller.PlayerReplicationInfo.PlayerID;
//  unrealId = GetArgVal("Id");
actionLabel = getActionLabel(e, causer);
 if( actionLabel == "NoAction")
     return;

 report = "Player " $ unrealId $ " interacted with object " $ objectName $ " action: " $ actionLabel;
 Log(report);

 if(causer.Controller.IsA('GBxPlayer'))
 {
  gbxCauser = GBxPlayer(causer.Controller);

  item = gbxCauser.getHoldedItem("UO");
  logString = "Currently holding: " $ item;
  Log(logString);

  handleCustomEvent( e, gbxCauser);
 }

}
function string getActionLabel( CustomEvent e, Pawn causer);
function string getObjectName() {
         return objectName;
}
function testCommand() {
  local string cmd;

  cmd =  "TEST COMMAND";
  Log(getObjectName());
}

function handleEventCommand(int e, Pawn causer) {
  Log("Hadle event Command");
  Log(e);
  switch(e) {
    case 1 :
         processCustomEvent(Event1, causer);
         break;
    case 2 :
         processCustomEvent(Event2, causer);
         break;
  }
}

function pickup() {
  Destroy();
}

defaultproperties
{
 bCollideWhenPlacing = true;
 bEdShouldSnap = true;
 bHidden = false;
 bHiddenEd = false;
 bMovable = false;
 bBlockActors = true;
 bBlockNonZeroExtentTraces = true;
 bBlockZeroExtentTraces = true;
 bProjTarget = true;
 bCollideActors = true;
 bUseCylinderCollision = true;
 DrawType = DT_StaticMesh;
 objectName = "Ancestor Object";
 enablePickup = false;
}


