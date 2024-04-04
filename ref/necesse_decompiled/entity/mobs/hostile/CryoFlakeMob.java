package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionShooterPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CryoMissileProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoFlakeMob extends FlyingHostileMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new MobConditionLootItemList((var0) -> {
      return var0.getLevel() == null || !var0.getLevel().isIncursionLevel;
   }, new LootItemInterface[]{LootItem.between("glacialshard", 1, 2)})});
   public static GameDamage baseDamage = new GameDamage(50.0F);
   public static GameDamage incursionDamage = new GameDamage(65.0F);
   public final EmptyMobAbility attackSoundAbility;

   public CryoFlakeMob() {
      super(175);
      this.setSpeed(35.0F);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.2F);
      this.setArmor(20);
      this.moveAccuracy = 10;
      this.collision = new Rectangle(-16, -16, 32, 32);
      this.hitBox = new Rectangle(-20, -20, 40, 40);
      this.selectBox = new Rectangle(-25, -25, 50, 50);
      this.attackSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (CryoFlakeMob.this.isClient()) {
               float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
               Screen.playSound(GameResources.jingle, SoundEffect.effect(CryoFlakeMob.this).volume(0.8F).pitch(var1));
            }

         }
      });
   }

   public void init() {
      super.init();
      final GameDamage var1;
      if (this.getLevel() instanceof IncursionLevel) {
         this.setMaxHealth(350);
         this.setHealthHidden(this.getMaxHealth());
         this.setArmor(30);
         var1 = incursionDamage;
      } else {
         var1 = baseDamage;
      }

      this.ai = new BehaviourTreeAI(this, new CollisionShooterPlayerChaserWandererAI<CryoFlakeMob>((Supplier)null, 448, var1, 100, CooldownAttackTargetAINode.CooldownTimer.CAN_ATTACK, 2000, 384, 40000) {
         public boolean shootAtTarget(CryoFlakeMob var1x, Mob var2) {
            if (CryoFlakeMob.this.canAttack()) {
               CryoFlakeMob.this.attackSoundAbility.runAndSend();
               CryoFlakeMob.this.startAttackCooldown();
               var1x.getLevel().entityManager.projectiles.add(new CryoMissileProjectile(var1x.getLevel(), var1x, var1x.x, var1x.y, var2.x, var2.y, 100.0F, 448, var1, 100));
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean shootAtTarget(Mob var1x, Mob var2) {
            return this.shootAtTarget((CryoFlakeMob)var1x, var2);
         }
      }, new FlyingAIMover());
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 30; ++var3) {
         this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.IMPORTANT_COSMETIC).movesConstant((float)(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1)), (float)(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1))).color(new Color(88, 105, 218));
      }

   }

   protected void playDeathSound() {
      this.playHitSound();
   }

   protected void playHitSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.jinglehit, SoundEffect.effect(this).pitch(var1));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = MobRegistry.Textures.cryoFlake.getWidth();
      int var12 = var11 / 2;
      int var13 = var8.getDrawX(var5) - var12;
      int var14 = var8.getDrawY(var6) - var12;
      long var15 = var4.getWorldEntity().getTime();
      float var17 = GameUtils.getTimeRotation(var15, 4);
      float var18 = GameUtils.getAnimFloatContinuous(var15, 1000) / 1.5F;
      TextureDrawOptionsEnd var19 = MobRegistry.Textures.cryoFlake.initDraw().sprite(0, 0, var11).rotate(var17 * (float)(this.dx < 0.0F ? -1 : 1), var12, var12).light(var10).pos(var13, var14);
      GameLight var20 = var10.copy();
      var20.setLevel((float)((int)(var18 * 150.0F)));
      TextureDrawOptionsEnd var21 = MobRegistry.Textures.cryoFlake.initDraw().sprite(0, 1, var11).rotate(var17 * (float)(this.dx < 0.0F ? -1 : 1), var12, var12).light(var20).pos(var13, var14);
      var3.add((var2x) -> {
         var19.draw();
         var21.draw();
      });
   }
}
