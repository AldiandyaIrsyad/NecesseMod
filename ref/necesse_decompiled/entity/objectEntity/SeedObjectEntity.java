package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HarvestCropLevelJob;
import necesse.level.maps.levelData.jobs.JobsLevelData;

public class SeedObjectEntity extends ObjectEntity {
   public int minGrowTime;
   public int maxGrowTime;
   protected long growTime;
   protected boolean isFertilized;
   public GameObjectReservable fertilizeReservable = new GameObjectReservable();

   public SeedObjectEntity(Level var1, int var2, int var3, int var4, int var5) {
      super(var1, "seed", var2, var3);
      this.minGrowTime = var4;
      this.maxGrowTime = var5;
      this.growTime = this.getNewGrowTime(this.getWorldEntity().getWorldTime());
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addLong("growTime", this.growTime);
      var1.addBoolean("isFertilized", this.isFertilized);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.growTime = var1.getLong("growTime", 0L, false);
      if (this.growTime == 0L) {
         this.growTime = this.getNewGrowTime(this.getWorldEntity().getWorldTime());
      }

      this.isFertilized = var1.getBoolean("isFertilized", false);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      var1.putNextLong(this.growTime);
      var1.putNextBoolean(this.isFertilized);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.growTime = var1.getNextLong();
      this.isFertilized = var1.getNextBoolean();
   }

   private long getNewGrowTime(long var1) {
      if (this.maxGrowTime == 0) {
         return var1;
      } else {
         return this.isFertilized ? var1 + (long)(GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime) / 2) : var1 + (long)GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime);
      }
   }

   public void serverTick() {
      long var1 = this.growTime - this.getWorldEntity().getWorldTime();
      if (var1 < 0L) {
         GameObject var3 = this.getLevel().getObject(this.getX(), this.getY());
         if (var3 instanceof SeedObject) {
            SeedObject var4 = (SeedObject)var3;
            if (!var4.isLastStage() && var4.getNextStageID() != -1) {
               this.getLevel().sendObjectChangePacket(this.getLevel().getServer(), this.getX(), this.getY(), var4.getNextStageID());
               GameObject var5 = this.getLevel().getObject(this.getX(), this.getY());
               if (var5 instanceof SeedObject) {
                  SeedObject var6 = (SeedObject)var5;
                  if (var6.isLastStage()) {
                     JobsLevelData.addJob(this.getLevel(), new HarvestCropLevelJob(this.getX(), this.getY()));
                  } else {
                     ObjectEntity var7 = this.getLevel().entityManager.getObjectEntity(this.getX(), this.getY());
                     if (var7 instanceof SeedObjectEntity) {
                        SeedObjectEntity var8 = (SeedObjectEntity)var7;
                        var8.isFertilized = this.isFertilized;
                        var8.fertilizeReservable = this.fertilizeReservable;
                        var8.growTime = var8.getNewGrowTime(this.getWorldEntity().getWorldTime() + var1);
                     }
                  }
               }
            }
         }
      }

   }

   public boolean isFertilized() {
      return this.isFertilized;
   }

   public void fertilize() {
      if (this.isFertilized) {
         this.getLevel().entityManager.pickups.add((new InventoryItem("fertilizer")).getPickupEntity(this.getLevel(), (float)(this.getX() * 32 + 16), (float)(this.getY() * 32 + 16)));
      } else {
         this.isFertilized = true;
         long var1 = this.growTime - this.getWorldEntity().getWorldTime();
         this.growTime -= var1 / 2L;
         this.markDirty();
      }
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      if (var2) {
         StringTooltips var3 = new StringTooltips();
         var3.add("Grows in: " + GameUtils.getTimeStringMillis(this.growTime - this.getWorldEntity().getWorldTime()));
         var3.add("Fertilized: " + (this.isFertilized ? "Yes" : "No"));
         Screen.addTooltip(var3, TooltipLocation.INTERACT_FOCUS);
      }

   }
}
