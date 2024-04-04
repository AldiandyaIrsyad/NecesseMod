package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementFlagObjectEntity extends ObjectEntity {
   public static int secondsToTickLevel = 120;
   private int tickCounter;
   private int activeTicks;
   private boolean active = false;
   private int finderTicks;
   private int finderX;
   private int finderY;

   public SettlementFlagObjectEntity(Level var1, int var2, int var3) {
      super(var1, "settlement", var2, var3);
   }

   public Packet getContainerContentPacket(ServerClient var1) {
      Packet var2 = new Packet();
      SettlementLevelData var3 = this.getLevelData();
      PacketWriter var4 = new PacketWriter(var2);
      (new SettlementBasicsEvent(var3)).write(var4);
      return var2;
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
      if (this.isServer()) {
         this.finderTicks = this.getLevel().width * this.getLevel().height / 20 / secondsToTickLevel;
         this.finderX = this.getX();
         this.finderY = this.getY();
         this.activeTicks = GameRandom.globalRandom.nextInt(500);
         this.updateActive();
      }

   }

   public void serverTick() {
      if (this.isServer() && !this.getLevel().getServer().world.settings.unloadSettlements) {
         this.getLevel().unloadLevelBuffer = 0;
      }

      Performance.record(this.getLevel().tickManager(), "settlementoe", (Runnable)(() -> {
         if (this.active) {
            this.tickFinder(this.finderTicks);
         }

         ++this.tickCounter;
         if (this.tickCounter >= 20) {
            this.tickCounter = 0;
            if (this.active) {
               this.getLevelData().tickSettlement(this);
            } else {
               ++this.activeTicks;
               if (this.activeTicks >= 600) {
                  this.updateActive();
                  this.activeTicks = 0;
               }
            }
         }

      }));
   }

   private void tickFinder(int var1) {
      SettlementLevelData var2 = this.getLevelData();

      for(int var3 = 0; var3 < var1; ++var3) {
         ++this.finderX;
         if (this.finderX >= this.getLevel().width) {
            this.finderX = 0;
            ++this.finderY;
            if (this.finderY >= this.getLevel().height) {
               this.finderY = 0;
            }
         }

         this.tickFinder(var2, this.finderX, this.finderY);
      }

   }

   private void tickFinder(SettlementLevelData var1, int var2, int var3) {
      var1.tickTile(var2, var3);
   }

   private void updateActive() {
      SettlementLevelData var1 = this.getLevelData();
      SettlementFlagObjectEntity var2 = var1.getObjectEntity();
      if (var2 != null && var2 != this) {
         this.active = false;
      } else {
         var1.setObjectEntityPos(new Point(this.getX(), this.getY()));
         this.active = true;
      }

   }

   public void remove() {
      if (!this.removed() && this.getLevel() != null && this.isServer()) {
         SettlementLevelData var1 = this.getLevelData();
         if (var1 != null && var1.getObjectEntityPos() != null && var1.getObjectEntityPos().equals(new Point(this.getX(), this.getY()))) {
            var1.setObjectEntityPos((Point)null);
         }
      }

      super.remove();
   }

   public SettlementLevelData getLevelData() {
      return (SettlementLevelData)Performance.record(this.getLevel().tickManager(), "getLevelData", (Supplier)(() -> {
         return SettlementLevelData.getSettlementDataCreateIfNonExist(this.getLevel());
      }));
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      if (var2 && this.isServer()) {
         SettlementLevelData var3 = this.getLevelData();
         Screen.addTooltip(var3.getDebugTooltips(), TooltipLocation.INTERACT_FOCUS);
      }

   }
}
