package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.StationaryPlayerShooterAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.FrostSentryProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FrostSentryMob extends HostileMob {
   public static LootTable lootTable;
   public static GameDamage damage;

   public FrostSentryMob() {
      super(100);
      this.setSpeed(0.0F);
      this.setFriction(3.0F);
      this.setKnockbackModifier(0.0F);
      this.setArmor(5);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -23, 28, 32);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new StationaryPlayerShooterAI<FrostSentryMob>(320) {
         public void shootTarget(FrostSentryMob var1, Mob var2) {
            FrostSentryProjectile var3 = new FrostSentryProjectile(FrostSentryMob.this.getLevel(), var1, var1.x, var1.y, var2.x, var2.y, 75.0F, 512, FrostSentryMob.damage, 50);
            var3.x -= var3.dx * 20.0F;
            var3.y -= var3.dy * 20.0F;
            FrostSentryMob.this.attack((int)(var1.x + var3.dx * 100.0F), (int)(var1.y + var3.dy * 100.0F), false);
            FrostSentryMob.this.getLevel().entityManager.projectiles.add(var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void shootTarget(Mob var1, Mob var2) {
            this.shootTarget((FrostSentryMob)var1, var2);
         }
      });
   }

   public void clientTick() {
      super.clientTick();
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.frostSentry, 1 + GameRandom.globalRandom.nextInt(5), var3, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 15;
      int var12 = var8.getDrawY(var6) - 26;
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      if (this.inLiquid(var5, var6)) {
         var12 -= 10;
      }

      float var13 = GameMath.limit(this.getAttackAnimProgress(), 0.0F, 1.0F);
      float var14;
      if (var13 < 0.5F) {
         var14 = var13 * 2.0F / 1.0F;
      } else {
         var14 = Math.abs((var13 - 0.5F) * 2.0F / 1.0F - 1.0F);
      }

      int var15 = (int)(var14 * 5.0F);
      final TextureDrawOptionsEnd var16 = MobRegistry.Textures.frostSentry.initDraw().sprite(0, 0, 32).size(32 - var15 * 2, 32 - var15).light(var10).pos(var11 + var15, var12 + var15);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var16.draw();
         }
      });
      if (this.inLiquid(var5, var6)) {
         var6 -= 10;
      }

      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.human_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2;
      var8 += this.getBobbing(var1, var2);
      var8 += this.getLevel().getTile(var1 / 32, var2 / 32).getMobSinkingAmount(this);
      return var5.initDraw().sprite(0, 0, var6).light(var3).pos(var7, var8);
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      if (this.isClient()) {
         Screen.playSound(GameResources.jingle, SoundEffect.effect(this).pitch(1.2F));
      }

   }

   static {
      lootTable = new LootTable(new LootItemInterface[]{randomMapDrop, LootItem.between("frostshard", 1, 2)});
      damage = new GameDamage(28.0F);
   }

   public static class FrostPillar extends GroundPillar {
      public GameTextureSection texture;
      public boolean mirror;

      public FrostPillar(int var1, int var2, double var3, long var5) {
         super(var1, var2, var3, var5);
         this.mirror = GameRandom.globalRandom.nextBoolean();
         this.texture = MobRegistry.Textures.cryoQueen == null ? null : (GameTextureSection)GameRandom.globalRandom.getOneOf((Object[])((new GameTextureSection(MobRegistry.Textures.frostSentry)).sprite(1, 0, 32), (new GameTextureSection(MobRegistry.Textures.frostSentry)).sprite(2, 0, 32), (new GameTextureSection(MobRegistry.Textures.frostSentry)).sprite(3, 0, 32), (new GameTextureSection(MobRegistry.Textures.frostSentry)).sprite(4, 0, 32), (new GameTextureSection(MobRegistry.Textures.frostSentry)).sprite(5, 0, 32)));
         this.behaviour = new GroundPillar.TimedBehaviour(300, 200, 800);
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
