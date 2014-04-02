package com.inherentgames;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.RGBColor;
import com.threed.jpct.Texture;

public class BBRenderer2D {
	private FrameBuffer fb;
	private BBTextureManager tm;

	private static final Texture REFERENCE_POINT = new Texture( 8, 8, RGBColor.WHITE );

	private static int charHeight = 16;
	private static int charWidth = 9;
	private static int charPerLine = 32;
	
	/**
	 * @param fb
	 */
	public BBRenderer2D( FrameBuffer fb ) {
		tm = BBTextureManager.getInstance();
		this.fb = fb;
	}

	public static int getTextWidth( String text ) {
		int length = text.length() * charWidth;
		return length;
	}

	/**
	 * @param text
	 * @param x
	 * @param y
	 */
	public void blitText( String text, int x, int y ) {
		Texture font = tm.getTexture( "gui_font" );
		for ( int i = 0; i < text.length(); i++ ) {
			char ch = text.charAt( i );
			int yS = ch / charPerLine;
			int xS = ( ch % charPerLine == 0 ) ? 0 : ch - yS * charPerLine;
			fb.blit( font, xS * charWidth, yS * charHeight, x, y, charWidth, charHeight, FrameBuffer.TRANSPARENT_BLITTING );
			x += charWidth;
		}
	}

	/**
	 * @param text
	 * @param x
	 * @param y
	 * @param addColor
	 */
	public void blitText( String text, int x, int y, RGBColor addColor ) {
		Texture font = tm.getTexture( "gui_font" );
		for ( int i = 0; i < text.length(); i++ ) {
			char ch = text.charAt( i );
			int yS = ch / charPerLine;
			int xS = ( ch % charPerLine == 0 ) ? 0 : ch - yS * charPerLine;
			fb.blit( font, xS * charWidth, yS * charHeight, x, y, charWidth, charHeight, charWidth, charHeight, 100,
					FrameBuffer.TRANSPARENT_BLITTING, addColor );
			x += charWidth;
		}
	}

	/**
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param addColor
	 */
	public void blitText( String text, int x, int y, int width, int height, RGBColor addColor ) {
		Texture font = tm.getTexture( "gui_font" );
		for ( int i = 0; i < text.length(); i++ ) {
			char ch = text.charAt( i );
			int yS = ch / charPerLine;
			int xS = ( ch % charPerLine == 0 ) ? 0 : ch - yS * charPerLine;
			fb.blit( font, xS * charWidth, yS * charHeight, x, y, charWidth, charHeight, width, height, 100,
					FrameBuffer.TRANSPARENT_BLITTING, addColor );
			x += width;
		}
	}
	
	/**
	 * @param text
	 * @param x
	 * @param y
	 * @param transparency
	 */
	public void blitText( String text, int x, int y, int transparency ) {
		Texture font = tm.getTexture( "gui_font" );
		for ( int i = 0; i < text.length(); i++ ) {
			char ch = text.charAt( i );
			int yS = ch / charPerLine;
			int xS = ( ch % charPerLine == 0 ) ? 0 : ch - yS * charPerLine;
			fb.blit( font, xS * charWidth, yS * charHeight, x, y, charWidth, charHeight, charWidth, charHeight, transparency,
					FrameBuffer.OPAQUE_BLITTING, RGBColor.WHITE );
			x += charWidth;
		}
	}
	
	/**
	 * @param fb
	 * @param textureName
	 * @param x
	 * @param y
	 * @param imageWidth
	 * @param imageHeight
	 * @param width
	 * @param height
	 * @param transparency
	 */
	public void blitImage( FrameBuffer fb, String textureName, int x, int y, int imageWidth, int imageHeight, int width, int height, int transparency ) {
		Texture image = tm.getTexture( textureName );
		fb.blit( image, 0, 0, ( x-width/2 ), y-( height/2 ), imageWidth, imageHeight, width, height, transparency, false, null );
	}
	
	/**
	 * @param fb
	 * @param textureName
	 * @param x
	 * @param y
	 * @param imageWidth
	 * @param imageHeight
	 * @param width
	 * @param height
	 * @param totalHeight
	 * @param transparency
	 */
	public void blitImageBottomUp( FrameBuffer fb, String textureName, int x, int y, int imageWidth, int imageHeight, int width, int height, int totalHeight, int transparency ) {
		Texture image = tm.getTexture( textureName );
		fb.blit( image, 0, 0, ( x-width/2 ), y-( height/2 ) + ( totalHeight- height )/2, imageWidth, imageHeight, width, height, transparency, false, null );
	}

	/**
	 * @param fb
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param transparency
	 * @param color
	 */
	public void blitFilledBox( FrameBuffer fb, int x, int y, int w, int h, int transparency, RGBColor color ) {
		fb.blit( REFERENCE_POINT, 0, 0, x, y, 8, 8, w, h, transparency, false, color );
	}
	
	/**
	 * @param fb
	 * @param w
	 * @param h
	 */
	public void blitCrosshair( FrameBuffer fb, int w, int h ) {
		fb.blit( REFERENCE_POINT, 0, 0, w/2-w/100, h/2-h/150, 8, 8, w/50, h/75, 10, false, RGBColor.BLACK );
		fb.blit( REFERENCE_POINT, 0, 0, w/2-h/150, h/2-w/100, 8, 8, h/75, w/50, 10, false, RGBColor.BLACK );
	}
	
	/**
	 * @param fb
	 * @param x
	 * @param y
	 * @param radius
	 * @param transparency
	 * @param color
	 */
	public void blitFilledCircle( FrameBuffer fb, int x, int y, int radius, int transparency, RGBColor color ) {
		for ( float i = 180.0f; i > 90.0f; i-=1 ) {
			fb.blit( REFERENCE_POINT, 0, 0, ( int )( x+Math.cos( i*Math.PI/180 )*radius ), ( int )( y-Math.sin( i*Math.PI/180 )*radius ),
					8, 8, ( int )( Math.abs( Math.cos( i*Math.PI/180 )*radius )*2 ), ( int )( Math.floor( (Math.sin( i*Math.PI/180 )*radius )-( Math.sin( (i-1 )*Math.PI/180 )*radius ) ) ), transparency, false, color );
		}
	}
	
}