package necesse.entity.mobs.hostile.bosses;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AncientVultureEggMob extends BossMob {
   public static LootTable lootTable = new LootTable();
   public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(30, 60, 75, 85, 110);
   private long spawnTime;
   private int hatchTime;
   private AncientVultureMob owner;

   public AncientVultureEggMob() {
      this((AncientVultureMob)null);
   }

   public AncientVultureEggMob(AncientVultureMob var1) {
      super(100);
      this.hatchTime = 6000;
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.isSummoned = true;
      this.owner = var1;
      this.collision = new Rectangle(-10, -12, 20, 20);
      this.hitBox = new Rectangle(-20, -20, 40, 40);
      this.selectBox = new Rectangle(-18, -45, 36, 58);
      this.setKnockbackModifier(0.0F);
      this.setArmor(20);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.spawnTime = var1.getNextLong();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextLong(this.spawnTime);
   }

   public void init() {
      super.init();
      if (this.spawnTime == 0L) {
         this.spawnTime = this.getWorldEntity().getTime();
      }

   }

   public void tickMovement(float var1) {
      this.dx = 0.0F;
      this.dy = 0.0F;
   }

   public void serverTick() {
      super.serverTick();
      if (this.getWorldEntity().getTime() > this.spawnTime + (long)this.hatchTime) {
         VultureHatchling var1 = new VultureHatchling(this.owner);
         this.getLevel().entityManager.addMob(var1, (float)this.getX(), (float)this.getY());
         if (this.owner != null) {
            this.owner.spawnedMobs.removeIf(Entity::removed);
            this.owner.spawnedMobs.add(var1);
         }

         this.setHealth(0);
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   protected void playDeathSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.crackdeath, SoundEffect.effect(this).volume(0.8F).pitch(var1));
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.ancientVultureEgg, var3, 2, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 54;
      TextureDrawOptions var13 = this.getShadowDrawOptions(var5, var6, var10, var8);
      float var14 = (float)(this.getWorldEntity().getTime() - this.spawnTime) / (float)this.hatchTime;
      float var15 = var14 * 4.0F;
      float var16 = var15 - (float)((int)var15);
      float var17;
      if (var16 < 0.3F && var15 >= 1.0F) {
         float var18 = var16 / 0.3F;
         float var19 = var18 * 4.0F;
         float var20 = var19 - (float)((int)var19);
         switch ((int)var19) {
            case 0:
               var17 = -var20 * 25.0F;
               break;
            case 1:
               var17 = -Math.abs(var20 - 1.0F) * 25.0F;
               break;
            case 2:
               var17 = var20 * 25.0F;
               break;
            case 3:
               var17 = Math.abs(var20 - 1.0F) * 25.0F;
               break;
            default:
               var17 = 0.0F;
         }
      } else {
         var17 = 0.0F;
      }

      int var21 = (int)var15 / 2;
      int var22 = 32 * ((int)var15 % 2);
      TextureDrawOptionsEnd var23 = MobRegistry.Textures.ancientVultureEgg.initDraw().spriteSection(var21, 0, 64, var22, 32 + var22, 0, 64, false).rotate(var17, 16, 54).light(var10).pos(var11, var12);
      var3.add((var2x) -> {
         var13.draw();
         var23.draw();
      });
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.ancientVultureEgg_shadow;
      int var6 = var4.getDrawX(var1) - var5.getWidth() / 2;
      int var7 = var4.getDrawY(var2) - var5.getHeight() / 2 + 2;
      return var5.initDraw().light(var3).pos(var6, var7);
   }
}
