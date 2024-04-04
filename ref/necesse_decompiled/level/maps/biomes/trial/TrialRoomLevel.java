package necesse.level.maps.biomes.trial;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class TrialRoomLevel extends Level {
   public TrialRoomLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public TrialRoomLevel(LevelIdentifier var1, WorldEntity var2) {
      this(var1, 50, 50, var2);
      this.isCave = true;
      this.isProtected = true;
      this.biome = BiomeRegistry.TRIAL_ROOM;
      int var3 = TileRegistry.getTileID("rocktile");

      for(int var4 = 0; var4 < this.width; ++var4) {
         for(int var5 = 0; var5 < this.height; ++var5) {
            this.setTile(var4, var5, var3);
         }
      }

   }

   public boolean shouldLimitCameraWithinBounds(PlayerMob var1) {
      return false;
   }
}
