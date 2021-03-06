package com.pengu.lostthaumaturgy.custom.research.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mrdimka.hammercore.HammerCore;
import com.mrdimka.hammercore.client.utils.TextDivider;
import com.pengu.hammercore.utils.IGetter;
import com.pengu.lostthaumaturgy.custom.research.Research;

/**
 * Client-common integration
 */
public class ResearchPageHandler
{
	public final Research research;
	
	protected List pages = new ArrayList<>();
	public IGetter<ItemStack> thaumonomiconIcon;
	
	public ResearchPageHandler(Research research)
	{
		this.research = research;
	}
	
	public void reload()
	{
		Runnable r = HammerCore.pipelineProxy.pipeIfOnGameSide(new Check(), Side.CLIENT);
		if(r != null)
			r.run();
	}
	
	public IPage getPage(int at)
	{
		if(pages.size() == 0)
			reload();
		
		if(at >= pages.size() || at < 0)
			return null;
		
		return HammerCore.pipelineProxy.pipeIfOnGameSide((IPage) pages.get(at), Side.CLIENT);
	}
	
	public int pageCount()
	{
		return pages.size();
	}
	
	public ResearchPageHandler addPage(IPage page)
	{
		pages.add(page);
		return this;
	}
	
	private class Check implements Runnable
	{
		@SideOnly(Side.CLIENT)
		public void run()
		{
			try
			{
				String desc = research.getDesc();
				String[] pages = TextDivider.divideByLenghtLimit(Minecraft.getMinecraft().fontRenderer, desc, 120 * 24);
				
				ResearchPageHandler.this.pages.clear();
				for(String page : pages)
					ResearchPageHandler.this.pages.add(new PageText(page));
			} catch(Throwable err)
			{
			}
		}
	}
}