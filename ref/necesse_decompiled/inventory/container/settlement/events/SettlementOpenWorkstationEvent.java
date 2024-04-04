package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadDataException;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class SettlementOpenWorkstationEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final ArrayList<SettlementWorkstationRecipe> recipes;

   public SettlementOpenWorkstationEvent(SettlementWorkstation var1) {
      this.tileX = var1.tileX;
      this.tileY = var1.tileY;
      this.recipes = new ArrayList(var1.recipes);
   }

   public SettlementOpenWorkstationEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextInt();
      this.tileY = var1.getNextInt();
      int var2 = var1.getNextShortUnsigned();
      this.recipes = new ArrayList(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         try {
            int var4 = var1.getNextInt();
            SettlementWorkstationRecipe var5 = new SettlementWorkstationRecipe(var4, var1);
            this.recipes.add(var5);
         } catch (LoadDataException var6) {
         }
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.tileX);
      var1.putNextInt(this.tileY);
      var1.putNextShortUnsigned(this.recipes.size());
      Iterator var2 = this.recipes.iterator();

      while(var2.hasNext()) {
         SettlementWorkstationRecipe var3 = (SettlementWorkstationRecipe)var2.next();
         var1.putNextInt(var3.uniqueID);
         var3.writePacket(var1);
      }

   }
}
