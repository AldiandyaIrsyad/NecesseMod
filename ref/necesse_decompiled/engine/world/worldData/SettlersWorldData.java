package necesse.engine.world.worldData;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.MobSave;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.SmokePuffLevelEvent;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlersWorldData extends WorldData {
   private HashMap<Integer, SettlerData> settlers = new HashMap();

   public SettlersWorldData() {
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (!this.settlers.isEmpty()) {
         SaveData var2 = new SaveData("settlers");
         Iterator var3 = this.settlers.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            int var5 = (Integer)var4.getKey();
            SettlerData var6 = (SettlerData)var4.getValue();
            SaveData var7 = new SaveData("settler");
            var7.addInt("uniqueID", var5);
            var7.addLong("returnTime", var6.returnTime);
            var7.addSaveData(MobSave.getSave("mob", var6.mob));
            var2.addSaveData(var7);
         }

         var1.addSaveData(var2);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.settlers.clear();
      LoadData var2 = var1.getFirstLoadDataByName("settlers");
      if (var2 != null) {
         Iterator var3 = var2.getLoadDataByName("settler").iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();

            try {
               int var5 = var4.getInt("uniqueID");
               long var6 = var4.getLong("returnTime");
               Mob var8 = MobSave.loadSave(var4.getFirstLoadDataByName("mob"), (Level)null);
               if (var8 instanceof HumanMob) {
                  this.settlers.put(var5, new SettlerData(var6, (HumanMob)var8));
               }
            } catch (Exception var9) {
               System.err.println("Error loading world settler:");
               var9.printStackTrace();
            }
         }
      }

   }

   public void tick() {
      super.tick();
   }

   public HumanMob getSettler(int var1) {
      SettlerData var2 = (SettlerData)this.settlers.get(var1);
      return var2 != null ? var2.mob : null;
   }

   public boolean exists(int var1) {
      return this.settlers.containsKey(var1);
   }

   public void remove(int var1) {
      this.settlers.remove(var1);
   }

   public boolean returnIfShould(int var1, SettlementLevelData var2) {
      SettlerData var3 = (SettlerData)this.settlers.get(var1);
      if (var3 != null) {
         if (var3.returnTime < this.worldEntity.getTime()) {
            this.returnToSettlement(var1, var3, var2, false);
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean returnToSettlement(HumanMob var1, boolean var2) {
      SettlerData var3 = (SettlerData)this.settlers.get(var1.getUniqueID());
      if (var3 != null) {
         SettlementLevelData var4 = var3.mob.getSettlementLevelData();
         if (var4 != null) {
            this.returnToSettlement(var1.getUniqueID(), var3, var4, var2);
            return true;
         }
      }

      return false;
   }

   public static Point getReturnPos(HumanMob var0, SettlementLevelData var1) {
      Point var2 = var1.getObjectEntityPos();
      Point var3;
      if (var2 != null && (var2.x != -1 || var2.y != -1)) {
         var3 = PortalObjectEntity.getTeleportDestinationAroundObject(var1.getLevel(), var0, var2.x, var2.y, true);
      } else {
         var3 = SettlementLevelData.findRandomSpawnLevelPos(var1.getLevel(), var0, false);
      }

      return var3;
   }

   private void returnToSettlement(int var1, SettlerData var2, SettlementLevelData var3, boolean var4) {
      Point var5 = getReturnPos(var2.mob, var3);
      if (var2.mob.getLevel() != null) {
         if (var4) {
            TeleportEvent var6 = new TeleportEvent(var2.mob, 0, var3.getLevel().getIdentifier(), 0.0F, (Function)null, (var2x) -> {
               return new TeleportResult(true, var3.getLevel().getIdentifier(), var5.x, var5.y);
            });
            var2.mob.getLevel().entityManager.addLevelEvent(var6);
         } else {
            var2.mob.getLevel().entityManager.addLevelEvent(new SmokePuffLevelEvent(var2.mob.x, var2.mob.y, 64, new Color(50, 50, 50)));
            var2.mob.getLevel().entityManager.changeMobLevel(var2.mob, var3.getLevel(), var5.x, var5.y, true);
         }
      } else {
         float var10002 = (float)var5.x;
         var3.getLevel().entityManager.addMob(var2.mob, var10002, (float)var5.y);
      }

      var3.onReturned(var1);
      var2.mob.clearCommandsOrders((ServerClient)null);
      this.settlers.remove(var1);
   }

   public void refreshWorldSettler(HumanMob var1, boolean var2) {
      SettlerData var3 = (SettlerData)this.settlers.get(var1.getUniqueID());
      if (var3 == null) {
         var3 = new SettlerData(0L, var1);
         this.settlers.put(var1.getUniqueID(), var3);
      }

      var3.mob = var1;
      if (var2) {
         var3.returnTime = this.worldEntity.getTime() + 5000L;
      }

   }

   public static SettlersWorldData getSettlersData(Server var0) {
      WorldData var1 = var0.world.worldEntity.getWorldData("settlers");
      if (var1 instanceof SettlersWorldData) {
         return (SettlersWorldData)var1;
      } else {
         SettlersWorldData var2 = new SettlersWorldData();
         var0.world.worldEntity.addWorldData("settlers", var2);
         return var2;
      }
   }

   private static class SettlerData {
      public long returnTime;
      public HumanMob mob;

      public SettlerData(long var1, HumanMob var3) {
         this.returnTime = var1;
         this.mob = var3;
      }
   }
}
