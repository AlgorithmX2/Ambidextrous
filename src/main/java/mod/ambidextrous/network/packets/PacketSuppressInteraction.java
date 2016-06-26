package mod.ambidextrous.network.packets;

import mod.ambidextrous.core.EventPlayerInteract;
import mod.ambidextrous.network.ModPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;

public class PacketSuppressInteraction extends ModPacket
{

	public EnumHand hand = EnumHand.MAIN_HAND;
	public boolean newState = false;

	@Override
	public void server(
			final EntityPlayerMP player )
	{
		EventPlayerInteract.instance.setPlayerSuppressionState( player, hand, newState, false );
	}

	@Override
	public void getPayload(
			final PacketBuffer buffer )
	{
		buffer.writeBoolean( newState );
		buffer.writeEnumValue( hand );
	}

	@Override
	public void readPayload(
			final PacketBuffer buffer )
	{
		newState = buffer.readBoolean();
		hand = buffer.readEnumValue( EnumHand.class );
	}

}
