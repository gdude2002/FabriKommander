package me.gserv.fabrikommander.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.util.Identifier

class IdentifierSerializer : KSerializer<Identifier> {
    override val descriptor = PrimitiveSerialDescriptor("Identifier", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Identifier {
        val string = decoder.decodeString()

        return Identifier.tryParse(string) ?: error("Invalid identifier: $string")
    }

    override fun serialize(encoder: Encoder, value: Identifier) {
        encoder.encodeString(value.toString())
    }

}
