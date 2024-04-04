package necesse.entity.objectEntity.interfaces;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEUseUpdate;
import necesse.engine.network.packet.PacketOEUseUpdateFull;
import necesse.engine.network.packet.PacketOEUseUpdateFullRequest;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public interface OEUsers {
   default Users constructUsersObject(long var1) {
      return new Users(var1);
   }

   Users getUsersObject();

   boolean canUse(Mob var1);

   default void startUser(Mob var1) {
      Level var2 = var1.getLevel();
      if (!var2.isClient()) {
         boolean var3 = this.isInUse();
         Users var4 = this.getUsersObject();
         int var5 = var1.getUniqueID();
         UserTime var6 = (UserTime)var4.users.get(var5);
         long var7 = var1.getWorldEntity().getTime();
         if (var6 == null) {
            UserTime var9 = new UserTime(var7);
            var4.users.put(var5, var9);
            if (var2.isServer()) {
               var2.getServer().network.sendToClientsAt(new PacketOEUseUpdate(this, var5, true), (Level)var2);
            }

            this.onUsageChanged(var1, true);
            var9.hasTriggeredMobUsageChanged = true;
            if (!var3) {
               this.onIsInUseChanged(true);
            }
         } else {
            var6.refreshTime = var7;
         }

      }
   }

   default void stopUser(Mob var1) {
      Level var2 = var1.getLevel();
      if (var2 != null && !var2.isClient()) {
         int var3 = var1.getUniqueID();
         UserTime var4 = (UserTime)this.getUsersObject().users.remove(var3);
         if (var4 != null) {
            this.onUsageChanged(var1, false);
            if (var2.isServer()) {
               var2.getServer().network.sendToClientsAt(new PacketOEUseUpdate(this, var3, false), (Level)var2);
            }

            if (!this.isInUse()) {
               this.onIsInUseChanged(false);
            }
         }

      }
   }

   default boolean isMobUsing(Mob var1) {
      Users var2 = this.getUsersObject();
      return var2.users.containsKey(var1.getUniqueID());
   }

   default boolean isClientUsing(ServerClient var1) {
      return var1.playerMob != null && this.isMobUsing(var1.playerMob);
   }

   default boolean isInUse() {
      return !this.getUsersObject().users.isEmpty();
   }

   default int getTotalUsers() {
      return this.getUsersObject().users.size();
   }

   default Collection<Integer> getUserUniqueIDs() {
      return this.getUsersObject().users.keySet();
   }

   default Stream<Mob> streamUsers(Level var1) {
      return this.getUserUniqueIDs().stream().map((var1x) -> {
         return GameUtils.getLevelMob(var1x, var1, false);
      }).filter(Objects::nonNull);
   }

   default void submitUpdatePacket(ObjectEntity var1, PacketOEUseUpdateFull var2) {
      this.getUsersObject().readUsersSpawnPacket(new PacketReader(var2.content), var1);
   }

   default void submitUpdatePacket(ObjectEntity var1, PacketOEUseUpdate var2) {
      Level var3 = var1.getLevel();
      boolean var4 = this.isInUse();
      Users var5 = this.getUsersObject();
      if (var2.isUsing) {
         UserTime var6 = new UserTime(var3.getWorldEntity().getTime());
         var5.users.put(var2.mobUniqueID, var6);
         Mob var7 = GameUtils.getLevelMob(var2.mobUniqueID, var3, false);
         if (var7 != null && var7.getLevel() != null) {
            this.onUsageChanged(var7, true);
            var6.hasTriggeredMobUsageChanged = true;
         }
      } else {
         var5.users.remove(var2.mobUniqueID);
         Mob var8 = GameUtils.getLevelMob(var2.mobUniqueID, var3, false);
         if (var8 != null && var8.getLevel() != null) {
            this.onUsageChanged(var8, false);
         } else {
            this.onUnknownUsageStopped(var2.mobUniqueID);
         }
      }

      boolean var9 = this.isInUse();
      if (var4 != var9) {
         this.onIsInUseChanged(var9);
      }

      if (var2.totalUsers != this.getUsersObject().users.size() && var1.isClient()) {
         var1.getLevel().getClient().network.sendPacket(new PacketOEUseUpdateFullRequest(this));
      }

   }

   void onUsageChanged(Mob var1, boolean var2);

   void onIsInUseChanged(boolean var1);

   default void onUnknownUsageStopped(int var1) {
   }

   public static class Users {
      private HashMap<Integer, UserTime> users = new HashMap();
      private long timeoutTime;

      public Users(long var1) {
         this.timeoutTime = var1;
      }

      public void serverTick(ObjectEntity var1) {
         if (this.timeoutTime > -1L && !this.users.isEmpty()) {
            Level var2 = var1.getLevel();
            OEUsers var3 = (OEUsers)var1;
            long var4 = var1.getWorldEntity().getTime();
            LinkedList var6 = new LinkedList();
            Iterator var7 = this.users.entrySet().iterator();

            while(var7.hasNext()) {
               Map.Entry var8 = (Map.Entry)var7.next();
               UserTime var9 = (UserTime)var8.getValue();
               if (var9.refreshTime + this.timeoutTime < var4) {
                  var6.add((Integer)var8.getKey());
               }
            }

            var7 = var6.iterator();

            while(var7.hasNext()) {
               int var10 = (Integer)var7.next();
               this.users.remove(var10);
               Mob var11 = GameUtils.getLevelMob(var10, var2, false);
               if (var11 != null) {
                  var3.onUsageChanged(var11, false);
               } else {
                  var3.onUnknownUsageStopped(var10);
               }

               if (var2.isServer()) {
                  var2.getServer().network.sendToClientsAt(new PacketOEUseUpdate(var3, var10, false), (Level)var2);
               }
            }

            if (!var6.isEmpty() && !var3.isInUse()) {
               var3.onIsInUseChanged(false);
            }

         }
      }

      public void clientTick(ObjectEntity var1) {
         Iterator var2 = this.users.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            UserTime var4 = (UserTime)var3.getValue();
            if (!var4.hasTriggeredMobUsageChanged) {
               OEUsers var5 = (OEUsers)var1;
               Mob var6 = GameUtils.getLevelMob((Integer)var3.getKey(), var1.getLevel(), false);
               if (var6 != null) {
                  var5.onUsageChanged(var6, true);
                  var4.hasTriggeredMobUsageChanged = true;
               }
            }
         }

      }

      public void writeUsersSpawnPacket(PacketWriter var1) {
         var1.putNextShortUnsigned(this.users.size());
         Iterator var2 = this.users.keySet().iterator();

         while(var2.hasNext()) {
            int var3 = (Integer)var2.next();
            var1.putNextInt(var3);
         }

      }

      public void readUsersSpawnPacket(PacketReader var1, ObjectEntity var2) {
         Level var3 = var2.getLevel();
         OEUsers var4 = (OEUsers)var2;
         boolean var5 = var4.isInUse();
         int var6 = var1.getNextShortUnsigned();
         HashSet var7 = new HashSet();

         for(int var8 = 0; var8 < var6; ++var8) {
            var7.add(var1.getNextInt());
         }

         LinkedList var13 = new LinkedList();
         Iterator var9 = this.users.keySet().iterator();

         int var10;
         while(var9.hasNext()) {
            var10 = (Integer)var9.next();
            if (!var7.contains(var10)) {
               var13.add(var10);
            }
         }

         var9 = var13.iterator();

         while(var9.hasNext()) {
            var10 = (Integer)var9.next();
            this.users.remove(var10);
            Mob var11 = (Mob)var3.entityManager.mobs.get(var10, false);
            if (var11 != null) {
               var4.onUsageChanged(var11, false);
            } else {
               var4.onUnknownUsageStopped(var10);
            }
         }

         var9 = var7.iterator();

         while(var9.hasNext()) {
            var10 = (Integer)var9.next();
            if (!this.users.containsKey(var10)) {
               UserTime var15 = new UserTime(var3.getWorldEntity().getTime());
               this.users.put(var10, var15);
               Mob var12 = GameUtils.getLevelMob(var10, var3, false);
               if (var12 != null) {
                  var4.onUsageChanged(var12, true);
                  var15.hasTriggeredMobUsageChanged = true;
               }
            }
         }

         boolean var14 = var4.isInUse();
         if (var5 != var14) {
            var4.onIsInUseChanged(var14);
         }

      }

      public void onRemoved(ObjectEntity var1) {
         OEUsers var2 = (OEUsers)var1;
         Level var3 = var1.getLevel();
         Iterator var4 = this.users.keySet().iterator();

         while(var4.hasNext()) {
            Integer var5 = (Integer)var4.next();
            Mob var6 = GameUtils.getLevelMob(var5, var3, false);
            if (var6 == null) {
               var2.onUnknownUsageStopped(var5);
            } else {
               var2.onUsageChanged(var6, false);
            }
         }

         if (!this.users.isEmpty()) {
            this.users.clear();
            var2.onIsInUseChanged(false);
         }

      }
   }

   public static class UserTime {
      public final long startUsageTime;
      public long refreshTime;
      public boolean hasTriggeredMobUsageChanged;

      public UserTime(long var1) {
         this.startUsageTime = this.refreshTime;
         this.refreshTime = var1;
      }
   }
}
