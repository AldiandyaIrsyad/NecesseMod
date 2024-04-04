package necesse.level.maps.hudManager.floatText;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class ItemPickupText extends FloatTextFade {
   private final InventoryItem item;
   private boolean specialOutline;

   public ItemPickupText(int var1, int var2, InventoryItem var3) {
      super(var1 + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), var2 + (int)(GameRandom.globalRandom.nextGaussian() * 4.0), (new FontOptions(16)).outline().color((Color)var3.item.getRarityColor(var3).color.get()));
      this.avoidOtherText = true;
      this.hoverTime = 1000;
      this.item = var3;
      this.updateText();
   }

   public ItemPickupText(Mob var1, InventoryItem var2) {
      this(var1.getX(), var1.getY() - 16, var2);
   }

   public ItemPickupText specialOutline(boolean var1) {
      this.specialOutline = var1;
      return this;
   }

   public void updateText() {
      this.setText(this.item.getItemDisplayName() + (this.item.getAmount() != 1 ? " (" + this.item.getAmount() + ")" : ""));
   }

   public int getItemID() {
      return this.item.item.getID();
   }

   public void addThis(Level var1, ArrayList<HudDrawElement> var2) {
      for(int var3 = 0; var3 < var2.size(); ++var3) {
         HudDrawElement var4 = (HudDrawElement)var2.get(var3);
         if (!var4.isRemoved() && var4 != this && var4 instanceof ItemPickupText) {
            ItemPickupText var5 = (ItemPickupText)var4;
            if (var5.getItemID() == this.getItemID()) {
               this.item.setAmount(this.item.getAmount() + var5.item.getAmount());
               this.specialOutline = this.specialOutline || var5.specialOutline;
               this.updateText();
               var2.remove(var3);
               --var3;
            }
         }
      }

      super.addThis(var1, var2);
   }

   public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
      if (this.specialOutline) {
         Item.Rarity var4 = this.item.item.getRarity(this.item);
         if (var4 == null) {
            var4 = Item.Rarity.NORMAL;
         }

         int var5 = var4.outlineMinHue;
         int var6 = var4.outlineMaxHue;
         short var7 = 1000;
         float var8 = (float)(System.currentTimeMillis() % (long)var7) / (float)var7;
         float var9 = GameMath.sin(var8 * 180.0F);
         if (var5 > var6) {
            var5 -= 360;
         }

         float var10 = (float)Math.floorMod((int)((float)var5 + (float)(var6 - var5) * var9), 360) / 360.0F;
         Color var11 = new Color(Color.HSBtoRGB(var10, 1.0F, 0.4F));
         this.fontOptions.outline(var11);
      }

      super.addDrawables(var1, var2, var3);
   }
}
