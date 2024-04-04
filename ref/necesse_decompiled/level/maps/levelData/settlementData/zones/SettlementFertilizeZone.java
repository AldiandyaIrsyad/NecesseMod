package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Color;
import java.awt.Point;
import java.util.function.BooleanSupplier;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SeedObjectEntity;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.jobs.FertilizeLevelJob;
import necesse.level.maps.levelData.jobs.JobsLevelData;

public class SettlementFertilizeZone extends SettlementTileTickZone {
   public SettlementFertilizeZone() {
   }

   protected void handleTile(Point var1) {
      ObjectEntity var2 = this.manager.data.getLevel().entityManager.getObjectEntity(var1.x, var1.y);
      if (var2 instanceof SeedObjectEntity) {
         SeedObjectEntity var3 = (SeedObjectEntity)var2;
         if (!var3.isFertilized()) {
            JobsLevelData.addJob(this.manager.data.getLevel(), new FertilizeLevelJob(var1.x, var1.y, this, var3.fertilizeReservable));
         }
      }

   }

   public boolean isHiddenSetting() {
      return (Boolean)Settings.hideSettlementFertilizeZones.get();
   }

   public GameMessage getDefaultName(int var1) {
      return new LocalMessage("ui", "settlementfertilizezonedefname", new Object[]{"number", var1});
   }

   public GameMessage getAbstractName() {
      return new LocalMessage("ui", "settlementfertilizezone");
   }

   public HudDrawElement getHudDrawElement(int var1, BooleanSupplier var2) {
      return this.getHudDrawElement(var1, var2, new Color(67, 217, 33, 150), new Color(36, 118, 18, 75));
   }
}
