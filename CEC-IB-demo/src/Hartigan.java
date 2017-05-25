package src;

import Jama.Matrix;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author marek
 */
public class Hartigan {

    public String fileIn;
    public String fileInClasses;
    public String fileOut;
    public int clNum;
    public int dim;
    public int dataNum;
    public String initMembership;
    public double epsNum;
    public int initTimes;
    public int classesNum;

    public List<Cluster> clusters;
    public List<Membership> memberships;
    public List<Integer> classes;
    public Matrix data;

    public double minEnergy;
    public int minClusters;
    public double beta;

    public int[] categoriesCard;


    Hartigan(String dataIn, String categoriesIn, String resultsOut, int clusters, int initTimes, double beta, double eps) throws FileNotFoundException, IOException {

        this.fileIn = dataIn;
        this.fileInClasses = categoriesIn;
        this.fileOut = resultsOut;
        this.clNum = clusters;
        this.initTimes = initTimes;
        this.beta = beta;

        this.epsNum = eps;
        initMembership = "seed";


        minEnergy = Double.MAX_VALUE;

    }


    public void hart() throws IOException {
        Pair<Double, Integer> energy = new Pair(0, 0);
        System.out.println("START");
        init();
        removeDegeneratedClusters();
        System.out.println("INIT");
        boolean switched = true;
        int iterations = 0;
        print(0);
        while (switched) {
            switched = false;
            for (int i = 0; i < dataNum; ++i) {

                Membership membership = memberships.get(i);
                double cost;
                if (!clusters.get(membership.cluster).validCluster) {
                    membership.cluster = 0;
                }
                if (membership.cluster == 0) {
                    cost = Double.POSITIVE_INFINITY;
                } else {
                    //energy after removing a point
                    clusters.get(membership.cluster).getEnergyOutOfCluster(membership);
                    cost = 0;
                }
                int newCl = membership.cluster;

                for (int j = 1; j <= clNum; ++j) {
                    if (clusters.get(j).validCluster && j != membership.cluster) {

                        clusters.get(j).getEnergyInCluster(membership);

                        double newCost = energyAfterSwitched(membership.cluster, j);

                        if (newCost < cost) {
                            newCl = j;
                            cost = newCost;
                            switched = true;
                        }

                    }
                }

                int oldCl = membership.cluster;
                if (newCl != oldCl) {

                    switchIt(membership, newCl);
                }
                //remove cluster
                if (oldCl != 0 && (clusters.get(oldCl).size < dataNum * epsNum || clusters.get(oldCl).size < dim + 2)) {
                    clusters.get(oldCl).validCluster = false;
                    clusters.get(oldCl).energy = 0;
                    clusters.get(oldCl).size = 0;
                }

            }
            ++iterations;
            energy = print(iterations);
            removeDegeneratedClusters();
        }

        if (energy.fst < minEnergy) {

            minEnergy = energy.fst;
            minClusters = energy.snd;
            System.out.println("Update membership with energy = " + minEnergy + " and clusters " + minClusters);
            InOut.writeMembership(memberships, clusters, fileOut);

        } else {
            System.out.println("No update, energy = " + energy.fst);
        }
        printLast();

    }


    private void init() throws IOException {
        data = InOut.readData(fileIn);
        dataNum = data.getRowDimension();
        dim = data.getColumnDimension();
        Pair<Map<Integer, Integer>, List<Integer>> pair = InOut.readClasses(fileInClasses);

        classes = pair.snd;
        classesNum = 0;
        for (int k : pair.fst.keySet()) {
            if (k > classesNum) {
                classesNum = k;
            }
        }
        classesNum++;
        categoriesCard = new int[classesNum];
        for (int c : pair.fst.keySet()) {
            categoriesCard[c] = pair.fst.get(c);
        }

        this.clusters = new ArrayList<>();
        clusters.add(new Cluster(this, false, classesNum, beta));
        for (int i = 1; i <= clNum; ++i) {
            clusters.add(new Cluster(this, true, classesNum, beta));
        }


        this.memberships = new ArrayList<>();
        ArrayList<Integer> centres = findMeanPlus();
        for (int i = 0; i < dataNum; ++i) {
            int argMin = 0;
            double min = Double.MAX_VALUE;
            for (int j = 0; j < clNum; ++j) {
                double actMin = dist(data.getMatrix(i, i, 0, dim - 1), data.getMatrix(centres.get(j), centres.get(j), 0, dim - 1));
                if (actMin < min) {
                    argMin = j;
                    min = actMin;
                }
            }
            Membership m = new Membership(i, argMin + 1, classes.get(i));
            memberships.add(m);
            clusters.get(m.cluster).getEnergyInCluster(m);
            clusters.get(m.cluster).updateClusterParameters();

        }

    }

    public ArrayList<Integer> findMeanPlus() {
        ArrayList<Integer> centres = new ArrayList<Integer>();
        Random r = new Random();
        int ind = r.nextInt(dataNum);
        centres.add(ind);
        ArrayList<Double> weights = new ArrayList<Double>();
        for (int i = 0; i < dataNum; ++i) {
            weights.add(new Double(0));
        }

        int argMin = 0;

        int k = 1;
        while (k <= clNum) {
            for (int i = 0; i < dataNum; ++i) {
                double minTmp = 1000000;
                for (int j = 0; j < k; ++j) {
                    minTmp = Math.min(minTmp, dist(data.getMatrix(centres.get(j), centres.get(j), 0, dim - 1), data.getMatrix(i, i, 0, dim - 1)));
                }
                if (i == 0) {
                    weights.set(i, minTmp);
                } else {
                    weights.set(i, minTmp + weights.get(i - 1));
                }
            }
            double liczba = r.nextDouble() * weights.get(dataNum - 1);
            argMin = findPoint(liczba, weights);
            if (centres.contains(argMin)) {
                continue;
            } else {
                centres.add(argMin);
                ++k;
            }
        }
        return centres;
    }

    public double dist(Matrix a, Matrix b) {
        Matrix val = a.minus(b);
        return (val.times(val.transpose())).get(0, 0);
    }

    public int findPoint(double liczba, ArrayList<Double> weights) {
        for (int i = dataNum - 1; i > 0; --i) {
            if (weights.get(i) >= liczba && weights.get(i - 1) < liczba) {
                return i;
            }
        }
        return 0;
    }



    private double energyAfterSwitched(int out, int in) {
        if (Double.isInfinite(clusters.get(out).tmpEnergy) || Double.isInfinite(clusters.get(in).tmpEnergy)
                || Double.isInfinite(clusters.get(out).energy) || Double.isInfinite(clusters.get(in).energy)) {
            return Double.MAX_VALUE;
        }

        return clusters.get(out).tmpEnergy + clusters.get(in).tmpEnergy
                - (clusters.get(out).energy + clusters.get(in).energy);

    }

    public void switchIt(Membership membership, int newCl) {
        clusters.get(membership.cluster).updateClusterParameters();
        clusters.get(newCl).updateClusterParameters();
        membership.cluster = newCl;
    }

    private Pair<Double, Integer> print(int it) {
        double e = 0;
        int cl = 0;
        for (int i = 0; i <= clNum; ++i) {
            if (clusters.get(i).validCluster) {
                ++cl;
                e += clusters.get(i).energy;
            }
        }
        System.out.println("it=" + it + ", energy=" + e + ", clusters=" + cl);
        return new Pair(e, cl);
    }

    private void printLast() {
        double e = 0;
        int cl = 0;
        System.out.format("%8s%16s%16s%16s", "cluster", "H(Yi)", "H(Yi||fi)", "H(Z|Yi)");
        System.out.println();
        for (int i = 0; i <= clNum; ++i) {
            if (clusters.get(i).validCluster) {
                ++cl;
                e += clusters.get(i).energy;
                System.out.format("%8d%16f%16f%16f", i, clusters.get(i).a, clusters.get(i).b, clusters.get(i).c);
                System.out.println();
            }
        }

    }

    void removeDegeneratedClusters() {
        for (Cluster cl : clusters) {
            if (cl.validCluster && cl.cov.det() <= 0) {
                cl.validCluster = false;
                cl.energy = 0;
                cl.size = 0;
            }
        }
    }


}
