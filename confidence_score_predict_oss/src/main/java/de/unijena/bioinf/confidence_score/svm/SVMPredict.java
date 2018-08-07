package de.unijena.bioinf.confidence_score.svm;


import de.bwaldvogel.liblinear.Model;
import de.unijena.bioinf.fingerid.Train;

import java.io.File;

/**
 * Created by martin on 21.06.18.
 */
public class SVMPredict {

SVMUtils utils = new SVMUtils();
LibLinearImpl impl =  new LibLinearImpl();
LibLinearImpl.svm_model model;


    public double[] predict_confidence(double[][] features,TrainedSVM svm){//TODO: frag energies?


        double[] scores =  new double[features.length];

        try {

//TODO: assert only normalized stuff comes in here

            LinearSVMPredictor predictor=  new LinearSVMPredictor(svm.weights,0);

            for(int i=0;i<features.length;i++) {

                if(svm.score_shift!=0 && svm.bogusDist!=null) {
                    scores[i] = 1 - (svm.bogusDist.cumulativeProbability(predictor.score(features[i]) + svm.score_shift));
                }else{
                    scores[i] = predictor.score(features[i]);
                    System.out.println("no bogus hit distribution found");

                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return scores;










    }

    public boolean[] predict_classes(double[][] features, TrainedSVM svm){

        boolean[] classes =  new boolean[features.length];

        try {


            //TODO need scales

            utils.standardize_features(features,svm.scales);
            utils.normalize_features(features,svm.scales);

            LinearSVMPredictor predicor=  new LinearSVMPredictor(svm.weights,0);

            for(int i=0;i<features.length;i++) {

                classes[i]=(predicor.predict(features[i]));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return classes;




    }




}
