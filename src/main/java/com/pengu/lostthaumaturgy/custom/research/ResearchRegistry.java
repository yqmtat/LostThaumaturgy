package com.pengu.lostthaumaturgy.custom.research;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.pengu.lostthaumaturgy.items.ItemResearch;
import com.pengu.lostthaumaturgy.items.ItemResearch.EnumResearchItemType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ResearchRegistry
{
	private static final ArrayList<Research> researches = new ArrayList<>();
	
	public static void registerResearch(Research research)
	{
		for(Research r : researches)
			if(r.uid.equals(research.uid))
				return;
		researches.add(research);
	}
	
	@Nullable
	public static Research getById(String uid)
	{
		for(Research r : researches)
			if(r.uid.equals(uid))
				return r;
		return null;
	}
	
	public static void addAllResearchItems(EnumResearchItemType type, NonNullList<ItemStack> list)
	{
		for(Research r : researches)
			list.add(ItemResearch.create(r, type));
	}
	
	public static ArrayList<Research> getResearches()
	{
		return researches;
	}
	
	@Nullable
	public static Research chooseRandomUnresearched(ItemStack baseStack, ItemStack boostStack1, ItemStack boostStack2, EntityPlayer initiator, int attempts)
	{
		ArrayList<Research> newResearches = new ArrayList<>(researches);
		newResearches.removeIf(new Predicate<Research>()
		{
			@Override
			public boolean test(Research t)
			{
				return t.isCompleted(initiator);
			}
		});
		
		do
		{
			Research r = newResearches.get(initiator.getRNG().nextInt(newResearches.size()));
			float gen = initiator.getRNG().nextInt(100000) / 1000F;
			if(gen < r.failChance || !r.canObtainFrom(baseStack, boostStack1, boostStack2, initiator))
				continue;
			return r;
		} while(attempts-- > 0);
		
		return null;
	}
}