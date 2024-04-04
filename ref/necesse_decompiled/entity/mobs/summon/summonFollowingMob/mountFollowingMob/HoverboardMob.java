package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class HoverboardMob extends MountFollowingMob {
   protected TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(30);

   public HoverboardMob() {
      super(100);
      this.setSpeed(100.0F);
      this.setFriction(0.2F);
      this.setSwimSpeed(1.0F);
      this.accelerationMod = 0.8F;
      this.setKnockbackModifier(0.5F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -14, 28, 28);
      this.selectBox = new Rectangle(-15, -15, 30, 30);
      this.overrideMountedWaterWalking = true;
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (this.isClient() && (this.moveX != 0.0F || this.moveY != 0.0F)) {
         this.particleTicks.tick(var1);

         float var2;
         float var3;
         for(; this.particleTicks.shouldTick(); this.getLevel().entityManager.addParticle(this.x - var2 * 20.0F + GameRandom.globalRandom.floatGaussian() * 3.0F, this.y - var3 * 12.0F + GameRandom.globalRandom.floatGaussian() * 3.0F, Particle.GType.COSMETIC).movesConstant(this.dx / 5.0F, this.dy / 5.0F).color(new Color(80, 98, 108)).sizeFades(6, 12).lifeTime(1000)) {
            var2 = 0.0F;
            var3 = 0.0F;
            if (this.dir == 0) {
               var3 = -1.0F;
            } else if (this.dir == 1) {
               var2 = 1.0F;
            } else if (this.dir == 2) {
               var3 = 1.0F;
            } else if (this.dir == 3) {
               var2 = -1.0F;
            }
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      if (!this.isMounted()) {
         this.moveX = 0.0F;
         this.moveY = 0.0F;
      }

   }

   public void clientTick() {
      super.clientTick();
      if (!this.isMounted()) {
         this.moveX = 0.0F;
         this.moveY = 0.0F;
      }

   }

   public int getFlyingHeight() {
      return 2;
   }

   protected String getInteractTip(PlayerMob var1, boolean var2) {
      return this.isMounted() ? null : Localization.translate("controls", "usetip");
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 40;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.hoverBoard.body.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
         }

         public void drawBehindRider(TickManager var1) {
            var14.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      return new Point((int)(this.getWorldEntity().getTime() / (long)this.getRockSpeed()) % 4, var3 % 4);
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.hoverBoard.shadow;
      int var6 = var4.getDrawX(var1) - 32;
      int var7 = var4.getDrawY(var2) - 40;
      var7 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(0, this.dir, 64).light(var3).pos(var6, var7);
   }

   public int getRockSpeed() {
      return 500;
   }

   public int getWaterRockSpeed() {
      return 500;
   }

   public Point getSpriteOffset(int var1, int var2) {
      Point var3 = new Point(0, 0);
      if (var1 == 0 || var1 == 2) {
         var3.y = 2;
      }

      var3.x += this.getRiderDrawXOffset();
      var3.y += this.getRiderDrawYOffset();
      return var3;
   }

   public int getRiderDrawYOffset() {
      return -8;
   }

   public int getRiderArmSpriteX() {
      return 1;
   }

   public int getRiderDir(int var1) {
      return (var1 + 1) % 4;
   }

   public GameTexture getRiderMask() {
      return null;
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of(new ModifierValue(BuffModifiers.BOUNCY, true));
   }

   public Stream<ModifierValue<?>> getDefaultRiderModifiers() {
      return Stream.of(new ModifierValue(BuffModifiers.TRAVEL_DISTANCE, 2), new ModifierValue(BuffModifiers.WATER_WALKING, true));
   }
}
