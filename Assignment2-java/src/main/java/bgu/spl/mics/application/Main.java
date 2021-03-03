package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import java.util.concurrent.CountDownLatch;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

    public static void main(String[] args) {
        //InputParser
        String inputPath = args[0];
        String outputPath =  args[1];
        Input input = null;
        try {
            input = JsonInputReader.getInputFromJson(inputPath);
        } catch (IOException e) {
        }

        //ApplicationCreation
        MessageBusImpl.getInstance();
        Ewoks ewoksInstance = Ewoks.getInstance();
        ewoksInstance.init(input.getEwoks());
        Diary diary = Diary.getInstance();
        LeiaMicroservice Leia = new LeiaMicroservice(input.getAttacks());
        R2D2Microservice R2D2 = new R2D2Microservice(input.getR2D2());
        HanSoloMicroservice HanSolo = new HanSoloMicroservice();
        C3POMicroservice C3PO = new C3POMicroservice();
        LandoMicroservice Lando = new LandoMicroservice(input.getLando());

        //Set CountDownLatch for all
        CountDownLatch initCount = new CountDownLatch(4);
        Leia.setInitializationCount(initCount);
        R2D2.setInitializationCount(initCount);
        HanSolo.setInitializationCount(initCount);
        C3PO.setInitializationCount(initCount);
        Lando.setInitializationCount(initCount);

        //Create Threads and start simulation
        Thread LeiaT = new Thread(Leia);
        Thread R2D2T = new Thread(R2D2);
        Thread HanSoloT = new Thread(HanSolo);
        Thread C3POT = new Thread(C3PO);
        Thread LandoT = new Thread(Lando);

        HanSoloT.start();
        C3POT.start();
        R2D2T.start();
        LandoT.start();
        LeiaT.start();


        //Wait until all finished




        try {
            HanSoloT.join();
        } catch (InterruptedException e) {
        }

        try {
            C3POT.join();
        } catch (InterruptedException e) {
        }

        try {
            LandoT.join();
        } catch (InterruptedException e) {
        }
        try {
            R2D2T.join();
        } catch (InterruptedException e) {
        }
        try {
            LeiaT.join();
        } catch (InterruptedException e) {
        }


        //Create output file
        Writer writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get(outputPath));
        } catch (IOException e) {
        }
        Gson gson = new Gson();
        gson.toJson(diary, writer);
        try {
            writer.close();
        } catch (IOException e) {
        }


    }
}

