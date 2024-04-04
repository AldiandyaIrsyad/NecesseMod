package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class EnumCustomAction<T extends Enum<T>> extends ContainerCustomAction {
   protected final Class<T> enumClass;

   public EnumCustomAction(Class<T> var1) {
      this.enumClass = var1;
   }

   public void runAndSend(T var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextShortUnsigned(var1.ordinal());
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();
      Enum[] var3 = (Enum[])this.enumClass.getEnumConstants();
      if (var2 < var3.length) {
         this.run(var3[var2]);
      } else {
         throw new IllegalStateException("Could not find enum with ordinal " + var2 + " from " + this.enumClass.getSimpleName());
      }
   }

   protected abstract void run(T var1);
}
