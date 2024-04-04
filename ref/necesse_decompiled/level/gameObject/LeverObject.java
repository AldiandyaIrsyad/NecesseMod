package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LeverObject extends SwitchObject {
   protected String textureName;
   public GameTexture texture;

   private LeverObject(String var1, int var2, boolean var3) {
      super(new Rectangle(0, 0), var2, var3);
      this.textureName = var1;
      this.setItemCategory(new String[]{"wiring"});
      this.displayMapTooltip = true;
      this.showsWire = true;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.drawDamage = false;
      this.replaceCategories.add("lever");
      this.canReplaceCategories.add("lever");
      this.canReplaceCategories.add("pressureplate");
      this.replaceRotations = false;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return !this.isSwitched ? super.getLootTable(var1, var2, var3) : ObjectRegistry.getObject(this.counterID).getLootTable(var1, var2, var3);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(this.isSwitched ? 1 : 0, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - this.texture.getHeight() + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 12;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(this.isSwitched ? 1 : 0, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
   }

   public boolean isWireActive(Level var1, int var2, int var3, int var4) {
      return this.isSwitched;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var4.isServerClient()) {
         var4.getServerClient().newStats.levers_flicked.increment(1);
      }

      super.interact(var1, var2, var3, var4);
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add(Localization.translate("itemtooltip", "activewiretip"));
      var3.addAll(super.getItemTooltips(var1, var2));
      return var3;
   }

   public void playSwitchSound(Level var1, int var2, int var3) {
      Screen.playSound(GameResources.tick, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)).pitch(0.8F));
   }

   private static LeverObject getInactiveLever(String var0) {
      return new LeverObject(var0, -1, false);
   }

   private static LeverObject getActiveLever(String var0) {
      return new LeverObject(var0, -1, true);
   }

   public static int[] registerLeverPair(String var0, String var1, float var2) {
      LeverObject var3 = getInactiveLever(var1);
      LeverObject var4 = getActiveLever(var1);
      int var5 = ObjectRegistry.registerObject(var0, var3, var2, true);
      int var6 = ObjectRegistry.registerObject(var0 + "active", var4, 0.0F, false);
      var3.counterID = var6;
      var4.counterID = var5;
      return new int[]{var5, var6};
   }
}
