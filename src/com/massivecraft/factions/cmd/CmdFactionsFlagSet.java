package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMFlag;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsFlagChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;

public class CmdFactionsFlagSet extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlagSet()
	{
		// Parameters
		this.addParameter(TypeMFlag.get(), "flag").setDesc("the faction flag to set a value for");
		this.addParameter(TypeBooleanYes.get(), "yes/no").setDesc("should the flag be on or off?");
		this.addParameter(TypeFaction.get(), "faction", "you").setDesc("the faction to set the flag for (per default your own)");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		MFlag flag = this.readArg();
		boolean value = this.readArg();
		Faction faction = this.readArg(msenderFaction);
		
		// Do the sender have the right to change flags for this faction?
		if ( ! MPerm.getPermFlags().has(msender, faction, true)) return;
		
		// Is this flag editable?
		if (!msender.isOverriding() && ! flag.isEditable())
		{
			throw new MassiveException().addMsg("<b>The flag <h>%s <b>is not editable.", flag.getName());
		}
		
		// Event
		EventFactionsFlagChange event = new EventFactionsFlagChange(sender, faction, flag, value);
		event.run();
		if (event.isCancelled()) return;
		value = event.isNewValue();
		
		// No change 
		if (faction.getFlag(flag) == value)
		{
			throw new MassiveException().addMsg("%s <i>already has %s <i>set to %s<i>.", faction.describeTo(msender), flag.getStateDesc(value, false, true, true, false, true), flag.getStateDesc(value, true, true, false, false, false));
		}
		
		// Apply
		faction.setFlag(flag, value);
		
		// Inform
		String stateInfo = flag.getStateDesc(faction.getFlag(flag), true, false, true, true, true);
		if (msender.getFaction() != faction)
		{
			// Send message to sender
			msg("<h>%s <i>set a flag for <h>%s<i>.", msender.describeTo(msender, true), faction.describeTo(msender, true));
			message(stateInfo);
		}
		faction.msg("<h>%s <i>set a flag for <h>%s<i>.", msender.describeTo(faction, true), faction.describeTo(faction, true));
		faction.sendMessage(stateInfo);
	}
	
}
