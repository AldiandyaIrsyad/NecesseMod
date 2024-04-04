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

public class SRLatchLogicGateEntity extends LogicGateEntity {
   public boolean[] activateInputs;
   public boolean[] resetInputs;
   public boolean[] wireOutputs;
   boolean active;

   public SRLatchLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.activateInputs = new boolean[4];
      this.resetInputs = new boolean[4];
      this.wireOutputs = new boolean[4];
      this.active = false;
   }

   public SRLatchLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("activateInputs", this.activateInputs);
      var1.addSmallBooleanArray("resetInputs", this.resetInputs);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
      var1.addBoolean("active", this.active);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.activateInputs = var1.getSmallBooleanArray("activateInputs", this.activateInputs);
      this.resetInputs = var1.getSmallBooleanArray("resetInputs", this.resetInputs);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.active = var1.getBoolean("active", false);
      this.updateOutputs(true);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.activateInputs[var2]);
         var1.putNextBoolean(this.resetInputs[var2]);
         var1.putNextBoolean(this.wireOutputs[var2]);
      }

      var1.putNextBoolean(this.active);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         this.activateInputs[var2] = var1.getNextBoolean();
         this.resetInputs[var2] = var1.getNextBoolean();
         this.wireOutputs[var2] = var1.getNextBoolean();
      }

      this.active = var1.getNextBoolean();
      if (this.isServer()) {
         this.updateOutputs(true);
      }

   }

   protected void onUpdate(int var1, boolean var2) {
      if (this.isServer()) {
         int var3;
         for(var3 = 0; var3 < 4; ++var3) {
            if (this.isWireActive(var3) && this.activateInputs[var3]) {
               this.active = true;
               break;
            }
         }

         for(var3 = 0; var3 < 4; ++var3) {
            if (this.isWireActive(var3) && this.resetInputs[var3]) {
               this.active = false;
               break;
            }
         }

         this.updateOutputs(false);
      }
   }

   public void updateOutputs(boolean var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         boolean var3 = this.wireOutputs[var2] && this.active;
         this.setOutput(var2, var3, var1);
      }

   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      var3.add(Localization.translate("logictooltips", "rsactivate", "value", this.getWireTooltip(this.activateInputs)));
      var3.add(Localization.translate("logictooltips", "rsreset", "value", this.getWireTooltip(this.resetInputs)));
      var3.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
      if (this.active) {
         var3.add(Localization.translate("logictooltips", "logicactive"));
      } else {
         var3.add(Localization.translate("logictooltips", "logicinactive"));
      }

      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.SRLATCH_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }
}
