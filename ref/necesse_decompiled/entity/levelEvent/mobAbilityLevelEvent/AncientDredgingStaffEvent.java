package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Iterator;
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
import necesse.level.maps.light.GameLight;

public class AncientDredgingStaffEvent extends WeaponShockWaveLevelEvent {
   protected final GroundPillarList<AncientDredgePillar> pillars = new GroundPillarList();

   public AncientDredgingStaffEvent() {
      super(30.0F, 20.0F, 5.0F);
   }

   public AncientDredgingStaffEvent(Mob var1, int var2, int var3, GameRandom var4, float var5, GameDamage var6, float var7, float var8, float var9, float var10) {
      super(var1, var2, var3, var4, var5, 30.0F, 20.0F, 5.0F, var6, var7, var8, var9, var10);
   }

   public void init() {
      super.init();
      if (this.isClient()) {
         this.level.entityManager.addPillarHandler(new GroundPillarHandler<AncientDredgePillar>(this.pillars) {
            protected boolean canRemove() {
               return AncientDredgingStaffEvent.this.isOver();
            }

            public double getCurrentDistanceMoved() {
               return 0.0;
            }
         });
      }

   }

   protected void spawnHitboxParticles(Polygon var1) {
   }

   protected void spawnHitboxParticles(float var1, float var2, float var3) {
      if (this.isClient()) {
         synchronized(this.pillars) {
            Iterator var5 = this.getPositionsAlongHit(var1, var2, var3, 20.0F, false).iterator();

            while(var5.hasNext()) {
               Point2D.Float var6 = (Point2D.Float)var5.next();
               this.pillars.add(new AncientDredgePillar((int)(var6.x + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F)), (int)(var6.y + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F)), (double)var1, this.level.getWorldEntity().getLocalTime()));
            }
         }
      }

   }

   public static class AncientDredgePillar extends GroundPillar {
      public GameTextureSection texture;
      public boolean mirror;

      public AncientDredgePillar(int var1, int var2, double var3, long var5) {
         super(var1, var2, var3, var5);
         this.mirror = GameRandom.globalRandom.nextBoolean();
         this.texture = null;
         GameTexture var7 = GameResources.ancientDredgingStaffPillars;
         if (var7 != null) {
            int var8 = var7.getHeight();
            int var9 = GameRandom.globalRandom.nextInt(var7.getWidth() / var8);
            this.texture = (new GameTextureSection(GameResources.ancientDredgingStaffPillars)).sprite(var9, 0, var8);
         }

         this.behaviour = new GroundPillar.TimedBehaviour(200, 100, 200);
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
