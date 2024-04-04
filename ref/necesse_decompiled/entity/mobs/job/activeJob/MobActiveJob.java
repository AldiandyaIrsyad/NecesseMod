package necesse.entity.mobs.job.activeJob;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public abstract class MobActiveJob<T extends Mob> extends EntityActiveJob<T> {
   public MobActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, T var3, GameTileRange var4) {
      super(var1, var2, var3, var4);
   }

   public MobMovement getDirectMoveTo(Mob var1) {
      return new MobMovementRelative(var1, 0.0F, 0.0F);
   }

   public boolean hasProjectileLOS() {
      return this.hasProjectileLOS(((Mob)this.target).x, ((Mob)this.target).y);
   }

   public boolean hasProjectileLOS(float var1, float var2) {
      Mob var3 = this.worker.getMobWorker();
      Line2D.Float var4 = new Line2D.Float(var3.x, var3.y, var1, var2);
      Mob var5 = (Mob)GameUtils.castRayFirstHit(var4, 100.0, (var2x) -> {
         Stream var3x = this.getLevel().entityManager.mobs.streamInRegionsShape(var2x, 1);
         Stream var4 = this.getLevel().entityManager.players.streamInRegionsShape(var2x, 1);
         Stream var10000 = Stream.concat(var3x, var4).filter((var1) -> {
            return var1 != var3;
         }).filter((var1) -> {
            Rectangle var2 = var1.getCollision();
            return var2x.intersects(var2) || GameMath.getPerpendicularLine(var2x, 10.0F).intersects(var2) || GameMath.getPerpendicularLine(var2x, -10.0F).intersects(var2);
         });
         Objects.requireNonNull(var3);
         return (Mob)var10000.min(Comparator.comparingDouble(var3::getDistance)).orElse((Object)null);
      });
      return var5 == null || var5 == this.target;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public MobMovement getDirectMoveTo(Entity var1) {
      return this.getDirectMoveTo((Mob)var1);
   }
}
