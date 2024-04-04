package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementForestryZoneConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.WorkZoneConfigComponent;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementForestryZoneUpdateEvent;
import necesse.level.gameObject.ForestryJobObject;
import necesse.level.gameObject.ForestrySaplingObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.LevelObject;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.jobs.ForestryLevelJob;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import necesse.level.maps.levelData.jobs.PlantSaplingLevelJob;

public class SettlementForestryZone extends SettlementTileTickZone {
   protected boolean choppingAllowed = true;
   protected boolean replantChoppedDownTrees = true;
   protected int autoPlantSaplingID = -1;

   public SettlementForestryZone() {
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addBoolean("choppingAllowed", this.choppingAllowed);
      var1.addBoolean("replantChoppedDownTrees", this.replantChoppedDownTrees);
      if (this.autoPlantSaplingID != -1) {
         GameObject var2 = ObjectRegistry.getObject(this.autoPlantSaplingID);
         if (var2 instanceof ForestrySaplingObject) {
            var1.addUnsafeString("autoPlantSaplingStringID", var2.getStringID());
         }
      }

   }

   public void applySaveData(LoadData var1, Collection<SettlementWorkZone> var2) {
      super.applySaveData(var1, var2);
      this.choppingAllowed = var1.getBoolean("choppingAllowed", this.choppingAllowed, false);
      this.replantChoppedDownTrees = var1.getBoolean("replantChoppedDownTrees", this.replantChoppedDownTrees, false);
      String var3 = var1.getUnsafeString("autoPlantSaplingStringID", (String)null, false);
      if (var3 != null) {
         String var4 = VersionMigration.tryFixStringID(var3, VersionMigration.oldObjectStringIDs);
         this.autoPlantSaplingID = ObjectRegistry.getObjectID(var4);
         this.validatePlantSaplingID();
      } else {
         this.autoPlantSaplingID = -1;
      }

   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);
      var1.putNextBoolean(this.choppingAllowed);
      var1.putNextBoolean(this.replantChoppedDownTrees);
      var1.putNextInt(this.autoPlantSaplingID);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);
      this.choppingAllowed = var1.getNextBoolean();
      this.replantChoppedDownTrees = var1.getNextBoolean();
      this.autoPlantSaplingID = var1.getNextInt();
   }

   protected void handleTile(Point var1) {
      if (this.isChoppingAllowed()) {
         LevelObject var2 = this.manager.data.getLevel().getLevelObject(var1.x, var1.y);
         if (var2.object.getID() != 0 && var2.object instanceof ForestryJobObject) {
            JobsLevelData.addJob(this.manager.data.getLevel(), new ForestryLevelJob(var1.x, var1.y, this, false));
         }
      }

      if (!this.replantChoppedDownTrees()) {
         this.getNewPlantJob(var1.x, var1.y, (GameObject)null);
      }

   }

   public PlantSaplingLevelJob getNewPlantJob(int var1, int var2, GameObject var3) {
      if (this.replantChoppedDownTrees()) {
         if (var3 instanceof ForestryJobObject) {
            String var4 = ((ForestryJobObject)var3).getSaplingStringID();
            if (var4 != null) {
               int var5 = ObjectRegistry.getObjectID(var4);
               if (var5 != -1) {
                  PlantSaplingLevelJob var6 = new PlantSaplingLevelJob(var1, var2, var5, true);
                  return (PlantSaplingLevelJob)JobsLevelData.addJob(this.manager.data.getLevel(), var6);
               }

               GameLog.warn.println("Could not find forestry sapling object with stringID " + var4);
            }
         }
      } else {
         int var10 = this.getAutoPlantSaplingID();
         if (var10 != -1) {
            Point var11 = (Point)this.zoning.getTiles().first();
            if (var11 != null) {
               GameObject var12 = this.manager.data.getLevel().getObject(var1, var2);
               GameObject var7;
               ForestrySaplingObject var8;
               PlantSaplingLevelJob var9;
               if (var12.getID() != var10) {
                  if (var12 instanceof ForestrySaplingObject) {
                     JobsLevelData.addJob(this.manager.data.getLevel(), new ForestryLevelJob(var1, var2, this, true));
                  } else {
                     var7 = ObjectRegistry.getObject(var10);
                     if (var7 instanceof ForestrySaplingObject && var7.canPlace(this.manager.data.getLevel(), var1, var2, 0) == null) {
                        var8 = (ForestrySaplingObject)var7;
                        if (var8.shouldForestryPlantAtTile(var11.x - var1, var11.y - var2)) {
                           var9 = new PlantSaplingLevelJob(var1, var2, var10, false);
                           return (PlantSaplingLevelJob)JobsLevelData.addJob(this.manager.data.getLevel(), var9);
                        }
                     }
                  }
               } else {
                  var7 = ObjectRegistry.getObject(var10);
                  if (var7 instanceof ForestrySaplingObject) {
                     var8 = (ForestrySaplingObject)var7;
                     if (var8.shouldForestryPlantAtTile(var11.x - var1, var11.y - var2)) {
                        var9 = new PlantSaplingLevelJob(var1, var2, var10, false);
                        return (PlantSaplingLevelJob)JobsLevelData.addJob(this.manager.data.getLevel(), var9);
                     }

                     JobsLevelData.addJob(this.manager.data.getLevel(), new ForestryLevelJob(var1, var2, this, true));
                  }
               }
            }
         }
      }

      return null;
   }

   public boolean isHiddenSetting() {
      return (Boolean)Settings.hideSettlementForestryZones.get();
   }

   public GameMessage getDefaultName(int var1) {
      return new LocalMessage("ui", "settlementforestryzonedefname", new Object[]{"number", var1});
   }

   public GameMessage getAbstractName() {
      return new LocalMessage("ui", "settlementforestryzone");
   }

   public HudDrawElement getHudDrawElement(int var1, BooleanSupplier var2) {
      return this.getHudDrawElement(var1, var2, new Color(217, 129, 33, 150), new Color(118, 70, 18, 75));
   }

   public boolean isChoppingAllowed() {
      return this.choppingAllowed;
   }

   public void setChoppingAllowed(boolean var1) {
      this.choppingAllowed = var1;
      if (this.manager != null && !this.isRemoved()) {
         (new SettlementForestryZoneUpdateEvent(this)).applyAndSendToClientsAt(this.manager.data.getLevel());
      }

   }

   public boolean replantChoppedDownTrees() {
      return this.replantChoppedDownTrees;
   }

   public void setReplantChoppedDownTrees(boolean var1) {
      this.replantChoppedDownTrees = var1;
      if (this.manager != null && !this.isRemoved()) {
         (new SettlementForestryZoneUpdateEvent(this)).applyAndSendToClientsAt(this.manager.data.getLevel());
      }

   }

   public int getAutoPlantSaplingID() {
      return this.autoPlantSaplingID;
   }

   protected boolean validatePlantSaplingID() {
      if (this.autoPlantSaplingID != -1) {
         GameObject var1 = ObjectRegistry.getObject(this.autoPlantSaplingID);
         if (var1 instanceof ForestrySaplingObject) {
            String var2 = ((ForestrySaplingObject)var1).getForestryResultObjectStringID();
            if (var2 != null) {
               return true;
            }
         }

         this.autoPlantSaplingID = -1;
         return false;
      } else {
         return true;
      }
   }

   public void setAutoPlantSaplingID(int var1) {
      if (var1 < 0) {
         var1 = -1;
      }

      this.autoPlantSaplingID = var1;
      this.validatePlantSaplingID();
      if (this.manager != null && !this.isRemoved()) {
         (new SettlementForestryZoneUpdateEvent(this)).applyAndSendToClientsAt(this.manager.data.getLevel());
      }

   }

   public void subscribeConfigEvents(SettlementContainer var1, BooleanSupplier var2) {
      super.subscribeConfigEvents(var1, var2);
      var1.subscribeEvent(SettlementForestryZoneUpdateEvent.class, (var1x) -> {
         return var1x.uniqueID == this.getUniqueID();
      }, var2);
   }

   public void writeSettingsForm(PacketWriter var1) {
      var1.putNextBoolean(this.choppingAllowed);
      var1.putNextBoolean(this.replantChoppedDownTrees);
      var1.putNextInt(this.autoPlantSaplingID);
   }

   public WorkZoneConfigComponent getSettingsForm(SettlementAssignWorkForm<?> var1, Runnable var2, PacketReader var3) {
      this.choppingAllowed = var3.getNextBoolean();
      this.replantChoppedDownTrees = var3.getNextBoolean();
      this.autoPlantSaplingID = var3.getNextInt();
      return new SettlementForestryZoneConfigForm(var1, this, var2);
   }
}
