package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.HandGunBulletProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.level.maps.Level;

public class GunProjectileToolItem extends ProjectileToolItem {
   public static String[] NORMAL_AMMO_TYPES = new String[]{"simplebullet", "bouncingbullet", "voidbullet", "frostbullet"};
   public String[] ammoTypes;
   public int moveDist;
   public boolean controlledRange;
   public int controlledMinRange;
   public int controlledInaccuracy;
   public float ammoConsumeChance;

   public GunProjectileToolItem(String var1, int var2) {
      this(new String[]{var1}, var2);
   }

   public GunProjectileToolItem(String[] var1, int var2) {
      super(var2);
      this.setItemCategory(new String[]{"equipment", "weapons", "rangedweapons"});
      this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "rangedweapons"});
      this.keyWords.add("gun");
      this.damageType = DamageTypeRegistry.RANGED;
      this.knockback.setBaseValue(50);
      this.velocity.setBaseValue(400);
      this.moveDist = 20;
      this.ammoConsumeChance = 1.0F;
      this.ammoTypes = var1;
      this.enchantCost.setUpgradedValue(1.0F, 1950);
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
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 0;
         String[] var3 = this.ammoTypes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            var2 += var1.getInv().main.getAmount(var1.getLevel(), var1, ItemRegistry.getItem(var6), "bulletammo");
         }

         return var2;
      }
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      this.addExtraGunTooltips(var4, var1, var2, var3);
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

   protected void addExtraGunTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
   }

   protected void addAmmoTooltips(ListGameTooltips var1, InventoryItem var2) {
      var1.add(Localization.translate("itemtooltip", "guntip"));
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      if (this.controlledRange) {
         int var6 = this.getAttackRange(var5);
         return new Point((int)(var4.x + var2 * (float)var6), (int)(var4.y + var3 * (float)var6));
      } else {
         return super.getControllerAttackLevelPos(var1, var2, var3, var4, var5);
      }
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return this.getAvailableAmmo(var4) > 0 ? null : "";
   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);
      Item var7 = var5.getInv().main.getFirstItem(var2, var5, this.ammoItems(), "bulletammo");
      if (var7 != null) {
         var1.putNextShortUnsigned(var7.getID());
      } else {
         var1.putNextShortUnsigned(65535);
      }

   }

   protected Item[] ammoItems() {
      return (Item[])Arrays.stream(this.ammoTypes).map(ItemRegistry::getItem).toArray((var0) -> {
         return new Item[var0];
      });
   }

   protected float getAmmoConsumeChance(PlayerMob var1, InventoryItem var2) {
      float var3 = var1 == null ? 1.0F : (Float)var1.buffManager.getModifier(BuffModifiers.BULLET_USAGE);
      return this.ammoConsumeChance * var3;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = var10.getNextShortUnsigned();
      if (var11 != 65535) {
         Item var12 = ItemRegistry.getItem(var11);
         if (var12 != null && var12.type == Item.Type.BULLET) {
            GameRandom var13 = new GameRandom((long)(var9 + 5));
            float var14 = ((BulletItem)var12).getAmmoConsumeChance() * this.getAmmoConsumeChance(var4, var6);
            boolean var15 = var14 >= 1.0F || var14 > 0.0F && var13.getChance(var14);
            if (!var15 || var4.getInv().main.removeItems(var1, var4, var12, 1, "bulletammo") >= 1) {
               this.fireProjectiles(var1, var2, var3, var4, var6, var9, (BulletItem)var12, var15, var10);
            }
         } else {
            GameLog.warn.println(var4.getDisplayName() + " tried to use item " + (var12 == null ? var11 : var12.getStringID()) + " as bullet.");
         }
      }

      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      Item var7 = ItemRegistry.getItem("simplebullet");
      if (var7 != null && var7.type == Item.Type.BULLET) {
         var2.attackItem(var3.getX(), var3.getY(), var6);
         this.fireSettlerProjectiles(var1, var2, var3, var6, var5, (BulletItem)var7, false);
      } else {
         GameLog.warn.println(var3.getDisplayName() + " tried to use item " + (var7 == null ? "simplebullet" : var7.getStringID()) + " as bullet.");
      }

      return var6;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         this.playFireSound(var4);
      }

   }

   protected void fireProjectiles(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, int var6, BulletItem var7, boolean var8, PacketReader var9) {
      int var10;
      if (this.controlledRange) {
         Point var11 = this.controlledRangePosition(new GameRandom((long)(var6 + 10)), var4, var2, var3, var5, this.controlledMinRange, this.controlledInaccuracy);
         var2 = var11.x;
         var3 = var11.y;
         var10 = (int)var4.getDistance((float)var2, (float)var3);
      } else {
         var10 = this.getAttackRange(var5);
      }

      Projectile var12 = this.getProjectile(var5, var7, var4.x, var4.y, (float)var2, (float)var3, var10, var4);
      var12.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var5)));
      var12.dropItem = var8;
      var12.getUniqueID(new GameRandom((long)var6));
      var1.entityManager.projectiles.addHidden(var12);
      if (this.moveDist != 0) {
         var12.moveDist((double)this.moveDist);
      }

      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var12), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

   }

   protected void fireSettlerProjectiles(Level var1, HumanMob var2, Mob var3, InventoryItem var4, int var5, BulletItem var6, boolean var7) {
      int var9 = this.getProjectileVelocity(var4, var2);
      Point2D.Float var10 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var9, (float)(-10 - this.moveDist));
      int var11 = (int)var10.x;
      int var12 = (int)var10.y;
      int var8;
      if (this.controlledRange) {
         Point var13 = this.controlledRangePosition(new GameRandom((long)(var5 + 10)), var2, var11, var12, var4, this.controlledMinRange, this.controlledInaccuracy);
         var11 = var13.x;
         var12 = var13.y;
         var8 = (int)var2.getDistance((float)var11, (float)var12);
      } else {
         var8 = this.getAttackRange(var4);
      }

      Projectile var14 = this.getProjectile(var4, var6, var2.x, var2.y, (float)var11, (float)var12, var8, var2);
      var14.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var4)));
      var14.dropItem = var7;
      var14.getUniqueID(new GameRandom((long)var5));
      var1.entityManager.projectiles.addHidden(var14);
      if (this.moveDist != 0) {
         var14.moveDist((double)this.moveDist);
      }

      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var14), (Level)var1);
      }

   }

   public Projectile getProjectile(InventoryItem var1, BulletItem var2, float var3, float var4, float var5, float var6, int var7, Mob var8) {
      float var9 = var2.modVelocity((float)this.getProjectileVelocity(var1, var8));
      var7 = var2.modRange(var7);
      GameDamage var10 = var2.modDamage(this.getAttackDamage(var1));
      int var11 = var2.modKnockback(this.getKnockback(var1, var8));
      return var2.overrideProjectile() ? var2.getProjectile(var3, var4, var5, var6, var9, var7, var10, var11, var8) : this.getNormalProjectile(var3, var4, var5, var6, var9, var7, var10, var11, var8);
   }

   public Projectile getNormalProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      return new HandGunBulletProjectile(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void playFireSound(AttackAnimMob var1) {
   }
}
