package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.gfx.GameHair;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalIntScroll;
import necesse.gfx.forms.components.FormHorizontalScroll;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormPlayerIcon;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.ColorSelectorFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.mob.StylistContainer;

public class StylistContainerForm<T extends StylistContainer> extends ShopContainerForm<T> {
   public Form styleForm;
   public FormPlayerIcon icon;
   public FormHorizontalIntScroll rotate;
   public FormHorizontalIntScroll hair;
   public FormHorizontalIntScroll hairColor;
   public FormSlider shirtRed;
   public FormSlider shirtGreen;
   public FormSlider shirtBlue;
   public FormSlider shoesRed;
   public FormSlider shoesGreen;
   public FormSlider shoesBlue;
   public FormLabel costText;
   public FormLocalTextButton styleButton;
   public FormLocalLabel costLabel;
   public FormItemPreview preview;

   public StylistContainerForm(Client var1, T var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
      this.styleForm = (Form)this.addComponent(new Form("style", 408, 10));
      short var6 = 144;
      int var7 = this.styleForm.getWidth() - var6 - 16;
      FormFlow var8 = new FormFlow(5);
      this.styleForm.addComponent(new FormLocalLabel("ui", "stylistchange", new FontOptions(20), 0, this.styleForm.getWidth() / 2, var8.next(25)));
      this.icon = (FormPlayerIcon)this.styleForm.addComponent(new FormPlayerIcon(8, var8.next(), 128, 128, var2.newPlayer));
      var8.next(5);
      ((FormLocalTextButton)this.styleForm.addComponent(new FormLocalTextButton("ui", "stylistreset", var6, var8.next(25), var7, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var3x) -> {
         var2.newPlayer.look = new HumanLook(var1.getPlayer().look);
         this.updateComponents();
         this.updateCanStyle();
      });
      this.rotate = (FormHorizontalIntScroll)this.styleForm.addComponent(new FormHorizontalIntScroll(var6, var8.next(20), var7, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "rotate"), 0, 0, 3));
      this.rotate.onChanged((var1x) -> {
         this.icon.setRotation((Integer)this.rotate.getValue());
         this.updateCanStyle();
      });
      this.hair = (FormHorizontalIntScroll)this.styleForm.addComponent(new FormHorizontalIntScroll(var6, var8.next(20), var7, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "hair"), 1, 0, GameHair.getTotalHair() - 1));
      this.hair.onChanged((var2x) -> {
         var2.newPlayer.look.setHair((Integer)this.hair.getValue());
         this.updateCanStyle();
      });
      this.hairColor = (FormHorizontalIntScroll)this.styleForm.addComponent(new FormHorizontalIntScroll(var6, var8.next(20), var7, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "haircolor"), 0, 0, GameHair.getTotalHairColors() - 1));
      this.hairColor.onChanged((var2x) -> {
         var2.newPlayer.look.setHairColor((Integer)this.hairColor.getValue());
         this.updateCanStyle();
      });
      int var9 = Math.max(var8.next(), this.icon.getY() + this.icon.getHeight() + 8);
      ((FormLocalTextButton)this.styleForm.addComponent(new FormLocalTextButton("ui", "stylistrandom", this.styleForm.getWidth() / 2 - 125, var9, 250, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var1x) -> {
         this.randomize();
         this.updateComponents();
         this.updateCanStyle();
      });
      int var10 = var9 + 25;
      FontOptions var11 = new FontOptions(12);
      int var12 = this.styleForm.getWidth() / 2 - 20;
      FormFlow var13 = new FormFlow(var10);
      this.styleForm.addComponent(new FormLocalLabel("ui", "stylistshirtcolor", new FontOptions(12), -1, 8, var13.next(15)));
      ((FormLocalTextButton)this.styleForm.addComponent(new FormLocalTextButton("ui", "selectcolor", 8, var13.next(25), var12, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var2x) -> {
         final Color var3 = new Color(var2.newPlayer.look.getShirtColor().getRGB());
         ((FormButton)var2x.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(var2x.from, var3) {
            public void onApplied(Color var1x) {
               if (var1x != null) {
                  var1x = HumanLook.limitClothesColor(var1x);
                  var1.newPlayer.look.setShirtColor(var1x);
                  var1.newPlayer.getInv().giveLookArmor();
                  StylistContainerForm.this.updateCanStyle();
                  StylistContainerForm.this.updateComponents();
               } else {
                  var1.newPlayer.look.setShirtColor(var3);
                  var1.newPlayer.getInv().giveLookArmor();
               }

            }

            public void onSelected(Color var1x) {
               var1x = HumanLook.limitClothesColor(var1x);
               var1.newPlayer.look.setShirtColor(var1x);
               var1.newPlayer.getInv().giveLookArmor();
            }
         });
      });
      this.shirtRed = (FormSlider)this.styleForm.addComponent((FormLocalSlider)var13.nextY(new FormLocalSlider("ui", "colorred", 8, 10, var2.newPlayer.look.getShirtColor().getRed(), 50, 200, var12, var11), 5));
      this.shirtRed.onChanged((var2x) -> {
         var2.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
         var2.newPlayer.getInv().giveLookArmor();
         this.updateCanStyle();
      });
      this.shirtGreen = (FormSlider)this.styleForm.addComponent((FormLocalSlider)var13.nextY(new FormLocalSlider("ui", "colorgreen", 8, 10, var2.newPlayer.look.getShirtColor().getGreen(), 50, 200, var12, var11), 5));
      this.shirtGreen.onChanged((var2x) -> {
         var2.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
         var2.newPlayer.getInv().giveLookArmor();
         this.updateCanStyle();
      });
      this.shirtBlue = (FormSlider)this.styleForm.addComponent((FormLocalSlider)var13.nextY(new FormLocalSlider("ui", "colorblue", 8, 10, var2.newPlayer.look.getShirtColor().getBlue(), 50, 200, var12, var11), 5));
      this.shirtBlue.onChanged((var2x) -> {
         var2.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
         var2.newPlayer.getInv().giveLookArmor();
         this.updateCanStyle();
      });
      FormFlow var14 = new FormFlow(var10);
      this.styleForm.addComponent(new FormLocalLabel("ui", "stylistshoescolor", new FontOptions(12), -1, 170, var14.next(15)));
      ((FormLocalTextButton)this.styleForm.addComponent(new FormLocalTextButton("ui", "selectcolor", this.styleForm.getWidth() - var12 - 8, var14.next(25), var12, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var2x) -> {
         final Color var3 = var2.newPlayer.look.getShoesColor();
         ((FormButton)var2x.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(var2x.from, var3) {
            public void onApplied(Color var1x) {
               if (var1x != null) {
                  var1x = HumanLook.limitClothesColor(var1x);
                  var1.newPlayer.look.setShoesColor(var1x);
                  var1.newPlayer.getInv().giveLookArmor();
                  StylistContainerForm.this.updateCanStyle();
                  StylistContainerForm.this.updateComponents();
               } else {
                  var1.newPlayer.look.setShoesColor(var3);
                  var1.newPlayer.getInv().giveLookArmor();
               }

            }

            public void onSelected(Color var1x) {
               var1x = HumanLook.limitClothesColor(var1x);
               var1.newPlayer.look.setShoesColor(var1x);
               var1.newPlayer.getInv().giveLookArmor();
            }
         });
      });
      this.shoesRed = (FormSlider)this.styleForm.addComponent((FormLocalSlider)var14.nextY(new FormLocalSlider("ui", "colorred", this.styleForm.getWidth() - var12 - 8, 200, var2.newPlayer.look.getShoesColor().getRed(), 50, 200, var12, var11), 5));
      this.shoesRed.onChanged((var2x) -> {
         var2.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
         var2.newPlayer.getInv().giveLookArmor();
         this.updateCanStyle();
      });
      this.shoesGreen = (FormSlider)this.styleForm.addComponent((FormLocalSlider)var14.nextY(new FormLocalSlider("ui", "colorgreen", this.styleForm.getWidth() - var12 - 8, 225, var2.newPlayer.look.getShoesColor().getGreen(), 50, 200, var12, var11), 5));
      this.shoesGreen.onChanged((var2x) -> {
         var2.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
         var2.newPlayer.getInv().giveLookArmor();
         this.updateCanStyle();
      });
      this.shoesBlue = (FormSlider)this.styleForm.addComponent((FormLocalSlider)var14.nextY(new FormLocalSlider("ui", "colorblue", this.styleForm.getWidth() - var12 - 8, 250, var2.newPlayer.look.getShoesColor().getBlue(), 50, 200, var12, var11), 5));
      this.shoesBlue.onChanged((var2x) -> {
         var2.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
         var2.newPlayer.getInv().giveLookArmor();
         this.updateCanStyle();
      });
      FormFlow var15 = new FormFlow(Math.max(var13.next(), var14.next()));
      int var16 = var15.next(25);
      this.costLabel = (FormLocalLabel)this.styleForm.addComponent(new FormLocalLabel("ui", "stylistcost", new FontOptions(16), -1, 40, var16));
      this.preview = (FormItemPreview)this.styleForm.addComponent(new FormItemPreview(this.costLabel.getX() + this.costLabel.getBoundingBox().width, var16 - 12, "coin"));
      this.costText = (FormLabel)this.styleForm.addComponent(new FormLabel("x " + var2.getStyleCost(), new FontOptions(16), -1, this.preview.getX() + 30, var16));
      int var17 = var15.next(40);
      ((FormLocalTextButton)this.styleForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.styleForm.getWidth() / 2 + 2, var17, this.styleForm.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.makeCurrent(this.dialogueForm);
      });
      this.styleButton = (FormLocalTextButton)this.styleForm.addComponent(new FormLocalTextButton("ui", "stylistbuy", 4, var17, this.styleForm.getWidth() / 2 - 6));
      this.styleButton.onClicked((var1x) -> {
         if (var2.canStyle()) {
            Packet var2x = new Packet();
            var2.newPlayer.look.setupContentPacket(new PacketWriter(var2x), true);
            var2.styleButton.runAndSend(var2x);
         }

      });
      this.styleForm.setHeight(var15.next());
      this.updateComponents();
      this.updateCanStyle();
   }

   public StylistContainerForm(Client var1, T var2) {
      this(var1, var2, 408, 170, 240);
   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            StylistContainerForm.this.preview.setX(StylistContainerForm.this.costLabel.getX() + StylistContainerForm.this.costLabel.getBoundingBox().width);
            StylistContainerForm.this.costText.setX(StylistContainerForm.this.preview.getX() + 30);
         }

         public boolean isDisposed() {
            return StylistContainerForm.this.isDisposed();
         }
      });
   }

   protected void setupExtraDialogueOptions() {
      super.setupExtraDialogueOptions();
      if (((StylistContainer)this.container).humanShop instanceof StylistHumanMob && ((StylistContainer)this.container).items != null) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "stylistwantchange"), () -> {
            this.makeCurrent(this.styleForm);
         });
      }

   }

   private void randomize() {
      ((StylistContainer)this.container).newPlayer.look.randomizeLook(false, false);
   }

   public void updateCanStyle() {
      this.costText.setText("x " + ((StylistContainer)this.container).getStyleCost());
      this.styleButton.setActive(((StylistContainer)this.container).canStyle());
   }

   public void updateComponents() {
      this.hair.setValue(((StylistContainer)this.container).newPlayer.look.getHair() % GameHair.getTotalHair());
      this.hairColor.setValue(((StylistContainer)this.container).newPlayer.look.getHairColor() % GameHair.getTotalHairColors());
      this.shirtRed.setValue(((StylistContainer)this.container).newPlayer.look.getShirtColor().getRed());
      this.shirtGreen.setValue(((StylistContainer)this.container).newPlayer.look.getShirtColor().getGreen());
      this.shirtBlue.setValue(((StylistContainer)this.container).newPlayer.look.getShirtColor().getBlue());
      this.shoesRed.setValue(((StylistContainer)this.container).newPlayer.look.getShoesColor().getRed());
      this.shoesGreen.setValue(((StylistContainer)this.container).newPlayer.look.getShoesColor().getGreen());
      this.shoesBlue.setValue(((StylistContainer)this.container).newPlayer.look.getShoesColor().getBlue());
      this.icon.setPlayer(((StylistContainer)this.container).newPlayer);
      ((StylistContainer)this.container).newPlayer.getInv().giveLookArmor();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.isCurrent(this.styleForm)) {
         this.updateCanStyle();
      }

      super.draw(var1, var2, var3);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.styleForm);
   }
}
