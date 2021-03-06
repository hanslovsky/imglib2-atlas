
package net.imglib2.labkit.panel;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.labkit.inputimage.DatasetInputImage;
import net.imglib2.labkit.labeling.Labeling;
import net.imglib2.labkit.models.ColoredLabelsModel;
import net.imglib2.labkit.models.ImageLabelingModel;
import net.imglib2.type.numeric.NumericType;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LabelPanelDemo {

	public static void main(String... args) {
		Img<? extends NumericType<?>> image = ArrayImgs.unsignedBytes(10, 10);
		ImageLabelingModel imageLabeling = new ImageLabelingModel(new DatasetInputImage(image));
		ColoredLabelsModel model = new ColoredLabelsModel(imageLabeling);
		LabelPanel panel = new LabelPanel(null, model, true,
			ignore -> new JPopupMenu());
		showInFrame(panel.getComponent());
	}

	private static void showInFrame(JComponent component) {
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		frame.setLayout(new BorderLayout());
		frame.add(component);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
