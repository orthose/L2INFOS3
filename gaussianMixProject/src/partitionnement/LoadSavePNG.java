package partitionnement;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class LoadSavePNG
{
    static BufferedImage bi;

    public static void main(String[] args) throws IOException
    {
        String path = "./";
        String imageMMS = path + "mms.png";

        // Lecture de l'image ici
        BufferedImage bui = ImageIO.read(new File(imageMMS));

        int width = bui.getWidth();
        int height = bui.getHeight();
        System.out.println("Hauteur=" + width);
        System.out.println("Largeur=" + height);

        int pixel = bui.getRGB(0, 0);
        //System.out.println("Pixel 0,0 = "+pixel);
        Color c = new Color(pixel);
        System.out.println("RGB = "+c.getRed()+" "+c.getGreen()+" "+c.getBlue());
        // Calcul des trois composant de couleurs normalisé à 1
        double[] pix = new double[3];
        pix[0] = (double) c.getRed()/255.0;
        pix[1] = (double) c.getGreen()/255.0;
        pix[2] = (double) c.getBlue()/255.0;
        System.out.println("RGB normalisé= "+pix[0]+" "+pix[1]+" "+pix[2]);

        int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);

        /** Creation du tableau **/
        Color[] tabColor= new Color[im_pixels.length];
        for(int i=0 ; i<im_pixels.length ; i++)
        tabColor[i]=new Color(im_pixels[i]);

        /** inversion des couleurs **/
        for(int i=0 ; i<tabColor.length ; i++)
        tabColor[i]=new Color(255-tabColor[i].getRed(),255-tabColor[i].getGreen(),255-tabColor[i].getBlue());

        /** sauvegarde de l'image **/
        BufferedImage bui_out = new BufferedImage(bui.getWidth(),bui.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        for(int i=0 ; i<height ; i++)
        {
        for(int j=0 ; j<width ; j++)
            bui_out.setRGB(j,i,tabColor[i*width+j].getRGB());
        }
        ImageIO.write(bui_out, "PNG", new File(path+"test.png"));

    }
}