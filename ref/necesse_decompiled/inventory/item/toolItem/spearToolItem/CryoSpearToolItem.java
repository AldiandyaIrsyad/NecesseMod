package necesse.inventory.item.toolItem.spearToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.CryoSpearShardProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CryoSpearToolItem extends SpearToolItem {
   public CryoSpearToolItem() {
      super(1600);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(45.0F).setUpgradedValue(1.0F, 60.0F);
      this.attackRange.setBaseValue(140);
      this.knockback.setBaseValue(50);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "cryospeartip"));
      return var4;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      InventoryItem var11 = super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      if (var8 == 0) {
         Point2D.Float var12 = GameMath.normalize((float)var2 - var4.x, (float)var3 - var4.y + (float)var5);
         Mob var13 = var4.getMount();
         if (var13 != null) {
            var5 -= var13.getRiderDrawYOffset();
         }

         CryoSpearShardProjectile var14 = new CryoSpearShardProjectile(var4.x + var12.x, var4.y + var12.y, var4.x + var12.x * 1000.0F, var4.y + var12.y * 1000.0F, 150.0F, 200, this.getAttackDamage(var6), this.getKnockback(var6, var4), var4, var5);
         var14.setLevel(var1);
         var14.resetUniqueID(new GameRandom((long)var9));
         var14.moveDist((double)(this.getAttackRange(var6) - 35));
         var14.traveledDistance = 0.0F;
         var1.entityManager.projectiles.addHidden(var14);
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var14), (ServerClient)var4.getServerClient(), var4.getServerClient());
         }
      }

      return var11;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      InventoryItem var7 = super.onSettlerAttack(var1, var2, var3, var4, var5, var6);
      Point2D.Float var8 = GameMath.normalize(var3.x - var2.x, var3.y - var2.y + (float)var4);
      Mob var9 = var2.getMount();
      if (var9 != null) {
         var4 -= var9.getRiderDrawYOffset();
      }

      int var10 = this.getAttackRange(var6);
      CryoSpearShardProjectile var11 = new CryoSpearShardProjectile(var2.x + var8.x * (float)(var10 - 35), var2.y + var8.y * (float)(var10 - 35), var2.x + var8.x * 1000.0F, var2.y + var8.y * 1000.0F, 150.0F, 200, this.getAttackDamage(var6), this.getKnockback(var6, var2), var2, var4);
      var11.resetUniqueID(new GameRandom((long)var5));
      var1.entityManager.projectiles.addHidden(var11);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var11), (Level)var1);
      }

      return var7;
   }
}
