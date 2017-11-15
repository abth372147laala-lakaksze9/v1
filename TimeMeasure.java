/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vpspinger.net;

class TimeMeasure {

    private long start;
    private int diff;

    public TimeMeasure() {
        start();
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public int end() {
        diff = (int) (System.currentTimeMillis() - start);
        return diff;
    }

    public int getDiff() {
        return diff;
    }
}
