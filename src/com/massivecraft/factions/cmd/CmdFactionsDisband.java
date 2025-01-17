package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeStringConfirmation;
import com.massivecraft.massivecore.util.ConfirmationUtil;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsDisband extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsDisband()
	{
		// Parameters
		this.addParameter(TypeFaction.get(), "faction");
		this.addParameter(TypeStringConfirmation.get(), "confirmation", "");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Faction faction = this.readArg();
		String confirmationString = this.readArg(null);

		if (MConf.get().requireConfirmationForFactionDisbanding) ConfirmationUtil.tryConfirm(this);
		
		// MPerm
		if ( ! MPerm.getPermDisband().has(msender, faction, true)) return;

		// Verify
		if (faction.getFlag(MFlag.getFlagPermanent()))
		{
			throw new MassiveException().addMsg("<i>This faction is designated as permanent, so you cannot disband it.");
		}

		// Event
		EventFactionsDisband event = new EventFactionsDisband(me, faction);
		event.run();
		if (event.isCancelled()) return;

		// Merged Apply and Inform
		
		// Run event for each player in the faction
		for (MPlayer mplayer : faction.getMPlayers())
		{
			EventFactionsMembershipChange membershipChangeEvent = new EventFactionsMembershipChange(sender, mplayer, FactionColl.get().getNone(), MembershipChangeReason.DISBAND);
			membershipChangeEvent.run();
		}

		// Inform
		for (MPlayer mplayer : faction.getMPlayersWhereOnline(true))
		{
			mplayer.msg("<h>%s<i> disbanded your faction.", msender.describeTo(mplayer));
		}
		
		if (msenderFaction != faction)
		{
			msender.msg("<i>You disbanded <h>%s<i>." , faction.describeTo(msender));
		}
		
		// Log
		if (MConf.get().logFactionDisband)
		{
			Factions.get().log(Txt.parse("<i>The faction <h>%s <i>(<h>%s<i>) was disbanded by <h>%s<i>.", faction.getName(), faction.getId(), msender.getDisplayName(IdUtil.getConsole())));
		}		
		
		// Apply
		faction.detach();
	}
	
}
