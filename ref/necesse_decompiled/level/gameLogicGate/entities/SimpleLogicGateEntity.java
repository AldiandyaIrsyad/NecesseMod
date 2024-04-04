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

public abstract class SimpleLogicGateEntity extends LogicGateEntity {
   public boolean[] wireInputs;
   public boolean[] wireOutputs;

   public SimpleLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.wireInputs = new boolean[4];
      this.wireOutputs = new boolean[4];
   }

   public SimpleLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("wireInputs", this.wireInputs);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.wireInputs = var1.getSmallBooleanArray("wireInputs", this.wireInputs);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.updateOutputs(true);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.wireInputs[var2]);
         var1.putNextBoolean(this.wireOutputs[var2]);
      }

   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         this.wireInputs[var2] = var1.getNextBoolean();
         this.wireOutputs[var2] = var1.getNextBoolean();
      }

      if (this.isServer()) {
         this.updateOutputs(true);
      }

   }

   public void onUpdate(int var1, boolean var2) {
      if (this.isServer()) {
         this.updateOutputs(false);
      }
   }

   public void updateOutputs(boolean var1) {
      boolean var2 = this.condition();

      for(int var3 = 0; var3 < 4; ++var3) {
         boolean var4 = this.wireOutputs[var3] && var2;
         this.setOutput(var3, var4, var1);
      }

   }

   public abstract boolean condition();

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.SIMPLE_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      var3.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.wireInputs)));
      var3.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
      return var3;
   }
}
