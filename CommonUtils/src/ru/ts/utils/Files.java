/**
 *
 */
package ru.ts.utils;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * some method of file handling
 *
 * @author sygsky
 */
public class Files
{

	/**
	 * gets current directory path without trailing file separator
	 *
	 * @return string with current directory used
	 */
	public static String getCurrentDir()
	{
		try
		{
			return new File( "." ).getCanonicalPath();
		}
		catch ( IOException ioex )
		{
			return ".";
		}
	}

	/**
	 * The system-dependent default name-separator character. This field is initialised to contain the first character of
	 * the value of the system property file.separator. On UNIX systems the value of this field is '/'; on Microsoft
	 * Windows systems it is '\\'.
	 */
	public static final char FileSeparator = File.separatorChar;

	/**
	 * The system-dependent default name-separator character. This field is initialised to contain the first character of
	 * the value of the system property file.separator. On UNIX systems the value of this field is '/'; on Microsoft
	 * Windows systems it is '\\'.
	 */
	public static final char FS = File.separatorChar;

	/**
	 * appends system dependent file separator char
	 *
	 * @param FilePath - string to check for file separator existence
	 *
	 * @return new string with appended file separator or the same string if it already was present
	 */
	public static String appendFileSeparator( String FilePath )
	{
		if ( FilePath.charAt( FilePath.length() - 1 ) != FileSeparator )
			return FilePath + FileSeparator;
		return FilePath;
	}

	/**
	 * the same as appendFileSeparator but with shorter name :o)
	 *
	 * @param path path to append file separator
	 */
	public static String resetDir( String path )
	{
		return appendFileSeparator( path );
	}

	/**
	 * removes trailing system dependent file separator char
	 *
	 * @param FilePath - string to check for file separator existence
	 *
	 * @return new string with removed trailing file separator or the same string if it is not present
	 */
	public static String removeFileSeparator( String FilePath )
	{
		if ( FilePath.charAt( FilePath.length() - 1 ) != FileSeparator )
			return FilePath;
		return FilePath.substring( 0, FilePath.length() - 1 );
	}

	/**
	 * checks if file with a designated path&name exists
	 *
	 * @param FilePath String with file path
	 *
	 * @return <code>true</code> if exists else <code>false</code>
	 */
	public static boolean fileExists( String FilePath )
	{
		try
		{
			return new File( FilePath ).exists();
		}
		catch ( Exception e )
		{
			return false;
		}
	}

	/**
	 * checks if a file with a designated path&name is a directory
	 *
	 * @param FilePath String with a probable directory path
	 *
	 * @return <code>true</code> if it is directory else <code>false</code> if not directory or file name not found at all
	 */
	public static boolean isDirectory( String FilePath )
	{
		try
		{
			return new File( FilePath ).isDirectory();
		}
		catch ( Exception e )
		{
			return false;
		}
	}

	/**
	 * gets file extension, that is part of file name after last point. The point itself is included into an extension
	 * string returned
	 *
	 * @param filePath file path to get extension
	 *
	 * @return file extension, includes heading point
	 */
	public static String getExtension( String filePath )
	{
		final int pos = filePath.lastIndexOf( (int) '.' );
		if ( pos < 0 )
			return "";
		return filePath.substring( pos );
	}

	/**
	 * changes file extension. It is supposed that filePath is the file name string, not directory one. That is if the
	 * filePath is not terminated by directory separator, it is assumed to be a file path.
	 *
	 * @param filePath original file path
	 * @param newExt   new extension, may be as with leading point '.' as without it
	 *
	 * @return new path for file with changed extension
	 */
	public static String changeFileExt( String filePath, String newExt )
	{
		// File file = new File(filePath);
		if ( !newExt.startsWith( "." ) )
			newExt = '.' + newExt;

		final int ppos = filePath.lastIndexOf( '.' );
		if ( ppos >= 0 ) // then point is found
		{
			// check possibility that it is a point in the middle of the
			// directory name
			final int dpos = filePath.lastIndexOf( FileSeparator );
			if ( ( dpos < 0 ) || ( dpos < ppos ) )
				return filePath.substring( 0, ppos ) + newExt;
		}
		// else this path has no extension, and we simply append it
		return filePath + newExt;
	}

	/**
	 * gets directory with trailing file separator
	 *
	 * @param filePath file path with optional disk and / or directory
	 *
	 * @return file parent disk and / or directory without trailing separator or <code>null</code> if no directory found in
	 *         the file path
	 */
	public static String getDirectory( String filePath )
	{
		return ( new File( filePath ) ).getParent() + File.separator;
	}

	/**
	 * gets file true name, that is part of file name without last point.
	 *
	 * @param FilePath file path to get name
	 *
	 * @return file name without extension, not including trailing point
	 */
	public static String getNameNoExt( String FilePath )
	{
		final String fname = ( new File( FilePath ) ).getName();
		final int pos = fname.lastIndexOf( (int) '.' );
		if ( pos < 0 )
			return "";
		return fname.substring( 0, pos );
	}

	/**
	 * gets file name, that is part of file name after last path separator.
	 *
	 * @param FilePath file path to get name
	 *
	 * @return file name with extension
	 */
	public static String getName( String FilePath )
	{
		return ( new File( FilePath ) ).getName();
	}

	/**
	 * tries to delete file with a designated path
	 *
	 * @param path to the file to delete
	 *
	 * @return <code>true</code> is file was existing and is deleted, else <code>false</code>
	 *
	 * changed by Vlad Mendel
	 */

	public static boolean deleteFile( String path )
	{
		File fl4del = new File(path);
		if (fl4del.isDirectory())
			for (String flname : fl4del.list())
				deleteFile(path + "/" + flname);
		return ( new File( path ) ).delete();
	}

	/**
	 * gets user running directory where his class with main() is situated. <strong>Note:</strong> Directory path is
	 * returned <strong>without</strong> trailing file separator
	 *
	 * @return directory path for running class with a main(...) method
	 */
	public static String getRunDirectory()
	{
		return System.getProperties().getProperty( "user.dir" );
	}

	/**
	 * creates BufferedReader by a file path, charset name and buffer size. Only path is needed, other parameters may be
	 * omitted (set to null or 0)
	 *
	 * @param fileName file path to open, can't be null or empty
	 * @param charset  he name of a supported {@link java.nio.charset.Charset </code>charset<code>} or <code>null</code> to
	 *                 use default one
	 * @param bufsize  size of internal buffer to read text in chunks, may be <= 0, so default size 1024 will be used for
	 *                 such case
	 *
	 * @return newly created BufferedReader instance
	 *
	 * @throws FileNotFoundException        if no the file exists
	 * @throws UnsupportedEncodingException illegal charset is used
	 */
	public static BufferedReader getBufferedReader( String fileName,
	                                                String charset, int bufsize ) throws FileNotFoundException,
	                                                                                     UnsupportedEncodingException
	{
		if ( bufsize <= 0 )
		{
			if ( charset == null || charset.length() == 0 )
				return new BufferedReader( new InputStreamReader(
						new FileInputStream( fileName ) ) );
			return new BufferedReader( new InputStreamReader(
					new FileInputStream( fileName ), charset ) );
		}
		return new BufferedReader( new InputStreamReader( new FileInputStream(
				fileName ), charset ), bufsize );
	}

	/**
	 * creates BufferedReader by a file path
	 *
	 * @param fileName file path to open, can't be null or empty
	 *
	 * @return newly created BufferedReader instance
	 *
	 * @throws FileNotFoundException        if no the file exists
	 * @throws UnsupportedEncodingException never could be thrown but needed to be declared here
	 */
	public static BufferedReader getBufferedReader( String fileName )
			throws FileNotFoundException, UnsupportedEncodingException
	{
		return getBufferedReader( fileName, null, 0 );
	}

	/**
	 * gets file length in bytes
	 *
	 * @param filepath file path to check length
	 *
	 * @return long value for a file length in bytes
	 */
	public static long getFileLength( String filepath )
	{
		return ( new File( filepath ) ).length();

	}

	/**
	 * Detects if designated directories points to the same path
	 *
	 * @param dir1 first directory
	 * @param dir2 second directory
	 *
	 * @return <code>true</code> if directories are equal else <code>false</code>
	 */
	public static boolean dirsAreSame( String dir1, String dir2 )
	{
		File file = new File( dir1 );
		try
		{
			dir1 = file.getCanonicalPath();
			file = new File( dir2 );
			dir2 = file.getCanonicalPath();
			return dir1.equalsIgnoreCase( dir2 );
		}
		catch ( IOException ex )
		{
			return false;
		}
	}

	/**
	 * detects if directory is writable for the user
	 *
	 * @param dir directory path ( with or without trailing file separator )
	 *
	 * @return <code>true</code> if directory is writable for this user else <code>false</code>
	 */
	public static boolean dirIsWritable( String dir )
	{
		File file = new File( dir );
		if ( ( !file.exists() ) || ( !file.isDirectory() ) )
			return false;
		file = makeTempFile( dir );
		if ( file == null )
			return false;
		file.delete();
		return true;
	}

	/**
	 * Makes temp file in the designated directory
	 *
	 * @param directory directory path to create the file
	 *
	 * @return File instance for the created file or <code>null</code> on any error
	 */
	public static File makeTempFile( String directory )
	{
		File tmpf = null;
		try
		{
			tmpf = File.createTempFile( "tmp_", null, new File( directory ) );
			return tmpf;
		}
		catch ( IOException ioe )
		{
			ioe.printStackTrace();
			return null;
		}
	}

	private static final String _seps = ":\\/";

	/**
	 * Detects if any separators are in the path, that is any of characters ":/\"
	 *
	 * @param path path to check for separators presence
	 *
	 * @return <code>true</code> if there are any of designated separators in the path else <code>false</code>
	 */
	public static boolean hasSepsInPath( String path )
	{
		return Text.indexOf( path, _seps ) >= 0;
	}

	/**
	 * Copies one file content to another one, overwriting destination if it exist. Copying is maximally fast as use native
	 * method for this OS
	 *
	 * @param src source file to copy from
	 * @param dst destination file to copy to. If it is a directory, new file with the same name as src is trying to
	 *            create
	 *
	 * @throws IOException if any error occur
	 */
	public static void copyFile( File src, File dst ) throws IOException
	{
		FileChannel in = ( new FileInputStream( src ) ).getChannel();
		if ( dst.isDirectory() )
			dst = new File( dst, src.getName() );
		FileChannel out = ( new FileOutputStream( dst ) ).getChannel();
		try
		{
			in.transferTo( 0, src.length(), out );
		}
		finally
		{
			if ( in != null )
				in.close();
			if ( out != null )
				out.close();
		}
	}

	/**
	 * Copies one file content to another one, overwriting destination if it exist. Copying is maximally fast as use native
	 * method for this OS
	 *
	 * @param src source file path to copy from
	 * @param dst destination file path to copy to
	 *
	 * @throws IOException if any error occur
	 */
	public static void copyFile( String src, String dst ) throws IOException
	{
		copyFile( new File( src ), new File( dst ) );
	}


	/**
	 * Renames existing file with source path to destination file path
	 *
	 * @param src source path
	 * @param dst destination path
	 *
	 * @return {@code true} if renamed successfully else return {@code false}
	 *
	 * @throws SecurityException    If a security manager exists and its <code>{@link java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
	 *                              method denies write access to either the old or new pathnames
	 * @throws NullPointerException If parameter <code>dest</code> is <code>null</code>
	 */
	public static boolean renameFiles( String src, String dst )
	{
		final File fsrc = new File( src );
		if ( !(fsrc.exists() && fsrc.isFile()))
			throw new SecurityException( "renameFiles: Src file \""+src+"\" not found" );
		final File fdst = new File( dst );
		if (fdst.exists())
			throw new SecurityException( "renameFiles: Dst file \""+src+"\" exists, remove it before call" );
		return fsrc.renameTo( fdst );
	}
}
