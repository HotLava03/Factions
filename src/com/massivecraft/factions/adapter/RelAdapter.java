package com.massivecraft.factions.adapter;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeRel;
import com.massivecraft.massivecore.MassiveException;

import java.lang.reflect.Type;

public class RelAdapter implements JsonDeserializer<Rel>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static RelAdapter i = new RelAdapter();
	public static RelAdapter get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Rel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		try
		{
			return TypeRel.get().read(json.getAsString());
		}
		catch (MassiveException e)
		{
			return null;
		}
		catch (Exception ex)
		{
			System.out.println(json);
			throw ex;
		}
	}
	
}
