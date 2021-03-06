package pl.karol202.cncclient.ui;

import pl.karol202.cncclient.cnc.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class PanelPreview extends JPanel
{
	private static final float MARGIN = 30;
	
	private CodePreviewer previewer;
	private MoveRecorder moveRecorder;
	
	private float scale;
	private int xOffset;
	private int yOffset;
	private boolean drawGCodePath;
	private boolean drawRecordedPath;
	
	private Graphics2D graphics;
	private PreviewPoint previousPoint;
	
	PanelPreview(GCode gcode, MoveRecorder moveRecorder)
	{
		this.previewer = new CodePreviewer(gcode);
		this.moveRecorder = moveRecorder;
		
		this.scale = 1f;
		this.xOffset = 100;
		this.yOffset = 100;
		this.drawGCodePath = true;
		this.drawRecordedPath = false;
		
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		graphics = (Graphics2D) g;
		drawBackground();
		if(drawGCodePath) drawPathLines();
		if(drawRecordedPath) drawRecordedLines();
	}
	
	private void drawBackground()
	{
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private boolean drawPathLines()
	{
		previousPoint = null;
		
		List<PreviewPoint> allPoints = previewer.getAllPoints();
		List<PreviewPoint> pointsOfLinesBeforeOrAtSelection = previewer.getPointsOfLinesBeforeOrAtSelection();
		if(allPoints == null) return false;
		
		calculateBounds(allPoints);
		pointsOfLinesBeforeOrAtSelection.forEach(this::nextPoint);
		return true;
	}
	
	private void drawRecordedLines()
	{
		previousPoint = null;
		
		List<RecordedPoint> points = moveRecorder.getRecordedPoints();
		points.forEach(this::nextPoint);
	}
	
	private void calculateBounds(List<PreviewPoint> points)
	{
		float minX = 0f;
		float minY = 0f;
		float maxX = 0f;
		float maxY = 0f;
		
		for(PreviewPoint point : points)
		{
			float x = point.getX();
			float y = point.getY();
			minX = Math.min(x, minX);
			minY = Math.min(y, minY);
			maxX = Math.max(x, maxX);
			maxY = Math.max(y, maxY);
		}
		minX -= MARGIN;
		minY -= MARGIN;
		maxX += MARGIN;
		maxY += MARGIN;
		
		float rangeX = maxX - minX;
		float rangeY = maxY - minY;
		float ratioX = getWidth() / rangeX;
		float ratioY = getHeight() / rangeY;
		scale = Math.min(ratioX, ratioY);
		
		xOffset = (int) (-half(minX, maxX) * scale) + (getWidth() / 2);
		yOffset = getHeight() - (int) (-half(minY, maxY) * scale) - (getHeight() / 2);
	}
	
	private float half(float a, float b)
	{
		return a + (0.5f * (b - a));
	}
	
	private void nextPoint(PreviewPoint point)
	{
		if(previousPoint != null) drawLine(point);
		previousPoint = point;
	}
	
	private void drawLine(PreviewPoint point)
	{
		Point transformedPreviousPoint = transformPoint(new Point((int) previousPoint.getX(), (int) previousPoint.getY()));
		Point transformedPoint = transformPoint(new Point((int) point.getX(), (int) point.getY()));
		Color lineColor = point.isInterpolated() ? Color.BLACK : Color.GRAY;
		float[] lineDash = point.getZ() < 5 ? null : new float[] { 5f, 5f };
		
		graphics.setColor(lineColor);
		graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, lineDash, 0f));
		graphics.drawLine(transformedPreviousPoint.x, transformedPreviousPoint.y, transformedPoint.x, transformedPoint.y);
	}
	
	private Point transformPoint(Point point)
	{
		float x = (point.x * scale) + xOffset;
		float y = (-point.y * scale) + yOffset;
		return new Point((int) x, (int) y);
	}
	
	boolean isDrawingGCodePath()
	{
		return drawGCodePath;
	}
	
	void setDrawingGCodePath(boolean drawGCodePath)
	{
		this.drawGCodePath = drawGCodePath;
		repaint();
	}
	
	boolean isDrawingRecordedPath()
	{
		return drawRecordedPath;
	}
	
	void setDrawingRecordedPath(boolean drawRecordedPath)
	{
		this.drawRecordedPath = drawRecordedPath;
		repaint();
	}
}