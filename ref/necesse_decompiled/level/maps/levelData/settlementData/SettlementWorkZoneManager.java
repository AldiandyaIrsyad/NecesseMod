package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import necesse.engine.GameLog;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;

public class SettlementWorkZoneManager {
   public final SettlementLevelData data;
   protected HashMap<Integer, SettlementWorkZone> zones = new HashMap();

   public SettlementWorkZoneManager(SettlementLevelData var1) {
      this.data = var1;
   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.zones.values().iterator();

      while(var2.hasNext()) {
         SettlementWorkZone var3 = (SettlementWorkZone)var2.next();
         if (!var3.shouldRemove()) {
            SaveData var4 = new SaveData("ZONE");
            var4.addUnsafeString("stringID", var3.getStringID());
            SaveData var5 = new SaveData("DATA");
            var3.addSaveData(var5);
            var4.addSaveData(var5);
            var1.addSaveData(var4);
         }
      }

   }

   public SettlementWorkZoneManager(SettlementLevelData var1, LoadData var2) {
      this.data = var1;
      var2.getLoadDataByName("ZONE").stream().filter((var0) -> {
         return var0.isArray();
      }).forEachOrdered((var2x) -> {
         try {
            String var3 = var2x.getUnsafeString("stringID", (String)null);
            if (var3 == null) {
               throw new LoadDataException("Could not load zone stringID");
            }

            LoadData var4 = var2x.getFirstLoadDataByName("DATA");
            if (var4 == null) {
               throw new LoadDataException("Could not lod zone data");
            }

            int var5 = SettlementWorkZoneRegistry.getZoneID(var3);
            if (var5 == -1) {
               throw new LoadDataException("Could not find zone with stringID " + var3);
            }

            SettlementWorkZone var6 = SettlementWorkZoneRegistry.getNewZone(var5);
            var6.applySaveData(var4, this.zones.values());
            if (!var6.shouldRemove()) {
               var6.fixOverlaps((var1x, var2) -> {
                  return this.zones.values().stream().anyMatch((var2x) -> {
                     return var2x.containsTile(var1x, var2);
                  });
               });
               this.zones.put(var6.getUniqueID(), var6);
               var6.init(this);
            }
         } catch (LoadDataException var7) {
            System.err.println("Could not load work zone at level " + var1.getLevel().getIdentifier() + ": " + var7.getMessage());
         } catch (Exception var8) {
            System.err.println("Unknown error loading work zone at level " + var1.getLevel().getIdentifier());
            var8.printStackTrace();
         }

      });
   }

   public void tickSecond() {
      LinkedList var1 = new LinkedList();
      Iterator var2 = this.zones.entrySet().iterator();

      while(true) {
         SettlementWorkZone var4;
         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            var4 = (SettlementWorkZone)var3.getValue();
            if (var4.getUniqueID() != (Integer)var3.getKey()) {
               GameLog.warn.println("Settlement zone had their unique ID changed, causing it to be removed");
               var1.add((Integer)var3.getKey());
            } else if (!var4.isRemoved() && !var4.shouldRemove()) {
               var4.tickSecond();
            } else {
               var1.add((Integer)var3.getKey());
            }
         }

         int var5;
         for(var2 = var1.iterator(); var2.hasNext(); (new SettlementWorkZoneRemovedEvent(var5)).applyAndSendToClientsAt(this.data.getLevel())) {
            var5 = (Integer)var2.next();
            var4 = (SettlementWorkZone)this.zones.remove(var5);
            if (var4 != null) {
               var4.remove();
            }
         }

         return;
      }
   }

   public void tickJobs() {
      Iterator var1 = this.zones.values().iterator();

      while(var1.hasNext()) {
         SettlementWorkZone var2 = (SettlementWorkZone)var1.next();
         var2.tickJobs();
      }

   }

   public HashMap<Integer, SettlementWorkZone> getZones() {
      return this.zones;
   }

   public SettlementWorkZone getZone(int var1) {
      return (SettlementWorkZone)this.zones.get(var1);
   }

   public boolean removeZone(int var1) {
      SettlementWorkZone var2 = this.getZone(var1);
      if (var2 == null) {
         return false;
      } else {
         this.zones.remove(var1);
         var2.remove();
         (new SettlementWorkZoneRemovedEvent(var1)).applyAndSendToClientsAt(this.data.getLevel());
         return true;
      }
   }

   public SettlementWorkZone expandZone(int var1, Rectangle var2, Point var3, ServerClient var4) {
      SettlementWorkZone var5 = this.getZone(var1);
      if (var5 == null) {
         if (var4 != null) {
            (new SettlementWorkZoneRemovedEvent(var1)).applyAndSendToClient(var4);
         }

         return null;
      } else {
         if (var5.expandZone(this.data.getLevel(), var2, var3, (var1x, var2x) -> {
            return this.zones.values().stream().anyMatch((var2) -> {
               return var2.containsTile(var1x, var2x);
            });
         })) {
            (new SettlementWorkZoneChangedEvent(var5)).applyAndSendToClientsAt(this.data.getLevel());
         }

         return var5;
      }
   }

   public void shrinkZone(int var1, Rectangle var2, ServerClient var3) {
      SettlementWorkZone var4 = this.getZone(var1);
      if (var4 == null) {
         if (var3 != null) {
            (new SettlementWorkZoneRemovedEvent(var1)).applyAndSendToClient(var3);
         }
      } else if (var4.shrinkZone(this.data.getLevel(), var2)) {
         if (var4.shouldRemove()) {
            var4.remove();
            (new SettlementWorkZoneRemovedEvent(var1)).applyAndSendToClientsAt(this.data.getLevel());
         } else {
            (new SettlementWorkZoneChangedEvent(var4)).applyAndSendToClientsAt(this.data.getLevel());
         }
      }

   }

   public SettlementWorkZone createZone(int var1, int var2, Rectangle var3, Point var4, ServerClient var5) {
      try {
         SettlementWorkZone var6 = this.getZone(var2);
         if (var6 != null) {
            (new SettlementWorkZoneChangedEvent(var6)).applyAndSendToClient(var5);
         } else {
            SettlementWorkZone var7 = SettlementWorkZoneRegistry.getNewZone(var1);
            var7.setUniqueID(var2);
            var7.expandZone(this.data.getLevel(), var3, var4, (var1x, var2x) -> {
               return this.zones.values().stream().anyMatch((var2) -> {
                  return var2.containsTile(var1x, var2x);
               });
            });
            if (!var7.shouldRemove()) {
               var7.generateDefaultName(this.zones.values());
               this.zones.put(var2, var7);
               var7.init(this);
               (new SettlementWorkZoneChangedEvent(var7)).applyAndSendToClientsAt(this.data.getLevel());
               return var7;
            }

            var7.remove();
            (new SettlementWorkZoneRemovedEvent(var2)).applyAndSendToClient(var5);
         }
      } catch (Exception var8) {
         (new SettlementWorkZoneRemovedEvent(var2)).applyAndSendToClient(var5);
      }

      return null;
   }
}
