package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.BiomeTrialIncursionData;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class ExplosiveIncursionModifier extends UniqueIncursionModifier {
   public ExplosiveIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel var1) {
      super(var1);
   }

   public int getModifierTickets(IncursionData var1) {
      return var1 instanceof BiomeTrialIncursionData ? 0 : super.getModifierTickets(var1);
   }

   public void onIncursionLevelGenerated(IncursionLevel var1, int var2) {
      ExplosiveModifierLevelEvent var3 = new ExplosiveModifierLevelEvent();
      var1.entityManager.addLevelEvent(var3);
      var1.gndData.setInt("explosive" + var2, var3.getUniqueID());
   }

   public void onIncursionLevelCompleted(IncursionLevel var1, int var2) {
      int var3 = var1.gndData.getInt("explosive" + var2);
      LevelEvent var4 = var1.entityManager.getLevelEvent(var3, false);
      if (var4 != null) {
         var4.over();
      }

   }
}
