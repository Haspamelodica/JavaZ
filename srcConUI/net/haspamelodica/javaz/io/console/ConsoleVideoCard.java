package net.haspamelodica.javaz.io.console;

import java.io.InputStreamReader;
import java.io.Reader;

import net.haspamelodica.javaz.GlobalConfig;
import net.haspamelodica.javaz.core.header.HeaderParser;
import net.haspamelodica.javaz.core.io.IOException;
import net.haspamelodica.javaz.core.io.VideoCard;
import net.haspamelodica.javaz.core.text.UnicodeZSCIIConverter;
import net.haspamelodica.javaz.core.text.ZSCIICharStream;

public class ConsoleVideoCard implements VideoCard
{
	private final UnicodeZSCIIConverter	unicodeConv;
	private final Reader				in;

	private int lastY = -1;

	public ConsoleVideoCard(GlobalConfig config, int version, HeaderParser headerParser, UnicodeZSCIIConverter unicodeConv)
	{
		this.unicodeConv = unicodeConv;
		this.in = new InputStreamReader(System.in);
	}

	@Override
	public void scroll(int y)
	{
		if(lastY != -1)
			lastY -= y;
	}
	@Override
	public void showChar(char unicodeChar, int font, int style, int trueFB, int trueBG, int x, int y)
	{
		if(lastY == -1)
			lastY = y;
		else
			for(; lastY < y; lastY ++)
				System.out.println();
		System.out.print(unicodeChar);
	}

	@Override
	public void flushScreen()
	{}
	@Override
	public int getScreenWidth()
	{
		return 60;
	}
	@Override
	public int getScreenHeight()
	{
		return 10;
	}
	@Override
	public void showStatusBar(ZSCIICharStream locationConv, int scoreOrHours, int turnsOrMinutes, boolean isTimeGame)
	{}
	@Override
	public int getTrueColor(int color)
	{
		return 0;
	}
	@Override
	public int inputSingleChar()
	{
		try
		{
			int unicodeToZSCII;
			do
			{
				int read = in.read();
				if(read == -1)
					return -1;
				unicodeToZSCII = unicodeConv.unicodeToZSCII((char) read);
			} while(unicodeToZSCII == -1);
			return unicodeToZSCII;
		} catch(java.io.IOException e)
		{
			throw new IOException(e);
		}
	}
	@Override
	public int getDefaultTrueFG()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getDefaultTrueBG()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void eraseArea(int x, int y, int w, int h, int trueBG)
	{}
	@Override
	public void showPicture(int picture, int x, int y)
	{}
	@Override
	public void erasePicture(int picture, int x, int y, int trueBG)
	{}

	@Override
	public int getCharWidth(char unicodeChar, int font, int style)
	{
		return 1;
	}

	@Override
	public int getFontHeight(int font)
	{
		return 1;
	}
}