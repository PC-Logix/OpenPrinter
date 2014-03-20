package pcl.OpenPrinter.Blocks;


import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.OpenPrinter.OpenPrinter;
import pcl.OpenPrinter.TileEntity.PrinterTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPrinter extends BlockContainer
{

	public static BlockPrinter block = null;
	
	public static void init(int blockID)
	{
		if( null == block )
		{
			block = new BlockPrinter(blockID);
		}
		GameRegistry.registerBlock(block, "openprinter.printer");
		LanguageRegistry.addName(block, "Open Printer");
	}
	
	public void breakBlock(World world, int x, int y, int z, int i, int j) {
		world.removeBlockTileEntity(x, y, z);
	}
        
    	private BlockPrinter(int blockID)
    	{
    		super(blockID, Material.rock);
    		setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
    		renderID = RenderingRegistry.getNextAvailableRenderId();
    	}
        
    	private Icon icon;
    	private int renderID;
    	
		public BlockPrinter (int id, Material material) 
        {
                super(id, material);
        }
        
		@Override
		public boolean isOpaqueCube()
		{
		   return false;
		}
		
		@Override
	    public boolean renderAsNormalBlock()
	    {
	        return false;
	    }

	    @SideOnly(Side.CLIENT)
	    @Override
	    public Icon getIcon(int par1, int par2)
	    {
	    	return icon;
	    }
	    
		@Override
		public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
			super.onBlockPlacedBy(world, x, y, z, player, stack);
			int dir = MathHelper.floor_double((double)((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
			world.setBlockMetadataWithNotify(x, y, z, dir, 0);
		}
	    
	    @SideOnly(Side.CLIENT)
	    @Override
	    public void registerIcons(IconRegister registry)
	    {
	    	icon = registry.registerIcon(OpenPrinter.MODID + ":openprinter");
	    }
	    
	    @SideOnly(Side.CLIENT)
	    @Override
	    public int getRenderType()
	    {
	    	return renderID;
	    }


		@Override
		public TileEntity createNewTileEntity(World world) {
			return new PrinterTE();
		}
        

}
