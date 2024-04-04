package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.LinkedList;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.AlchemistContainer;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;

public class AlchemistContainerForm<T extends AlchemistContainer> extends ShopContainerForm<T> {
   public Form fireworkForm;
   public LinkedList<FormContentIconToggleButton> shapeButtons;
   public LinkedList<FormContentIconToggleButton> colorButtons;
   public LinkedList<FormContentIconToggleButton> crackleButtons;
   public FireworkPlaceableItem.FireworksShape selectedShape;
   public FireworkPlaceableItem.FireworkColor selectedColor;
   public FireworkPlaceableItem.FireworkCrackle selectedCrackle;
   public int costY;
   public FormFairTypeLabel costLabel;
   public FormLocalTextButton buyButton;
   public FormLocalTextButton backButton;

   public AlchemistContainerForm(Client var1, T var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
      this.shapeButtons = new LinkedList();
      this.colorButtons = new LinkedList();
      this.crackleButtons = new LinkedList();
      this.selectedShape = null;
      this.selectedColor = null;
      this.selectedCrackle = null;
      this.fireworkForm = (Form)this.addComponent(new Form("firework", var3, 360));
      FormFlow var6 = new FormFlow(5);
      this.fireworkForm.addComponent(new FormLocalLabel("ui", "alchemistfirework", new FontOptions(20), -1, 5, var6.next(30)));
      byte var7 = 32;
      byte var8 = 8;
      int var9 = var3 / (var7 + var8);
      this.fireworkForm.addComponent((FormLocalLabel)var6.nextY(new FormLocalLabel("ui", "fireworkshape", new FontOptions(16), -1, 5, 0, this.fireworkForm.getWidth() - 10), 4));
      int var10 = 0;
      FormContentIconToggleButton var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_random, new GameMessage[]{new LocalMessage("itemtooltip", "fireworkrandom")}));
      this.shapeButtons.add(var12);
      var12.setToggled(true);
      var12.onToggled((var1x) -> {
         this.shapeButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedShape == null) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedShape = null;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_sphere, new GameMessage[]{FireworkPlaceableItem.FireworksShape.Sphere.displayName}));
      this.shapeButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.shapeButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Sphere) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedShape = FireworkPlaceableItem.FireworksShape.Sphere;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_splash, new GameMessage[]{FireworkPlaceableItem.FireworksShape.Splash.displayName}));
      this.shapeButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.shapeButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Splash) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedShape = FireworkPlaceableItem.FireworksShape.Splash;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_disc, new GameMessage[]{FireworkPlaceableItem.FireworksShape.Disc.displayName}));
      this.shapeButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.shapeButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Disc) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedShape = FireworkPlaceableItem.FireworksShape.Disc;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_star, new GameMessage[]{FireworkPlaceableItem.FireworksShape.Star.displayName}));
      this.shapeButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.shapeButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Star) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedShape = FireworkPlaceableItem.FireworksShape.Star;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_heart, new GameMessage[]{FireworkPlaceableItem.FireworksShape.Heart.displayName}));
      this.shapeButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.shapeButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Heart) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedShape = FireworkPlaceableItem.FireworksShape.Heart;
         this.updateCost();
      });
      ++var10;
      int var11 = (int)Math.ceil((double)((float)var10 / (float)var9));
      var6.next(var11 * (var7 + var8));
      this.fireworkForm.addComponent((FormLocalLabel)var6.nextY(new FormLocalLabel("ui", "fireworkcolor", new FontOptions(16), -1, 5, 0, this.fireworkForm.getWidth() - 10), 4));
      byte var14 = 0;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var14 % var9 * (var7 + var8) + var8 / 2, var6.next() + var14 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_random, new GameMessage[]{new LocalMessage("itemtooltip", "fireworkrandom")}));
      this.colorButtons.add(var12);
      var12.setToggled(true);
      var12.onToggled((var1x) -> {
         this.colorButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedColor == null) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedColor = null;
         this.updateCost();
      });
      var10 = var14 + 1;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_confetti, new GameMessage[]{FireworkPlaceableItem.FireworkColor.Confetti.displayName}));
      this.colorButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.colorButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Confetti) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedColor = FireworkPlaceableItem.FireworkColor.Confetti;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_flame, new GameMessage[]{FireworkPlaceableItem.FireworkColor.Flame.displayName}));
      this.colorButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.colorButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Flame) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedColor = FireworkPlaceableItem.FireworkColor.Flame;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_red, new GameMessage[]{FireworkPlaceableItem.FireworkColor.Red.displayName}));
      this.colorButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.colorButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Red) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedColor = FireworkPlaceableItem.FireworkColor.Red;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_green, new GameMessage[]{FireworkPlaceableItem.FireworkColor.Green.displayName}));
      this.colorButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.colorButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Green) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedColor = FireworkPlaceableItem.FireworkColor.Green;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_blue, new GameMessage[]{FireworkPlaceableItem.FireworkColor.Blue.displayName}));
      this.colorButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.colorButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Blue) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedColor = FireworkPlaceableItem.FireworkColor.Blue;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_pink, new GameMessage[]{FireworkPlaceableItem.FireworkColor.Pink.displayName}));
      this.colorButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.colorButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Pink) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedColor = FireworkPlaceableItem.FireworkColor.Pink;
         this.updateCost();
      });
      ++var10;
      var11 = (int)Math.ceil((double)((float)var10 / (float)var9));
      var6.next(var11 * (var7 + var8));
      this.fireworkForm.addComponent((FormLocalLabel)var6.nextY(new FormLocalLabel("ui", "fireworkcrackle", new FontOptions(16), -1, 5, 0, this.fireworkForm.getWidth() - 10), 4));
      var14 = 0;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var14 % var9 * (var7 + var8) + var8 / 2, var6.next() + var14 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_random, new GameMessage[]{new LocalMessage("itemtooltip", "fireworkrandom")}));
      this.crackleButtons.add(var12);
      var12.setToggled(true);
      var12.onToggled((var1x) -> {
         this.crackleButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedCrackle == null) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedCrackle = null;
         this.updateCost();
      });
      var10 = var14 + 1;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_crackle, new GameMessage[]{FireworkPlaceableItem.FireworkCrackle.Crackle.displayName}));
      this.crackleButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.crackleButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedCrackle == FireworkPlaceableItem.FireworkCrackle.Crackle) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedCrackle = FireworkPlaceableItem.FireworkCrackle.Crackle;
         this.updateCost();
      });
      ++var10;
      var12 = (FormContentIconToggleButton)this.fireworkForm.addComponent(new FormContentIconToggleButton(var10 % var9 * (var7 + var8) + var8 / 2, var6.next() + var10 / var9 * (var7 + var8) + var8 / 2, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.firework_nocrackle, new GameMessage[]{FireworkPlaceableItem.FireworkCrackle.NoCrackle.displayName}));
      this.crackleButtons.add(var12);
      var12.onToggled((var1x) -> {
         this.crackleButtons.stream().filter((var1) -> {
            return var1 != var1x.from;
         }).forEach((var0) -> {
            var0.setToggled(false);
         });
         if (this.selectedCrackle == FireworkPlaceableItem.FireworkCrackle.NoCrackle) {
            ((FormButtonToggle)var1x.from).setToggled(true);
         }

         this.selectedCrackle = FireworkPlaceableItem.FireworkCrackle.NoCrackle;
         this.updateCost();
      });
      ++var10;
      var11 = (int)Math.ceil((double)((float)var10 / (float)var9));
      var6.next(var11 * (var7 + var8));
      this.costY = var6.next() + 5;
      this.costLabel = (FormFairTypeLabel)this.fireworkForm.addComponent(new FormFairTypeLabel("", 5, 0), -100);
      var6.next(30);
      int var13 = this.fireworkForm.getWidth() / 2;
      this.buyButton = (FormLocalTextButton)this.fireworkForm.addComponent(new FormLocalTextButton("ui", "buybutton", 4, 0, var13 - 6));
      this.buyButton.onClicked((var2x) -> {
         char var3 = 1;
         if (Control.CRAFT_ALL.isDown()) {
            var3 = '\uffff';
         }

         Packet var4 = new Packet();
         PacketWriter var5 = new PacketWriter(var4);
         this.getFireworkData().writePacket(var5);
         var5.putNextShortUnsigned(var3);
         var2.buyFireworkButton.runAndSend(var4);
         var2x.preventDefault();
      });
      this.buyButton.acceptMouseRepeatEvents = true;
      this.backButton = (FormLocalTextButton)this.fireworkForm.addComponent(new FormLocalTextButton("ui", "backbutton", var13 + 2, 0, var13 - 6));
      this.backButton.onClicked((var1x) -> {
         this.makeCurrent(this.dialogueForm);
      });
      this.fireworkForm.setHeight(var6.next());
      this.updateCost();
   }

   public AlchemistContainerForm(Client var1, T var2) {
      this(var1, var2, 408, 170, 240);
   }

   protected void setupExtraDialogueOptions() {
      super.setupExtraDialogueOptions();
      if (((AlchemistContainer)this.container).items != null) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "alchemstwantfirework"), () -> {
            this.makeCurrent(this.fireworkForm);
         });
      }

   }

   public void updateCost() {
      GNDItemMap var1 = this.getFireworkData();
      int var2 = ((AlchemistContainer)this.container).getFireworksCost(var1);
      InventoryItem var3 = new InventoryItem("fireworkrocket");
      var3.setGndData(var1);
      FormFlow var4 = new FormFlow(this.costY);
      this.costLabel.setText((GameMessage)(new LocalMessage("ui", "alchemistfireworkcost", new Object[]{"cost", var2, "firework", TypeParsers.getItemParseString(var3)})));
      FontOptions var5 = (new FontOptions(16)).color(Settings.UI.activeTextColor);
      this.costLabel.setFontOptions(var5);
      this.costLabel.setParsers(TypeParsers.GAME_COLOR, TypeParsers.InputIcon(var5), TypeParsers.ItemIcon(16));
      var4.nextY(this.costLabel, 5);
      int var6 = var4.next(40);
      this.buyButton.setY(var6);
      this.backButton.setY(var6);
      this.fireworkForm.setHeight(var4.next());
      this.updateCanBuy();
      ContainerComponent.setPosFocus(this.fireworkForm);
   }

   public void updateCanBuy() {
      this.buyButton.setActive(((AlchemistContainer)this.container).canBuyFirework(this.getFireworkData()));
   }

   public GNDItemMap getFireworkData() {
      GNDItemMap var1 = new GNDItemMap();
      (new FireworkPlaceableItem.FireworkItemCreator()).shape(this.selectedShape).color(this.selectedColor).crackle(this.selectedCrackle).applyToData(var1);
      return var1;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.isCurrent(this.fireworkForm)) {
         this.updateCanBuy();
      }

      super.draw(var1, var2, var3);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.fireworkForm);
   }
}
