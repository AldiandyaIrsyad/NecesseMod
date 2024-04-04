package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Iterator;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class MoundShockWaveLevelEvent extends ShockWaveLevelEvent {
   protected final GroundPillarList<Mound> pillars = new GroundPillarList();

   public MoundShockWaveLevelEvent() {
      super(360.0F, 150.0F, 150.0F, 20.0F, 5.0F);
   }

   public MoundShockWaveLevelEvent(Mob var1, int var2, int var3, GameRandom var4, float var5) {
      super(var1, var2, var3, var4, var5, 360.0F, 150.0F, 150.0F, 20.0F, 5.0F);
   }

   public void init() {
      super.init();
      this.drawDebugHitboxes = true;
      if (this.isClient()) {
         this.level.entityManager.addPillarHandler(new GroundPillarHandler<Mound>(this.pillars) {
            protected boolean canRemove() {
               return MoundShockWaveLevelEvent.this.isOver();
            }

            public double getCurrentDistanceMoved() {
               return 0.0;
            }
         });
      }

   }

   public void damageTarget(Mob var1) {
      var1.isServerHit(new GameDamage(10.0F), 0.0F, 0.0F, 0.0F, this.owner);
   }

   protected void spawnHitboxParticles(Polygon var1) {
   }

   protected void spawnHitboxParticles(float var1, float var2, float var3) {
      if (this.isClient()) {
         synchronized(this.pillars) {
            Iterator var5 = this.getPositionsAlongHit(var1, var2, var3, 20.0F, false).iterator();

            while(var5.hasNext()) {
               Point2D.Float var6 = (Point2D.Float)var5.next();
               this.pillars.add(new Mound((int)(var6.x + GameRandom.globalRandom.getFloatBetween(-5.0F, 5.0F)), (int)(var6.y + GameRandom.globalRandom.getFloatBetween(-5.0F, 5.0F)), (double)var1, this.level.getWorldEntity().getLocalTime()));
            }
         }
      }

   }

   public void hitObject(LevelObjectHit var1) {
   }

   private static class Mound extends GroundPillar {
      public GameTexture texture;

      public Mound(int var1, int var2, double var3, long var5) {
         super(var1, var2, var3, var5);
         this.texture = (GameTexture)GameRandom.globalRandom.getOneOf((Object[])(MobRegistry.Textures.mound1, MobRegistry.Textures.mound2, MobRegistry.Textures.mound3));
         this.behaviour = new GroundPillar.TimedBehaviour(200, 100, 200);
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
