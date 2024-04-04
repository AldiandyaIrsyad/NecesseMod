package necesse.inventory.container.mob;

import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ExpeditionMissionRegistry;

public class ContainerExpedition {
   public final SettlerExpedition expedition;
   public final boolean available;
   public final float successChance;
   public final int price;

   public ContainerExpedition(SettlerExpedition var1, boolean var2, float var3, int var4) {
      this.expedition = var1;
      this.available = var2;
      this.successChance = var3;
      this.price = var4;
   }

   public ContainerExpedition(SettlerExpedition var1) {
      this(var1, false, 0.0F, 0);
   }

   public ContainerExpedition(PacketReader var1) {
      this.expedition = ExpeditionMissionRegistry.getExpedition(var1.getNextShortUnsigned());
      this.available = var1.getNextBoolean();
      if (this.available) {
         this.successChance = var1.getNextFloat();
         this.price = var1.getNextInt();
      } else {
         this.successChance = 0.0F;
         this.price = 0;
      }

   }

   public void writePacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.expedition.getID());
      var1.putNextBoolean(this.available);
      if (this.available) {
         var1.putNextFloat(this.successChance);
         var1.putNextInt(this.price);
      }

   }
}
