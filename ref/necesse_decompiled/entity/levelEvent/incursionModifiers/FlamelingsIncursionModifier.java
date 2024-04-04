package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class FlamelingsIncursionModifier extends UniqueIncursionModifier {
   private final int flamelingSpawnInterval = 10000;
   private final int flamelingUptime = 4000;

   public FlamelingsIncursionModifier(UniqueIncursionModifierRegistry.ModifierChallengeLevel var1) {
      super(var1);
   }

   public void onIncursionLevelGenerated(IncursionLevel var1, int var2) {
      FlamelingsModifierLevelEvent var3 = new FlamelingsModifierLevelEvent(10000L, var1.getTime(), 4000L);
      var1.entityManager.addLevelEvent(var3);
      var1.gndData.setInt("flamelings" + var2, var3.getUniqueID());
   }

   public void onIncursionLevelCompleted(IncursionLevel var1, int var2) {
      int var3 = var1.gndData.getInt("flamelings" + var2);
      LevelEvent var4 = var1.entityManager.getLevelEvent(var3, false);
      if (var4 != null) {
         var4.over();
      }

   }
}
