package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderkinArcherMob extends SpiderkinMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new ChanceLootItemList(0.75F, new LootItemInterface[]{new LootItem("spideritearrow", 10)})});
   public static GameDamage damage = new GameDamage(60.0F);
   protected int shotsRemaining;

   public SpiderkinArcherMob() {
      super(350, 40, 30);
      this.texture = MobRegistry.Textures.spiderkinArcher;
      this.attackAnimTime = 250;
      this.attackCooldown = 1000;
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerChaserWandererAI<Mob>((Supplier)null, 384, 384, 40000, false, false) {
         public boolean attackTarget(Mob var1, Mob var2) {
            if (SpiderkinArcherMob.this.canAttack()) {
               if (SpiderkinArcherMob.this.shotsRemaining <= 0) {
                  SpiderkinArcherMob.this.shotsRemaining = 3;
               }

               --SpiderkinArcherMob.this.shotsRemaining;
               var1.attack(var2.getX(), var2.getY(), false);
               Projectile var3 = ProjectileRegistry.getProjectile("spideritearrow", var1.getLevel(), var1.x, var1.y, var2.x, var2.y, 120.0F, 480, SpiderkinArcherMob.damage, var1);
               var3.moveDist(10.0);
               var1.getLevel().entityManager.projectiles.add(var3);
               return true;
            } else {
               return false;
            }
         }
      });
   }

   public boolean canAttack() {
      if ((Boolean)this.buffManager.getModifier(BuffModifiers.INTIMIDATED)) {
         return false;
      } else {
         return super.canAttack() || this.shotsRemaining > 0 && this.getTimeSinceLastAttack() >= 150L;
      }
   }

   protected void doWasHitLogic(MobWasHitEvent var1) {
      super.doWasHitLogic(var1);
      if (!var1.wasPrevented) {
         this.startAttackCooldown();
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 7;
      float var13 = this.getAttackAnimProgress();
      Point var14 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      HumanDrawOptions var15 = (new HumanDrawOptions(var4, this.texture)).sprite(var14).dir(this.dir).light(var10);
      var15.hatTexture((var0, var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8x, var9x, var10x, var11x, var12x, var13x) -> {
         return MobRegistry.Textures.spiderkinArcher_light.initDraw().sprite(var2x, var3x, var4x).light(var11x.minLevelCopy(150.0F)).alpha(var12x).size(var7x, var8x).mirror(var9x, var10x).addShaderTextureFit(var13x, 1).pos(var5x, var6x);
      }, ArmorItem.HairDrawMode.NO_HAIR);
      if (this.isAttacking) {
         var15.itemAttack(new InventoryItem("goldenarachnidwebbow"), (PlayerMob)null, var13, this.attackDir.x, this.attackDir.y);
      }

      final DrawOptions var16 = var15.pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var16.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      if (this.isClient()) {
         Screen.playSound(GameResources.bow, SoundEffect.effect(this));
      }

   }
}
