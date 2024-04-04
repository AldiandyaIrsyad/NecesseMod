package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.Zoning;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.mobs.Mob;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class LevelSettler {
   public final SettlementLevelData data;
   public final Settler settler;
   public final int mobUniqueID;
   public final int settlerSeed;
   public final ItemCategoriesFilter dietFilter;
   public boolean preferArmorSets;
   public final ItemCategoriesFilter equipmentFilter;
   public int restrictZoneUniqueID;
   public boolean isOutsideLevel;
   protected SettlementBed bed;

   public LevelSettler(SettlementLevelData var1, Settler var2, int var3, int var4) {
      this.dietFilter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
      this.equipmentFilter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
      Objects.requireNonNull(var2);
      this.data = var1;
      this.settler = var2;
      this.mobUniqueID = var3;
      this.settlerSeed = var4;
      this.dietFilter.loadFromCopy(var1.getNewSettlerDiet());
      this.equipmentFilter.loadFromCopy(var1.getNewSettlerEquipmentFilter());
      this.preferArmorSets = var1.newSettlerEquipmentPreferArmorSets;
      this.restrictZoneUniqueID = var1.getNewSettlerRestrictZoneUniqueID();
   }

   public LevelSettler(SettlementLevelData var1, SettlerMob var2) {
      this(var1, var2.getSettler(), var2.getMob().getUniqueID(), var2.getSettlerSeed());
   }

   public LevelSettler(SettlementLevelData var1, LoadData var2) {
      this.dietFilter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
      this.equipmentFilter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
      this.data = var1;
      this.settler = SettlerRegistry.getSettler(var2.getUnsafeString("stringID"));
      Objects.requireNonNull(this.settler);
      this.mobUniqueID = var2.getInt("mobUniqueID", -1);
      this.settlerSeed = var2.getInt("seed", GameRandom.globalRandom.nextInt());
      Point var3 = var2.getPoint("bed", (Point)null, false);
      if (var3 != null) {
         SettlementBed var4 = var1.addOrValidateBed(var3.x, var3.y);
         if (var4 != null && !var4.isLocked && var4.getSettler() == null) {
            this.bed = var4;
            this.bed.settler = this;
            this.bed.isLocked = false;
         }
      }

      LoadData var6 = var2.getFirstLoadDataByName("dietFilter");
      if (var6 != null) {
         this.dietFilter.applyLoadData(var6);
      } else {
         this.dietFilter.loadFromCopy(var1.getNewSettlerDiet());
      }

      LoadData var5 = var2.getFirstLoadDataByName("equipmentFilter");
      if (var5 != null) {
         this.equipmentFilter.applyLoadData(var5);
      } else {
         this.equipmentFilter.loadFromCopy(var1.getNewSettlerEquipmentFilter());
      }

      this.preferArmorSets = var2.getBoolean("preferArmorSets", var1.newSettlerEquipmentPreferArmorSets, false);
      this.restrictZoneUniqueID = var2.getInt("restrictZoneUniqueID", 0, false);
      this.isOutsideLevel = var2.getBoolean("isOutsideLevel", this.isOutsideLevel, false);
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("stringID", this.settler.getStringID());
      var1.addInt("mobUniqueID", this.mobUniqueID);
      var1.addInt("seed", this.settlerSeed);
      if (this.bed != null) {
         var1.addPoint("bed", new Point(this.bed.tileX, this.bed.tileY));
      }

      SaveData var2;
      if (!this.dietFilter.isEqualsFilter(this.data.getNewSettlerDiet())) {
         var2 = new SaveData("dietFilter");
         this.dietFilter.addSaveData(var2);
         var1.addSaveData(var2);
      }

      if (!this.equipmentFilter.isEqualsFilter(this.data.getNewSettlerEquipmentFilter())) {
         var2 = new SaveData("equipmentFilter");
         this.equipmentFilter.addSaveData(var2);
         var1.addSaveData(var2);
      }

      var1.addBoolean("preferArmorSets", this.preferArmorSets);
      if (this.restrictZoneUniqueID != 0) {
         var1.addInt("restrictZoneUniqueID", this.restrictZoneUniqueID);
      }

      var1.addBoolean("isOutsideLevel", this.isOutsideLevel);
   }

   public SettlerMob getMob() {
      if (this.isOutsideLevel) {
         SettlersWorldData var2 = SettlersWorldData.getSettlersData(this.data.getLevel().getServer());
         return var2.getSettler(this.mobUniqueID);
      } else {
         Mob var1 = (Mob)this.data.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
         return var1 instanceof SettlerMob ? (SettlerMob)var1 : null;
      }
   }

   public void updateHome() {
      SettlerMob var1 = this.getMob();
      if (var1 != null) {
         if (!this.isOutsideLevel) {
            if (this.bed != null) {
               var1.setHome(new Point(this.bed.tileX, this.bed.tileY));
            } else {
               var1.setHome(this.data.getObjectEntityPos());
            }
         } else {
            var1.setHome((Point)null);
         }
      }

   }

   public void tick() {
      SettlerMob var1 = this.getMob();
      if (var1 != null) {
         var1.tickSettler(this.data, this);
      }

   }

   public void setRestrictZoneUniqueID(int var1) {
      this.restrictZoneUniqueID = var1;
   }

   public int getRestrictZoneUniqueID() {
      if (this.restrictZoneUniqueID != 0 && this.restrictZoneUniqueID != 1 && this.data.getRestrictZone(this.restrictZoneUniqueID) == null) {
         this.restrictZoneUniqueID = 0;
      }

      return this.restrictZoneUniqueID;
   }

   public ZoneTester isTileInRestrictZoneTester() {
      if (this.restrictZoneUniqueID == 0) {
         return (var0, var1x) -> {
            return true;
         };
      } else if (this.restrictZoneUniqueID == 1) {
         Zoning var2 = this.data.getDefendZone();
         Objects.requireNonNull(var2);
         return var2::containsTile;
      } else {
         RestrictZone var1 = this.data.getRestrictZone(this.restrictZoneUniqueID);
         if (var1 == null) {
            return (var0, var1x) -> {
               return true;
            };
         } else {
            Objects.requireNonNull(var1);
            return var1::containsTile;
         }
      }
   }

   public void assignBed(SettlementBed var1) {
      if (this.bed != null) {
         this.bed.settler = null;
      }

      if (var1 != null) {
         if (var1.settler != null) {
            var1.settler.bed = null;
         }

         var1.settler = this;
         var1.isLocked = false;
      }

      this.bed = var1;
      SettlerMob var2 = this.getMob();
      if (var2 != null) {
         var2.assignBed(this, var1, true);
         var2.makeSettler(this.data, this);
         this.settler.onMoveIn(this);
      }
   }

   public void onSettlerDeath() {
      if (this.bed != null && this.bed.getSettler() == this) {
         this.bed.settler = null;
      }

      this.bed = null;
      GameLog.debug.println("Removed settler from bed because of death");
      this.data.sendEvent(SettlementSettlersChangedEvent.class);
   }

   public SettlementBed getBed() {
      return this.bed;
   }

   public void moveOut() {
      SettlerMob var1 = this.getMob();
      if (var1 != null) {
         var1.moveOut();
      }

   }

   public boolean canMoveOut() {
      return this.settler.canMoveOut(this, this.data);
   }

   public boolean canBanish() {
      return this.settler.canBanish(this, this.data);
   }
}
