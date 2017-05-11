package com.pengu.lostthaumaturgy.tile;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.mrdimka.hammercore.HammerCore;
import com.mrdimka.hammercore.common.inventory.InventoryNonTile;
import com.mrdimka.hammercore.common.utils.WorldUtil;
import com.pengu.lostthaumaturgy.LTInfo;
import com.pengu.lostthaumaturgy.api.RecipesInfuser;
import com.pengu.lostthaumaturgy.api.tiles.IConnection;
import com.pengu.lostthaumaturgy.api.tiles.IInfuser;
import com.pengu.lostthaumaturgy.api.tiles.IUpgradable;
import com.pengu.lostthaumaturgy.api.tiles.TileVisUser;
import com.pengu.lostthaumaturgy.client.gui.GuiInfuser;
import com.pengu.lostthaumaturgy.init.ItemsLT;
import com.pengu.lostthaumaturgy.inventory.ContainerInfuser;
import com.pengu.lostthaumaturgy.items.ItemMultiMaterial.EnumMultiMaterialType;

public class TileInfuser extends TileVisUser implements ISidedInventory, IUpgradable, IInfuser
{
	protected static final ItemStack[] boreItemStacks = null;
	public InventoryNonTile infuserItemStacks = new InventoryNonTile(8);
	public float infuserCookTime = 0.0f;
	public float currentItemCookCost;
	public int angle = -1;
	public float sucked = 0.0f;
	protected int soundDelay = 0;
	public int boost = 0;
	protected byte[] upgrades = new byte[] { -1 };
	int boostDelay = 20;
	public UUID initiator = null;
	
	public boolean canSpawnParticle = true;
	
	@Override
	public boolean hasGui()
	{
		return true;
	}
	
	@Override
	public Object getClientGuiElement(EntityPlayer player)
	{
		return new GuiInfuser(player.inventory, this);
	}
	
	@Override
	public Object getServerGuiElement(EntityPlayer player)
	{
		return new ContainerInfuser(player.inventory, this);
	}
	
	public void readNBT(NBTTagCompound nbt)
	{
		infuserItemStacks.readFromNBT(nbt.getCompoundTag("Items"));
		this.infuserCookTime = nbt.getFloat("CookTime");
		this.currentItemCookCost = nbt.getFloat("CookCost");
		this.upgrades = nbt.getByteArray("Upgrades");
		sucked = nbt.getFloat("Sucked");
	}
	
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setFloat("CookTime", this.infuserCookTime);
		nbt.setFloat("CookCost", this.currentItemCookCost);
		nbt.setByteArray("Upgrades", this.upgrades);
		NBTTagCompound items = new NBTTagCompound();
		infuserItemStacks.writeToNBT(items);
		nbt.setTag("Items", items);
		nbt.setFloat("Sucked", sucked);
	}
	
	public float getCookProgressScaled(int i)
	{
		return this.infuserCookTime / this.currentItemCookCost * (float) i;
	}
	
	public float getBoostScaled()
	{
		return (0.1f + (float) this.boost / 2.0f) * 6;
	}
	
	public boolean isCooking()
	{
		return this.infuserCookTime > 0.0f;
	}
	
	@Override
	public void tick()
	{
		canSpawnParticle = true;
		
		boolean flag;
		if(this.soundDelay > 0)
		{
			--this.soundDelay;
		}
		this.angle = (int) (this.infuserCookTime / this.currentItemCookCost * 360.0f);
		if(world.isRemote)
			return;
		boolean flag1 = false;
		boolean bl = flag = this.infuserCookTime > 0.0f;
		b: if(this.canProcess() && this.currentItemCookCost > 0.0f)
		{
			if(world.isBlockIndirectlyGettingPowered(pos) > 0)
			{
				setSuction(0);
				break b;
			}
			
			float sa = Math.min(0.5f + 0.05f * (float) this.boost + (this.hasUpgrade((byte) 0) ? 0.5f : 0.0f), this.currentItemCookCost - this.infuserCookTime + 0.01f);
			this.sucked = this.getAvailablePureVis(sa);
			this.infuserCookTime += this.sucked;
			if(sucked > 0)
				sync();
			if(this.soundDelay == 0 && this.sucked >= 0.025f)
			{
				HammerCore.audioProxy.playSoundAt(world, LTInfo.MOD_ID + ":infuser", pos, 0.2F, 1F, SoundCategory.BLOCKS);
				this.soundDelay = 62;
				
				// int auraX = this.j >> 4;
				// int auraZ = this.l >> 4;
				// SIAuraChunk ac = (SIAuraChunk)
				// mod_ThaumCraft.AuraHM.get(Arrays.asList(auraX, auraZ,
				// Byte.valueOf(ThaumCraftCore.getDimension(this.i))));
				// if(ac != null) ac.badVibes = (short) (ac.badVibes + 1);
			}
			
			if(!this.hasUpgrade((byte) -1))
			{
				switch(world.rand.nextInt(4))
				{
				case 0:
				{
					// ThaumCraftCore.createSmallGreenFlameFX(this.i, (float)
					// this.j + 0.1f, (float) this.k + 1.15f, (float) this.l +
					// 0.1f);
					break;
				}
				case 1:
				{
					// ThaumCraftCore.createSmallGreenFlameFX(this.i, (float)
					// this.j + 0.1f, (float) this.k + 1.15f, (float) this.l +
					// 0.9f);
					break;
				}
				case 2:
				{
					// ThaumCraftCore.createSmallGreenFlameFX(this.i, (float)
					// this.j + 0.9f, (float) this.k + 1.15f, (float) this.l +
					// 0.1f);
					break;
				}
				case 3:
				{
					// ThaumCraftCore.createSmallGreenFlameFX(this.i, (float)
					// this.j + 0.9f, (float) this.k + 1.15f, (float) this.l +
					// 0.9f);
				}
				}
			}
		} else
		{
			this.sucked = 0.0f;
			setSuction(0);
		}
		
		if(this.infuserCookTime >= this.currentItemCookCost && flag)
		{
			this.addProcessedItem();
			this.infuserCookTime = 0.0f;
			this.currentItemCookCost = 0.0f;
			sync();
		}
		
		if(this.currentItemCookCost != 0.0f && this.currentItemCookCost != this.getCookCost())
		{
			this.infuserCookTime = 0.0f;
			this.currentItemCookCost = 0.0f;
			HammerCore.audioProxy.playSoundAt(world, "block.fire.extinguish", pos, 1F, 1.6F, SoundCategory.BLOCKS);
		}
		
		if(this.infuserCookTime == 0.0f && this.canProcess())
		{
			this.currentItemCookCost = this.getCookCost();
			sync();
		}
		if(flag != this.infuserCookTime > 0.0f)
		{
			flag1 = true;
		}
		
		if(flag1)
		{
			sync();
		}
		
		if(this.boostDelay <= 0 || this.boostDelay == 10)
		{
			// int auraX = this.j >> 4;
			// int auraZ = this.l >> 4;
			// SIAuraChunk ac = (SIAuraChunk)
			// mod_ThaumCraft.AuraHM.get(Arrays.asList(auraX, auraZ,
			// Byte.valueOf(ThaumCraftCore.getDimension(this.i))));
			// if(ac != null && this.boost < 10 && ac.boost > 0)
			// {
			// ++this.boost;
			// ac.boost = (short) (ac.boost - 1);
			// }
		}
		
		if(this.boostDelay <= 0)
		{
			if(this.boost > 0)
			{
				--this.boost;
			}
			this.boostDelay = 20;
		} else
		{
			--this.boostDelay;
		}
	}
	
	protected float getCookCost()
	{
		ArrayList<ItemStack> isal = new ArrayList<ItemStack>();
		for(int a = 2; a <= 7; ++a)
		{
			if(infuserItemStacks.getStackInSlot(a).isEmpty())
				continue;
			ItemStack is = new ItemStack(infuserItemStacks.getStackInSlot(a).getItem(), 1, infuserItemStacks.getStackInSlot(a).getItemDamage());
			isal.add(is);
		}
		if(isal.size() > 0)
		{
			float cost = RecipesInfuser.getInfusingCost(isal.toArray(), this);
			if(this.hasUpgrade((byte) 1))
				cost *= 0.8f;
			return cost;
		}
		return 0.0f;
	}
	
	protected ItemStack getResultStack()
	{
		ArrayList<ItemStack> isal = new ArrayList<ItemStack>();
		for(int a = 2; a <= 7; ++a)
		{
			if(infuserItemStacks.getStackInSlot(a).isEmpty())
				continue;
			ItemStack is = new ItemStack(infuserItemStacks.getStackInSlot(a).getItem(), 1, infuserItemStacks.getStackInSlot(a).getItemDamage());
			isal.add(is);
		}
		if(isal.size() > 0)
			return RecipesInfuser.getInfusingResult(isal.toArray(), this);
		return ItemStack.EMPTY;
	}
	
	protected boolean canProcess()
	{
		ItemStack itemstack = this.getResultStack();
		if(itemstack.isEmpty())
			return false;
		if(infuserItemStacks.getStackInSlot(0).isEmpty())
			return true;
		if(!this.infuserItemStacks.getStackInSlot(0).isItemEqual(itemstack))
			return false;
		int st = this.infuserItemStacks.getStackInSlot(0).getCount() + itemstack.getCount();
		if(st <= this.getInventoryStackLimit() && st <= itemstack.getMaxStackSize())
			return true;
		return false;
	}
	
	protected void addProcessedItem()
	{
		if(!this.canProcess())
		{
			return;
		}
		ItemStack itemstack = this.getResultStack();
		if(this.infuserItemStacks.getStackInSlot(0).isEmpty())
		{
			this.infuserItemStacks.setInventorySlotContents(0, itemstack.copy());
		} else if(this.infuserItemStacks.getStackInSlot(0).isItemEqual(itemstack) && this.infuserItemStacks.getStackInSlot(0).getCount() < itemstack.getMaxStackSize())
		{
			this.infuserItemStacks.getStackInSlot(0).grow(itemstack.getCount());
		}
		for(int a = 2; a <= 7; ++a)
		{
			if(this.infuserItemStacks.getStackInSlot(a).isEmpty())
				continue;
			
			if(isCrystal(infuserItemStacks.getStackInSlot(a)) && world.rand.nextBoolean())
			{
				if(this.infuserItemStacks.getStackInSlot(1).isEmpty())
				{
					this.infuserItemStacks.setInventorySlotContents(1, EnumMultiMaterialType.DEPLETED_CRYSTAL.stack());
				} else if(this.infuserItemStacks.getStackInSlot(1).isItemEqual(EnumMultiMaterialType.DEPLETED_CRYSTAL.stack()) && infuserItemStacks.getStackInSlot(1).getCount() < 64)
				{
					this.infuserItemStacks.getStackInSlot(1).grow(1);
				} else
				{
					if(!world.isRemote)
					{
						EntityItem entityItem = new EntityItem(world, pos.getX() + .5, pos.getY() + .75, pos.getZ() + .5, EnumMultiMaterialType.DEPLETED_CRYSTAL.stack());
						entityItem.motionY = 0.20000000298023224;
						world.spawnEntity(entityItem);
					}
				}
			}
			this.infuserItemStacks.getStackInSlot(a).shrink(1);
		}
	}
	
	private boolean isCrystal(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem() == ItemsLT.MULTI_MATERIAL && (stack.getItemDamage() == EnumMultiMaterialType.AQUEOUS_CRYSTAL.getDamage() || stack.getItemDamage() == EnumMultiMaterialType.EARTHEN_CRYSTAL.getDamage() || stack.getItemDamage() == EnumMultiMaterialType.FIERY_CRYSTAL.getDamage() || stack.getItemDamage() == EnumMultiMaterialType.TAINTED_CRYSTAL.getDamage() || stack.getItemDamage() == EnumMultiMaterialType.VAPOROUS_CRYSTAL.getDamage() || stack.getItemDamage() == EnumMultiMaterialType.VIS_CRYSTAL.getDamage());
	}
	
	public int getStartInventorySide(int side)
	{
		if(side == 0 || side == 1)
		{
			return 2;
		}
		return 0;
	}
	
	public int getSizeInventorySide(int side)
	{
		if(side == 0 || side == 1)
		{
			return 6;
		}
		return 2;
	}
	
	@Override
	public boolean getConnectable(EnumFacing face)
	{
		return true;
	}
	
	@Override
	public boolean canAcceptUpgrade(byte upgrade)
	{
		if(upgrade != 0 && upgrade != 1)
		{
			return false;
		}
		if(this.hasUpgrade(upgrade))
		{
			return false;
		}
		return true;
	}
	
	@Override
	public int getUpgradeLimit()
	{
		return 1;
	}
	
	@Override
	public byte[] getUpgrades()
	{
		return this.upgrades;
	}
	
	@Override
	public boolean hasUpgrade(byte upgrade)
	{
		if(this.upgrades.length < 1)
		{
			return false;
		}
		for(int a = 0; a < this.getUpgradeLimit(); ++a)
		{
			if(this.upgrades[a] != upgrade)
				continue;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setUpgrade(byte upgrade)
	{
		for(int a = 0; a < this.getUpgradeLimit(); ++a)
		{
			if(this.upgrades[a] >= 0 || !this.canAcceptUpgrade(upgrade))
				continue;
			this.upgrades[a] = upgrade;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean clearUpgrade(int index)
	{
		if(this.upgrades[index] >= 0)
		{
			this.upgrades[index] = -1;
			return true;
		}
		return false;
	}
	
	@Override
	public int getSizeInventory()
	{
		return infuserItemStacks.getSizeInventory();
	}
	
	@Override
	public boolean isEmpty()
	{
		return infuserItemStacks.isEmpty();
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return infuserItemStacks.getStackInSlot(index);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return infuserItemStacks.decrStackSize(index, count);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return infuserItemStacks.removeStackFromSlot(index);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		infuserItemStacks.setInventorySlotContents(index, stack);
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return infuserItemStacks.getInventoryStackLimit();
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return infuserItemStacks.isUsableByPlayer(player, pos);
	}
	
	@Override
	public void openInventory(EntityPlayer player)
	{
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return infuserItemStacks.isItemValidForSlot(index, stack);
	}
	
	@Override
	public int getField(int id)
	{
		return 0;
	}
	
	@Override
	public void setField(int id, int value)
	{
	}
	
	@Override
	public int getFieldCount()
	{
		return 0;
	}
	
	@Override
	public void clear()
	{
		infuserItemStacks.clear();
	}
	
	@Override
	public String getName()
	{
		return "Thaumic Infuser";
	}
	
	@Override
	public boolean hasCustomName()
	{
		return false;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return infuserItemStacks.getAllAvaliableSlots();
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
	{
		return direction.getAxis() != Axis.Y && index > 1 && index < 7;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return index >= 0 && index < 2;
	}
	
	public boolean isConnected(EnumFacing to)
	{
		BlockPos tpos = pos.offset(to);
		if(world.isBlockLoaded(tpos))
		{
			IConnection c = WorldUtil.cast(world.getTileEntity(tpos), IConnection.class);
			if(c != null)
				return c.getConnectable(to.getOpposite());
		}
		return false;
	}
	
	@Nullable
	@Override
	public EntityPlayer getInitiator()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(server == null)
			return null;
		return server.getPlayerList().getPlayerByUUID(initiator);
	}
	
	// public ItemStack b(int var1)
	// {
	// if(boreItemStacks[var1] != null)
	// {
	// ItemStack var2 = boreItemStacks[var1];
	// TileInfuser.boreItemStacks[var1] = null;
	// return var2;
	// }
	// return null;
	// }
}