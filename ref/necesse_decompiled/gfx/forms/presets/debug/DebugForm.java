package necesse.gfx.forms.presets.debug;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.MainGameCamera;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.gameFont.FontOptions;

public class DebugForm extends FormSwitcher {
   private boolean hidden;
   public Form mainMenu;
   public DebugItemForm items;
   public DebugMobsForm mobs;
   public DebugPlayerForm player;
   public DebugBuffsForm buffs;
   public DebugWorldForm world;
   public DebugTilesForm tiles;
   public DebugObjectsForm objects;
   public DebugWireForm wire;
   public DebugTimeForm time;
   public DebugShadersForm shaders;
   public DebugSceneForm scene;
   public DebugToolsList tools;
   public final Client client;
   public final MainGame mainGame;

   public DebugForm(String var1, Client var2, MainGame var3) {
      this.client = var2;
      this.mainGame = var3;
      this.mainMenu = (Form)this.addComponent(new Form(var1 + "MainMenu", 160, 400));
      this.mainMenu.addComponent(new FormLabel("Debug menu", new FontOptions(20), 0, this.mainMenu.getWidth() / 2, 10));
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("Items", 0, 40, this.mainMenu.getWidth()))).onClicked((var1x) -> {
         this.items.itemList.populateIfNotAlready();
         this.makeCurrent(this.items);
      });
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("Mobs", 0, 80, this.mainMenu.getWidth()))).onClicked((var1x) -> {
         this.mobs.mobList.populateIfNotAlready();
         this.makeCurrent(this.mobs);
      });
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("Player", 0, 120, this.mainMenu.getWidth()))).onClicked((var1x) -> {
         this.makeCurrent(this.player);
      });
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("Buffs", 0, 160, this.mainMenu.getWidth()))).onClicked((var1x) -> {
         this.buffs.buffList.populateIfNotAlready();
         this.makeCurrent(this.buffs);
      });
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("World", 0, 200, this.mainMenu.getWidth()))).onClicked((var1x) -> {
         this.makeCurrent(this.world);
      });
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("Shaders", 0, 240, this.mainMenu.getWidth()))).onClicked((var1x) -> {
         this.makeCurrent(this.shaders);
      });
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("Scene", 0, 280, this.mainMenu.getWidth()))).onClicked((var1x) -> {
         this.makeCurrent(this.scene);
      });
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("Show server", 0, 320, this.mainMenu.getWidth()))).onClicked((var0) -> {
         Settings.serverPerspective = !Settings.serverPerspective;
         ((FormTextButton)var0.from).setText(Settings.serverPerspective ? "Show client" : "Show server");
      });
      ((FormTextButton)this.mainMenu.addComponent(new FormTextButton("Dev tools", 0, 360, this.mainMenu.getWidth()))).onClicked((var1x) -> {
         ((FormButton)var1x.from).getManager().openFloatMenu(this.tools.getFloatMenu(var1x.from));
      });
      this.items = (DebugItemForm)this.addComponent(new DebugItemForm(var1 + "Items", this), (var0, var1x) -> {
         var0.itemFilter.setTyping(var1x);
      });
      this.mobs = (DebugMobsForm)this.addComponent(new DebugMobsForm(var1 + "Mobs", this), (var0, var1x) -> {
         var0.mobFilter.setTyping(var1x);
      });
      this.player = (DebugPlayerForm)this.addComponent(new DebugPlayerForm(var1 + "Player", this), (var0, var1x) -> {
         if (var1x) {
            var0.refreshPlayer();
         }

      });
      this.buffs = (DebugBuffsForm)this.addComponent(new DebugBuffsForm(var1 + "Buffs", this), (var0, var1x) -> {
         var0.buffFilter.setTyping(var1x);
      });
      this.world = (DebugWorldForm)this.addComponent(new DebugWorldForm(var1 + "World", this));
      this.tiles = (DebugTilesForm)this.addComponent(new DebugTilesForm(var1 + "Tiles", this), (var0, var1x) -> {
         var0.tileFilter.setTyping(var1x);
      });
      this.objects = (DebugObjectsForm)this.addComponent(new DebugObjectsForm(var1 + "Objects", this), (var0, var1x) -> {
         var0.objectFilter.setTyping(var1x);
      });
      this.wire = (DebugWireForm)this.addComponent(new DebugWireForm(var1 + "Wire", this));
      this.time = (DebugTimeForm)this.addComponent(new DebugTimeForm(var1 + "Time", this));
      this.shaders = (DebugShadersForm)this.addComponent(new DebugShadersForm(var1 + "Shaders", this));
      this.scene = (DebugSceneForm)this.addComponent(new DebugSceneForm(var1 + "Scene", this));
      this.tools = new DebugToolsList(this);
      this.onWindowResized();
      this.makeCurrent(this.mainMenu);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      super.handleInputEvent(var1, var2, var3);
      if (!this.mainGame.formManager.isMouseOver()) {
         if (!Screen.isKeyDown(340) || !var1.state) {
            return;
         }

         MainGameCamera var4 = this.mainGame.getCamera();
         int var5 = var4.getMouseLevelPosX();
         int var6 = var4.getMouseLevelPosY();
         if (var1.getID() == -100) {
            if (!this.mainGame.showMap()) {
               PlayerMob var7 = this.client.getPlayer();
               var7.setPos((float)var5, (float)var6, true);
               this.client.sendMovementPacket(true);
            }

            var1.use();
         }
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.mainMenu.setPosition(Screen.getHudWidth() - this.mainMenu.getWidth() - 10, 230);
      this.items.setPosition(Screen.getHudWidth() - this.items.getWidth() - 10, 230);
      this.mobs.setPosition(Screen.getHudWidth() - this.mobs.getWidth() - 10, 230);
      this.player.setPosition(Screen.getHudWidth() - this.player.getWidth() - 10, 230);
      this.buffs.setPosition(Screen.getHudWidth() - this.buffs.getWidth() - 10, 230);
      this.world.setPosition(Screen.getHudWidth() - this.world.getWidth() - 10, 230);
      this.tiles.setPosition(Screen.getHudWidth() - this.tiles.getWidth() - 10, 230);
      this.objects.setPosition(Screen.getHudWidth() - this.objects.getWidth() - 10, 230);
      this.wire.setPosition(Screen.getHudWidth() - this.wire.getWidth() - 10, 230);
      this.time.setPosition(Screen.getHudWidth() - this.time.getWidth() - 10, 230);
      this.shaders.setPosition(Screen.getHudWidth() - this.shaders.getWidth() - 10, 230);
      this.scene.setPosition(Screen.getHudWidth() - this.scene.getWidth() - 10, 230);
   }

   public boolean shouldDraw() {
      return super.shouldDraw() && !this.hidden;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean var1) {
      this.hidden = var1;
   }
}
