package mod.ambidextrous.core;

import java.util.WeakHashMap;

import mod.ambidextrous.network.NetworkRouter;
import mod.ambidextrous.network.packets.PacketSuppressInteraction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerInteract
{

	private class SupressionState
	{
		public SupressionState(
				final EnumHand hand2,
				final boolean newSetting )
		{
			hand = hand2;
			suppress = newSetting;
		}

		public EnumHand hand = EnumHand.MAIN_HAND;
		public boolean suppress = false;
	};

	public static EventPlayerInteract instance = new EventPlayerInteract();

	// just for the sake of simplicity.
	private final WeakHashMap<EntityPlayer, SupressionState> server_state = new WeakHashMap<EntityPlayer, SupressionState>();
	private final WeakHashMap<EntityPlayer, SupressionState> client_state = new WeakHashMap<EntityPlayer, SupressionState>();

	// Are we on the server or the client?
	private WeakHashMap<EntityPlayer, SupressionState> getState(
			final EntityPlayer entityPlayer )
	{
		if ( entityPlayer == null || entityPlayer.getEntityWorld() == null || entityPlayer.getEntityWorld().isRemote )
		{
			return client_state;
		}

		return server_state;
	}

	@SubscribeEvent
	public void click(
			final PlayerInteractEvent.RightClickItem e )
	{
		handleClick( e );
	}

	@SubscribeEvent
	public void click(
			final PlayerInteractEvent.RightClickBlock e )
	{
		handleClick( e );

		if ( !e.isCanceled() )
		{
			final ItemStack s = e.getItemStack();
			if ( s == null || s.getItem().doesSneakBypassUse( s, e.getWorld(), e.getPos(), e.getEntityPlayer() ) )
			{
				e.setUseBlock( Result.ALLOW );
			}
		}
	}

	private void handleClick(
			final PlayerInteractEvent e )
	{
		final SupressionState current = getState( e.getEntityPlayer() ).get( e.getEntityPlayer() );

		if ( current == null || current.suppress == false )
		{
			// if nothing is set, or supression isn't on, then we don't care.
			return;
		}

		if ( current.hand == e.getHand() )
		{
			// if we here suppression is on, was it the correct hand?
			e.setCanceled( true );
		}
	}

	public void setPlayerSuppressionState(
			final EntityPlayer player,
			final EnumHand hand,
			final boolean newState,
			final boolean sendToServer )
	{
		if ( sendToServer )
		{
			final PacketSuppressInteraction packetSI = new PacketSuppressInteraction();
			packetSI.hand = hand;
			packetSI.newState = newState;
			NetworkRouter.instance.sendToServer( packetSI );
		}

		getState( player ).put( player, new SupressionState( hand, newState ) );
	}

}
