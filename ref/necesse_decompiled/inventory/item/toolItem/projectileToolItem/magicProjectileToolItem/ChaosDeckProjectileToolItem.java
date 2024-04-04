package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.util.ArrayList;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.level.maps.Level;

public class ChaosDeckProjectileToolItem extends MagicProjectileToolItem {
   public ChaosDeckProjectileToolItem() {
      super(200);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(800);
      this.attackDamage.setBaseValue(22.0F).setUpgradedValue(1.0F, 22.0F);
      this.damageType = DamageTypeRegistry.MAGIC;
      this.velocity.setBaseValue(75);
      this.attackRange.setBaseValue(500);
      this.knockback.setBaseValue(5);
      this.manaCost.setBaseValue(0.75F).setUpgradedValue(1.0F, 2.5F);
      this.attackXOffset = 20;
      this.attackYOffset = 20;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   protected float getSettlerProjectileCanHitWidth(HumanMob var1, Mob var2, InventoryItem var3) {
      return 5.0F;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt2, SoundEffect.effect(var4).volume(0.4F).pitch(GameRandom.globalRandom.getFloatBetween(0.8F, 0.9F)));
      }

   }

   public InventoryItem onAttack(Level var1, final int var2, final int var3, final PlayerMob var4, final int var5, final InventoryItem var6, final PlayerInventorySlot var7, final int var8, final int var9, PacketReader var10) {
      new GameRandom((long)var9);
      ArrayList var12 = new ArrayList();
      PlayerInventory var13 = var4.getInv().main;

      int var14;
      InventoryItem var15;
      for(var14 = 0; var14 < 10; ++var14) {
         var15 = var13.getItem(var14);
         if (var15 != null && var15.item.isToolItem()) {
            var12.add(var15);
         }
      }

      for(var14 = 0; var14 < var12.size(); ++var14) {
         var15 = (InventoryItem)var12.get(var14);
         final ToolItem var16 = (ToolItem)var15.item;
         var1.entityManager.addLevelEventHidden(new WaitForSecondsEvent((float)var14 * 0.1F) {
            public void onWaitOver() {
               Item var1 = null;
               if (var16 instanceof BowProjectileToolItem) {
                  var1 = var4.getInv().main.getFirstItem(this.level, var4, Item.Type.ARROW, "arrowammo");
                  if (var1 != null) {
                     if (var16 instanceof GreatbowProjectileToolItem) {
                        var6.getGndData().setFloat("chargePercent", 1.0F);
                     }

                     Packet var2x = new Packet();
                     var16.setupAttackContentPacket(new PacketWriter(var2x), this.level, var2, var3, var4, var6);
                     var16.onAttack(this.level, var2, var3, var4, var5, var6, var7, var8, var9, new PacketReader(var2x));
                     var6.getGndData().setFloat("chargePercent", 0.0F);
                  }
               } else if (!(var16 instanceof MagicProjectileToolItem) && var16 instanceof SwordToolItem) {
               }

            }
         });
      }

      this.consumeMana(var4, var6);
      return var6;
   }
}
