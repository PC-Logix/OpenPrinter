package pcl.openprinter.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.tileentity.ShredderTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class BlockShredder extends BlockContainer {
	private Random random;

	@SideOnly(Side.CLIENT)
	public static IIcon sideIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon frontIcon;

	public BlockShredder() {
		super(Material.iron );
		setCreativeTab(OpenPrinter.CreativeTab);
		setBlockName("shredder");
		setHardness(.5f);

		random = new Random();
	}

	@Override
	public void breakBlock (World world, int x, int y, int z, Block block, int meta) {
		ShredderTE tileEntity = (ShredderTE) world.getTileEntity(x, y, z);
		dropContent(tileEntity, world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		super.breakBlock(world, x, y, z, block, meta);
	}

	public void dropContent(IInventory chest, World world, int xCoord, int yCoord, int zCoord) {
		if (chest == null)
			return;

		for (int i1 = 0; i1 < chest.getSizeInventory(); ++i1) {
			ItemStack itemstack = chest.getStackInSlot(i1);

			if (itemstack != null) {
				float offsetX = random.nextFloat() * 0.8F + 0.1F;
				float offsetY = random.nextFloat() * 0.8F + 0.1F;
				float offsetZ = random.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem;

				for (; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
					int stackSize = random.nextInt(21) + 10;
					if (stackSize > itemstack.stackSize)
						stackSize = itemstack.stackSize;

					itemstack.stackSize -= stackSize;
					entityitem = new EntityItem(world, (double)((float)xCoord + offsetX), (double)((float)yCoord + offsetY), (double)((float)zCoord + offsetZ), new ItemStack(itemstack.getItem(), stackSize, itemstack.getItemDamage()));

					float velocity = 0.05F;
					entityitem.motionX = (double)((float)random.nextGaussian() * velocity);
					entityitem.motionY = (double)((float)random.nextGaussian() * velocity + 0.2F);
					entityitem.motionZ = (double)((float)random.nextGaussian() * velocity);

					if (itemstack.hasTagCompound())
						entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
				}
			}
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return !OpenPrinter.render3D;

	}

	@Override
	public boolean renderAsNormalBlock() {
		return !OpenPrinter.render3D;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float clickX, float clickY, float clickZ) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
		// code to open gui explained later		
		player.openGui(OpenPrinter.instance, 2, world, x, y, z);
		return true;
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack)
	{
		int l = MathHelper.floor_double(par5EntityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
		par1World.setBlockMetadataWithNotify(par2, par3, par4, l + 1, 2);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister registry) {
		registry.registerIcon(OpenPrinter.MODID + ":shredder");
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		sideIcon = ir.registerIcon(OpenPrinter.MODID + ":block_side");
		frontIcon = ir.registerIcon(OpenPrinter.MODID + ":shredder_front");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int j)
	{
		switch (i)
		{
		case 2: 
			if (j == 1)
			{
				return frontIcon;
			}
			return sideIcon;
		case 3: 
			if (j == 0)
				return frontIcon;
			if (j == 3) {
				return frontIcon;
			}
			return sideIcon;
		case 4: 
			if (j == 4)
			{
				return frontIcon;
			}
			return sideIcon;
		case 5: 
			if (j == 2)
			{
				return frontIcon;
			}
			return sideIcon;
		}
		return sideIcon;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new ShredderTE();
	}

}
