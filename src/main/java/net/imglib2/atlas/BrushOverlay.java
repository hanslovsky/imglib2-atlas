/**
 *
 */
package net.imglib2.atlas;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import bdv.util.Affine3DHelpers;
import bdv.viewer.ViewerPanel;
import net.imglib2.atlas.color.IntegerColorProvider;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.OverlayRenderer;

/**
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 *
 */
public class BrushOverlay implements OverlayRenderer
{
	final static BasicStroke stroke = new BasicStroke( 1 );
	final protected ViewerPanel viewer;
	protected int x, y, width, height, radius = 5;
	protected boolean visible = false;
	final AffineTransform3D viewerTransform = new AffineTransform3D();

	private int label;

	private final IntegerColorProvider colorProvider;

	public int getLabel()
	{
		return label;
	}

	public void setLabel( final int label )
	{
		this.label = label;
	}

	public BrushOverlay( final ViewerPanel viewer, final int label, final IntegerColorProvider colorProvider )
	{
		this.viewer = viewer;
		this.label = label;
		this.colorProvider = colorProvider;
	}

	public void setPosition( final int x, final int y )
	{
		this.x = x;
		this.y = y;
	}

	public void setRadius( final int radius )
	{
		this.radius = radius;
	}

	public void setVisible( final boolean visible )
	{
		this.visible = visible;
	}

	@Override
	public void drawOverlays( final Graphics g )
	{
		if ( visible )
		{
			final Graphics2D g2d = ( Graphics2D )g;

			g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g2d.setComposite( AlphaComposite.SrcOver );

			final double scale;
			synchronized ( viewer )
			{
				viewer.getState().getViewerTransform( viewerTransform );
				scale = Affine3DHelpers.extractScale( viewerTransform, 0 );
			}
			final double scaledRadius = scale * radius;

			if (
					x + scaledRadius > 0 &&
					x - scaledRadius < width &&
					y + scaledRadius > 0 &&
					y - scaledRadius < height )
			{
				final int roundScaledRadius = ( int )Math.round( scaledRadius );
				final FontMetrics fm = g.getFontMetrics();
				final String str = "" + label;
				final Rectangle2D rect = fm.getStringBounds( str, g );
				g2d.setColor( Color.WHITE );
				g2d.fillRect( x + roundScaledRadius, y + roundScaledRadius - fm.getAscent(), ( int ) rect.getWidth(), ( int ) rect.getHeight() );
				g2d.setColor( new Color( colorProvider.getColor( label ) ) );
				g2d.setStroke( stroke );
				g2d.drawOval( x - roundScaledRadius, y - roundScaledRadius, 2 * roundScaledRadius + 1, 2 * roundScaledRadius + 1 );
				g2d.drawString( str, x + roundScaledRadius, y + roundScaledRadius );

			}
		}
	}

	@Override
	public void setCanvasSize( final int width, final int height )
	{
		this.width = width;
		this.height = height;
	}

}