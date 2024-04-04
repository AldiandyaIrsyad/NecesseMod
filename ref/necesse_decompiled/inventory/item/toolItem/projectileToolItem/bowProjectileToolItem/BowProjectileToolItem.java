package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import java.awt.geom.Point2D;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.level.maps.Level;

public class BowProjectileToolItem extends ProjectileToolItem {
   public int moveDist = 25;

   public BowProjectileToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "weapons", "rangedweapons"});
      this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "rangedweapons"});
      this.keyWords.add("bow");
      this.damageType = DamageTypeRegistry.RANGED;
      this.knockback.setBaseValue(25);
      this.attackRange.setBaseValue(500);
      this.enchantCost.setUpgradedValue(1.0F, 2100);
   }

   public void draw(InventoryItem var1, PlayerMob var2, int var3, int var4, boolean var5) {
      super.draw(var1, var2, var3, var4, var5);
      if (var5) {
         int var6 = this.getAvailableAmmo(var2);
         if (var6 > 999) {
            var6 = 999;
         }

         String var7 = String.valueOf(var6);
         int var8 = FontManager.bit.getWidthCeil(var7, tipFontOptions);
         FontManager.bit.drawString((float)(var3 + 28 - var8), (float)(var4 + 16), var7, tipFontOptions);
      }

   }

   public int getAvailableAmmo(PlayerMob var1) {
      return var1 == null ? 0 : var1.getInv().main.getAmount(var1.getLevel(), var1, Item.Type.ARROW, "arrowammo");
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      this.addExtraBowTooltips(var4, var1, var2, var3);
      this.addAmmoTooltips(var4, var1);
      int var5 = this.getAvailableAmmo(var2);
      var4.add(Localization.translate("itemtooltip", "ammotip", "value", (Object)var5));
      return var4;
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addAttackSpeedTip(var1, var2, var3, var4);
      this.addResilienceGainTip(var1, var2, var3, var4, var5);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
   }

   protected void addExtraBowTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
   }

   protected void addAmmoTooltips(ListGameTooltips var1, InventoryItem var2) {
      var1.add(Localization.translate("itemtooltip", "bowtip"));
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return this.getAvailableAmmo(var4) > 0 ? null : "";
   }

   protected float getAmmoConsumeChance(PlayerMob var1, InventoryItem var2) {
      return var1 == null ? 1.0F : (Float)var1.buffManager.getModifier(BuffModifiers.ARROW_USAGE);
   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);
      Item var7 = var5.getInv().main.getFirstItem(var2, var5, Item.Type.ARROW, "arrowammo");
      if (var7 != null) {
         var1.putNextShortUnsigned(var7.getID());
      } else {
         var1.putNextShortUnsigned(65535);
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = var10.getNextShortUnsigned();
      if (var11 != 65535) {
         Item var12 = ItemRegistry.getItem(var11);
         if (var12 != null && var12.type == Item.Type.ARROW) {
            GameRandom var13 = new GameRandom((long)(var9 + 5));
            float var14 = ((ArrowItem)var12).getAmmoConsumeChance() * this.getAmmoConsumeChance(var4, var6);
            boolean var15 = var14 >= 1.0F || var14 > 0.0F && var13.getChance(var14);
            if (!var15 || var4.getInv().main.removeItems(var1, var4, var12, 1, "arrowammo") >= 1) {
               this.fireProjectiles(var1, var2, var3, var4, var6, var9, (ArrowItem)var12, var15, var10);
            }
         } else {
            GameLog.warn.println(var4.getDisplayName() + " tried to use item " + (var12 == null ? var11 : var12.getStringID()) + " as arrow.");
         }
      }

      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      Item var7 = ItemRegistry.getItem("stonearrow");
      if (var7 != null && var7.type == Item.Type.ARROW) {
         var2.attackItem(var3.getX(), var3.getY(), var6);
         this.fireSettlerProjectiles(var1, var2, var3, var6, var5, (ArrowItem)var7, false);
      } else {
         GameLog.warn.println(var3.getDisplayName() + " tried to use item " + (var7 == null ? "stonearrow" : var7.getStringID()) + " as arrow.");
      }

      return var6;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.bow, SoundEffect.effect(var4));
      }

   }

   public Projectile getProjectile(Level var1, int var2, int var3, Mob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, PacketReader var9) {
      float var10 = var7.modVelocity((float)this.getProjectileVelocity(var5, var4));
      int var11 = var7.modRange(this.getAttackRange(var5));
      GameDamage var12 = var7.modDamage(this.getAttackDamage(var5));
      int var13 = var7.modKnockback(this.getKnockback(var5, var4));
      float var14 = this.getResilienceGain(var5);
      return this.getProjectile(var1, var2, var3, var4, var5, var6, var7, var8, var10, var11, var12, var13, var14, var9);
   }

   public Projectile getProjectile(Level var1, int var2, int var3, Mob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, float var9, int var10, GameDamage var11, int var12, float var13, PacketReader var14) {
      return var7.getProjectile(var4.x, var4.y, (float)var2, (float)var3, var9, var10, var11, var12, var4);
   }

   protected void fireProjectiles(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, PacketReader var9) {
      Projectile var10 = this.getProjectile(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      var10.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var5)));
      var10.dropItem = var8;
      var10.getUniqueID(new GameRandom((long)var6));
      var1.entityManager.projectiles.addHidden(var10);
      if (this.moveDist != 0) {
         var10.moveDist((double)this.moveDist);
      }

      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var10), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

   }

   protected void fireSettlerProjectiles(Level var1, HumanMob var2, Mob var3, InventoryItem var4, int var5, ArrowItem var6, boolean var7) {
      float var8 = (float)this.getProjectileVelocity(var4, var2) * var6.speedMod;
      Point2D.Float var9 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, var8, -10.0F);
      int var10 = (int)var9.x;
      int var11 = (int)var9.y;
      Projectile var12 = this.getProjectile(var1, var10, var11, var2, var4, var5, var6, var7, new PacketReader(new Packet()));
      var12.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var4)));
      var12.dropItem = var7;
      var12.getUniqueID(new GameRandom((long)var5));
      var1.entityManager.projectiles.addHidden(var12);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var12), (Level)var1);
      }

   }
}
