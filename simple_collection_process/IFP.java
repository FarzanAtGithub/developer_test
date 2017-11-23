
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Administrator
 */
public class IFP {

    final static String BLOG_URL = "http://ifpartners.com/cut-the-wire/";
    final static String HEADER = "DateTime,Article Title,Autor,Banner Image, Content Body HTML";
    private final static int BUFFER_SIZE = 8 * 1024;
    final static String CSV_FILE_NAME = "results.csv";
    
    private int numOfArticles;  
    private PrintWriter out;

    IFP(int numOfArticles) {
        this.numOfArticles = numOfArticles;
    }
    
    public static void main(String[] args) throws IOException {
        IFP ifp = new IFP(6);
        ifp.initialise();
        ifp.start();
    }

    /**
     * Instantiates a PrintWriter object to the given CSV file.
     */
    private void initialise() throws IOException {
        //Initialise PrintWriter
        try {
            out = new PrintWriter(new File(CSV_FILE_NAME), "UTF8");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            if (out != null) {
                out.close();
            }
            System.exit(1);
        }
    }


    private void start() throws IOException {
        //Extract Info
        List<Elements> elements = extractHTMLInfo(BLOG_URL);
        System.out.println("Step#1 Completed!\nInformation Successfully Extracted from the blog!\n");

        //Write to CSV File & Download/Save Banner Image
        copyToCSV(elements, numOfArticles, out);
        System.out.println("Step#2 Completed!\nInformation Successfully Written to the CSV file!\n");

        //Save banner images of each article to the local disk (root directory where the code resides
        saveImage(elements, numOfArticles);
        System.out.println("Step#3 Completed!\nImages Successfully Downloaded & Saved to Local Disk!");

        out.close();
    }

    /**
     * Parses/processed the html data from the url, and saves the desired extracted info to a List
     * 
     * @param url String url to be parsed/processed
     * @return a List<Elements> that contains the required information extracted from the URL
     * @throws IOException 
     */
    private List<Elements> extractHTMLInfo(String url) throws IOException {

        //All required info to be collected is within the <section> tag
        Element sectionTag = Jsoup.connect(url).get().select("section").get(0);

        List<Elements> list = new ArrayList();

        //Banner img Tag
        list.add(sectionTag.select("img"));

        //TimeTag
        list.add(sectionTag.select("header").select("time"));

        //Article Title
        list.add(sectionTag.select("header").select("h2 a"));

        //Author
        list.add(sectionTag.select("header").select("p a"));

        //Content Body HTML
        list.add(sectionTag.select("article").select("div"));

        return list;
    }

    /**
     * Copies the data in the list of Elements in the desired order to a CSV file
     * 
     * @param elements List<Elements>
     * @param numOfArticles int
     * @param out PrintWriter
     */
    void copyToCSV(List<Elements> elements, int numOfArticles, PrintWriter out) {

        out.print(HEADER + "\n");

        for (int i = 0; i < numOfArticles; i++) {
            String dateTime = elements.get(1).get(i).text().substring(10);
            out.print("\"" + dateTime + "\","); 
            
            String articleTitle = elements.get(2).get(i).text();
            out.write("\"" + articleTitle + "\","); 
            
            String authorName = elements.get(3).get(i).text();
            out.print("\"" + authorName + "\","); 
            
            String bannerImage = elements.get(0).get(i).attr("src");
            out.print("\"" + bannerImage + "\","); 
            
            String bodyContentHTML = elements.get(4).get(i).html().replace('\n', ' ');
            out.write("\"" + bodyContentHTML + "\"\n"); 
        }
        out.close();
    }

    /**
     * Extracts the images href/url from List<Elements> object, downloads and saves them to the local disk
     * The original name of the files are preserved
     * 
     * @param elements List<Elements>
     * @param numOfArticles int numOfArticles specifies the articles/images to be processed
     * @throws IOException 
     */
    void saveImage(List<Elements> elements, int numOfArticles) throws IOException {
        //For simplicity, exceptions are not caught...
        URL url = null;
        HttpURLConnection conn = null;
        InputStream reader = null;
        FileOutputStream writer = null;
        byte[] buffer = null;

        for (int i = 0; i < numOfArticles; i++) {
            String urlName = elements.get(0).get(i).attr("src");
            url = new URL(urlName);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "");

            String imageName = url.getFile();
            imageName = imageName.substring(imageName.lastIndexOf("/") + 1);

            reader = conn.getInputStream();
            writer = new FileOutputStream(new File(imageName));
            buffer = new byte[BUFFER_SIZE];
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
        }
        if (writer != null) {
            writer.close();
        }
        if (reader != null) {
            reader.close();
        }
    }
}
