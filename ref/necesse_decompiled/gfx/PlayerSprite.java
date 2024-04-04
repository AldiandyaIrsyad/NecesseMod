package necesse.gfx;

import java.awt.Point;
import java.util.function.Consumer;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PlayerSprite {
   public PlayerSprite() {
   }

   public static DrawOptions getIconDrawOptions(int var0, int var1, int var2, int var3, PlayerMob var4, int var5, int var6) {
      return getIconDrawOptions(var0, var1, var2, var3, var4, var5, var6, 1.0F, new GameLight(150.0F));
   }

   public static DrawOptions getIconDrawOptions(int var0, int var1, int var2, int var3, PlayerMob var4, int var5, int var6, float var7, GameLight var8) {
      InventoryItem var9 = getPlayerDisplayArmor(var4, 0);
      InventoryItem var10 = getPlayerDisplayArmor(var4, 1);
      InventoryItem var11 = getPlayerDisplayArmor(var4, 2);
      HumanDrawOptions var12 = (new HumanDrawOptions(var4.getLevel(), var4.look, false)).player(var4).helmet(var9).chestplate(var10).boots(var11).size(var2, var3).alpha(var7).invis((Boolean)var4.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var5, var6).dir(var6).light(var8);
      return var12.pos(var0, var1);
   }

   private static InventoryItem getPlayerDisplayArmor(PlayerMob var0, int var1) {
      if (var0.getInv().cosmetic.isSlotClear(var1) && !var0.getInv().armor.isSlotClear(var1)) {
         if (var0.getInv().armor.getItemSlot(var1).isArmorItem()) {
            return var0.getInv().armor.getItem(var1);
         }
      } else if (!var0.getInv().cosmetic.isSlotClear(var1) && var0.getInv().cosmetic.getItemSlot(var1).isArmorItem()) {
         return var0.getInv().cosmetic.getItem(var1);
      }

      return null;
   }

   public static DrawOptions getIconAnimationDrawOptions(int var0, int var1, int var2, int var3, PlayerMob var4) {
      Point var5 = var4.getAnimSprite(var4.getX(), var4.getY(), var4.dir);
      return getIconDrawOptions(var0, var1, var2, var3, var4, var5.x, var4.dir);
   }

   public static DrawOptions getIconDrawOptions(int var0, int var1, PlayerMob var2) {
      return getIconDrawOptions(var0, var1, 32, 32, var2, 0, 2);
   }

   public static DrawOptions getDrawOptions(PlayerMob var0, int var1, int var2, GameLight var3, GameCamera var4, Consumer<HumanDrawOptions> var5) {
      Level var6 = var0.getLevel();
      if (var6 == null) {
         return () -> {
         };
      } else {
         int var7 = var4.getDrawX(var1) - 32;
         int var8 = var4.getDrawY(var2) - 51;
         InventoryItem var9 = getPlayerDisplayArmor(var0, 0);
         InventoryItem var10 = getPlayerDisplayArmor(var0, 1);
         InventoryItem var11 = getPlayerDisplayArmor(var0, 2);
         int var12 = var0.dir;
         Mob var13 = null;
         if (var0.isRiding()) {
            var13 = var0.getMount();
         }

         if (var13 != null && !var0.isAttacking) {
            var12 = var13.getRiderDir(var12);
         }

         Point var14 = var0.getAnimSprite(var1, var2, var12);
         var8 += var0.getBobbing(var1, var2);
         var8 += var6.getTile(var1 / 32, var2 / 32).getMobSinkingAmount(var0);
         GameTexture var15 = null;
         int var16 = 0;
         int var17 = 0;
         if (var0.inLiquid(var1, var2)) {
            var15 = MobRegistry.Textures.swimmask;
         }

         int var18 = var14.x;
         if (var13 != null) {
            var18 = var13.getRiderArmSpriteX();
            Point var19 = var13.getSpriteOffset(var13.getAnimSprite(var1, var2, var13.dir));
            var7 += var19.x;
            var8 += var19.y;
            var15 = var13.getRiderMask();
            var16 = var13.getRiderMaskXOffset();
            var17 = var13.getRiderMaskYOffset();
         }

         float var23 = var0.getInvincibilityFrameAlpha();
         HumanDrawOptions var20 = (new HumanDrawOptions(var6, var0.look, false)).player(var0).helmet(var9).chestplate(var10).boots(var11).dir(var12).allAlpha(var23).invis((Boolean)var0.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var14).armSprite(var18).light(var3);
         var0.buffManager.addHumanDraws(var20);
         if (var5 != null) {
            var5.accept(var20);
         } else {
            InventoryItem var21 = var0.getSelectedItem();
            if (var21 != null && var21.item.holdsItem(var21, var0)) {
               var20.holdItem(var21);
            }

            if (var15 != null) {
               var20.mask(var15, var16, var17);
            }

            float var22 = var0.getAttackAnimProgress();
            if (var0.isAttacking) {
               var20.itemAttack(var0.attackingItem, var0, var22, var0.attackDir.x, var0.attackDir.y);
            }
         }

         return var20.pos(var7, var8);
      }
   }
}
