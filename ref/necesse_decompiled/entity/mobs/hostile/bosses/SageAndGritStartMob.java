package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class SageAndGritStartMob extends Mob {
   public LevelMob<FlyingSpiritsHead> sage;
   public LevelMob<FlyingSpiritsHead> grit;
   public boolean sageDead;
   public boolean gritDead;
   public Point pedestalPosition;

   public SageAndGritStartMob() {
      super(100000);
      this.shouldSave = false;
   }

   public void init() {
      super.init();
      Level var1 = this.getLevel();
      if (this.isServer()) {
         float var2 = (float)GameRandom.globalRandom.nextInt(360);
         float var3 = (float)Math.cos(Math.toRadians((double)var2));
         float var4 = (float)Math.sin(Math.toRadians((double)var2));
         float var5 = GameMath.fixAngle(var2 + 180.0F);
         float var6 = (float)Math.cos(Math.toRadians((double)var5));
         float var7 = (float)Math.sin(Math.toRadians((double)var5));
         float var8 = 960.0F;
         FlyingSpiritsHead var9 = (FlyingSpiritsHead)MobRegistry.getMob("grit", var1);
         FlyingSpiritsHead var10 = (FlyingSpiritsHead)MobRegistry.getMob("sage", var1);
         var9.setLevel(var1);
         var10.setLevel(var1);
         var9.friend.uniqueID = var10.getUniqueID();
         var10.friend.uniqueID = var9.getUniqueID();
         var9.pedestalPosition = this.pedestalPosition;
         var10.pedestalPosition = this.pedestalPosition;
         var1.entityManager.addMob(var9, (float)(this.getX() + (int)(var3 * var8)), (float)(this.getY() + (int)(var4 * var8)));
         var1.entityManager.addMob(var10, (float)(this.getX() + (int)(var6 * var8)), (float)(this.getY() + (int)(var7 * var8)));
         this.grit = new LevelMob(var9);
         this.sage = new LevelMob(var10);
      }

   }

   public void serverTick() {
      super.serverTick();
      boolean var1 = true;
      FlyingSpiritsHead var2;
      if (this.sage != null) {
         var2 = (FlyingSpiritsHead)this.sage.get(this.getLevel());
         if (var2 != null && !var2.removed()) {
            var1 = false;
         } else if (var2 != null && var2.hasDied()) {
            this.sageDead = true;
         }
      }

      if (this.grit != null) {
         var2 = (FlyingSpiritsHead)this.grit.get(this.getLevel());
         if (var2 != null && !var2.removed()) {
            var1 = false;
         } else if (var2 != null && var2.hasDied()) {
            this.gritDead = true;
         }
      }

      if (var1) {
         this.remove(0.0F, 0.0F, (Attacker)null, this.sageDead && this.gritDead);
      }

   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
   }

   public boolean isHealthBarVisible() {
      return false;
   }

   public boolean canTakeDamage() {
      return false;
   }

   public boolean countDamageDealt() {
      return false;
   }

   public Point getLootDropsPosition(ServerClient var1) {
      Mob var2 = null;
      Mob var3 = null;
      if (this.sage != null) {
         var2 = (Mob)this.getLevel().entityManager.mobs.get(this.sage.uniqueID, true);
      }

      if (this.grit != null) {
         var3 = (Mob)this.getLevel().entityManager.mobs.get(this.grit.uniqueID, true);
      }

      if (var2 != null && var3 != null) {
         if (var2.removedTime != 0L && var2.removedTime < var3.removedTime) {
            return var2.getLootDropsPosition(var1);
         } else {
            return var3.removedTime != 0L && var3.removedTime < var2.removedTime ? var3.getLootDropsPosition(var1) : var2.getLootDropsPosition(var1);
         }
      } else if (var2 != null) {
         return var2.getLootDropsPosition(var1);
      } else {
         return var3 != null ? var3.getLootDropsPosition(var1) : super.getLootDropsPosition(var1);
      }
   }
}
