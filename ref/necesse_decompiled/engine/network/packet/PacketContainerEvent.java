package necesse.engine.network.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.inventory.container.Container;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.events.ContainerEventRegistry;

public class PacketContainerEvent extends Packet {
   public final int eventID;
   public final Packet content;

   public PacketContainerEvent(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.eventID = var2.getNextShort();
      this.content = var2.getNextContentPacket();
   }

   public PacketContainerEvent(ContainerEvent var1) {
      this.eventID = ContainerEventRegistry.getID(var1);
      if (this.eventID == -1) {
         throw new IllegalStateException("Cannot send unregistered event");
      } else {
         this.content = new Packet();
         var1.write(new PacketWriter(this.content));
         PacketWriter var2 = new PacketWriter(this);
         var2.putNextShort((short)this.eventID);
         var2.putNextContentPacket(this.content);
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      Container var3 = var2.getContainer();
      if (var3 != null) {
         Constructor var4 = ContainerEventRegistry.getReaderConstructor(this.eventID);

         try {
            ContainerEvent var5 = (ContainerEvent)var4.newInstance(new PacketReader(this.content));
            var3.handleEvent(var5);
         } catch (IllegalAccessException | InvocationTargetException | InstantiationException var6) {
            System.err.println("Failed in constructing received container event");
            var6.printStackTrace();
         }
      }

   }
}
