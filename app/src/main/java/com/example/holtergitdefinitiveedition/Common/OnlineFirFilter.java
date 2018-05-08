package com.example.holtergitdefinitiveedition.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * FIR带通滤波器
 */

public class OnlineFirFilter {
    double[] _coefficients;            //_coefficients：滤波因子存放的数组
    List<Double> _buffer;            //_buffer：存放输入变量的链表
    int _size;                        //_size：滤波因子的长度


    /**
     * 有参构造函数，主要用来在实现该类时传递一些参数或者进行一些其他初始化操作，这里传入了滤波因子
     * <p>
     * coefficients，为数组形式
     *
     * @param coefficients
     */


    public OnlineFirFilter(double[] coefficients) {
        _size = coefficients.length;
        _buffer = new ArrayList<>();
        _coefficients = new double[_size << 1];
        for (int i = 0; i < _size; i++) {
            _coefficients[i] = _coefficients[_size + i] = coefficients[i];
        }
    }

    /**
     * 处理一个采样点
     *
     * @param sample 需要滤波的采样点
     * @return
     */
    public double ProcessSample(double sample) {
        if (_buffer.size() < _size) {
            _buffer.add(sample);
            return 0;
        } else {
            _buffer.remove(0);
            _buffer.add(sample);
        }

        double acc = 0;
        for (int i = 0; i < _size; i++) {
            acc += _buffer.get(i) * _coefficients[i];
        }

        return acc;

    }

    /**
     * 处理多个采样点
     *
     * @param samples 需要滤波的采样点数组
     * @return
     */
    public double[] ProcessSamples(double[] samples) {
        if (null == samples) {
            return null;
        }

        double[] ret = new double[samples.length];
        for (int i = 0; i < samples.length; i++) {
            ret[i] = ProcessSample(samples[i]);
        }

        return ret;
    }

    /**
     * 重置
     */
    public void Reset() {
        _buffer.clear();
    }

    /**
     * 获取带通滤波器参数的静态方法
     * sampleingRate：采样频率；
     * cutoffLow：带通滤波低频截至频率值；
     * cutoffHigh：带通滤波高频截至频率值；
     * halforder：生成的滤波器阶数
     */
    public static double[] BandPass(double samplingRate, double cutoffLow, double cutoffHigh, int halforder) {
//        halforder = 0;
        double nu1 = 2d * cutoffLow / samplingRate; // normalized low frequency
        double nu2 = 2d * cutoffHigh / samplingRate; // normalized high frequency

        // Default filter order
        if (halforder == 0) {
            final double TRANSWINDRATIO = 0.25;
            double maxDf = (cutoffLow < samplingRate / 2 - cutoffHigh) ? cutoffLow : samplingRate / 2 - cutoffHigh;
            double df = (cutoffLow * TRANSWINDRATIO > 2) ? cutoffLow * TRANSWINDRATIO : 2;
            df = (df < maxDf) ? df : maxDf;
            halforder = (int) Math.ceil(3.3 / (df / samplingRate) / 2);
        }

        int order = 2 * halforder + 1;
        double[] c = new double[order];
        c[halforder] = nu2 - nu1;

        for (int i = 0, n = halforder; i < halforder; i++, n--) {
            double npi = n * Math.PI;
            c[i] = (Math.sin(npi * nu2) - Math.sin(npi * nu1)) / npi;
            c[n + halforder] = c[i];
        }

        return c;
    }

}
