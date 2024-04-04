package necesse.entity.objectEntity;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;

public class GrainMillObjectEntity extends ProcessingTechInventoryObjectEntity {
   public float bladeRotation;

   public GrainMillObjectEntity(Level var1, int var2, int var3) {
      super(var1, "grainmill", var2, var3, 2, 2, RecipeTechRegistry.GRAIN_MILL);
   }

   public void frameTick(float var1) {
      super.frameTick(var1);
      if (this.isProcessing()) {
         this.bladeRotation = (this.bladeRotation + var1 / 250.0F * 30.0F) % 360.0F;
      }

   }

   public void clientTick() {
      super.clientTick();
      if (this.isProcessing() && GameRandom.globalRandom.nextInt(10) == 0) {
         MultiTile var1 = this.getObject().getMultiTile(this.getLevel(), this.getX(), this.getY());
         Point var2 = new Point(var1.getCenterXOffset() * 16, var1.getCenterYOffset() * 16);
         int var3 = 24 + GameRandom.globalRandom.nextInt(16);
         this.getLevel().entityManager.addParticle((float)(this.getX() * 32 + var2.x + GameRandom.globalRandom.nextInt(32)), (float)(this.getY() * 32 + var2.y + 32), Particle.GType.COSMETIC).color(new Color(150, 150, 150)).heightMoves((float)var3, (float)(var3 + 20)).lifeTime(1000);
      }

   }

   public int getProcessTime() {
      return 15000;
   }
}
