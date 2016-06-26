package mod.ambidextrous.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientSide
{

	private static ClientSide instance;

	// track the standard MC bind so it can be restored when we arn't one of the
	// two below.
	KeyBinding bindingOriginal = null;

	// basically the mod, in two lines.
	KeyBinding bindingMainHand;
	KeyBinding bindingOffHand;

	public ClientSide()
	{
		// just put them next to the original use item keybind.
		final String category = "key.categories.gameplay";

		ClientRegistry.registerKeyBinding( bindingOffHand = new KeyBinding( "mod.ambidextrous.offhand", KeyConflictContext.IN_GAME, 0, category ) );
		ClientRegistry.registerKeyBinding( bindingMainHand = new KeyBinding( "mod.ambidextrous.mainhand", KeyConflictContext.IN_GAME, 0, category ) );
	}

	// track when a button was pressed to allow swapping to the previous button
	// seamlessly. 0 = not pressed.
	long msecondsForMainHand = 0;
	long msecondsForOffHand = 0;

	// check key-binds.
	@SubscribeEvent
	public void tick(
			final TickEvent.ClientTickEvent e )
	{
		final Minecraft mc = Minecraft.getMinecraft();

		if ( bindingOriginal == null )
		{
			// find keybind for use item, we need this...
			bindingOriginal = mc.gameSettings.keyBindUseItem;
		}

		// when mousing up switch to the other button if its down.
		if ( msecondsForMainHand != 0 && msecondsForMainHand < msecondsForOffHand && bindingMainHand.isKeyDown() && !bindingOffHand.isKeyDown() )
		{
			EventPlayerInteract.instance.setPlayerSuppressionState( mc.thePlayer, EnumHand.OFF_HAND, true, true );
			mc.gameSettings.keyBindUseItem = bindingMainHand;
			msecondsForOffHand = 0;
		}

		if ( msecondsForOffHand != 0 && msecondsForOffHand < msecondsForMainHand && bindingOffHand.isKeyDown() && !bindingMainHand.isKeyDown() )
		{
			EventPlayerInteract.instance.setPlayerSuppressionState( mc.thePlayer, EnumHand.MAIN_HAND, true, true );
			mc.gameSettings.keyBindUseItem = bindingOffHand;
			msecondsForMainHand = 0;
		}

		// handle switch binsd to new active key.
		if ( mc.gameSettings.keyBindUseItem != bindingMainHand && bindingMainHand.isPressed() )
		{
			bindingMainHand.pressTime++;
			EventPlayerInteract.instance.setPlayerSuppressionState( mc.thePlayer, EnumHand.OFF_HAND, true, true );
			mc.gameSettings.keyBindUseItem = bindingMainHand;
			msecondsForMainHand = System.currentTimeMillis();
		}

		if ( mc.gameSettings.keyBindUseItem != bindingOffHand && bindingOffHand.isPressed() )
		{
			bindingOffHand.pressTime++;
			EventPlayerInteract.instance.setPlayerSuppressionState( mc.thePlayer, EnumHand.MAIN_HAND, true, true );
			mc.gameSettings.keyBindUseItem = bindingOffHand;
			msecondsForOffHand = System.currentTimeMillis();
		}

		// stop using one of the two key binds.
		if ( !bindingMainHand.isKeyDown() && !bindingOffHand.isKeyDown() && mc.gameSettings.keyBindUseItem != bindingOriginal )
		{
			EventPlayerInteract.instance.setPlayerSuppressionState( mc.thePlayer, EnumHand.OFF_HAND, false, true );
			mc.gameSettings.keyBindUseItem = bindingOriginal;
			msecondsForMainHand = 0;
			msecondsForOffHand = 0;
		}
	}

	public static void init(
			final FMLPreInitializationEvent event )
	{
		instance = new ClientSide();
		MinecraftForge.EVENT_BUS.register( instance );
	}

}
