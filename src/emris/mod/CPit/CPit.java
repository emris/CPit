package emris.mods.CPit;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid="CPit", name="Charcoal Pit", version="1.3.b75")
@NetworkMod(clientSideRequired=false, serverSideRequired=false)
public class CPit {

	@Instance("CPit")
	public static CPit instance;
	
	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandCPit());
	}
}
