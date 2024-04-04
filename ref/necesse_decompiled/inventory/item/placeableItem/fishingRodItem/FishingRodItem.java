package necesse.inventory.item.placeableItem.fishingRodItem;

import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class FishingRodItem extends PlaceableItem {
   public final int poleWidth;
   public final int poleHeight;
   public final int fishingPower;
   public final int hookSpeed;
   public final int lineLength;
   public final int reelWindow;
   public final int precision;
   public final int lineCount;
   public GameTexture particlesTexture;
   public GameTexture attackTexture;

   public FishingRodItem(int var1, Item.Rarity var2) {
      this(var1, 40, 34, var2);
   }

   public FishingRodItem(int var1, int var2, int var3, Item.Rarity var4) {
      this(var1, var2, var3, 90, 200, 1, 30, 45, var4);
   }

   public FishingRodItem(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Item.Rarity var9) {
      super(1, false);
      this.setItemCategory(new String[]{"equipment", "tools", "fishingrods"});
      this.keyWords.add("fishingrod");
      this.fishingPower = var1;
      this.poleWidth = var2;
      this.poleHeight = var3;
      this.attackAnimTime.setBaseValue(300);
      this.hookSpeed = var4;
      this.lineLength = var5;
      this.lineCount = var6;
      this.reelWindow = Math.max(10, var7);
      this.precision = Math.max(2, var8);
      this.attackXOffset = 6;
      this.attackYOffset = 6;
      this.rarity = var9;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public void loadTextures() {
      super.loadTextures();
      this.attackTexture = GameTexture.fromFile("player/weapons/" + this.getStringID());
      this.particlesTexture = GameTexture.fromFile("particles/" + this.getStringID());
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      return new Point((int)(var4.x + var2 * (float)this.lineLength), (int)(var4.y + var3 * (float)this.lineLength));
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      return new GameSprite(this.attackTexture);
   }

   public GameSprite getHookProjectileSprite() {
      return new GameSprite(this.particlesTexture, 2, 0, 32);
   }

   public GameSprite getHookShadowSprite() {
      return new GameSprite(this.particlesTexture, 3, 0, 32);
   }

   public GameSprite getHookParticleSprite() {
      return new GameSprite(this.particlesTexture, 1, 0, 32);
   }

   public GameSprite getTrailSprite() {
      return new GameSprite(this.particlesTexture, 0, 0, 32);
   }

   public int getTipX(Mob var1) {
      return var1.getX() + (var1.dir == 1 ? this.poleWidth + 10 : -this.poleWidth - 10);
   }

   public int getTipY(Mob var1) {
      return var1.getY();
   }

   public int getTipHeight(Mob var1) {
      int var2 = this.poleHeight + 15;
      Level var3 = var1.getLevel();
      if (var3 != null) {
         Mob var4 = null;
         if (var1.isRiding()) {
            var4 = var1.getMount();
         }

         var2 -= var1.getBobbing();
         var2 -= var3.getTile(var1.getX() / 32, var1.getY() / 32).getMobSinkingAmount(var1);
         if (var4 != null) {
            Point var5 = var4.getSpriteOffset(var4.getAnimSprite());
            var2 -= var5.y;
         }
      }

      return var2;
   }

   public Point getTipPos(Mob var1) {
      return new Point(this.getTipX(var1), this.getTipY(var1));
   }

   public void draw(InventoryItem var1, PlayerMob var2, int var3, int var4, boolean var5) {
      super.draw(var1, var2, var3, var4, var5);
      if (var5) {
         int var6 = this.getAvailableBait(var2);
         if (var6 > 999) {
            var6 = 999;
         }

         String var7 = String.valueOf(var6);
         int var8 = FontManager.bit.getWidthCeil(var7, tipFontOptions);
         FontManager.bit.drawString((float)(var3 + 28 - var8), (float)(var4 + 16), var7, tipFontOptions);
      }

   }

   protected int getAvailableBait(PlayerMob var1) {
      return var1 == null ? 0 : var1.getInv().main.getAmount(var1.getLevel(), var1, Item.Type.BAIT, "fishingbait");
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return null;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         BaitItem var7 = (BaitItem)var4.getInv().main.removeItem(var1, var4, Item.Type.BAIT, "fishingbait");
         FishingEvent var8 = new FishingEvent(var4, var2, var3, this, var7);
         var1.entityManager.addLevelEvent(var8);
      }

      return var5;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "fishingrodtip"));
      var4.add(Localization.translate("itemtooltip", "fishingpower", "value", this.fishingPower + "%"));
      if (this.lineCount != 1) {
         var4.add(Localization.translate("itemtooltip", "fishinglines", "value", this.lineCount > 0 ? "+" + (this.lineCount - 1) : this.lineCount));
      }

      int var5 = this.getAvailableBait(var2);
      var4.add(Localization.translate("itemtooltip", "baitamounttip", "value", (Object)var5));
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5, 110.0F, -20.0F);
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }

   public float getSinkingRate(ItemPickupEntity var1, float var2) {
      return super.getSinkingRate(var1, var2) / 5.0F;
   }
}
