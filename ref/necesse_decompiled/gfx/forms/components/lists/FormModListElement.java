package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModNextListData;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;

public abstract class FormModListElement extends Form {
   public final LoadedMod mod;
   public final ModListData listData;
   public final boolean[] dependsMet;
   public final boolean[] optionalDependsMet;
   private int dependError;
   private int dependWarning;
   private int currentIndex;
   public FormIconButton moveUpButton;
   public FormIconButton moveDownButton;
   public FormCheckBox enabledCheckbox;

   public FormModListElement(final ModNextListData var1, final int var2) {
      super(var2, 24);
      this.mod = var1.mod;
      this.listData = new ModListData(this.mod);
      this.listData.enabled = var1.enabled;
      this.dependsMet = new boolean[this.mod.depends.length];
      this.optionalDependsMet = new boolean[this.mod.optionalDepends.length];
      this.dependError = this.dependsMet.length;
      this.dependWarning = this.optionalDependsMet.length;
      this.drawBase = false;
      this.shouldLimitDrawArea = false;
      this.moveUpButton = (FormIconButton)this.addComponent(new FormIconButton(4, 0, Settings.UI.button_moveup, 16, 13, new GameMessage[]{new LocalMessage("ui", "moveupbutton")}));
      this.moveUpButton.onClicked((var1x) -> {
         this.onMovedUp();
      });
      this.moveUpButton.setActive(var1.enabled);
      this.moveDownButton = (FormIconButton)this.addComponent(new FormIconButton(4, 11, Settings.UI.button_movedown, 16, 13, new GameMessage[]{new LocalMessage("ui", "movedownbutton")}));
      this.moveDownButton.onClicked((var1x) -> {
         this.onMovedDown();
      });
      this.moveDownButton.setActive(var1.enabled);
      this.enabledCheckbox = (FormCheckBox)this.addComponent(new FormCheckBox("", 24, 6) {
         public GameTooltips getTooltip() {
            return this.checked ? new StringTooltips(Localization.translate("ui", "moddisable")) : new StringTooltips(Localization.translate("ui", "modenable"));
         }
      });
      this.enabledCheckbox.checked = var1.enabled;
      this.enabledCheckbox.onClicked((var1x) -> {
         this.listData.enabled = ((FormCheckBox)var1x.from).checked;
         this.onEnabledChanged(((FormCheckBox)var1x.from).checked);
      });
      this.enabledCheckbox.setupDragToOtherCheckboxes("toggleModEnabled", true);
      this.addComponent(new FormCustomDraw(44, 0, 20, 20) {
         public void draw(TickManager var1x, PlayerMob var2, Rectangle var3) {
            HoverStateTextures var4 = (HoverStateTextures)var1.mod.loadLocation.iconSupplier.get();
            GameTexture var5 = var4.active;
            Color var6 = FormModListElement.this.listData.enabled ? Settings.UI.activeTextColor : Settings.UI.inactiveTextColor;
            if (this.isHovering() && Screen.getMouseDraggingElement() == null || FormModListElement.this.isCurrentlySelected()) {
               var6 = Settings.UI.highlightTextColor;
               var5 = var4.highlighted;
            }

            var5.initDraw().size(24).color(var6).draw(this.getX(), this.getY());
            if (this.isHovering()) {
               Screen.addTooltip(new StringTooltips(var1.mod.loadLocation.message.translate()), TooltipLocation.FORM_FOCUS);
            }

         }
      });
      FormButton var3 = (FormButton)this.addComponent(new FormButton() {
         public void draw(TickManager var1x, PlayerMob var2x, Rectangle var3) {
            Color var4 = null;
            ListGameTooltips var5 = new ListGameTooltips();
            if (FormModListElement.this.dependWarning > 0) {
               var4 = Settings.UI.warningTextColor;
            }

            if (!"0.24.2".equals(var1.mod.gameVersion)) {
               var4 = Settings.UI.warningTextColor;
               var5.add((Object)(new StringTooltips(Localization.translate("ui", "modwronggameversion"), new Color(200, 150, 50), 300)));
            }

            if (FormModListElement.this.dependError > 0) {
               var4 = Settings.UI.errorTextColor;
               var5.add((Object)(new StringTooltips(Localization.translate("ui", "modmissingdep"), GameColor.RED, 300)));
            }

            if (var1.mod.initError || var1.mod.runError) {
               var4 = Settings.UI.errorTextColor;
               var5.add((Object)(new StringTooltips(Localization.translate("ui", "moderrorwarning"), GameColor.RED, 300)));
            }

            int var6 = 70;
            if (var4 != null) {
               TextureDrawOptionsEnd var7 = Settings.UI.warning_icon.initDraw().size(24).color(var4).alpha(FormModListElement.this.listData.enabled ? 1.0F : 0.5F);
               var7.draw(var6, 0);
               var6 += var7.getWidth() + 2;
            }

            Color var8 = FormModListElement.this.listData.enabled ? Settings.UI.activeTextColor : Settings.UI.inactiveTextColor;
            if (this.isHovering() && Screen.getMouseDraggingElement() == null || FormModListElement.this.isCurrentlySelected()) {
               var8 = Settings.UI.highlightTextColor;
            }

            FontManager.bit.drawString((float)var6, 4.0F, var1.mod.name, (new FontOptions(16)).color(var8));
            if (this.isHovering() && !var5.isEmpty()) {
               Screen.addTooltip(var5, TooltipLocation.FORM_FOCUS);
            }

         }

         public List<Rectangle> getHitboxes() {
            return Collections.singletonList(new Rectangle(70, 0, var2 - 70, FormModListElement.this.getHeight()));
         }
      }, 100);
      var3.onClicked((var1x) -> {
         this.onSelected();
      });
      var3.onDragStarted((var1x) -> {
         this.onStartDragged();
      });
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isCurrentlySelected() && !var1.isUsed() && !var1.state) {
         if (var1.getID() == 265) {
            this.onMovedUp();
            this.playTickSound();
            var1.use();
         } else if (var1.getID() == 264) {
            this.onMovedDown();
            this.playTickSound();
            var1.use();
         }
      }

      super.handleInputEvent(var1, var2, var3);
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isControllerFocus()) {
         if (var1.getState() == ControllerInput.MENU_SELECT) {
            if (var1.buttonState) {
               this.enabledCheckbox.checked = !this.enabledCheckbox.checked;
               this.listData.enabled = this.enabledCheckbox.checked;
               this.onEnabledChanged(this.enabledCheckbox.checked);
               this.playTickSound();
            }

            var1.use();
         } else if (var1.getState() != ControllerInput.MENU_PREV && !var1.isRepeatEvent((Object)this.moveUpButton)) {
            if (var1.getState() == ControllerInput.MENU_NEXT || var1.isRepeatEvent((Object)this.moveDownButton)) {
               if (var1.buttonState && this.listData.enabled && this.moveDownButton.isActive()) {
                  var1.startRepeatEvents(this.moveDownButton);
                  this.onMovedDown();
                  this.playTickSound();
                  ControllerInput.submitNextRefreshFocusEvent();
               }

               var1.use();
            }
         } else {
            if (var1.buttonState && this.listData.enabled && this.moveUpButton.isActive()) {
               var1.startRepeatEvents(this.moveUpButton);
               this.onMovedUp();
               this.playTickSound();
               ControllerInput.submitNextRefreshFocusEvent();
            }

            var1.use();
         }
      }

      super.handleControllerEvent(var1, var2, var3);
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   public void drawControllerFocus(ControllerFocus var1) {
      super.drawControllerFocus(var1);
      Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      Screen.addControllerGlyph(Localization.translate("ui", "moveupbutton"), ControllerInput.MENU_PREV);
      Screen.addControllerGlyph(Localization.translate("ui", "movedownbutton"), ControllerInput.MENU_NEXT);
   }

   public abstract void onEnabledChanged(boolean var1);

   public abstract void onMovedUp();

   public abstract void onMovedDown();

   public abstract void onStartDragged();

   public abstract void onSelected();

   public abstract boolean isCurrentlySelected();

   public int getCurrentIndex() {
      return this.currentIndex;
   }

   public void setCurrentIndex(List<FormModListElement> var1, int var2) {
      this.currentIndex = var2;
      this.enabledCheckbox.checked = this.listData.enabled;
      this.moveUpButton.setActive(var2 > 0);
      this.moveDownButton.setActive(var2 < var1.size() - 1);
   }

   public void updateDepends(List<FormModListElement> var1) {
      Arrays.fill(this.dependsMet, false);
      Arrays.fill(this.optionalDependsMet, false);
      this.dependError = this.dependsMet.length;
      this.dependWarning = this.optionalDependsMet.length;

      for(int var2 = 0; var2 < this.currentIndex; ++var2) {
         FormModListElement var3 = (FormModListElement)var1.get(var2);
         if (var3.listData.enabled || !this.listData.enabled) {
            int var4;
            for(var4 = 0; var4 < this.mod.depends.length; ++var4) {
               if (this.mod.depends[var4].equals(var3.mod.id)) {
                  this.dependsMet[var4] = true;
                  --this.dependError;
               }
            }

            for(var4 = 0; var4 < this.mod.optionalDepends.length; ++var4) {
               if (this.mod.optionalDepends[var4].equals(var3.mod.id)) {
                  this.optionalDependsMet[var4] = true;
                  --this.dependWarning;
               }
            }
         }
      }

   }
}
