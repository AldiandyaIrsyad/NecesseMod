package necesse.entity.levelEvent.fishingEvent;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collection;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.FishingMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.regionSystem.RegionPosition;

public class FishingEvent extends LevelEvent {
   public static final int MAX_TICKS = 4800;
   public static final int MAX_CATCH_TICKS = 500;
   public static final int MIN_CATCH_TICKS = 10;
   public static final int MAX_POWER = 100;
   private Mob mob;
   private FishingMob fishingMob;
   private int targetX;
   private int targetY;
   private FishingPhase currentPhase;
   private boolean isMine;
   private int seed;
   private int lines;
   private Point[] randomTargets;
   public FishingRodItem fishingRod;
   public BaitItem bait;
   public boolean isReeled;

   public FishingEvent() {
   }

   public FishingEvent(FishingMob var1, int var2, int var3, FishingRodItem var4, BaitItem var5) {
      this.mob = (Mob)var1;
      this.fishingMob = var1;
      this.targetX = var2;
      this.targetY = var3;
      this.fishingRod = var4;
      this.bait = var5;
      this.seed = GameRandom.globalRandom.nextInt();
      if (this.mob != null && this.mob.getPositionPoint().distance((double)var2, (double)var3) > (double)var4.lineLength) {
         Point2D.Float var6 = new Point2D.Float((float)(var2 - this.mob.getX()), (float)(var3 - this.mob.getY()));
         float var7 = (float)var6.distance(0.0, 0.0);
         float var8 = var6.x / var7;
         float var9 = var6.y / var7;
         this.targetX = (int)((float)this.mob.getX() + var8 * (float)var4.lineLength);
         this.targetY = (int)((float)this.mob.getY() + var9 * (float)var4.lineLength);
      }

   }

   public boolean shouldSendOverPacket() {
      return true;
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      int var2 = var1.getNextInt();
      Mob var3 = GameUtils.getLevelMob(var2, this.level);
      if (var3 instanceof FishingMob && var3.getLevel() != null) {
         this.fishingMob = (FishingMob)var3;
         this.mob = var3;
      } else {
         this.over();
      }

      this.targetX = var1.getNextInt();
      this.targetY = var1.getNextInt();
      this.fishingRod = (FishingRodItem)ItemRegistry.getItem(var1.getNextShortUnsigned());
      short var4 = var1.getNextShort();
      if (var4 == -1) {
         this.bait = null;
      } else {
         this.bait = (BaitItem)ItemRegistry.getItem(var4 & '\uffff');
      }

      this.seed = var1.getNextInt();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.mob.getUniqueID());
      var1.putNextInt(this.targetX);
      var1.putNextInt(this.targetY);
      var1.putNextShortUnsigned(this.fishingRod.getID());
      if (this.bait == null) {
         var1.putNextShort((short)-1);
      } else {
         var1.putNextShortUnsigned(this.bait.getID());
      }

      var1.putNextInt(this.seed);
   }

   public void init() {
      super.init();
      if (this.mob == null) {
         GameLog.warn.println("Could not find owner for fishing event " + this.getClass().getSimpleName() + " server level: " + this.isServer());
         this.over();
      } else if (this.mob.getLevel() == null) {
         GameLog.warn.println("Could not find owner level for fishing event " + this.getClass().getSimpleName() + " server level: " + this.isServer());
         this.over();
      } else {
         if (this.isClient()) {
            this.isMine = this.mob == this.level.getClient().getPlayer();
         }

         int var1 = (Integer)this.mob.buffManager.getModifier(BuffModifiers.FISHING_LINES);
         this.lines = GameMath.limit(this.fishingRod.lineCount + var1, 1, 1000);
         this.setPhase(new SwingFishingPhase(this));
         GameRandom var2 = new GameRandom((long)this.seed);
         this.randomTargets = new Point[this.lines];
         int var3 = this.fishingRod.precision;

         for(int var4 = 0; var4 < this.lines; ++var4) {
            int var5 = this.targetX + var2.getIntBetween(-var3, var3);
            int var6 = this.targetY + var2.getIntBetween(-var3, var3);
            this.randomTargets[var4] = new Point(var5, var6);
         }

         this.updateMobDir();
      }
   }

   public void setPhase(FishingPhase var1) {
      if (this.currentPhase != null) {
         this.currentPhase.end();
      }

      this.currentPhase = var1;
   }

   public Point getRandomTarget(int var1) {
      return this.randomTargets[var1];
   }

   public Point getTarget() {
      return new Point(this.targetX, this.targetY);
   }

   public Mob getMob() {
      return this.mob;
   }

   public FishingMob getFishingMob() {
      return this.fishingMob;
   }

   public int getLines() {
      return this.lines;
   }

   public boolean isMine() {
      return this.isMine;
   }

   public void tickMovement(float var1) {
      if (!this.isOver()) {
         this.updateMobDir();
         if (this.currentPhase != null) {
            this.currentPhase.tickMovement(var1);
         }

      }
   }

   public void clientTick() {
      if (!this.isOver()) {
         if (this.getMob().removed()) {
            this.over();
         } else {
            if (this.currentPhase != null) {
               this.currentPhase.clientTick();
            }

         }
      }
   }

   public void serverTick() {
      if (!this.isOver()) {
         if (this.getMob().removed()) {
            this.over();
         } else {
            if (this.currentPhase != null) {
               this.currentPhase.serverTick();
            }

         }
      }
   }

   public boolean checkOutsideRange() {
      if (this.mob.getPositionPoint().distance((double)this.targetX, (double)this.targetY) > (double)(this.fishingRod.lineLength + this.fishingRod.precision + 64)) {
         this.giveBaitBack();
         this.over();
         return true;
      } else {
         return false;
      }
   }

   private void updateMobDir() {
      if (this.targetX > this.mob.getX()) {
         this.mob.dir = 1;
      } else {
         this.mob.dir = 3;
      }

   }

   public Point getPoleTipPos() {
      return this.fishingRod.getTipPos(this.mob);
   }

   public int getPoleTipHeight() {
      return this.fishingRod.getTipHeight(this.mob);
   }

   public void addCatch(int var1, int var2, InventoryItem var3) {
      if (this.currentPhase != null) {
         this.currentPhase.addNewCatch(var1, var2, var3);
      }

   }

   public void reel() {
      if (this.currentPhase != null) {
         this.currentPhase.reel();
      }

      this.isReeled = true;
   }

   public int getTicksToNextCatch() {
      return this.currentPhase != null ? this.currentPhase.getTicksToNextCatch() : 500;
   }

   public void giveBaitBack() {
      if (this.bait != null) {
         this.fishingMob.giveBaitBack(this.bait);
         this.bait = null;
      }

   }

   public void over() {
      if (!this.isOver()) {
         super.over();
         if (this.currentPhase != null) {
            this.currentPhase.over();
         }

      }
   }

   public int getMobUniqueID() {
      return this.mob.getUniqueID();
   }

   public int getSeed() {
      return this.seed;
   }

   public Collection<RegionPosition> getRegionPositions() {
      return this.mob != null ? this.mob.getRegionPositions() : super.getRegionPositions();
   }
}
