package com.massivecraft.factions.engine;

import com.massivecraft.factions.AccessStatus;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.util.AsciiMap;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;

public class EngineMoveChunk extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineMoveChunk i = new EngineMoveChunk();
	public static EngineMoveChunk get() { return i; }

	// -------------------------------------------- //
	// MOVE CHUNK: DETECT
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void moveChunkDetect(PlayerMoveEvent event)
	{
		// If the player is moving from one chunk to another ...
		if (MUtil.isSameChunk(event)) return;
		Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;

		// ... gather info on the player and the move ...
		MPlayer mplayer = MPlayer.get(player);

		PS psFrom = PS.valueOf(event.getFrom());
		PS psTo = PS.valueOf(event.getTo());

		// ... send info onwards and try auto-claiming.
		sendChunkInfo(mplayer, player, psFrom, psTo);
		tryAutoClaim(mplayer, psTo);
	}

	// -------------------------------------------- //
	// MOVE CHUNK: SEND CHUNK INFO
	// -------------------------------------------- //

	private static void sendChunkInfo(MPlayer mplayer, Player player, PS psFrom, PS psTo)
	{
		sendAutoMapUpdate(mplayer, psTo);
		sendFactionTerritoryInfo(mplayer, player, psFrom, psTo);
		sendTerritoryAccessMessage(mplayer, psFrom, psTo);
	}
	
	private static void sendAutoMapUpdate(MPlayer mplayer, PS ps)
	{
		if (!mplayer.isMapAutoUpdating()) return;
		AsciiMap map = new AsciiMap(mplayer, ps, false);
		mplayer.message(map.render());
	}
	
	private static void sendFactionTerritoryInfo(MPlayer mplayer, Player player, PS psFrom, PS psTo)
	{
		Faction factionFrom = BoardColl.get().getFactionAt(psFrom);
		Faction factionTo = BoardColl.get().getFactionAt(psTo);
		
		if (factionFrom == factionTo) return;
		
		if (mplayer.isTerritoryInfoTitles())
		{
			String titleMain = parseTerritoryInfo(MConf.get().territoryInfoTitlesMain, mplayer, factionTo);
			String titleSub = parseTerritoryInfo(MConf.get().territoryInfoTitlesSub, mplayer, factionTo);
			int ticksIn = MConf.get().territoryInfoTitlesTicksIn;
			int ticksStay = MConf.get().territoryInfoTitlesTicksStay;
			int ticksOut = MConf.get().territoryInfoTitleTicksOut;
			MixinTitle.get().sendTitleMessage(player, ticksIn, ticksStay, ticksOut, titleMain, titleSub);
		}
		else
		{
			String message = parseTerritoryInfo(MConf.get().territoryInfoChat, mplayer, factionTo);
			player.sendMessage(message);
		}
	}
	
	private static String parseTerritoryInfo(String string, MPlayer mplayer, Faction faction)
	{
		if (string == null) throw new NullPointerException("string");
		if (faction == null) throw new NullPointerException("faction");
		
		string = Txt.parse(string);
		string = string.replace("{name}", faction.getName());
		string = string.replace("{relcolor}", faction.getColorTo(mplayer).toString());
		string = string.replace("{desc}", faction.getDescriptionDesc());
		
		return string;
	}
	
	private static void sendTerritoryAccessMessage(MPlayer mplayer, PS psFrom, PS psTo)
	{
		// Get TerritoryAccess for from & to chunks
		TerritoryAccess accessFrom = BoardColl.get().getTerritoryAccessAt(psFrom);
		TerritoryAccess accessTo = BoardColl.get().getTerritoryAccessAt(psTo);
		
		// See if the status has changed
		AccessStatus statusFrom = accessFrom.getTerritoryAccess(mplayer);
		AccessStatus statusTo = accessTo.getTerritoryAccess(mplayer);
		if (statusFrom == statusTo) return;
		
		// Inform
		mplayer.message(statusTo.getStatusMessage());
	}

	// -------------------------------------------- //
	// MOVE CHUNK: TRY AUTO CLAIM
	// -------------------------------------------- //

	private static void tryAutoClaim(MPlayer mplayer, PS chunkTo)
	{
		// If the player is auto claiming ...
		Faction autoClaimFaction = mplayer.getAutoClaimFaction();
		if (autoClaimFaction == null) return;

		// ... try claim.
		mplayer.tryClaim(autoClaimFaction, Collections.singletonList(chunkTo));
	}

}
