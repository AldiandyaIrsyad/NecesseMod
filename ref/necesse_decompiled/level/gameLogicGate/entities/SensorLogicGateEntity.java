package necesse.level.gameLogicGate.entities;

import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class SensorLogicGateEntity extends LogicGateEntity {
   public boolean[] wireOutputs;
   private boolean active;
   public int range;
   public boolean players;
   public boolean passiveMobs;
   public boolean hostileMobs;

   public SensorLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.wireOutputs = new boolean[4];
      this.active = false;
      this.players = true;
      this.passiveMobs = true;
      this.hostileMobs = true;
      this.range = 5;
   }

   public SensorLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
      var1.addBoolean("players", this.players);
      var1.addBoolean("passiveMobs", this.passiveMobs);
      var1.addBoolean("hostileMobs", this.hostileMobs);
      var1.addInt("range", this.range);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.players = var1.getBoolean("players", true);
      this.passiveMobs = var1.getBoolean("passiveMobs", true);
      this.hostileMobs = var1.getBoolean("hostileMobs", true);
      this.range = var1.getInt("range", 5);
      this.updateOutputs(true);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.wireOutputs[var2]);
      }

      var1.putNextBoolean(this.players);
      var1.putNextBoolean(this.passiveMobs);
      var1.putNextBoolean(this.hostileMobs);
      var1.putNextByteUnsigned(this.range);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         this.wireOutputs[var2] = var1.getNextBoolean();
      }

      this.players = var1.getNextBoolean();
      this.passiveMobs = var1.getNextBoolean();
      this.hostileMobs = var1.getNextBoolean();
      this.range = var1.getNextByteUnsigned();
   }

   public void tick() {
      super.tick();
      if (this.isServer()) {
         this.checkCollision();
      }

   }

   private void checkCollision() {
      boolean var1 = this.level.entityManager.mobs.getInRegionByTileRange(this.tileX, this.tileY, this.range).stream().anyMatch((var1x) -> {
         return var1x.canLevelInteract() && (var1x.isHostile && this.hostileMobs || !var1x.isHostile && this.passiveMobs);
      });
      if (!var1 && this.players) {
         Rectangle var2 = new Rectangle(this.tileX * 32 - this.range * 32, this.tileY * 32 - this.range * 32, (this.range * 2 + 1) * 32, (this.range * 2 + 1) * 32);
         if (this.isServer()) {
            var1 = GameUtils.streamServerClients(this.level).anyMatch((var1x) -> {
               return var1x.playerMob != null && var1x.hasSpawned() && var2.contains(var1x.playerMob.getPositionPoint());
            });
         }
      }

      if (this.active != var1) {
         this.active = var1;
         this.updateOutputs(false);
      }

   }

   public boolean isActive() {
      return this.active;
   }

   public void updateOutputs(boolean var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         boolean var3 = this.wireOutputs[var2] && this.active;
         this.setOutput(var2, var3, var1);
      }

   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      if (this.players) {
         var3.add(Localization.translate("logictooltips", "sensorplayeron"));
      } else {
         var3.add(Localization.translate("logictooltips", "sensorplayeroff"));
      }

      if (this.hostileMobs) {
         var3.add(Localization.translate("logictooltips", "sensorhostileon"));
      } else {
         var3.add(Localization.translate("logictooltips", "sensorhostileoff"));
      }

      if (this.passiveMobs) {
         var3.add(Localization.translate("logictooltips", "sensorpassiveon"));
      } else {
         var3.add(Localization.translate("logictooltips", "sensorpassiveoff"));
      }

      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.SENSOR_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }
}
