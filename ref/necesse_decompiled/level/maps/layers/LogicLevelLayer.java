package necesse.level.maps.layers;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLogicGateUpdate;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class LogicLevelLayer extends LevelLayer {
   private final HashMap<Point, LogicGateEntity> entities;
   protected boolean[] hasGate;

   public LogicLevelLayer(Level var1) {
      super(var1);
      this.hasGate = new boolean[var1.width * var1.height];
      this.entities = new HashMap();
   }

   protected int getDataIndex(int var1, int var2) {
      return var1 + var2 * this.level.width;
   }

   public boolean hasGate(int var1, int var2) {
      return var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height ? this.hasGate[this.getDataIndex(var1, var2)] : false;
   }

   public void init() {
   }

   public void clientTick() {
      Iterator var1 = this.entities.values().iterator();

      while(var1.hasNext()) {
         LogicGateEntity var2 = (LogicGateEntity)var1.next();
         var2.tick();
      }

   }

   public void serverTick() {
      Iterator var1 = this.entities.values().iterator();

      while(var1.hasNext()) {
         LogicGateEntity var2 = (LogicGateEntity)var1.next();
         var2.tick();
      }

   }

   public void onLoadingComplete() {
      for(int var1 = 0; var1 < this.level.width; ++var1) {
         for(int var2 = 0; var2 < this.level.height; ++var2) {
            if (this.hasGate(var1, var2)) {
               LogicGateEntity var3 = this.getEntity(var1, var2);
               if (var3 == null) {
                  var3 = this.getLogicGate(var1, var2).getNewEntity(this.level, var1, var2);
                  this.entities.put(new Point(var1, var2), var3);
               }

               var3.init();

               for(int var4 = 0; var4 < 4; ++var4) {
                  var3.onWireUpdate(var4, this.level.wireManager.isWireActive(var1, var2, var4));
               }
            }
         }
      }

   }

   public int getLogicGateID(int var1, int var2) {
      return this.hasGate(var1, var2) ? this.getEntity(var1, var2).logicGateID : -1;
   }

   public GameLogicGate getLogicGate(int var1, int var2) {
      return LogicGateRegistry.getLogicGate(this.getLogicGateID(var1, var2));
   }

   public LogicGateEntity getEntity(int var1, int var2) {
      return (LogicGateEntity)this.entities.get(new Point(var1, var2));
   }

   public void clearLogicGate(int var1, int var2) {
      this.hasGate[this.getDataIndex(var1, var2)] = false;
      this.entities.computeIfPresent(new Point(var1, var2), (var0, var1x) -> {
         var1x.remove();
         return null;
      });
   }

   public void setLogicGate(int var1, int var2, int var3, PacketReader var4) {
      if (var3 == -1) {
         this.clearLogicGate(var1, var2);
      } else {
         GameLogicGate var5 = LogicGateRegistry.getLogicGate(var3);
         if (!this.level.isLoadingComplete()) {
            this.hasGate[this.getDataIndex(var1, var2)] = true;
            this.entities.put(new Point(var1, var2), var5.getNewEntity(this.level, var1, var2));
         } else if (!this.hasGate(var1, var2)) {
            boolean[] var6 = new boolean[4];

            for(int var7 = 0; var7 < 4; ++var7) {
               var6[var7] = this.isWireActive(var1, var2, var7);
            }

            this.hasGate[this.getDataIndex(var1, var2)] = true;
            LogicGateEntity var9 = var5.getNewEntity(this.level, var1, var2);
            this.entities.compute(new Point(var1, var2), (var1x, var2x) -> {
               if (var2x != null) {
                  var2x.remove();
               }

               return var9;
            });
            var9.init();
            if (var4 != null) {
               var9.applyPacket(var4);
               var9.applyPacketEvents.triggerEvent(new LogicGateEntity.ApplyPacketEvent());
            }

            for(int var8 = 0; var8 < 4; ++var8) {
               if (this.isWireActive(var1, var2, var8) != var6[var8]) {
                  this.level.wireManager.updateWire(var1, var2, var8, !var6[var8]);
               }
            }
         } else if (var4 != null) {
            this.entities.compute(new Point(var1, var2), (var1x, var2x) -> {
               if (var2x != null) {
                  var2x.applyPacket(var4);
                  var2x.applyPacketEvents.triggerEvent(new LogicGateEntity.ApplyPacketEvent());
               }

               return var2x;
            });
         }

      }
   }

   public boolean isWireActive(int var1, int var2, int var3) {
      if (this.hasGate(var1, var2)) {
         LogicGateEntity var4 = (LogicGateEntity)this.entities.get(new Point(var1, var2));
         return var4.getOutput(var3);
      } else {
         return false;
      }
   }

   public void onWireUpdate(int var1, int var2, int var3, boolean var4) {
      if (this.hasGate(var1, var2)) {
         LogicGateEntity var5 = (LogicGateEntity)this.entities.get(new Point(var1, var2));
         var5.onWireUpdate(var3, var4);
      }

   }

   public PacketLogicGateUpdate getUpdatePacket(int var1, int var2) {
      if (this.hasGate(var1, var2)) {
         LogicGateEntity var3 = this.getEntity(var1, var2);
         return new PacketLogicGateUpdate(this.level, var1, var2, var3.logicGateID, var3);
      } else {
         return new PacketLogicGateUpdate(this.level, var1, var2, -1, (LogicGateEntity)null);
      }
   }

   public void addSaveData(SaveData var1) {
      SaveData var2 = new SaveData("LOGICGATES");
      Iterator var3 = this.entities.values().iterator();

      while(var3.hasNext()) {
         LogicGateEntity var4 = (LogicGateEntity)var3.next();
         var2.addSaveData(var4.getSaveData("LOGICGATE"));
      }

      var1.addSaveData(var2);
   }

   public void loadSaveData(LoadData var1) {
      LoadData var2 = var1.getFirstLoadDataByName("LOGICGATES");
      if (var2 != null) {
         Iterator var3 = var2.getLoadDataByName("LOGICGATE").iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();

            try {
               LogicGateEntity var5 = LogicGateEntity.loadEntity(this.level, var4, true);
               this.entities.put(new Point(var5.tileX, var5.tileY), var5);
               this.hasGate[this.getDataIndex(var5.tileX, var5.tileY)] = true;
            } catch (LogicGateEntity.LogicGateLoadException var6) {
               GameLog.warn.println(var6.getMessage());
               if (var6.getCause() != null) {
                  var6.getCause().printStackTrace();
               }
            }
         }
      } else {
         GameLog.warn.println("Could not find logic gate entity data");
      }

   }

   public void writeRegionPacket(Region var1, PacketWriter var2) {
      for(int var3 = 0; var3 < var1.regionWidth; ++var3) {
         int var4 = var1.getLevelX(var3);

         for(int var5 = 0; var5 < var1.regionHeight; ++var5) {
            int var6 = var1.getLevelY(var5);
            if (this.hasGate(var4, var6)) {
               var2.putNextBoolean(true);
               LogicGateEntity var7 = (LogicGateEntity)this.entities.get(new Point(var4, var6));
               var2.putNextShortUnsigned(var7.logicGateID);
               var7.writePacket(var2);
            } else {
               var2.putNextBoolean(false);
            }
         }
      }

   }

   public boolean readRegionPacket(Region var1, PacketReader var2) {
      for(int var3 = 0; var3 < var1.regionWidth; ++var3) {
         int var4 = var1.getLevelX(var3);

         for(int var5 = 0; var5 < var1.regionHeight; ++var5) {
            int var6 = var1.getLevelY(var5);
            if (var2.getNextBoolean()) {
               int var7 = var2.getNextShortUnsigned();
               this.hasGate[this.getDataIndex(var4, var6)] = true;
               GameLogicGate var8 = LogicGateRegistry.getLogicGate(var7);
               if (var8 == null) {
                  return false;
               }

               LogicGateEntity var9 = var8.getNewEntity(this.level, var4, var6);
               this.entities.compute(new Point(var4, var6), (var1x, var2x) -> {
                  if (var2x != null) {
                     var2x.remove();
                  }

                  return var9;
               });
               var9.applyPacket(var2);
               var9.applyPacketEvents.triggerEvent(new LogicGateEntity.ApplyPacketEvent());
               var9.init();
            } else {
               this.hasGate[this.getDataIndex(var4, var6)] = false;
               this.entities.computeIfPresent(new Point(var4, var6), (var0, var1x) -> {
                  var1x.remove();
                  return null;
               });
            }
         }
      }

      return true;
   }

   public void unloadRegion(Region var1) {
      for(int var2 = 0; var2 < var1.regionWidth; ++var2) {
         int var3 = var1.getLevelX(var2);

         for(int var4 = 0; var4 < var1.regionHeight; ++var4) {
            int var5 = var1.getLevelY(var4);
            this.clearLogicGate(var3, var5);
         }
      }

   }
}
