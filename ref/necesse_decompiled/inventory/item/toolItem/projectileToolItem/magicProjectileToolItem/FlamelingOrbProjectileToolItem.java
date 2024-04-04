package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class FlamelingOrbProjectileToolItem extends MagicProjectileToolItem {
   public FlamelingOrbProjectileToolItem() {
      super(0);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(0);
      this.attackDamage.setBaseValue(0.0F).setUpgradedValue(1.0F, 0.0F);
      this.velocity.setBaseValue(0);
      this.attackXOffset = 20;
      this.attackYOffset = 20;
      this.attackRange.setBaseValue(0);
      this.knockback.setBaseValue(0);
      this.manaCost.setBaseValue(0.0F).setUpgradedValue(1.0F, 0.0F);
      this.settlerProjectileCanHitWidth = 0.0F;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt2, SoundEffect.effect(var4).volume(0.4F).pitch(GameRandom.globalRandom.getFloatBetween(0.8F, 0.9F)));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      return var6;
   }
}
