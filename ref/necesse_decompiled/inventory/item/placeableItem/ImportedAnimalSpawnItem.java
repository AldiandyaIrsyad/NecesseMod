package necesse.inventory.item.placeableItem;

import necesse.engine.network.PacketReader;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class ImportedAnimalSpawnItem extends MobSpawnItem {
   public ImportedAnimalSpawnItem(int var1, boolean var2, String var3) {
      super(var1, var2, var3);
   }

   protected void beforeSpawned(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, Mob var7) {
      if (var7 instanceof HusbandryMob) {
         ((HusbandryMob)var7).setImported();
      }

   }
}
