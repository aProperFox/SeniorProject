package com.inherentgames;

import java.util.ConcurrentModificationException;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.opengl.GLSurfaceView;
import android.util.Log;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IPaintListener;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.World;

// Renders the state of the game
class BBRenderer implements GLSurfaceView.Renderer {
	
	// Library objects
	protected static FrameBuffer fb;
	private BBTextureManager tm;
	private World loadingWorld;
	private BBGame game;
	
	/* Internal parameters */
	
	//private GLText glText;
	
	// Represents the "cleared" background color of the frame buffer 
	private RGBColor bg = new RGBColor( 255, 255, 255 );
	
	private int letterWidth;
	
	int soundID = 1;
	
	private float fuelHeight;
	private float timeHeight;
	
	private int arrowX;
	private int arrowY;
	private int arrowImageWidth;
	private int arrowImageHeight;
	private int arrowScreenWidth;
	private int arrowScreenHeight;
	
	private String arrowState = "ArrowUp";
	
	private int paintCount = 0;
	
	private static final Texture REFERENCE_POINT = new Texture( 8, 8, RGBColor.WHITE );

	private static int charHeight = 16;
	private static int charWidth = 9;
	private static int charPerLine = 32;
	
	/*private float[] mProjMatrix = new float[16];
	private float[] mVMatrix = new float[16];
	private float[] mVPMatrix = new float[16];*/
	
	/**
	 * @param c
	 * @param w
	 * @param h
	 * @param level
	 */
	public BBRenderer() {
		
		// Initialize variables
		tm = BBTextureManager.getInstance();
		game = BBGame.getInstance();
		
		letterWidth = BB.width / 96;
		
		timeHeight = (int) (BB.height * 0.76);
		fuelHeight = 0;
		
		arrowX = BB.width / 12;
		arrowY = BB.height / 10 + BB.width / 6;
		arrowImageWidth = 32;
		arrowImageHeight = 64;
		arrowScreenWidth = BB.width / 12;
		arrowScreenHeight = BB.width / 6;
	}
	
	// Triggered when the view port is created
	public void onSurfaceCreated( GL10 gl, EGLConfig config ) {
		/*glText = new GLText( BB.getAppContext().getAssets() );
		glText.load( "futura-normal.ttf", 14, 2, 2 );
		
		// enable texture + alpha blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);*/
	}
	
	// Triggered when the view port changes (e.g., by size)
	public void onSurfaceChanged( GL10 gl, int w, int h ) {
		// Clear the frame buffer if it's not clean
		if ( fb != null ) {
			fb.dispose();
		}
		// Initialize a new frame buffer with the new frame/screen size
		fb = new FrameBuffer( gl, w, h );
		
		// Create and render a dummy world for the loading screen
		// Note: We need to do this because jPCT-AE won't allow us to blit 
		//       a frame buffer until we've rendered a world to it at least once.
		loadingWorld = new World();
		loadingWorld.renderScene( fb );
		
		// Watch the frame buffer for paint events
		fb.setPaintListener( new IPaintListener() {

			private static final long serialVersionUID = 449987772860108478L;

			@Override
			// When the frame buffer finishes painting the first time, we have our 
			// loading screen painted. Then, we trigger the resource loader runnable
			// to load the actual game resources asynchronously. When the actual game
			// loads, it clears the frame buffer and starts painting the actual game scene.
			public void finishedPainting() {
				++paintCount;
				if ( paintCount == 1 )
					new Thread( game.loader ).start();
				if ( !game.loading ) {
					Log.d( "BBRenderer", "Finished loading game" );
					paintCount = 0;
					fb.setPaintListener( null );
				}
			}

			@Override
			public void startPainting() {}
			
		} );
		
	}
	
	// Triggers every clock cycle / time the system wants to draw a frame
	public void onDrawFrame( GL10 gl ) {
		
		// Clear the frame buffer
		fb.clear( bg );
		
		// Render loading screen if the game is loading
		if ( game.loading ) {
			
			/*Matrix.multiplyMM(mVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
			glText.begin( 0.0f, 0.0f, 1.0f, 1.0f, mVPMatrix );
			glText.draw( "Loading...", 50, 200 );
			glText.end();*/
			
			loadingWorld.draw( fb );
			fb.blit( tm.getTexture( "loading_splash" ), 0, 0, BB.width / 2 - 512, BB.height / 2 - 512, 1024, 1024, true );
			fb.display();
			
		} else {
			
			// Update game elements
			game.update();
			
			// Render sky box (if applicable)
			if ( game.world.skybox != null )
				game.world.skybox.render( game.world, fb );
		    
		    // Render the world, and then draw to the frame buffer
			game.world.renderScene( fb );
			game.world.draw( fb );
			
			fuelHeight = (int) ( ((float) game.captured / game.world.getNumWordObjects()) * (BB.height * 0.75) );
			
			// Render the HUD/sprite elements
			display2DGameInfo();
			/*
			 * TODO: add color to WordObjects when camera is aimed at them
			int id = world.getCameraBox().checkForCollision( cam.getDirection(), 80 );
			if ( id != -100 ) {
				WordObject wordObject = ( WordObject )world.getObject( id );
				if ( wordObject.getStaticState() ) {
				Log.i( "BBRenderer", "Viewed object collision!" );
					//wordObject.setAdditionalColor( 255, 255, 0 );
				}
			}
			*/
		}
			
	}
	
	/**
	 * @return
	 */
	public static FrameBuffer getFrameBuffer() {
		return fb;
	}
	
	/**
	 * @param fb
	 */
	public void display2DGameInfo() {
		int w = BB.width;
		int h = BB.height;
		
		if ( game.isTutorial ) {
			//CrossHair
			blitCrosshair( w, h );
			
			if ( game.hasCompletedSteps ) {
				
				// Bubble image
				blitImage( game.bubbleTex, w/2, h, 256, 256, w/3, w/3, 5 );
				// Bubble text
				blitText( game.world.getBubbleArticle(), w/2-w/25, h-w/10, w/25, h/10, RGBColor.WHITE );
				// Fire Button
				blitImage( game.fireButtonState, w/8, h-( w/8 ), 128, 128, w/8, w/8, 10 );
				// Pause Button
				blitImage( game.pauseButtonState, w-w/30, w/35, 128, 128, w/15, w/15, 100 );
				// Dynamic fuel/time bars
				blitImageBottomUp( "FuelBar", (int) (w * 0.909), h/2, 16, 512, w/38, (int) fuelHeight, (int) (h * 0.76), 100 );
				blitImageBottomUp( "TimeBar", (int) (w * 0.966), h/2, 16, 512, w/38, (int) (h * 0.76), (int) (h * 0.76), 100 );
				// Score bars 
				blitImage( "ScoreBars", w-( w/16 ), h/2, 128, 512, w/8, ( int )( h*0.9 ), 100 );
				blitImage( "ScoreArrow", (int) (w * 0.9) , (int) (h * 0.881 - fuelHeight), 32, 32, w / 38, w / 38, 100 );
				
			} else {
				// Pause Button
				blitImage( game.pauseButtonState, w-w/30, w/35, 128, 128, w/15, w/15, 100 );
				
				int iterator = 0, size = game.screenItems.length - 1;
				for ( int i : game.screenItems ) {
					if ( iterator < size )
						displayScreenItem( fb, i );
					iterator++;
				}
				if ( game.screenItems[game.screenItems.length - 1] <= 0 ) {
				}
				else {
					blitImage( "Filter", w/2, h/2, 64, 64, w, h, 10 );
				}
				displayScreenItem( fb, game.screenItems[size] );
			}
			if ( game.screenItems[game.screenItems.length-1] > 0 )
				blitImage( arrowState, arrowX, arrowY, arrowImageWidth, arrowImageHeight, arrowScreenWidth, arrowScreenHeight, 100 );
			//Info Bar
			//Has extra 1 px hang if using real size? Decremented to 127x127
			blitImage( "InfoBar", w/10, w/10, 127, 127, w/5, w/5, 100 );

			try {
				// Wattson help text
				int iteration = 0;
				for ( String string : game.wattsonText ) {
					blitText( string, w/6, h/30 + ( letterWidth*2*iteration ), letterWidth, letterWidth*2, RGBColor.WHITE );
					iteration++;
				}
			} catch ( ConcurrentModificationException e ) {
				Log.e( "BBRenderer", "display2DGameInfo got ConcurrentModificationError: " + e );
			}
			
		}
		
		// Not tutorial displays
		else {
			
			blitCrosshair( w, h );
			//Bubble image
			blitImage( game.bubbleTex, w/2, h, 256, 256, w/3, w/3, 5 );
			//Bubble text
			blitText( game.world.getBubbleArticle(), w/2-w/25, h-w/10, w/25, h/10, RGBColor.WHITE );
			//Fire Button
			blitImage( game.fireButtonState, w/8, h-( w/8 ), 128, 128, w/8, w/8, 10 );
			//Pause Button
			blitImage( game.pauseButtonState, w-w/30, w/35, 128, 128, w/15, w/15, 100 );
			//Info Bar
			//Has extra 1px hang if using real size? Decremented to 255x255
			blitImage( "InfoBar", w/10, w/10, 127, 127, w/5, w/5, 100 );
			
			//Dynamic fuel/time bars
			blitImageBottomUp( "FuelBar", (int) (w * 0.909), h/2, 16, 512, w/38, (int) fuelHeight, (int) (h * 0.76), 100 );
			blitImageBottomUp( "TimeBar", (int) (w * 0.966), h/2, 16, 512, w/38, (int) timeHeight, (int) (h * 0.76), 100 );
			//Score bars
			blitImage( "ScoreBars", w-( w/16 ), h/2, 128, 512, w/8, ( int )( h*0.9 ), 100 );
			blitImage( "ScoreArrow", (int) (w * 0.9), (int) (h * 0.881 - fuelHeight), 32, 32, w/38, w/38, 100 );
		
		}
		
		fb.display();
	}
	
	/**
	 * @param fb
	 * @param itemNum
	 */
	// TODO: Figure out what this function actually does, and separate the tasks between BBRenderer & BBGame
	private void displayScreenItem( FrameBuffer fb, int itemNum ) {
		int w = BB.width;
		int h = BB.height;
		
		boolean isLastItem;
		if ( game.screenItems[game.screenItems.length-1] == itemNum )
			isLastItem = true;
		else
			isLastItem = false;
		switch( itemNum ) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				//Fire Button
				blitImage( game.fireButtonState, w/8, h-( w/8 ), 128, 128, w/8, w/8, 10 );
				//Bubble image
				blitImage( game.bubbleTex, w/2, h, 256, 256, w/3, w/3, 5 );
				//Bubble text
				blitText( game.world.getBubbleArticle(), w/2-w/25, h-w/10, w/25, h/10, RGBColor.WHITE );
				
				if ( isLastItem ) {
					arrowX = ( int )( w/3.5f );
					arrowY = h - ( w/8 );
					arrowImageWidth = 64;
					arrowImageHeight = 32;
					arrowScreenWidth = w/6;
					arrowScreenHeight = w/12;
					arrowState = "ArrowLeft";
				}
				break;
			case 3:
				// Dynamic fuel bar
				blitImageBottomUp( "FuelBar", (int) (w * 0.909), h/2, 16, 512, w/38, (int) fuelHeight, (int) (h * 0.76), 100 );
				blitImage( "ScoreBars", w-( w/16 ), h/2, 128, 512, w/8, (int) (h * 0.9), 100 );
				blitImage( "ScoreArrow", (int) (w * 0.9), (int) (h * 0.881 - fuelHeight), 32, 32, w/38, w/38, 100 );
				
				if ( isLastItem ) {
					arrowX = 4*w/5;
					arrowY = h/2;
					arrowImageWidth = 64;
					arrowImageHeight = 32;
					arrowScreenWidth = w/6;
					arrowScreenHeight = w/12;
					arrowState = "ArrowRight";
				}
				break;
			case 4:
				//Dynamic time bar
				
				blitImageBottomUp( "TimeBar", (int) (w * 0.966), h/2, 16, 512, w/38, (int) (h * 0.76), (int) (h * 0.76), 100 );
				//Score bars 
				blitImage( "ScoreBars", w-( w/16 ), h/2, 128, 512, w/8, (int) (h * 0.9), 100 );
				blitImage( "ScoreArrow", (int) (w * 0.9), (int) (h * 0.881 - fuelHeight), 32, 32, w/38, w/38, 100 );
				
				if ( isLastItem ) {
					arrowX = 7*w/8;
					arrowY = h/2;
					arrowImageWidth = 64;
					arrowImageHeight = 32;
					arrowScreenWidth = w/6;
					arrowScreenHeight = w/12;
					arrowState = "ArrowRight";
				}
				
				break;
			case 5:
				arrowX = w/12;
				arrowY = h/10 + w/6;
				arrowImageWidth = 32;
				arrowImageHeight = 64;
				arrowScreenWidth = w/12;
				arrowScreenHeight = w/6;
				arrowState = "ArrowUp";
				break;
			case -3:
				if ( game.screenItems[0] == itemNum )
					game.cam.lookAt( new SimpleVector( 0, 0, 1 ) );
				if ( isLastItem )
					game.cam.lookAt( new SimpleVector( -.358, 0.174, 0.917 ) );
				break;
			default:
				break;
		}
		
	}
	
	/**
	 * @param obj
	 * @return
	 */
	public Vector3f getDimensions( Object3D obj ) {
		PolygonManager polyMan = obj.getPolygonManager();
		int polygons = polyMan.getMaxPolygonID();
		Vector3f minVerts = new Vector3f( 1000, 1000, 1000 );
		Vector3f maxVerts = new Vector3f( -1000, -1000, -1000 );
		for ( int i = 0; i < polygons; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				if ( minVerts.x > polyMan.getTransformedVertex( i, j ).x )
					minVerts.x = polyMan.getTransformedVertex( i, j ).x;
				if ( maxVerts.x < polyMan.getTransformedVertex( i, j ).x )
					maxVerts.x = polyMan.getTransformedVertex( i, j ).x;
				if ( minVerts.y > polyMan.getTransformedVertex( i, j ).y )
					minVerts.y = polyMan.getTransformedVertex( i, j ).y;
				if ( maxVerts.y < polyMan.getTransformedVertex( i, j ).y )
					maxVerts.y = polyMan.getTransformedVertex( i, j ).y;
				if ( minVerts.z > polyMan.getTransformedVertex( i, j ).z )
					minVerts.z = polyMan.getTransformedVertex( i, j ).z;
				if ( maxVerts.z < polyMan.getTransformedVertex( i, j ).z )
					maxVerts.z = polyMan.getTransformedVertex( i, j ).z;
			}
		}
		return new Vector3f( maxVerts.x - minVerts.x, maxVerts.y - minVerts.y, maxVerts.z - minVerts.z );
	}
	
	
	/**
	 * @param vector
	 * @return
	 */
	public Vector3f toVector3f( SimpleVector vector ) {
		return new Vector3f( vector.x, vector.y, vector.z );
	}
	
	/**
	 * @param vector
	 * @return
	 */
	public SimpleVector toSimpleVector( Vector3f vector ) {
		return new SimpleVector( vector.x, vector.y, vector.z );
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
	 * @param textureName
	 * @param x
	 * @param y
	 * @param imageWidth
	 * @param imageHeight
	 * @param width
	 * @param height
	 * @param transparency
	 */
	public void blitImage( String textureName, int x, int y, int imageWidth, int imageHeight, int width, int height, int transparency ) {
		Texture image = tm.getTexture( textureName );
		fb.blit( image, 0, 0, ( x-width/2 ), y-( height/2 ), imageWidth, imageHeight, width, height, transparency, false, null );
	}
	
	/**
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
	public void blitImageBottomUp( String textureName, int x, int y, int imageWidth, int imageHeight, int width, int height, int totalHeight, int transparency ) {
		Texture image = tm.getTexture( textureName );
		fb.blit( image, 0, 0, ( x-width/2 ), y-( height/2 ) + ( totalHeight- height )/2, imageWidth, imageHeight, width, height, transparency, false, null );
	}

	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param transparency
	 * @param color
	 */
	public void blitFilledBox( int x, int y, int w, int h, int transparency, RGBColor color ) {
		fb.blit( REFERENCE_POINT, 0, 0, x, y, 8, 8, w, h, transparency, false, color );
	}
	
	/**
	 * @param fb
	 * @param w
	 * @param h
	 */
	public void blitCrosshair( int w, int h ) {
		fb.blit( REFERENCE_POINT, 0, 0, w/2-w/100, h/2-h/150, 8, 8, w/50, h/75, 10, false, RGBColor.BLACK );
		fb.blit( REFERENCE_POINT, 0, 0, w/2-h/150, h/2-w/100, 8, 8, h/75, w/50, 10, false, RGBColor.BLACK );
	}
	
	/**
	 * @param x
	 * @param y
	 * @param radius
	 * @param transparency
	 * @param color
	 */
	public void blitFilledCircle( int x, int y, int radius, int transparency, RGBColor color ) {
		for ( float i = 180.0f; i > 90.0f; i-=1 ) {
			fb.blit( REFERENCE_POINT, 0, 0, ( int )( x+Math.cos( i*Math.PI/180 )*radius ), ( int )( y-Math.sin( i*Math.PI/180 )*radius ),
					8, 8, ( int )( Math.abs( Math.cos( i*Math.PI/180 )*radius )*2 ), ( int )( Math.floor( (Math.sin( i*Math.PI/180 )*radius )-( Math.sin( (i-1 )*Math.PI/180 )*radius ) ) ), transparency, false, color );
		}
	}
}

