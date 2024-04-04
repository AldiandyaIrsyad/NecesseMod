package necesse.engine.network.server;

import java.util.function.BiConsumer;
import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;

public class MobFollower {
   public final String summonType;
   public final Mob mob;
   public final FollowPosition position;
   public final String buffType;
   public final float spaceTaken;
   public final Function<PlayerMob, Integer> maxSpace;
   public final BiConsumer<ServerClient, Mob> updateMob;

   public MobFollower(String var1, Mob var2, FollowPosition var3, String var4, float var5, Function<PlayerMob, Integer> var6, BiConsumer<ServerClient, Mob> var7) {
      this.summonType = var1;
      this.mob = var2;
      this.position = var3;
      this.buffType = var4;
      this.spaceTaken = var5;
      this.maxSpace = var6;
      this.updateMob = var7;
   }
}
