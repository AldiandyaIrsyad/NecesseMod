package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SlimeQuakeWarningEvent extends WeaponShockWaveLevelEvent {
   protected final GroundPillarList<SlimePillar> pillars = new GroundPillarList();
   protected int warningTime;

   public SlimeQuakeWarningEvent() {
      super(360.0F, 135.0F, 50.0F);
   }

   public SlimeQuakeWarningEvent(Mob var1, int var2, int var3, GameRandom var4, float var5, float var6, float var7, int var8, float var9) {
      super(var1, var2, var3, var4, var5, 360.0F, 135.0F, 50.0F, new GameDamage(0.0F), 0.0F, var6, 0.0F, var7);
      this.hitDistanceOffset = var9;
      this.warningTime = var8;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.warningTime);
      var1.putNextFloat(this.hitDistanceOffset);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.warningTime = var1.getNextInt();
      this.hitDistanceOffset = var1.getNextFloat();
   }

   public void init() {
      super.init();
      if (this.isClient()) {
         this.level.entityManager.addPillarHandler(new GroundPillarHandler<SlimePillar>(this.pillars) {
            protected boolean canRemove() {
               return SlimeQuakeWarningEvent.this.isOver();
            }

            public double getCurrentDistanceMoved() {
               return 0.0;
            }
         });
      }

   }

   public void damageTarget(Mob var1) {
   }

   public void hitObject(LevelObjectHit var1) {
      if (this.canDamageObjects) {
         var1.getLevelObject().attackThrough(this.damage, this.owner);
      }

   }

   protected void spawnHitboxParticles(Polygon var1) {
   }

   protected void spawnHitboxParticles(float var1, float var2, float var3) {
      if (this.isClient()) {
         synchronized(this.pillars) {
            Iterator var5 = this.getPositionsAlongHit(var1, var2, var3, 20.0F, false).iterator();

            Point2D.Float var6;
            while(var5.hasNext()) {
               var6 = (Point2D.Float)var5.next();
               this.pillars.add(new SlimePillar((int)(var6.x + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F)), (int)(var6.y + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F)), (double)var1, this.level.getWorldEntity().getLocalTime(), this.warningTime));
            }

            var5 = this.getPositionsAlongHit(var1 + 20.0F, var2, var3, 20.0F, false).iterator();

            while(var5.hasNext()) {
               var6 = (Point2D.Float)var5.next();
               this.pillars.add(new SlimePillar((int)(var6.x + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F)), (int)(var6.y + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F)), (double)var1, this.level.getWorldEntity().getLocalTime(), this.warningTime));
            }

            var5 = this.getPositionsAlongHit(var1 - 20.0F, var2, var3, 20.0F, false).iterator();

            while(var5.hasNext()) {
               var6 = (Point2D.Float)var5.next();
               this.pillars.add(new SlimePillar((int)(var6.x + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F)), (int)(var6.y + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F)), (double)var1, this.level.getWorldEntity().getLocalTime(), this.warningTime));
            }
         }
      }

   }

   public static class SlimePillar extends GroundPillar {
      public GameTextureSection texture;
      public boolean mirror;

      public SlimePillar(int var1, int var2, double var3, long var5, int var7) {
         super(var1, var2, var3, var5);
         this.mirror = GameRandom.globalRandom.nextBoolean();
         this.texture = null;
         GameTexture var8 = GameResources.slimeGround;
         if (var8 != null) {
            int var9 = var8.getHeight();
            int var10 = GameRandom.globalRandom.nextInt(var8.getWidth() / var9);
            this.texture = (new GameTextureSection(GameResources.slimeGround)).sprite(var10, 0, var9);
         }

         this.behaviour = new GroundPillar.TimedBehaviour(Math.max(var7 - 300, 100), 500, 200);
      }

      public DrawOptions getDrawOptions(Level var1, long var2, double var4, GameCamera var6) {
         GameLight var7 = var1.getLightLevel(this.x / 32, this.y / 32);
         int var8 = var6.getDrawX(this.x);
         int var9 = var6.getDrawY(this.y);
         double var10 = this.getHeight(var2, var4);
         int var12 = (int)(var10 * (double)this.texture.getHeight());
         return this.texture.section(0, this.texture.getWidth(), 0, var12).initDraw().mirror(this.mirror, false).light(var7).pos(var8 - this.texture.getWidth() / 2, var9 - var12);
      }
   }
}
