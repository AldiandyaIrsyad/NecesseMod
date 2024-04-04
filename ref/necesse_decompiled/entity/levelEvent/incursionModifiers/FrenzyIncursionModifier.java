package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class FrenzyIncursionModifier extends UniqueIncursionModifier {
   public FrenzyIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel var1) {
      super(var1);
   }

   public void onIncursionLevelGenerated(IncursionLevel var1, int var2) {
      FrenzyModifierLevelEvent var3 = new FrenzyModifierLevelEvent();
      var1.entityManager.addLevelEvent(var3);
      var1.gndData.setInt("frenzy" + var2, var3.getUniqueID());
   }

   public void onIncursionLevelCompleted(IncursionLevel var1, int var2) {
      int var3 = var1.gndData.getInt("frenzy" + var2);
      LevelEvent var4 = var1.entityManager.getLevelEvent(var3, false);
      if (var4 != null) {
         var4.over();
      }

   }
}
