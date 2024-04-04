package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrapArrowProjectile extends Projectile {
   public TrapArrowProjectile() {
   }

   public TrapArrowProjectile(float var1, float var2, float var3, float var4, GameDamage var5, Mob var6) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = 200.0F;
      this.setDamage(var5);
      this.setOwner(var6);
      this.setDistance(400);
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.heightBasedOnDistance = true;
      this.setWidth(16.0F);
      this.clientHandlesHit = false;
      this.canBreakObjects = true;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(150, 150, 150), 10.0F, 250, 18.0F);
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

   public GameMessage getAttackerName() {
      Mob var1 = this.getOwner();
      return (GameMessage)(var1 != null ? var1.getAttackerName() : new LocalMessage("deaths", "arrowtrapname"));
   }

   public void applyDamage(Mob var1, float var2, float var3) {
      if (this.getLevel().isTrialRoom) {
         GameDamage var4 = new GameDamage(DamageTypeRegistry.TRUE, (float)var1.getMaxHealth() / 4.0F);
         var1.isServerHit(var4, var1.x - var2 * -this.dx * 50.0F, var1.y - var3 * -this.dy * 50.0F, (float)this.knockback, this);
      } else {
         super.applyDamage(var1, var2, var3);
      }

   }

   protected void playHitSound(float var1, float var2) {
      Screen.playSound(GameResources.bowhit, SoundEffect.effect(var1, var2));
   }
}
