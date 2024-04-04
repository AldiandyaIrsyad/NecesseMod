package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.FruitTreeObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.JobsLevelData;

public class FruitGrowerObjectEntity extends ObjectEntity {
   protected int stage;
   protected int maxStage;
   public int minGrowTime;
   public int maxGrowTime;
   protected float fruitPerStage;
   protected String fruitStringID;
   protected long lastGrowTime;
   protected int growTime;
   protected boolean isFertilized;

   public FruitGrowerObjectEntity(Level var1, int var2, int var3, int var4, int var5, int var6, String var7, float var8) {
      super(var1, "fruitgrow", var2, var3);
      this.minGrowTime = var4;
      this.maxGrowTime = var5;
      this.maxStage = var6;
      this.fruitStringID = var7;
      this.fruitPerStage = var8;
      this.growTime = this.getNewGrowTime();
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("stage", this.stage);
      var1.addLong("lastGrowTime", this.lastGrowTime);
      var1.addInt("growTime", this.growTime);
      var1.addBoolean("isFertilized", this.isFertilized);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.stage = var1.getInt("stage", 0, 0, this.maxStage, false);
      this.lastGrowTime = var1.getLong("lastGrowTime", 0L, false);
      this.growTime = var1.getInt("growTime", 0, false);
      if (this.growTime == 0) {
         this.growTime = this.getNewGrowTime();
      }

      this.isFertilized = var1.getBoolean("isFertilized", false);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      var1.putNextShortUnsigned(this.stage);
      var1.putNextLong(this.lastGrowTime);
      var1.putNextInt(this.growTime);
      var1.putNextBoolean(this.isFertilized);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.stage = var1.getNextShortUnsigned();
      this.lastGrowTime = var1.getNextLong();
      this.growTime = var1.getNextInt();
      this.isFertilized = var1.getNextBoolean();
   }

   public void init() {
      super.init();
      if (this.lastGrowTime <= 0L) {
         this.lastGrowTime = this.getWorldEntity().getWorldTime();
      }

   }

   private int getNewGrowTime() {
      if (this.maxGrowTime == 0) {
         return 0;
      } else {
         int var1 = GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime);
         return this.isFertilized ? var1 / 2 : var1;
      }
   }

   public void serverTick() {
      long var1 = this.getWorldEntity().getWorldTime();
      if (this.lastGrowTime <= 0L) {
         this.lastGrowTime = var1;
      }

      while(this.stage < this.maxStage && this.lastGrowTime + (long)this.growTime <= var1) {
         ++this.stage;
         JobsLevelData.addJob(this.getLevel(), new HarvestFruitLevelJob(this.getX(), this.getY()));
         this.lastGrowTime += (long)this.growTime;
         this.growTime = this.getNewGrowTime();
         this.markDirty();
      }

      if (this.stage >= this.maxStage) {
         this.lastGrowTime = var1;
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
         long var1 = (long)this.growTime - this.getWorldEntity().getWorldTime();
         this.growTime = (int)((long)this.growTime - var1 / 2L);
         this.markDirty();
      }
   }

   public int getStage() {
      return this.stage;
   }

   public void resetStage() {
      this.stage = 0;
      this.markDirty();
   }

   public ArrayList<InventoryItem> getHarvestItems() {
      ArrayList var1 = new ArrayList();
      int var2 = this.getFruitDropCount();
      if (var2 > 0) {
         var1.add(new InventoryItem(this.fruitStringID, var2));
      }

      return var1;
   }

   public ArrayList<InventoryItem> getHarvestSplitItems() {
      ArrayList var1 = new ArrayList();
      int var2 = this.getFruitDropCount();
      if (var2 > 0) {
         (new LootItem(this.fruitStringID, var2)).splitItems(5).addItems(var1, GameRandom.globalRandom, 1.0F);
      }

      return var1;
   }

   public void harvest(Mob var1) {
      if (!this.isClient() && this.stage > 0) {
         Point var2 = FruitTreeObject.getItemDropPos(this.getTileX(), this.getTileY(), var1);
         ArrayList var3 = this.getHarvestSplitItems();
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            InventoryItem var5 = (InventoryItem)var4.next();
            this.getLevel().entityManager.pickups.add(var5.getPickupEntity(this.getLevel(), (float)var2.x, (float)var2.y));
         }

         this.resetStage();
      }

   }

   public int getFruitDropCount() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.stage; ++var2) {
         float var3 = this.fruitPerStage;
         var1 = (int)((float)var1 + var3);
         var3 -= (float)((int)var3);
         if (GameRandom.globalRandom.getChance(var3)) {
            ++var1;
         }
      }

      return var1;
   }

   public ArrayList<InventoryItem> getDroppedItems() {
      return this.getHarvestSplitItems();
   }

   public void setRandomStage(GameRandom var1) {
      this.stage = var1.nextInt(this.maxStage);
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      if (var2) {
         StringTooltips var3 = new StringTooltips();
         var3.add("Stage: " + this.stage);
         var3.add("Grows in: " + GameUtils.getTimeStringMillis(this.lastGrowTime + (long)this.growTime - this.getWorldEntity().getWorldTime()));
         var3.add("Fertilized: " + (this.isFertilized ? "Yes" : "No"));
         Screen.addTooltip(var3, TooltipLocation.INTERACT_FOCUS);
      }

   }
}
