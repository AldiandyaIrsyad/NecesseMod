package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.GrainItem;
import necesse.level.maps.levelData.jobs.MilkHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.ShearHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.SlaughterHusbandryMobLevelJob;
import necesse.level.maps.regionSystem.SemiRegion;

public abstract class HusbandryMob extends FriendlyRopableMob implements FeedingTroughMob {
   public static int maxCloseMobsToBirthTileRange = 8;
   public static int maxCloseMobsToBirth = 10;
   public float hungerLossPerHour = 2.0F;
   public float defaultHungerGainPerFeed = 0.75F;
   public float tamenessLossPerHour = 0.5F;
   public float tamenessGainPerHour = 1.0F;
   public int birthingCooldown = 1440000;
   public int timeToGrowUp = 1920000;
   protected float hunger = 0.0F;
   protected float tameness = 0.0F;
   protected long growUpTime;
   protected long nextBirthTime;
   protected int checkBirthTimer = 20;
   public MilkHusbandryMobLevelJob milkJob;
   public ShearHusbandryMobLevelJob shearJob;
   public SlaughterHusbandryMobLevelJob slaughterJob;

   public HusbandryMob(int var1) {
      super(var1);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addFloat("hunger", this.hunger);
      var1.addFloat("tameness", this.tameness);
      var1.addLong("growUpTime", this.growUpTime);
      var1.addLong("nextBirthTime", this.nextBirthTime);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.hunger = var1.getFloat("hunger", this.hunger, false);
      this.tameness = var1.getFloat("tameness", this.tameness, false);
      this.growUpTime = var1.getLong("growUpTime", this.growUpTime, false);
      this.nextBirthTime = var1.getLong("nextBirthTime", this.nextBirthTime, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.tameness);
      var1.putNextLong(this.growUpTime);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.tameness = var1.getNextFloat();
      this.growUpTime = var1.getNextLong();
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextFloat(this.hunger);
      if (this.nextBirthTime != 0L) {
         var1.putNextBoolean(true);
         var1.putNextLong(this.nextBirthTime);
      } else {
         var1.putNextBoolean(false);
      }

   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.hunger = var1.getNextFloat();
      if (var1.getNextBoolean()) {
         this.nextBirthTime = var1.getNextLong();
      } else {
         this.nextBirthTime = 0L;
      }

   }

   public void clientTick() {
      super.clientTick();
      this.tickHungerAndTameness();
   }

   public void serverTick() {
      super.serverTick();
      this.tickHungerAndTameness();
      if (!this.isOnBirthingCooldown()) {
         this.nextBirthTime = 0L;
         if (this.isGrown() && this.tameness >= 1.0F) {
            --this.checkBirthTimer;
            if (this.checkBirthTimer <= 0) {
               this.tryBirth();
            }
         }
      }

   }

   public void tickHungerAndTameness() {
      short var1;
      float var2;
      float var3;
      if (this.hunger > 0.0F) {
         var1 = 3600;
         var2 = this.hungerLossPerHour / (float)var1;
         var3 = var2 / 20.0F;
         this.hunger = Math.max(this.hunger - var3, 0.0F);
         if (this.tameness < 1.0F) {
            float var4 = this.tamenessGainPerHour / (float)var1;
            float var5 = var4 / 20.0F;
            this.tameness = Math.min(this.tameness + var5, 1.0F);
         }
      } else if (this.tameness > 0.0F) {
         var1 = 3600;
         var2 = this.tamenessLossPerHour / (float)var1;
         var3 = var2 / 20.0F;
         this.tameness = Math.max(this.tameness - var3, 0.0F);
      }

   }

   public void startBaby() {
      this.growUpTime = this.getWorldEntity().getWorldTime() + (long)this.timeToGrowUp;
      this.tameness = 1.0F;
      this.hunger = 1.0F;
   }

   public void setImported() {
      this.tameness = 1.0F;
      this.hunger = 1.0F;
   }

   private void tryBirth() {
      Set var1 = (Set)this.getLevel().regionManager.getSemiRegion(this.getTileX(), this.getTileY()).getAllConnected((var0) -> {
         return var0.getType() == SemiRegion.RegionType.OPEN || var0.getType() == SemiRegion.RegionType.SOLID;
      }).stream().map(SemiRegion::getRegionID).collect(Collectors.toSet());
      List var2 = (List)this.getLevel().entityManager.mobs.getInRegionByTileRange(this.getTileX(), this.getTileY(), maxCloseMobsToBirthTileRange).stream().filter((var2x) -> {
         return var1.contains(this.getLevel().getRegionID(var2x.getTileX(), var2x.getTileY()));
      }).filter((var0) -> {
         return var0 instanceof HusbandryMob;
      }).map((var0) -> {
         return (HusbandryMob)var0;
      }).map((var1x) -> {
         return new ObjectValue(var1x, GameMath.diagonalMoveDistance(this.getX(), this.getY(), var1x.getX(), var1x.getY()));
      }).filter((var0) -> {
         return (Double)var0.value <= (double)(maxCloseMobsToBirthTileRange * 32);
      }).collect(Collectors.toList());
      if (var2.size() <= maxCloseMobsToBirth) {
         var2.stream().filter((var0) -> {
            return (Double)var0.value <= 96.0;
         }).map((var0) -> {
            return (HusbandryMob)var0.object;
         }).filter((var1x) -> {
            return var1x.getID() == this.getID() && var1x != this;
         }).filter((var0) -> {
            return var0.isGrown() && var0.tameness >= 1.0F && !var0.isOnBirthingCooldown();
         }).findFirst().ifPresent((var1x) -> {
            var1x.nextBirthTime = this.getWorldEntity().getWorldTime() + (long)this.birthingCooldown;
            this.nextBirthTime = this.getWorldEntity().getWorldTime() + (long)this.birthingCooldown;
            HusbandryMob var2 = (HusbandryMob)MobRegistry.getMob(this.getID(), this.getLevel());
            var2.startBaby();
            ArrayList var3 = new ArrayList();

            for(int var4 = -1; var4 <= 1; ++var4) {
               for(int var5 = -1; var5 <= 1; ++var5) {
                  if (var4 != 0 || var5 != 0) {
                     Point var6 = new Point(this.getX() + var4 * 4, this.getY() + var5 * 4);
                     if (!var2.collidesWith(this.getLevel(), var6.x, var6.y)) {
                        var3.add(var6);
                     }
                  }
               }
            }

            Point var7 = (Point)GameRandom.globalRandom.getOneOf((List)var3);
            if (var7 == null) {
               var7 = new Point(this.getX(), this.getY());
            }

            float var10002 = (float)var7.x;
            this.getLevel().entityManager.addMob(var2, var10002, (float)var7.y);
            this.sendMovementPacket(false);
            var1x.sendMovementPacket(false);
         });
      }

      this.checkBirthTimer = 600;
   }

   public InventoryItem onFed(InventoryItem var1) {
      this.hunger += this.defaultHungerGainPerFeed;
      this.sendMovementPacket(false);
      var1.setAmount(var1.getAmount() - 1);
      return var1;
   }

   public boolean canFeed(InventoryItem var1) {
      return !this.isOnFeedCooldown() && var1.item instanceof GrainItem;
   }

   public boolean isOnFeedCooldown() {
      return this.hunger > 1.0F;
   }

   public boolean isGrown() {
      return this.getWorldEntity() == null || this.growUpTime <= this.getWorldEntity().getWorldTime();
   }

   public boolean isOnBirthingCooldown() {
      return this.getWorldEntity() == null || this.getWorldEntity().getWorldTime() <= this.nextBirthTime;
   }

   public boolean canMilk(InventoryItem var1) {
      return false;
   }

   public InventoryItem onMilk(InventoryItem var1, List<InventoryItem> var2) {
      return var1;
   }

   public boolean canShear(InventoryItem var1) {
      return false;
   }

   public InventoryItem onShear(InventoryItem var1, List<InventoryItem> var2) {
      return var1;
   }

   protected void addHoverTooltips(ListGameTooltips var1, boolean var2) {
      super.addHoverTooltips(var1, var2);
      if (this.tameness > 0.0F && this.hunger <= 0.0F) {
         var1.add(Localization.translate("misc", "animalhungry"));
      }

      if (this.tameness >= 1.0F) {
         var1.add(Localization.translate("misc", "animaltame"));
      } else if (this.tameness > 0.0F) {
         int var3 = (int)(this.tameness * 100.0F);
         var1.add(Localization.translate("misc", "animaltameness", "percent", (Object)var3));
      }

   }

   protected void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      if (this.isOnBirthingCooldown()) {
         var1.add("Birth cooldown: " + GameUtils.getTimeStringMillis(this.nextBirthTime - this.getWorldEntity().getWorldTime()));
      }

      if (!this.isGrown()) {
         var1.add("Grown in: " + GameUtils.getTimeStringMillis(this.growUpTime - this.getWorldEntity().getWorldTime()));
      }

      var1.add("Hunger: " + this.hunger);
      var1.add("Tameness: " + this.tameness);
   }
}
