package necesse.inventory.container.settlement.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class SettlementWorkstationRecipeUpdateEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final int index;
   public final int uniqueID;
   public final Packet recipeContent;

   public SettlementWorkstationRecipeUpdateEvent(int var1, int var2, int var3, SettlementWorkstationRecipe var4) {
      this.tileX = var1;
      this.tileY = var2;
      this.index = var3;
      this.uniqueID = var4.uniqueID;
      this.recipeContent = new Packet();
      var4.writePacket(new PacketWriter(this.recipeContent));
   }

   public SettlementWorkstationRecipeUpdateEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextShortUnsigned();
      this.tileY = var1.getNextShortUnsigned();
      this.index = var1.getNextShort();
      this.uniqueID = var1.getNextInt();
      this.recipeContent = var1.getNextContentPacket();
   }

   public SettlementWorkstationRecipeUpdateEvent(int var1, int var2, SettlementWorkstationRecipe var3) {
      this(var1, var2, -1, var3);
   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.tileX);
      var1.putNextShortUnsigned(this.tileY);
      var1.putNextShort((short)this.index);
      var1.putNextInt(this.uniqueID);
      var1.putNextContentPacket(this.recipeContent);
   }
}
