package necesse.inventory.item.trinketItem;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class ForceOfWindTrinketItem extends TrinketItem {
   protected GameTexture attackTexture;

   public ForceOfWindTrinketItem() {
      super(Item.Rarity.EPIC, 500);
      this.attackAnimTime.setBaseValue(200);
      this.attackXOffset = 4;
      this.attackYOffset = 4;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      if (var3.getBoolean("equipped")) {
         var4.add(Localization.translate("itemtooltip", "fowtipequipped"));
      } else {
         var4.add(Localization.translate("itemtooltip", "fowtipnotequipped"));
      }

      var4.add(Localization.translate("itemtooltip", "fowtip2"));
      return var4;
   }

   public TrinketBuff[] getBuffs(InventoryItem var1) {
      return new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff("forceofwindtrinket")};
   }

   public void loadTextures() {
      super.loadTextures();
      this.attackTexture = GameTexture.fromFile("player/weapons/" + this.getStringID());
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      return new GameSprite(this.attackTexture);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      short var11 = 150;
      Point2D.Float var12 = GameMath.normalize((float)var2 - var4.x, (float)var3 - var4.y);
      PacketForceOfWind.applyToPlayer(var1, var4, var12.x, var12.y, (float)var11);
      PacketForceOfWind.addCooldownStack(var4, 3.0F, var1.isServer());
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, var4, 0.15F, (Attacker)null), var1.isServer());
      var4.buffManager.forceUpdateBuffs();
      if (var1.isServer()) {
         ServerClient var13 = var4.getServerClient();
         var1.getServer().network.sendToClientsAtExcept(new PacketForceOfWind(var13.slot, var12.x, var12.y, (float)var11), (ServerClient)var13, var13);
      }

      return var6;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.swoosh, SoundEffect.effect(var4).volume(0.5F).pitch(1.7F));
      }

   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      String var6 = super.canAttack(var1, var2, var3, var4, var5);
      if (var6 != null) {
         return var6;
      } else {
         return !var4.isRiding() && !PacketForceOfWind.isOnCooldown(var4) ? null : "";
      }
   }
}
