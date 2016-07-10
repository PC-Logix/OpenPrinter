 package pcl.openprinter.network;
 
import pcl.openprinter.network.MessageGUIFolder;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

 public class PacketHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("openprinter");

     private PacketHandler() {
     }

     public static void init() {
     INSTANCE.registerMessage(GUIFolderMessageHandlerServer.class, MessageGUIFolder.class, 0, Side.SERVER);
     INSTANCE.registerMessage(GUIFolderMessageHandlerClient.class, MessageGUIFolder.class, 0, Side.CLIENT);
   }
 }


