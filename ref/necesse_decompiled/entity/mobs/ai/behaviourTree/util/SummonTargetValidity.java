package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.ai.behaviourTree.AINode;

public class SummonTargetValidity<T extends Mob> extends TargetValidity<T> {
   public String baseKey = "mobBase";

   public SummonTargetValidity() {
   }

   public boolean isValidTarget(AINode<T> var1, T var2, Mob var3, boolean var4) {
      if (!super.isValidTarget(var1, var2, var3, var4)) {
         return false;
      } else {
         Point var5 = (Point)var1.getBlackboard().getObjectNotNull(Point.class, this.baseKey, new Point(var2.getX(), var2.getY()));
         if (var3.getLevel().getLightLevel(var3).getLevel() <= 0.0F && !var3.isBoss() && var3.getDistance((float)var5.x, (float)var5.y) >= 192.0F) {
            return false;
         } else {
            if (!var3.isHostile) {
               ServerClient var6 = var2.getFollowingServerClient();
               if (var6 != null && var3 != var6.summonFocus) {
                  return false;
               }
            }

            PathDoorOption var7 = var2.getLevel().regionManager.BASIC_DOOR_OPTIONS;
            return var7.canMoveToTile(var5.x / 32, var5.y / 32, var3.getTileX(), var3.getTileY(), false);
         }
      }
   }
}
