
package net.imglib2.labkit.models;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.labkit.labeling.Label;
import net.imglib2.labkit.labeling.Labeling;
import net.imglib2.labkit.utils.Notifier;
import net.imglib2.roi.IterableRegion;
import net.imglib2.trainable_segmention.RevampUtils;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.ARGBType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ColoredLabelsModel {

	private final ImageLabelingModel model;

	private final Notifier<Runnable> listeners = new Notifier<>();

	public ColoredLabelsModel(ImageLabelingModel model) {
		this.model = model;
		model.labeling().notifier().add(l -> notifyListeners());
		model.selectedLabel().notifier().add(s -> notifyListeners());
	}

	private void notifyListeners() {
		listeners.forEach(Runnable::run);
	}

	public List<Label> items() {
		return model.labeling().get().getLabels();
	}

	public Label selected() {
		return model.selectedLabel().get();
	}

	public void setSelected(Label value) {
		model.selectedLabel().set(value);
	}

	public Notifier<Runnable> listeners() {
		return listeners;
	}

	public void addLabel() {
		Holder<Labeling> holder = model.labeling();
		Labeling labeling = holder.get();
		String newName = suggestName(labeling.getLabels().stream().map(Label::name)
			.collect(Collectors.toList()));
		if (newName == null) return;
		Label newLabel = labeling.addLabel(newName);
		model.selectedLabel().set(newLabel);
		holder.notifier().forEach(l -> l.accept(labeling));
	}

	public void removeLabel(Label label) {
		Holder<Labeling> holder = model.labeling();
		Labeling labeling = holder.get();
		holder.get().removeLabel(label);
		holder.notifier().forEach(l -> l.accept(labeling));
	}

	public void renameLabel(Label label, String newLabel) {
		Holder<Labeling> holder = model.labeling();
		Labeling labeling = holder.get();
		holder.get().renameLabel(label, newLabel);
		holder.notifier().forEach(l -> l.accept(labeling));
	}

	public void moveLabel(Label label, int movement) {
		Holder<Labeling> holder = model.labeling();
		Labeling labeling = holder.get();
		List<Label> oldOrder = new ArrayList<>(labeling.getLabels());
		Function<Label, Double> priority = l -> oldOrder.indexOf(l) + (l == label
			? movement + 0.5 * Math.signum(movement) : 0.0);
		labeling.setLabelOrder(Comparator.comparing(priority));
		holder.notifier().forEach(l -> l.accept(labeling));
	}

	public void setColor(Label label, ARGBType newColor) {
		Holder<Labeling> holder = model.labeling();
		Labeling labeling = holder.get();
		label.setColor(newColor);
		holder.notifier().forEach(l -> l.accept(labeling));
	}

	private String suggestName(List<String> labels) {
		for (int i = 1; i < 10000; i++) {
			String label = "Label " + i;
			if (!labels.contains(label)) return label;
		}
		return null;
	}

	public void localizeLabel(final Label label) {
		Interval labelBox = getBoundingBox(model.labeling().get().iterableRegions()
			.get(label));
		if (model.isTimeSeries()) labelBox = RevampUtils.removeLastDimension(
			labelBox);
		if (labelBox != null) model.transformationModel().transformToShowInterval(
			labelBox, model.labelTransformation());
	}

	private static Interval getBoundingBox(IterableRegion<BitType> region) {
		int numDimensions = region.numDimensions();
		Cursor<?> cursor = region.cursor();
		if (!cursor.hasNext()) return null;
		long[] min = new long[numDimensions];
		long[] max = new long[numDimensions];
		cursor.fwd();
		cursor.localize(min);
		cursor.localize(max);
		while (cursor.hasNext()) {
			cursor.fwd();
			for (int i = 0; i < numDimensions; i++) {
				int pos = cursor.getIntPosition(i);
				min[i] = Math.min(min[i], pos);
				max[i] = Math.max(max[i], pos);
			}
		}
		return new FinalInterval(min, max);
	}

	public void clearLabel(Label selected) {
		Holder<Labeling> holder = model.labeling();
		Labeling labeling = holder.get();
		labeling.clearLabel(selected);
		holder.notifier().forEach(l -> l.accept(labeling));
	}

	public void setActive(Label label, boolean b) {
		label.setActive(b);
		Holder<Labeling> holder = model.labeling();
		Labeling labeling = holder.get();
		model.labeling().notifier().forEach(l -> l.accept(labeling));
	}
}
