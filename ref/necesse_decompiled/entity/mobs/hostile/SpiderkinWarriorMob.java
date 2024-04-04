package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderkinWarriorMob extends SpiderkinMob {
   public static GameDamage damage = new GameDamage(65.0F);

   public SpiderkinWarriorMob() {
      super(450, 35, 40);
      this.attackAnimTime = 300;
      this.attackCooldown = 500;
      this.texture = MobRegistry.Textures.spiderkinWarrior;
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerChaserWandererAI<Mob>((Supplier)null, 384, 64, 40000, false, false) {
         public boolean attackTarget(Mob var1, Mob var2) {
            if (SpiderkinWarriorMob.this.canAttack()) {
               var1.attack(var2.getX(), var2.getY(), false);
               var2.isServerHit(SpiderkinWarriorMob.damage, var1.dx, var1.dy, 75.0F, var1);
               return true;
            } else {
               return false;
            }
         }
      });
   }

   public boolean canAttack() {
      return super.canAttack();
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
      var15.hatTexture(new HumanDrawOptions.HumanDrawOptionsGetter() {
         public DrawOptions getDrawOptions(PlayerMob var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, boolean var11, GameLight var12, float var13, GameTexture var14) {
            return MobRegistry.Textures.spiderkinWarrior_light.initDraw().sprite(var3, var4, var5).light(var12.minLevelCopy(150.0F)).alpha(var13).size(var8, var9).mirror(var10, var11).addShaderTextureFit(var14, 1).pos(var6, var7);
         }
      }, ArmorItem.HairDrawMode.NO_HAIR);
      if (this.isAttacking) {
         var15.itemAttack(new InventoryItem("goldencausticexecutioner"), (PlayerMob)null, var13, this.attackDir.x, this.attackDir.y);
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
         Screen.playSound(GameResources.swing2, SoundEffect.effect(this));
      }

   }
}
