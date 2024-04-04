package necesse.entity.mobs.jumping;

import java.awt.geom.Point2D;
import java.util.function.BiConsumer;
import necesse.engine.network.packet.PacketMobJump;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public interface JumpingMobInterface {
   JumpingMobStats getJumpStats();

   default void tickJump(float var1, float var2) {
      JumpingMobStats var3 = this.getJumpStats();
      if (var3.mob.isServer()) {
         this.tickJump(var1, var2, (var1x, var2x) -> {
            ((JumpingMobInterface)var3.mob).runJump(var1x, var2x);
            Server var3x = var3.mob.getLevel().getServer();
            var3x.network.sendToClientsWithEntity(new PacketMobJump(var3.mob, var1x, var2x), var3.mob);
         });
      }

   }

   default void tickJump(float var1, float var2, BiConsumer<Float, Float> var3) {
      JumpingMobStats var4 = this.getJumpStats();
      if ((var1 != 0.0F || var2 != 0.0F) && var4.lastJumpTime + (long)var4.lastJumpAnimationTime + (long)var4.getJumpCooldown() < var4.mob.getWorldEntity().getLocalTime()) {
         Point2D.Float var5 = GameMath.normalize(var1, var2);
         float var6 = var4.getJumpStrength();
         var3.accept(var5.x * var6, var5.y * var6);
      }

   }

   default int getJumpAnimationFrame(int var1) {
      JumpingMobStats var2 = this.getJumpStats();
      long var3 = Math.max(0L, var2.mob.getWorldEntity().getLocalTime() - var2.lastJumpTime);
      return var3 < (long)var2.lastJumpAnimationTime ? GameUtils.getAnim(var3, var1, var2.lastJumpAnimationTime) : 0;
   }

   default void runJump(float var1, float var2) {
      JumpingMobStats var3 = this.getJumpStats();
      Mob var10000 = var3.mob;
      var10000.dx += var1;
      var10000 = var3.mob;
      var10000.dy += var2;
      if (!var3.mob.isAttacking) {
         var3.mob.setFacingDir(var1, var2);
      }

      var3.lastJumpTime = var3.mob.getWorldEntity().getLocalTime();
      var3.lastJumpAnimationTime = var3.getJumpAnimationTime();
   }

   default void setJumpAnimationTime(int var1) {
      this.getJumpStats().setJumpAnimationTime(var1);
   }

   default void setJumpCooldown(int var1) {
      this.getJumpStats().setJumpCooldown(var1);
   }

   default void setJumpStrength(float var1) {
      this.getJumpStats().setJumpStrength(var1);
   }
}
