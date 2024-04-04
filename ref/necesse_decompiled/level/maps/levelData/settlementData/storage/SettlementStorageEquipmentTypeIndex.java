package necesse.level.maps.levelData.settlementData.storage;

import java.util.HashMap;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.SettlerWeaponItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;

public class SettlementStorageEquipmentTypeIndex extends SettlementStorageIndex {
   private static int[] armorTypeToEquipmentTypeOrdinal = new int[ArmorItem.ArmorType.values().length];
   protected HashMap<EquipmentType, SettlementStorageRecordsRegionData> regions = new HashMap();

   public static EquipmentType armorTypeToEquipmentType(ArmorItem.ArmorType var0) {
      return SettlementStorageEquipmentTypeIndex.EquipmentType.values()[armorTypeToEquipmentTypeOrdinal[var0.ordinal()]];
   }

   public SettlementStorageEquipmentTypeIndex(Level var1) {
      super(var1);
   }

   public void clear() {
      this.regions.clear();
   }

   protected SettlementStorageRecordsRegionData getRegionData(EquipmentType var1) {
      return (SettlementStorageRecordsRegionData)this.regions.compute(var1, (var2, var3) -> {
         if (var3 == null) {
            return var1 == SettlementStorageEquipmentTypeIndex.EquipmentType.WEAPON ? new SettlementStorageRecordsRegionData(this, (var0) -> {
               return var0.item instanceof SettlerWeaponItem;
            }) : new SettlementStorageRecordsRegionData(this, (var1x) -> {
               return var1x.item.isArmorItem() && ((ArmorItem)var1x.item).armorType == var1.armorType;
            });
         } else {
            return var3;
         }
      });
   }

   public void add(InventoryItem var1, SettlementStorageRecord var2) {
      if (var1.item.isArmorItem()) {
         ArmorItem var3 = (ArmorItem)var1.item;
         this.getRegionData(armorTypeToEquipmentType(var3.armorType)).add(var2);
      } else if (var1.item instanceof SettlerWeaponItem) {
         this.getRegionData(SettlementStorageEquipmentTypeIndex.EquipmentType.WEAPON).add(var2);
      }

   }

   public SettlementStorageRecordsRegionData getEquipmentType(EquipmentType var1) {
      return (SettlementStorageRecordsRegionData)this.regions.get(var1);
   }

   static {
      EquipmentType[] var0 = SettlementStorageEquipmentTypeIndex.EquipmentType.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         EquipmentType var3 = var0[var2];
         if (var3.armorType != null) {
            armorTypeToEquipmentTypeOrdinal[var3.armorType.ordinal()] = var3.ordinal();
         }
      }

   }

   public static enum EquipmentType {
      WEAPON((ArmorItem.ArmorType)null),
      HEAD(ArmorItem.ArmorType.HEAD),
      CHEST(ArmorItem.ArmorType.CHEST),
      FEET(ArmorItem.ArmorType.FEET);

      public final ArmorItem.ArmorType armorType;

      private EquipmentType(ArmorItem.ArmorType var3) {
         this.armorType = var3;
      }

      // $FF: synthetic method
      private static EquipmentType[] $values() {
         return new EquipmentType[]{WEAPON, HEAD, CHEST, FEET};
      }
   }
}
