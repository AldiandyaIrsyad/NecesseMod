package necesse.gfx.forms.presets.containerComponent;

import java.awt.Dimension;
import java.awt.Rectangle;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.world.WorldGenerator;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.FormTravelContainerGrid;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.travel.IslandData;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.level.maps.biomes.Biome;

public class TravelContainerComponent<T extends TravelContainer> extends ContainerFormSwitcher<T> {
   private FocusData focus;
   private Form travelForm;
   private Form focusForm;
   private Dimension hudSize;
   private FormTravelContainerGrid grid;
   private FormLocalLabel focusLabel;
   private FormLabel focusTip;
   private String lastSavedNotes;
   private FormContentBox notesContent;
   private FormTextBox notes;
   private FormLocalTextButton travelButton;

   public static TypeParser[] getNoteParsers(FontOptions var0) {
      return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(var0.getSize()), TypeParsers.InputIcon(var0)};
   }

   public TravelContainerComponent(Client var1, T var2) {
      super(var1, var2);
      this.updateTravelForm();
      this.focusForm = (Form)this.addComponent(new Form("focusForm", 280, 280));
      this.focusLabel = (FormLocalLabel)this.focusForm.addComponent(new FormLocalLabel((GameMessage)null, new FontOptions(20), 0, this.focusForm.getWidth() / 2, 5));
      this.focusTip = (FormLabel)this.focusForm.addComponent(new FormLabel("", new FontOptions(12), 0, this.focusForm.getWidth() / 2, 25));
      this.focusForm.addComponent(new FormLocalLabel("ui", "travelnotes", new FontOptions(16), -1, 5, 40));
      this.notesContent = (FormContentBox)this.focusForm.addComponent(new FormContentBox(4, 60, this.focusForm.getWidth() - 8, this.focusForm.getHeight() - 80 - 56 - 8, GameBackground.textBox));
      FontOptions var3 = new FontOptions(16);
      this.notes = (FormTextBox)this.notesContent.addComponent(new FormTextBox(var3, FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, this.notesContent.getMinContentWidth(), 10, -1) {
         public void changedTyping(boolean var1) {
            super.changedTyping(var1);
            if (!var1) {
               TravelContainerComponent.this.saveIslandNotes();
            }

         }
      });
      this.notes.setParsers(getNoteParsers(var3));
      this.notes.allowItemAppend = true;
      this.notes.setEmptyTextSpace(new Rectangle(this.notesContent.getX(), this.notesContent.getY(), this.notesContent.getWidth(), this.notesContent.getHeight()));
      this.notes.onChange((var1x) -> {
         Rectangle var2 = this.notesContent.getContentBoxToFitComponents();
         this.notesContent.setContentBox(var2);
         this.notesContent.scrollToFit(this.notes.getCaretBoundingBox());
      });
      this.notes.onCaretMove((var1x) -> {
         if (!var1x.causedByMouse) {
            this.notesContent.scrollToFit(this.notes.getCaretBoundingBox());
         }

      });
      this.notes.onInputEvent((var0) -> {
         if (var0.event.getID() == 256) {
            ((FormTypingComponent)var0.from).setTyping(false);
            var0.event.use();
            var0.preventDefault();
         }

      });
      this.travelButton = (FormLocalTextButton)this.focusForm.addComponent(new FormLocalTextButton("ui", "travelconfirm", 4, this.focusForm.getHeight() - 80, this.focusForm.getWidth() - 8));
      this.travelButton.onClicked((var1x) -> {
         this.saveIslandNotes();
         this.travelTo(this.focus.destination);
      });
      ((FormLocalTextButton)this.focusForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.focusForm.getHeight() - 40, this.focusForm.getWidth() - 8))).onClicked((var1x) -> {
         this.saveIslandNotes();
         this.focus = null;
         this.makeCurrent(this.travelForm);
         this.onWindowResized();
      });
      this.makeCurrent(this.travelForm);
      this.onWindowResized();
   }

   private void updateTravelForm() {
      Dimension var1 = this.hudSize;
      this.hudSize = new Dimension(Screen.getHudWidth(), Screen.getHudHeight());
      if (!this.hudSize.equals(var1)) {
         int var2 = Math.min(this.hudSize.width, this.hudSize.height);
         Dimension var3;
         if (var2 > 900) {
            var3 = new Dimension(480, 600);
         } else if (var2 > 720) {
            var3 = new Dimension(400, 520);
         } else {
            var3 = new Dimension(320, 440);
         }

         if (this.travelForm == null || this.travelForm.getWidth() != var3.width || this.travelForm.getHeight() != var3.height) {
            boolean var4 = false;
            if (this.travelForm != null) {
               var4 = this.isCurrent(this.travelForm);
               this.removeComponent(this.travelForm);
            }

            this.travelForm = (Form)this.addComponent(new Form("travelForm", var3.width, var3.height));
            this.travelForm.addComponent(new FormLocalLabel("ui", ((TravelContainer)this.container).travelDir == TravelDir.None ? "travelworldmap" : "travelselect", new FontOptions(20), 0, this.travelForm.getWidth() / 2, 5));
            this.travelForm.addComponent(new FormLocalLabel("ui", "travelcoords", new FontOptions(16), -1, 5, 30));
            FormDropdownSelectionButton var5 = (FormDropdownSelectionButton)this.travelForm.addComponent(new FormDropdownSelectionButton(4, 50, FormInputSize.SIZE_24, ButtonColor.BASE, this.travelForm.getWidth() - 8, Settings.mapCoordinates.displayName));
            FormTravelContainerGrid.CoordinateSetting[] var6 = FormTravelContainerGrid.CoordinateSetting.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               FormTravelContainerGrid.CoordinateSetting var9 = var6[var8];
               var5.options.add(var9, var9.displayName);
            }

            var5.onSelected((var0) -> {
               Settings.mapCoordinates = (FormTravelContainerGrid.CoordinateSetting)var0.value;
               Settings.saveClientSettings();
            });
            this.grid = (FormTravelContainerGrid)this.travelForm.addComponent(new FormTravelContainerGrid(this.grid, 0, 80, this.travelForm.getWidth(), this.travelForm.getHeight() - 120, this.client, (TravelContainer)this.container, this));
            ((FormLocalTextButton)this.travelForm.addComponent(new FormLocalTextButton("ui", "travelcancel", 4, this.travelForm.getHeight() - 40, this.travelForm.getWidth() - 8))).onClicked((var1x) -> {
               this.client.closeContainer(true);
            });
            if (((TravelContainer)this.container).travelDir != TravelDir.None) {
               this.travelForm.addComponent(new FormContentIconButton(this.travelForm.getWidth() - 5 - 20, 5, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new GameMessage[]{new LocalMessage("ui", "travelhelp")}));
            }

            if (var4) {
               this.makeCurrent(this.travelForm);
            }

            this.prioritizeControllerFocus(new ControllerFocusHandler[]{this.grid});
         }
      }

   }

   private void saveIslandNotes() {
      String var1 = this.notes.getText();
      if (!var1.equals(this.lastSavedNotes)) {
         this.lastSavedNotes = var1;
         IslandData var2 = this.focus.destination;
         this.client.islandNotes.set(var2.islandX, var2.islandY, var1);
         this.grid.reloadNotes(var2.islandX, var2.islandY);
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.updateTravelForm();
      this.travelForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.focusForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void focusTravel(IslandData var1, Biome var2) {
      if (this.focus == null) {
         this.focus = new FocusData(var1, var2);
         if (((TravelContainer)this.container).travelDir == TravelDir.None) {
            this.travelButton.setActive(false);
            this.travelButton.setLocalTooltip("ui", "travelcantmap");
         } else if (!var1.canTravel) {
            this.travelButton.setActive(false);
            this.travelButton.setLocalTooltip("ui", "travelcantrange");
         } else if (this.client.getLevel().isIslandPosition() && var1.islandX == this.client.getLevel().getIslandX() && var1.islandY == this.client.getLevel().getIslandY()) {
            this.travelButton.setActive(false);
            this.travelButton.setLocalTooltip("ui", "travelcantcurrent");
         } else {
            this.travelButton.setActive(true);
            this.travelButton.setLocalTooltip((GameMessage)null);
         }

         this.focusLabel.setLocalization(this.focus.biome.getLocalization());
         if (GlobalData.debugActive()) {
            this.focusTip.setText("(" + var1.islandX + ", " + var1.islandY + ")");
         } else if (this.client.getLevel().isIslandPosition()) {
            this.focusTip.setText("(" + (var1.islandX - this.client.getLevel().getIslandX()) + ", " + (var1.islandY - this.client.getLevel().getIslandY()) + ")");
         } else {
            this.focusTip.setText("");
         }

         if (GlobalData.debugCheatActive()) {
            this.focusTip.addLine((GameMessage)(new StaticMessage("(" + WorldGenerator.getIslandSize(var1.islandX, var1.islandY) + ")")));
         }

         String var3 = this.client.islandNotes.get(var1.islandX, var1.islandY);
         if (var3 != null) {
            this.notes.setText(var3);
         } else {
            this.notes.setText("");
         }

         this.lastSavedNotes = this.notes.getText();
         Rectangle var4 = this.notesContent.getContentBoxToFitComponents();
         this.notesContent.setContentBox(var4);
         this.makeCurrent(this.focusForm);
         this.onWindowResized();
      }
   }

   public void travelTo(IslandData var1) {
      ((TravelContainer)this.container).travelToDestination.runAndSend(var1.islandX, var1.islandY);
   }

   public boolean shouldOpenInventory() {
      return false;
   }

   public boolean shouldShowToolbar() {
      return false;
   }

   private static class FocusData {
      public final IslandData destination;
      public final Biome biome;

      public FocusData(IslandData var1, Biome var2) {
         this.destination = var1;
         this.biome = var2;
      }
   }
}
