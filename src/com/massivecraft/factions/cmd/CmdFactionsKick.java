package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.IdUtil;
import org.bukkit.ChatColor;

public class CmdFactionsKick extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsKick()
	{
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player").setDesc("the player to kick");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Arg
		MPlayer mplayer = this.readArg();
		
		// Validate
		if (msender == mplayer)
		{
			msg("<b>You can't kick yourself.");
			message(mson(mson("You might want to: ").color(ChatColor.YELLOW), CmdFactions.get().cmdFactionsLeave.getTemplate(false)));
			return;
		}
		
		if ( !msender.isOverriding() && mplayer.getRank().isLeader())
		{
			throw new MassiveException().addMsg("<b>The leader cannot be kicked.");
		}
		
		if (! msender.isOverriding() && mplayer.getFaction() == msenderFaction && mplayer.getRank().isMoreThan(msender.getRank()) )
		{
			throw new MassiveException().addMsg("<b>You can't kick people of higher rank than yourself.");
		}
		
		if (! msender.isOverriding() && mplayer.getRank() == msender.getRank())
		{
			throw new MassiveException().addMsg("<b>You can't kick people of the same rank as yourself.");
		}

		if ( ! msender.isOverriding() && ! MConf.get().canLeaveWithNegativePower && mplayer.getPower() < 0)
		{
			msg("<b>You can't kick that person until their power is positive.");
			return;
		}
		
		// MPerm
		Faction mplayerFaction = mplayer.getFaction();
		if ( ! MPerm.getPermKick().has(msender, mplayerFaction, true)) return;

		// Event
		EventFactionsMembershipChange event = new EventFactionsMembershipChange(sender, mplayer, FactionColl.get().getNone(), MembershipChangeReason.KICK);
		event.run();
		if (event.isCancelled()) return;

		// Inform
		mplayerFaction.msg("%s<i> kicked %s<i> from the faction! :O", msender.describeTo(mplayerFaction, true), mplayer.describeTo(mplayerFaction, true));
		mplayer.msg("%s<i> kicked you from %s<i>! :O", msender.describeTo(mplayer, true), mplayerFaction.describeTo(mplayer));
		if (mplayerFaction != msenderFaction)
		{
			msender.msg("<i>You kicked %s<i> from the faction %s<i>!", mplayer.describeTo(msender), mplayerFaction.describeTo(msender));
		}

		if (MConf.get().logFactionKick)
		{
			Factions.get().log(msender.getDisplayName(IdUtil.getConsole()) + " kicked " + mplayer.getName() + " from the faction " + mplayerFaction.getName());
		}

		// Apply
		if (mplayer.getRank().isLeader())
		{
			mplayerFaction.promoteNewLeader();
		}
		mplayerFaction.uninvite(mplayer);
		mplayer.resetFactionData();
	}
	
}
