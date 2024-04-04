package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.WebWeaverWebEvent;
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

public class SpiderkinMageMob extends SpiderkinMob {
   public static GameDamage damage = new GameDamage(55.0F);
   private int attackCooldown = 3000;

   public SpiderkinMageMob() {
      super(300, 40, 30);
      this.attackAnimTime = 1500;
      this.texture = MobRegistry.Textures.spiderkinMage;
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerChaserWandererAI<Mob>((Supplier)null, 384, 256, 40000, false, false) {
         public boolean attackTarget(Mob var1, Mob var2) {
            if (SpiderkinMageMob.this.canAttack() && !SpiderkinMageMob.this.isOnGenericCooldown("attackCooldown")) {
               var1.attack(var2.getX(), var2.getY(), false);
               WebWeaverWebEvent var3 = new WebWeaverWebEvent(var1, (int)var2.x, (int)var2.y, GameRandom.globalRandom, SpiderkinMageMob.damage, 0.0F, 1000L);
               var1.getLevel().entityManager.addLevelEvent(var3);
               SpiderkinMageMob.this.startGenericCooldown("attackCooldown", (long)SpiderkinMageMob.this.attackCooldown);
            }

            return true;
         }
      });
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
            return MobRegistry.Textures.spiderkinMage_light.initDraw().sprite(var3, var4, var5).light(var12.minLevelCopy(150.0F)).alpha(var13).size(var8, var9).mirror(var10, var11).addShaderTextureFit(var14, 1).pos(var6, var7);
         }
      }, ArmorItem.HairDrawMode.NO_HAIR);
      if (this.isAttacking) {
         var15.itemAttack(new InventoryItem("goldenwebweaver"), (PlayerMob)null, var13, this.attackDir.x, this.attackDir.y);
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
         Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(this).volume(0.3F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.6F)));
      }

   }
}
