package com.pengu.lostthaumaturgy.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.mrdimka.hammercore.common.items.MultiVariantItem;
import com.pengu.hammercore.utils.IGetter;
import com.pengu.hammercore.utils.IRegisterListener;
import com.pengu.lostthaumaturgy.LTInfo;
import com.pengu.lostthaumaturgy.LostThaumaturgy;
import com.pengu.lostthaumaturgy.entity.EntityTravelingTrunk;
import com.pengu.lostthaumaturgy.init.BlocksLT;
import com.pengu.lostthaumaturgy.init.ItemsLT;

public class ItemMultiMaterial extends MultiVariantItem implements IRegisterListener
{
	private static ItemMultiMaterial instance;
	
	public ItemMultiMaterial()
	{
		super("multi_material", gen());
		instance = this;
	}
	
	public static String[] gen()
	{
		String[] names = new String[EnumMultiMaterialType.values().length];
		for(int i = 0; i < names.length; ++i)
			names[i] = EnumMultiMaterialType.values()[i].mod + ":" + EnumMultiMaterialType.values()[i];
		return names;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		int dm = stack.getItemDamage();
		if(dm < EnumMultiMaterialType.values().length)
			return EnumMultiMaterialType.values()[dm].getRarity();
		return EnumRarity.COMMON;
	}
	
	@Override
	public void onRegistered()
	{
		EnumMultiMaterialType.VAPOROUS_CRYSTAL.setHasEffect(true);
		EnumMultiMaterialType.AQUEOUS_CRYSTAL.setHasEffect(true);
		EnumMultiMaterialType.EARTHEN_CRYSTAL.setHasEffect(true);
		EnumMultiMaterialType.FIERY_CRYSTAL.setHasEffect(true);
		EnumMultiMaterialType.VIS_CRYSTAL.setHasEffect(true);
		EnumMultiMaterialType.TAINTED_CRYSTAL.setHasEffect(true);
		
		EnumMultiMaterialType.ENCHANTED_WOOD.setHasEffect(true);
		EnumMultiMaterialType.ENCHANTED_SILVERWOOD.setHasEffect(true);
		EnumMultiMaterialType.ENCHANTED_FABRIC.setHasEffect(true);
		
		EnumMultiMaterialType.EXTRACT_COOLEST_WATER.setHasEffect(true);
		EnumMultiMaterialType.EXTRACT_DEEPEST_EARTH.setHasEffect(true);
		EnumMultiMaterialType.EXTRACT_FOULEST_TAINT.setHasEffect(true);
		EnumMultiMaterialType.EXTRACT_LIGHTEST_AIR.setHasEffect(true);
		EnumMultiMaterialType.EXTRACT_PUREST_MAGIC.setHasEffect(true);
		EnumMultiMaterialType.EXTRACT_WARMEST_FIRE.setHasEffect(true);
		
		EnumMultiMaterialType.REZULI_CRYSTAL.setHasEffect(true);
		
		for(EnumMultiMaterialType type : EnumMultiMaterialType.values())
			if(type.oredict != null)
				for(String name : type.oredict)
					if(!name.isEmpty())
						OreDictionary.registerOre(name, type.stack());
		
		OreDictionary.registerOre("logWood", BlocksLT.SILVERWOOD_LOG);
		OreDictionary.registerOre("logWood", BlocksLT.GREATWOOD_LOG);
		OreDictionary.registerOre("treeLeaves", BlocksLT.SILVERWOOD_LEAVES);
		OreDictionary.registerOre("plankWood", BlocksLT.SILVERWOOD_PLANKS);
		OreDictionary.registerOre("stairWood", BlocksLT.SILVERWOOD_STAIRS);
		OreDictionary.registerOre("oreQuicksilver", BlocksLT.CINNABAR_ORE);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> l)
	{
		for(int i = 0; i < this.names.length; ++i)
			if(EnumMultiMaterialType.values()[i].tab == tab || tab == LostThaumaturgy.tab)
			{
				l.add(new ItemStack(item, 1, i));
				if(i == EnumMultiMaterialType.ALUMENTUM.ordinal())
					l.add(new ItemStack(BlocksLT.NITOR));
			}
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack held = player.getHeldItem(hand);
		if(held.getItemDamage() == EnumMultiMaterialType.TRAVELING_TRUNK.getDamage())
		{
			BlockPos sp = pos.offset(facing);
			if(!worldIn.isRemote)
				worldIn.spawnEntity(new EntityTravelingTrunk(worldIn, player, sp.getX() + .5, sp.getY(), sp.getZ() + .5));
			held.shrink(1);
			player.swingArm(hand);
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		int dmg = stack.getItemDamage();
		return EnumMultiMaterialType.values()[dmg % EnumMultiMaterialType.values().length].hasEffect;
	}
	
	public enum EnumMultiMaterialType implements IGetter<ItemStack>
	{
		VAPOROUS_CRYSTAL("crystalVis"), //
		AQUEOUS_CRYSTAL("crystalVis"), //
		EARTHEN_CRYSTAL("crystalVis"), //
		FIERY_CRYSTAL("crystalVis"), //
		VIS_CRYSTAL("crystalVis"), //
		TAINTED_CRYSTAL("crystalVis"), //
		DEPLETED_CRYSTAL("crystalDepleted"), //
		ENCHANTED_WOOD, //
		ENCHANTED_SILVERWOOD, //
		CINDERPEARL_POD, //
		ZOMBIE_BRAINS, //
		QUICKSILVER("gemQuicksilver"), //
		THAUMIUM_INGOT("ingotThaumium"), //
		ENCHANTED_FABRIC, //
		ANIMATED_PISTON, //
		ALUMENTUM, //
		ANCIENT_POTTERY, //
		TARNISHED_CHALICE, //
		WORN_STATUETTE, //
		ANCIENT_WEAPON, //
		ANCIENT_SEAL, //
		ANCIENT_STONE_TABLET, //
		CRACKED_WISP_SHELL, //
		DISTORTED_SKULL, //
		INHUMAN_SKULL, //
		DARKENED_CRYSTAL_EYE, //
		KNOTTED_SPIKE, //
		TOME_FORBIDDEN_KNOWLEDGE, //
		TAINT_SPORES, //
		TAINTED_ORGAN, //
		TAINTED_FRUIT, //
		TAINTED_BRANCH, //
		INTACT_TAINTSPORE_POD, //
		WRITHING_TAINT_TENDRILS, //
		SHARD_STRANGE_METAL, //
		ELDRITCH_MECHANISM, //
		OPALESCENT_EYE, //
		DISTURBING_MIRROR, //
		GLOWING_ELDRITCH_DEVICE, //
		ELDRITCH_REPOSITORY, //
		DARKNESS_SEED, //
		VOID_INGOT("ingotVoid"), //
		TOPAZ("gemLTTopaz"), //
		EXTRACT_PUREST_MAGIC, //
		EXTRACT_FOULEST_TAINT, //
		EXTRACT_WARMEST_FIRE, //
		EXTRACT_DEEPEST_EARTH, //
		EXTRACT_LIGHTEST_AIR, //
		EXTRACT_COOLEST_WATER, //
		SOUL_FRAGMENT, //
		CONGEALED_TAINT, //
		REZULI_CRYSTAL("gemRezuli"), //
		ELDRITCH_KEYSTONE_INERT, //
		ELDRITCH_KEYSTONE_TLHUTLH, //
		TRAVELING_TRUNK, //
		INERT_CARPET, //
		MITHRILLIUM_INGOT("ingotMithrillium"), //
		ADAMINITE_INGOT("ingotAdaminite"), //
		CAP_IRON, //
		CAP_GOLD, //
		CAP_THAUMIUM, //
		CAP_VOID, //
		ROD_GREATWOOD, //
		ROD_SILVERWOOD, TALLOW;
		
		private final String oredict[];
		public final String mod;
		public final CreativeTabs tab;
		public boolean hasEffect = false;
		private EnumRarity rarity = EnumRarity.COMMON;
		
		static
		{
			VAPOROUS_CRYSTAL.setRarity(EnumRarity.UNCOMMON);
			AQUEOUS_CRYSTAL.setRarity(EnumRarity.UNCOMMON);
			FIERY_CRYSTAL.setRarity(EnumRarity.UNCOMMON);
			EARTHEN_CRYSTAL.setRarity(EnumRarity.UNCOMMON);
			VIS_CRYSTAL.setRarity(EnumRarity.UNCOMMON);
			TAINTED_CRYSTAL.setRarity(EnumRarity.UNCOMMON);
			DEPLETED_CRYSTAL.setRarity(EnumRarity.UNCOMMON);
			ZOMBIE_BRAINS.setRarity(EnumRarity.UNCOMMON);
			ENCHANTED_SILVERWOOD.setRarity(EnumRarity.UNCOMMON);
			ENCHANTED_WOOD.setRarity(EnumRarity.UNCOMMON);
			DARKNESS_SEED.setRarity(EnumRarity.UNCOMMON);
			VOID_INGOT.setRarity(EnumRarity.UNCOMMON);
			CONGEALED_TAINT.setRarity(EnumRarity.UNCOMMON);
			EXTRACT_COOLEST_WATER.setRarity(EnumRarity.RARE);
			EXTRACT_DEEPEST_EARTH.setRarity(EnumRarity.RARE);
			EXTRACT_FOULEST_TAINT.setRarity(EnumRarity.RARE);
			EXTRACT_LIGHTEST_AIR.setRarity(EnumRarity.RARE);
			EXTRACT_PUREST_MAGIC.setRarity(EnumRarity.RARE);
			EXTRACT_WARMEST_FIRE.setRarity(EnumRarity.RARE);
			SOUL_FRAGMENT.setRarity(EnumRarity.UNCOMMON);
			WORN_STATUETTE.setRarity(EnumRarity.UNCOMMON);
			ANCIENT_WEAPON.setRarity(EnumRarity.UNCOMMON);
			INHUMAN_SKULL.setRarity(EnumRarity.UNCOMMON);
			DARKENED_CRYSTAL_EYE.setRarity(EnumRarity.UNCOMMON);
			TAINTED_FRUIT.setRarity(EnumRarity.UNCOMMON);
			TAINTED_BRANCH.setRarity(EnumRarity.UNCOMMON);
			OPALESCENT_EYE.setRarity(EnumRarity.UNCOMMON);
			DISTURBING_MIRROR.setRarity(EnumRarity.UNCOMMON);
			ANCIENT_SEAL.setRarity(EnumRarity.RARE);
			KNOTTED_SPIKE.setRarity(EnumRarity.RARE);
			INTACT_TAINTSPORE_POD.setRarity(EnumRarity.RARE);
			GLOWING_ELDRITCH_DEVICE.setRarity(EnumRarity.RARE);
			ANCIENT_STONE_TABLET.setRarity(EnumRarity.EPIC);
			TOME_FORBIDDEN_KNOWLEDGE.setRarity(EnumRarity.EPIC);
			WRITHING_TAINT_TENDRILS.setRarity(EnumRarity.EPIC);
			ELDRITCH_REPOSITORY.setRarity(EnumRarity.EPIC);
			INERT_CARPET.setRarity(EnumRarity.RARE);
			ELDRITCH_KEYSTONE_INERT.setRarity(EnumRarity.RARE);
			ELDRITCH_KEYSTONE_TLHUTLH.setRarity(EnumRarity.RARE);
		}
		
		EnumMultiMaterialType()
		{
			this.oredict = null;
			this.tab = LostThaumaturgy.tab;
			this.mod = LTInfo.MOD_ID;
		}
		
		EnumMultiMaterialType(String... oredict)
		{
			this.oredict = oredict;
			this.tab = LostThaumaturgy.tab;
			this.mod = LTInfo.MOD_ID;
		}
		
		EnumMultiMaterialType(String mod, CreativeTabs tab, String... oredict)
		{
			this.oredict = oredict;
			this.mod = mod;
			this.tab = tab;
		}
		
		public void setRarity(EnumRarity rarity)
		{
			this.rarity = rarity;
		}
		
		public EnumRarity getRarity()
		{
			return rarity;
		}
		
		public void setHasEffect(boolean hasEffect)
		{
			this.hasEffect = hasEffect;
		}
		
		public boolean isEqualByOredict(ItemStack stack)
		{
			if(oredict == null || stack == null)
				return false;
			if(stack.isItemEqual(stack(stack.getCount())))
				return true;
			int[] ids = OreDictionary.getOreIDs(stack);
			for(int id : ids)
			{
				String dict = OreDictionary.getOreName(id);
				if(dict.equals(oredict))
					return true;
			}
			return false;
		}
		
		public int getDamage()
		{
			return ordinal();
		}
		
		public ItemStack stack()
		{
			return stack(1);
		}
		
		public ItemStack stack(int count)
		{
			return new ItemStack(instance, count, getDamage());
		}
		
		@Override
		public String toString()
		{
			return name().toLowerCase();
		}
		
		@Override
		public ItemStack get()
		{
			return stack();
		}
		
		public boolean isThisItem(ItemStack src)
		{
			return src.getItem() == ItemsLT.MULTI_MATERIAL && src.getItemDamage() == getDamage();
		}
		
		public static boolean isCrystal(ItemStack src)
		{
			return DEPLETED_CRYSTAL.isThisItem(src) || VAPOROUS_CRYSTAL.isThisItem(src) || AQUEOUS_CRYSTAL.isThisItem(src) || FIERY_CRYSTAL.isThisItem(src) || EARTHEN_CRYSTAL.isThisItem(src) || VIS_CRYSTAL.isThisItem(src) || TAINTED_CRYSTAL.isThisItem(src);
		}
		
		public static boolean isNormalCrystal(ItemStack src)
		{
			return VAPOROUS_CRYSTAL.isThisItem(src) || AQUEOUS_CRYSTAL.isThisItem(src) || FIERY_CRYSTAL.isThisItem(src) || EARTHEN_CRYSTAL.isThisItem(src) || VIS_CRYSTAL.isThisItem(src) || TAINTED_CRYSTAL.isThisItem(src);
		}
	}
}