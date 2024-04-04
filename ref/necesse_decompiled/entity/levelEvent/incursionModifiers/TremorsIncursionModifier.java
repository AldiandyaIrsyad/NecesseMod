package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class TremorsIncursionModifier extends UniqueIncursionModifier {
   private final int tremorInterval = 25000;
   private final int tremorDuration = 5000;

   public TremorsIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel var1) {
      super(var1);
   }

   public LocalMessage getModifierDescription() {
      return new LocalMessage("ui", "incursionmodifier" + this.getStringID() + "info", new Object[]{"tremorinterval", 25, "tremorduration", 5});
   }

   public void onIncursionLevelGenerated(IncursionLevel var1, int var2) {
      TremorsModifierLevelEvent var3 = new TremorsModifierLevelEvent(var1.getTime(), 25000L, 5000L);
      var1.entityManager.addLevelEvent(var3);
      var1.gndData.setInt("tremors" + var2, var3.getUniqueID());
   }

   public void onIncursionLevelCompleted(IncursionLevel var1, int var2) {
      int var3 = var1.gndData.getInt("tremors" + var2);
      LevelEvent var4 = var1.entityManager.getLevelEvent(var3, false);
      if (var4 != null) {
         var4.over();
      }

   }
}
