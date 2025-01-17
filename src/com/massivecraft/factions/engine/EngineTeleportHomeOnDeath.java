package com.massivecraft.factions.engine;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Warp;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class EngineTeleportHomeOnDeath extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineTeleportHomeOnDeath i = new EngineTeleportHomeOnDeath();
	public static EngineTeleportHomeOnDeath get() { return i; }

	// -------------------------------------------- //
	// TELEPORT TO HOME ON DEATH
	// -------------------------------------------- //
	
	public void teleportToHomeOnDeath(PlayerRespawnEvent event, EventPriority priority)
	{
		// If a player is respawning ...
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		final MPlayer mplayer = MPlayer.get(player);
		
		// ... homes are enabled, active and at this priority ...
		if (!MConf.get().warpsEnabled) return;
		if (!MConf.get().warpsTeleportToOnDeathActive) return;
		if (MConf.get().warpsTeleportToOnDeathPriority != priority) return;
		
		// ... and the player has a faction ...
		final Faction faction = mplayer.getFaction();
		if (faction.isNone()) return;
		
		// ... and the faction has a home ...
		List<Warp> warps = faction.getWarps().getAll((java.util.function.Predicate<Warp>) (warp -> warp.getName().equalsIgnoreCase(MConf.get().warpsTeleportToOnDeathName)));
		if (warps.isEmpty()) return;

		Warp warp = warps.get(0);
		
		// ... and the home is translatable ...
		Location respawnLocation = null;
		try
		{
			respawnLocation = warp.getLocation().asBukkitLocation(true);
		}
		catch (Exception e)
		{
			// The home location map may have been deleted
			return;
		}
		
		// ... then use it for the respawn location.
		event.setRespawnLocation(respawnLocation);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void teleportToHomeOnDeathLowest(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.LOWEST);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void teleportToHomeOnDeathLow(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.LOW);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void teleportToHomeOnDeathNormal(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.NORMAL);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void teleportToHomeOnDeathHigh(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.HIGH);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void teleportToHomeOnDeathHighest(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.HIGHEST);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void teleportToHomeOnDeathMonitor(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.MONITOR);
	}
	
}
