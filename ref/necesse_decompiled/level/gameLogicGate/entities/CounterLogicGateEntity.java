package necesse.level.gameLogicGate.entities;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class CounterLogicGateEntity extends LogicGateEntity {
   public boolean[] incInputs;
   public boolean[] decInputs;
   public boolean[] resetInputs;
   public boolean[] wireOutputs;
   public int currentValue;
   protected int maxValue;

   public CounterLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.incInputs = new boolean[4];
      this.decInputs = new boolean[4];
      this.resetInputs = new boolean[4];
      this.wireOutputs = new boolean[4];
      this.currentValue = 0;
      this.maxValue = 1;
   }

   public CounterLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("incInputs", this.incInputs);
      var1.addSmallBooleanArray("decInputs", this.decInputs);
      var1.addSmallBooleanArray("resetInputs", this.resetInputs);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
      var1.addInt("currentValue", this.currentValue);
      var1.addInt("maxValue", this.maxValue);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.incInputs = var1.getSmallBooleanArray("incInputs", this.incInputs);
      this.decInputs = var1.getSmallBooleanArray("decInputs", this.decInputs);
      this.resetInputs = var1.getSmallBooleanArray("resetInputs", this.resetInputs);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.currentValue = var1.getInt("currentValue", this.currentValue);
      this.maxValue = var1.getInt("maxValue", this.maxValue);
      this.updateOutputs(true);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.incInputs[var2]);
         var1.putNextBoolean(this.decInputs[var2]);
         var1.putNextBoolean(this.resetInputs[var2]);
         var1.putNextBoolean(this.wireOutputs[var2]);
      }

      var1.putNextShortUnsigned(this.currentValue);
      var1.putNextShortUnsigned(this.maxValue);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         this.incInputs[var2] = var1.getNextBoolean();
         this.decInputs[var2] = var1.getNextBoolean();
         this.resetInputs[var2] = var1.getNextBoolean();
         this.wireOutputs[var2] = var1.getNextBoolean();
      }

      this.currentValue = var1.getNextShortUnsigned();
      this.setMaxValue(var1.getNextShortUnsigned());
      if (this.isServer()) {
         this.updateOutputs(true);
      }

   }

   public void setupOutputUpdate(PacketWriter var1) {
      super.setupOutputUpdate(var1);
      var1.putNextShortUnsigned(this.currentValue);
   }

   public void applyOutputUpdate(PacketReader var1) {
      super.applyOutputUpdate(var1);
      this.currentValue = var1.getNextShortUnsigned();
   }

   public void setMaxValue(int var1) {
      this.maxValue = GameMath.limit(var1, 1, 256);
      if (this.currentValue > this.maxValue) {
         this.currentValue %= this.maxValue + 1;
         this.updateWireOuts = true;
      }

   }

   public int getMaxValue() {
      return this.maxValue;
   }

   protected void onUpdate(int var1, boolean var2) {
      if (this.isServer()) {
         if (var2 && this.resetInputs[var1]) {
            if (this.currentValue != 0) {
               this.currentValue = 0;
               this.updateWireOuts = true;
            }
         } else {
            if (var2 && this.incInputs[var1]) {
               ++this.currentValue;
               if (this.currentValue > this.maxValue) {
                  this.currentValue %= this.maxValue + 1;
               }

               this.updateWireOuts = true;
            }

            if (var2 && this.decInputs[var1]) {
               --this.currentValue;
               if (this.currentValue < 0) {
                  this.currentValue = this.maxValue + 1 + this.currentValue % (this.maxValue + 1);
               }

               this.updateWireOuts = true;
            }
         }

         this.updateOutputs(false);
      }
   }

   public void updateOutputs(boolean var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         boolean var3 = this.wireOutputs[var2] && this.currentValue == this.maxValue;
         this.setOutput(var2, var3, var1);
      }

   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      var3.add(Localization.translate("logictooltips", "counterinc", "value", this.getWireTooltip(this.incInputs)));
      var3.add(Localization.translate("logictooltips", "counterdec", "value", this.getWireTooltip(this.decInputs)));
      var3.add(Localization.translate("logictooltips", "rsreset", "value", this.getWireTooltip(this.resetInputs)));
      var3.add(Localization.translate("logictooltips", "countervalue", "value", this.currentValue, "max", this.maxValue));
      var3.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.COUNTER_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }
}
