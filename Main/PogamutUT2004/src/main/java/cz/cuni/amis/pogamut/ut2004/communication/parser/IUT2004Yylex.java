package cz.cuni.amis.pogamut.ut2004.communication.parser;

import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylex;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UnrealIdTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;

public interface IUT2004Yylex extends IYylex {
	
	public void setTranslator(UnrealIdTranslator translator);
	
	public void setItemTranslator(ItemTranslator translator);
	
	public void setTeamId(ITeamId teamId);

	public void setItemTypeTranslator(ItemTypeTranslator itemTypeTranslator);

}
