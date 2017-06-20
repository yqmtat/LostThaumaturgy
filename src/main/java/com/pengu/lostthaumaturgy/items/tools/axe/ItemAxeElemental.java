package com.pengu.lostthaumaturgy.items.tools.axe;

import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.pengu.hammercore.utils.WorldLocation;
import com.pengu.lostthaumaturgy.init.ItemMaterialsLT;
import com.pengu.lostthaumaturgy.init.SoundEventsLT;
import com.pengu.lostthaumaturgy.utils.ListDelta;
import com.pengu.lostthaumaturgy.utils.WoodHelper;

public class ItemAxeElemental extends ItemAxe
{
	public ItemAxeElemental()
	{
		super(ItemMaterialsLT.tool_elemental, 10, -2.8F);
		setUnlocalizedName("elemental_axe");
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
	{
		WorldLocation loc = new WorldLocation(player.world, pos);
		
		if(loc.getBlock() instanceof BlockLog && !player.isSneaking())
		{
			itemstack.damageItem(1, player);
			
			if(!player.world.isRemote)
			{
				WoodHelper helper = WoodHelper.newHelper(loc, 64);
				BlockPos woodPos = helper.getFarthest(pos);
				
				SoundEventsLT.SWING.playAt(new WorldLocation(player.world, woodPos), 1F, 1F, SoundCategory.PLAYERS);
				
				AxisAlignedBB aabb = new AxisAlignedBB(woodPos).grow(1);
				List<EntityItem> before = player.world.getEntitiesWithinAABB(EntityItem.class, aabb);
				player.world.destroyBlock(woodPos, true);
				List<EntityItem> after = player.world.getEntitiesWithinAABB(EntityItem.class, aabb);
				
				List<EntityItem> drops = ListDelta.positiveDelta(before, after);
				for(EntityItem drop : drops)
				{
					drop.setPositionAndUpdate(player.posX, player.posY, player.posZ);
					drop.setNoPickupDelay();
				}
			}
			
			return true;
		}
		
		return false;
	}
}