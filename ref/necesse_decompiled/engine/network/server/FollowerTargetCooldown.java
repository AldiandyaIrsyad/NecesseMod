package necesse.engine.network.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.ai.path.RegionPathfinding;
import necesse.level.maps.Level;

public class FollowerTargetCooldown {
   public final ServerClient owner;
   protected HashMap<Integer, Cooldown> cache = new HashMap();
   protected int minCachedTime = 1000;
   protected int maxCachedTime = 2000;

   public FollowerTargetCooldown(ServerClient var1) {
      this.owner = var1;
   }

   public boolean canMoveTo(Mob var1, int var2, int var3) {
      Level var4 = this.owner.getLevel();
      int var5 = var4.getRegionID(var2, var3);
      PathDoorOption var6 = var4.regionManager.BASIC_DOOR_OPTIONS;
      if (var6 != null) {
         return var6.canMoveToTile(this.owner.playerMob.getTileX(), this.owner.playerMob.getTileY(), var2, var3, var1.canBeTargetedFromAdjacentTiles());
      } else {
         Cooldown var7 = (Cooldown)this.cache.compute(var5, (var5x, var6x) -> {
            return var6x != null && var6x.cooldown > this.owner.playerMob.getWorldEntity().getTime() ? var6x : new Cooldown(RegionPathfinding.canMoveToTile(this.owner.playerMob.getLevel(), this.owner.playerMob.getTileX(), this.owner.playerMob.getTileY(), var2, var3, var6, var1.canBeTargetedFromAdjacentTiles()), this.owner.playerMob.getWorldEntity().getTime() + (long)GameRandom.globalRandom.getIntBetween(this.minCachedTime, this.maxCachedTime));
         });
         return var7.canMoveTo;
      }
   }

   public boolean canMoveTo(Mob var1) {
      return this.canMoveTo(var1, var1.getTileX(), var1.getTileY());
   }

   public void cleanCache() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.cache.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Cooldown var4 = (Cooldown)var3.getValue();
         if (var4.cooldown <= this.owner.playerMob.getWorldEntity().getTime()) {
            var1.add((Integer)var3.getKey());
         }
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         Integer var5 = (Integer)var2.next();
         this.cache.remove(var5);
      }

   }

   protected static class Cooldown {
      public boolean canMoveTo;
      public long cooldown;

      public Cooldown(boolean var1, long var2) {
         this.canMoveTo = var1;
         this.cooldown = var2;
      }
   }
}
