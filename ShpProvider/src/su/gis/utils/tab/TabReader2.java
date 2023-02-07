package su.gis.utils.tab;


import java.io.*;
import java.text.MessageFormat;
import java.awt.*;
import java.awt.geom.Point2D;

import ru.ts.common.arrays.DirectIntArray;
import ru.ts.common.arrays.DirectPointArray;
import ru.ts.common.misc.Text;
import shp.core.ShpPoint;

/**
 * Created by IntelliJ IDEA.
 * User: Syg
 * Date: 02.07.2012
 * Time: 12:35:33
 * MapInfo TAB reader. Example text of TAB file is as follow:
 * <pre><b>
 * !table
 * !version 300
 * !charset WindowsCyrillic
 * <p/>
 * Definition Table
 *   File "proba.bmp"
 *   Type "RASTER"
 *   (99.0616302754092,56.0421971276744) (0,0) Label "Point 1",
 *   (99.1259358300973,56.0421971276744) (800,0) Label "Point 2",
 *   (99.1259358300973,55.9995015002965) (800,948) Label "Point 3",
 *   (99.0616302754092,55.9995015002965) (0,948) Label "Point 4"
 * CoordSys Earth Projection 1,104
 * Units "degree"
 * RasterStyle 4 1
 * RasterStyle 7 0
 * RasterStyle 8 255
 * </b> </pre>
 */
public class TabReader2
{
  private final String m_path;
  private final String m_cp;
  private String m_imgPath;
  private String m_imgName;

  private static final int STATE_AT_START = 0;
  private static final int STATE_DEF_TABLE = 1;
  private static final int STATE_AFTER_DEF = 2;
  private DirectIntArray m_refArrayPix;
  private DirectPointArray m_refArrayDeg;

  /**
   * Main constructor
   *
   * @param path String with a TAB file path to open and read
   * @param cp   String with a code page ("Windows-1251" for example)
   */
  public TabReader2(String path, String cp) throws IOException
  {
    m_path = path;
    m_cp = cp;
    parseAll();
    // todo : add image path generation
    // fp.setFileName()
  }

  /**
   * Default constructor with Windows-1251 code page
   *
   * @param path String with a TAB file path to open and read
   */
  public TabReader2(String path) throws IOException
  {
    this(path, "cp1251");
  }

  public String getCodePage()
  {
    return m_cp;
  }

  public String getTabPath()
  {
    return m_path;
  }

  public String getImagePath()
  {
    return m_imgPath;
  }


  private void parseAll() throws IOException, UnsupportedEncodingException, IllegalArgumentException
  {
    // read TAB line any line and get all the info needed
    BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(m_path), m_cp));

    String line;
    int state = STATE_AT_START;
    for ( int cnt = 1; (line = rd.readLine()) != null; cnt++ )
    {
      line = line.trim();
      if ( Text.isEmpty(line))
        continue; // comment line detected
      if ( line.startsWith("!") )
      {
        // some of the first lines
        if ( line.startsWith("charset", 1) )
        {
          // todo check charset value
        }
        else if ( line.startsWith("version", 1) )
        {
          // todo check version
        }
        else if ( !line.equals("!table") )
        {
          // todo some unknown tags
        }
        continue;
      }
      if ( Text.orderOfWords(line, new String[]{ "Definition", "Table" }) )
      {
        state = STATE_DEF_TABLE;
        continue;
      }
      switch ( state )
      {
        case STATE_AT_START:
        default:
          break;
        case STATE_DEF_TABLE:
          if (Text.startsWithNoCase(line, "File") )
          {
            // get image file name
            m_imgName = Text.findWord(line, 4, "\"", "\"");
            break;
          }
          if ( line.startsWith("(") ) // parse reference point data
          {
            parseNextPoint(line);
            break;
          }
          if ( Text.startsWithNoCase(line, "Type") )
          {
            // TAB type parsing
            if ( !Text.orderOfWords(line, new String[]{"Type", "\"RASTER\""}) )
            {
              throw new IllegalArgumentException("Expected Type not \"RASTER\"");
            }
            break;
          }
          state = STATE_AFTER_DEF;
          // we are out of Definition Table
        case STATE_AFTER_DEF:
          // todo parse any lines after Definition Table
          break;
      }
    }
  }

  /**
   * Add one more reference Point to the array
   *
   * @param line String with text of point defintion to parse
   */
  private void parseNextPoint(String line)
  {
    if ( m_refArrayPix == null )
    {
      m_refArrayPix = new DirectIntArray(8);
      m_refArrayDeg = new DirectPointArray(4);
    }
    String[] items = Text.splitItems(line, "()", false); // 3 pairs should be returned
    if ( items.length != 3 )
    {
      throw new IllegalArgumentException(
              MessageFormat.format("Expected ref. point definition line don't contain 3 parts: \"{0}\"", line));
    }

    double[] degPoint = Text.splitDoubles(items[ 0 ], ',', true); // 2 numbers expected
    if ( degPoint.length != 2 )
    {
      throw new IllegalArgumentException(
              MessageFormat.format("Expected 2 degree coordinates per ref. point not found: \"{0}\"", line));
    }
    m_refArrayDeg.append(degPoint[ 0 ], degPoint[ 1 ]);

    int[] imgPoint = Text.splitInts(items[ 1 ], ',', true); // 2 numbers expected
    if ( imgPoint.length != 2 )
    {
      throw new IllegalArgumentException(MessageFormat.format("Expected 2 pixel positions per ref. point not found: \"{0}\"", line));
    }
    m_refArrayPix.add(imgPoint[ 0 ]);
    m_refArrayPix.add(imgPoint[ 1 ]);
  }

  /**
   * Number of reference points
   *
   * @return int with number of reference points found in the Definition Ta��� of the TAB file
   */
  public int pntCount()
  {
    if ( m_refArrayDeg == null )
    {
      return 0;
    }
    return m_refArrayDeg.size();
  }

  private boolean checkPointIndex(int index)
  {
    return pntCount() < index;
  }

  public ShpPoint getProjectedPoint(int index)
  {
    if ( checkPointIndex(index) )
    {
      return new ShpPoint(m_refArrayDeg.getX(index), m_refArrayDeg.getY(index));
    }
    return null;
  }

  public Point getImagePoint( int index)
  {
    if ( checkPointIndex(index) )
    {
      index = index * 2;
      return new Point(m_refArrayPix.get(index), m_refArrayPix.get(index+1));
    }
    return null;
  }

  public Point getMinPixXY()
  {
    int minx = Integer.MAX_VALUE;
    int miny = Integer.MAX_VALUE;
    for(int i = 0, pos = 0; i < m_refArrayPix.size() /2; i++, pos +=2 )
    {
      if ( minx > m_refArrayPix.get(pos) )
      {
        minx = m_refArrayPix.get(pos);
      }
      if ( miny > m_refArrayPix.get(pos+1) )
      {
        miny = m_refArrayPix.get(pos+1);
      }
    }
    return new Point( minx, miny );
  }

  public Point getMaxPixXY()
  {
    int maxx = -Integer.MAX_VALUE;
    int maxy = -Integer.MAX_VALUE;
    for(int i = 0, pos = 0; i < m_refArrayPix.size() /2; i++, pos +=2 )
    {
      if ( maxx < m_refArrayPix.get(pos) )
      {
        maxx = m_refArrayPix.get(pos);
      }
      if ( maxy < m_refArrayPix.get(pos+1) )
      {
        maxy = m_refArrayPix.get(pos+1);
      }
    }
    return new Point( maxx, maxy );
  }

  public Point2D.Double getMinDegXY()
  {
    double minx = Double.MAX_VALUE;
    double miny = Double.MAX_VALUE;
    for(int i = 0; i < m_refArrayDeg.size(); i++)
    {
      if ( minx > m_refArrayDeg.getX(i) )
      {
        minx = m_refArrayDeg.getX(i);
      }
      if ( miny > m_refArrayDeg.getY(i) )
      {
        miny = m_refArrayDeg.getY(i);
      }
    }
    return new Point2D.Double( minx, miny );
  }

  public Point2D.Double getMaxDegXY()
  {
    double maxx = -Double.MAX_VALUE;
    double maxy = -Double.MAX_VALUE;
    for(int i = 0; i < m_refArrayDeg.size(); i++)
    {
      if ( maxx < m_refArrayDeg.getX(i) )
      {
        maxx = m_refArrayDeg.getX(i);
      }
      if ( maxy < m_refArrayDeg.getY(i) )
      {
        maxy = m_refArrayDeg.getY(i);
      }
    }
    return new Point2D.Double( maxx, maxy );
  }

  /**
   * Image name designated in the TAB Definition Table
   * @return String with a raster fiel name only, no other path elements
   */
  public String getImgName()
  {
    return m_imgName;
  }

    public static void main(String[] args) throws Exception
    {
        TabReader2 tb = new TabReader2("MAPDIR\\proba2 short.TAB");
    }


}
