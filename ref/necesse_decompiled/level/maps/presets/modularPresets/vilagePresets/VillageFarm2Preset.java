package necesse.level.maps.presets.modularPresets.vilagePresets;

import java.util.LinkedList;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;

public class VillageFarm2Preset extends VillagePreset {
   public VillageFarm2Preset(GameRandom var1) {
      super(4, 4, false, var1);
      this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 240, feedingtrough2, 55, woodfence, 56, woodfencegate, 239, feedingtrough],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 55, 55, 56, 55, 55, 55, 55, 55, 55, 55, 0, 0, 55, 0, 0, 0, 0, 239, 240, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 55, 0, 0, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.open(1, 0, 0);
      this.open(2, 0, 0);
      this.addCustomApplyArea(4, 4, this.width - 4, this.height - 4, 0, (var1x, var2, var3, var4, var5, var6) -> {
         LinkedList var7 = new LinkedList();
         String var8 = (String)var1.getOneOf((Object[])("sheep", "cow"));
         int var9 = var1.getIntBetween(5, 9);

         for(int var10 = 0; var10 < var9; ++var10) {
            Mob var11 = MobRegistry.getMob(var8, var1x);
            var1x.entityManager.addMob(var11, (float)(var1.getIntBetween(var2, var4) * 32 + 16), (float)(var1.getIntBetween(var3, var5) * 32 + 16));
            var7.add(var11);
         }

         return (var1xx, var2x, var3x) -> {
            var7.forEach(Mob::remove);
         };
      });
   }
}
