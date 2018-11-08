package net.imglib2.labkit.imageserver.dvid;

public class ImageId {
	private final String uuid;
	private final String dataName;

	public ImageId(String uuid, String dataName) {
		this.uuid = uuid;
		this.dataName = dataName;
	}

	public String getUuid() {
		return uuid;
	}

	public String getDataName() {
		return dataName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		ImageId imageId = (ImageId) obj;

		if (!uuid.equals(imageId.uuid)) return false;
		return dataName.equals(imageId.dataName);
	}

	@Override
	public int hashCode() {
		int result = uuid.hashCode();
		result = 31 * result + dataName.hashCode();
		return result;
	}
}
