#exec OBJ LOAD FILE=EpicParticles.utx

//-----------------------------------------------------------
//
//-----------------------------------------------------------
class EmotionEmitter extends Emitter;

var color FirstColor;
var color SecondColor;
var vector myStartRelativeVelocity;
var vector myEndRelativeVelocity;
var range myStartVelocityRadialRange;
var rangeVector myStartSizeRange;
var bool bPauseParticles;
var float myFadeOutStart, myFadeInEnd;
var range myLifeTime;

replication
{
  reliable if(Role == Role_Authority)
    FirstColor, SecondColor, myStartRelativeVelocity, myEndRelativeVelocity, myStartVelocityRadialRange,
	bPauseParticles, myStartSizeRange, myFadeOutStart, myFadeInEnd, myLifeTime;
}

simulated function SetLifeTime(range newLifeTime)
{
	myLifeTime = newLifeTime;
}

simulated function SetFadeTime(float newStartFade, float newEndFade)
{
	myFadeOutStart = newStartFade;
	myFadeInEnd = newEndFade;
}

simulated function SetStartRelativeVelocity(vector newV)
{
	myStartRelativeVelocity = newV;
}

simulated function SetEndRelativeVelocity(vector newV)
{
	myEndRelativeVelocity = newV;
}

simulated function SetStartSize(rangeVector newStartSize)
{
	myStartSizeRange = newStartSize;
}

simulated function SetVelocityRange(range velR)
{
	myStartVelocityRadialRange = velR;
	//NetUpdateTime = Level.TimeSeconds - 1;
}

simulated function SetFirstColor(Color firstC)
{
	FirstColor = firstC;
	//NetUpdateTime = Level.TimeSeconds - 1;
}

simulated function SetSecondColor(Color secondC)
{
	SecondColor = secondC;
	//NetUpdateTime = Level.TimeSeconds - 1;

}

simulated function PauseParticles()
{
	bPauseParticles = true;
}

simulated function ResumeParticles()
{
	bPauseParticles = false;
}

//This is our key function. This function is called on clients if the actor has set bNetNotify to true
//Here we will set the emitter to use our variables
simulated function PostNetReceive()
{
	Emitters[0].ColorScale[1].Color = FirstColor;
	Emitters[0].ColorScale[2].Color = SecondColor;
	Emitters[0].StartVelocityRadialRange = myStartVelocityRadialRange;
	Emitters[0].VelocityScale[0].RelativeVelocity = myStartRelativeVelocity;
	Emitters[0].VelocityScale[2].RelativeVelocity = myEndRelativeVelocity;
	Emitters[0].StartSizeRange = myStartSizeRange;
	Emitters[0].LifetimeRange = myLifeTime;
	Emitters[0].FadeOutStartTime = myFadeOutStart;
	Emitters[0].FadeInEndTime = myFadeInEnd;

	if (bPauseParticles)
	{
		Emitters[0].InitialParticlesPerSecond = 0.0001;
		Emitters[0].AutomaticInitialSpawning = false;
	}
	else
	{
		Emitters[0].InitialParticlesPerSecond = default.Emitters[0].InitialParticlesPerSecond;
		Emitters[0].AutomaticInitialSpawning = default.Emitters[0].AutomaticInitialSpawning;
	}
}

defaultproperties
{
    Begin Object Class=SpriteEmitter Name=SpriteEmitter2
	UseColorScale=True
        ColorScale(0)=(Color=(B=255,G=255,R=255))
        ColorScale(1)=(RelativeTime=0.200000,Color=(G=170,R=255))
        ColorScale(2)=(RelativeTime=1.000000,Color=(G=217,R=255))
        FadeOutStartTime=1.300000
        FadeOut=True
        FadeInEndTime=0.250000
        FadeIn=True
        MaxParticles=15
        StartLocationShape=PTLS_Sphere
        SphereRadiusRange=(Min=16.000000,Max=16.000000)
        RevolutionsPerSecondRange=(Z=(Min=0.200000,Max=0.500000))
        RevolutionScale(0)=(RelativeRevolution=(Z=2.000000))
        RevolutionScale(1)=(RelativeTime=0.600000)
        RevolutionScale(2)=(RelativeTime=1.000000,RelativeRevolution=(Z=2.000000))
        SpinsPerSecondRange=(X=(Max=4.000000))
        StartSizeRange=(X=(Min=4.000000,Max=4.000000),Y=(Min=4.000000,Max=4.000000),Z=(Min=8.000000,Max=8.000000))
        UniformSize=True
        Texture=Texture'EpicParticles.Flares.HotSpot'
        LifetimeRange=(Min=1.600000,Max=1.600000)
        StartVelocityRadialRange=(Min=-20.000000,Max=-20.000000)
        VelocityLossRange=(X=(Min=0.200000,Max=0.200000),Y=(Min=0.200000,Max=0.200000),Z=(Min=1.000000,Max=1.000000))
        GetVelocityDirectionFrom=PTVD_AddRadial
        UseVelocityScale=True
        VelocityScale(0)=(RelativeVelocity=(X=2.000000,Y=2.000000,Z=2.000000))
        VelocityScale(1)=(RelativeTime=0.600000)
        VelocityScale(2)=(RelativeTime=1.000000,RelativeVelocity=(X=-10.000000,Y=-10.000000,Z=-10.000000))
        LowDetailFactor=+1.0
        Name="SpriteEmitter7"
    End Object
    Emitters(0)=SpriteEmitter'SpriteEmitter2'
    bNoDelete=false
	bAlwaysRelevant=True
	bNetTemporary=False
	bHidden=False
	bNetNotify=True  //this is important here, otherwise the function PostNetReceive() would not be called
	RemoteRole=ROLE_SimulatedProxy
    CullDistance=+2000.0
    //defaults here:
    FirstColor=(R=255)
    SecondColor=(R=255)
    bPauseParticles=false
    myStartRelativeVelocity=(X=2.000000,Y=2.000000,Z=2.000000)
    myEndRelativeVelocity=(X=-10.000000,Y=-10.000000,Z=-10.000000)
    myStartVelocityRadialRange=(Min=-20.000000,Max=-20.000000)
    myStartSizeRange=(X=(Min=4.000000,Max=4.000000),Y=(Min=4.000000,Max=4.000000),Z=(Min=8.000000,Max=8.000000))
    myFadeOutStart=1.300000
	myFadeInEnd=0.250000
	myLifeTime=(Min=1.600000,Max=1.600000)
}
