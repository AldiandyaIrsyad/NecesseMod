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

public class TFlipFlopLogicGateEntity extends LogicGateEntity {
   public boolean[] wireInputs;
   public boolean[] wireOutputs1;
   public boolean[] wireOutputs2;
   private boolean changeCooldown;
   private boolean flipped;

   public TFlipFlopLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.wireInputs = new boolean[4];
      this.wireOutputs1 = new boolean[4];
      this.wireOutputs2 = new boolean[4];
      this.flipped = false;
   }

   public TFlipFlopLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("wireInputs", this.wireInputs);
      var1.addSmallBooleanArray("wireOutputs1", this.wireOutputs1);
      var1.addSmallBooleanArray("wireOutputs2", this.wireOutputs2);
      var1.addBoolean("flipped", this.flipped);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.wireInputs = var1.getSmallBooleanArray("wireInputs", this.wireInputs);
      this.wireOutputs1 = var1.getSmallBooleanArray("wireOutputs1", this.wireOutputs1);
      this.wireOutputs2 = var1.getSmallBooleanArray("wireOutputs2", this.wireOutputs2);
      this.flipped = var1.getBoolean("flipped", false);
      this.updateOutputs(true);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.wireInputs[var2]);
         var1.putNextBoolean(this.wireOutputs1[var2]);
         var1.putNextBoolean(this.wireOutputs2[var2]);
      }

      var1.putNextBoolean(this.flipped);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         this.wireInputs[var2] = var1.getNextBoolean();
         this.wireOutputs1[var2] = var1.getNextBoolean();
         this.wireOutputs2[var2] = var1.getNextBoolean();
      }

      this.flipped = var1.getNextBoolean();
      if (this.isServer()) {
         this.updateOutputs(true);
      }

   }

   public void tick() {
      super.tick();
      this.changeCooldown = false;
   }

   protected void onUpdate(int var1, boolean var2) {
      if (this.isServer()) {
         if (!this.changeCooldown) {
            this.changeCooldown = true;
            if (this.wireInputs[var1] && var2) {
               this.flipped = !this.flipped;
            }

            this.updateOutputs(false);
         }
      }
   }

   public void updateOutputs(boolean var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         boolean var3 = this.wireOutputs1[var2] && !this.flipped || this.wireOutputs2[var2] && this.flipped;
         this.setOutput(var2, var3, var1);
      }

   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      var3.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.wireInputs)));
      var3.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs1) + ", " + this.getWireTooltip(this.wireOutputs2)));
      if (this.flipped) {
         var3.add(Localization.translate("logictooltips", "tflipflopout1"));
      } else {
         var3.add(Localization.translate("logictooltips", "tflipflopout2"));
      }

      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.TFLIPFLOP_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }
}
