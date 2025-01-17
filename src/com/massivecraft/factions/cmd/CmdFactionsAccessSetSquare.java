package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.ChunkUtil;

import java.util.Set;


public class CmdFactionsAccessSetSquare extends CmdFactionsAccessSetXRadius
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsAccessSetSquare(boolean grant)
	{
		// Super
		super(grant);
		
		// Aliases
		this.addAliases("square");
		
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
		Perm perm = grant ? Perm.ACCESS_GRANT_SQUARE : Perm.ACCESS_DENY_SQUARE;
		this.addRequirements(RequirementHasPerm.get(perm));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<PS> getChunks() throws MassiveException
	{
		// Common Startup
		final PS chunk = PS.valueOf(me.getLocation()).getChunk(true);
		return ChunkUtil.getChunksSquare(chunk, this.getRadius());
	}
	
}
