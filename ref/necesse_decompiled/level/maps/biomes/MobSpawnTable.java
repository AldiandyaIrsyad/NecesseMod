package necesse.level.maps.biomes;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class MobSpawnTable {
   private final LinkedList<MobSpawnTable> includes = new LinkedList();
   private final LinkedList<MobChance> table = new LinkedList();

   public static CanSpawnPredicate canSpawnEither(int var0, int var1, int var2) {
      return (var3, var4, var5) -> {
         return var4.characterStats().time_played.get() >= var3.getWorldEntity().getDayTimeMax() * var0 || var4.playerMob.getArmor() >= (float)var1 || var4.playerMob.getMaxHealthFlat() >= var2;
      };
   }

   public MobSpawnTable() {
   }

   public MobSpawnTable include(MobSpawnTable var1) {
      this.includes.add(var1);
      return this;
   }

   public MobSpawnTable clear() {
      this.includes.clear();
      this.table.clear();
      return this;
   }

   public MobSpawnTable add(MobChance var1) {
      this.table.add(var1);
      return this;
   }

   public MobSpawnTable addLimited(int var1, String var2, int var3, int var4, Predicate<Mob> var5) {
      return this.add(var1, (var3x, var4x, var5x) -> {
         Point var6 = new Point(var5x.x * 32 + 16, var5x.y * 32 + 16);
         long var7 = var3x.entityManager.mobs.streamInRegionsShape(GameUtils.rangeBounds(var6.x, var6.y, var4), 0).filter(var5).filter((var2) -> {
            return var2.getDistance((float)var6.x, (float)var6.y) <= (float)var4;
         }).count();
         return var7 < (long)var3;
      }, var2);
   }

   public MobSpawnTable addLimited(int var1, String var2, int var3, int var4) {
      return this.addLimited(var1, var2, var3, var4, (var1x) -> {
         return var1x.getStringID().equals(var2);
      });
   }

   public MobSpawnTable add(int var1, final CanSpawnPredicate var2, final MobProducer var3) {
      return this.add(new MobChance(var1) {
         public boolean canSpawn(Level var1, ServerClient var2x, Point var3x) {
            return var2.canSpawn(var1, var2x, var3x);
         }

         public Mob getMob(Level var1, ServerClient var2x, Point var3x) {
            return var3.getMob(var1, var2x, var3x);
         }
      });
   }

   public MobSpawnTable add(int var1, CanSpawnPredicate var2, String var3) {
      return this.add(var1, var2, (var1x, var2x, var3x) -> {
         return MobRegistry.getMob(var3, var1x);
      });
   }

   public MobSpawnTable add(int var1, String var2) {
      return this.add(var1, (var0, var1x, var2x) -> {
         return true;
      }, var2);
   }

   public MobSpawnTable add(int var1, MobProducer var2) {
      return this.add(var1, (var0, var1x, var2x) -> {
         return true;
      }, var2);
   }

   public MobSpawnTable withoutRandomMob(MobChance var1) {
      MobSpawnTable var2 = new MobSpawnTable();
      Iterator var3 = this.includes.iterator();

      while(var3.hasNext()) {
         MobSpawnTable var4 = (MobSpawnTable)var3.next();
         var2.include(var4.withoutRandomMob(var1));
      }

      var2.table.addAll(this.table);
      var2.table.remove(var1);
      return var2;
   }

   public MobChance getRandomMob(Level var1, ServerClient var2, Point var3, GameRandom var4) {
      int var5 = 0;
      LinkedList var6 = new LinkedList();
      var5 = this.addCanSpawns(var6, var5, var1, var2, var3);
      if (var5 <= 0) {
         return null;
      } else {
         int var7 = var4.nextInt(var5);
         int var8 = 0;

         MobChance var10;
         for(Iterator var9 = var6.iterator(); var9.hasNext(); var8 += var10.tickets) {
            var10 = (MobChance)var9.next();
            if (var7 >= var8 && var7 < var8 + var10.tickets) {
               return var10;
            }
         }

         return null;
      }
   }

   private int addCanSpawns(LinkedList<MobChance> var1, int var2, Level var3, ServerClient var4, Point var5) {
      Iterator var6;
      MobSpawnTable var7;
      for(var6 = this.includes.iterator(); var6.hasNext(); var2 = var7.addCanSpawns(var1, var2, var3, var4, var5)) {
         var7 = (MobSpawnTable)var6.next();
      }

      var6 = this.table.iterator();

      while(var6.hasNext()) {
         MobChance var8 = (MobChance)var6.next();
         if (var8.canSpawn(var3, var4, var5)) {
            var1.add(var8);
            var2 += var8.tickets;
         }
      }

      return var2;
   }

   @FunctionalInterface
   public interface CanSpawnPredicate {
      boolean canSpawn(Level var1, ServerClient var2, Point var3);
   }

   @FunctionalInterface
   public interface MobProducer {
      Mob getMob(Level var1, ServerClient var2, Point var3);
   }
}
