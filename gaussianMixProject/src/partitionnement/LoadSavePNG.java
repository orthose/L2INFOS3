package partitionnement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class LoadSavePNG {
	static BufferedImage bi;

	public static Color[] loadPNG(BufferedImage bui) throws IOException {

		int width = bui.getWidth();
		int height = bui.getHeight();
		System.out.println("Hauteur=" + height);
		System.out.println("Largeur=" + width);

		int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);

		/** Creation du tableau **/
		Color[] tabColor = new Color[im_pixels.length];
		for (int i = 0; i < im_pixels.length; i++) {
			tabColor[i] = new Color(im_pixels[i]);
		}

		return tabColor;
	}

	public static double[][] normaliseColor(Color[] tabColor) {
		double[][] normalizedColor = new double[tabColor.length][3];
		for (int i = 0; i < tabColor.length; i++) {
			normalizedColor[i][0] = tabColor[i].getRed() / 255f;
			normalizedColor[i][1] = tabColor[i].getGreen() / 255f;
			normalizedColor[i][2] = tabColor[i].getBlue() / 255f;
		}

		return normalizedColor;
	}

	public static Color/*[]*/ denormaliseColor(double[]/*[]*/ normalizedColor) {
		Color/*[]*/ denormalizedColor /*= new Color[normalizedColor.length]*/;
		//for (int i = 0; i < normalizedColor.length; i++) {
			denormalizedColor/*[i]*/ = new Color((int) (normalizedColor/*[i]*/[0] * 255), (int) (normalizedColor/*[i]*/[1] * 255), (int) (normalizedColor/*[i]*/[2] * 255));
		//}
		return denormalizedColor;
	}

	public static void main(String[] args) throws IOException {

		String path = "./src/";
		String imageMMS = path + "mms.png";
		// Lecture de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		Color[] tabColor = loadPNG(bui);
		double[][] normalizedColor = normaliseColor(tabColor);

		// Question Bonus: Compression
		Compression(bui.getWidth(), bui.getHeight(), path);

		/** inversion des couleurs **/
		/*
		 * for(int i=0 ; i<tabColor.length ; i++) tabColor[i]=new
		 * Color(255-tabColor[i].getRed(),255-tabColor[i].getGreen(),255-tabColor[i].
		 * getBlue());
		 */

		/** sauvegarde de l'image **/
		/*
		 * BufferedImage bui_out = new
		 * BufferedImage(bui.getWidth(),bui.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		 * for(int i=0 ; i<height ; i++) { for(int j=0 ; j<width ; j++)
		 * bui_out.setRGB(j,i,tabColor[i*width+j].getRGB()); } ImageIO.write(bui_out,
		 * "PNG", new File(path+"test.png"));
		 */

	}

	// QUESTION COMPRESSION
	public static void Compression(int width, int height, String path) throws IOException {
		
		/** sauvegarde de l'image **/
		BufferedImage bui_out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//double[] centerColor[3] coord d'un centre du pixel (i * width + j)
				double[] centerColor = new double[3]; // = Focntion qui renvoie els coord normalisé d'un centre 
				bui_out.setRGB(j, i, denormaliseColor(centerColor).getRGB());
			}
		}
		ImageIO.write(bui_out, "PNG", new File(path + "test.png"));

	}
}