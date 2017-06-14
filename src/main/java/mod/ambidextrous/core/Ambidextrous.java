package mod.ambidextrous.core;

import mod.ambidextrous.network.NetworkRouter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
		name = Ambidextrous.MODNAME,
		modid = Ambidextrous.MODID,
		version = Ambidextrous.VERSION,
		acceptedMinecraftVersions = "[1.9.4,1.13)",
		dependencies = Ambidextrous.DEPENDENCIES )
public class Ambidextrous
{
	public static final String MODNAME = "Ambidextrous";
	public static final String MODID = "ambidextrous";
	public static final String VERSION = "@VERSION@";
	public static final String DEPENDENCIES = "after:Forge@[12.17.0.1909,)";

	@EventHandler
	public void preinit(
			final FMLPreInitializationEvent event )
	{
		if ( event.getSide() == Side.CLIENT )
		{
			ClientSide.init( event );
		}

		NetworkRouter.instance = new NetworkRouter();
		MinecraftForge.EVENT_BUS.register( EventPlayerInteract.instance );
	}

}
