package necesse.engine.expeditions;

import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public abstract class SettlerExpedition implements IDDataContainer {
   public static int minCompleteTicks = 6000;
   public static int maxCompleteTicks = 12000;
   public final IDData idData = new IDData();
   protected GameMessage displayName;
   public static float[] questProgressSuccessChances = new float[]{0.6F, 0.8F, 1.0F};

   public IDData getIDData() {
      return this.idData;
   }

   public SettlerExpedition() {
   }

   public void onExpeditionMissionRegistryClosed() {
   }

   public void initDisplayName() {
      this.displayName = new LocalMessage("expedition", this.getStringID());
   }

   public GameMessage getDisplayName() {
      return this.displayName;
   }

   public boolean isAvailable(SettlementLevelData var1, HumanMob var2) {
      return this.getSuccessChance(var1, var2) > 0.0F;
   }

   public abstract GameMessage getUnavailableMessage();

   public abstract float getSuccessChance(SettlementLevelData var1, HumanMob var2);

   public abstract int getBaseCost(SettlementLevelData var1, HumanMob var2);

   public abstract List<InventoryItem> getRewardItems(SettlementLevelData var1, HumanMob var2);

   public int getTicksToComplete() {
      return GameRandom.globalRandom.getIntBetween(minCompleteTicks, maxCompleteTicks);
   }

   public static float questProgressSuccessChance(SettlementLevelData var0, String var1) {
      return questProgressSuccessChance(var0, var1, 0);
   }

   public static float questProgressSuccessChance(SettlementLevelData var0, String var1, int var2) {
      int var3 = SettlementQuestTier.getTierIndex(var1);
      int var4 = var0.getQuestTiersCompleted();
      int var5 = var4 - var3 - var2;
      return var5 < 0 ? 0.0F : questProgressSuccessChances[Math.min(var5, questProgressSuccessChances.length - 1)];
   }
}
