package necesse.entity.mobs.hostile.bosses;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperSpiritPortalMob extends BossMob {
   private long spawnTime;
   public ReaperMob owner;
   public final EmptyMobAbility magicSoundAbility;

   public ReaperSpiritPortalMob() {
      super(100);
      this.isSummoned = true;
      this.isStatic = true;
      this.setSpeed(100.0F);
      this.setArmor(20);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-18, -15, 36, 36);
      this.selectBox = new Rectangle(-20, -18, 40, 36);
      this.magicSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (ReaperSpiritPortalMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(ReaperSpiritPortalMob.this));
            }

         }
      });
   }

   public void init() {
      super.init();
      this.spawnTime = this.getWorldEntity().getTime();
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 16;
      float var13 = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 2.0F;
      TextureDrawOptionsEnd var14 = MobRegistry.Textures.reaperSpiritPortal.initDraw().sprite(0, 0, 32).light(var10).rotate(-var13, 16, 16).pos(var11, var12);
      var3.add((var1x) -> {
         var14.draw();
      });
   }
}
