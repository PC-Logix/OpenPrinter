package pcl.openprinter.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.tileentity.PrinterTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

public class Printer extends BlockContainer {
	private Random random;

	public Printer() {
		super(Material.iron );
		setCreativeTab(OpenPrinter.CreativeTab);
		setBlockName("printer");
		setHardness(.5f);
		if (OpenPrinter.render3D) {
			renderID = RenderingRegistry.getNextAvailableRenderId();
		}
		random = new Random();
	}

	private IIcon icon;
	private int renderID;

	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z) {
		int meta = par1IBlockAccess.getBlockMetadata(x, y, z);
		switch(meta) {
		case 0:
			this.setBlockBounds(0.0F,0.0F,0.4F,1.0F,1.0F,1.0F);
			break;
		case 1:
			this.setBlockBounds(0.0F,0.0F,0.0F,0.6F,1.0F,1.0F);
			break;
		case 2:
			this.setBlockBounds(0.0F,0.0F,0.0F,1.0F,1.0F,0.6F);
			break;
		case 3:
			this.setBlockBounds(0.4F,0.0F,0.0F,1.0F,1.0F,1.0F);
			break;
		default:
			this.setBlockBounds(0.0F,0.0F,0.0F,1.0F,1.0F,1.0F);
			break;
		}
	}
	
	
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_)
    {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, p_149743_5_, p_149743_6_, p_149743_7_);
    }

	@Override
	public void breakBlock (World world, int x, int y, int z, Block block, int meta) {
		PrinterTE tileEntity = (PrinterTE) world.getTileEntity(x, y, z);
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
		if (OpenPrinter.render3D) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public boolean renderAsNormalBlock() {
		if (OpenPrinter.render3D) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float clickX, float clickY, float clickZ) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
		// code to open gui explained later		
		player.openGui(OpenPrinter.instance, 0, world, x, y, z);
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2) {
		return icon;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		int dir = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister registry) {
		icon = registry.registerIcon(OpenPrinter.MODID + ":openprinter");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return renderID;
	}


	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new PrinterTE();
	}

}
