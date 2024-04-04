package necesse.inventory.item.toolItem.summonToolItem;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReapersCallSummonToolItem extends SummonToolItem {
   public ReapersCallSummonToolItem() {
      super("playerreaperspirit", (FollowPosition)null, 1.0F, 1500);
      this.summonType = "summonedmobtemp";
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(40.0F).setUpgradedValue(1.0F, 50.0F);
      this.knockback.setBaseValue(0);
      this.attackXOffset = 4;
      this.attackYOffset = 20;
      this.drawMaxSummons = false;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "reaperscalltip1"));
      var4.add(Localization.translate("itemtooltip", "reaperscalltip2"));
      var4.add(Localization.translate("itemtooltip", "secondarysummon"));
      return var4;
   }

   public int getMaxSummons(InventoryItem var1, PlayerMob var2) {
      return 5;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(var4).volume(0.6F).pitch(GameRandom.globalRandom.getFloatBetween(1.0F, 1.1F)));
      }

   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4);
   }

   public DrawOptions getAttackDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, float var5, float var6, float var7, GameSprite var8, InventoryItem var9, int var10, int var11, GameLight var12) {
      DrawOptions var13 = super.getAttackDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      long var14 = var3.getWorldEntity().getTime() / 2L;
      TextureDrawOptionsEnd var16 = MobRegistry.Textures.reaperSpiritPortal.initDraw().sprite(0, 0, 32).light(var12).rotate((float)(-var14), 16, 16).pos(var10 + 16 + (int)(var5 * 40.0F), var11 + 22 + (int)(var6 * 40.0F));
      return () -> {
         var13.draw();
         var16.draw();
      };
   }

   public Point2D.Float findSpawnLocation(AttackingFollowingMob var1, Level var2, int var3, int var4, int var5, PlayerMob var6, InventoryItem var7) {
      Point2D.Float var8 = GameMath.normalize((float)var3 - var6.x, (float)var4 - var6.y + (float)var5);
      return new Point2D.Float(var6.x + 4.0F + var8.x * 40.0F + (float)var6.getCurrentAttackDrawXOffset(), var6.y + 4.0F + var8.y * 40.0F + (float)var6.getCurrentAttackDrawYOffset());
   }

   protected void beforeSpawn(AttackingFollowingMob var1, InventoryItem var2, PlayerMob var3) {
      super.beforeSpawn(var1, var2, var3);
      int var4 = GameRandom.globalRandom.nextInt(360);
      float var5 = (float)GameRandom.globalRandom.getIntBetween(50, 60);
      var1.dx = (float)Math.cos(Math.toRadians((double)var4)) * var5;
      var1.dy = (float)Math.sin(Math.toRadians((double)var4)) * var5;
   }
}
