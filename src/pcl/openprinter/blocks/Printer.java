package pcl.openprinter.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.tileentity.PrinterTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class Printer extends BlockContainer {

	public static Printer block = null;

	public static void init(int blockID) {
		if (null == block) {
			block = new Printer(blockID);
		}
		GameRegistry.registerBlock(block, "openprinter.printer");
		block.setUnlocalizedName("printer");
		// LanguageRegistry.addName(block, "Open Printer");
	}

	public void breakBlock(World world, int x, int y, int z, int i, int j) {
		world.removeBlockTileEntity(x, y, z);
	}

	private Printer(int blockID) {
		super(blockID, Material.rock);
		setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
		if (OpenPrinter.render3D) {
			renderID = RenderingRegistry.getNextAvailableRenderId();
		}
	}

	private Icon icon;
	private int renderID;

	public Printer(int id, Material material) {
		super(id, material);
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
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
		// code to open gui explained later
		player.openGui(OpenPrinter.instance, 0, world, x, y, z);
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int par1, int par2) {
		return icon;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		int dir = MathHelper
				.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister registry) {
		icon = registry.registerIcon(OpenPrinter.MODID + ":openprinter");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return renderID;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new PrinterTE();
	}

}
