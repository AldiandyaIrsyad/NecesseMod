package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Point;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.WebWeaverWebEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class WebWeaverToolItem extends MagicProjectileToolItem {
   public WebWeaverToolItem() {
      super(1900);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(1000);
      this.attackDamage.setBaseValue(37.0F).setUpgradedValue(1.0F, 40.0F);
      this.attackXOffset = 30;
      this.attackYOffset = 30;
      this.attackRange.setBaseValue(5000);
      this.manaCost.setBaseValue(15.0F);
      this.resilienceGain.setBaseValue(2.0F);
      this.settlerProjectileCanHitWidth = 5.0F;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "webweavertip"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      short var6 = 500;
      return new Point((int)(var4.x + var2 * (float)var6), (int)(var4.y + var3 * (float)var6));
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(var4).volume(0.3F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.6F)));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      GameRandom var11 = new GameRandom((long)var9);
      Point var12 = this.controlledRangePosition(var11, var4, var2, var3, var6, 0, 40);
      float var13 = this.getAttackSpeedModifier(var6, var4);
      WebWeaverWebEvent var14 = new WebWeaverWebEvent(var4, var12.x, var12.y, var11, this.getAttackDamage(var6), this.getResilienceGain(var6), (long)(1000.0F * (1.0F / var13)));
      var1.entityManager.addLevelEventHidden(var14);
      if (var1.isServer()) {
         var1.getServer().network.sendToAllClientsExcept(new PacketLevelEvent(var14), var4.getServerClient());
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      var2.attackItem((int)var3.x, (int)var3.y, var6);
      WebWeaverWebEvent var7 = new WebWeaverWebEvent(var2, (int)var3.x, (int)var3.y, GameRandom.globalRandom, this.getAttackDamage(var6), this.getResilienceGain(var6), 1000L);
      var2.getLevel().entityManager.addLevelEvent(var7);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketLevelEvent(var7), (Level)var1);
      }

      return var6;
   }

   public int getSettlerAttackRange(HumanMob var1, InventoryItem var2) {
      return this.getAttackRange(var2) / 4;
   }
}
