package pcl.openprinter.network;

import pcl.openprinter.OpenPrinter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class GUIFolderMessageHandlerServer implements IMessageHandler<MessageGUIFolder, IMessage> {

	@Override
	public IMessage onMessage(MessageGUIFolder message, MessageContext ctx) {
	     PacketHandler.INSTANCE.sendToAll(message);
	     WorldServer targetWorld = null;
	     //net.minecraft.tileentity.TileEntity tileEntity = null;
	     WorldServer[] ws = MinecraftServer.getServer().worldServers;
	     for (WorldServer s : ws) {
	       if (s.provider.dimensionId == message.dim) {
	         targetWorld = s;
	         ctx.getServerHandler().playerEntity.getHeldItem().setStackDisplayName(message.folderName);
	       }
	     }
		return null;
	}
}
