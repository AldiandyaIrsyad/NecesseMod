package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class SaplingObjectEntity extends ObjectEntity {
   public long growTime;
   public int minGrowTime;
   public int maxGrowTime;
   private String resultObjectStringID;

   public SaplingObjectEntity(Level var1, int var2, int var3, String var4, int var5, int var6) {
      super(var1, "sapling", var2, var3);
      this.resultObjectStringID = var4;
      this.minGrowTime = var5 * 1000;
      this.maxGrowTime = var6 * 1000;
      this.generateGrowTime();
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addLong("growTime", this.growTime);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.growTime = var1.getLong("growTime", 0L);
   }

   public boolean shouldRequestPacket() {
      return false;
   }

   public void generateGrowTime() {
      this.growTime = this.getWorldEntity().getWorldTime() + (long)GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime);
   }

   public void serverTick() {
      if (this.getWorldEntity().getWorldTime() > this.growTime) {
         this.grow();
      }

   }

   public void grow() {
      if (this.resultObjectStringID != null) {
         GameObject var1 = ObjectRegistry.getObject(this.resultObjectStringID);
         if (var1 != null && var1.isValid(this.getLevel(), this.getX(), this.getY())) {
            this.getLevel().sendObjectChangePacket(this.getLevel().getServer(), this.getX(), this.getY(), var1.getID());
            this.remove();
         } else {
            this.growTime = this.getWorldEntity().getWorldTime() + (long)GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime);
         }
      }

   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      if (var2) {
         Screen.addTooltip(new StringTooltips("Growtime: " + ActiveBuff.convertSecondsToText((float)(this.growTime - this.getWorldEntity().getWorldTime()) / 1000.0F)), TooltipLocation.INTERACT_FOCUS);
      }

   }
}
