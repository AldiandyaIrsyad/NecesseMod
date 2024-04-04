package necesse.entity.mobs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class MobHitCooldowns {
   private HashMap<Integer, Long> hitCooldowns;
   public int hitCooldown;

   public MobHitCooldowns(int var1) {
      this.hitCooldowns = new HashMap();
      this.hitCooldown = var1;
   }

   public MobHitCooldowns() {
      this(500);
   }

   public void setupPacket(PacketWriter var1, boolean var2, long var3) {
      if (var2) {
         List var5 = (List)this.hitCooldowns.entrySet().stream().filter((var2x) -> {
            return (Long)var2x.getValue() > var3;
         }).collect(Collectors.toList());
         var1.putNextShortUnsigned(var5.size());
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            var1.putNextInt((Integer)var7.getKey());
            var1.putNextInt((int)((Long)var7.getValue() - var3));
         }
      } else {
         var1.putNextShortUnsigned(this.hitCooldowns.size());
         Iterator var8 = this.hitCooldowns.entrySet().iterator();

         while(var8.hasNext()) {
            Map.Entry var9 = (Map.Entry)var8.next();
            var1.putNextInt((Integer)var9.getKey());
            var1.putNextInt((int)((Long)var9.getValue() - var3));
         }
      }

   }

   public void applyPacket(PacketReader var1, long var2) {
      int var4 = var1.getNextShortUnsigned();

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var1.getNextInt();
         long var7 = var2 + (long)var1.getNextInt();
         this.hitCooldowns.put(var6, var7);
      }

   }

   public boolean canHit(Mob var1, long var2, int var4) {
      long var5 = (Long)this.hitCooldowns.getOrDefault(var1.getHitCooldownUniqueID(), 0L);
      return var5 - (long)var4 <= var2;
   }

   public boolean canHit(Mob var1, int var2) {
      return this.canHit(var1, var1.getWorldEntity().getTime(), var2);
   }

   public boolean canHit(Mob var1, long var2) {
      return this.canHit(var1, var2, 0);
   }

   public boolean canHit(Mob var1) {
      return this.canHit(var1, var1.getWorldEntity().getTime());
   }

   public void startCooldown(Mob var1, long var2) {
      this.hitCooldowns.put(var1.getHitCooldownUniqueID(), var2 + (long)this.hitCooldown);
   }

   public void startCooldown(Mob var1) {
      this.startCooldown(var1, var1.getWorldEntity().getTime());
   }

   public void resetCooldown(Mob var1) {
      this.hitCooldowns.remove(var1.getHitCooldownUniqueID());
   }

   public void resetCooldowns() {
      this.hitCooldowns.clear();
   }
}
