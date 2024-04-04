package necesse.level.gameTile;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class MoonPath extends PathTiledTile {
   public MoonPath() {
      super("moonpath", new Color(200, 200, 200));
      this.lightLevel = 100;
   }

   public void tickEffect(Level var1, int var2, int var3) {
      super.tickEffect(var1, var2, var3);
      GameRandom var4 = GameRandom.globalRandom;
      if (var4.getChance(0.002F)) {
         int var5 = var2 * 32 + var4.nextInt(32);
         int var6 = var3 * 32 + var4.nextInt(32);
         var1.entityManager.addParticle((float)var5, (float)var6, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.magicSparkParticles.sprite(var4.nextInt(4), 0, 22)).sizeFades(11, 22).minDrawLight(100).lifeTime(1500).height(16.0F);
      }

   }
}
