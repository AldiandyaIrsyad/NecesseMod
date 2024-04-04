package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SpiderEggObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SpiderEggBrokenParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderEggObject extends GameObject {
   private GameTexture texture;
   private GameTexture brokenTexture;

   public SpiderEggObject() {
      super(new Rectangle(32, 32));
      this.drawDamage = false;
      this.objectHealth = 10;
      this.attackThrough = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/spideregg");
      this.brokenTexture = GameTexture.fromFile("objects/spideregg_broken");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameTexture var9 = this.texture;
      ObjectEntity var10 = this.getCurrentObjectEntity(var3, var4, var5);
      if (var10 instanceof SpiderEggObjectEntity && ((SpiderEggObjectEntity)var10).isBroken) {
         var9 = this.brokenTexture;
      }

      GameLight var11 = var3.getLightLevel(var4, var5);
      int var12 = var7.getTileDrawX(var4) - 16;
      int var13 = var7.getTileDrawY(var5) - 24;
      final TextureDrawOptionsEnd var14 = var9.initDraw().sprite(0, 0, 64).light(var11).pos(var12, var13);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new SpiderEggObjectEntity(var1, var2, var3);
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      this.playDamageSound(var1, var2, var3, true);
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.slimesplash, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      super.onDestroyed(var1, var2, var3, var4, var5);
      if (var4 != null) {
         ObjectEntity var6 = this.getCurrentObjectEntity(var1, var2, var3);
         if (var6 instanceof SpiderEggObjectEntity) {
            SpiderEggObjectEntity var7 = (SpiderEggObjectEntity)var6;
            if (!var7.isBroken) {
               var7.breakEgg();
            }
         }
      }

      if (var1.isClient()) {
         var1.entityManager.addParticle((Particle)(new SpiderEggBrokenParticle(var1, (float)(var2 * 32 + 16), (float)(var3 * 32 + 16), 5000L, this.brokenTexture)), Particle.GType.CRITICAL);

         for(int var8 = 0; var8 < 40; ++var8) {
            var1.entityManager.addParticle((float)(var2 * 32 + 16), (float)(var3 * 32 + 16), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).lifeTime(1000).sizeFades(30, 50).movesFriction((float)(GameRandom.globalRandom.getIntBetween(-10, 10) * (GameRandom.globalRandom.nextBoolean() ? -3 : 3)), (float)(GameRandom.globalRandom.getIntBetween(5, 15) * (GameRandom.globalRandom.nextBoolean() ? -1 : -3)), 0.5F).color(new Color(166, 204, 52)).height(10.0F);
         }
      }

   }
}
