package ru.ts.gisutils.geometry;

/**
 * Created by IntelliJ IDEA.
 * User: YUGL
 * Date: 10.10.2008
 * Time: 15:30:28
 * To convert xy-ccordinates during import/export, for instance.
 */
//++ -------- IAdoptXY -------------
public interface IAdoptXY {
  // build new IGetXY for given IGetXY
  IXY adopt(IGetXY xy);
  // put result into IXY
  void adopt(IGetXY from, IXY to);

  //++ -------- Copy implementaion -------------
  static public class Copy implements IAdoptXY {
    // static instance to avoid superfluous 'new' operations
    static protected IAdoptXY _instance = null;
    static public synchronized IAdoptXY getInstance() {
      if (_instance == null) _instance = new Copy();
      return _instance;
    }
    // build new IGetXY for given IGetXY
    public IXY adopt(IGetXY from) {
      return doIt(from);
    }
    // put result into IXY
    public void adopt(IGetXY from, IXY to) {
      doIt(from, to);
    }
    // static implementation
    static public IXY doIt(IGetXY from) {
      IXY to = new XY();
      doIt(from, to);
      return to;
    }
    static public void doIt(IGetXY from, IXY to) {
      from.copyTo(to);
    }
  }
  //-- -------- Copy implementaion -------------

  //++ -------- Exchange XY implementaion -------------
  static public class XYExchange implements IAdoptXY {
    // static instance to avoid superfluous 'new' operations
    static protected IAdoptXY _instance = null;
    static public synchronized IAdoptXY getInstance() {
      if (_instance == null) _instance = new XYExchange();
      return _instance;
    }
    // build new IGetXY for given IGetXY
    public IXY adopt(IGetXY from) {
      return doIt(from);
    }
    // put result into IXY
    public void adopt(IGetXY from, IXY to) {
      doIt(from, to);
    }
    // static implementation
    static public IXY doIt(IGetXY from) {
      IXY to = new XY();
      doIt(from, to);
      return to;
    }
    static public void doIt(IGetXY from, IXY to) {
      to.setXY(from.getY(), from.getX());
    }
  }
  //-- -------- Exchange XY implementaion -------------

  //++ -------- Degrees to radians -------------
  static public class ToRadians implements IAdoptXY {
    // static instance to avoid superfluous 'new' operations
    static protected IAdoptXY _instance = null;
    static public synchronized IAdoptXY getInstance() {
      if (_instance == null) _instance = new ToRadians();
      return _instance;
    }
    // build new IGetXY for given IGetXY
    public IXY adopt(IGetXY from) {
      return doIt(from);
    }
    // put result into IXY
    public void adopt(IGetXY from, IXY to) {
      doIt(from, to);
    }
    // static implementation
    static public IXY doIt(IGetXY from) {
      IXY to = new XY();
      doIt(from, to);
      return to;
    }
    static public void doIt(IGetXY from, IXY to) {
      to.setX( from.getX()* Math.PI / 180 );
      to.setY( from.getY()* Math.PI / 180 );
    }
  }
  //-- -------- Degrees to radians -------------

  //++ -------- Exchange XY and Degrees to radians -------------
  static public class XYExchangeToRadians implements IAdoptXY {
    // static instance to avoid superfluous 'new' operations
    static protected IAdoptXY _instance = null;
    static public synchronized IAdoptXY getInstance() {
      if (_instance == null) _instance = new XYExchangeToRadians();
      return _instance;
    }
    // build new IGetXY for given IGetXY
    public IXY adopt(IGetXY from) {
      return doIt(from);
    }
    // put result into IXY
    public void adopt(IGetXY from, IXY to) {
      doIt(from, to);
    }
    // static implementation
    static public IXY doIt(IGetXY from) {
      IXY to = new XY();
      doIt(from, to);
      return to;
    }
    static public void doIt(IGetXY from, IXY to) {
      XYExchange.doIt(from, to);
      ToRadians.doIt(to, to);
    }
  }
  //-- -------- Exchange XY and Degrees to radians -------------

}
//-- -------- IAdoptXY -------------
