package necesse.inventory.item.questItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.QuestItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ObtainTip;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public class QuestItem extends Item implements ObtainTip {
   public GameMessage obtainTip;

   public QuestItem(int var1, GameMessage var2) {
      super(var1);
      this.rarity = Item.Rarity.QUEST;
      this.keyWords.add("quest");
      this.dropsAsMatDeathPenalty = true;
      this.setItemCategory(new String[]{"misc", "questitems"});
      this.obtainTip = var2;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 10000;
   }

   public QuestItem(GameMessage var1) {
      this(1, var1);
   }

   public QuestItem() {
      this((GameMessage)null);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "questitem"));
      return var4;
   }

   public GameMessage getObtainTip() {
      return this.obtainTip;
   }

   public ItemPickupEntity getPickupEntity(Level var1, InventoryItem var2, float var3, float var4, float var5, float var6) {
      return new QuestItemPickupEntity(var1, var2, var3, var4, var5, var6);
   }

   public MobSpawnTable getExtraCritterSpawnTable(ServerClient var1, Level var2) {
      return null;
   }

   public MobSpawnTable getExtraMobSpawnTable(ServerClient var1, Level var2) {
      return null;
   }

   public FishingLootTable getExtraFishingLoot(ServerClient var1, FishingSpot var2) {
      return null;
   }

   public LootTable getExtraMobDrops(ServerClient var1, Mob var2) {
      return null;
   }
}
