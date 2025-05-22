package com.belman.domain.photo;

import com.belman.domain.common.base.ValueObject;

import java.util.Objects;

/**
 * Value object representing metadata about a photo's quality and technical characteristics.
 * This includes information such as resolution, file size, and image format.
 */
public final class PhotoMetadata implements ValueObject {

    private final int width;
    private final int height;
    private final long fileSize;
    private final String imageFormat;
    private final String colorSpace;
    private final Integer dpi;

    /**
     * Creates a new PhotoMetadata with the specified values.
     *
     * @param width       the width of the image in pixels
     * @param height      the height of the image in pixels
     * @param fileSize    the size of the image file in bytes
     * @param imageFormat the format of the image (e.g., "JPEG", "PNG")
     * @param colorSpace  the color space of the image (e.g., "RGB", "CMYK")
     * @param dpi         the resolution of the image in dots per inch (optional)
     * @throws IllegalArgumentException if any of the required parameters are invalid
     */
    public PhotoMetadata(int width, int height, long fileSize, String imageFormat, String colorSpace, Integer dpi) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than zero");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero");
        }
        if (fileSize <= 0) {
            throw new IllegalArgumentException("File size must be greater than zero");
        }
        if (imageFormat == null || imageFormat.isBlank()) {
            throw new IllegalArgumentException("Image format must not be null or blank");
        }
        if (colorSpace == null || colorSpace.isBlank()) {
            throw new IllegalArgumentException("Color space must not be null or blank");
        }
        if (dpi != null && dpi <= 0) {
            throw new IllegalArgumentException("DPI must be greater than zero if provided");
        }

        this.width = width;
        this.height = height;
        this.fileSize = fileSize;
        this.imageFormat = imageFormat;
        this.colorSpace = colorSpace;
        this.dpi = dpi;
    }

    /**
     * Returns the width of the image in pixels.
     *
     * @return the width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the image in pixels.
     *
     * @return the height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the size of the image file in bytes.
     *
     * @return the file size in bytes
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Returns the format of the image.
     *
     * @return the image format (e.g., "JPEG", "PNG")
     */
    public String getImageFormat() {
        return imageFormat;
    }

    /**
     * Returns the color space of the image.
     *
     * @return the color space (e.g., "RGB", "CMYK")
     */
    public String getColorSpace() {
        return colorSpace;
    }

    /**
     * Returns the resolution of the image in dots per inch, if available.
     *
     * @return the DPI value, or null if not available
     */
    public Integer getDpi() {
        return dpi;
    }

    /**
     * Returns the resolution of the image as a string (e.g., "1920x1080").
     *
     * @return the resolution as a string
     */
    public String getResolution() {
        return width + "x" + height;
    }

    /**
     * Returns the megapixel count of the image.
     *
     * @return the megapixel count
     */
    public double getMegapixels() {
        return (width * height) / 1_000_000.0;
    }

    /**
     * Returns the aspect ratio of the image.
     *
     * @return the aspect ratio as a double
     */
    public double getAspectRatio() {
        return (double) width / height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoMetadata that = (PhotoMetadata) o;
        return width == that.width &&
               height == that.height &&
               fileSize == that.fileSize &&
               imageFormat.equals(that.imageFormat) &&
               colorSpace.equals(that.colorSpace) &&
               Objects.equals(dpi, that.dpi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, fileSize, imageFormat, colorSpace, dpi);
    }

    @Override
    public String toString() {
        return "PhotoMetadata{" +
               "resolution=" + getResolution() +
               ", fileSize=" + fileSize +
               ", imageFormat='" + imageFormat + '\'' +
               ", colorSpace='" + colorSpace + '\'' +
               ", dpi=" + dpi +
               '}';
    }
}