package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.io.FileNotFoundException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.MobWasKilledEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public abstract class Buff {
   public static final FontOptions durationFontOptions;
   public final IDData idData = new IDData();
   protected boolean shouldSave;
   protected boolean isPassive;
   protected boolean overrideSync;
   protected boolean isVisible;
   protected boolean canCancel;
   protected boolean isImportant;
   protected GameTexture iconTexture;
   protected GameMessage displayName;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public Buff() {
      if (BuffRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct Buff objects when buff registry is closed, since they are a static registered objects. Use BuffRegistry.getBuff(...) to get buffs.");
      } else {
         this.displayName = new StaticMessage("Unknown");
         this.shouldSave = true;
         this.isPassive = false;
         this.overrideSync = false;
         this.isVisible = true;
         this.canCancel = true;
      }
   }

   public void onBuffRegistryClosed() {
   }

   public String getLocalizationKey() {
      return this.getStringID();
   }

   public void updateLocalDisplayName() {
      this.displayName = (GameMessage)(this.isVisible ? new LocalMessage("buff", this.getLocalizationKey()) : new StaticMessage(this.getStringID()));
   }

   public GameMessage getLocalization() {
      return this.displayName;
   }

   public String getDisplayName() {
      return this.displayName.translate();
   }

   public abstract void init(ActiveBuff var1, BuffEventSubscriber var2);

   /** @deprecated */
   @Deprecated
   public void init(ActiveBuff var1) {
   }

   public void firstAdd(ActiveBuff var1) {
   }

   public void onUpdate(ActiveBuff var1) {
   }

   public void onRemoved(ActiveBuff var1) {
   }

   public void onStacksUpdated(ActiveBuff var1) {
   }

   public void onBeforeHit(ActiveBuff var1, MobBeforeHitEvent var2) {
   }

   public void onBeforeAttacked(ActiveBuff var1, MobBeforeHitEvent var2) {
   }

   public void onBeforeHitCalculated(ActiveBuff var1, MobBeforeHitCalculatedEvent var2) {
   }

   public void onBeforeAttackedCalculated(ActiveBuff var1, MobBeforeHitCalculatedEvent var2) {
   }

   public void onWasHit(ActiveBuff var1, MobWasHitEvent var2) {
   }

   public void onHasAttacked(ActiveBuff var1, MobWasHitEvent var2) {
   }

   public void onItemAttacked(ActiveBuff var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8) {
   }

   public void onHasKilledTarget(ActiveBuff var1, MobWasKilledEvent var2) {
   }

   public boolean isPotionBuff() {
      return false;
   }

   public void serverTick(ActiveBuff var1) {
   }

   public void clientTick(ActiveBuff var1) {
   }

   public GameTexture getDrawIcon(ActiveBuff var1) {
      return this.iconTexture;
   }

   public void drawIcon(int var1, int var2, ActiveBuff var3) {
      GameTexture var4 = this.getDrawIcon(var3);
      var4.initDraw().size(32, 32).draw(var1, var2);
      String var5;
      int var6;
      if (var3.getStacks() > 1) {
         var5 = Integer.toString(var3.getStacks());
         var6 = FontManager.bit.getWidthCeil(var5, durationFontOptions);
         FontManager.bit.drawString((float)(var1 + 28 - var6), (float)(var2 + 30 - FontManager.bit.getHeightCeil(var5, durationFontOptions)), var5, durationFontOptions);
      }

      if (this.shouldDrawDuration(var3)) {
         var5 = var3.getDurationText();
         var6 = FontManager.bit.getWidthCeil(var5, durationFontOptions);
         FontManager.bit.drawString((float)(var1 + 16 - var6 / 2), (float)(var2 + 30), var5, durationFontOptions);
      }

   }

   public void loadTextures() {
      try {
         this.iconTexture = GameTexture.fromFileRaw("buffs/" + this.getStringID());
      } catch (FileNotFoundException var2) {
         this.iconTexture = GameTexture.fromFile("buffs/unknown");
      }

   }

   public boolean isVisible(ActiveBuff var1) {
      return this.isVisible;
   }

   public boolean isImportant(ActiveBuff var1) {
      return this.isImportant;
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      return new ListGameTooltips(this.getDisplayName());
   }

   public boolean canCancel(ActiveBuff var1) {
      return this.isVisible(var1) && this.canCancel;
   }

   public boolean shouldDrawDuration(ActiveBuff var1) {
      return !this.isPassive();
   }

   public boolean isPassive() {
      return this.isPassive;
   }

   public int getStackSize() {
      return 1;
   }

   public boolean overridesStackDuration() {
      return false;
   }

   public boolean showsFirstStackDurationText() {
      return false;
   }

   public boolean shouldNetworkSync() {
      return !this.isPassive || this.overrideSync;
   }

   public boolean shouldSave() {
      return this.shouldSave;
   }

   public Attacker getSource(Attacker var1) {
      return var1;
   }

   static {
      durationFontOptions = (new FontOptions(12)).color(Color.WHITE).outline();
   }
}
