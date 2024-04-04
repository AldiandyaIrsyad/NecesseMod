package necesse.level.gameLogicGate.entities;

import java.util.ArrayList;
import necesse.engine.GameEventsHandler;
import necesse.engine.GameState;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketLogicGateOutputUpdate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;
import necesse.level.maps.presets.PresetRotation;

public abstract class LogicGateEntity implements GameState, WorldEntityGameClock {
   public final int logicGateID;
   public final Level level;
   public final int tileX;
   public final int tileY;
   private ArrayList<UpdateCooldown> updateCooldowns;
   protected boolean updateWireOuts;
   private boolean[] outputs;
   private boolean isRemoved;
   public GameEventsHandler<ApplyPacketEvent> applyPacketEvents = new GameEventsHandler(true);

   public LogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      this.logicGateID = var1.getID();
      this.level = var2;
      this.tileX = var3;
      this.tileY = var4;
      this.updateCooldowns = new ArrayList();
      this.updateWireOuts = false;
      this.outputs = new boolean[4];
   }

   public final SaveData getSaveData(String var1) {
      SaveData var2 = new SaveData(var1);
      var2.addInt("tileX", this.tileX);
      var2.addInt("tileY", this.tileY);
      var2.addUnsafeString("stringID", this.getLogicGate().getStringID());
      this.addSaveData(var2);
      return var2;
   }

   public static LogicGateEntity loadEntity(Level var0, LoadData var1, boolean var2) throws LogicGateLoadException {
      int var3 = var1.getInt("tileX", Integer.MIN_VALUE, var2);
      int var4 = var1.getInt("tileY", Integer.MIN_VALUE, var2);
      if (var3 != Integer.MIN_VALUE && var4 != Integer.MIN_VALUE) {
         String var5 = var1.getUnsafeString("stringID", (String)null, var2);
         if (var5 == null) {
            throw new LogicGateLoadException("Failed to load logic gate: No stringID");
         } else {
            int var6 = LogicGateRegistry.getLogicGateID(var5);
            if (var6 == -1) {
               throw new LogicGateLoadException("Failed to load logic gate: Invalid stringID " + var5);
            } else {
               try {
                  GameLogicGate var7 = LogicGateRegistry.getLogicGate(var6);
                  LogicGateEntity var8 = var7.getNewEntity(var0, var3, var4);
                  var8.applyLoadData(var1);
                  return var8;
               } catch (Exception var9) {
                  throw new LogicGateLoadException("Error loading logic gate entity: " + var3 + ", " + var4 + ", " + var5, var9);
               }
            }
         }
      } else {
         throw new LogicGateLoadException("Failed to load a logic gate: Missing position");
      }
   }

   public void addSaveData(SaveData var1) {
      var1.addSmallBooleanArray("outputs", this.outputs);
   }

   public void applyLoadData(LoadData var1) {
      this.outputs = var1.getSmallBooleanArray("outputs");
   }

   public void applyPresetRotation(boolean var1, boolean var2, PresetRotation var3) {
   }

   public void writePacket(PacketWriter var1) {
      boolean[] var2 = this.outputs;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         boolean var5 = var2[var4];
         var1.putNextBoolean(var5);
      }

   }

   public void applyPacket(PacketReader var1) {
      for(int var2 = 0; var2 < this.outputs.length; ++var2) {
         boolean var3 = var1.getNextBoolean();
         if (this.outputs[var2] != var3) {
            this.outputs[var2] = var3;
            this.level.wireManager.updateWire(this.tileX, this.tileY, var2, this.outputs[var2]);
         }
      }

   }

   public void setupOutputUpdate(PacketWriter var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.outputs[var2]);
      }

   }

   public void applyOutputUpdate(PacketReader var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         boolean var3 = var1.getNextBoolean();
         if (this.outputs[var2] != var3) {
            this.outputs[var2] = var3;
            this.level.wireManager.updateWire(this.tileX, this.tileY, var2, var3);
         }
      }

   }

   public void init() {
   }

   public void tick() {
      this.updateCooldowns.clear();
      if (this.isServer() && this.updateWireOuts) {
         this.level.getServer().network.sendToClientsWithTile(new PacketLogicGateOutputUpdate(this), this.level, this.tileX, this.tileY);

         for(int var1 = 0; var1 < 4; ++var1) {
            this.level.wireManager.updateWire(this.tileX, this.tileY, var1, this.outputs[var1]);
         }

         this.updateWireOuts = false;
      }

   }

   protected void onUpdate(int var1, boolean var2) {
   }

   public final void onWireUpdate(int var1, boolean var2) {
      UpdateCooldown var3 = new UpdateCooldown(var1, var2);
      boolean var4 = this.updateCooldowns.contains(var3);
      if (!var4) {
         this.updateCooldowns.add(var3);
         this.onUpdate(var1, var2);
      }
   }

   public final void setOutput(int var1, boolean var2) {
      this.setOutput(var1, var2, false);
   }

   public final void setOutput(int var1, boolean var2, boolean var3) {
      boolean var4 = this.outputs[var1] != var2;
      if (var4 || var3) {
         this.outputs[var1] = var2;
         if (var4) {
            this.updateWireOuts = true;
         }

         this.level.wireManager.updateWire(this.tileX, this.tileY, var1, var2);
      }

   }

   public final boolean getOutput(int var1) {
      return this.outputs[var1];
   }

   public final GameLogicGate getLogicGate() {
      return LogicGateRegistry.getLogicGate(this.logicGateID);
   }

   public boolean isWireActive(int var1) {
      return this.level.wireManager.isWireActive(this.tileX, this.tileY, var1);
   }

   public WorldEntity getWorldEntity() {
      return this.level == null ? null : this.level.getWorldEntity();
   }

   public boolean isClient() {
      return this.level != null && this.level.isClient();
   }

   public Client getClient() {
      return this.level == null ? null : this.level.getClient();
   }

   public boolean isServer() {
      return this.level != null && this.level.isServer();
   }

   public Server getServer() {
      return this.level == null ? null : this.level.getServer();
   }

   public void remove() {
      this.isRemoved = true;
   }

   public boolean isRemoved() {
      return this.isRemoved;
   }

   public void sendUpdatePacket() {
      if (this.isServer()) {
         this.level.getServer().network.sendToClientsWithTile(this.level.logicLayer.getUpdatePacket(this.tileX, this.tileY), this.level, this.tileX, this.tileY);
      }
   }

   public abstract void openContainer(ServerClient var1);

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      return new ListGameTooltips(this.getLogicGate().getDisplayName());
   }

   protected String getWireTooltip(boolean[] var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < 4; ++var3) {
         if (var1[var3]) {
            var2.append("RGBY".charAt(var3));
         }
      }

      return var2.toString();
   }

   public static class LogicGateLoadException extends Exception {
      public LogicGateLoadException(String var1) {
         super(var1);
      }

      public LogicGateLoadException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   private static class UpdateCooldown {
      public final int wireID;
      public final boolean active;

      public UpdateCooldown(int var1, boolean var2) {
         this.wireID = var1;
         this.active = var2;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof UpdateCooldown)) {
            return super.equals(var1);
         } else {
            UpdateCooldown var2 = (UpdateCooldown)var1;
            return this.wireID == var2.wireID && this.active == var2.active;
         }
      }
   }

   public static class ApplyPacketEvent {
      public ApplyPacketEvent() {
      }
   }
}
