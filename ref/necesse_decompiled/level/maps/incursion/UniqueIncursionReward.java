package necesse.level.maps.incursion;

import java.util.ArrayList;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class UniqueIncursionReward implements IDDataContainer {
   public final IDData idData = new IDData();
   public final UniqueIncursionModifierRegistry.ModifierChallengeLevel challengeLevel;
   public final LootTable lootTable;

   public UniqueIncursionReward(LootTable var1, UniqueIncursionModifierRegistry.ModifierChallengeLevel var2) {
      this.challengeLevel = var2;
      this.lootTable = var1;
   }

   public IDData getIDData() {
      return this.idData;
   }

   public String getStringID() {
      return this.idData.getStringID();
   }

   public int getID() {
      return this.idData.getID();
   }

   public InventoryItem getSeededRandomInventoryItemFromLootTable(GameRandom var1) {
      ArrayList var2 = this.lootTable.getNewList(var1, 1.0F);
      if (var2.isEmpty()) {
         return null;
      } else {
         return var2.size() == 1 ? (InventoryItem)var2.get(0) : (InventoryItem)var2.get(var1.getIntBetween(0, var2.size() - 1));
      }
   }
}
