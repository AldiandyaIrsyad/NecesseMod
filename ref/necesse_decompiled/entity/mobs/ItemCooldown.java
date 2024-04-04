package necesse.entity.mobs;

import necesse.engine.util.GameMath;

public class ItemCooldown {
   public final long startTime;
   public final int cooldown;

   public ItemCooldown(long var1, int var3) {
      this.startTime = var1;
      this.cooldown = var3;
   }

   public int getTimeRemaining(long var1) {
      return (int)GameMath.limit(this.startTime + (long)this.cooldown - var1, 0L, (long)this.cooldown);
   }

   public float getPercentRemaining(long var1) {
      return (float)this.getTimeRemaining(var1) / (float)this.cooldown;
   }
}
