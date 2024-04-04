package necesse.inventory.container.customAction;

import java.util.HashSet;
import java.util.function.BooleanSupplier;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;

public abstract class EventSubscribeEmptyCustomAction extends ContainerCustomAction {
   protected HashSet<Integer> active = new HashSet();

   public EventSubscribeEmptyCustomAction() {
   }

   public int subscribe() {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      int var3 = GameRandom.globalRandom.nextInt();
      var2.putNextInt(var3);
      var2.putNextBoolean(true);
      this.runAndSendAction(var1);
      return var3;
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
         this.active.add(var2);
      } else {
         this.active.remove(var2);
      }

      if (var3) {
         this.onSubscribed(() -> {
            return this.active.contains(var2);
         });
      }

   }

   public abstract void onSubscribed(BooleanSupplier var1);
}
