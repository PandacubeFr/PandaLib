package fr.pandacube.util.network.packet.bytebuffer;

/**
 * Cette interface permet à un {@link ByteBuffer} de sérialiser sous forme de
 * données binaire
 * les attributs de la classe courante.<br/>
 * <br/>
 * Les classes concrètes implémentant cette interface doivent avoir un
 * constructeur vide, utilisé
 * lors de la désérialisation
 *
 */
public interface ByteSerializable {

	public void serializeToByteBuffer(ByteBuffer buffer);

	public void deserializeFromByteBuffer(ByteBuffer buffer);

}
