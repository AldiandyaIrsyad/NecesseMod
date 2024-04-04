package necesse.inventory.container.settlement.data;

import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.SettlerRegistry;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlerData {
   public final Settler settler;
   public final int mobUniqueID;
   public final Point bedPosition;

   public SettlementSettlerData(LevelSettler var1) {
      this.settler = var1.settler;
      this.mobUniqueID = var1.mobUniqueID;
      SettlementBed var2 = var1.getBed();
      this.bedPosition = var2 == null ? null : new Point(var2.tileX, var2.tileY);
   }

   public SettlementSettlerData(PacketReader var1) {
      short var2 = var1.getNextShort();
      this.settler = SettlerRegistry.getSettler(var2);
      this.mobUniqueID = var1.getNextInt();
      if (var1.getNextBoolean()) {
         int var3 = var1.getNextShortUnsigned();
         int var4 = var1.getNextShortUnsigned();
         this.bedPosition = new Point(var3, var4);
      } else {
         this.bedPosition = null;
      }

   }

   public void writeContentPacket(PacketWriter var1) {
      var1.putNextShort((short)this.settler.getID());
      var1.putNextInt(this.mobUniqueID);
      var1.putNextBoolean(this.bedPosition != null);
      if (this.bedPosition != null) {
         var1.putNextShortUnsigned(this.bedPosition.x);
         var1.putNextShortUnsigned(this.bedPosition.y);
      }

   }

   public SettlerMob getSettlerMob(Level var1) {
      Mob var2 = (Mob)var1.entityManager.mobs.get(this.mobUniqueID, false);
      return var2 instanceof SettlerMob ? (SettlerMob)var2 : null;
   }
}
