package emris.mods.CPit;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="CPit", name="Charcoal Pit", version="1.0.b75")
@NetworkMod(clientSideRequired=false, serverSideRequired=false)
public class CPit {

	@Instance("CPit")
	public static CPit instance;
	
	@Init
	public void load(FMLInitializationEvent event) {
		if (event.getSide() == Side.SERVER) {
			MinecraftServer server = ModLoader.getMinecraftServerInstance();
			ICommandManager icm = server.getCommandManager();
			ServerCommandManager scm = ((ServerCommandManager) icm);
			scm.registerCommand(new CommandCPit());
		}
	}
}
