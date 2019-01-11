package test;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.*;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

public class DialDemo3 extends JFrame
{
	static class DemoPanel extends JPanel
		implements ChangeListener
	{

		JSlider slider;
		DefaultValueDataset dataset;

		public void stateChanged(ChangeEvent changeevent)
		{
			dataset.setValue(new Integer(slider.getValue()));
		}

		public DemoPanel()
		{
			super(new BorderLayout());
			dataset = new DefaultValueDataset(50D);
			DialPlot dialplot = new DialPlot();
			dialplot.setView(0.20999999999999999D, 0.0D, 0.57999999999999996D, 0.29999999999999999D);
			dialplot.setDataset(dataset);
			ArcDialFrame arcdialframe = new ArcDialFrame(60D, 60D);
			arcdialframe.setInnerRadius(0.59999999999999998D);
			arcdialframe.setOuterRadius(0.90000000000000002D);
			arcdialframe.setForegroundPaint(Color.darkGray);
			arcdialframe.setStroke(new BasicStroke(3F));
			dialplot.setDialFrame(arcdialframe);
			GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(240, 240, 240));
			DialBackground dialbackground = new DialBackground(gradientpaint);
			dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
			dialplot.addLayer(dialbackground);
			StandardDialScale standarddialscale = new StandardDialScale(0.0D, 100D, 115D, -50D, 10D, 4);
			standarddialscale.setTickRadius(0.88D);
			standarddialscale.setTickLabelOffset(0.070000000000000007D);
			standarddialscale.setMajorTickIncrement(25D);
			dialplot.addScale(0, standarddialscale);
			org.jfree.chart.plot.dial.DialPointer.Pin pin = new org.jfree.chart.plot.dial.DialPointer.Pin();
			pin.setRadius(0.81999999999999995D);
			dialplot.addLayer(pin);
			JFreeChart jfreechart = new JFreeChart(dialplot);
			jfreechart.setTitle("Dial Demo 3");
			ChartPanel chartpanel = new ChartPanel(jfreechart);
			chartpanel.setPreferredSize(new Dimension(400, 250));
			slider = new JSlider(0, 100);
			slider.setMajorTickSpacing(10);
			slider.setPaintLabels(true);
			slider.addChangeListener(this);
			add(chartpanel);
			add(slider, "South");
		}
	}


	public static JPanel createDemoPanel()
	{
		return new DemoPanel();
	}

	public DialDemo3(String s)
	{
		super(s);
		setDefaultCloseOperation(3);
		setContentPane(createDemoPanel());
	}

	public static void main(String args[])
	{
		DialDemo3 dialdemo3 = new DialDemo3("JFreeChart - Demo Dial 3");
		dialdemo3.pack();
		dialdemo3.setVisible(true);
	}
}