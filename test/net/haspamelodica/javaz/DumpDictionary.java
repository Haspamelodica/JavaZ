package net.haspamelodica.javaz;

import static net.haspamelodica.javaz.core.header.HeaderField.DictionaryLoc;
import static net.haspamelodica.javaz.core.header.HeaderField.Version;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.haspamelodica.javaz.core.header.HeaderParser;
import net.haspamelodica.javaz.core.memory.CopyOnWriteMemory;
import net.haspamelodica.javaz.core.memory.SequentialMemoryAccess;
import net.haspamelodica.javaz.core.memory.StaticArrayBackedMemory;
import net.haspamelodica.javaz.core.text.UnicodeZSCIIConverter;
import net.haspamelodica.javaz.core.text.ZCharsAlphabet;
import net.haspamelodica.javaz.core.text.ZCharsSeqMemUnpacker;
import net.haspamelodica.javaz.core.text.ZCharsToZSCIIConverter;

public class DumpDictionary
{
	public static void main(String[] args) throws IOException
	{
		GlobalConfig config = new GlobalConfig();
		CopyOnWriteMemory mem = new CopyOnWriteMemory(new StaticArrayBackedMemory(Files.readAllBytes(Paths.get("storyfiles/zork1.z3"))));
		int version = HeaderParser.getFieldUnchecked(mem, Version);
		HeaderParser header = new HeaderParser(config, version, mem);
		SequentialMemoryAccess seqMem = new SequentialMemoryAccess(mem);
		ZCharsAlphabet alphabet = new ZCharsAlphabet(config, version, header, mem);
		ZCharsSeqMemUnpacker zCharsUnpacker = new ZCharsSeqMemUnpacker(seqMem);
		ZCharsToZSCIIConverter textConverter = new ZCharsToZSCIIConverter(config, version, header, mem, alphabet, zCharsUnpacker);
		UnicodeZSCIIConverter unicodeConv = new UnicodeZSCIIConverter(config);
		mem.reset();
		alphabet.reset();
		textConverter.reset();
		seqMem.setAddress(header.getField(DictionaryLoc));
		System.out.print("Word separators: ");
		for(int i = seqMem.readNextByte(); i > 0; i --)
			unicodeConv.zsciiToUnicode(seqMem.readNextByte(), System.out::print);
		System.out.println();
		System.out.println(version > 3 ? "Z-Chars                    ZSCII       Data" : "Z-Chars           ZSCII    Data");
		int dataLength = seqMem.readNextByte() - (version > 3 ? 6 : 4);
		for(int i = seqMem.readNextWord(); i > 0; i --)
		{
			int textualWordStart = seqMem.getAddress();
			zCharsUnpacker.reset();
			for(int j = (version > 3 ? 9 : 6); j > 0; j --)
				System.out.printf("%2d ", zCharsUnpacker.nextZChar());
			seqMem.setAddress(textualWordStart);
			System.out.print('"');
			int wordLenZSCII = textConverter.decode(z -> unicodeConv.zsciiToUnicode(z, System.out::print));
			System.out.print('"');
			for(int j = (version > 3 ? 9 : 6) - wordLenZSCII; j > 0; j --)
				System.out.print(' ');
			for(int j = 0; j < dataLength; j ++)
				System.out.printf(" 0x%02x", seqMem.readNextByte());
			System.out.println();
		}
	}
}