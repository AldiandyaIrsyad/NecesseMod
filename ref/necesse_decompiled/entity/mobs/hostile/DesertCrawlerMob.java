package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DesertCrawlerMob extends HostileMob {
   public static LootTable lootTable = new LootTable();
   public static GameDamage baseDamage = new GameDamage(60.0F);
   public static GameDamage incursionDamage = new GameDamage(70.0F);
   private final GroundPillarList<Mound> mounds = new GroundPillarList();
   private int moundCounter;
   private double moveBuffer;
   private double moveCounter;

   public DesertCrawlerMob() {
      super(300);
      this.setSpeed(45.0F);
      this.setFriction(4.0F);
      this.setKnockbackModifier(0.4F);
      this.setArmor(20);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-16, -16, 32, 32);
      this.selectBox = new Rectangle(-23, -23, 46, 46);
   }

   public void init() {
      super.init();
      GameDamage var1;
      if (this.getLevel() instanceof IncursionLevel) {
         this.setMaxHealth(350);
         this.setHealthHidden(this.getMaxHealth());
         this.setArmor(30);
         var1 = incursionDamage;
      } else {
         var1 = baseDamage;
      }

      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI((Supplier)null, 512, var1, 100, 40000));
      this.addNewMound(new Point2D.Float(0.0F, 1.0F), true);
      this.addNewMound(new Point2D.Float(1.0F, 0.0F), true);
      this.addNewMound(new Point2D.Float(1.0F, 0.0F), true);
   }

   public LootTable getLootTable() {
      return lootTable;
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

   private void addNewMound(Point2D.Float var1, boolean var2) {
      Point2D.Float var3 = GameMath.getPerpendicularDir(var1.x, var1.y);
      int var4 = this.moundCounter % 2 == 0 ? 5 : -5;
      synchronized(this.mounds) {
         this.mounds.add(new Mound(this.getX() + (int)(var3.x * (float)var4 + var1.x * 10.0F) + GameRandom.globalRandom.getIntBetween(-2, 2) + (var2 ? var4 : 0), this.getY() + (int)(var3.y * (float)var4 * 0.7F + var1.y * 10.0F) + GameRandom.globalRandom.getIntBetween(-2, 2), this.moveCounter - (double)(var2 ? 20 : 0), this.getWorldEntity().getLocalTime()));
      }

      ++this.moundCounter;
   }

   public void clientTick() {
      super.clientTick();
      synchronized(this.mounds) {
         this.mounds.clean(this.getWorldEntity().getLocalTime(), this.moveCounter);
      }
   }

   public void serverTick() {
      super.serverTick();
      synchronized(this.mounds) {
         this.mounds.clean(this.getWorldEntity().getLocalTime(), this.moveCounter);
      }
   }

   public CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().allLiquidTiles();
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.desertCrawler, var3, 0, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
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
   }

   public int getRockSpeed() {
      return 5;
   }

   private static class Mound extends GroundPillar {
      public int res = 32;
      public GameTextureSection texture = null;

      public Mound(int var1, int var2, double var3, long var5) {
         super(var1, var2, var3, var5);
         GameTexture var7 = MobRegistry.Textures.mounds32;
         if (var7 != null) {
            int var8 = GameRandom.globalRandom.nextInt(var7.getWidth() / this.res);
            this.texture = (new GameTextureSection(var7)).sprite(var8, 0, this.res, var7.getHeight());
         }

         this.behaviour = new GroundPillar.DistanceTimedBehaviour(1000, 0, 500, 50.0, 20.0, 100.0);
      }

      public DrawOptions getDrawOptions(Level var1, long var2, double var4, GameCamera var6) {
         GameTile var7 = var1.getTile(this.x / 32, this.y / 32);
         if (var7.isLiquid) {
            return null;
         } else {
            Color var8 = var7.getMapColor(var1, this.x / 32, this.y / 32);
            GameLight var9 = var1.getLightLevel(this.x / 32, this.y / 32);
            int var10 = var6.getDrawX(this.x);
            int var11 = var6.getDrawY(this.y);
            double var12 = this.getHeight(var2, var4);
            int var14 = (int)(var12 * (double)this.texture.getHeight());
            return this.texture.section(0, this.texture.getWidth(), 0, var14).initDraw().color(var8).light(var9).pos(var10 - this.texture.getWidth() / 2, var11 - var14);
         }
      }
   }
}
