package necesse.inventory.container.customAction;

import java.util.HashSet;
import java.util.function.BooleanSupplier;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;

public abstract class EventSubscribeCustomAction<T> extends ContainerCustomAction {
   protected HashSet<Integer> activeIDs = new HashSet();

   public EventSubscribeCustomAction() {
   }

   public int subscribe(T var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      int var4 = GameRandom.globalRandom.nextInt();
      var3.putNextInt(var4);
      var3.putNextBoolean(true);
      this.writeData(var3, var1);
      this.runAndSendAction(var2);
      return var4;
   }

   public void unsubscribe(int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1);
      var3.putNextBoolean(false);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      boolean var3 = var1.getNextBoolean();
      if (var3) {
         this.activeIDs.add(var2);
      } else {
         this.activeIDs.remove(var2);
      }

      if (var3) {
         Object var4 = this.readData(var1);
         this.onSubscribed(() -> {
            return this.isActive(var2);
         }, var4);
      }

   }

   public boolean isActive(int var1) {
      return this.activeIDs.contains(var1);
   }

   public abstract void writeData(PacketWriter var1, T var2);

   public abstract T readData(PacketReader var1);

   public abstract void onSubscribed(BooleanSupplier var1, T var2);
}
