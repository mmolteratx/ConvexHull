
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Math.floor;

public class QuarterHull {

    double[][] pts;
    int numPts;
    double shiftRads = 2 * Math.PI;
    boolean ptsSet = false;
    int threadsHere = 0;
    int numThreads;
    int minXIdx = 0;
    int maxXIdx = 0;
    int minYIdx = 0;
    int maxYIdx = 0;
    double minX, maxX, minY, maxY;
    boolean[] ptsInHull;

    Semaphore mutex = new Semaphore(1);

    final ReentrantLock lockExec_0 = new ReentrantLock();
    final ReentrantLock lockExec = new ReentrantLock();
    final Condition doneAngle = lockExec.newCondition();


    public void SetPtsAndAngle(double[][] pts_, int tid, int numThreads_) throws InterruptedException {

        numThreads = numThreads_;

        lockExec_0.lock();
        if (!ptsSet){
            pts = pts_;
            numPts = pts.length;
            System.out.println("Setting up pts  " + tid);
            ptsSet = true;
        }
        lockExec_0.unlock();

        // Initialize ptsInHull
        ptsInHull = new boolean[numPts];

        // Calculate angle
        for (int ii = tid-1; ii < numPts; ii += numThreads){

            pts[ii][2] = Math.atan2(pts[ii][1], pts[ii][0]);
            if (pts[ii][2] < 0) {
                pts[ii][2] += shiftRads;
            }
        }

    }



    public void OrderPts(int tid, int startIdx, int endIdx) throws InterruptedException {
        //System.out.println("Inside Order  " + tid);

        // Implementation of quick sort
        double[] tempFlip = new double[3];
        int idxFromLeft = startIdx;
        int idxFromRight = endIdx;


        // Define pivot
        int pivotIdx = (int) (startIdx + floor( ((double)endIdx - (double)startIdx) / 2));
        double pivot = pts[pivotIdx][2];
        //System.out.println("Pivot: " + pivotIdx);
        //for (int ii=0; ii<=numPts-1; ii++) {
        //    System.out.println("Pts-post: " + pts[ii][0] + " " + pts[ii][1] + " " + pts[ii][2]);
        //}

        // Move pivot to endIdx
        for (int ii = 0; ii <= 2; ii++) {
            tempFlip[ii] = pts[pivotIdx][ii];
            pts[pivotIdx][ii] = pts[endIdx][ii];
            pts[endIdx][ii] = tempFlip[ii];
        }

        // Iterate over list to get pivot in right place
        while (idxFromLeft < idxFromRight) {

            // Search for item > pivot from left
            idxFromLeft = endIdx;
            for (int ii = startIdx; ii <= endIdx; ii++) {
                if (pts[ii][2] > pivot) {
                    idxFromLeft = ii;
                    break;
                }
            }

            // Search for iten < pivot from right
            idxFromRight = startIdx;
            for (int ii = endIdx; ii >= startIdx; ii--) {
                if (pts[ii][2] < pivot) {
                    idxFromRight = ii;
                    break;
                }
            }

            // Switch elements
            if (idxFromLeft < idxFromRight){
                for (int ii = 0; ii <= 2; ii++) {
                    tempFlip[ii] = pts[idxFromLeft][ii];
                    pts[idxFromLeft][ii] = pts[idxFromRight][ii];
                    pts[idxFromRight][ii] = tempFlip[ii];
                }
            }
        }

        // Set pivot in right position, swap with idxFromLeft, remember we moved pivot to end
        for (int ii = 0; ii <= 2; ii++) {
            tempFlip[ii] = pts[idxFromLeft][ii];
            pts[idxFromLeft][ii] = pts[endIdx][ii];
            pts[endIdx][ii] = tempFlip[ii];
        }

        // Call quick sort again if needed
        if (idxFromLeft >= startIdx + 2){
            this.OrderPts(tid, startIdx, idxFromLeft-1);
        }

        if (idxFromLeft <= endIdx - 2){
            this.OrderPts(tid, idxFromLeft+1, endIdx);
        }

    }



    public void IDMinMax (){
        minX = pts[0][0];
        maxX = pts[0][0];
        minY = pts[0][1];
        maxY = pts[0][1];

        for (int ii = 1; ii <= numPts-1; ii++) {
            if (pts[ii][0] < minX){
                minX = pts[ii][0];
                minXIdx = ii;
            }

            if (pts[ii][0] > maxX){
                maxX = pts[ii][0];
                maxXIdx = ii;
            }

            if (pts[ii][1] < minY){
                minY = pts[ii][1];
                minYIdx = ii;
            }

            if (pts[ii][1] > maxY){
                maxY = pts[ii][1];
                maxYIdx = ii;
            }
        }

    }


    public void QuarterHull(int startIdx, int endIdx, int quad){

        // First and last pts always stay
        ptsInHull[startIdx] = true;
        ptsInHull[endIdx] = true;

        int endIdxCycle;
        int ii_;

        // Cyclic conditions
        if (endIdx < startIdx)
            endIdxCycle = endIdx + numPts;
        else
            endIdxCycle = endIdx;

        // Groing from start to end, last point to be included in hull
        double ptX = pts[startIdx][0];
        double ptY = pts[startIdx][1];

        //System.out.println("startIdx: " + startIdx + " endIdx " + endIdx);

        if (quad == 1 | quad == 4) {
            // Check if intermediate pts are to the right of line
            for (int ii = startIdx + 1; ii <= endIdxCycle - 1; ii++) {
                ii_ = ii;
                if (ii_ >= numPts)
                    ii_ -= numPts;

                if (this.ptIsRightOfLine(ptX, pts[endIdx][0], pts[ii_][0], ptY, pts[endIdx][1], pts[ii_][1])) {
                    ptX = pts[ii_][0];
                    ptY = pts[ii_][1];
                    ptsInHull[ii_] = true;
                } else {
                    ptsInHull[ii_] = false;
                }
                //System.out.println("ii-1-4: " + ii_ + " is " + ptsInHull[ii_]);
            }
        } else { // 2nd and 3th quadrant cases
            // Check if intermediate pts are to the left of line
            for (int ii = startIdx + 1; ii <= endIdxCycle - 1; ii++) {
                ii_ = ii;
                if (ii_ >= numPts)
                    ii_ -= numPts;

                if (this.ptIsRightOfLine(ptX, pts[endIdx][0], pts[ii_][0], ptY, pts[endIdx][1], pts[ii_][1])) {
                    ptsInHull[ii_] = false;
                } else {
                    ptX = pts[ii_][0];
                    ptY = pts[ii_][1];
                    ptsInHull[ii_] = true;
                }
            }
        }
    }



    public boolean ptIsRightOfLine(double x1, double x2, double ptX, double y1, double y2, double ptY){
        return (ptX - x1) * (y2 - y1) - (ptY - y1) * (x2 - x1) > 0;
    }



    public void MergeHulls(int ptIdx, int quad){
        // In this function we determine if we need to remove some of the pts from the convex hull

        // Set 2 neighbors of pt to check if this pt should be removed from final hull
        // Find last element with smaller index that is true
        int idx1 = -1;
        for (int ii = ptIdx-1; ii>=0; ii--){
            if (ptsInHull[ii]) {
                idx1 = ii;
                break;
            }
        }
        if (idx1 == -1){
            // pt not found, continue at end of array
            for (int ii = numPts-1; ii>=ptIdx+1; ii--){
                if (ptsInHull[ii]) {
                    idx1 = ii;
                    break;
                }
            }
        }

        // Find first element with larger idx
        int idx2 = -1;
        for (int ii = ptIdx+1; ii<=numPts-1; ii++){
            if (ptsInHull[ii]) {
                idx2 = ii;
                break;
            }
        }
        if (idx2 == -1){
            // pt not found, continue at beggining of array
            for (int ii = 0; ii<=ptIdx-1; ii++){
                if (ptsInHull[ii]) {
                    idx2 = ii;
                    break;
                }
            }
        }

        if (quad == 1 | quad == 4) {
            if (!this.ptIsRightOfLine(pts[idx1][0], pts[idx2][0], pts[ptIdx][0], pts[idx1][1], pts[idx2][1], pts[ptIdx][1])){
                // Point should not be in final hull, remove and test neighbors
                ptsInHull[ptIdx] = false;
                this.MergeHulls(idx1, quad);
                this.MergeHulls(idx2, quad);

                //System.out.println("Removing: " + pts[ptIdx][0] + ", " + pts[ptIdx][1] + "; because " + pts[idx1][0] + ", " + pts[idx1][1] + "; and " + pts[idx2][0] + ", " + pts[idx2][1]);
            }

        } else { // In 2 or 3 quad, so pt is out if right of line
            if (this.ptIsRightOfLine(pts[idx1][0], pts[idx2][0], pts[ptIdx][0], pts[idx1][1], pts[idx2][1], pts[ptIdx][1])){
                // Point should not be in final hull, remove and test neighbors
                ptsInHull[ptIdx] = false;
                this.MergeHulls(idx1, quad);
                this.MergeHulls(idx2, quad);
            }

        }


    }


    public int GetMidPtIdx(int startIdx, int endIdx){
        if (endIdx > startIdx) {
            return (int) floor(((double)startIdx + (double)endIdx)/2);
        } else {
            // Must take into account cyclic conditions
            endIdx += numPts;
            int result = (int) floor(((double)startIdx + (double)endIdx)/2);

            if (result >= numPts)
                result -= numPts;

            return result;
        }
    }
}
