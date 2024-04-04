package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.decorators.FailerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TeleportOnProjectileHitAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.NinjaStarProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class NinjaMob extends HostileMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new MobConditionLootItemList((var0) -> {
      return var0.getLevel() == null || !var0.getLevel().isIncursionLevel;
   }, new LootItemInterface[]{new ChanceLootItemList(0.25F, new LootItemInterface[]{new OneOfLootItems(new LootItem("ninjahood"), new LootItemInterface[]{new LootItem("ninjarobe"), new LootItem("ninjashoes")})}), LootItem.between("ninjastar", 15, 30)})});
   public static GameDamage baseDamage = new GameDamage(50.0F);
   public static GameDamage incursionDamage = new GameDamage(60.0F);
   public final CoordinateMobAbility teleportAbility;

   public NinjaMob() {
      super(250);
      this.attackCooldown = 500;
      this.attackAnimTime = 200;
      this.setSpeed(40.0F);
      this.setFriction(3.0F);
      this.setArmor(10);
      this.moveAccuracy = 8;
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.teleportAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            if (NinjaMob.this.isClient()) {
               NinjaMob.this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(NinjaMob.this.getLevel(), NinjaMob.this.x, NinjaMob.this.y)), Particle.GType.CRITICAL);
               NinjaMob.this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(NinjaMob.this.getLevel(), (float)var1, (float)var2)), Particle.GType.CRITICAL);
            }

            NinjaMob.this.setPos((float)var1, (float)var2, true);
         }
      });
   }

   public void init() {
      super.init();
      final GameDamage var1;
      if (this.getLevel() instanceof IncursionLevel) {
         this.setMaxHealth(300);
         this.setHealthHidden(this.getMaxHealth());
         this.setArmor(30);
         var1 = incursionDamage;
      } else {
         var1 = baseDamage;
      }

      PlayerChaserWandererAI var2 = new PlayerChaserWandererAI<NinjaMob>((Supplier)null, 640, 320, 40000, true, true) {
         public boolean attackTarget(NinjaMob var1x, Mob var2) {
            if (var1x.canAttack() && !var1x.isAccelerating() && !var1x.hasCurrentMovement()) {
               var1x.attack(var2.getX(), var2.getY(), false);
               var1x.getLevel().entityManager.projectiles.add(new NinjaStarProjectile(var1x.x, var1x.y, var2.x, var2.y, var1, var1x));
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean attackTarget(Mob var1x, Mob var2) {
            return this.attackTarget((NinjaMob)var1x, var2);
         }
      };
      var2.addChildFirst(new FailerAINode(new TeleportOnProjectileHitAINode<NinjaMob>(5000, 7) {
         public boolean teleport(NinjaMob var1, int var2, int var3) {
            if (var1.isServer()) {
               var1.teleportAbility.runAndSend(var2, var3);
               this.getBlackboard().mover.stopMoving(var1);
            }

            return true;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean teleport(Mob var1, int var2, int var3) {
            return this.teleport((NinjaMob)var1, var2, var3);
         }
      }));
      this.ai = new BehaviourTreeAI(this, var2);
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

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.ninja.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 7;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.ninja)).sprite(var13).dir(this.dir).light(var10);
      float var15 = this.getAttackAnimProgress();
      if (this.isAttacking) {
         var14.itemAttack(new InventoryItem("ninjastar"), (PlayerMob)null, var15, this.attackDir.x, this.attackDir.y);
      }

      final DrawOptions var16 = var14.pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var16.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public int getRockSpeed() {
      return 20;
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      if (this.isClient()) {
         Screen.playSound(GameResources.swing1, SoundEffect.effect(this));
      }

   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.FRICTION, 0.0F)).min(0.75F));
   }
}
