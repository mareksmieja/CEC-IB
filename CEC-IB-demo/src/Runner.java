/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.*;
import java.util.Properties;

/**
 *
 * @author marek
 */
public class Runner {


    public static void main(String[] args) throws IOException {
        Runner runner;
        if (args.length == 0) {
            runner = new Runner("d:\\OneDrive - Uniwersytet Jagielloński\\programy\\git-repos\\CEC-IB\\CEC-IB-demo\\run.properties");
        } else {
            runner = new Runner(args[0]);

        }
    }

    public Runner(String propertiesFile) throws FileNotFoundException, IOException {

        Properties properties = new Properties();
        InputStream input = new FileInputStream(propertiesFile);
        properties.load(input);

        //data
        String dataIn = properties.getProperty("fileInData");
        //categories
        String categoriesIn = properties.getProperty("fileInCategories");
        //resultsFile
        String resultsOut = properties.getProperty("fileOutMembership");
        //clusters
        int clusters = Integer.parseInt(properties.getProperty("clustersNumber"));
        //initTimes
        int initTimes = Integer.parseInt(properties.getProperty("initTimes"));
        //beta
        double beta = Double.parseDouble(properties.getProperty("beta"));
        //beta
        double eps = Double.parseDouble(properties.getProperty("eps"));

        Hartigan hartigan;
        hartigan = new Hartigan(dataIn, categoriesIn, resultsOut, clusters, initTimes, beta, eps);
        for (int ii = 0; ii < hartigan.initTimes; ++ii) {
            hartigan.hart();
        }
        System.out.println("Minimal energy = " + hartigan.minEnergy + " optimal clusters " + hartigan.minClusters);


    }


}
