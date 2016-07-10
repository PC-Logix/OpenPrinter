package pcl.openprinter.network;

import pcl.openprinter.OpenPrinter;
import pcl.openprinter.gui.GuiFolderInventory;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGUIFolder implements IMessage {

	public int folderNameLength;
	public String folderName;
	public int dim;
	
	public MessageGUIFolder() {}
	
	public MessageGUIFolder(String name, int i) {
		// TODO Auto-generated constructor stub
		//guiFolderInventory.setName(name);
		folderName = name;
		folderNameLength = folderName.length();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.folderNameLength = buf.readInt();
		this.folderName = new String(buf.readBytes(this.folderNameLength).array());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.writeInt(this.folderNameLength);
		buf.writeBytes(this.folderName.getBytes());
	}
}
