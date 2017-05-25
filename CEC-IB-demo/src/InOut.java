package src;

import Jama.Matrix;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marek
 */
public class InOut {

    static Matrix readData(String fileIn) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileIn));
        String line = br.readLine();
        String[] st = line.split(" ");
        int dataNum = Integer.parseInt(st[0]);
        int dim = Integer.parseInt(st[1]);
        Matrix matrix = new Matrix(dataNum, dim);
        for (int i = 0; i < dataNum; ++i) {
            line = br.readLine();
            st = line.split(" ");
            for (int j = 0; j < dim; ++j) {
                matrix.set(i, j, Double.parseDouble(st[j]));
            }
        }
        br.close();
        return matrix;
    }

    static Pair<Map<Integer, Integer>, List<Integer>> readClasses(String initMembership) throws IOException {
        List<Integer> memberships = new ArrayList<>();
        Map<Integer, Integer> cardinalities = new HashMap<Integer, Integer>();

        BufferedReader br = new BufferedReader(new FileReader(initMembership));
        String line = br.readLine();
        int dataNum = Integer.parseInt(line);
        for (int i = 0; i < dataNum; ++i) {
            line = br.readLine();
            int category = Integer.parseInt(line);
            memberships.add(category);
            if(cardinalities.containsKey(category)){
               cardinalities.put(category, cardinalities.get(category) + 1);
            } else{
                cardinalities.put(category, 1);
            }
        }
        br.close();

        return new Pair(cardinalities, memberships);
    }

    public static void writeMembership(List<Membership> membership, List<Cluster> clusters, String file) throws IOException {

        TreeMap<Integer, Integer> kolejnosc = new TreeMap<Integer, Integer>();
        int kol = 1;
        for (Membership m : membership) {

            if (!kolejnosc.containsKey(m.cluster)) {
                clusters.get(m.cluster).finalNo = kol;
                kolejnosc.put(m.cluster, kol);
                ++kol;
            }
        }

        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (Membership m : membership) {
            indices.add(0);
        }

        int kk = 0;
        for (Membership m : membership) {
            indices.set(kk, kolejnosc.get(m.cluster));;
            ++kk;
        }

        FileWriter fw = null;
        fw = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fw);
        out.write(membership.size() + "\n");
        for (int j = 0; j < indices.size(); ++j) {
            //for (Integer i : indices) {
            out.write(indices.get(j).toString() + "\n");
            out.flush();
        }

        fw.close();
    }


    
}
