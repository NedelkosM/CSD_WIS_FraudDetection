/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterprojectwithoutmaven;

import java.util.Objects;

/**
 *
 * @author Chris
 */
class TweetDist {

    private double dist;
    private final String tID1;
    private final String tID2;

    TweetDist(String id, String id0, double dist) {
        tID1 = id;
        tID2 = id0;
        this.dist = dist;
    }

    void normalize(int max) {
        dist = (dist / max) * 100;
    }

    double getDist() {
        return dist;
    }

    @Override
    public boolean equals(Object obj) {
        TweetDist td = (TweetDist) obj;
        if ((this.tID1 == null ? td.tID1 == null : this.tID1.equals(td.tID1)) && (this.tID2 == null ? td.tID2 == null : this.tID2.equals(td.tID2))) {
            return true;
        }
        if ((this.tID1 == null ? td.tID2 == null : this.tID1.equals(td.tID2)) && this.tID2.equals(td.tID1)) {
            return true;
        }
        return false;
    }

}
