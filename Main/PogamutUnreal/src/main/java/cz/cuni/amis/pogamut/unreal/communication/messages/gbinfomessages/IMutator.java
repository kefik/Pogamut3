package cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

/**

Info batch message. Starts with SMUT message, ends with EMUT
message. Hold information about current mutators active
on the server.
Corresponding GameBots message is  MUT.
 */
public interface IMutator {

    public UnrealId getId();

    /**

    Name of the mutator.

     */
    public String getName();

    public String toHtmlString();
}
