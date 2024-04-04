package necesse.entity.levelEvent.fishingEvent;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.control.Control;
import necesse.engine.network.packet.PacketFishingStatus;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FishingHookParticle;
import necesse.entity.particle.Particle;
import necesse.entity.trails.FishingTrail;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.FishItemInterface;
import necesse.level.maps.Level;
import necesse.level.maps.LevelTile;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;

public class WaitFishingPhase extends FishingPhase {
   private ArrayList<FishingLure> lures;
   private int tickCounter;
   private int lastSplashTick = -1;

   public WaitFishingPhase(FishingEvent var1, Point[] var2) {
      super(var1);
      this.lures = new ArrayList(var1.getLines());

      for(int var3 = 0; var3 < var1.getLines(); ++var3) {
         this.lures.add(new FishingLure(var3, var2[var3]));
      }

   }

   public void tickMovement(float var1) {
      if (this.event.isMine() && Control.MOUSE1.isPressed()) {
         this.event.level.getClient().network.sendPacket(PacketFishingStatus.getReelPacket(this.event));
         this.event.reel();
      }

      this.lures.forEach(FishingLure::updateLine);
   }

   public void clientTick() {
      if (this.event.isReeled) {
         this.reel();
      } else {
         this.event.getFishingMob().showFishingWaitAnimation(this.event.fishingRod, this.event.getTarget().x, this.event.getTarget().y);
         ++this.tickCounter;
         this.lures.forEach(FishingLure::clientTick);
         if (this.tickCounter > 4800) {
            this.over();
         }

      }
   }

   public void serverTick() {
      if (this.event.isReeled) {
         this.reel();
      } else {
         this.event.getFishingMob().showFishingWaitAnimation(this.event.fishingRod, this.event.getTarget().x, this.event.getTarget().y);
         ++this.tickCounter;
         this.lures.forEach(FishingLure::serverTick);
         if (!this.event.checkOutsideRange()) {
            if (this.tickCounter > 4800) {
               this.over();
            }

         }
      }
   }

   public void end() {
      this.lures.forEach(FishingLure::remove);
   }

   public void over() {
      this.event.getFishingMob().stopFishing();
      this.lures.forEach(FishingLure::remove);
   }

   private int getNewCatchTick() {
      int var1 = this.event.fishingRod.fishingPower;
      int var2 = this.event.bait == null ? 0 : this.event.bait.fishingPower;
      int var3 = (Integer)this.event.getMob().buffManager.getModifier(BuffModifiers.FISHING_POWER);
      int var4 = GameMath.limit(var1 + var2 + var3, 0, 100);
      float var5 = Math.abs((float)var4 / 100.0F - 1.0F);
      int var6 = (int)(500.0F * var5);
      return this.tickCounter + (var6 <= 0 ? 0 : GameRandom.globalRandom.nextInt(var6)) + 10;
   }

   public void addNewCatch(int var1, int var2, InventoryItem var3) {
      if (var1 >= 0 && var1 < this.lures.size()) {
         ((FishingLure)this.lures.get(var1)).addCatch(var3, var2);
      } else {
         GameLog.warn.println("Received invalid new fishing catch index " + var1 + " out of " + this.lures.size());
      }

   }

   public void reel() {
      this.event.setPhase(new ReelFishingPhase(this.event, this.lures));
   }

   public int getTicksToNextCatch() {
      return this.lures.stream().mapToInt(FishingLure::getTicksToNextCatch).min().orElse(500);
   }

   public class FishingLure {
      public final int lineIndex;
      public FishingTrail line;
      public FishingHookParticle hookParticle;
      public final Point hookPosition;
      private InventoryItem caught;
      public int catchNum;
      public int catchSent;
      public int catchTick;

      public FishingLure(int var2, Point var3) {
         this.lineIndex = var2;
         this.hookPosition = var3;
         if (!WaitFishingPhase.this.event.isClient()) {
            this.catchTick = WaitFishingPhase.this.getNewCatchTick();
         } else {
            this.catchTick = -1;
         }

         ++this.catchNum;
         if (!WaitFishingPhase.this.event.level.isServer()) {
            WaitFishingPhase.this.event.level.entityManager.particles.add(this.hookParticle = new FishingHookParticle(WaitFishingPhase.this.event.level, (float)var3.x, (float)var3.y, WaitFishingPhase.this.event.fishingRod));
            WaitFishingPhase.this.event.level.entityManager.addTrail(this.line = new FishingTrail(WaitFishingPhase.this.event.getMob(), WaitFishingPhase.this.event.level, this.hookParticle, WaitFishingPhase.this.event.fishingRod));
         }

      }

      public void updateLine() {
         if (this.line != null) {
            this.line.update();
         }

      }

      public void clientTick() {
         if (this.hookParticle != null) {
            this.hookParticle.refreshSpawnTime();
         }

         if (this.caught != null) {
            if (WaitFishingPhase.this.tickCounter == this.catchTick) {
               if (WaitFishingPhase.this.lastSplashTick != WaitFishingPhase.this.tickCounter) {
                  Screen.playSound(GameResources.splash, SoundEffect.effect((float)this.hookPosition.x, (float)this.hookPosition.y));
                  WaitFishingPhase.this.lastSplashTick = WaitFishingPhase.this.tickCounter;
               }

               if (this.hookParticle != null) {
                  this.hookParticle.blob();

                  for(int var1 = 0; var1 < 5; ++var1) {
                     WaitFishingPhase.this.event.level.entityManager.addParticle(this.hookParticle.x, this.hookParticle.y, Particle.GType.IMPORTANT_COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 6.0), -GameRandom.globalRandom.nextFloat() * 4.0F).color(new Color(89, 139, 224));
                  }
               }
            }

            if (WaitFishingPhase.this.tickCounter - this.catchTick > WaitFishingPhase.this.event.fishingRod.reelWindow) {
               this.caught = null;
            }
         }

      }

      public void serverTick() {
         int var1 = this.catchTick - WaitFishingPhase.this.tickCounter;
         if (this.caught == null && var1 <= 20 && this.catchSent < this.catchNum) {
            this.catchSent = this.catchNum++;
            this.caught = this.getNewCatch();
            if (this.caught != null) {
               if (WaitFishingPhase.this.event.level.isServer()) {
                  WaitFishingPhase.this.event.level.getServer().network.sendToClientsAt(PacketFishingStatus.getUpcomingCatchPacket(WaitFishingPhase.this.event, this.lineIndex, var1, this.caught), (Level)WaitFishingPhase.this.event.level);
               }
            } else {
               this.catchTick = var1 + WaitFishingPhase.this.getNewCatchTick();
            }
         } else if (this.caught != null && var1 < -WaitFishingPhase.this.event.fishingRod.reelWindow) {
            this.catchTick = WaitFishingPhase.this.getNewCatchTick();
            this.caught = null;
         }

      }

      public InventoryItem getCatch() {
         return this.catchTick - WaitFishingPhase.this.tickCounter < 0 ? this.caught : null;
      }

      public int getTicksToNextCatch() {
         int var1 = this.catchTick - WaitFishingPhase.this.tickCounter;
         if (var1 < 0) {
            return this.caught != null ? var1 : 500;
         } else {
            return var1;
         }
      }

      public void addCatch(InventoryItem var1, int var2) {
         if (var1.item instanceof FishItemInterface) {
            FishItemInterface var3 = (FishItemInterface)var1.item;
            Particle var4 = var3.getParticle(WaitFishingPhase.this.event.level, this.hookPosition.x, this.hookPosition.y, var2 * 50);
            if (var4 != null) {
               WaitFishingPhase.this.event.level.entityManager.addParticle(var4, Particle.GType.CRITICAL);
            }
         }

         this.caught = var1;
         this.catchTick = WaitFishingPhase.this.tickCounter + var2;
      }

      private InventoryItem getNewCatch() {
         FishingSpot var1 = new FishingSpot(new LevelTile(WaitFishingPhase.this.event.level, this.hookPosition.x / 32, this.hookPosition.y / 32), WaitFishingPhase.this.event.fishingRod, WaitFishingPhase.this.event.bait);
         FishingLootTable var2 = WaitFishingPhase.this.event.getFishingMob().getFishingLootTable(var1);
         return var2.getRandomItem(var1, GameRandom.globalRandom);
      }

      public void remove() {
         if (this.line != null && !this.line.isRemoved()) {
            this.line.remove();
         }

         if (this.hookParticle != null && !this.hookParticle.removed()) {
            this.hookParticle.remove();
         }

      }
   }
}
