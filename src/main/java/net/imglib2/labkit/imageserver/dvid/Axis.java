package net.imglib2.labkit.imageserver.dvid;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Axis {
	private final String label;
	private final double resolution;
	private final Unit unit;
	private final long size;
	private final long offset;

	public Axis(String label, double resolution, Unit unit, long size, long offset) {
		this.label = label;
		this.resolution = resolution;
		this.unit = unit;
		this.size = size;
		this.offset = offset;
	}

	public static Axis create(String label, long size) {
		return new Axis(label, 1.0, Unit.PIXEL, size, 0);
	}

	@JsonProperty("Label")
	public String getLabel() {
		return label;
	}

	@JsonProperty("Resolution")
	public double getResolution() {
		return resolution;
	}

	@JsonProperty("Unit")
	public Unit getUnit() {
		return unit;
	}

	@JsonProperty("Size")
	public long getSize() {
		return size;
	}

	@JsonProperty("Offset")
	public long getOffset() {
		return offset;
	}

}
