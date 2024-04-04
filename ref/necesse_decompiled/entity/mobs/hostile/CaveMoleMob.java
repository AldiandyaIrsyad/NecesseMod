package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CaveMoleMob extends HostileMob {
   public static LootTable lootTable = new LootTable();
   public static GameDamage damage = new GameDamage(30.0F);
   private final GroundPillarList<Mound> mounds = new GroundPillarList();
   private int moundCounter;
   private int stateChangeAnimationTime = 200;
   private double moveBuffer;
   private double moveCounter;
   private long stateChangeTime;
   private boolean isUp;
   private boolean nextIsUp;
   private boolean wantIsUp;
   private int wantIsUpCounter;

   public CaveMoleMob() {
      super(75);
      this.setSpeed(40.0F);
      this.setFriction(3.0F);
      this.setKnockbackModifier(0.0F);
      this.attackCooldown = 1500;
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -31, 28, 38);
      this.isUp = true;
      this.nextIsUp = true;
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.isUp);
      var1.putNextBoolean(this.nextIsUp);
      var1.putNextBoolean(this.wantIsUp);
      long var2 = this.getWorldEntity().getLocalTime() - this.stateChangeTime;
      var1.putNextLong(var2);
      var1.putNextByteUnsigned(this.wantIsUpCounter);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.isUp = var1.getNextBoolean();
      this.nextIsUp = var1.getNextBoolean();
      this.wantIsUp = var1.getNextBoolean();
      long var3 = var1.getNextLong();
      this.stateChangeTime = this.getWorldEntity().getLocalTime() - var3;
      this.wantIsUpCounter = var1.getNextByteUnsigned();
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerChaserWandererAI<Mob>((Supplier)null, 512, 256, 40000, true, false) {
         public boolean attackTarget(Mob var1, Mob var2) {
            return this.shootSimpleProjectile(var1, var2, "stone", CaveMoleMob.damage, 80, 384);
         }
      });
   }

   public boolean canAttack() {
      return super.canAttack() && this.isUp && this.nextIsUp;
   }

   public void tickMovement(float var1) {
      label16:
      while(true) {
         if (var1 > 0.0F) {
            int var2 = this.getRockSpeed();
            Point2D.Float var3 = new Point2D.Float(this.x, this.y);
            super.tickMovement(Math.min((float)(var2 * 2), var1));
            var1 -= (float)(var2 * 2);
            double var4 = var3.distance((double)this.x, (double)this.y);
            this.moveBuffer += var4;
            this.moveCounter += var4;

            while(true) {
               if (!(this.moveBuffer > (double)var2)) {
                  continue label16;
               }

               this.addNewMound(GameMath.normalize(this.x - var3.x, this.y - var3.y), false);
               this.moveBuffer -= (double)var2;
            }
         }

         return;
      }
   }

   protected void calcAcceleration(float var1, float var2, float var3, float var4, float var5) {
      super.calcAcceleration(var1, var2, var3, var4, var5);
      if (this.isUp || this.nextIsUp) {
         this.dx = 0.0F;
         this.dy = 0.0F;
      }

   }

   public void setFacingDir(float var1, float var2) {
   }

   public void clientTick() {
      super.clientTick();
      this.tickStateChange();
      synchronized(this.mounds) {
         this.mounds.clean(this.getWorldEntity().getLocalTime(), this.moveCounter);
      }
   }

   public void serverTick() {
      super.serverTick();
      this.tickStateChange();
      synchronized(this.mounds) {
         this.mounds.clean(this.getWorldEntity().getLocalTime(), this.moveCounter);
      }
   }

   public CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().allLiquidTiles();
   }

   private void tickStateChange() {
      if (this.nextIsUp != this.isUp) {
         long var1 = this.getWorldEntity().getLocalTime();
         if (var1 > this.stateChangeTime + (long)this.stateChangeAnimationTime) {
            this.isUp = this.nextIsUp;
         }
      } else if (this.isAccelerating() && this.isUp && this.canAttack()) {
         this.changeIsUp(false);
      } else if (!this.isAccelerating() && !this.isUp) {
         this.changeIsUp(true);
      }

   }

   private float getStateChangeProgress() {
      if (this.nextIsUp != this.isUp) {
         long var1 = this.getWorldEntity().getLocalTime();
         if (var1 <= this.stateChangeTime + (long)this.stateChangeAnimationTime) {
            return GameMath.limit((float)(var1 - this.stateChangeTime) / (float)this.stateChangeAnimationTime, 0.0F, 1.0F);
         }
      }

      return 1.0F;
   }

   private void changeIsUp(boolean var1) {
      if (this.nextIsUp != var1) {
         if (this.wantIsUp != var1) {
            this.wantIsUp = var1;
            this.wantIsUpCounter = 0;
         } else {
            ++this.wantIsUpCounter;
            if (this.wantIsUpCounter >= 4) {
               this.stateChangeTime = this.getWorldEntity().getLocalTime();
               this.nextIsUp = var1;
               this.wantIsUpCounter = 0;
            }
         }
      }
   }

   private void addNewMound(Point2D.Float var1, boolean var2) {
      Point2D.Float var3 = GameMath.getPerpendicularDir(var1.x, var1.y);
      int var4 = this.moundCounter % 2 == 0 ? 5 : -5;
      synchronized(this.mounds) {
         this.mounds.add(new Mound(this.getX() + (int)(var3.x * (float)var4 + var1.x * 10.0F) + GameRandom.globalRandom.getIntBetween(-2, 2) + (var2 ? var4 : 0), this.getY() + (int)(var3.y * (float)var4 * 0.7F + var1.y * 10.0F) + GameRandom.globalRandom.getIntBetween(-2, 2), this.moveCounter - (double)(var2 ? 20 : 0), this.getWorldEntity().getLocalTime()));
      }

      ++this.moundCounter;
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.mole, 2, var3, 16, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addExtraDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addExtraDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      long var10 = this.getWorldEntity().getLocalTime();
      synchronized(this.mounds) {
         this.mounds.clean(var10, this.moveCounter);
         Iterator var13 = this.mounds.iterator();

         while(var13.hasNext()) {
            final Mound var14 = (Mound)var13.next();
            final DrawOptions var15 = var14.getDrawOptions(var4, var10, this.moveCounter, var8);
            if (var15 != null) {
               var1.add(new LevelSortedDrawable(this) {
                  public int getSortY() {
                     return var14.y;
                  }

                  public void draw(TickManager var1) {
                     var15.draw();
                  }
               });
            }
         }

      }
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5);
      int var12 = var8.getDrawY(var6);
      byte var13 = 0;
      if (this.dir == 3 || this.dir == 2) {
         var13 = 1;
      }

      float var14 = this.getStateChangeProgress();
      if (!this.nextIsUp) {
         var14 = Math.abs(var14 - 1.0F);
      }

      int var15 = (int)(var14 * 32.0F);
      final DrawOptionsList var16 = new DrawOptionsList();
      var16.add(MobRegistry.Textures.mole.initDraw().spriteSection(0, var13, 32, 0, 32, 0, var15).light(var10).pos(var11 - 16, var12 + 32 - var15 - 30));
      GameTile var17 = var4.getTile(var5 / 32, var6 / 32);
      if (!var17.isLiquid) {
         Color var18 = var17.getMapColor(var4, var5 / 32, var6 / 32);
         int var19 = MobRegistry.Textures.mound1.getWidth();
         int var20 = MobRegistry.Textures.mound1.getHeight();
         int var21 = (int)(var14 * (float)var20);
         var16.add(MobRegistry.Textures.mound1.initDraw().section(0, var19, 0, var21).color(var18).light(var10).pos(var11 - var19 / 2 - 5, var12 + var20 / 2 - var21));
         var16.add(MobRegistry.Textures.mound1.initDraw().section(0, var19, 0, var21).color(var18).light(var10).pos(var11 - var19 / 2 + 5, var12 + var20 / 2 - var21));
      }

      float var22 = this.getAttackAnimProgress();
      final DrawOptions var23;
      if (this.isAttacking) {
         var23 = ItemAttackDrawOptions.start(this.dir).armSprite(MobRegistry.Textures.mole, 0, 2, 32).armRotatePoint(10, 15).swingRotation(var22).light(var10).pos(var11 - 31, var12 - 45);
      } else {
         var23 = null;
      }

      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var16.draw();
            if (var23 != null) {
               var23.draw();
            }

         }
      });
   }

   public int getRockSpeed() {
      return 5;
   }

   protected void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      var1.add("isUp: " + this.isUp);
      var1.add("nextIsUp: " + this.nextIsUp);
      var1.add("wantIsUp: " + this.wantIsUp);
      var1.add("stateChange: " + this.getStateChangeProgress());
   }

   private static class Mound extends GroundPillar {
      public GameTexture texture;

      public Mound(int var1, int var2, double var3, long var5) {
         super(var1, var2, var3, var5);
         this.texture = (GameTexture)GameRandom.globalRandom.getOneOf((Object[])(MobRegistry.Textures.mound1, MobRegistry.Textures.mound2, MobRegistry.Textures.mound3));
      }

      public DrawOptions getDrawOptions(Level var1, long var2, double var4, GameCamera var6) {
         GameTile var7 = var1.getTile(this.x / 32, this.y / 32);
         if (var7.isLiquid) {
            return null;
         } else {
            GameLight var8 = var1.getLightLevel(this.x / 32, this.y / 32);
            Color var9 = var7.getMapColor(var1, this.x / 32, this.y / 32);
            int var10 = var6.getDrawX(this.x);
            int var11 = var6.getDrawY(this.y);
            double var12 = this.getHeight(var2, var4);
            int var14 = (int)(var12 * (double)this.texture.getHeight());
            return this.texture.initDraw().section(0, this.texture.getWidth(), 0, var14).color(var9).light(var8).pos(var10 - this.texture.getWidth() / 2, var11 - var14);
         }
      }
   }
}
