package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class BeeHiveObjectEntity extends AbstractBeeHiveObjectEntity {
   public static int maxStoredHoney = 5;
   public static int maxBees = 20;
   public int hiveBeeCapacity;

   public BeeHiveObjectEntity(Level var1, int var2, int var3) {
      super(var1, "beehive", var2, var3);
      this.hasQueen = true;
      this.hiveBeeCapacity = GameRandom.globalRandom.getIntBetween(1, maxBees);
      this.honey.amount = GameRandom.globalRandom.getIntBetween(0, maxStoredHoney);
      this.bees.amount = GameRandom.globalRandom.getIntBetween(this.hiveBeeCapacity / 2, this.hiveBeeCapacity);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("hiveBeeCapacity", this.hiveBeeCapacity);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.hiveBeeCapacity = var1.getInt("hiveBeeCapacity", this.hiveBeeCapacity, false);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      var1.putNextInt(this.hiveBeeCapacity);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.hiveBeeCapacity = var1.getNextInt();
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      StringTooltips var3 = new StringTooltips();
      if (var2) {
         var3.add("Capacity: " + this.hiveBeeCapacity);
         this.addDebugTooltips(var3);
      }

      Screen.addTooltip(var3, TooltipLocation.INTERACT_FOCUS);
   }

   public boolean canAddWorkerBee() {
      return false;
   }

   public int getMaxBees() {
      return this.hiveBeeCapacity;
   }

   public int getMaxFrames() {
      return 0;
   }

   public int getMaxStoredHoney() {
      return maxStoredHoney;
   }

   public boolean canCreateQueens() {
      return false;
   }
}
