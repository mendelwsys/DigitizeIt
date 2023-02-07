package ru.ts.gisutils.algs.transform;

/**
 * Created by IntelliJ IDEA.
 * User: YUGL
 * Date: 24.11.2008
 * Time: 12:37:40
 * Complex functionality where a number consists of a real part and an imaginary one.
 */
public class Complex {
  //++ -------- Data --------------
  // real part
  public double x;
  // imaginary part
  public double y;
  //-- -------- Data --------------

  //++ -------- Constructor --------------
  public Complex() { this(0, 0); }
  public Complex(double x, double y) { set(x, y); }
  public Complex(Complex z2) { set(z2); }
  //-- -------- Constructor --------------

  //++ -------- Polar coordinates --------------
  // radius
  public double getR() { return getR(this); }
  // radius square
  public double getR2() { return getR2(this); }
  // imaginary part
  public double getA() { return getA(this); }
  //-- -------- Polar coordinates --------------

  //++ -------- Static Polar coordinates --------------
  // radius
  static public double getR(Complex z) { return Math.sqrt(getR2(z)); }
  // radius square
  static public double getR2(Complex z) { return z.x*z.x + z.y*z.y; }
  // angle (phase)
  static public double getA(Complex z) {
    if (z.y == 0) return 0;
    if (z.x == 0) {
      if (z.y > 0) return 0.5 * Math.PI;
      return -0.5 * Math.PI;
    }
    return Math.atan2(z.y, z.x);
  }
  static public double getTgA(Complex z) { return 0; }
  //-- -------- Static Polar coordinates --------------

  //++ -------- Operations --------------
  // add
  public void add(Complex z2) {
    add(this, z2, this);
  }
  // sub
  public void sub(Complex z2) {
    sub(this, z2, this);
  }
  // mul
  public void mul(double d) {
    mul(this, d, this);
  }
  public void mul(Complex z2) {
    mul(this, z2, this);
  }

  // set
  public void set(double x, double y) {
    set(x , y, this);
  }
  public void set(Complex z2) {
    set(z2, this);
  }
  // conjugation
  public void con() {
    con(this, this);
  }
  // inversion
  public void inv() {
    inv(this, this);
  }
  // div
  public void div(double d) {
    div(this, d, this);
  }
  public void div(Complex z2) {
    div(this, z2, this);
  }

  // power
  public void pow(double d) {
    pow(this, d, this);
  }
  public void powi(int n) {
    pow(this, n, this);
  }
  //-- -------- Operations --------------

  //++ -------- Static Ops --------------
  // add
  static public void add(Complex z1, Complex z2, Complex z) {
    z.x = z1.x + z2.x;
    z.y = z1.y + z2.y;
  }
  static public Complex add(Complex z1, Complex z2) {
    Complex z = new Complex();
    add(z1, z2, z);
    return z; 
  }
  // sub
  static public void sub(Complex z1, Complex z2, Complex z) {
    z.x = z1.x - z2.x;
    z.y = z1.y - z2.y;
  }
  static public Complex sub(Complex z1, Complex z2) {
    Complex z = new Complex();
    sub(z1, z2, z);
    return z;
  }
  // mul
  static public void mul(Complex z1, double d, Complex z) {
    z.x = z1.x * d;
    z.y = z1.y * d;
  }
  static public Complex mul(Complex z1, double d) {
    Complex z = new Complex();
    mul(z1, d, z);
    return z;
  }
  static public void mul(Complex z1, Complex z2, Complex z) {
    double x = z1.x*z2.x - z1.y*z2.y;
    double y = z1.x*z2.y + z1.y*z2.x;
    z.x = x;
    z.y = y;
  }
  static public Complex mul(Complex z1, Complex z2) {
    Complex z = new Complex();
    mul(z1, z2, z);
    return z;
  }

  // set
  static public void set(double x, double y, Complex z) {
    z.x = x;
    z.y = y;
  }
  static public void set(Complex z1, Complex z) {
    z.x = z1.x;
    z.y = z1.y;
  }
  // conjugation
  static public void con(Complex z1, Complex z) {
    z.x = z1.x;
    z.y = -z1.y;
  }
  static public Complex con(Complex z1) {
    Complex z = new Complex();
    con(z1, z);
    return z;
  }
  // inversion
  static public void inv(Complex z1, Complex z) {
    con(z1, z);
    double r2 = z.getR2();
    div(z, r2, z);
  }
  static public Complex inv(Complex z1) {
    Complex z = new Complex();
    inv(z1, z);
    return z;
  }
  // div
  static public void div(Complex z1, double d, Complex z) {
    z.x = z1.x / d;
    z.y = z1.y / d;
  }
  static public Complex div(Complex z1, double d) {
    Complex z = new Complex();
    div(z1, d, z);
    return z;
  }
  static public void div(Complex z1, Complex z2, Complex z) {
    inv(z2, z);
    mul(z1, z, z);
  }
  static public Complex div(Complex z1, Complex z2) {
    Complex z = new Complex();
    div(z1, z2, z);
    return z;
  }

  // power
  static public void pow(Complex z1, double d, Complex z) {
    double r = z1.getR2();
    r = Math.pow(r, d/2);
    double a = z1.getA();
    z.x = r * Math.cos(a * d);
    z.y = r * Math.sin(a * d);
  }
  static public Complex pow(Complex z1, double d) {
    Complex z = new Complex();
    pow(z1, d, z);
    return z;
  }
  static public void powi(Complex z1, int n, Complex z) {
    if (n == 0) {
      z.set(1, 0);
    }
    else {
      Complex z0 = new Complex(z1);
      if (n < 0) {
        z0.inv(); n = -n;
      }
      z.set(z0);
      for (int i=1; i<n; i++) {
        z.mul(z0);
      }
    }
  }
  static public Complex powi(Complex z1, int n) {
    Complex z = new Complex();
    powi(z1, n, z);
    return z;
  }
  //-- -------- Static Ops --------------



}
