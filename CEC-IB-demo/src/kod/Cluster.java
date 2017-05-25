package kod;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.Iterator;

import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.SparseVector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author marek
 */
public class Cluster {

    int finalNo;

    Hartigan hartigan;
    public boolean validCluster;
    public int size;
    public int tmpSize;
    public Matrix mean;
    public Matrix tmpMean;
    public Matrix cov;
    public Matrix tmpCov;
    public double energy;
    public double tmpEnergy;

    public double a, b, c;

    public int[] classesCard;// |Y_i \cap Z_j|
    public double entropyOfClassesCard; //current \sum_j |Y_i \cap Z_j| (-log |Y_i \cap Z_j|)
    public int tmpCardInd; //j such that classesCard[j] will be increased or decreased by 1
    public int tmpCard; //new value of card = classesCard[tmpCardInd] i.e. either card +1 or card - 1
    public double tmpEntropyOfClassesCard; //new  \sum_j |Y_i \cap Z_j| (-log |Y_i \cap Z_j|)
    public int tmpLabelSize;// new |X_L \cap Y_i|
    public int labelSize;//|X_L \cap Y_i|

    public double beta;

    //    Matrix correction;
    Cluster(Hartigan hartigan, boolean valid, int classesNum, double beta) {
        this.hartigan = hartigan;
        validCluster = valid;

        this.mean = new Matrix(hartigan.dim, 1);
        this.cov = new Matrix(hartigan.dim, hartigan.dim);
        this.tmpMean = new Matrix(hartigan.dim, 1);
        this.tmpCov = new Matrix(hartigan.dim, hartigan.dim);

        energy = tmpEnergy = size = tmpSize = tmpCardInd = tmpCard = labelSize = tmpLabelSize = 0;
        entropyOfClassesCard = tmpEntropyOfClassesCard = 0;

        classesCard = new int[classesNum];

        this.beta = beta;
    }

    public void getEnergyInCluster(Membership membership) {
        Matrix vect = (hartigan.data.getMatrix(membership.index, membership.index, 0, hartigan.dim - 1)).transpose();
        tmpSize = size + 1;
        tmpMean = getInMeanFast(vect);
        tmpCov = getInCovFast(vect);

        tmpCardInd = membership.classP;
        tmpCard = classesCard[tmpCardInd] + 1;
        tmpLabelSize = getLabelSize(tmpCardInd, labelSize, 1);
        tmpEntropyOfClassesCard = getEntropyOfClassesCard(tmpCard, tmpCardInd);

        tmpEnergy = getEnergy((1. * tmpSize) / hartigan.dataNum, tmpMean, tmpCov, tmpEntropyOfClassesCard, tmpLabelSize);
    }

    public Matrix getInMeanFast(Matrix vect) {
        Matrix one = (mean.times(size)).plus(vect);
        return (one.times(1.0 / (tmpSize)));
    }

    public Matrix getInCovFast(Matrix vect) {

        Matrix one = mean.minus(vect);
        return ((cov.plus((one.times(one.transpose())).times(1.0 / (tmpSize)))).times((size * 1.0) / (tmpSize)));

    }

    public double getEnergy(double prob, Matrix mean, Matrix cov, double entropyOfClassesCard, int labelSize) {
        return sh(prob) + prob * getEnergyClassic(cov) + beta * getEnergyClass(entropyOfClassesCard, prob, labelSize);
    }

    private double getEnergyClass(double entropyOfClassesCard, double prob, int labelSize) {
        if (labelSize > 0) {
            return prob * (1. / labelSize * entropyOfClassesCard + Math.log(labelSize));
        } else {
            return 0;
        }
    }

    private double getEntropyOfClassesCard(int card, int cardInd) {
        if (cardInd > 0) {
            return entropyOfClassesCard - sh(classesCard[cardInd]) + sh(card);
        } else {
            return entropyOfClassesCard;
        }
    }

    public void getEnergyOutOfCluster(Membership membership) {
        Matrix vect = (hartigan.data.getMatrix(membership.index, membership.index, 0, hartigan.dim - 1)).transpose();
        tmpSize = size - 1;
        tmpMean = getOutMeanFast(vect);
        tmpCov = getOutCovFast(vect);

        tmpCardInd = membership.classP;
        tmpCard = classesCard[tmpCardInd] - 1;
        tmpLabelSize = getLabelSize(tmpCardInd, labelSize, -1);
        tmpEntropyOfClassesCard = getEntropyOfClassesCard(tmpCard, tmpCardInd);

        tmpEnergy = getEnergy((1. * tmpSize) / hartigan.dataNum, tmpMean, tmpCov, tmpEntropyOfClassesCard, tmpLabelSize);

    }

    public Matrix getOutMeanFast(Matrix vect) {
        Matrix one = (mean.times(size)).minus(vect);
        return one.times(1. / tmpSize);
    }

    public Matrix getOutCovFast(Matrix vect) {

        Matrix one = mean.minus(vect);
        return ((cov.minus((one.times(one.transpose())).times(1.0 / (tmpSize)))).times((size * 1.0) / (tmpSize)));
    }

    public double getEnergyClassic(Matrix cov) {
        if (cov.det() <= 0) {
            return Double.POSITIVE_INFINITY;
        }

        return ((hartigan.dim) / 2.) * Math.log(2 * Math.PI * Math.E) + 1.0 / 2 * Math.log(cov.det());
    }

    public void updateClusterParameters() {
        size = tmpSize;
        mean = tmpMean;
        cov = tmpCov;
        energy = tmpEnergy;
        classesCard[tmpCardInd] = tmpCard;
        entropyOfClassesCard = tmpEntropyOfClassesCard;
        labelSize = tmpLabelSize;

        a = sh(1. * size / hartigan.dataNum);
        b = 1. * size / hartigan.dataNum * getEnergyClassic(cov);
        c = getEnergyClass(entropyOfClassesCard, 1. * size / hartigan.dataNum, labelSize);
    }


    double sh(double x) {
        if (x == 0) {
            return 0;
        } else {
            return x * (-Math.log(x));
        }
    }

    private int getLabelSize(int cardInd, int labelSize, int change) {
        if (cardInd > 0) {
            return labelSize + change;
        } else {
            return labelSize;
        }
    }


}
