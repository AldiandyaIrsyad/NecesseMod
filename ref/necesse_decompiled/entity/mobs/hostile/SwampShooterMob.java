package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.StationaryPlayerShooterAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SwampBoltProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampShooterMob extends HostileMob {
   public static LootTable lootTable;
   public static GameDamage damage;

   public SwampShooterMob() {
      super(200);
      this.setSpeed(0.0F);
      this.setFriction(3.0F);
      this.setKnockbackModifier(0.0F);
      this.setArmor(10);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new StationaryPlayerShooterAI<SwampShooterMob>(448) {
         public void shootTarget(SwampShooterMob var1, Mob var2) {
            SwampBoltProjectile var3 = new SwampBoltProjectile(SwampShooterMob.this.getLevel(), var1, var1.x, var1.y, var2.x, var2.y, 100.0F, 640, SwampShooterMob.damage, 50);
            var3.setTargetPrediction(var2);
            SwampShooterMob.this.attack((int)(var1.x + var3.dx * 100.0F), (int)(var1.y + var3.dy * 100.0F), false);
            var3.x += Math.signum(SwampShooterMob.this.attackDir.x) * 10.0F;
            var3.y += SwampShooterMob.this.attackDir.y * 6.0F;
            SwampShooterMob.this.getLevel().entityManager.projectiles.add(var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void shootTarget(Mob var1, Mob var2) {
            this.shootTarget((SwampShooterMob)var1, var2);
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
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.swampShooter, 10, var3, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 56;
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      boolean var13 = false;
      int var14;
      float var15;
      if (this.attackDir != null) {
         var15 = 0.4F;
         if (Math.abs(this.attackDir.x) - Math.abs(this.attackDir.y) <= var15) {
            var14 = this.attackDir.y < 0.0F ? 0 : 2;
            if (this.attackDir.x < 0.0F) {
               var13 = true;
            }
         } else {
            var14 = this.attackDir.x < 0.0F ? 3 : 1;
         }
      } else if (this.dir != 0 && this.dir != 1) {
         var14 = 3;
      } else {
         var14 = 1;
      }

      var15 = this.getAttackAnimProgress();
      int var16;
      if (this.isAttacking) {
         var16 = 1 + Math.min((int)(var15 * 4.0F), 3);
      } else {
         var16 = 0;
      }

      final TextureDrawOptionsEnd var17 = MobRegistry.Textures.swampShooter.initDraw().sprite(var16, var14, 64).mirror(var13, false).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var17.draw();
         }
      });
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
         Screen.playSound(GameResources.flick, SoundEffect.effect(this).pitch(1.2F));
      }

   }

   static {
      lootTable = new LootTable(new LootItemInterface[]{randomMapDrop, ChanceLootItem.between(0.5F, "swampsludge", 1, 1)});
      damage = new GameDamage(45.0F);
   }
}
