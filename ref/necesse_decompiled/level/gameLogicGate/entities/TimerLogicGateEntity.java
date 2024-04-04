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

public class TimerLogicGateEntity extends LogicGateEntity {
   public boolean[] wireInputs;
   public boolean[] wireOutputs;
   public int timerTicks;
   public int timer;
   private boolean isRunning;
   private boolean active;
   private boolean firstTick;

   public TimerLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.wireInputs = new boolean[4];
      this.wireOutputs = new boolean[4];
      this.timerTicks = 20;
      this.firstTick = true;
   }

   public TimerLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("wireInputs", this.wireInputs);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
      var1.addInt("timerTicks", this.timerTicks);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.wireInputs = var1.getSmallBooleanArray("wireInputs", this.wireInputs);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.timerTicks = var1.getInt("timerTicks", this.timerTicks);
      this.updateOutputs(true);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.putNextBoolean(this.wireInputs[var2]);
         var1.putNextBoolean(this.wireOutputs[var2]);
      }

      var1.putNextShortUnsigned(this.timerTicks);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         this.wireInputs[var2] = var1.getNextBoolean();
         this.wireOutputs[var2] = var1.getNextBoolean();
      }

      this.timerTicks = var1.getNextShortUnsigned();
      this.updateRunning();
      if (this.isServer()) {
         this.updateOutputs(true);
      }

   }

   public void tick() {
      super.tick();
      if (this.isServer()) {
         this.tickActive();
      }

      if (this.firstTick) {
         this.updateRunning();
      }

   }

   private void tickActive() {
      boolean var1;
      if (this.isRunning) {
         ++this.timer;
         this.timer %= this.timerTicks;
         var1 = this.active;
         this.active = this.timer == 0;
         if (var1 != this.active) {
            this.updateOutputs(false);
         }
      } else {
         this.timer = 0;
         var1 = this.active;
         this.active = false;
         if (var1) {
            this.updateOutputs(false);
         }
      }

   }

   protected void onUpdate(int var1, boolean var2) {
      this.updateRunning();
      if (this.isServer()) {
         this.updateOutputs(false);
      }
   }

   public void updateRunning() {
      this.isRunning = false;

      for(int var1 = 0; var1 < 4; ++var1) {
         if (this.wireInputs[var1] && this.isWireActive(var1)) {
            this.isRunning = true;
            break;
         }
      }

      if (!this.isRunning) {
         this.timer = 0;
         this.active = false;
      }

   }

   public void updateOutputs(boolean var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         boolean var3 = this.wireOutputs[var2] && this.active;
         this.setOutput(var2, var3, var1);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public ListGameTooltips getTooltips(PlayerMob var1, boolean var2) {
      ListGameTooltips var3 = super.getTooltips(var1, var2);
      var3.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.wireInputs)));
      var3.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
      var3.add(Localization.translate("logictooltips", "timerticks", "value", (Object)this.timerTicks));
      var3.add(Localization.translate("logictooltips", "timercurrent", "value", (Object)this.timer));
      if (this.isRunning) {
         var3.add(Localization.translate("logictooltips", "timeractive"));
      } else {
         var3.add(Localization.translate("logictooltips", "timerinactive"));
      }

      return var3;
   }

   public void openContainer(ServerClient var1) {
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.TIMER_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }
}
