package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class StinkFlaskItem extends ConsumableItem {
   public StinkFlaskItem() {
      super(1, false);
      this.attackAnimTime.setBaseValue(300);
      this.rarity = Item.Rarity.RARE;
      this.itemCooldownTime.setBaseValue(2000);
      this.worldDrawSize = 32;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotationInv(var5);
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return null;
   }

   public boolean shouldSendToOtherClients(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, String var6, PacketReader var7) {
      return var6 == null;
   }

   public void onOtherPlayerPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      Screen.playSound(GameResources.drink, SoundEffect.effect(var4));
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      if (var1.isServer()) {
         var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.Potions.STINKFLASK, var4, 180.0F, (Attacker)null), true);
      } else if (var1.isClient()) {
         Screen.playSound(GameResources.drink, SoundEffect.effect(var4));
      }

      return var5;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      super.showAttack(var1, var2, var3, var4, var5, var6, var7, var8);

      for(int var9 = 0; var9 < 20; ++var9) {
         var1.entityManager.addParticle(var4.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var4.y + 2.0F + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(var4.dx / 2.0F, var4.dy / 2.0F).color(new Color(91, 130, 36)).heightMoves(36.0F, 4.0F).lifeTime(750);
      }

   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "stinkflasktip"));
      var4.add(Localization.translate("itemtooltip", "infiniteuse"));
      return var4;
   }
}
