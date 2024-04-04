package necesse.entity.projectile;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class GlacialBowProjectile extends Projectile {
   private ArrayList<Integer> ignoredTargets = new ArrayList();
   private int shatterNumber = 2;

   public GlacialBowProjectile() {
   }

   public GlacialBowProjectile(Mob var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9) {
      this.setOwner(var1);
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.setDamage(var8);
      this.speed = var6;
      this.setDistance(var7);
      this.knockback = var9;
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.trailOffset = -6.0F;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.ignoredTargets.size());
      Iterator var2 = this.ignoredTargets.iterator();

      while(var2.hasNext()) {
         Integer var3 = (Integer)var2.next();
         var1.putNextInt(var3);
      }

      var1.putNextShortUnsigned(this.shatterNumber);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.ignoredTargets.add(var1.getNextInt());
      }

      this.shatterNumber = var1.getNextShortUnsigned();
   }

   public Color getParticleColor() {
      return new Color(109, 137, 222);
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 2;
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      var1.lifeTime(1000);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y);
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (var1 != null && this.isServer() && this.shatterNumber > 0) {
         Mob var5 = (Mob)GameUtils.streamTargets(this.getOwner(), GameUtils.rangeBounds(var3, var4, 350)).filter((var2x) -> {
            return var2x.isHostile && var2x != var1 && !this.ignoredTargets.contains(var2x.getUniqueID());
         }).filter((var2x) -> {
            return var2x.getDistance(var3, var4) < 350.0F;
         }).min(Comparator.comparingInt((var2x) -> {
            return (int)var2x.getDistance(var3, var4);
         })).orElse((Object)null);
         if (var5 != null) {
            --this.shatterNumber;
            GlacialBowProjectile var6 = new GlacialBowProjectile(this.getOwner(), var3, var4, var5.x, var5.y, this.speed, 600, this.getDamage(), this.knockback);
            if (this.modifier != null) {
               this.modifier.initChildProjectile(var6, 1.0F, 1);
            }

            var6.setTargetPrediction(var5);
            var6.ignoredTargets.addAll(this.ignoredTargets);
            var6.ignoredTargets.add(var1.getUniqueID());
            var6.shatterNumber = this.shatterNumber;
            this.getLevel().entityManager.projectiles.add(var6);
         }
      }

   }

   public boolean canHit(Mob var1) {
      return this.ignoredTargets.contains(var1.getUniqueID()) ? false : super.canHit(var1);
   }

   protected void playHitSound(float var1, float var2) {
      Screen.playSound(GameResources.jinglehit, SoundEffect.effect(var1, var2));
   }
}
