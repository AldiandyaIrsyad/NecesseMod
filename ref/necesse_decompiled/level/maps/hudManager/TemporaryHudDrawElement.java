package necesse.level.maps.hudManager;

import java.util.ArrayList;
import necesse.level.maps.Level;

public abstract class TemporaryHudDrawElement extends HudDrawElement {
   public int lifeTime;
   public long spawnTime;

   public TemporaryHudDrawElement(int var1) {
      this.lifeTime = var1;
   }

   public boolean isRemoved() {
      return super.isRemoved() || this.getTimeSinceSpawned() > this.lifeTime;
   }

   public int getTimeSinceSpawned() {
      Level var1 = this.getLevel();
      return var1 == null ? 0 : (int)(var1.getWorldEntity().getLocalTime() - this.spawnTime);
   }

   public float getLifeProgressPercent() {
      return (float)this.getTimeSinceSpawned() / (float)this.lifeTime;
   }

   public void addThis(Level var1, ArrayList<HudDrawElement> var2) {
      super.addThis(var1, var2);
      this.spawnTime = var1.getWorldEntity().getLocalTime();
   }
}
