package necesse.entity.pickup;

import java.awt.Color;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketPickupEntityPickup;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class StarBarrierPickupEntity extends PickupEntity {
   private int tickCounter;

   public StarBarrierPickupEntity() {
   }

   public StarBarrierPickupEntity(Level var1, float var2, float var3, float var4, float var5) {
      super(var1, var2, var3, var4, var5);
      this.bouncy = 0.75F;
   }

   public void init() {
      super.init();
      if (this.isClient()) {
         GameRandom var1 = GameRandom.globalRandom;

         for(int var2 = 0; var2 < 10; ++var2) {
            this.getLevel().entityManager.addParticle(this.x + var1.floatGaussian() * 16.0F, this.y + var1.floatGaussian() * 12.0F, Particle.GType.IMPORTANT_COSMETIC).sizeFades(22, 11).sprite(GameResources.magicSparkParticles.sprite(var1.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).movesFrictionAngle((float)var1.getIntBetween(0, 360), 50.0F, 0.5F).lifeTime(5000).givesLight(75.0F, 0.5F);
         }
      }

   }

   public void clientTick() {
      ++this.tickCounter;
      if (this.tickCounter > 200) {
         this.remove();
      } else {
         super.clientTick();
         if (this.getLevel() != null) {
            this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 10.0), this.y - 20.0F + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sizeFades(5, 10).lifeTime(1500).movesConstantAngle((float)GameRandom.globalRandom.getIntBetween(0, 360), 3.0F).height(-20.0F).givesLight(247.0F, 1.0F);
         }
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if (this.tickCounter > 200) {
         this.remove();
      } else {
         super.serverTick();
      }

   }

   public void onPickup(ServerClient var1) {
      this.getLevel().getServer().network.sendToClientsWithEntity(new PacketPickupEntityPickup(this, new Packet()), this);
      Screen.playSound(GameResources.shatter2, SoundEffect.effect(this).volume(3.0F).pitch(2.0F));
      var1.playerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAR_BARRIER_BUFF, var1.playerMob, 20000, (Attacker)null), true);
      this.remove();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      var5.getLightLevel((int)(this.x / 32.0F), (int)(this.y / 32.0F));
      int var10 = var7.getDrawX(this.x);
      int var11 = var7.getDrawY(this.y);
      byte var12 = 100;
      int var13 = (int)(this.getWorldEntity().getTime() / (long)var12) % 2;
      final TextureDrawOptionsEnd var14 = GameResources.starBarrierPickup.initDraw().sprite(var13, 0, 28, 40).size(28, 40).pos(var10 - 14, var11 - 20);
      var3.add(new LevelSortedDrawable(this) {
         public int getSortY() {
            return 0;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
   }
}
