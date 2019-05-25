/* 
 * Class: DarkRoomAlgorithms
 * --------------------------
 * This class provides the methods of image processing for
 * DarkRoom.java
 * 
 */



import acm.graphics.*;

public class DarkRoomAlgorithms implements DarkRoomAlgorithmsInterface {
	private static final int NEGATIVE = 255;
	
	/**
	 * Method: rotateLeft
	 * --------------------
	 * This method will rotate the image in counterclockwise
	 * direction.
	 */
	public GImage rotateLeft(GImage source) {
		int[][] pixels = source.getPixelArray();
		int imgRows = numRows(pixels);
		int imgCols = numCols(pixels);
		int[][] newPixels = new int[imgCols][imgRows];
		
		for (int r = 0; r < imgRows; r++) {
			for (int c = 0; c < imgCols; c++) {
				newPixels[imgCols - c - 1][r] = pixels[r][c];
			}
		}
		return new GImage(newPixels);
	}
	
	/**
	 * Method: rotateRight
	 * --------------------
	 * This method will rotate the image in clockwise direction.
	 */
	public GImage rotateRight(GImage source) {
		int[][] pixels = source.getPixelArray();
		int imgRows = numRows(pixels);
		int imgCols = numCols(pixels);
		int[][] newPixels = new int[imgCols][imgRows];
		
		for (int r = 0; r < imgRows; r++) {
			for (int c = 0; c < imgCols; c++) {
				newPixels[c][imgRows - r - 1] = pixels[r][c];
			}
		}
		return new GImage(newPixels);
	}

	/**
	 * Method: flipHorizontal
	 * ----------------------
	 * This method will flip the image horizontally.
	 */
	public GImage flipHorizontal(GImage source) {
		int[][] pixels = source.getPixelArray();
		int imgRows = numRows(pixels);
		int imgCols = numCols(pixels);
		
		for (int r = 0; r < imgRows; r++) {
			for (int p1 = 0; p1 < imgCols/2; p1++) {
				int p2 = imgCols - p1 - 1;
				int temp = pixels[r][p1];
				pixels[r][p1] = pixels[r][p2];
				pixels[r][p2] = temp;
			}
		}
		return new GImage(pixels);
	}
	
	/**
	 * Method: negative
	 * -----------------
	 * This method will turn the image into negative color.
	 */
	public GImage negative(GImage source) {
		int[][] pixels = source.getPixelArray();
		int imgRows = numRows(pixels);
		int imgCols = numCols(pixels);
		
		
		for (int r = 0; r < imgRows; r++) {
			for (int c = 0; c < imgCols; c++) {
			int newRed = NEGATIVE - GImage.getRed(pixels[r][c]);
			int newGreen = NEGATIVE - GImage.getGreen(pixels[r][c]);
			int newBlue = NEGATIVE - GImage.getBlue(pixels[r][c]);
			
			pixels[r][c] = GImage.createRGBPixel(newRed, newGreen, newBlue);
			}
		}
		return new GImage(pixels);
	}
	
	/**
	 * Method: greenScreen
	 * --------------------
	 * This method will remove green color from the image to
	 * isolate the subject.
	 */
	public GImage greenScreen(GImage source) {
		int[][] pixels = source.getPixelArray();
		int imgRows = numRows(pixels);
		int imgCols = numCols(pixels);
		
		for (int r = 0; r < imgRows; r++) {
			for (int c = 0; c < imgCols; c++) {
				int red = GImage.getRed(pixels[r][c]);
				int green = GImage.getGreen(pixels[r][c]);
				int blue = GImage.getBlue(pixels[r][c]);
				int bigger = Math.max(red, blue);
				
				if (green > 2 * bigger) {
					pixels[r][c] = GImage.createRGBPixel(red, green, blue, 0);
				}
			}
		}
		return new GImage(pixels);
	}
	
	/**
	 * Method: blur
	 * -------------
	 * This method will blur the image.
	 */
	public GImage blur(GImage source) {
		int[][] pixels = source.getPixelArray();
		int imgRows = numRows(pixels);
		int imgCols = numCols(pixels);
		int[][] newPixels = new int[imgRows][imgCols];
		int newRed = 0;
		int newGreen = 0;
		int newBlue = 0;
		
		for (int r = 0; r < imgRows - 1; r++) {
			for (int c = 0; c < imgCols - 1; c++) {
				if (r != 0 && c != 0 && r != imgRows - 1 && c != imgCols - 1) {
					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							int red = GImage.getRed(pixels[r-1+i][c-1+j]);
							int green = GImage.getGreen(pixels[r-1+i][c-1+j]);
							int blue = GImage.getBlue(pixels[r-1+i][c-1+j]);
							newRed += red;
							newGreen += green;
							newBlue += blue;
						}
					}
					newRed = newRed/10;
					newGreen = newGreen/10;
					newBlue = newBlue/10;
					newPixels[r][c] = GImage.createRGBPixel(newRed, newGreen, newBlue);
				}
				
			}
		}
		return new GImage(newPixels);
	}

	/**
	 * Method: crop
	 * --------------
	 * This method will crop the selected image.
	 */
	public GImage crop(GImage source, int cropX, int cropY, int cropWidth, int cropHeight) {
		int[][] pixels = source.getPixelArray();
		int[][] newPixels = new int[cropHeight][cropWidth];
		
		for (int r = cropY; r < cropY + cropHeight; r++) {
			for (int c = cropX; c < cropX + cropWidth; c++) {
				newPixels[r - cropY][c - cropX] = pixels[r][c];
			}
		}
		return new GImage(newPixels);
	}

	/**
	 * Method: equalize
	 * ----------------
	 * This method will enhance the image in grayscale by spreading the
	 * Luminosity level of each pixel. 
	 */
	public GImage equalize(GImage source) {
		int[] luminosityHistogram = computeLuminosityHistogram(source);
		int[] cumulativeLuminosityHistogram = 
				computeCumulativeLuminosityHistogram(luminosityHistogram);
		
		return enhancedImage(cumulativeLuminosityHistogram, source);
	}
	
	/**
	 * Method: computeLuminosityHistogram
	 * -----------------------------------
	 * This method will create the Luminosity Histogram.
	 * This histogram consist of an array of 256 length, which
	 * represent the level of luminosity from 0 until 255.
	 * Each index stores the number of pixels with that luminosity
	 * level.
	 * @param source
	 * @return
	 */
	private int[] computeLuminosityHistogram(GImage source) {
		int pixels[][] = source.getPixelArray();
		int imgRows = numRows(pixels);
		int imgCols = numCols(pixels);
		int[] histogram = new int[256];
		
		for (int r = 0; r < imgRows; r++) {
			for (int c = 0; c < imgCols; c++) {
				int red = GImage.getRed(pixels[r][c]);
				int green = GImage.getGreen(pixels[r][c]);
				int blue = GImage.getBlue(pixels[r][c]);
				int luminosity = computeLuminosity(red, green, blue);
				histogram[luminosity-1]++;
			}
		}
		return histogram;
	}
	
	/**
	 * Method: computeCumulativeLuminosityHistogram
	 * ---------------------------------------------
	 * This method will create a Cumulative Luminosity Histogram.
	 * This histogram consist of an array of 256 length. Luminosity 
	 * level of each pixel is added from array[0] until array[255].
	 * @param histogram
	 * @return
	 */
	private int[] computeCumulativeLuminosityHistogram(int[] histogram) {
		int[] cumulativeLuminosityHistogram = new int[256];
		
		for (int i = 0; i < histogram.length; i++) {
			if (i != 0) {
				cumulativeLuminosityHistogram[i] = 
						cumulativeLuminosityHistogram[i - 1] + histogram[i];
			} else {
				cumulativeLuminosityHistogram[i] = histogram[i];
			}
		}
		return cumulativeLuminosityHistogram;
	}
	
	/**
	 * Method: enhancedImage
	 * ----------------------
	 * This method will enhance the image by spreading the luminosity
	 * level of each pixel based on the Cumulative Luminosity Histogram
	 * @param histogram
	 * @param source
	 * @return
	 */
	private GImage enhancedImage(int[] histogram, GImage source) {
		int[][] pixels = source.getPixelArray();
		int imgRows = numRows(pixels);
		int imgCols = numCols(pixels);
		int newLuminosity = 0;
		int totalPixels = imgRows * imgCols;
		
		for (int r = 0; r < imgRows; r++) {
			for (int c = 0; c < imgCols; c++) {
				int red = GImage.getRed(pixels[r][c]);
				int green = GImage.getGreen(pixels[r][c]);
				int blue = GImage.getBlue(pixels[r][c]);
				int luminosity = computeLuminosity(red, green, blue);
				
				newLuminosity = 255 * histogram[luminosity] / totalPixels;
				pixels[r][c] = GImage.createRGBPixel(newLuminosity, newLuminosity, newLuminosity);
			}
		}
		return new GImage(pixels);
	}
	
	/**
	 * Method: numRows
	 * ----------------
	 * This method will calculate the number of row of the image.
	 * @param pixels
	 * @return
	 */
	private int numRows(int[][] pixels) {
		return pixels.length;
	}
	
	/**
	 * Method: numCols
	 * ----------------
	 * This method will calculate the number of column of
	 * the image.
	 * @param pixels
	 * @return
	 */
	private int numCols(int[][] pixels) {
		return pixels[0].length;
	}
}
