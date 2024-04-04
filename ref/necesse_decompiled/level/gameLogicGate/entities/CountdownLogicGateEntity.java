package necesse.level.gameLogicGate.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.presets.PresetRotation;

public class CountdownLogicGateEntity extends LogicGateEntity {
   public boolean[] startInputs;
   public boolean[] resetInputs;
   public boolean[] wireOutputs;
   public int totalCountdownTime;
   public boolean[] relayDirections;
   public boolean reverseRelayActive;
   private boolean currentlyActive;
   private int currentCountdownTime;
   private ArrayList<ArrayList<CountdownRelayLogicGateEntity>> currentRelays;

   public CountdownLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.startInputs = new boolean[4];
      this.resetInputs = new boolean[4];
      this.wireOutputs = new boolean[4];
      this.totalCountdownTime = 200;
      this.relayDirections = new boolean[4];
   }

   public CountdownLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("startInputs", this.startInputs);
      var1.addSmallBooleanArray("resetInputs", this.resetInputs);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
      var1.addInt("totalCountdownTime", this.totalCountdownTime);
      var1.addSmallBooleanArray("relayDirections", this.relayDirections);
      var1.addBoolean("reverseRelayActive", this.reverseRelayActive);
      var1.addBoolean("currentlyActive", this.currentlyActive);
      var1.addInt("currentCountdownTime", this.currentCountdownTime);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.startInputs = var1.getSmallBooleanArray("startInputs", this.startInputs);
      this.resetInputs = var1.getSmallBooleanArray("resetInputs", this.resetInputs);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.totalCountdownTime = var1.getInt("totalCountdownTime", this.totalCountdownTime);
      this.relayDirections = var1.getSmallBooleanArray("relayDirections", this.relayDirections);
      this.reverseRelayActive = var1.getBoolean("reverseRelayActive", this.reverseRelayActive);
      this.currentlyActive = var1.getBoolean("currentlyActive", this.currentlyActive);
      this.currentCountdownTime = var1.getInt("currentCountdownTime", this.currentCountdownTime);
      this.updateOutputs(true);
   }

   public void applyPresetRotation(boolean var1, boolean var2, PresetRotation var3) {
      super.applyPresetRotation(var1, var2, var3);
      applyPresetRotationToDirectionArray(this.relayDirections, var1, var2, var3);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.startInputs[var2]);
         var1.putNextBoolean(this.resetInputs[var2]);
         var1.putNextBoolean(this.wireOutputs[var2]);
      }

      var1.putNextInt(this.totalCountdownTime);
      boolean[] var6 = this.relayDirections;
      int var3 = var6.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         boolean var5 = var6[var4];
         var1.putNextBoolean(var5);
      }

      var1.putNextBoolean(this.reverseRelayActive);
      var1.putNextBoolean(this.currentlyActive);
      var1.putNextInt(this.currentCountdownTime);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      int var2;
      for(var2 = 0; var2 < 4; ++var2) {
         this.startInputs[var2] = var1.getNextBoolean();
         this.resetInputs[var2] = var1.getNextBoolean();
         this.wireOutputs[var2] = var1.getNextBoolean();
      }

      this.totalCountdownTime = var1.getNextInt();

      for(var2 = 0; var2 < this.relayDirections.length; ++var2) {
         this.relayDirections[var2] = var1.getNextBoolean();
      }

      this.reverseRelayActive = var1.getNextBoolean();
      this.currentlyActive = var1.getNextBoolean();
      this.currentCountdownTime = var1.getNextInt();
      if (this.isServer()) {
         this.updateOutputs(true);
      }

   }

   public void tick() {
      super.tick();
      if (this.isServer()) {
         if (this.currentlyActive) {
            boolean var1 = false;

            for(int var2 = 0; var2 < 4; ++var2) {
               if (this.startInputs[var2] && this.isWireActive(var2)) {
                  var1 = true;
                  break;
               }
            }

            if (!var1) {
               ++this.currentCountdownTime;
               if (this.currentRelays == null) {
                  this.currentRelays = this.getRelayList();
               }

               this.updateRelays(var1);
               if (this.currentCountdownTime >= this.totalCountdownTime - 1) {
                  if (this.currentCountdownTime >= this.totalCountdownTime) {
                     this.currentlyActive = false;
                     this.currentCountdownTime = 0;
                  }

                  this.updateOutputs(false);
               }
            }
         }

      }
   }

   protected void onUpdate(int var1, boolean var2) {
      if (this.isServer()) {
         boolean var3 = false;

         for(int var4 = 0; var4 < 4; ++var4) {
            if (this.startInputs[var4] && this.isWireActive(var4)) {
               var3 = true;
               break;
            }
         }

         if (var3) {
            this.currentCountdownTime = 0;
            this.currentlyActive = true;
            this.currentRelays = this.getRelayList();
            this.updateRelays(var3);
            this.updateOutputs(false);
         } else if (this.resetInputs[var1] && var2 && this.currentlyActive) {
            this.currentCountdownTime = 0;
            this.currentlyActive = false;
            this.currentRelays = this.getRelayList();
            this.updateRelays(var3);
            this.updateOutputs(false);
         }

      }
   }

   public void updateOutputs(boolean var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         boolean var3 = this.wireOutputs[var2] && this.currentCountdownTime == this.totalCountdownTime - 1;
         this.setOutput(var2, var3, var1);
      }

   }

   protected void updateRelays(boolean var1) {
      if (var1) {
         Iterator var2 = this.currentRelays.iterator();

         while(var2.hasNext()) {
            ArrayList var3 = (ArrayList)var2.next();
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               CountdownRelayLogicGateEntity var5 = (CountdownRelayLogicGateEntity)var4.next();
               var5.setActive(!this.reverseRelayActive);
            }
         }
      } else {
         float var10 = 1.0F / (float)this.currentRelays.size();
         float var11 = (float)this.currentCountdownTime / (float)this.totalCountdownTime;

         for(int var12 = 0; var12 < this.currentRelays.size(); ++var12) {
            ArrayList var13 = (ArrayList)this.currentRelays.get(var12);
            if (this.currentlyActive) {
               float var14 = (float)var12 / (float)this.currentRelays.size();
               boolean var15 = var11 <= var14 + var10 / 2.0F;
               if (this.reverseRelayActive) {
                  var15 = !var15;
               }

               Iterator var8 = var13.iterator();

               while(var8.hasNext()) {
                  CountdownRelayLogicGateEntity var9 = (CountdownRelayLogicGateEntity)var8.next();
                  var9.setActive(var15);
               }
            } else {
               Iterator var6 = var13.iterator();

               while(var6.hasNext()) {
                  CountdownRelayLogicGateEntity var7 = (CountdownRelayLogicGateEntity)var6.next();
                  var7.setActive(this.reverseRelayActive);
               }
            }
         }
      }

   }

   protected ArrayList<ArrayList<CountdownRelayLogicGateEntity>> getRelayList() {
      ArrayList var1 = new ArrayList();
      HashSet var2 = new HashSet();
      ArrayList var3 = new ArrayList();

      for(int var4 = 0; var4 < this.relayDirections.length; ++var4) {
         if (this.relayDirections[var4]) {
            addNextRelay(this.level, var3, var2, this.tileX, this.tileY, var4);
         }
      }

      if (!var3.isEmpty()) {
         var1.add(var3);

         while(true) {
            ArrayList var9 = (ArrayList)var1.get(var1.size() - 1);
            ArrayList var5 = new ArrayList();
            Iterator var6 = var9.iterator();

            while(var6.hasNext()) {
               CountdownRelayLogicGateEntity var7 = (CountdownRelayLogicGateEntity)var6.next();

               for(int var8 = 0; var8 < var7.relayDirections.length; ++var8) {
                  if (var7.relayDirections[var8]) {
                     addNextRelay(var7.level, var5, var2, var7.tileX, var7.tileY, var8);
                  }
               }
            }

            if (var5.isEmpty()) {
               break;
            }

            var1.add(var5);
         }
      }

      return var1;
   }

   public static void addNextRelay(Level var0, ArrayList<CountdownRelayLogicGateEntity> var1, HashSet<Point> var2, int var3, int var4, int var5) {
      Function var6;
      switch (var5) {
         case 0:
            var6 = (var2x) -> {
               return new Point(var3, var4 - var2x);
            };
            break;
         case 1:
            var6 = (var2x) -> {
               return new Point(var3 + var2x, var4);
            };
            break;
         case 2:
            var6 = (var2x) -> {
               return new Point(var3, var4 + var2x);
            };
            break;
         case 3:
            var6 = (var2x) -> {
               return new Point(var3 - var2x, var4);
            };
            break;
         default:
            return;
      }

      for(int var7 = 1; var7 < 20; ++var7) {
         Point var8 = (Point)var6.apply(var7);
         if (!var2.contains(var8)) {
            LogicGateEntity var9 = var0.logicLayer.getEntity(var8.x, var8.y);
            if (var9 instanceof CountdownRelayLogicGateEntity) {
               CountdownRelayLogicGateEntity var10 = (CountdownRelayLogicGateEntity)var9;
               var1.add(var10);
               var2.add(var8);
               return;
            }
         }
      }

   }

   public static GameMessage getRelayDirName(int var0) {
      switch (var0) {
         case 0:
            return new LocalMessage("ui", "dirnorth");
         case 1:
            return new LocalMessage("ui", "direast");
         case 2:
            return new LocalMessage("ui", "dirsouth");
         case 3:
            return new LocalMessage("ui", "dirwest");
         default:
            return new LocalMessage("ui", "countdownrelaynone");
      }
   }

   public static void applyPresetRotationToDirectionArray(boolean[] var0, boolean var1, boolean var2, PresetRotation var3) {
      boolean var4;
      boolean var5;
      if (var1) {
         var4 = var0[1];
         var5 = var0[3];
         var0[1] = var5;
         var0[3] = var4;
      }

      if (var2) {
         var4 = var0[0];
         var5 = var0[2];
         var0[0] = var5;
         var0[2] = var4;
      }

      if (var3 != null) {
         for(int var9 = 0; var9 < var3.dirOffset; ++var9) {
            var5 = var0[0];
            boolean var6 = var0[1];
            boolean var7 = var0[2];
            boolean var8 = var0[3];
            var0[0] = var8;
            var0[1] = var5;
            var0[2] = var6;
            var0[3] = var7;
         }
      }

   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      var3.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.startInputs)));
      var3.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.COUNTDOWN_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }
}
