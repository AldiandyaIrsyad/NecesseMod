package necesse.engine.commands.clientCommands;

import necesse.engine.GlobalData;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.gfx.camera.MainGameCamera;
import necesse.gfx.camera.MainGamePanningCamera;

public class PanningCameraClientCommand extends ModularChatCommand {
   public PanningCameraClientCommand() {
      super("camerapan", "Start panning camera mode", PermissionLevel.ADMIN, false, new CmdParameter("xDir", new IntParameterHandler(), false, new CmdParameter[0]), new CmdParameter("yDir", new IntParameterHandler(), false, new CmdParameter[0]), new CmdParameter("speed", new IntParameterHandler(100), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      int var7 = (Integer)var4[0];
      int var8 = (Integer)var4[1];
      int var9 = (Integer)var4[2];
      MainGame var10 = GlobalData.getCurrentState() instanceof MainGame ? (MainGame)GlobalData.getCurrentState() : null;
      if (var10 != null) {
         MainGameCamera var11 = var10.getCamera();
         MainGamePanningCamera var12 = new MainGamePanningCamera(var11.getX(), var11.getY());
         var12.setDirection((float)var7, (float)var8);
         var12.setSpeed((float)var9);
         var10.setCamera(var12);
      }

   }
}
