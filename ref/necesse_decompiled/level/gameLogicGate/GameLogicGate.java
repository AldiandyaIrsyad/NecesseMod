package necesse.level.gameLogicGate;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.SharedGameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.LogicGateItem;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;

public class GameLogicGate {
   public static SharedGameTexture logicGateTextures = new SharedGameTexture("logicGatesShared");
   public static GameTexture generatedLogicGateTexture;
   public final IDData idData = new IDData();
   private GameMessage displayName = new StaticMessage("Unknown");
   protected GameTextureSection texture;

   public static void generateLogicGateTextures() {
      generatedLogicGateTexture = logicGateTextures.generate();
      logicGateTextures.close();
   }

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public GameLogicGate() {
      if (LogicGateRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct GameLogicGate objects when logic gate registry is closed, since they are a static registered objects. Use LogicGateRegistry.getLogicGate(...) to get logic gates.");
      }
   }

   public void onLogicGateRegistryClosed() {
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("logicgate", this.getStringID());
   }

   public void updateLocalDisplayName() {
      this.displayName = this.getNewLocalization();
   }

   public final GameMessage getLocalization() {
      return this.displayName;
   }

   public final String getDisplayName() {
      return this.displayName.translate();
   }

   public void loadTextures() {
      this.texture = logicGateTextures.addTexture(GameTexture.fromFile("logic/" + this.getStringID()));
   }

   public GameTexture generateItemTexture() {
      return GameTexture.fromFile("logic/" + this.getStringID());
   }

   public Item generateNewItem() {
      return new LogicGateItem(this);
   }

   public LogicGateEntity getNewEntity(Level var1, int var2, int var3) {
      return null;
   }

   public ListGameTooltips getItemTooltips() {
      ListGameTooltips var1 = new ListGameTooltips();
      var1.add(Localization.translate("itemtooltip", "placetip"));
      return var1;
   }

   public void playPlaceSound(int var1, int var2) {
      Screen.playSound(GameResources.tap, SoundEffect.effect((float)(var1 * 32 + 16), (float)(var2 * 32 + 16)));
   }

   public void placeGate(Level var1, int var2, int var3) {
      var1.logicLayer.setLogicGate(var2, var3, this.getID(), (PacketReader)null);
   }

   public String canPlace(Level var1, int var2, int var3) {
      return var1.logicLayer.hasGate(var2, var3) ? "occupied" : null;
   }

   public void attemptPlace(Level var1, int var2, int var3, PlayerMob var4, String var5) {
   }

   public void removeGate(Level var1, int var2, int var3) {
      var1.logicLayer.clearLogicGate(var2, var3);
      if (var1.isServer()) {
         InventoryItem var4 = new InventoryItem(this.getStringID());
         var1.entityManager.pickups.add(var4.getPickupEntity(var1, (float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
      }

   }

   public void onMouseHover(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      LogicGateEntity var6 = var1.logicLayer.getEntity(var2, var3);
      if (var6 != null && Settings.showLogicGateTooltips) {
         Screen.addTooltip(var6.getTooltips(var4, var5), TooltipLocation.INTERACT_FOCUS);
      }

   }

   public void addDrawables(SharedTextureDrawOptions var1, Level var2, int var3, int var4, LogicGateEntity var5, TickManager var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var3);
      int var9 = var7.getTileDrawY(var4);
      var1.add(this.texture).pos(var8, var9);
   }

   public void drawPreview(Level var1, int var2, int var3, float var4, PlayerMob var5, GameCamera var6) {
      int var7 = var6.getTileDrawX(var2);
      int var8 = var6.getTileDrawY(var3);
      this.texture.initDraw().alpha(var4).draw(var7, var8);
   }
}
