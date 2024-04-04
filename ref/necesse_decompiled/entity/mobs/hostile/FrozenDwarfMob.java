package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FrozenDwarfMob extends HostileMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("icejavelin", 10, 15)});
   public static GameDamage damage = new GameDamage(24.0F);

   public FrozenDwarfMob() {
      super(100);
      this.attackCooldown = 1250;
      this.attackAnimTime = 250;
      this.setSpeed(35.0F);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -35, 28, 42);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerChaserWandererAI<Mob>((Supplier)null, 384, 256, 40000, false, false) {
         public boolean attackTarget(Mob var1, Mob var2) {
            return this.shootSimpleProjectile(var1, var2, "hostileicejavelin", FrozenDwarfMob.damage, 80, 480, 40);
         }
      });
   }

   public LootTable getLootTable() {
      return lootTable;
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

   protected void doWasHitLogic(MobWasHitEvent var1) {
      super.doWasHitLogic(var1);
      if (!var1.wasPrevented) {
         this.startAttackCooldown();
      }

   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.frozenDwarf.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
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
      float var14 = this.getAttackAnimProgress();
      HumanDrawOptions var15 = (new HumanDrawOptions(var4, MobRegistry.Textures.frozenDwarf)).sprite(var13).dir(this.dir).light(var10);
      if (this.isAttacking) {
         ItemAttackDrawOptions var16 = ItemAttackDrawOptions.start(this.dir).itemSprite(MobRegistry.Textures.frozenDwarf.body, 0, 9, 32).itemRotatePoint(4, 4).itemEnd().armSprite(MobRegistry.Textures.frozenDwarf.body, 0, 8, 32).offsets(32, 27, 12, 4, 12).armRotatePoint(8, 15).swingRotation(var14).light(var10);
         var15.attackAnim(var16, var14);
      }

      final DrawOptions var17 = var15.pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var17.draw();
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
         Screen.playSound(GameResources.swing1, SoundEffect.effect(this).volume(0.5F));
      }

   }
}
