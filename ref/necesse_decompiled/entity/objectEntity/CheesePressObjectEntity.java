package necesse.entity.objectEntity;

import java.awt.Color;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class CheesePressObjectEntity extends ProcessingTechInventoryObjectEntity {
   public CheesePressObjectEntity(Level var1, int var2, int var3) {
      super(var1, "cheesepress", var2, var3, 2, 2, RecipeTechRegistry.CHEESE_PRESS);
   }

   public void clientTick() {
      super.clientTick();
      if (this.isProcessing() && GameRandom.globalRandom.nextInt(10) == 0) {
         int var1 = 24 + GameRandom.globalRandom.nextInt(16);
         this.getLevel().entityManager.addParticle((float)(this.getX() * 32 + GameRandom.globalRandom.nextInt(32)), (float)(this.getY() * 32 + 32), Particle.GType.COSMETIC).color(new Color(150, 150, 150)).heightMoves((float)var1, (float)(var1 + 20)).lifeTime(1000);
      }

   }

   public int getProcessTime() {
      return 60000;
   }
}
