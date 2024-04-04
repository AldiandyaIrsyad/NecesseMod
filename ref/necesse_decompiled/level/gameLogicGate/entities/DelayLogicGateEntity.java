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

public class DelayLogicGateEntity extends LogicGateEntity {
   public boolean[] wireInputs;
   public boolean[] wireOutputs;
   public int delayTicks;
   public int ticksToFlip;
   private boolean active;

   public DelayLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.wireInputs = new boolean[4];
      this.wireOutputs = new boolean[4];
      this.delayTicks = 20;
   }

   public DelayLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("wireInputs", this.wireInputs);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
      var1.addInt("delayTicks", this.delayTicks);
      var1.addInt("ticksToFlip", this.ticksToFlip);
      var1.addBoolean("active", this.active);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.wireInputs = var1.getSmallBooleanArray("wireInputs", this.wireInputs);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.delayTicks = var1.getInt("delayTicks", this.delayTicks);
      this.ticksToFlip = var1.getInt("ticksToFlip", this.ticksToFlip);
      this.active = var1.getBoolean("active", this.active);
      this.updateOutputs(true);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.wireInputs[var2]);
         var1.putNextBoolean(this.wireOutputs[var2]);
      }

      var1.putNextShortUnsigned(this.delayTicks);
      var1.putNextBoolean(this.active);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         this.wireInputs[var2] = var1.getNextBoolean();
         this.wireOutputs[var2] = var1.getNextBoolean();
      }

      this.delayTicks = var1.getNextShortUnsigned();
      this.active = var1.getNextBoolean();
      if (this.isServer()) {
         this.updateOutputs(true);
      }

   }

   public void tick() {
      super.tick();
      if (this.isServer()) {
         this.tickActive();
      }

   }

   private void tickActive() {
      if (this.ticksToFlip > 0) {
         --this.ticksToFlip;
         if (this.ticksToFlip <= 0) {
            this.active = !this.active;
            this.updateOutputs(false);
         }
      }

   }

   protected void onUpdate(int var1, boolean var2) {
      if (this.isServer()) {
         if (this.wireInputs[var1]) {
            boolean var3 = false;

            for(int var4 = 0; var4 < 4; ++var4) {
               if (this.wireInputs[var4] && this.isWireActive(var4)) {
                  var3 = true;
                  break;
               }
            }

            if (var3 != this.active && this.ticksToFlip == 0) {
               this.ticksToFlip = this.delayTicks;
            } else {
               this.ticksToFlip = 0;
            }
         }

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
      var3.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.wireInputs)));
      var3.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
      var3.add(Localization.translate("logictooltips", "bufferdelay", "value", (Object)this.delayTicks));
      if (this.active) {
         var3.add(Localization.translate("logictooltips", "logicactive"));
      } else {
         var3.add(Localization.translate("logictooltips", "logicinactive"));
      }

      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.DELAY_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }
}
