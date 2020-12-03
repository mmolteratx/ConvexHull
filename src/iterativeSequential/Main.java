
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;
import java.util.Random;


public class Main {

    static class RunQH_2 implements Runnable{
        QuarterHull qh;
        int startIdx1, startIdx2;
        int endIdx1, endIdx2;
        int quad1, quad2;

        public RunQH_2(QuarterHull qh_, int startIdx1_, int endIdx1_, int quad1_, int startIdx2_, int endIdx2_, int quad2_){
            qh = qh_;
            startIdx1 = startIdx1_;
            endIdx1 = endIdx1_;
            quad1 = quad1_;
            startIdx2 = startIdx2_;
            endIdx2 = endIdx2_;
            quad2 = quad2_;
        }

        public void run() {
            qh.QuarterHull(startIdx1, endIdx1, quad1);
            qh.QuarterHull(startIdx2, endIdx2, quad2);
        }

    }


    static class RunQH implements Runnable{
        QuarterHull qh;
        int startIdx;
        int endIdx;
        int quad;

        public RunQH(QuarterHull qh_, int startIdx_, int endIdx_, int quad_){
            qh = qh_;
            startIdx = startIdx_;
            endIdx = endIdx_;
            quad = quad_;
        }

        public void run() {
            qh.QuarterHull(startIdx, endIdx, quad);
        }

    }

    static class RunSetPtsAndAngle implements Runnable{
        QuarterHull qh;
        double[][] pts;
        private ThreadLocal <Integer> threadId = new ThreadLocal<Integer>();
        int tID;
        int numThreads;

        public RunSetPtsAndAngle(QuarterHull qh_, double[][] pts_, int tid_, int numThreads_){
            qh = qh_;
            pts = pts_;
            tID = tid_;
            numThreads = numThreads_;
        }

        public void run() {
            try {
                // Store set of pts, and calculate angle for each
                qh.SetPtsAndAngle(pts, tID, numThreads);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) throws InterruptedException {
        // Define some initial parameters
        int numPts = 200000;
        double[][] pts = new double[numPts][3];

        // Generate random set of points: [x,y,angle]
        // We initialize the angle as -1 as a marker of not calculated yet
        Random rand = new Random();
        for (int ii = 0; ii < numPts; ii++){
            pts[ii][0] = rand.nextDouble()*2 - 1;
            pts[ii][1] = rand.nextDouble()*2 - 1;
            pts[ii][2] = -1;
            // System.out.println("x: " + pts[ii][0] + " y: " +  pts[ii][1] + " z: " + pts[ii][2]);
        }

        // Get class that will get the convex hull
        QuarterHull qh = new QuarterHull();
        long startTime = System.nanoTime();

        int numThreadsTemp = 7;
        ExecutorService threadPool = Executors.newFixedThreadPool(numThreadsTemp);
        for (int ii = 1; ii <= numThreadsTemp; ii++) {
            threadPool.execute(new RunSetPtsAndAngle(qh, pts, ii, numThreadsTemp));
        }
        threadPool.shutdownNow();

        // Wait for all threads to finish execution
        try {
            if (!threadPool.awaitTermination(600, TimeUnit.SECONDS)) {
                System.err.println("Threads didn't finish in 60 seconds!");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            threadPool.shutdownNow();
            System.err.println (ie);
        }

        // Order Pts
        qh.OrderPts(1, 0, qh.numPts-1);
        /*for (int ii=0; ii<=qh.numPts-1; ii++) {
            System.out.println("Pts-post: " + qh.pts[ii][0] + " " + qh.pts[ii][1] + " " + qh.pts[ii][2]);
        }*/

        // Identify pts with min and max locations in x and y
        qh.IDMinMax();
        System.out.println("minMax: " + qh.minX + " " + qh.maxX + " " + qh.minY + " " + qh.maxY);
        System.out.println("minMaxIdx: " + qh.minXIdx + " " + qh.maxXIdx + " " + qh.minYIdx + " " + qh.maxYIdx);

        // Quarter Hall part +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        /////////// 1 Thread -------------------------------------------------------------------------------------------
        long startTimeQH = System.nanoTime();
        qh.QuarterHull(qh.maxXIdx, qh.maxYIdx, 1);
        qh.QuarterHull(qh.maxYIdx, qh.minXIdx, 2);
        qh.QuarterHull(qh.minXIdx, qh.minYIdx, 3);
        qh.QuarterHull(qh.minYIdx, qh.maxXIdx, 4);

        long qhTime = (System.nanoTime() - startTimeQH) / 1000;
        System.out.println("QH-1 Time: " + qhTime + " microseconds");

        /////////// 2 Threads ------------------------------------------------------------------------------------------
        startTimeQH = System.nanoTime();
        ExecutorService threadPool_2 = Executors.newFixedThreadPool(2);
        threadPool_2.execute(new RunQH_2(qh, qh.maxXIdx, qh.maxYIdx, 1, qh.maxYIdx, qh.minXIdx, 2));
        threadPool_2.execute(new RunQH_2(qh, qh.minXIdx, qh.minYIdx, 3, qh.minYIdx, qh.maxXIdx, 4));
        threadPool_2.shutdownNow();

        // Wait for all threads to finish execution
        try {
            if (!threadPool_2.awaitTermination(600, TimeUnit.SECONDS)) {
                System.err.println("Threads didn't finish in 60 seconds!");
            }
        } catch (InterruptedException ie) {
            System.err.println (ie);
        }
        qhTime = (System.nanoTime() - startTimeQH) / 1000;
        System.out.println("QH-2 Time: " + qhTime + " microseconds");

        /////////// 4 Threads ------------------------------------------------------------------------------------------
        startTimeQH = System.nanoTime();
        ExecutorService threadPool_4 = Executors.newFixedThreadPool(4);
        threadPool_4.execute(new RunQH(qh, qh.maxXIdx, qh.maxYIdx, 1));
        threadPool_4.execute(new RunQH(qh, qh.maxYIdx, qh.minXIdx, 2));
        threadPool_4.execute(new RunQH(qh, qh.minXIdx, qh.minYIdx, 3));
        threadPool_4.execute(new RunQH(qh, qh.minYIdx, qh.maxXIdx, 4));
        threadPool_4.shutdownNow();

        // Wait for all threads to finish execution
        try {
            if (!threadPool_4.awaitTermination(600, TimeUnit.SECONDS)) {
                System.err.println("Threads didn't finish in 60 seconds!");
            }
        } catch (InterruptedException ie) {
            System.err.println (ie);
        }
        qhTime = (System.nanoTime() - startTimeQH) / 1000;
        System.out.println("QH-4 Time: " + qhTime + " microseconds");

        /////////// 8 Threads ------------------------------------------------------------------------------------------
        startTimeQH = System.nanoTime();
        ExecutorService threadPool_8 = Executors.newFixedThreadPool(8);
        int midPtIdx1 = qh.GetMidPtIdx(qh.maxXIdx, qh.maxYIdx);
        threadPool_8.execute(new RunQH(qh, qh.maxXIdx, midPtIdx1, 1));
        threadPool_8.execute(new RunQH(qh, midPtIdx1, qh.maxYIdx, 1));

        int midPtIdx2 = qh.GetMidPtIdx(qh.maxYIdx, qh.minXIdx);
        threadPool_8.execute(new RunQH(qh, qh.maxYIdx, midPtIdx2, 2));
        threadPool_8.execute(new RunQH(qh, midPtIdx2, qh.minXIdx, 2));

        int midPtIdx3 = qh.GetMidPtIdx(qh.minXIdx, qh.minYIdx);
        threadPool_8.execute(new RunQH(qh, qh.minXIdx, midPtIdx3, 3));
        threadPool_8.execute(new RunQH(qh, midPtIdx3, qh.minYIdx, 3));

        int midPtIdx4 = qh.GetMidPtIdx(qh.minYIdx, qh.maxXIdx);
        threadPool_8.execute(new RunQH(qh, qh.minYIdx, midPtIdx4, 4));
        threadPool_8.execute(new RunQH(qh, midPtIdx4, qh.maxXIdx, 4));

        threadPool_8.shutdownNow();

        // Wait for all threads to finish execution
        try {
            if (!threadPool_4.awaitTermination(600, TimeUnit.SECONDS)) {
                System.err.println("Threads didn't finish in 60 seconds!");
            }
        } catch (InterruptedException ie) {
            System.err.println (ie);
        }

        // Merge Hulls
        qh.MergeHulls(midPtIdx1, 1);
        qh.MergeHulls(midPtIdx2, 2);
        qh.MergeHulls(midPtIdx3, 3);
        qh.MergeHulls(midPtIdx4, 4);

        qhTime = (System.nanoTime() - startTimeQH) / 1000;
        System.out.println("QH-8 Time: " + qhTime + " microseconds");


        //}else if (numThreads == 8) {
        //    qh.MergeHulls();
        //}



        /*for (int ii=0; ii<=qh.numPts-1; ii++) {
            if (qh.ptsInHull[ii]) {
                System.out.println("QH: " + qh.pts[ii][0] + " " + qh.pts[ii][1] + " " + qh.pts[ii][2]);
            }
        }

        long totalTime = (System.nanoTime() - startTime) / 1000000;
        System.out.println("Time: " + totalTime + " milliseconds");*/
    }
}
