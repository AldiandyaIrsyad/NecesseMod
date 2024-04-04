package necesse.level.maps.incursion;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.level.maps.IncursionLevel;

public class UniqueIncursionModifier implements IDDataContainer {
   public final IDData idData = new IDData();
   public final UniqueIncursionModifierRegistry.ModifierChallengeLevel challengeLevel;

   public UniqueIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel var1) {
      this.challengeLevel = var1;
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

   public LocalMessage getModifierName() {
      return new LocalMessage("ui", "incursionmodifier" + this.getStringID());
   }

   public LocalMessage getModifierDescription() {
      return new LocalMessage("ui", "incursionmodifier" + this.getStringID() + "info");
   }

   public int getModifierTickets(IncursionData var1) {
      return 100;
   }

   public void onIncursionLevelGenerated(IncursionLevel var1, int var2) {
   }

   public void onIncursionLevelCompleted(IncursionLevel var1, int var2) {
   }
}
