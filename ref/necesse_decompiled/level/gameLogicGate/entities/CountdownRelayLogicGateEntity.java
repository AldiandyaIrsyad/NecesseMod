package necesse.level.gameLogicGate.entities;

import necesse.engine.localization.Localization;
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

public class CountdownRelayLogicGateEntity extends LogicGateEntity {
   public boolean[] wireOutputs;
   public boolean[] relayDirections;
   private boolean isActive;

   public CountdownRelayLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.wireOutputs = new boolean[4];
      this.relayDirections = new boolean[4];
   }

   public CountdownRelayLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
      var1.addSmallBooleanArray("relayDirections", this.relayDirections);
      var1.addBoolean("isActive", this.isActive);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.relayDirections = var1.getSmallBooleanArray("relayDirections", this.relayDirections);
      this.isActive = var1.getBoolean("isActive", this.isActive);
      this.updateOutputs(true);
   }

   public void applyPresetRotation(boolean var1, boolean var2, PresetRotation var3) {
      super.applyPresetRotation(var1, var2, var3);
      CountdownLogicGateEntity.applyPresetRotationToDirectionArray(this.relayDirections, var1, var2, var3);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.wireOutputs[var2]);
      }

      boolean[] var6 = this.relayDirections;
      int var3 = var6.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         boolean var5 = var6[var4];
         var1.putNextBoolean(var5);
      }

      var1.putNextBoolean(this.isActive);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      int var2;
      for(var2 = 0; var2 < 4; ++var2) {
         this.wireOutputs[var2] = var1.getNextBoolean();
      }

      for(var2 = 0; var2 < this.relayDirections.length; ++var2) {
         this.relayDirections[var2] = var1.getNextBoolean();
      }

      this.isActive = var1.getNextBoolean();
      if (this.isServer()) {
         this.updateOutputs(true);
      }

   }

   public void setActive(boolean var1) {
      if (this.isActive != var1) {
         this.isActive = var1;
         this.updateOutputs(false);
      }
   }

   protected void onUpdate(int var1, boolean var2) {
   }

   public void updateOutputs(boolean var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         boolean var3 = this.wireOutputs[var2] && this.isActive;
         this.setOutput(var2, var3, var1);
      }

   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      var3.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.COUNTDOWN_RELAY_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }
}
