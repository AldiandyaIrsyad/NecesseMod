package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.BombProjectile;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;

public class CandelabraObject extends LampObject {
   public float flameHue;
   public float smokeHue;

   public CandelabraObject(String var1, ToolType var2, Color var3, float var4, float var5) {
      super(var1, new Rectangle(4, 4, 24, 24), var2, var3, var4, var5);
      this.flameHue = ParticleOption.defaultFlameHue;
      this.smokeHue = ParticleOption.defaultSmokeHue;
      this.furnitureType = "candelabra";
   }

   public CandelabraObject(String var1, Color var2, float var3, float var4) {
      this(var1, ToolType.ALL, var2, var3, var4);
   }

   public void tickEffect(Level var1, int var2, int var3) {
      if (GameRandom.globalRandom.getChance(20) && this.isActive(var1, var2, var3)) {
         byte var4 = var1.getObjectRotation(var2, var3);
         int var6 = 40;
         Point var5;
         if (var4 != 0 && var4 != 2) {
            var6 += (Integer)GameRandom.globalRandom.getOneOf((Object[])(-5, 0, 5));
            var5 = new Point(0, 0);
         } else {
            var5 = (Point)GameRandom.globalRandom.getOneOf((Object[])(new Point(-10, 0), new Point(0, 0), new Point(10, 0)));
         }

         BombProjectile.spawnFuseParticle(var1, (float)(var2 * 32 + 16 + var5.x), (float)(var3 * 32 + 16 + var5.y), (float)var6, this.flameHue, this.smokeHue);
      }

   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -28, 32, 28));
      return var4;
   }
}
