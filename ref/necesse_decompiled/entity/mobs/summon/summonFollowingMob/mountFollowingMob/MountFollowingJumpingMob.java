package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import necesse.engine.network.packet.PacketMountMobJump;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.jumping.JumpingMobInterface;
import necesse.entity.mobs.jumping.JumpingMobStats;

public class MountFollowingJumpingMob extends MountFollowingMob implements JumpingMobInterface {
   protected JumpingMobStats jumpStats = new JumpingMobStats(this);

   public MountFollowingJumpingMob(int var1) {
      super(var1);
   }

   protected void calcAcceleration(float var1, float var2, float var3, float var4, float var5) {
      boolean var6 = this.inLiquid();
      if (var6) {
         super.calcAcceleration(var1, var2, var3, var4, var5);
      } else {
         Mob var7 = this.getRider();
         boolean var8 = var7 != null && var7.isPlayer;
         if (!var8) {
            this.tickJump(var3, var4);
         } else if (this.isClient() && this.getLevel().getClient().getPlayer() == var7) {
            this.tickJump(var3, var4, (var1x, var2x) -> {
               this.runJump(var1x, var2x);
               this.getLevel().getClient().network.sendPacket(new PacketMountMobJump(this, var1x, var2x));
            });
         }

         super.calcAcceleration(var1, var2, 0.0F, 0.0F, var5);
      }

   }

   public JumpingMobStats getJumpStats() {
      return this.jumpStats;
   }
}
