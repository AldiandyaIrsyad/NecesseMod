package necesse.entity.mobs;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.Settings;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.FloatPosDirMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.light.GameLight;

public abstract class WormMobHead<T extends WormMobBody<B, T>, B extends WormMobHead<T, B>> extends Mob {
   public ArrayList<LevelMob<T>> bodyParts = new ArrayList();
   public GameLinkedList<WormMoveLine> moveLines = new GameLinkedList();
   public float height;
   public float moveAngle;
   protected float moveAngleAccuracy = 2.0F;
   public float distanceMoved;
   public boolean dive;
   public boolean isUnderground;
   public float soundCounter;
   public final EmptyMobAbility diveAbility;
   public final FloatPosDirMobAbility appearAbility;
   public float waveLength;
   public float distPerMoveSound;
   public final int totalBodyParts;
   public float heightMultiplier;
   public float heightOffset;
   public int removeWhenTilesOutOfLevel = 0;

   public WormMobHead(int var1, float var2, float var3, int var4, float var5, float var6) {
      super(var1);
      this.waveLength = var2;
      this.distPerMoveSound = var3;
      this.totalBodyParts = var4;
      this.heightMultiplier = var5;
      this.heightOffset = var6;
      this.moveAccuracy = 160;
      this.setSpeed(50.0F);
      this.setSwimSpeed(1.0F);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.accelerationMod = 1.0F;
      this.decelerationMod = 1.0F;
      this.diveAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            WormMobHead.this.dive = true;
         }
      });
      this.appearAbility = (FloatPosDirMobAbility)this.registerAbility(new FloatPosDirMobAbility() {
         protected void run(float var1, float var2, float var3, float var4) {
            WormMobHead.this.distanceMoved = 0.0F;
            WormMobHead.this.isUnderground = false;
            WormMobHead.this.dive = false;
            float var5 = (float)(new Point2D.Float(WormMobHead.this.dx, WormMobHead.this.dy)).distance(0.0, 0.0);
            Point2D.Float var6 = GameMath.normalize(var3, var4);
            WormMobHead.this.moveAngle = GameMath.getAngle(var6);
            WormMobHead.this.dx = var6.x * var5;
            WormMobHead.this.dy = var6.y * var5;
            WormMobHead.this.setPos(var1, var2, true);
            WormMobHead.this.onAppearAbility();
         }
      });
   }

   protected void onAppearAbility() {
   }

   protected abstract float getDistToBodyPart(T var1, int var2, float var3);

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.bodyParts.size());
      Iterator var2 = this.bodyParts.iterator();

      while(var2.hasNext()) {
         LevelMob var3 = (LevelMob)var2.next();
         var1.putNextInt(var3.uniqueID);
      }

      ArrayList var7 = new ArrayList(this.bodyParts.size());
      Iterator var8 = this.bodyParts.iterator();

      while(var8.hasNext()) {
         LevelMob var4 = (LevelMob)var8.next();
         WormMobBody var5 = (WormMobBody)var4.get(this.getLevel());
         if (var5 != null && var5.moveLine != null) {
            WormMoveLine var6 = (WormMoveLine)var5.moveLine.object;
            var7.add(() -> {
               var6.writeSpawnPacket(var1, var5.x, var5.y, var5.moveLineExtraDist);
            });
         }
      }

      var1.putNextFloat(this.distanceMoved);
      var1.putNextShortUnsigned(var7.size());
      var8 = var7.iterator();

      while(var8.hasNext()) {
         Runnable var9 = (Runnable)var8.next();
         var9.run();
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      Iterator var2 = this.bodyParts.iterator();

      while(var2.hasNext()) {
         LevelMob var3 = (LevelMob)var2.next();
         WormMobBody var4 = (WormMobBody)var3.get(this.getLevel());
         if (var4 != null) {
            var4.remove();
         }
      }

      this.bodyParts.clear();
      int var6 = var1.getNextShortUnsigned();

      int var7;
      for(var7 = 0; var7 < var6; ++var7) {
         this.bodyParts.add(new LevelMob(var1.getNextInt()));
      }

      this.distanceMoved = var1.getNextFloat();
      this.moveLines.clear();
      var7 = var1.getNextShortUnsigned();
      WormMoveLineSpawnData var8 = new WormMoveLineSpawnData(this.x, this.y);

      for(int var5 = 0; var5 < var7; ++var5) {
         this.moveLines.addLast(this.readMoveLine(var1, var8));
      }

   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextFloat(this.moveAngle);
      var1.putNextShortUnsigned(this.moveAccuracy);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      Point2D.Float var3 = new Point2D.Float((float)this.getDrawX(), (float)this.getDrawY());
      super.applyMovementPacket(var1, var2);
      this.moveAngle = var1.getNextFloat();
      this.moveAccuracy = var1.getNextShortUnsigned();
      Point2D.Float var4 = new Point2D.Float((float)this.getDrawX(), (float)this.getDrawY());
      if (var2) {
         var4 = new Point2D.Float(this.x, this.y);
         var3 = new Point2D.Float(this.x, this.y);
      }

      WormMoveLine var5 = this.newMoveLine(var3, var4, var2, this.distanceMoved, this.isUnderground);
      if (var5.dist() > 0.0) {
         this.moveLines.addFirst(var5);
         this.updateBodyParts();
      }

   }

   protected abstract T createNewBodyPart(int var1);

   public void init() {
      super.init();
      int var2;
      if (this.bodyParts.size() < this.totalBodyParts) {
         GameRandom var1 = new GameRandom();
         var2 = this.bodyParts.size();
         int var3 = this.bodyParts.isEmpty() ? this.getUniqueID() : ((LevelMob)this.bodyParts.get(this.bodyParts.size() - 1)).uniqueID;

         for(int var4 = 0; var4 < this.totalBodyParts - var2; ++var4) {
            var1.setSeed((long)var3);
            int var5 = getNewUniqueID(this.getLevel(), var1);
            this.bodyParts.add(new LevelMob(var5));
            var3 = var5;
         }
      }

      WormMobBody var6 = null;

      for(var2 = 0; var2 < this.bodyParts.size(); ++var2) {
         LevelMob var7 = (LevelMob)this.bodyParts.get(var2);
         WormMobBody var8 = this.createNewBodyPart(var2);
         var8.next = var6;
         var6 = var8;
         var8.setLevel(this.getLevel());
         var8.setUniqueID(var7.uniqueID);
         var8.setPos(this.x, this.y, true);
         var8.setMaxHealth(this.getMaxHealth());
         var8.setHealthHidden(this.getHealth());
         var8.master.uniqueID = this.getUniqueID();
         var8.collisionHitCooldowns = this.collisionHitCooldowns;
         this.getLevel().entityManager.mobs.addHidden(var8);
         this.bodyParts.set(var2, new LevelMob(var8));
      }

      this.updateBodyParts();
   }

   public boolean canCollisionHit(Mob var1) {
      return this.isVisible() && super.canCollisionHit(var1);
   }

   public boolean isVisible() {
      return !this.isUnderground;
   }

   public boolean canBeHit(Attacker var1) {
      return !this.isVisible() ? false : super.canBeHit(var1);
   }

   public void clientTick() {
      super.clientTick();
      Iterator var1 = this.bodyParts.iterator();

      while(var1.hasNext()) {
         LevelMob var2 = (LevelMob)var1.next();
         var2.computeIfPresent(this.getLevel(), (var0) -> {
            var0.removeTicker = 0;
         });
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.removeWhenTilesOutOfLevel > 0 && (this.getX() < -this.removeWhenTilesOutOfLevel * 32 || this.getY() < -this.removeWhenTilesOutOfLevel * 32 || this.getX() > (this.getLevel().width + this.removeWhenTilesOutOfLevel) * 32 || this.getY() > (this.getLevel().height + this.removeWhenTilesOutOfLevel) * 32)) {
         this.remove();
      }

      Iterator var1 = this.bodyParts.iterator();

      while(var1.hasNext()) {
         LevelMob var2 = (LevelMob)var1.next();
         var2.computeIfPresent(this.getLevel(), (var0) -> {
            var0.removeTicker = 0;
         });
      }

   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      super.remove(var1, var2, var3, var4);
      Iterator var5 = this.bodyParts.iterator();

      while(var5.hasNext()) {
         LevelMob var6 = (LevelMob)var5.next();
         var6.computeIfPresent(this.getLevel(), (var4x) -> {
            var4x.remove(var1, var2, var3, var4);
         });
      }

   }

   public Stream<T> streamBodyParts() {
      return this.bodyParts.stream().map((var1) -> {
         return (WormMobBody)var1.get(this.getLevel());
      }).filter(Objects::nonNull);
   }

   public Point getLootDropsPosition(ServerClient var1) {
      ArrayList var2 = this.getLargestBodyPartSection((var1x) -> {
         return var1x.isVisible() && !this.getLevel().isSolidTile(var1x.getX() / 32, var1x.getY() / 32) && !this.getLevel().isLiquidTile(var1x.getX() / 32, var1x.getY() / 32);
      }, true);
      if (!var2.isEmpty()) {
         WormMobBody var3 = (WormMobBody)var2.get(var2.size() / 2);
         return new Point(var3.getX(), var3.getY());
      } else {
         return super.getLootDropsPosition(var1);
      }
   }

   public int stoppingDistance(float var1, float var2) {
      return 0;
   }

   public Rectangle getSelectBox(int var1, int var2) {
      Rectangle var3 = super.getSelectBox(var1, var2);
      var3.y = (int)((float)var3.y - this.height);
      if (this.height < 0.0F) {
         var3.height = (int)((float)var3.height + this.height);
      }

      return var3;
   }

   public ArrayList<T> getLargestBodyPartSection(Predicate<T> var1, boolean var2) {
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      Iterator var5 = this.bodyParts.iterator();

      while(true) {
         while(var5.hasNext()) {
            LevelMob var6 = (LevelMob)var5.next();
            WormMobBody var7;
            if (var2) {
               Mob var8 = (Mob)this.getLevel().entityManager.mobs.get(var6.uniqueID, true);

               try {
                  var7 = (WormMobBody)var8;
               } catch (ClassCastException var10) {
                  var7 = null;
               }
            } else {
               var7 = (WormMobBody)var6.get(this.getLevel());
            }

            if (var7 != null && var1.test(var7)) {
               var4.add(var7);
            } else if (var3.size() < var4.size()) {
               var3 = var4;
               var4 = new ArrayList();
            }
         }

         if (var3.size() < var4.size()) {
            var3 = var4;
         }

         return var3;
      }
   }

   public boolean isHealthBarVisible() {
      return Settings.showMobHealthBars && this.getHealthUnlimited() < this.getMaxHealth() && (!this.isBoss() || !Settings.showBossHealthBars);
   }

   public Rectangle getHealthBarBounds(int var1, int var2) {
      ArrayList var3 = this.getLargestBodyPartSection(WormMobBody::isVisible, false);
      if (var3.isEmpty()) {
         return null;
      } else {
         GameLinkedList.Element var4 = ((WormMobBody)var3.get(var3.size() / 2)).moveLine;
         double var5 = 0.0;
         GameLinkedList var7 = new GameLinkedList();
         var7.add((WormMoveLine)var4.object);
         var5 += ((WormMoveLine)var4.object).dist();

         GameLinkedList.Element var8;
         for(var8 = var4; var8.hasPrev(); var5 += ((WormMoveLine)var8.object).dist()) {
            var8 = var8.prev();
            if (var8.object == null || ((WormMoveLine)var8.object).isUnderground || ((WormMoveLine)var8.object).isMoveJump) {
               break;
            }

            var7.addLast((WormMoveLine)var8.object);
         }

         for(var8 = var4; var8.hasNext(); var5 += ((WormMoveLine)var8.object).dist()) {
            var8 = var8.next();
            if (var8.object == null || ((WormMoveLine)var8.object).isUnderground || ((WormMoveLine)var8.object).isMoveJump) {
               break;
            }

            var7.addFirst((WormMoveLine)var8.object);
         }

         ComputedObjectValue var9 = moveDistance(var7.getFirstElement(), var5 / 2.0);
         Point2D.Double var10 = linePos((GameLinkedList.Element)var9.object, (Double)var9.get());
         byte var11 = 64;
         return new Rectangle((int)var10.x - var11 / 2, (int)var10.y - 30, var11, 7);
      }
   }

   public void tickMovement(float var1) {
      Point2D.Float var2 = new Point2D.Float((float)this.getDrawX(), (float)this.getDrawY());
      super.tickMovement(var1);
      Point2D.Float var3 = new Point2D.Float((float)this.getDrawX(), (float)this.getDrawY());
      WormMoveLine var4 = this.newMoveLine(var2, var3, false, this.distanceMoved, this.isUnderground);
      if (var4.dist() > 0.0) {
         if (this.dive && this.runsPastMinHeight(this.distanceMoved, (float)var4.dist())) {
            var4.isUnderground = true;
            this.isUnderground = true;
         }

         this.distanceMoved = (float)((double)this.distanceMoved + var4.dist());
         if (this.isClient()) {
            this.soundCounter = (float)((double)this.soundCounter + var4.dist());
            if (this.soundCounter >= this.distPerMoveSound) {
               this.playMoveSound();
               this.soundCounter -= this.distPerMoveSound;
            }
         }

         this.moveLines.addFirst(var4);
         this.updateBodyParts();
      }

   }

   public WormMoveLine newMoveLine(Point2D var1, Point2D var2, boolean var3, float var4, boolean var5) {
      return new WormMoveLine(var1, var2, var3, var4, var5);
   }

   public WormMoveLine readMoveLine(PacketReader var1, WormMoveLineSpawnData var2) {
      return new WormMoveLine(var1, var2);
   }

   protected abstract void playMoveSound();

   public void tickCurrentMovement(float var1) {
      super.tickCurrentMovement(var1);
      if (this.moveX != 0.0F || this.moveY != 0.0F) {
         float var2 = GameMath.getAngle(new Point2D.Float(this.moveX, this.moveY));
         float var3 = this.getTurnSpeed(var1);
         float var4 = GameMath.getAngleDifference(var2, this.moveAngle);
         if (Math.abs(var4) > this.moveAngleAccuracy) {
            if (Math.abs(var4) - var3 < 1.0F) {
               this.moveAngle = GameMath.limit(this.moveAngle + var3 * Math.signum(var4), var2 - 1.0F, var2 + 1.0F);
            } else {
               this.moveAngle += var3 * Math.signum(var4);
            }
         }
      }

      Point2D.Float var5 = GameMath.getAngleDir(this.moveAngle);
      this.moveX = var5.x;
      this.moveY = var5.y;
   }

   public float getTurnSpeed(float var1) {
      float var2 = (float)Math.pow(0.10000000149011612, (double)(this.getCurrentSpeed() / this.getSpeed()));
      return (40.0F + 100.0F * var2) * var1 / 250.0F;
   }

   public float getWaveHeight(float var1) {
      return (float)Math.sin(Math.toRadians((double)(var1 / this.waveLength * 360.0F - 90.0F))) * this.heightMultiplier + this.heightOffset;
   }

   public float getDistAtHeight(float var1) {
      float var2 = GameMath.limit((var1 - this.heightOffset) / this.heightMultiplier, -1.0F, 1.0F);
      return (float)((Math.toDegrees(Math.asin((double)var2)) + 90.0) * (double)this.waveLength) / 360.0F;
   }

   public boolean runsPastMinHeight(float var1, float var2) {
      float var3 = (var1 + this.waveLength / 2.0F) % this.waveLength / this.waveLength;
      float var4 = var2 / this.waveLength;
      return var4 > 1.0F || var3 < 0.5F && var3 + var4 >= 0.5F;
   }

   public void updateBodyParts() {
      float var1 = -this.distanceMoved;
      this.height = this.getWaveHeight(var1);
      if (this.moveLines.isEmpty()) {
         Iterator var2 = this.bodyParts.iterator();

         while(var2.hasNext()) {
            LevelMob var3 = (LevelMob)var2.next();
            WormMobBody var4 = (WormMobBody)var3.get(this.getLevel());
            if (var4 != null) {
               var4.moveLine = null;
            }
         }
      } else {
         ComputedObjectValue var10 = new ComputedObjectValue(this.moveLines.getFirstElement(), () -> {
            return 0.0;
         });
         float var11 = 0.0F;

         for(int var12 = 0; var12 < this.bodyParts.size(); ++var12) {
            WormMobBody var5 = (WormMobBody)((LevelMob)this.bodyParts.get(var12)).get(this.getLevel());
            if (var10.object != null) {
               float var6 = this.getDistToBodyPart(var5, var12, var11);
               var11 += var6;
               double var7 = (double)var6 + (Double)var10.get();
               var10 = moveDistance((GameLinkedList.Element)var10.object, var7);
               if (var10.object != null) {
                  if (var5 != null) {
                     var5.moveLine = (GameLinkedList.Element)var10.object;
                     var5.moveLineExtraDist = ((Double)var10.get()).floatValue();
                     var5.height = this.getWaveHeight(((WormMoveLine)var5.moveLine.object).movedDist + var5.moveLineExtraDist);
                     Point2D.Double var9 = linePos(var10);
                     var5.updateBodyPartPosition(this, (float)var9.x, (float)var9.y);
                     var5.distanceRan = this.distanceRan;
                     this.onUpdatedBodyPartPos(var5, var12, var6);
                  }
                  continue;
               }
            }

            var5.moveLine = null;
            var5.moveLineExtraDist = 0.0F;
            WormMoveLine var13 = (WormMoveLine)this.moveLines.getLast();
            var5.updateBodyPartPosition(this, var13.x2, var13.y2);
            var5.distanceRan = this.distanceRan;
            var10 = new ComputedObjectValue((Object)null, () -> {
               return 0.0;
            });
         }

         if (var10.object != null) {
            this.removeRemaining(((GameLinkedList.Element)var10.object).next());
         }
      }

   }

   protected void onUpdatedBodyPartPos(T var1, int var2, float var3) {
   }

   public static ComputedObjectValue<GameLinkedList<WormMoveLine>.Element, Double> moveDistance(GameLinkedList<WormMoveLine>.Element var0, double var1) {
      if (var0 == null) {
         return new ComputedObjectValue((Object)null, () -> {
            return var1;
         });
      } else {
         do {
            if (Math.abs(var1) <= ((WormMoveLine)var0.object).dist()) {
               return new ComputedObjectValue(var0, () -> {
                  return var1;
               });
            }

            if (var1 < 0.0) {
               var1 += ((WormMoveLine)var0.object).dist();
               var0 = var0.prev();
            } else {
               var1 -= ((WormMoveLine)var0.object).dist();
               var0 = var0.next();
            }
         } while(var0 != null);

         return new ComputedObjectValue((Object)null, () -> {
            return var1;
         });
      }
   }

   public static Point2D.Double linePos(GameLinkedList<WormMoveLine>.Element var0, double var1) {
      WormMoveLine var3 = (WormMoveLine)var0.object;
      Point2D.Float var4 = var3.dir();
      return var1 < 0.0 ? new Point2D.Double((double)var3.x1 - (double)var4.x * var1, (double)var3.y1 - (double)var4.y * var1) : new Point2D.Double((double)var3.x1 + (double)var4.x * var1, (double)var3.y1 + (double)var4.y * var1);
   }

   public static Point2D.Double linePos(ComputedObjectValue<GameLinkedList<WormMoveLine>.Element, Double> var0) {
      Double var1 = (Double)var0.get();
      return var1 < 0.0 ? linePos((GameLinkedList.Element)var0.object, var1) : linePos((GameLinkedList.Element)var0.object, ((WormMoveLine)((GameLinkedList.Element)var0.object).object).dist() - var1);
   }

   private void removeRemaining(GameLinkedList<?>.Element var1) {
      while(var1 != null) {
         GameLinkedList.Element var2 = var1;
         var1 = var1.next();
         var2.remove();
      }

   }

   public boolean canHitThroughCollision() {
      return true;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return null;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public static MobDrawable getAngledDrawable(GameSprite var0, GameTexture var1, GameLight var2, final int var3, float var4, int var5, int var6, final int var7) {
      var6 -= var3;
      var6 -= var7;
      Point2D.Float var10 = GameMath.getAngleDir(var4);
      int var8;
      int var9;
      if (Math.abs(var10.y) - Math.abs(var10.x) <= 0.0F) {
         var8 = var10.x < 0.0F ? -1 : 1;
         var9 = var10.x < 0.0F ? 180 : 0;
      } else {
         var8 = var10.y < 0.0F ? 0 : 2;
         var9 = var10.y < 0.0F ? 90 : 270;
      }

      TextureDrawOptionsEnd var11 = var0.initDraw().rotate(var4 + (float)var9, var0.spriteWidth / 2, var0.spriteHeight / 2).rotateTexture(var8).light(var2);
      if (var1 != null) {
         var11 = var11.addShaderTexture((GameTexture)var1, 1);
      }

      final TextureDrawOptionsEnd var12 = var11.pos(var5, var6);
      return var1 != null ? new MobDrawable() {
         public void draw(TickManager var1) {
            GameResources.maskShader.use(0, -var3 - var7);

            try {
               var12.draw();
            } finally {
               GameResources.maskShader.stop();
            }

         }
      } : new MobDrawable() {
         public void draw(TickManager var1) {
            var12.draw();
         }
      };
   }

   public static void addAngledDrawable(List<MobDrawable> var0, GameSprite var1, GameTexture var2, GameLight var3, int var4, float var5, int var6, int var7, int var8) {
      var0.add(getAngledDrawable(var1, var2, var3, var4, var5, var6, var7, var8));
   }

   public static int getDirSprite(float var0) {
      Point2D.Float var1 = GameMath.getAngleDir(var0);
      if (Math.abs(var1.y) - Math.abs(var1.x) <= 0.0F) {
         return var1.x < 0.0F ? 3 : 1;
      } else {
         return var1.y < 0.0F ? 0 : 2;
      }
   }

   public static void addDrawable(List<MobDrawable> var0, GameSprite var1, GameTexture var2, GameLight var3, int var4, int var5, int var6, int var7) {
      var0.add(getDrawable(var1, var2, var3, var4, var5, var6, var7));
   }

   public static MobDrawable getDrawable(GameSprite var0, GameTexture var1, GameLight var2, final int var3, int var4, int var5, final int var6) {
      var5 -= var3;
      var5 -= var6;
      TextureDrawOptionsEnd var7 = var0.initDraw().light(var2);
      if (var1 != null) {
         var7 = var7.addShaderTexture((GameTexture)var1, 1);
      }

      final TextureDrawOptionsEnd var8 = var7.pos(var4, var5);
      return var1 != null ? new MobDrawable() {
         public void draw(TickManager var1) {
            GameResources.maskShader.use(0, -var3 - var6);

            try {
               var8.draw();
            } finally {
               GameResources.maskShader.stop();
            }

         }
      } : new MobDrawable() {
         public void draw(TickManager var1) {
            var8.draw();
         }
      };
   }

   protected <C extends WormMobHead<T, B>> WormMobHead<T, B>.BodyPartTarget getRandomTargetFromBodyPart(AINode<C> var1, TargetFinderAINode<C> var2, BiFunction<Mob, T, Boolean> var3) {
      Point var4 = new Point(this.getX(), this.getY());
      int var5 = this.bodyParts.size() - 5;
      ArrayList var6 = new ArrayList(var5);

      for(int var7 = 0; var7 < var5; ++var7) {
         WormMobBody var8 = (WormMobBody)((LevelMob)this.bodyParts.get(var7)).get(this.getLevel());
         if (var8 != null && var8.isVisible()) {
            var6.add(var8);
         }
      }

      if (var6.isEmpty()) {
         return null;
      } else {
         List var9 = (List)var2.streamPossibleTargets(this, var4, var2.distance).collect(Collectors.toList());
         Collections.shuffle(var9);
         return (BodyPartTarget)var9.stream().filter((var3x) -> {
            return var2.validity.isValidTarget(var1, this, var3x, true);
         }).map((var3x) -> {
            Collections.shuffle(var6);
            WormMobBody var4 = (WormMobBody)var6.stream().filter((var2) -> {
               return (Boolean)var3.apply(var3x, var2);
            }).findAny().orElse((Object)null);
            return var4 != null ? new BodyPartTarget(var4, var3x) : null;
         }).filter(Objects::nonNull).findAny().orElse((Object)null);
      }
   }

   protected class BodyPartTarget {
      public final T bodyPart;
      public final Mob target;

      public BodyPartTarget(T var2, Mob var3) {
         this.bodyPart = var2;
         this.target = var3;
      }
   }
}
