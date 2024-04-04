package necesse.level.gameLogicGate.entities;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
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

public class BufferLogicGateEntity extends LogicGateEntity {
   public boolean[] wireInputs;
   public boolean[] wireOutputs;
   public int delayTicks;
   public long tickCounter;
   private LinkedList<ActiveChange> changes;
   private boolean active;

   public BufferLogicGateEntity(GameLogicGate var1, Level var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.wireInputs = new boolean[4];
      this.wireOutputs = new boolean[4];
      this.delayTicks = 20;
      this.changes = new LinkedList();
   }

   public BufferLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      this(var1, var2.level, var2.tileX, var2.tileY);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSmallBooleanArray("wireInputs", this.wireInputs);
      var1.addSmallBooleanArray("wireOutputs", this.wireOutputs);
      var1.addInt("delayTicks", this.delayTicks);
      var1.addBoolean("active", this.active);
      SaveData var2 = new SaveData("changes");
      Iterator var3 = this.changes.iterator();

      while(var3.hasNext()) {
         ActiveChange var4 = (ActiveChange)var3.next();
         SaveData var5 = new SaveData("");
         var5.addLong("tick", var4.tick - this.tickCounter);
         var5.addBoolean("active", var4.active);
         var2.addSaveData(var5);
      }

      if (!var2.isEmpty()) {
         var1.addSaveData(var2);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.wireInputs = var1.getSmallBooleanArray("wireInputs", this.wireInputs);
      this.wireOutputs = var1.getSmallBooleanArray("wireOutputs", this.wireOutputs);
      this.delayTicks = var1.getInt("delayTicks", this.delayTicks);
      this.active = var1.getBoolean("active", this.active);
      this.changes.clear();
      LoadData var2 = var1.getFirstLoadDataByName("changes");
      if (var2 != null) {
         Iterator var3 = var2.getLoadData().iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();
            long var5 = var4.getLong("tick", -1L, false);
            if (var5 != -1L) {
               boolean var7 = var4.getBoolean("active", false, false);
               this.changes.addLast(new ActiveChange(this.tickCounter + var5, var7));
            }
         }

         this.changes.sort(Comparator.comparingLong((var0) -> {
            return var0.tick;
         }));
      }

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
      ++this.tickCounter;
      boolean var1 = this.active;
      if (!this.changes.isEmpty()) {
         ActiveChange var2 = (ActiveChange)this.changes.getFirst();
         if (var2.tick <= this.tickCounter) {
            this.active = var2.active;
            this.changes.removeFirst();
         }
      }

      if (var1 != this.active) {
         this.updateOutputs(false);
      }

   }

   protected void onUpdate(int var1, boolean var2) {
      if (this.isServer()) {
         boolean var3 = false;

         for(int var4 = 0; var4 < 4; ++var4) {
            if (this.wireInputs[var4] && this.isWireActive(var4)) {
               var3 = true;
               break;
            }
         }

         boolean var8 = this.active;
         if (!this.changes.isEmpty()) {
            var8 = ((ActiveChange)this.changes.getLast()).active;
         }

         if (var3 != var8) {
            long var5 = this.tickCounter + (long)this.delayTicks;
            if (!this.changes.isEmpty() && ((ActiveChange)this.changes.getLast()).tick == var5) {
               ActiveChange var7 = (ActiveChange)this.changes.getLast();
               var7.active = var7.active || var3;
            } else {
               this.changes.addLast(new ActiveChange(var5, var3));
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
      ContainerRegistry.openAndSendContainer(var1, PacketOpenContainer.LevelObject(ContainerRegistry.BUFFER_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
   }

   private static class ActiveChange {
      public final long tick;
      public boolean active;

      public ActiveChange(long var1, boolean var3) {
         this.tick = var1;
         this.active = var3;
      }
   }
}
